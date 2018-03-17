package io.ucia0xff.fe.anim;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;

import io.ucia0xff.fe.Values;

public class ActorAnim extends Anim {
    //动画帧的尺寸
    private int size;

    //构造方法
    public ActorAnim(int[] resId, boolean isLoop) {
        super(resId, isLoop);
        init();
    }
    public ActorAnim(Bitmap[] frames, boolean isLoop) {
        super(frames, isLoop);
        init();
    }

    public void init() {
        int frameWidth = getFrame().getWidth();
        int frameHeight = getFrame().getHeight();
        src = new Rect(0, 0, frameWidth, frameHeight);
        if (frameWidth == Values.RES_ANIM_SIZE_16){
            if (frameHeight == Values.RES_ANIM_SIZE_16) {
                size = Values.RES_ANIM_SIZE_16x16;
                dst = Values.MAP_ANIM_SIZE_16x16;
            } else {
                size = Values.RES_ANIM_SIZE_16x32;
                dst = Values.MAP_ANIM_SIZE_16x32;
            }
        } else {
            size = Values.RES_ANIM_SIZE_32x32;
            dst = Values.MAP_ANIM_SIZE_32x32;
        }
    }

    /**
     * 在指定像素坐标绘制帧动画
     * @param canvas
     * @param paint
     * @param xyPos 屏幕上要绘制的区域的左上角
     */
    @Override
    public void drawAnim(Canvas canvas, Paint paint, int xyPos[]) {
        switch (size){
            case Values.RES_ANIM_SIZE_16x16:
                dst.offsetTo(xyPos[0], xyPos[1]);
                break;
            case Values.RES_ANIM_SIZE_16x32:
                dst.offsetTo(xyPos[0], xyPos[1] - Values.MAP_TILE_HEIGHT);
                break;
            case Values.RES_ANIM_SIZE_32x32:
                dst.offsetTo(xyPos[0] - Values.MAP_TILE_WIDTH / 2, xyPos[1] - Values.MAP_TILE_HEIGHT);
                break;
        }

        drawFrames(canvas, paint);
    }

    /**
     * 在指定格子坐标绘制帧动画
     * @param canvas
     * @param paint
     * @param xyTile 地图上要绘制的格子
     * @param xyOffset 游戏地图左上角相对屏幕左上角的偏移
     */
    @Override
    public void drawAnim(Canvas canvas, Paint paint, int[] xyTile, int[] xyOffset) {
        switch (size) {
            case Values.RES_ANIM_SIZE_16x16:
                dst.offsetTo(xyTile[0] * Values.MAP_TILE_WIDTH + xyOffset[0], xyTile[1] * Values.MAP_TILE_HEIGHT + xyOffset[1]);
                break;
            case Values.RES_ANIM_SIZE_16x32:
                dst.offsetTo(xyTile[0] * Values.MAP_TILE_WIDTH + xyOffset[0], xyTile[1] * Values.MAP_TILE_HEIGHT - Values.MAP_TILE_HEIGHT + xyOffset[1]);
                break;
            case Values.RES_ANIM_SIZE_32x32:
                dst.offsetTo(xyTile[0] * Values.MAP_TILE_WIDTH - Values.MAP_TILE_WIDTH / 2 + xyOffset[0], xyTile[1] * Values.MAP_TILE_HEIGHT - Values.MAP_TILE_HEIGHT + xyOffset[1]);
                break;
        }

        drawFrames(canvas, paint);
    }

    @Override
    public void drawFrame(Canvas canvas, Paint paint, int[] xyPos, int frameID) {}
    @Override
    public void drawAnim(Canvas canvas, Paint paint, Rect dst) {}
}
