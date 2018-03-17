package io.ucia0xff.fe.game;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

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
//    public static Actor[] actors = null;//所有角色列表
//    public static Actor selectedActor = null;//当前角色
//    public static Actor targetActor = null;//目标角色
    Map map;
    CursorAnim cursor;
    MapInfo mapInfo;
    ActorMoveHelper move;
    Actor actor;
//    ActorInfoPanel actorInfoPanel;
//    GameOptions gameOptions;
//    Move.Node targetNode;//移动地点

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
    public static int[] xyOffset = {0,0};

    //单击点在屏幕上的像素坐标
    private int[] xyInScreen = {0,0};
    //单击点在地图上的像素坐标
    private int[] xyInMap = {0,0};
    //单击点在地图上的格子坐标
    private int[] xyInTile = {0,0};

    //是否在屏幕某区域
    private Boolean isLeft = true;
    private Boolean isDown = true;

    //光标在地图中的格子坐标
    public static int[] cursorXY = {0,0};

    //游戏进行阶段
    public static int GAME_CASE = Values.CASE_NORMAL;

    //移动方向
    public static String[] directions;
    public static int dirIndex = 0;

    //构造方法
    public GameView() {
        super(Values.CONTEXT);
        paint = new Paint();
        paint.setTextSize(80);
        paint.setColor(Color.WHITE);
//        actors = new Actor[1];
//            actors[0] = new Actor("Natasha");
//            Career career = new Career("Sister");
            map = new Map("map1");
            mapInfo = new MapInfo(map);
            cursor = CursorAnims.cursorAnims.get(Values.CURSOR_DYNAMIC);
            actor = Actors.actors.get("Natasha");
            move = new ActorMoveHelper(map);
            move.setSrcActor(actor);
//        setParties(actors);
//        actorInfoPanel = new ActorInfoPanel(context);
//        gameOptions = new GameOptions(context);

        surfaceHolder = getHolder();
        surfaceHolder.addCallback(this);
        setClickable(true);
        setFocusable(true);
        gestureDetector = new GestureDetector(this);
        setOnTouchListener(this);
    }

    //绘制游戏画面
    protected void Draw() {
        Map.DrawMap(canvas, paint, xyOffset);
        cursor.drawAnim(canvas, paint, cursorXY, xyOffset);
        mapInfo.show(canvas, paint, isLeft);
        move.drawCanMove(canvas, paint, xyOffset);
        actor.drawAnim(canvas, paint, xyOffset);
//        setParties(actors);
/*        switch (GAME_CASE) {
            case Values.CASE_NORMAL:
                tileIndex.DrawMap(canvas, paint, mapOffsetScreenX, mapOffsetScreenY);
                DrawActors(canvas, paint, mapOffsetScreenX, mapOffsetScreenY);
                DrawCursor(canvas, paint, mapOffsetScreenX, mapOffsetScreenY);
//                terrainInfoPanel.DisplayTileInfo(canvas, paint, isLeft);
                break;
            case Values.CASE_BEFORE_MOVE:
                tileIndex.DrawMap(canvas, paint, mapOffsetScreenX, mapOffsetScreenY);
                DrawMoveArea(canvas, paint, mapOffsetScreenX, mapOffsetScreenY);
                DrawActors(canvas, paint, mapOffsetScreenX, mapOffsetScreenY);
                DrawCursor(canvas, paint, mapOffsetScreenX, mapOffsetScreenY);
                break;
            case Values.CASE_MOVING:
                tileIndex.DrawMap(canvas, paint, mapOffsetScreenX, mapOffsetScreenY);
                DrawActors(canvas, paint, mapOffsetScreenX, mapOffsetScreenY);
                Moving(selectedActor);
                break;
            case Values.CASE_AFTER_MOVE:
                tileIndex.DrawMap(canvas, paint, mapOffsetScreenX, mapOffsetScreenY);
                DrawActors(canvas, paint, mapOffsetScreenX, mapOffsetScreenY);
                DisplayActorOptions(selectedActor);
                break;
            case Values.CASE_BEFORE_ACT:
                break;
            case Values.CASE_SHOW_ACTOR_INFO:
                actorInfoPanel.DisplayActorInfo(canvas, paint, selectedActor);
                break;
            case Values.CASE_SHOW_GAME_OPTIONS:
                tileIndex.DrawMap(canvas, paint, mapOffsetScreenX, mapOffsetScreenY);
                DrawActors(canvas, paint, mapOffsetScreenX, mapOffsetScreenY);
                DisplayGameOptions(canvas, paint, isLeft);
                break;
        }*/
    }

