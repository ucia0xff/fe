package io.ucia0xff.fe.anim;

import android.graphics.Bitmap;

import java.util.HashMap;
import java.util.Map;

public class ActorAnims {
    public static Bitmap bitmap;
    public static Bitmap[] bitmaps;
    public static ActorAnim actorAnim;
    public static Map<String, ActorAnim> actorAnims = new HashMap<String, ActorAnim>();

    static {
        {//修女娜塔莎
            //修女娜塔莎静态图标
            bitmap = Anim.readBitMap("actor_anim/SisterNatashaPlayerStatic.png");
            bitmaps = Anim.splitBitmap(bitmap, 1, 3, 16);
            actorAnim = new ActorAnim(bitmaps, true);
            actorAnim.setDurations(400);
            actorAnims.put("SisterNatashaPlayerStatic", actorAnim);

            //修女娜塔莎待机图标
            bitmaps = Anim.toGreyBitmap(bitmaps);//静态图标帧转换成灰度得到待机图标帧
            actorAnim = new ActorAnim(bitmaps, true);
            actorAnim.setDurations(400);
            actorAnims.put("SisterNatashaPlayerStandby", actorAnim);

            //修女娜塔莎动态图标
            bitmap = Anim.readBitMap("actor_anim/SisterNatashaPlayerDynamic.png");
            bitmaps = Anim.splitBitmap(bitmap, 13, 3, 32);
            actorAnim = new ActorAnim(bitmaps, true);
            actorAnim.setDurations(200);
            actorAnims.put("SisterNatashaPlayerDynamic", actorAnim);

            //修女娜塔莎下移动画
            bitmaps = Anim.splitBitmap(bitmap, 5, 4, 32);
            actorAnim = new ActorAnim(bitmaps, true);
            actorAnim.setDurations(100);
            actorAnims.put("SisterNatashaPlayerDown", actorAnim);

            //修女娜塔莎左移动画
            bitmaps = Anim.splitBitmap(bitmap, 1, 4, 32);
            actorAnim = new ActorAnim(bitmaps, true);
            actorAnim.setDurations(100);
            actorAnims.put("SisterNatashaPlayerLeft", actorAnim);

            //修女娜塔莎右移动画
            bitmaps = Anim.toMirrorBitmap(bitmaps);//左移动画帧左右翻转得到右移动画帧
            actorAnim = new ActorAnim(bitmaps, true);
            actorAnim.setDurations(100);
            actorAnims.put("SisterNatashaPlayerRight", actorAnim);

            //修女娜塔莎上移动画
            bitmaps = Anim.splitBitmap(bitmap, 9, 4, 32);
            actorAnim = new ActorAnim(bitmaps, true);
            actorAnim.setDurations(100);
            actorAnims.put("SisterNatashaPlayerUp", actorAnim);
        }

        {//魔刹犬
            //魔刹犬静态图标
            bitmap = Anim.readBitMap("actor_anim/MautheDogPlayerStatic.png");
            bitmaps = Anim.splitBitmap(bitmap, 1, 3, 32);
            actorAnim = new ActorAnim(bitmaps, true);
            actorAnim.setDurations(400);
            actorAnims.put("MautheDogPlayerStatic", actorAnim);

            //魔刹犬待机图标
            bitmaps = Anim.toGreyBitmap(bitmaps);//静态图标帧转换成灰度得到待机图标帧
            actorAnim = new ActorAnim(bitmaps, true);
            actorAnim.setDurations(400);
            actorAnims.put("MautheDogPlayerStandby", actorAnim);

            //魔刹犬动态图标
            bitmap = Anim.readBitMap("actor_anim/MautheDogPlayerDynamic.png");
            bitmaps = Anim.splitBitmap(bitmap, 13, 3, 32);
            actorAnim = new ActorAnim(bitmaps, true);
            actorAnim.setDurations(200);
            actorAnims.put("MautheDogPlayerDynamic", actorAnim);

            //魔刹犬下移动画
            bitmaps = Anim.splitBitmap(bitmap, 5, 4, 32);
            actorAnim = new ActorAnim(bitmaps, true);
            actorAnim.setDurations(100);
            actorAnims.put("MautheDogPlayerDown", actorAnim);

            //魔刹犬左移动画
            bitmaps = Anim.splitBitmap(bitmap, 1, 4, 32);
            actorAnim = new ActorAnim(bitmaps, true);
            actorAnim.setDurations(100);
            actorAnims.put("MautheDogPlayerLeft", actorAnim);

            //魔刹犬右移动画
            bitmaps = Anim.toMirrorBitmap(bitmaps);//左移动画帧左右翻转得到右移动画帧
            actorAnim = new ActorAnim(bitmaps, true);
            actorAnim.setDurations(100);
            actorAnims.put("MautheDogPlayerRight", actorAnim);

            //魔刹犬上移动画
            bitmaps = Anim.splitBitmap(bitmap, 9, 4, 32);
            actorAnim = new ActorAnim(bitmaps, true);
            actorAnim.setDurations(100);
            actorAnims.put("MautheDogPlayerUp", actorAnim);
        }

        {//翼龙骑士
            //翼龙骑士静态图标
            bitmap = Anim.readBitMap("actor_anim/WyvernKnightEnemyStatic.png");
            bitmaps = Anim.splitBitmap(bitmap, 1, 3, 32);
            actorAnim = new ActorAnim(bitmaps, true);
            actorAnim.setDurations(400);
            actorAnims.put("WyvernKnightEnemyStatic", actorAnim);

            //翼龙骑士待机图标
            bitmaps = Anim.toGreyBitmap(bitmaps);//静态图标帧转换成灰度得到待机图标帧
            actorAnim = new ActorAnim(bitmaps, true);
            actorAnim.setDurations(400);
            actorAnims.put("WyvernKnightEnemyStandby", actorAnim);

            //翼龙骑士动态图标
            bitmap = Anim.readBitMap("actor_anim/WyvernKnightEnemyDynamic.png");
            bitmaps = Anim.splitBitmap(bitmap, 13, 3, 32);
            actorAnim = new ActorAnim(bitmaps, true);
            actorAnim.setDurations(200);
            actorAnims.put("WyvernKnightEnemyDynamic", actorAnim);

            //翼龙骑士下移动画
            bitmaps = Anim.splitBitmap(bitmap, 5, 4, 32);
            actorAnim = new ActorAnim(bitmaps, true);
            actorAnim.setDurations(100);
            actorAnims.put("WyvernKnightEnemyDown", actorAnim);

            //翼龙骑士左移动画
            bitmaps = Anim.splitBitmap(bitmap, 1, 4, 32);
            actorAnim = new ActorAnim(bitmaps, true);
            actorAnim.setDurations(100);
            actorAnims.put("WyvernKnightEnemyLeft", actorAnim);

            //翼龙骑士右移动画
            bitmaps = Anim.toMirrorBitmap(bitmaps);//左移动画帧左右翻转得到右移动画帧
            actorAnim = new ActorAnim(bitmaps, true);
            actorAnim.setDurations(100);
            actorAnims.put("WyvernKnightEnemyRight", actorAnim);

            //翼龙骑士上移动画
            bitmaps = Anim.splitBitmap(bitmap, 9, 4, 32);
            actorAnim = new ActorAnim(bitmaps, true);
            actorAnim.setDurations(100);
            actorAnims.put("WyvernKnightEnemyUp", actorAnim);
        }
    }
}
