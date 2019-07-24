package io.ucia0xff.fe.util;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;

import java.util.HashMap;
import java.util.Map;

import io.ucia0xff.fe.R;
import io.ucia0xff.fe.Values;
import io.ucia0xff.fe.actor.Actor;
import io.ucia0xff.fe.actor.Actors;
import io.ucia0xff.fe.anim.Anim;
import io.ucia0xff.fe.anim.CursorAnim;

public class Cursor {
    public static int[] xy = {0,0};
    public static int[] lastXy = {0,0};

    private Map<String, CursorAnim> cursorAnims;//光标动画
    private String nowAnim;

    public Cursor() {
        init();
        nowAnim = Values.CURSOR_DYNAMIC;
    }

    public void init() {
        cursorAnims = new HashMap<>();

        //光标动画-动态
        Bitmap bitmap = Anim.readBitmap(R.drawable.cursor);
        Bitmap[] bitmaps = Anim.splitBitmap(bitmap, 1, 2, 32);
        CursorAnim cursorAnim = new CursorAnim(bitmaps);
        cursorAnim.setDurations(new int[]{200, 300});
        cursorAnims.put(Values.CURSOR_DYNAMIC, cursorAnim);

        //光标动画-静态
        bitmaps = Anim.splitBitmap(bitmap, 3, 1, 32);
        cursorAnim = new CursorAnim(bitmaps);
        cursorAnims.put(Values.CURSOR_STATIC, cursorAnim);
    }

    public void drawCursor(Canvas canvas, Paint paint, int[] xyOffset) {
        Actor actor = Actors.getActor(xy);
        if (actor == null || actor.getParty() != Values.PARTY_PLAYER || actor.isStandby()) {
            cursorAnims.get(Values.CURSOR_DYNAMIC).drawAnim(canvas, paint, xy, xyOffset);
        } else {
            cursorAnims.get(Values.CURSOR_STATIC).drawAnim(canvas, paint, xy, xyOffset);
        }
    }

    public static int[] getXy() {
        return xy;
    }

    public static void setXy(int[] xy) {
        Cursor.xy[0] = xy[0];
        Cursor.xy[1] = xy[1];
    }

    public static int[] getLastXy() {
        return lastXy;
    }

    public static void setLastXy(int[] lastXy) {
        Cursor.lastXy[0] = lastXy[0];
        Cursor.lastXy[1] = lastXy[1];
    }

    public String getNowAnim() {
        return nowAnim;
    }

    public void setNowAnim(String nowAnim) {
        this.nowAnim = nowAnim;
    }
}