/*    private void DrawActors(Canvas canvas, Paint paint, int offsetX, int offsetY){
        for (Actor actor : actors) {
            if (actor.equals(selectedActor))
                continue;
            actor.RenderAnimation(canvas, paint, offsetX, offsetY);
        }
        if (selectedActor!=null)
            selectedActor.RenderAnimation(canvas, paint, offsetX, offsetY);
    }*/

    //显示光标
/*    private void DrawCursor(Canvas canvas, Paint paint, int offsetX, int offsetY) {
        selectedActor = getActor(cursorXY[0], cursorXY[1]);
        if (selectedActor==null || selectedActor.getParty() != Values.PARTY_PLAYER || selectedActor.isStandby()) {
            cursor = CursorAnims.CURSOR_ANIMS.get(Values.CURSOR_DYNAMIC);
        } else {
            cursor = CursorAnims.CURSOR_ANIMS.get(Values.CURSOR_STATIC);
            selectedActor.getCursor();
        }
        cursor.drawAnim(canvas, paint, new int[]{cursorXY[0], cursorXY[1]},offsetX, offsetY);
    }*/

    //显示移动区域
/*    public void DrawMoveArea(Canvas canvas, Paint paint, int offsetX, int offsetY) {
        if (selectedActor == null) return;
        move.setActor(selectedActor);
        move.DrawMoveArea(canvas, paint, offsetX, offsetY);
    }*/


    //显示行动选项
/*    public void DisplayActorOptions(Actor actor) {
        actor.setStandby(true);
        cursorXY[0] = actor.getXyTile()[0];
        cursorXY[1] = actor.getXyTile()[1];
        GAME_CASE = Values.CASE_NORMAL;
    }*/

    //显示游戏选项
