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
        //光标动画-动态
        bitmap = Anim.readBitmap(R.drawable.cursor);
        bitmaps = Anim.splitBitmap(bitmap, 1, 2, 32);
        cursorAnim = new CursorAnim(bitmaps);
        cursorAnim.setDurations(new int[]{200, 300});
        cursorAnims.put(Values.CURSOR_DYNAMIC, cursorAnim);

        //光标动画-静态
        bitmaps = Anim.splitBitmap(bitmap, 3, 1, 32);
        cursorAnim = new CursorAnim(bitmaps);
        cursorAnims.put(Values.CURSOR_STATIC, cursorAnim);
    }
}
