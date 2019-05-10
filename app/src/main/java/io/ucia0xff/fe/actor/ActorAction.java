package io.ucia0xff.fe.actor;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;

import io.ucia0xff.fe.Paints;
import io.ucia0xff.fe.R;
import io.ucia0xff.fe.Values;
import io.ucia0xff.fe.anim.Anim;
import io.ucia0xff.fe.career.Career;
import io.ucia0xff.fe.game.GameView;
import io.ucia0xff.fe.item.Item;
import io.ucia0xff.fe.map.Map;

public class ActorAction {
    public static final String[] ACTIONS = {"攻击", "物品", "待机"};
    public static final boolean[] ACTIONS_ENABLE = {false, false, true};
    public static final int ACTION_ATTACK = 0;
    public static final int ACTION_ITEMS = 1;
    public static final int ACTION_STANDBY = 2;


    private ActorMove move;
    private Actor actor;
    private Actor targetActor;
    private Career career;
    private Item equipedWeapon;

    //背景图片
    private Bitmap bg;

    public ActorAction(Map map) {
        move = new ActorMove(map);
        bg = Anim.readBitMap(R.drawable.bg_actor_action);
    }

    //检查所有行动指令
    public void prepare() {
        ACTIONS_ENABLE[0] = prepareAttack();
        ACTIONS_ENABLE[1] = prepareItems();
    }

    //检查攻击指令是否可用
    public boolean prepareAttack() {
        equipedWeapon = actor.getEquipedWeapon();
        if (equipedWeapon == null)
            return false;
        ActorMove.NodeList attackRange = move.getAttackRange(actor);
        for (ActorMove.Node node : attackRange) {
            targetActor = Actors.getActor(node.getXy());
            if (targetActor != null) {//则判断该节点上有没有角色存在
                switch (actor.getParty()) {//如果有，则判断两者的阵营关系
                    case Values.PARTY_ALLY:
                    case Values.PARTY_PLAYER:
                        if (targetActor.getParty() == Values.PARTY_ENEMY)//互为敌对阵营,可以成为攻击目标
                            return true;
                        break;
                    case Values.PARTY_ENEMY:
                        if (targetActor.getParty() != Values.PARTY_ENEMY)//互为敌对阵营,可以成为攻击目标
                            return true;
                        break;
                }
            }
        }
        return false;
    }

    //检查物品指令是否可用
    public boolean prepareItems() {
        return actor.getItems().size() > 0;
    }

    public void show(Canvas canvas, Paint paint) {
        if (canvas == null) return;
        int startX;
        int startY;
        if (actor.getXyInScrPx()[0] < (Values.SCREEN_WIDTH / 2 - Values.MAP_TILE_WIDTH)) {
            startX = Values.SCREEN_WIDTH / 2 + Values.MAP_TILE_WIDTH;
            startY = Values.SCREEN_HEIGHT / 2;
        } else {
            startX = Values.SCREEN_WIDTH / 2 - Values.MAP_TILE_WIDTH - bg.getWidth();
            startY = Values.SCREEN_HEIGHT / 2;
        }
        canvas.drawBitmap(bg, startX, startY, paint);
        for (int i = 0; i < ACTIONS.length; i++) {
            canvas.drawBitmap(bg, startX, startY + i * bg.getHeight(), paint);
            canvas.drawText(ACTIONS[i], startX + bg.getWidth() / 2, startY + i * bg.getHeight() + bg.getHeight() / 3 * 2,
                    ACTIONS_ENABLE[i] ? Paints.paints.get("actor_action_enable") : Paints.paints.get("actor_action_disable"));
        }
    }

    public Actor getActor() {
        return actor;
    }

    public void setActor(Actor actor) {
        this.actor = actor;
        this.prepare();
    }

    public Career getCareer() {
        return career;
    }

    public void setCareer(Career career) {
        this.career = career;
    }

    public Item getEquipedWeapon() {
        return equipedWeapon;
    }

    public void setEquipedWeapon(Item equipedWeapon) {
        this.equipedWeapon = equipedWeapon;
    }

    public Bitmap getBg() {
        return bg;
    }

    public void setBg(Bitmap bg) {
        this.bg = bg;
    }
}
