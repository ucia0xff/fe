package io.ucia0xff.fe.anim;


import java.io.InputStream;
import java.util.Arrays;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;

import io.ucia0xff.fe.Values;

public abstract class Anim {
    //上一帧播放时间
    public long lastTime = 0;

    //当前播放的帧
    public int nowFrame = 0;

    //动画的帧数
    public int frameCount = 0;

    //动画的每一帧
    public Bitmap[] frames = null;

    //是否循环播放
    public boolean isLoop = false;

    //是否播放结束
    public boolean isEnd = false;

    //每一帧的持续时间
    public int durations[];

    //默认每一帧的持续时间
    public static final int defaultDuration = 100;

    //一帧中要绘制的区域
    public Rect src;

    //屏幕上绘制一帧的区域
    public Rect dst;

    //设置动画播放间隙时间
    public void setDurations(int duration) {
        Arrays.fill(durations, duration);
    }
    public void setDurations(int index, int duration) {
        durations[index] = duration;
    }
    public void setDurations(int[] durations){
        this.durations = durations;
    }

    //获取一帧
    public Bitmap getFrame(){
        return frames[0];
    }
    public Bitmap getFrame(int frameId){
        return frames[frameId];
    }

    // 构造方法
    public Anim(){}
    public Anim(int[] resId, boolean isLoop) {
        frameCount = resId.length;
        frames = new Bitmap[frameCount];
        durations = new int[frameCount];
        setDurations(defaultDuration);
        for (int i = 0; i < frameCount; i++) {
            frames[i] = readBitMap(resId[i]);
        }
        this.isLoop = isLoop;
    }
    public Anim(Bitmap[] frames, boolean isLoop) {
        frameCount = frames.length;
        durations = new int[frameCount];
        setDurations(defaultDuration);
        this.frames = frames;
        this.isLoop = isLoop;
    }


    //在屏幕像素坐标绘制动画
    public abstract void drawAnim(Canvas canvas, Paint paint, int[] xyInScrPx);
    //在地图格子坐标绘制动画
    public abstract void drawAnim(Canvas canvas, Paint paint, int[] xyInMapTile, int[] xyOffset);
    //在指定区域绘制动画
    public abstract void drawAnim(Canvas canvas, Paint paint, Rect dst);
    //在指定像素坐标绘制一帧
    public abstract void drawFrame(Canvas canvas, Paint paint, int[] xyPos, int frameId);

    /**
     * 将所有帧绘制到屏幕指定区域
     * @param canvas
     * @param paint
     */
    protected void drawFrames(Canvas canvas, Paint paint) {
        if (!isEnd && canvas!=null) {
            canvas.drawBitmap(frames[nowFrame], src, dst, paint);
            long time = System.currentTimeMillis();
            if (time - lastTime > durations[nowFrame]) {
                nowFrame++;
                lastTime = time;
                if (nowFrame >= frameCount) {
                    //标志动画播放结束
                    isEnd = true;
                    if (isLoop) {
                        //设置循环播放
                        isEnd = false;
                        nowFrame = 0;
                    }
                }
            }
        }
    }

    /**
     * 工具方法：从res读取图片
     * @param resId 资源ID
     * @return
     */
    public static Bitmap readBitMap(int resId) {
        try {
            BitmapFactory.Options opt = new BitmapFactory.Options();
            opt.inPreferredConfig = Bitmap.Config.ARGB_8888;//色彩模式
            InputStream is = Values.CONTEXT.getResources().openRawResource(resId);
            Bitmap bitmap = BitmapFactory.decodeStream(is, null, opt);
            is.close();
            return bitmap;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 工具方法：从assets读取图片
     * @param fileName assets路径下的的文件名，如faces/Unknown.png
     * @return
     */
    public static Bitmap readBitMap(String fileName) {
        try {
            BitmapFactory.Options opt = new BitmapFactory.Options();
            opt.inPreferredConfig = Bitmap.Config.ARGB_8888;
            InputStream is = Values.CONTEXT.getAssets().open(fileName);
            Bitmap bitmap = BitmapFactory.decodeStream(is, null, opt);
            is.close();
            return bitmap;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 工具方法：从一张完整图片中切割出一组图片
     * @param src 源图片
     * @param start 切割起点
     * @param count 需要切割的数量
     * @param height 单张高度
     * @return
     */
    public static Bitmap[] splitBitmap(Bitmap src, int start, int count, int height) {
        Bitmap[] split = new Bitmap[count];
        for (int i = 0; i < count; i++)
            split[i] = Bitmap.createBitmap(src, 0, (start - 1 + i) * height, src.getWidth(), height);
        return split;
    }

    /**
     * 工具方法：将图片左右翻转
     * @param src 源图片
     * @return 左右翻转的图片
     */
    public static Bitmap[] toMirrorBitmap(Bitmap[] src) {
        Bitmap[] mirrors = new Bitmap[src.length];
        for (int i = 0; i < src.length; i++)
            mirrors[i] = toMirrorBitmap(src[i]);
        return mirrors;
    }
    public static Bitmap toMirrorBitmap(Bitmap src){
        Matrix m = new Matrix();
        m.preScale(-1, 1);
        return Bitmap.createBitmap(src, 0, 0, src.getWidth(), src.getHeight(), m, false);
    }

    /**
     * 工具方法：转换为灰度图片
     * @param src 源图片
     * @return 灰度图片
     */
    public static Bitmap[] toGreyBitmap(Bitmap[] src) {
        Bitmap[] grey = new Bitmap[src.length];
        for (int i=0;i<src.length;i++)
            grey[i] = toGreyBitmap(src[i]);
        return grey;
    }
    public static Bitmap toGreyBitmap(Bitmap src) {
        int width = src.getWidth(); // 获取位图的宽
        int height = src.getHeight(); // 获取位图的高
        int[] pixels = new int[width * height]; // 通过位图的大小创建像素点数组

        src.getPixels(pixels, 0, width, 0, 0, width, height);
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                int argb = pixels[width * i + j];
                //分离透明度和三原色
                int alpha = argb & 0xFF000000;
                int red = ((argb & 0x00FF0000) >> 16);
                int green = ((argb & 0x0000FF00) >> 8);
                int blue = (argb & 0x000000FF);
                //转化成灰度像素
                int grey = (int) (red * 0.3 + green * 0.59 + blue * 0.11);
                grey = alpha | (grey << 16) | (grey << 8) | grey;
                pixels[width * i + j] = grey;
            }
        }
        //新建图片
        Bitmap newBmp = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        //设置图片数据
        newBmp.setPixels(pixels, 0, width, 0, 0, width, height);
        return newBmp;
    }

}
