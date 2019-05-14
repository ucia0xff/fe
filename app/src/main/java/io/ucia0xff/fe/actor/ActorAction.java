package io.ucia0xff.fe.actor;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

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
    private Actor srcActor;
    private Career career;
    private Item equipedWeapon;

    private List<Actor> targetList;

    private int[] startXY = {0,0};//行动选项显示起始坐标
    private int actionIndex;//选择的选项

    //背景图片
    private Bitmap bg;

    public ActorAction(ActorMove move) {
        this.move = move;
        bg = Anim.readBitMap(R.drawable.bg_actor_action);
        targetList = new ArrayList<>();
    }

    //检查所有行动指令
    public void prepareActions() {
        ACTIONS_ENABLE[0] = prepareAttack();
        ACTIONS_ENABLE[1] = prepareItems();
    }

    //检查攻击指令是否可用
    public boolean prepareAttack() {
        setTargetList();
        return targetList.size() > 0;
    }

    //检查物品指令是否可用
    public boolean prepareItems() {
        return srcActor.getItems().size() > 0;
    }

    //显示行动选项
    public void show(Canvas canvas, Paint paint) {
        if (canvas == null) return;
        if (srcActor.getXyInScrPx()[0] < (Values.SCREEN_WIDTH / 2 - Values.MAP_TILE_WIDTH)) {
            startXY[0] = Values.SCREEN_WIDTH / 2 + Values.MAP_TILE_WIDTH;
            startXY[1] = Values.SCREEN_HEIGHT / 2;
        } else {
            startXY[0] = Values.SCREEN_WIDTH / 2 - Values.MAP_TILE_WIDTH - bg.getWidth();
            startXY[1] = Values.SCREEN_HEIGHT / 2;
        }
        canvas.drawBitmap(bg, startXY[0], startXY[1], paint);
        for (int i = 0; i < ACTIONS.length; i++) {
            canvas.drawBitmap(bg, startXY[0], startXY[1] + i * bg.getHeight(), paint);
            canvas.drawText(ACTIONS[i], startXY[0] + bg.getWidth() / 2, startXY[1] + i * bg.getHeight() + bg.getHeight() / 3 * 2,
                    ACTIONS_ENABLE[i] ? Paints.paints.get("actor_action_enable") : Paints.paints.get("actor_action_disable"));
        }
    }

    //检查选择的选项
    public boolean checkAction(int[] xy) {
        if (startXY[0] + bg.getWidth() < xy[0] || xy[0] < startXY[0])//超出选项左右边界
            return false;
        actionIndex = -1;
        for (int i = 0; i < ACTIONS.length; i++) {
            if (startXY[1] + i * bg.getHeight() < xy[1] && xy[1] < startXY[1] + i * bg.getHeight() + bg.getHeight())
                actionIndex = i;
        }
        switch (actionIndex) {
            case ACTION_ATTACK:
                if (!ACTIONS_ENABLE[ACTION_ATTACK])//攻击选项不可用
                    return false;
                break;
            case ACTION_ITEMS:
                if (!ACTIONS_ENABLE[ACTION_ITEMS])//物品选项不可用
                    return false;
                break;
            case ACTION_STANDBY:
                if (!ACTIONS_ENABLE[ACTION_STANDBY])//待机选项不可用
                    return false;
                break;
            default:
                return false;
        }
        return true;
    }

    public Actor getTargetActor(int[] xyTile) {
        setTargetList();
        for (int i=0;i<targetList.size();i++) {
            Actor target = targetList.get(i);
            if ((target.getXyInMapTile()[0]==xyTile[0]) && (target.getXyInMapTile()[1] == xyTile[1])) {
                return target;
            }
        }
        return null;
    }

    public Actor getTargetActor(int index) {
        return targetList.get(index);
    }

    public List<Actor> getTargetList() {
        setTargetList();
        return targetList;
    }

    public void setTargetList() {
        targetList.clear();
        equipedWeapon = srcActor.getEquipedWeapon();
        if (equipedWeapon == null)
            return;
        ActorMove.NodeList attackRange = move.getAttackRange(srcActor);
        for (ActorMove.Node node : attackRange) {
            Actor actor = Actors.getActor(node.getXy());
            if (actor != null) {//则判断该节点上有没有角色存在
                switch (srcActor.getParty()) {//如果有，则判断两者的阵营关系
                    case Values.PARTY_ALLY:
                    case Values.PARTY_PLAYER:
                        if (actor.getParty() == Values.PARTY_ENEMY)//互为敌对阵营,可以成为攻击目标
                            targetList.add(actor);
                        break;
                    case Values.PARTY_ENEMY:
                        if (actor.getParty() != Values.PARTY_ENEMY)//互为敌对阵营,可以成为攻击目标
                            targetList.add(actor);
                        break;
                }
            }
        }
        for (Actor actor : targetList) {
            Log.d("TargetList", actor.getXyInMapTile()[0] + "，" + actor.getXyInMapTile()[1] + "：" + actor.getName());
        }
    }


    //显示战斗信息
    public void showBattleInfo(Canvas canvas, Paint paint, Actor actor1, Actor actor2) {
        if (actor1==null || actor2==null) return;
        paint = Paints.paints.get("battle_info_center");
        int startY = (int)(paint.getFontMetrics().bottom - paint.getFontMetrics().top);
        int rowStep = startY;
        int margin = (int) paint.measureText("对战");
        canvas.drawText("|对战|", Values.SCREEN_WIDTH/2, startY, Paints.paints.get("battle_info_center"));
        canvas.drawText(actor1.getName(), Values.SCREEN_WIDTH/2 - margin, startY, Paints.paints.get("battle_info_right"));
        canvas.drawText(actor2.getName(), Values.SCREEN_WIDTH/2 + margin, startY, Paints.paints.get("battle_info_left"));

        int value1;
        int value2;
        if (actor1.getEquipedWeapon().getDmgType()==Values.DAMAGE_TYPE_PHYSICS)
            value1 = actor1.getAtk() - actor2.getDef();
        else
            value1 = actor1.getMat() - actor2.getRes();
        value1 = value1<0?0:value1;
        if (actor2.getEquipedWeapon().getDmgType()==Values.DAMAGE_TYPE_PHYSICS)
            value2 = actor2.getAtk() - actor1.getDef();
        else
            value2 = actor2.getMat() - actor1.getRes();
        value2 = value2<0?0:value2;
        startY+=rowStep;
        canvas.drawText("|伤害|", Values.SCREEN_WIDTH/2, startY, Paints.paints.get("battle_info_center"));
        canvas.drawText(""+value1, Values.SCREEN_WIDTH/2 - margin, startY, Paints.paints.get("battle_info_right"));
        canvas.drawText(""+value2, Values.SCREEN_WIDTH/2 + margin, startY, Paints.paints.get("battle_info_left"));

        value1=actor1.getHit() - actor2.getAvd();
        value2=actor2.getHit() - actor1.getAvd();
        startY+=rowStep;
        canvas.drawText("|命中|", Values.SCREEN_WIDTH/2, startY, Paints.paints.get("battle_info_center"));
        canvas.drawText(""+value1, Values.SCREEN_WIDTH/2 - margin, startY, Paints.paints.get("battle_info_right"));
        canvas.drawText(""+value2, Values.SCREEN_WIDTH/2 + margin, startY, Paints.paints.get("battle_info_left"));

        value1=actor1.getCrt() - actor2.getLuc();
        value2=actor2.getCrt() - actor1.getLuc();
        startY+=rowStep;
        canvas.drawText("|必杀|", Values.SCREEN_WIDTH/2, startY, Paints.paints.get("battle_info_center"));
        canvas.drawText(""+value1, Values.SCREEN_WIDTH/2 - margin, startY, Paints.paints.get("battle_info_right"));
        canvas.drawText(""+value2, Values.SCREEN_WIDTH/2 + margin, startY, Paints.paints.get("battle_info_left"));
    }

    public Actor getSrcActor() {
        return srcActor;
    }

    public void setSrcActor(Actor actor) {
        this.srcActor = actor;
        this.prepareActions();
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