/*    public void DisplayGameOptions(Canvas canvas, Paint paint, Boolean isLeft){
        if (GAME_CASE!=Values.CASE_SHOW_GAME_OPTIONS) return;
        gameOptions.DisplayGameOptions(canvas, paint, isLeft);
    }*/

    //长按
    @Override
    public void onLongPress(MotionEvent e) {
/*//        if (nowParty!=Values.PARTY_PLAYER)
//            return;
        xInScreen = (int) e.getX();
        yInScreen = (int) e.getY();
        xInMap = xInScreen - mapOffsetScreenX;
        yInMap = yInScreen - mapOffsetScreenY;
        xyInTile[0] = xInMap / Values.MAP_TILE_WIDTH;
        xyInTile[1] = yInMap / Values.MAP_TILE_HEIGHT;
        switch (GAME_CASE) {
            case Values.CASE_NORMAL:
                cursorXY[0] = xyInTile[0];
                cursorXY[1] = xyInTile[1];
                if (selectedActor != null)
                    selectedActor.lostCursor();
                selectedActor = getActor(cursorXY[0], cursorXY[1]);
                if (selectedActor != null) {
                    selectedActor.getCursor();
                    GAME_CASE = Values.CASE_SHOW_ACTOR_INFO;//游戏进入显示角色信息阶段
                }
                break;
            default:
                break;
        }*/
    }


    //单击
    @Override
    public boolean onSingleTapConfirmed(MotionEvent e) {
//        if (nowParty!=Values.PARTY_PLAYER)
//            return true;
        xyInScreen[0] = (int) e.getX();
        xyInScreen[1] = (int) e.getY();
        xyInMap[0] = xyInScreen[0] - xyOffset[0];
        xyInMap[1] = xyInScreen[1] - xyOffset[1];
        xyInTile[0] = xyInMap[0] / Values.MAP_TILE_WIDTH;
        xyInTile[1] = xyInMap[1] / Values.MAP_TILE_HEIGHT;
        cursorXY[0] = xyInTile[0];
        cursorXY[1] = xyInTile[1];
        isLeft = xyInScreen[0] < Values.SCREEN_WIDTH / 2;
        isDown = xyInScreen[1] > Values.SCREEN_HEIGHT / 2;
/*        targetActor = getActor(xyInTile[0], xyInTile[1]);
        switch (GAME_CASE) {
            //一般阶段
            case Values.CASE_NORMAL:
                cursorXY[0] = xyInTile[0];
                cursorXY[1] = xyInTile[1];
                if (selectedActor != null)
                    selectedActor.lostCursor();
                selectedActor = getActor(cursorXY[0], cursorXY[1]);
                if (selectedActor != null)
                    selectedActor.getCursor();
                isLeft = (xInScreen < Values.SCREEN_WIDTH / 2) ? true : false;
                isDown = (yInScreen > Values.SCREEN_HEIGHT / 2) ? true : false;
                break;
            //移动前
            case Values.CASE_BEFORE_MOVE:
                Move.NodeList canMove = move.getCanMove();
                if (selectedActor.getParty() == Values.PARTY_PLAYER && canMove.indexOf(new int[]{xyInTile[0], xyInTile[1]}) != -1) {
                    if (targetActor == null || targetActor.equals(selectedActor)) {//格子上没有单位或者是自己
                        directions = move.getMoveDirections(new int[]{xyInTile[0], xyInTile[1]});
                        GAME_CASE = Values.CASE_MOVING;
                    }
                    else{//格子上有单位且不是自己
                        cursorXY[0] = xyInTile[0];
                        cursorXY[1] = xyInTile[1];
                        selectedActor.lostCursor();
                        selectedActor=targetActor;
                        selectedActor.getCursor();
                        GAME_CASE = Values.CASE_NORMAL;
                    }
                }else {//非玩家单位
                    cursorXY[0] = xyInTile[0];
                    cursorXY[1] = xyInTile[1];
                    selectedActor.lostCursor();
                    selectedActor = targetActor;
                    if (selectedActor != null)
                        selectedActor.getCursor();
                    GAME_CASE = Values.CASE_NORMAL;
                }
                isLeft = (xInScreen < Values.SCREEN_WIDTH / 2) ? true : false;
                isDown = (yInScreen > Values.SCREEN_HEIGHT / 2) ? true : false;
                break;
            //显示角色信息
            case Values.CASE_SHOW_ACTOR_INFO:
                break;
            case Values.CASE_MOVING:
                break;
            case Values.CASE_SHOW_GAME_OPTIONS:
                if (gameOptions.CheckOption(xInScreen, yInScreen, isLeft) > GameOptions.OPTIONS.length){
                    GAME_CASE = Values.CASE_NORMAL;
                }
                break;
            default:
                GAME_CASE = Values.CASE_NORMAL;
        }*/
        return true;
    }

    //双击
    @Override
    public boolean onDoubleTap(MotionEvent e) {
//        if (nowParty!=Values.PARTY_PLAYER)
//            return true;
/*         xInScreen = (int) e.getX();
        yInScreen = (int) e.getY();
        xInMap = xInScreen - mapOffsetScreenX;
        yInMap = yInScreen - mapOffsetScreenY;
        xyInTile[0] = xInMap / Values.MAP_TILE_WIDTH;
        xyInTile[1] = yInMap / Values.MAP_TILE_HEIGHT;
        switch (GAME_CASE) {
            case Values.CASE_NORMAL:
                cursorXY[0] = xyInTile[0];
                cursorXY[1] = xyInTile[1];
                if (selectedActor != null)
                    selectedActor.lostCursor();
                selectedActor = getActor(cursorXY[0], cursorXY[1]);
                if (selectedActor == null || selectedActor.isStandby()) {
                    GAME_CASE = Values.CASE_SHOW_GAME_OPTIONS;
                }else{
                    selectedActor.getCursor();
                    if (selectedActor.getParty() == Values.PARTY_PLAYER){
                        selectedActor.setNowAnim(Values.MAP_ANIM_DOWN);
                    }
                    GAME_CASE = Values.CASE_BEFORE_MOVE;
                }
                isLeft = (xInScreen < Values.SCREEN_WIDTH / 2) ? true : false;
                isDown = (yInScreen > Values.SCREEN_HEIGHT / 2) ? true : false;
                break;
            case Values.CASE_SHOW_GAME_OPTIONS:
                gameOptions.HandleOption(xInScreen,yInScreen,isLeft);
                GAME_CASE = Values.CASE_NORMAL;
                break;
            case Values.CASE_SHOW_ACTOR_INFO:
                GAME_CASE = Values.CASE_NORMAL;
            default:
                break;
        }*/
        return true;
    }

    /**
     * 在屏幕上拖动
     * @param e1 整个滑动过程中某个起点
     * @param e2 整个滑动过程中某个终点
     * @param distanceX = e1.x-e2.x，结果带符号
     * @param distanceY = e1.y-e2.y，同上
     * @return
     */
    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
