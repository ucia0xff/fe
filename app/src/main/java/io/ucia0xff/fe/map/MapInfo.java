package io.ucia0xff.fe.map;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;

import io.ucia0xff.fe.Paints;
import io.ucia0xff.fe.R;
import io.ucia0xff.fe.Values;
import io.ucia0xff.fe.anim.Anim;
import io.ucia0xff.fe.game.GameView;

public class MapInfo {
    //背景图片
    private Bitmap bg;
    private Rect src;
    private Rect dst;

    //地形信息
    private int[][] terrain;
    private String terrainName = "";
    private int[] terrainEffect;
    private String effectText = "";
    private int strLength;

    //构造方法
    public MapInfo(Map map){
        bg = Anim.readBitMap(R.drawable.bg_terrain_info);
        src = new Rect(0, 0, bg.getWidth(), bg.getHeight());
        dst = new Rect(0, 0, 3*Values.MAP_TILE_WIDTH, 3*Values.MAP_TILE_WIDTH/bg.getWidth() *bg.getHeight());
        setTerrain(map.getTerrain());
    }

    public void setTerrain(int[][] terrain) {
        this.terrain = terrain;
    }

    public int getTerrain(int[] xy){
        return terrain[xy[1]][xy[0]];
    }

    /**
     * 显示地形信息
     * @param canvas
     * @param paint
     * @param isLeft 光标所在的格子是否在屏幕左边
     */
    public void show(Canvas canvas, Paint paint, Boolean isLeft) {
        terrainName = Terrain.TERRAIN_NAME[getTerrain(GameView.cursorXY)];
        terrainEffect = Terrain.TERRAIN_EFFECT[getTerrain(GameView.cursorXY)];
        if (canvas==null) return;
        if (isLeft) //光标在屏幕左边，则在屏幕右边显示地形信息
            dst.offsetTo(Values.SCREEN_WIDTH - dst.width(), Values.SCREEN_HEIGHT - dst.height());
        else //光标在屏幕右边，则在屏幕左边显示地形信息
            dst.offsetTo(0, Values.SCREEN_HEIGHT - dst.height());
        draw(canvas, paint);
    }

    /**
     * 绘制背景和地形信息
     * @param canvas
     * @param paint
     */
    private void draw(Canvas canvas, Paint paint) {
        //画背景
        canvas.drawBitmap(bg, src, dst, paint);

        //地形名
        canvas.drawText(terrainName, dst.left + dst.width() / 2, dst.top + 95, Paints.paints.get("terrain_name"));

        //回避附加
        effectText = "回避." + (terrainEffect[0]>9 ? terrainEffect[0] : " "+ terrainEffect[0]);
        canvas.drawText(effectText, dst.left + dst.width() / 2, dst.top + 150, Paints.paints.get("terrain_effect"));

        //守备附加
        effectText = "守备." + (terrainEffect[1]>9 ? terrainEffect[1]: " "+ terrainEffect[1]);
        canvas.drawText(effectText, dst.left + dst.width() / 2, dst.top + 190, Paints.paints.get("terrain_effect"));

        //魔防附加
        effectText = "魔防." + (terrainEffect[2]>9 ? terrainEffect[2]: " "+ terrainEffect[2]);
        canvas.drawText(effectText, dst.left + dst.width() / 2, dst.top + 230, Paints.paints.get("terrain_effect"));

        //恢复百分比
        effectText = "恢复." + (terrainEffect[3]>9 ? terrainEffect[3]: " "+ terrainEffect[3]);
        canvas.drawText(effectText, dst.left + dst.width() / 2, dst.top + 270, Paints.paints.get("terrain_effect"));
    }

}
