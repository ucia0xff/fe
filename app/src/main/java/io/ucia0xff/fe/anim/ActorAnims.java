package io.ucia0xff.fe.anim;

import android.graphics.Bitmap;

import java.util.HashMap;
import java.util.Map;

public class ActorAnims {
    public static Bitmap bitmap;
    public static Bitmap[] bitmaps;
//    public static ActorAnim actorAnim = null;
    public static Map<String, ActorAnim> actorAnims = new HashMap<String, ActorAnim>();

    static {
        bitmap = Anim.readBitMap("actor_anim/SisterNatashaPlayerStatic.png");
        bitmaps = Anim.splitBitmap(bitmap, 1, 3, 16);
        ActorAnim actorAnim = new ActorAnim(bitmaps, true);
        actorAnim.setDurations(400);
        actorAnims.put("SisterNatashaPlayerStatic", actorAnim);
    }
}
