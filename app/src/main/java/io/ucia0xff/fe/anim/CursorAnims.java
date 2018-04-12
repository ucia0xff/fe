package io.ucia0xff.fe.anim;

import android.graphics.Bitmap;

import java.util.HashMap;
import java.util.Map;

import io.ucia0xff.fe.R;
import io.ucia0xff.fe.Values;

public class CursorAnims {
    public static Bitmap bitmap;
    public static Bitmap[] bitmaps;
    public static CursorAnim cursorAnim;
    public static Map<String, CursorAnim> cursorAnims = new HashMap<String, CursorAnim>();

    static {
        bitmap = Anim.readBitMap(R.drawable.cursor);
        bitmaps = Anim.splitBitmap(bitmap, 1, 2, 32);
        cursorAnim = new CursorAnim(bitmaps, true);
        cursorAnim.setDurations(new int[]{200, 300});
        cursorAnims.put(Values.CURSOR_DYNAMIC, cursorAnim);

        bitmaps = Anim.splitBitmap(bitmap, 3, 1, 32);
        cursorAnim = new CursorAnim(bitmaps, true);
        cursorAnims.put(Values.CURSOR_STATIC, cursorAnim);

        bitmaps = Anim.splitBitmap(bitmap, 4, 1, 32);
        cursorAnim = new CursorAnim(bitmaps, true);
        cursorAnims.put(Values.CURSOR_ALLOWED, cursorAnim);

        bitmaps = Anim.splitBitmap(bitmap, 5, 1, 32);
        cursorAnim = new CursorAnim(bitmaps, true);
        cursorAnims.put(Values.CURSOR_FORBIDEN, cursorAnim);
    }
}
