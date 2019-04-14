package io.ucia0xff.fe.anim;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;

import io.ucia0xff.fe.Values;

public class CursorAnim extends Anim {
    /**
     * 构造方法
     * @param resId 所有动画帧的资源ID
     * @param isLoop 是否循环播放
     */
    public CursorAnim(int[] resId, boolean isLoop) {
        super(resId, isLoop);
        src = new Rect(0, 0, getFrame().getWidth(), getFrame().getHeight());
        dst = Values.MAP_ANIM_SIZE_32x32;
    }

    /**
     * 构造方法
     * @param frames 所有动画帧
     * @param isLoop 是否循环播放
     */
    public CursorAnim(Bitmap[] frames, boolean isLoop) {
        super(frames, isLoop);
        src = new Rect(0, 0, getFrame().getWidth(), getFrame().getHeight());
        dst = Values.MAP_ANIM_SIZE_32x32;
    }

    /**
     * 在指定像素坐标绘制帧动画
     * @param canvas
     * @param paint
     * @param xyInScrPx 屏幕上要绘制的区域的左上角
     */
    @Override
    public void drawAnim(Canvas canvas, Paint paint, int[] xyInScrPx) {
        dst.offsetTo(xyInScrPx[0] - Values.MAP_TILE_WIDTH / 2, xyInScrPx[1] - Values.MAP_TILE_HEIGHT);
        drawFrames(canvas, paint);
    }

    /**
     * 在指定格子坐标绘制帧动画
     * @param canvas
     * @param paint
     * @param xyInMapTile 地图上要绘制的格子
     * @param xyOffset 游戏地图左上角相对屏幕左上角的偏移（光标是在地图上的，跟随地图移动）
     */
    @Override
    public void drawAnim(Canvas canvas, Paint paint, int[] xyInMapTile, int[] xyOffset) {
        dst.offsetTo(xyInMapTile[0] * Values.MAP_TILE_WIDTH - Values.MAP_TILE_WIDTH / 2 + xyOffset[0], xyInMapTile[1] * Values.MAP_TILE_HEIGHT - Values.MAP_TILE_HEIGHT / 2+ xyOffset[1]);
        drawFrames(canvas, paint);
    }


    @Override
    public void drawAnim(Canvas canvas, Paint paint, Rect dst) {}
    @Override
    public void drawFrame(Canvas canvas, Paint paint, int[] xyPos, int frameID) {}
}