/*        switch (GAME_CASE) {
            case Values.CASE_NORMAL:
            case Values.CASE_BEFORE_MOVE:
            case Values.CASE_MOVING:
            case Values.CASE_AFTER_MOVE:
            case Values.CASE_BEFORE_ACT:
            case Values.CASE_SELECT_ITEM:*/
                if (xyOffset[0] - (int) distanceX + map.getMapWidth() <= Values.SCREEN_WIDTH)//向左滑到了最右
                    xyOffset[0] = -(map.getMapWidth() - Values.SCREEN_WIDTH);//偏移量=地图宽度-屏幕宽度，使地图右边界在屏幕右边界
                else if (xyOffset[0] - (int) distanceX >= 0)//最左
                    xyOffset[0] = 0;
                else xyOffset[0] -= (int) distanceX;

                if (xyOffset[1] - (int) distanceY + map.getMapHeight() <= Values.SCREEN_HEIGHT)//向上滑到了最下
                    xyOffset[1] = -(map.getMapHeight() - Values.SCREEN_HEIGHT);
                else if (xyOffset[1] - (int) distanceY >= 0)//最上
                    xyOffset[1] = 0;
                else xyOffset[1] -= (int) distanceY;/*
                break;
            case Values.CASE_SHOW_ACTOR_INFO:
                break;
            default:
        }*/
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

/*    //移动过程
    public void Moving(Actor actor) {
        int[] actorXY = actor.getXyTile();
        if (dirIndex < actor.getMove()) {
            switch (directions[dirIndex]) {
                case Values.MAP_ANIM_UP:
                    actorXY[1] -= 1;
                    actor.setNowAnim(Values.MAP_ANIM_UP);
                    actor.setXyTile(actorXY);
                    dirIndex++;
                    break;
                case Values.MAP_ANIM_RIGHT:
                    actorXY[0] += 1;
                    actor.setNowAnim(Values.MAP_ANIM_RIGHT);
                    actor.setXyTile(actorXY);
                    dirIndex++;
                    break;
                case Values.MAP_ANIM_DOWN:
                    actorXY[1] += 1;
                    actor.setNowAnim(Values.MAP_ANIM_DOWN);
                    actor.setXyTile(actorXY);
                    dirIndex++;
                    break;
                case Values.MAP_ANIM_LEFT:
                    actorXY[0] -= 1;
                    actor.setNowAnim(Values.MAP_ANIM_LEFT);
                    actor.setXyTile(actorXY);
                    dirIndex++;
                    break;
                default:
                    GAME_CASE = Values.CASE_AFTER_MOVE;//未达到最大移动力或通过复杂地形的移动结束
                    dirIndex = 0;//复位
            }
        } else {
            GAME_CASE = Values.CASE_AFTER_MOVE;//达到最大移动力的移动结束
            dirIndex = 0;//复位
        }
    }*/

/*    public void setParties(Actor[] actors){
        parties = 0;
        int[] partyCount = new int[Values.PARTY_COUNT];
        for (Actor actor:actors){
            partyCount[actor.getParty()]++;
        }
        for (int n:partyCount){
            if (n>0) parties++;
        }
    }*/

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
            //在这里加上线程安全锁
            synchronized (surfaceHolder) {
                if (surfaceHolder != null)try {
                    /*拿到当前画布 然后锁定*/
                    canvas = surfaceHolder.lockCanvas();
                    Draw();
                } catch (Exception e){
                    e.printStackTrace();
                } finally {
                    /*绘制结束后解锁显示在屏幕上*/
                    if(canvas !=null)
                        surfaceHolder.unlockCanvasAndPost(canvas);
                }
            }
            newTime = System.currentTimeMillis();
            waitTime = 1000/Values.FPS - (newTime-nowTime);
            if (waitTime > 0) try {
                Thread.sleep(waitTime);
            } catch (Exception e){}
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

    //判断单击点是否有角色
/*    public static Actor getActor(int xTile, int yTile) {
        for (Actor actor : actors)
            if (actor.getXyTile()[0] == xTile && actor.getXyTile()[1] == yTile)
                return actor;
        return null;
    }*/

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
/*        switch (GAME_CASE){
            case Values.CASE_SHOW_ACTOR_INFO:
                if (e2.getX()-e1.getX()>200)
                    actorInfoPanel.TurnLeft();
                else if (e2.getX()-e1.getX()<-200)
                    actorInfoPanel.TurnRight();
                break;
        }*/
        return true;
    }
}