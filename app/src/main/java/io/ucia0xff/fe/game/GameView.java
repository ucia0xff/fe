package io.ucia0xff.fe.game;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import io.ucia0xff.fe.Values;
import io.ucia0xff.fe.actor.*;
import io.ucia0xff.fe.map.*;
import io.ucia0xff.fe.util.Cursor;
import io.ucia0xff.fe.util.RangeHelper;

public class GameView extends SurfaceView
        implements SurfaceHolder.Callback,
        View.OnTouchListener,
        GestureDetector.OnGestureListener,
        GestureDetector.OnDoubleTapListener,
        Runnable {

    public static int[] PARTY_INDEX;//场上所有阵营序号
    public static int PARTY_COUNT;//场上阵营总数
    public static int GAME_TURN = 1;//游戏进行的回合
    public static int TURN_PHASE = 0;//回合进行阶段
    public static int NOW_PARTY;//当前行动阵营
    public static int GAME_CASE = Values.CASE_NORMAL;//游戏进行状态
    public static java.util.Map<String, ArrayList<Actor>> parties = null;//每个阵营角色列表
    public static Actor selectedActor = null;//当前角色
    public static Actor targetActor = null;//目标角色
    public Map map;//地图对象
    public Cursor cursor;//光标
    public Terrain terrain;//地形信息面板
    public RangeHelper rangeHelper;//范围计算工具类
    public ActorInfo actorInfo;//角色信息
    public ActorAction actorAction;//行动选项
    public GameOption gameOption;//游戏选项

    //画笔
    private Paint paint = null;

    //画布
    private Canvas canvas = null;

    // 游戏绘图线程
    private Thread thread = null;

    // 线程循环标志
    private boolean isRunning = false;

    private SurfaceHolder surfaceHolder = null;

    //手势解析器
    private GestureDetector gestureDetector;

    //游戏地图左上角对屏幕左上角的偏移量
    public static int[] xyOffset = {0, 0};

    //单击点在屏幕上的像素坐标
    private int[] xyInScrPx = {0, 0};
    //单击点在地图上的像素坐标
    private int[] xyInMapPx = {0, 0};
    //单击点在地图上的格子坐标
    private int[] xyInMapTile = {0, 0};

    //是否在屏幕某区域
    private Boolean isLeft = true;
    private Boolean isDown = true;

    //光标在地图中的格子坐标
    public static int[] cursorXY = {0, 0};

    //保存上一个格子坐标
    public static int[] lastXY = {0, 0};

    //构造方法
    public GameView() {
        super(Values.CONTEXT);
        paint = new Paint();
        //地图
        map = new Map();
        map.readMap("map1");
        //地形信息
        terrain = new Terrain();
        terrain.setMap(map);
        //光标
        cursor = new Cursor();
        //范围工具
        rangeHelper = new RangeHelper(map);
        //角色信息
        actorInfo = new ActorInfo();
        //行动选项
        actorAction = new ActorAction(rangeHelper);
        //游戏选项
        gameOption = new GameOption();

        setParties();
        TURN_PHASE = 0;
        NOW_PARTY = PARTY_INDEX[TURN_PHASE];

        surfaceHolder = getHolder();
        surfaceHolder.addCallback(this);
        setClickable(true);
        setFocusable(true);
        gestureDetector = new GestureDetector(Values.CONTEXT, this);
        setOnTouchListener(this);
    }

    //绘制游戏画面
    protected void Draw() {
        switch (GAME_CASE) {
            case Values.CASE_NORMAL://通常状态
                map.drawMap(canvas, paint, xyOffset);
                DrawActors(canvas, paint, xyOffset);
                cursor.drawCursor(canvas, paint, xyOffset);
                actorInfo.show(canvas, paint, isLeft);
                terrain.show(canvas, paint, isLeft);
                break;
            case Values.CASE_BEFORE_MOVE://移动前状态，显示移动范围和攻击范围
                map.drawMap(canvas, paint, xyOffset);
                rangeHelper.drawMoveRange(canvas, paint, xyOffset);
                rangeHelper.drawAttackRange(canvas, paint, xyOffset);
                DrawActors(canvas, paint, xyOffset);
                cursor.drawCursor(canvas, paint, xyOffset);
                break;
            case Values.CASE_MOVING://移动中状态，播放移动动画
                map.drawMap(canvas, paint, xyOffset);
                if (selectedActor.move(rangeHelper.getMovePath())) {//移动结束
                    GAME_CASE = Values.CASE_AFTER_MOVE;
                    rangeHelper.setAttackRangeAt(selectedActor.getXyInMapTile());//计算攻击范围
                    actorAction.setSrcActor(selectedActor);//准备行动选项
                    Log.d("GAME_CASE", "AFTER_MOVE");
                }
                DrawActors(canvas, paint, xyOffset);
                break;
            case Values.CASE_AFTER_MOVE://移动后状态，显示行动选项
                map.drawMap(canvas, paint, xyOffset);
                DrawActors(canvas, paint, xyOffset);
                rangeHelper.drawAttackRangeAt(canvas, paint, xyOffset);
                actorAction.show(canvas, paint);
                break;
            case Values.CASE_BEFORE_ACT:
                map.drawMap(canvas, paint, xyOffset);
                DrawActors(canvas, paint, xyOffset);
//                rangeHelper.drawAttackRangeAt(canvas, paint, xyOffset);
                cursor.drawCursor(canvas, paint, xyOffset);
                actorAction.showBattleInfo(canvas, paint, selectedActor, targetActor);
                break;
            case Values.CASE_ACTING:
                map.drawMap(canvas, paint, xyOffset);
                DrawActors(canvas, paint, xyOffset);
                if (selectedActor.doAttack(targetActor)) {
                    if (targetActor!=null && targetActor.isVisible()) {
                        targetActor.doAttack(selectedActor);
                    }
                }
                if ((selectedActor==null || !selectedActor.isVisible() || !selectedActor.isCanAttack()) &&
                        (targetActor==null || !targetActor.isVisible() || !targetActor.isCanAttack())) {//双方攻击结束
                    GAME_CASE = Values.CASE_AFTER_ACT;
                    Log.d("GAME_CASE", "AFTER_ACT");
                }
                break;
            case Values.CASE_AFTER_ACT:
//                actorAction.battle(selectedActor, targetActor);
                if (selectedActor!=null) { //角色自身未死亡
                    if (targetActor!=null && targetActor.isVisible()) {
                        selectedActor.gainExp(20);//未杀死敌人
                    } else {
                        selectedActor.gainExp(50);//杀死了敌人
                        Actors.restore();
                    }
                    selectedActor.standby();
                } else {
                    Actors.restore();
                }
                selectedActor = Actors.getActor(cursor.getXy());
                actorInfo.setActor(selectedActor);
                GAME_CASE = Values.CASE_NORMAL;
                Log.d("GAME_CASE", "NORMAL");
                break;
            case Values.CASE_SHOW_ACTOR_INFO:
                actorInfo.showDetail(canvas, paint);
                break;
            case Values.CASE_SHOW_GAME_OPTIONS:
                map.drawMap(canvas, paint, xyOffset);
                DrawActors(canvas, paint, xyOffset);
                gameOption.show(canvas, paint, isLeft);
                break;
        }
    }

    //显示地图上所有角色
    private void DrawActors(Canvas canvas, Paint paint, int[] xyOffset) {
        Iterator<Actor> iterator = Actors.actors.iterator();
        while (iterator.hasNext()) {
            Actor actor = iterator.next();
            if (actor.isVisible()) {
                actor.drawAnim(canvas, paint, xyOffset);
            }
        }
    }

    //长按
    @Override
    public void onLongPress(MotionEvent e) {
//        if (TURN_PHASE != Values.PARTY_PLAYER)//非我方阶段不响应屏幕操作
//            return;

        //长按点在屏幕像素上的坐标
        xyInScrPx[0] = (int) e.getX();
        xyInScrPx[1] = (int) e.getY();

        //长按点在地图像素上的坐标
        xyInMapPx[0] = xyInScrPx[0] - xyOffset[0];
        xyInMapPx[1] = xyInScrPx[1] - xyOffset[1];

        //长按点在地图格子上的坐标
        xyInMapTile[0] = xyInMapPx[0] / Values.MAP_TILE_WIDTH;
        xyInMapTile[1] = xyInMapPx[1] / Values.MAP_TILE_HEIGHT;

        //光标移到新坐标
        cursor.setXy(xyInMapTile);

        //长按点在屏幕哪个区域
        isLeft = xyInScrPx[0] < Values.SCREEN_WIDTH / 2;
        isDown = xyInScrPx[1] > Values.SCREEN_HEIGHT / 2;

        switch (GAME_CASE) {
            //通常状态
            case Values.CASE_NORMAL:
                selectedActor = Actors.getActor(cursor.getXy());
                if (selectedActor != null) {//长按点有角色，显示角色信息
                    cursor.setLastXy(selectedActor.getXyInMapTile());
                    actorInfo.setActor(selectedActor);
                    GAME_CASE = Values.CASE_SHOW_ACTOR_INFO;
                    Log.d("GAME_CASE", "SHOW_ACTOR_INFO");
                }
                break;
            //显示角色信息
            case Values.CASE_SHOW_ACTOR_INFO:
                cursor.setXy(cursor.getLastXy());
                GAME_CASE = Values.CASE_NORMAL;//长按返回通常状态
                Log.d("GAME_CASE", "NORMAL");
                break;
            default:
                break;
        }
    }


    //单击
    @Override
    public boolean onSingleTapConfirmed(MotionEvent e) {
//        if (TURN_PHASE != Values.PARTY_PLAYER)//非我方阶段不响应屏幕操作
//            return true;

        //单击点在屏幕像素上的坐标
        xyInScrPx[0] = (int) e.getX();
        xyInScrPx[1] = (int) e.getY();

        //单击点在地图像素上的坐标
        xyInMapPx[0] = xyInScrPx[0] - xyOffset[0];
        xyInMapPx[1] = xyInScrPx[1] - xyOffset[1];

        //单击点在地图格子上的坐标
        xyInMapTile[0] = xyInMapPx[0] / Values.MAP_TILE_WIDTH;
        xyInMapTile[1] = xyInMapPx[1] / Values.MAP_TILE_HEIGHT;

        //光标移到新坐标
        cursor.setXy(xyInMapTile);

        //单击点在屏幕哪个区域
        isLeft = xyInScrPx[0] < Values.SCREEN_WIDTH / 2;
        isDown = xyInScrPx[1] > Values.SCREEN_HEIGHT / 2;

        Log.d("XY_CLICK", cursor.getXy()[0] + "," + cursor.getXy()[1]);

        switch (GAME_CASE) {
            //通常状态
            case Values.CASE_NORMAL:
                selectedActor = Actors.getActor(cursor.getXy());
                actorInfo.setActor(selectedActor);
                Log.d("GAME_CASE", "NORMAL");
                break;
            //移动前
            case Values.CASE_BEFORE_MOVE:
                if (selectedActor.getParty() == Values.PARTY_PLAYER &&
                        rangeHelper.canMoveTo(cursor.getXy())) {//进入移动前状态的是我方角色，并且这次单击的坐标在可移动范围内
                    targetActor = Actors.getActor(cursor.getXy());
                    if (targetActor == null || targetActor.equals(selectedActor)) {//单击的坐标上没有角色，或者有角色但是自己
                        cursor.setLastXy(selectedActor.getXyInMapTile());//保存移动前角色的坐标
                        Log.d("XY_BEFORE_MOVE", cursor.getLastXy()[0] + "," + cursor.getLastXy()[1]);
                        rangeHelper.setMovePath(cursor.getXy());//设置到单击坐标的移动路径
                        GAME_CASE = Values.CASE_MOVING;
                        Log.d("GAME_CASE", "MOVING");
                    } else {//单击的坐标上有角色，也不是自己，那么选中该角色
                        selectedActor = targetActor;
                        GAME_CASE = Values.CASE_NORMAL;
                        Log.d("GAME_CASE", "NORMAL");
                    }
                } else {//进入移动前状态的不是我方角色，或者这次单击的坐标在可移动范围外
                    selectedActor = Actors.getActor(cursor.getXy());
                    GAME_CASE = Values.CASE_NORMAL;
                    Log.d("GAME_CASE", "NORMAL");
                }
                targetActor = null;
                break;
            //移动中
            case Values.CASE_MOVING:
                break;
            //移动后，选择行动选项
            case Values.CASE_AFTER_MOVE:
                switch (actorAction.checkAction(xyInScrPx)) {
                    case ActorAction.ACTION_ATTACK:
                        cursor.setXy(actorAction.getTargetList().get(0).getXyInMapTile());
                        targetActor = Actors.getActor(cursor.getXy());
                        selectedActor.setCanAttack(true);
                        if (rangeHelper.getAttackRange(targetActor).contains(selectedActor.getXyInMapTile()))
                            targetActor.setCanAttack(true);//目标能反击
                        else
                            targetActor.setCanAttack(false);//目标不能反击
                        GAME_CASE = Values.CASE_BEFORE_ACT;//行动前，选择行动目标
                        Log.d("GAME_CASE", "BEFORE_ACT");
                        break;
                    case ActorAction.ACTION_ITEMS:
                        break;
                    case ActorAction.ACTION_STANDBY:
                        selectedActor.standby();//角色待机
                        cursor.setXy(cursor.getLastXy());
                        GAME_CASE = Values.CASE_NORMAL;
                        Log.d("GAME_CASE", "NORMAL");
                        break;
                    case ActorAction.ACTION_FAILED://单击的行动选项不可用，或单击选项外的区域，取消移动
                        selectedActor.setXyInMapTile(cursor.getLastXy());
                        selectedActor.lostCursor();
                        cursor.setXy(cursor.getLastXy());
                        rangeHelper.setSrcActor(selectedActor);//设置移动起始角色，计算移动范围和攻击范围
                        GAME_CASE = Values.CASE_BEFORE_MOVE;//进入移动前状态
                        Log.d("GAME", "MOVE_CANCEL");
                        Log.d("GAME_CASE", "BEFORE_MOVE");
                        break;
                }
                break;
            //行动前，选择攻击目标和显示战斗信息
            case Values.CASE_BEFORE_ACT:
                targetActor = actorAction.getTargetActor(cursor.getXy());
                if (targetActor == null) {//单击点无目标，取消行动
                    GAME_CASE = Values.CASE_AFTER_MOVE;
                    Log.d("GAME", "ACT_CANCEL");
                    Log.d("GAME_CASE", "AFTER_MOVE");
                } else if (rangeHelper.getAttackRange(targetActor).contains(selectedActor.getXyInMapTile())) {
                    targetActor.setCanAttack(true);//目标能反击
                    Log.d("Target", targetActor.getName());
                } else {
                    targetActor.setCanAttack(false);//目标不能反击
                    Log.d("Target", targetActor.getName());
                }
                break;
            //显示角色信息
            case Values.CASE_SHOW_ACTOR_INFO:
                break;
            //显示游戏选项
            case Values.CASE_SHOW_GAME_OPTIONS:
                switch (gameOption.checkAction(xyInScrPx)) {
                    case GameOption.OPTIONS_OVER:
                        GAME_CASE = Values.CASE_NORMAL;
                        Log.d("GAME_CASE", "NORMAL");
                        PhaseOver();//阶段结束
                        break;
                    default:
                        GAME_CASE = Values.CASE_NORMAL;
                        Log.d("GAME_CASE", "NORMAL");
                        break;
                }
                break;
            default:
                GAME_CASE = Values.CASE_NORMAL;
                Log.d("GAME_CASE", "NORMAL");
        }
        return true;
    }

    //双击
    @Override
    public boolean onDoubleTap(MotionEvent e) {
//        if (TURN_PHASE != Values.PARTY_PLAYER)//非我方阶段不响应屏幕操作
//            return true;

        //双击点在屏幕像素上的坐标
        xyInScrPx[0] = (int) e.getX();
        xyInScrPx[1] = (int) e.getY();

        //双击点在地图像素上的坐标
        xyInMapPx[0] = xyInScrPx[0] - xyOffset[0];
        xyInMapPx[1] = xyInScrPx[1] - xyOffset[1];

        //双击点在地图格子上的坐标
        xyInMapTile[0] = xyInMapPx[0] / Values.MAP_TILE_WIDTH;
        xyInMapTile[1] = xyInMapPx[1] / Values.MAP_TILE_HEIGHT;

        //光标移到新坐标
        cursor.setXy(xyInMapTile);

        //双击点在屏幕哪个区域
        isLeft = xyInScrPx[0] < Values.SCREEN_WIDTH / 2;
        isDown = xyInScrPx[1] > Values.SCREEN_HEIGHT / 2;

        switch (GAME_CASE) {
            //通常
            case Values.CASE_NORMAL:
                selectedActor = Actors.getActor(cursor.getXy());
                if (selectedActor == null || selectedActor.isStandby()) {//双击空白处或已待机角色，显示游戏选项
                    GAME_CASE = Values.CASE_SHOW_GAME_OPTIONS;
                    Log.d("GAME_CASE", "SHOW_GAME_OPTIONS");
                } else {//双击未待机角色，显示移动范围
                    rangeHelper.setSrcActor(selectedActor);//设置移动起始角色，计算移动范围和攻击范围
                    GAME_CASE = Values.CASE_BEFORE_MOVE;//进入移动前状态
                    Log.d("GAME_CASE", "BEFORE_MOVE");
                }
                break;
            case Values.CASE_SHOW_ACTOR_INFO:
                GAME_CASE = Values.CASE_NORMAL;
                break;
            //行动前阶段，确定行动目标
            case Values.CASE_BEFORE_ACT:
                targetActor = actorAction.getTargetActor(cursor.getXy());
                if (targetActor == null) {//单击点无目标，取消行动
                    GAME_CASE = Values.CASE_AFTER_MOVE;
                    Log.d("GAME_CASE", "AFTER_MOVE");
                } else {//双击点有目标，开始行动
                    lastXY = selectedActor.getXyInMapTile();
                    selectedActor.setCanAttack(true);
                    if (rangeHelper.getAttackRange(targetActor).indexOf(selectedActor.getXyInMapTile()) != -1)
                        targetActor.setCanAttack(true);//目标能反击
                    else
                        targetActor.setCanAttack(false);//目标不能反击
                    GAME_CASE = Values.CASE_ACTING;
                    Log.d("GAME_CASE", "ACTING");
                    Log.d("TARGET", targetActor.getName());
                }
                break;
            default:
                break;
        }
        return true;
    }

    /**
     * 在屏幕上拖动
     *
     * @param e1        整个滑动过程中某个起点
     * @param e2        整个滑动过程中某个终点
     * @param distanceX = e1.x-e2.x，结果带符号
     * @param distanceY = e1.y-e2.y，同上
     * @return
     */
    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        switch (GAME_CASE) {
            case Values.CASE_NORMAL:
            case Values.CASE_BEFORE_MOVE:
            case Values.CASE_MOVING:
            case Values.CASE_AFTER_MOVE:
            case Values.CASE_BEFORE_ACT:
            case Values.CASE_SHOW_GAME_OPTIONS:
                if (xyOffset[0] - (int) distanceX + map.getMapWidth() <= Values.SCREEN_WIDTH)//向左滑到了最右
                    xyOffset[0] = -(map.getMapWidth() - Values.SCREEN_WIDTH);//偏移量=地图宽度-屏幕宽度，使地图右边界在屏幕右边界
                else if (xyOffset[0] - (int) distanceX >= 0)//最左
                    xyOffset[0] = 0;
                else xyOffset[0] -= (int) distanceX;

                if (xyOffset[1] - (int) distanceY + map.getMapHeight() <= Values.SCREEN_HEIGHT)//向上滑到了最下
                    xyOffset[1] = -(map.getMapHeight() - Values.SCREEN_HEIGHT);
                else if (xyOffset[1] - (int) distanceY >= 0)//最上
                    xyOffset[1] = 0;
                else xyOffset[1] -= (int) distanceY;
                break;
            case Values.CASE_SHOW_ACTOR_INFO:
                break;
            default:
        }
        return true;
    }

    //阶段结束
    public static void PhaseOver(){
        for (Actor actor: Actors.actors) {
            actor.awake();
        }
        Log.d("TURN_PHASE", Values.PARTY_NAME[NOW_PARTY] + " Phase Over");
        TURN_PHASE++;
        if (TURN_PHASE == PARTY_COUNT) {//所有阵营行动完，回合结束
            TurnOver();
        } else {
            NOW_PARTY = PARTY_INDEX[TURN_PHASE];
            Log.d("TURN_PHASE", Values.PARTY_NAME[NOW_PARTY] + " Phase Start");
        }
    }


    //回合结束
    public static void TurnOver(){
        Log.d("GAME_TURN", GAME_TURN+" Turn Over");
        TURN_PHASE = Values.PARTY_PLAYER;
        GAME_TURN += 1;
        Log.d("GAME_TURN", GAME_TURN+" Turn Start");
    }

    public void setParties() {
        PARTY_COUNT = 0;//阵营数置0
        PARTY_INDEX = new int[Values.PARTY_COUNT];
        parties = new HashMap<>();
        ArrayList<Actor> player = new ArrayList<>();
        ArrayList<Actor> ally = new ArrayList<>();
        ArrayList<Actor> enemy = new ArrayList<>();
        for (Actor actor : Actors.actors) {
            switch (actor.getParty()) {
                case Values.PARTY_PLAYER:
                    player.add(actor);
                    break;
                case Values.PARTY_ALLY:
                    ally.add(actor);
                    break;
                case Values.PARTY_ENEMY:
                    enemy.add(actor);
                    break;
            }
        }


        if (!player.isEmpty()) {
            PARTY_INDEX[PARTY_COUNT] = Values.PARTY_PLAYER;
            PARTY_COUNT++;
            parties.put("Player", player);
        }
        if (!ally.isEmpty()) {
            PARTY_INDEX[PARTY_COUNT] = Values.PARTY_ALLY;
            PARTY_COUNT++;
            parties.put("Ally", ally);
        }
        if (!enemy.isEmpty()) {
            PARTY_INDEX[PARTY_COUNT] = Values.PARTY_ENEMY;
            PARTY_COUNT++;
            parties.put("Enemy", enemy);
        }
    }

    @Override
    public boolean onDoubleTapEvent(MotionEvent e) {
        return false;
    }

    @Override
    public void run() {
        long nowTime;
        long newTime;
        long waitTime;
        while (isRunning) {
            nowTime = System.currentTimeMillis();
            //加上线程安全锁
            synchronized (surfaceHolder) {
                if (surfaceHolder != null) {
                    try {
                        //拿到当前画布然后锁定
                        canvas = surfaceHolder.lockCanvas();
                        Draw();
                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        //绘制结束后解锁画布
                        if (canvas != null)
                            surfaceHolder.unlockCanvasAndPost(canvas);
                    }
                }
            }
            newTime = System.currentTimeMillis();
            waitTime = 1000 / Values.FPS - (newTime - nowTime);
            if (waitTime > 0)
                try {
                    Thread.sleep(waitTime);
                } catch (Exception e) {
                    e.printStackTrace();
                }
        }

    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        isRunning = true;
        thread = new Thread(this);
        thread.start();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        isRunning = false;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        return gestureDetector.onTouchEvent(event);
    }

    @Override
    public boolean onDown(MotionEvent e) {
        return false;
    }

    @Override
    public void onShowPress(MotionEvent e) {

    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        return false;
    }


    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        switch (GAME_CASE) {
            case Values.CASE_SHOW_ACTOR_INFO:
                if (e2.getX() - e1.getX() > 200)
                    actorInfo.turnLeft();
                else if (e2.getX() - e1.getX() < -200)
                    actorInfo.turnRight();
                break;
        }
        return true;
    }
}
