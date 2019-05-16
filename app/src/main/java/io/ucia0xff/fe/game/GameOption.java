package io.ucia0xff.fe.game;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;

import io.ucia0xff.fe.Paints;
import io.ucia0xff.fe.R;
import io.ucia0xff.fe.Values;
import io.ucia0xff.fe.anim.Anim;

public class GameOption {
    public static final String[] OPTIONS = {"结束", "部队", "设定", "保存"};
    public static final boolean[] OPTIONS_ENABLE = {true, true, true, true};

    public static final int OPTIONS_FAILED = -1;
    public static final int OPTIONS_OVER = 0;
    public static final int OPTIONS_UNITS = 1;
    public static final int OPTIONS_SETTING = 2;
    public static final int OPTIONS_SAVE = 3;

    private int[] startXY = {0,0};//游戏选项显示起始坐标
    private int optionIndex;//选择的选项
    //背景图片
    private Bitmap bg;

    public GameOption() {
        bg = Anim.readBitMap(R.drawable.bg_actor_action);
    }

    //显示行动选项
    public void show(Canvas canvas, Paint paint, boolean isLeft) {
        if (canvas == null) return;
        if (isLeft) {
            startXY[0] = Values.SCREEN_WIDTH / 2 + Values.MAP_TILE_WIDTH;
            startXY[1] = Values.SCREEN_HEIGHT / 2;
        } else {
            startXY[0] = Values.SCREEN_WIDTH / 2 - Values.MAP_TILE_WIDTH - bg.getWidth();
            startXY[1] = Values.SCREEN_HEIGHT / 2;
        }
        canvas.drawBitmap(bg, startXY[0], startXY[1], paint);
        for (int i = 0; i < OPTIONS.length; i++) {
            canvas.drawBitmap(bg, startXY[0], startXY[1] + i * bg.getHeight(), paint);
            canvas.drawText(OPTIONS[i], startXY[0] + bg.getWidth() / 2, startXY[1] + i * bg.getHeight() + bg.getHeight() / 3 * 2,
                    OPTIONS_ENABLE[i] ? Paints.paints.get("actor_action_enable") : Paints.paints.get("actor_action_disable"));
        }
    }

    //检查选择的选项
    public int checkAction(int[] xy) {
        if (startXY[0] + bg.getWidth() < xy[0] || xy[0] < startXY[0])//超出选项左右边界
            return OPTIONS_FAILED;
        optionIndex = -1;
        for (int i = 0; i < OPTIONS.length; i++) {
            if (startXY[1] + i * bg.getHeight() < xy[1] && xy[1] < startXY[1] + i * bg.getHeight() + bg.getHeight())
                optionIndex = i;
        }
        switch (optionIndex) {
            case OPTIONS_OVER:
                if (OPTIONS_ENABLE[OPTIONS_OVER])//结束选项可用
                    return OPTIONS_OVER;
                break;
            case OPTIONS_UNITS:
                if (OPTIONS_ENABLE[OPTIONS_UNITS])//部队选项可用
                    return OPTIONS_UNITS;
                break;
            case OPTIONS_SETTING:
                if (OPTIONS_ENABLE[OPTIONS_SETTING])//设定选项可用
                    return OPTIONS_SETTING;
                break;
            case OPTIONS_SAVE:
                if (OPTIONS_ENABLE[OPTIONS_SAVE])//保存选项可用
                    return OPTIONS_SAVE;
                break;
            default:
                return OPTIONS_FAILED;
        }
        return OPTIONS_FAILED;
    }
}
