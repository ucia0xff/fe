package io.ucia0xff.fe.game;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

import java.util.List;

import io.ucia0xff.fe.Values;
import io.ucia0xff.fe.actor.*;
import io.ucia0xff.fe.anim.*;
import io.ucia0xff.fe.map.*;

public class GameView extends SurfaceView
        implements SurfaceHolder.Callback,
        View.OnTouchListener,
        GestureDetector.OnGestureListener,
        GestureDetector.OnDoubleTapListener,
        Runnable {

    public static int parties = 0;//阵营总数
    public static int nowParty = 0;//当前行动的阵营
    public static java.util.Map<String, Actor> actors = null;//场上所有角色的列表
    public static List<Actor> targetList = null;//目标角色列表
    public static Actor selectedActor = null;//当前角色
    public static Actor targetActor = null;//目标角色
    public Map map;//地图对象
    public CursorAnim cursor;//鼠标动画
    public MapInfo mapInfo;
    public ActorMove move;
    public ActorInfo actorInfo;
    public ActorAction actorAction;
//    GameOptions gameOptions;

    //画笔
    private Paint paint = null;

    //画布
    private Canvas canvas = null;

    // 游戏主线程
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
    public static int[] cursorLastXY = {0, 0};

    //游戏进行阶段
    public static int GAME_CASE = Values.CASE_NORMAL;

    //构造方法
    public GameView() {
        super(Values.CONTEXT);
        paint = new Paint();
        paint.setTextSize(80);
        paint.setColor(Color.WHITE);
        actors = Actors.getActors();
        map = new Map("map1");
        mapInfo = new MapInfo(map);
        cursor = CursorAnims.cursorAnims.get(Values.CURSOR_DYNAMIC);
        move = new ActorMove(map);
        setParties(actors);
        actorInfo = new ActorInfo();
        actorAction = new ActorAction(map);
//        gameOptions = new GameOptions(context);


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
            case Values.CASE_NORMAL:
                map.drawMap(canvas, paint, xyOffset);
                DrawActors(canvas, paint, xyOffset);
                DrawCursor(canvas, paint, xyOffset);
                mapInfo.show(canvas, paint, isLeft);
                break;
            case Values.CASE_BEFORE_MOVE:
                map.drawMap(canvas, paint, xyOffset);
                move.drawMoveRange(canvas, paint, xyOffset);
                move.drawAllAttackRange(canvas, paint, xyOffset);
                DrawActors(canvas, paint, xyOffset);
                DrawCursor(canvas, paint, xyOffset);
                break;
            case Values.CASE_MOVING:
                map.drawMap(canvas, paint, xyOffset);
                if (!(selectedActor.move(move.getMovePath()))) {
                    GAME_CASE = Values.CASE_AFTER_MOVE;
                    move.setAttackRange(selectedActor.getXyInMapTile());
                    actorAction.setSrcActor(selectedActor);
                    Log.d("GAME_CASE", "AFTER_MOVE");
                }
                DrawActors(canvas, paint, xyOffset);
                break;
            case Values.CASE_AFTER_MOVE:
                map.drawMap(canvas, paint, xyOffset);
                DrawActors(canvas, paint, xyOffset);
                move.drawAttackRange(canvas, paint, xyOffset);
                actorAction.show(canvas, paint);
                break;
            case Values.CASE_BEFORE_ACT:
                map.drawMap(canvas, paint, xyOffset);
                DrawActors(canvas, paint, xyOffset);
                move.drawAttackRange(canvas, paint, xyOffset);
                DrawCursor(canvas, paint, xyOffset);
                break;
            case Values.CASE_ACTING:
                map.drawMap(canvas, paint, xyOffset);
                DrawActors(canvas, paint, xyOffset);
                GAME_CASE = Values.CASE_NORMAL;
                Log.d("GAME_CASE", "NORMAL");
                break;
            case Values.CASE_SHOW_ACTOR_INFO:
                actorInfo.display(canvas, paint);
                break;
            case Values.CASE_SHOW_GAME_OPTIONS:
                map.drawMap(canvas, paint, xyOffset);
                DrawActors(canvas, paint, xyOffset);
//                DisplayGameOptions(canvas, paint, isLeft);
                break;
        }
    }

    //显示地图上所有角色
    private void DrawActors(Canvas canvas, Paint paint, int[] xyOffset) {
        for (Actor actor : actors.values()) {
            actor.drawAnim(canvas, paint, xyOffset);
        }
    }

    //显示光标
    private void DrawCursor(Canvas canvas, Paint paint, int[] xyOffset) {
        if (selectedActor == null || selectedActor.getParty() != Values.PARTY_PLAYER || selectedActor.isStandby()) {
            cursor = CursorAnims.cursorAnims.get(Values.CURSOR_DYNAMIC);
        } else {
            cursor = CursorAnims.cursorAnims.get(Values.CURSOR_STATIC);
            selectedActor.getCursor();
        }
        cursor.drawAnim(canvas, paint, cursorXY, xyOffset);
    }


    //长按
    @Override
    public void onLongPress(MotionEvent e) {
        if (nowParty != Values.PARTY_PLAYER)//非我方阶段不响应屏幕操作
            return;

        //长按点在屏幕像素上的坐标
        xyInScrPx[0] = (int) e.getX();
        xyInScrPx[1] = (int) e.getY();

        //长按点在地图像素上的坐标
        xyInMapPx[0] = xyInScrPx[0] - xyOffset[0];
        xyInMapPx[1] = xyInScrPx[1] - xyOffset[1];

        //长按点在地图格子上的坐标
        xyInMapTile[0] = xyInMapPx[0] / Values.MAP_TILE_WIDTH;
        xyInMapTile[1] = xyInMapPx[1] / Values.MAP_TILE_HEIGHT;

//        //保存光标当前坐标
//        cursorLastXY[0] = cursorXY[0];
//        cursorLastXY[1] = cursorXY[1];
        //光标移到新坐标
        cursorXY[0] = xyInMapTile[0];
        cursorXY[1] = xyInMapTile[1];

        //长按点在屏幕哪个区域
        isLeft = xyInScrPx[0] < Values.SCREEN_WIDTH / 2;
        isDown = xyInScrPx[1] > Values.SCREEN_HEIGHT / 2;

        switch (GAME_CASE) {
            //通常阶段
            case Values.CASE_NORMAL:
                if (selectedActor != null)
                    selectedActor.lostCursor();
                selectedActor = Actors.getActor(cursorXY);
                if (selectedActor != null) {//长按点有角色，游戏进入显示角色信息阶段
                    selectedActor.getCursor();
                    actorInfo.setActor(selectedActor);
                    GAME_CASE = Values.CASE_SHOW_ACTOR_INFO;
                    Log.d("GAME_CASE", "SHOW_ACTOR_INFO");
                }
                break;
            //显示角色信息阶段
            case Values.CASE_SHOW_ACTOR_INFO:
                cursorXY[0] = selectedActor.getXyInMapTile()[0];
                cursorXY[1] = selectedActor.getXyInMapTile()[1];
                GAME_CASE = Values.CASE_NORMAL;
                Log.d("GAME_CASE", "NORMAL");
                break;
            default:
                break;
        }
    }


    //单击
    @Override
    public boolean onSingleTapConfirmed(MotionEvent e) {
        if (nowParty != Values.PARTY_PLAYER)//非我方阶段不响应屏幕操作
            return true;

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
        cursorXY[0] = xyInMapTile[0];
        cursorXY[1] = xyInMapTile[1];

        //单击点在屏幕哪个区域
        isLeft = xyInScrPx[0] < Values.SCREEN_WIDTH / 2;
        isDown = xyInScrPx[1] > Values.SCREEN_HEIGHT / 2;

        Log.d("XY_CLICK", cursorXY[0] + "," + cursorXY[1]);

        switch (GAME_CASE) {
            //一般阶段
            case Values.CASE_NORMAL:
                if (selectedActor != null)
                    selectedActor.lostCursor();
                selectedActor = Actors.getActor(cursorXY);
                if (selectedActor != null)
                    selectedActor.getCursor();
                Log.d("GAME_CASE", "NORMAL");
                break;
            //移动前阶段
            case Values.CASE_BEFORE_MOVE:
                if (selectedActor.getParty() == Values.PARTY_PLAYER &&
                        move.canMoveTo(cursorXY)) {//进入移动前状态的是我方角色，并且这次单击的坐标在可移动范围内
                    targetActor = Actors.getActor(cursorXY);
                    if (targetActor == null || targetActor.equals(selectedActor)) {//单击的坐标上没有角色，或者有角色但是自己
                        cursorLastXY[0] = selectedActor.getXyInMapTile()[0];//保存移动前角色的坐标
                        cursorLastXY[1] = selectedActor.getXyInMapTile()[1];
                        Log.d("XY_BEFORE_MOVE", cursorLastXY[0] + "," + cursorLastXY[1]);
                        move.setMovePath(cursorXY);//设置到单击坐标的移动路径
                        GAME_CASE = Values.CASE_MOVING;
                        Log.d("GAME_CASE", "MOVING");
                    } else {//单击的坐标上有角色，也不是自己，那么选中该角色
                        selectedActor.lostCursor();
                        selectedActor = targetActor;
                        selectedActor.getCursor();
                        GAME_CASE = Values.CASE_NORMAL;
                    }
                } else {//进入移动前状态的不是我方角色，或者这次单击的坐标在可移动范围外
                    selectedActor.lostCursor();
                    selectedActor = Actors.getActor(cursorXY);
                    if (selectedActor != null) {//单击的坐标上有角色，那么选中该角色
                        selectedActor.getCursor();
                    }
                    GAME_CASE = Values.CASE_NORMAL;
                    Log.d("GAME_CASE", "NORMAL");
                }
                break;
            //移动中
            case Values.CASE_MOVING:
                break;
            //移动后阶段，选择行动选项
            case Values.CASE_AFTER_MOVE:
                if (actorAction.checkAction(xyInScrPx)) {//单击的行动选项可用
                    targetActor = actorAction.getTargetActor(0);
                    cursorXY = targetActor.getXyInMapTile();
                    GAME_CASE = Values.CASE_BEFORE_ACT;//进入行动前阶段，选择行动目标
                    Log.d("GAME_CASE", "BEFORE_ACT");
                    Log.d("TARGET", targetActor.getName());
                } else {//单击的行动选项不可用，或单击选项外的区域，取消移动
//                    if (selectedActor != null)
//                        selectedActor.lostCursor();
//                    selectedActor = Actors.getActor(cursorXY);
//                    if (selectedActor != null)
//                        selectedActor.getCursor();
                    selectedActor.setXyInMapTile(cursorLastXY);
                    cursorXY[0] = cursorLastXY[0];
                    cursorXY[1] = cursorLastXY[1];
                    GAME_CASE = Values.CASE_NORMAL;
                    Log.d("GAME_CASE", "NORMAL");
                }
                break;
            //行动前阶段，选择攻击目标和显示战斗信息
            case Values.CASE_BEFORE_ACT:
                targetActor = actorAction.getTargetActor(cursorXY);
                if (targetActor == null) {//单击点无目标，取消行动
                    GAME_CASE = Values.CASE_AFTER_MOVE;
                    Log.d("GAME_CASE", "AFTER_MOVE");
                } else {
                    Log.d("TARGET", targetActor.getName());
                }
                break;
            //显示角色信息
            case Values.CASE_SHOW_ACTOR_INFO:
                break;
            case Values.CASE_SHOW_GAME_OPTIONS:
                Log.d("GAME_CASE", "NORMAL");
                GAME_CASE = Values.CASE_NORMAL;
                break;
            default:
                GAME_CASE = Values.CASE_NORMAL;
        }
        return true;
    }

    //双击
    @Override
    public boolean onDoubleTap(MotionEvent e) {
        if (nowParty != Values.PARTY_PLAYER)//非我方阶段不响应屏幕操作
            return true;
        //双击点在屏幕像素上的坐标
        xyInScrPx[0] = (int) e.getX();
        xyInScrPx[1] = (int) e.getY();

        //双击点在地图像素上的坐标
        xyInMapPx[0] = xyInScrPx[0] - xyOffset[0];
        xyInMapPx[1] = xyInScrPx[1] - xyOffset[1];

        //双击点在地图格子上的坐标
        xyInMapTile[0] = xyInMapPx[0] / Values.MAP_TILE_WIDTH;
        xyInMapTile[1] = xyInMapPx[1] / Values.MAP_TILE_HEIGHT;

//        //保存光标当前坐标
//        cursorLastXY[0] = cursorXY[0];
//        cursorLastXY[1] = cursorXY[1];
        //光标移到新坐标
        cursorXY[0] = xyInMapTile[0];
        cursorXY[1] = xyInMapTile[1];

        //双击点在屏幕哪个区域
        isLeft = xyInScrPx[0] < Values.SCREEN_WIDTH / 2;
        isDown = xyInScrPx[1] > Values.SCREEN_HEIGHT / 2;

        switch (GAME_CASE) {
            //通常阶段
            case Values.CASE_NORMAL:
                if (selectedActor != null)
                    selectedActor.lostCursor();
                selectedActor = Actors.getActor(cursorXY);
                if (selectedActor == null || selectedActor.isStandby()) {//双击空白处或已待机角色
                    GAME_CASE = Values.CASE_SHOW_GAME_OPTIONS;//显示游戏菜单
                    Log.d("GAME_CASE", "SHOW_GAME_OPTIONS");
                } else {//双击未待机角色
                    selectedActor.getCursor();
                    move.setSrcActor(selectedActor);//设置移动起始角色，计算移动范围和攻击范围
                    GAME_CASE = Values.CASE_BEFORE_MOVE;//进入移动前阶段
                    Log.d("GAME_CASE", "BEFORE_MOVE");
                }
                break;
            case Values.CASE_SHOW_ACTOR_INFO:
                GAME_CASE = Values.CASE_NORMAL;
                break;
            //行动前阶段，确定行动目标
            case Values.CASE_BEFORE_ACT:
                targetActor = actorAction.getTargetActor(cursorXY);
                if (targetActor == null) {//双击点无目标，光标不移动，目标不变
                    cursorXY[0] = cursorLastXY[0];
                    cursorXY[1] = cursorLastXY[1];
                    targetActor = actorAction.getTargetActor(cursorXY);
                } else {//双击点有目标，开始行动
                    GAME_CASE = Values.CASE_ACTING;
                    Log.d("GAME_CASE", "ACTING");
                    Log.d("TARGET", targetActor.getName());
                }
                break;
            default:
                GAME_CASE = Values.CASE_NORMAL;
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
            case Values.CASE_SELECT_ITEM:
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

/*    //回合结束
    public static void TurnOver(){
        for (Actor actor:actors){
            actor.setStandby(false);
            actor.setNowAnim(Values.MAP_ANIM_STATIC);
        }
        nowParty = (nowParty+1)%parties;
    }*/


    public void setParties(java.util.Map<String, Actor> actors) {
        parties = 0;
        int[] partyCount = new int[Values.PARTY_COUNT];

        for (Actor actor : actors.values()) {
            partyCount[actor.getParty()]++;
        }
        for (int n : partyCount) {
            if (n > 0) parties++;
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
