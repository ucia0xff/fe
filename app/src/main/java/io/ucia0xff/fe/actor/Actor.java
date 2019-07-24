package io.ucia0xff.fe.actor;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.Log;
import android.util.Xml;

import org.xmlpull.v1.XmlPullParser;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Random;

import io.ucia0xff.fe.Values;
//import io.ucia0xff.fe.anim.CareerAnims;
import io.ucia0xff.fe.anim.Anim;
import io.ucia0xff.fe.career.Career;
import io.ucia0xff.fe.career.Careers;
import io.ucia0xff.fe.item.Item;
import io.ucia0xff.fe.item.Items;
import io.ucia0xff.fe.map.TerrainInfo;
import io.ucia0xff.fe.map.Terrain;
import io.ucia0xff.fe.util.RangeHelper;

public class Actor {
    //配置信息
    private int party;//阵营
    private int[] xyInMapTile;//角色在地图数组中的坐标
    private int[] xyInMapPx;//角色动画左上角在地图的像素坐标
    private int[] xyInScrPx;//角色动画左上角在屏幕的像素坐标

    //角色信息
    private String actorKey;//角色标识
    private String careerKey;//职业标识
    private String name;//角色名
    private String info;//角色说明
    private Bitmap face;//角色头像
    private int affin;//相性

    private ArrayList<Item> items;//携带的物品
    private Item equipedWeapon;//当前装备的武器

    //能力信息
    private int LV;//等级
    private int exp;//经验
    private int MHP;//最大HP
    private int HP;//当前HP
    private int str;//力量
    private int mag;//魔力
    private int skl;//技术
    private int spd;//速度
    private int luc;//幸运
    private int def;//守备
    private int res;//魔防
    private int mov;//移动
    private int con;//体格
    private int aid;//救出

    private int[] status;//状态

    //成长率
    private int growMHP;//MaxHP
    private int growStr;//力量
    private int growMag;//魔力
    private int growSkl;//技术
    private int growSpd;//速度
    private int growLuc;//幸运
    private int growDef;//守备
    private int growRes;//魔防

    //武器熟练度
    private int expSwd;
    private int expLan;
    private int expAxe;
    private int expBow;
    private int expStf;
    private int expAnm;
    private int expLgt;
    private int expDrk;

    //战斗面板
    private int atk;//物攻
    private int mat;//魔攻
    private int hit;//命中
    private int avd;//回避
    private int crt;//必杀
    private int asp;//攻速

    private String animKey;//角色动画标识
    private String nowAnim;//当前动画

    private boolean standby;//是否待机
    private boolean visible;//是否可见
    private boolean canAttack;//可否攻击
    private boolean goBack;//攻击完返回原位

    public Actor(String actorConfigFileName) {
        try {
            InputStream in = Values.CONTEXT.getAssets().open("actor_config/" + actorConfigFileName + ".xml");
            XmlPullParser parser = Xml.newPullParser();
            parser.setInput(in, "UTF-8");

            //START_TAG, END_TAG, TEXT等等的节点
            int eventType = parser.getEventType();

            //当还没有解析到结束文档的节点，一直循环
            while (eventType != XmlPullParser.END_DOCUMENT) {
                if (eventType == XmlPullParser.START_TAG) {
                    if ("party".equals(parser.getName())) {
                        party = Integer.parseInt(parser.nextText());
                    } else if ("xy".equals(parser.getName())) {
                        String[] xyText = parser.nextText().split(",");
                        xyInMapTile = new int[2];
                        xyInMapPx = new int[2];
                        xyInScrPx = new int[2];
                        xyInMapTile[0] = Integer.parseInt(xyText[0]);
                        xyInMapTile[1] = Integer.parseInt(xyText[1]);
                        xyInMapPx[0] = xyInMapTile[0] * Values.MAP_TILE_WIDTH;
                        xyInMapPx[1] = xyInMapTile[1] * Values.MAP_TILE_HEIGHT;
                    } else if ("actor-key".equals(parser.getName())) {
                        actorKey = parser.nextText().trim();;//角色标识
                    } else if ("career-key".equals(parser.getName())) {
                        String key = parser.nextText().trim();
                        if (!Careers.careers.containsKey(key)) {
                            Careers.careers.put(key, new Career(key));
                        }
                        careerKey = key;//职业标识
                        animKey = careerKey + Values.PARTY_NAME[party];//角色动画标识
                        setNowAnim(Values.MAP_ANIM_NORMAL);//角色当前动画状态为“静止地图动画”
                    } else if ("name".equals(parser.getName())) {
                        name = parser.nextText();
                    } else if ("info".equals(parser.getName())) {
                        info = parser.nextText();
                    } else if ("face".equals(parser.getName())) {
                        String fileName = parser.nextText();
                        face = (fileName.trim().length() > 0) ? Anim.readBitmap("faces/" + fileName) : Careers.getCareer(careerKey).getFace();
                    } else if ("affin".equals(parser.getName())) {
                        String text = parser.nextText();
                        if (text.length() > 0) affin = Integer.parseInt(text);
                    } else if ("adj-lv".equals(parser.getName())) {
                        String adjust = parser.nextText();
                        LV = Careers.getCareer(careerKey).getInitLV();
                        if (adjust.length() > 0) LV += Integer.parseInt(adjust);
                    } else if ("adj-mhp".equals(parser.getName())) {
                        String adjust = parser.nextText();
                        MHP = Careers.getCareer(careerKey).getInitMHP();
                        if (adjust.length() > 0) MHP += Integer.parseInt(adjust);
                        HP = MHP;
                    } else if ("adj-str".equals(parser.getName())) {
                        String adjust = parser.nextText();
                        str = Careers.getCareer(careerKey).getInitStr();
                        if (adjust.length() > 0) str += Integer.parseInt(adjust);
                    } else if ("adj-mag".equals(parser.getName())) {
                        String adjust = parser.nextText();
                        mag = Careers.getCareer(careerKey).getInitMag();
                        if (adjust.length() > 0) mag += Integer.parseInt(adjust);
                    } else if ("adj-skl".equals(parser.getName())) {
                        String adjust = parser.nextText();
                        skl = Careers.getCareer(careerKey).getInitSkl();
                        if (adjust.length() > 0) skl += Integer.parseInt(adjust);
                    } else if ("adj-spd".equals(parser.getName())) {
                        String adjust = parser.nextText();
                        spd = Careers.getCareer(careerKey).getInitSpd();
                        if (adjust.length() > 0) spd += Integer.parseInt(adjust);
                    } else if ("adj-luc".equals(parser.getName())) {
                        String adjust = parser.nextText();
                        luc = Careers.getCareer(careerKey).getInitLuc();
                        if (adjust.length() > 0) luc += Integer.parseInt(adjust);
                    } else if ("adj-def".equals(parser.getName())) {
                        String adjust = parser.nextText();
                        def = Careers.getCareer(careerKey).getInitDef();
                        if (adjust.length() > 0) def += Integer.parseInt(adjust);
                    } else if ("adj-res".equals(parser.getName())) {
                        String adjust = parser.nextText();
                        res = Careers.getCareer(careerKey).getInitRes();
                        if (adjust.length() > 0) res += Integer.parseInt(adjust);
                    } else if ("adj-mov".equals(parser.getName())) {
                        String adjust = parser.nextText();
                        mov = Careers.getCareer(careerKey).getInitMov();
                        if (adjust.length() > 0) mov += Integer.parseInt(adjust);
                    } else if ("adj-con".equals(parser.getName())) {
                        String adjust = parser.nextText();
                        con = Careers.getCareer(careerKey).getInitCon();
                        if (adjust.length() > 0) con += Integer.parseInt(adjust);
                    } else if ("grow-mhp".equals(parser.getName())) {
                        String rate = parser.nextText();
                        growMHP = (rate.length() > 0) ? Integer.parseInt(rate) : Careers.getCareer(careerKey).getGrowMHP();
                    } else if ("grow-str".equals(parser.getName())) {
                        String rate = parser.nextText();
                        growStr = (rate.length() > 0) ? Integer.parseInt(rate) : Careers.getCareer(careerKey).getGrowStr();
                    } else if ("grow-mag".equals(parser.getName())) {
                        String rate = parser.nextText();
                        growMag = (rate.length() > 0) ? Integer.parseInt(rate) : Careers.getCareer(careerKey).getGrowMag();
                    } else if ("grow-skl".equals(parser.getName())) {
                        String rate = parser.nextText();
                        growSkl = (rate.length() > 0) ? Integer.parseInt(rate) : Careers.getCareer(careerKey).getGrowSkl();
                    } else if ("grow-spd".equals(parser.getName())) {
                        String rate = parser.nextText();
                        growSpd = (rate.length() > 0) ? Integer.parseInt(rate) : Careers.getCareer(careerKey).getGrowSpd();
                    } else if ("grow-luc".equals(parser.getName())) {
                        String rate = parser.nextText();
                        growLuc = (rate.length() > 0) ? Integer.parseInt(rate) : Careers.getCareer(careerKey).getGrowLuc();
                    } else if ("grow-def".equals(parser.getName())) {
                        String rate = parser.nextText();
                        growDef = (rate.length() > 0) ? Integer.parseInt(rate) : Careers.getCareer(careerKey).getGrowDef();
                    } else if ("grow-res".equals(parser.getName())) {
                        String rate = parser.nextText();
                        growRes = (rate.length() > 0) ? Integer.parseInt(rate) : Careers.getCareer(careerKey).getGrowRes();
                    } else if ("exp-swd".equals(parser.getName())) {
                        String exp = parser.nextText();
                        expSwd = (exp.length() > 0) ? Integer.parseInt(exp) : Careers.getCareer(careerKey).getInitSwd();
                    } else if ("exp-lan".equals(parser.getName())) {
                        String exp = parser.nextText();
                        expLan = (exp.length() > 0) ? Integer.parseInt(exp) : Careers.getCareer(careerKey).getInitLan();
                    } else if ("exp-axe".equals(parser.getName())) {
                        String exp = parser.nextText();
                        expAxe = (exp.length() > 0) ? Integer.parseInt(exp) : Careers.getCareer(careerKey).getInitAxe();
                    } else if ("exp-bow".equals(parser.getName())) {
                        String exp = parser.nextText();
                        expBow = (exp.length() > 0) ? Integer.parseInt(exp) : Careers.getCareer(careerKey).getInitBow();
                    } else if ("exp-stf".equals(parser.getName())) {
                        String exp = parser.nextText();
                        expStf = (exp.length() > 0) ? Integer.parseInt(exp) : Careers.getCareer(careerKey).getInitStf();
                    } else if ("exp-anm".equals(parser.getName())) {
                        String exp = parser.nextText();
                        expAnm = (exp.length() > 0) ? Integer.parseInt(exp) : Careers.getCareer(careerKey).getInitAnm();
                    } else if ("exp-lgt".equals(parser.getName())) {
                        String exp = parser.nextText();
                        expLgt = (exp.length() > 0) ? Integer.parseInt(exp) : Careers.getCareer(careerKey).getInitLgt();
                    } else if ("exp-drk".equals(parser.getName())) {
                        String exp = parser.nextText();
                        expDrk = (exp.length() > 0) ? Integer.parseInt(exp) : Careers.getCareer(careerKey).getInitDrk();
                    } else if ("items".equals(parser.getName())) {
                        items = new ArrayList<>();
                        Item item;
                        String str = parser.nextText().trim();
                        if (str.length() > 0) {
                            String[] keys = str.split(",");
                            for (String key : keys) {
                                if ((item = Items.getItem(key)) == null) {
                                    item = new Item(key);
                                    Items.addItem(item);
                                }
                                if (items.size() < Values.ITEM_MAX_NUM)
                                    items.add(item);
                            }
                        }
                        setEquipedWeapon();
                    }
                }
                //获取到下一个节点，再进行解析
                eventType = parser.next();
            }
            in.close();
            setVisible(true);
            setStandby(false);
            setCanAttack(true);
            setGoBack(false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void drawAnim(Canvas canvas, Paint paint, int[] xyOffset) {
        if (isStandby())
            setNowAnim(Values.MAP_ANIM_STANDBY);
        xyInScrPx[0] = xyInMapPx[0] + xyOffset[0];
        xyInScrPx[1] = xyInMapPx[1] + xyOffset[1];
//        CareerAnims.careerAnims.get(nowAnim).drawAnim(canvas, paint, xyInScrPx);
        Careers.careers.get(careerKey).getAnim(nowAnim).drawAnim(canvas, paint, xyInScrPx);
    }

    //角色待机
    public void standby() {
        setStandby(true);
        setNowAnim(Values.MAP_ANIM_STANDBY);
        Log.d("ACTOR", getName() + " Standby");
    }

    //角色唤醒
    public void awake() {
        setStandby(false);
        setNowAnim(Values.MAP_ANIM_NORMAL);
    }

    public void lostCursor() {
        if (isStandby()) {
            setNowAnim(Values.MAP_ANIM_STANDBY);
        } else {
            setNowAnim(Values.MAP_ANIM_NORMAL);
        }

    }

    //角色按照移动路径移动
    public boolean move(RangeHelper.NodeList movePath) {
        RangeHelper.Node node;
        int[] nextXy;
        for (int i = 0; i < movePath.size(); i++) {
            node = movePath.get(i);
            if (!(node.equals(xyInMapTile)))//找到移动路径中角色当前位置的节点
                continue;
            if (i < movePath.size() - 1) {//还有下一个位置，继续移动
                node = movePath.get(i + 1);
                nextXy = node.getXy();
                if (nextXy[0] < xyInMapTile[0]) {//左移
                    xyInMapPx[0] -= Careers.careers.get(careerKey).getMoveSpd();
                    setNowAnim(Values.MAP_ANIM_LEFT);
                    if (xyInMapPx[0] <= (xyInMapTile[0] - 1) * Values.MAP_TILE_WIDTH)
                        xyInMapTile[0] -= 1;//像素左移超过了一个格子，x-1
                } else if (nextXy[0] > xyInMapTile[0]) {//右移
                    xyInMapPx[0] += Careers.careers.get(careerKey).getMoveSpd();
                    setNowAnim(Values.MAP_ANIM_RIGHT);
                    if (xyInMapPx[0] >= (xyInMapTile[0] + 1) * Values.MAP_TILE_WIDTH)
                        xyInMapTile[0] += 1;//像素右移超过了一个格子，x+1
                } else if (nextXy[1] < xyInMapTile[1]) {//上移
                    xyInMapPx[1] -= Careers.careers.get(careerKey).getMoveSpd();
                    setNowAnim(Values.MAP_ANIM_UP);
                    if (xyInMapPx[1] <= (xyInMapTile[1] - 1) * Values.MAP_TILE_HEIGHT)
                        xyInMapTile[1] -= 1;//像素上移超过了一个格子，y-1
                } else if (nextXy[1] > xyInMapTile[1]) {//下移
                    xyInMapPx[1] += Careers.careers.get(careerKey).getMoveSpd();
                    setNowAnim(Values.MAP_ANIM_DOWN);
                    if (xyInMapPx[1] >= (xyInMapTile[1] + 1) * Values.MAP_TILE_HEIGHT)
                        xyInMapTile[1] += 1;//像素下移超过了一个格子，y+1
                }
                return false;
            } else {//没有下一个位置，移动结束
                return true;
            }
        }
        return false;
    }

    //攻击
    public boolean doAttack(Actor target) {
        if (!isCanAttack()) return true;//不能攻击，攻击过程结束
        if (target.getXyInMapTile()[0] < getXyInMapTile()[0]) {//敌人在左，向左进半格攻击
            xyInMapPx[0] -= goBack ? -5 : 5;
            setNowAnim(Values.MAP_ANIM_LEFT);
            if (xyInMapTile[0] * Values.MAP_TILE_WIDTH - xyInMapPx[0] >= 0.5 * Values.MAP_TILE_WIDTH) { //进了半格
                setGoBack(true);
            }
            if (xyInMapTile[0] * Values.MAP_TILE_WIDTH <= xyInMapPx[0]) { //退回了原位,攻击过程结束
                setGoBack(false);
                setCanAttack(false);
                Log.d("BATTLE_INFO", this.getName() + "对" + target.getName() + "发动攻击!");
                target.beAttacked(this);
                setNowAnim(Values.MAP_ANIM_NORMAL);
                return true;//攻击完成
            }
        } else if (target.getXyInMapTile()[0] > getXyInMapTile()[0]) {//敌人在右，向右进半格攻击
            xyInMapPx[0] += goBack ? -5 : 5;
            setNowAnim(Values.MAP_ANIM_RIGHT);
            if (xyInMapPx[0] - xyInMapTile[0] * Values.MAP_TILE_WIDTH >= 0.5 * Values.MAP_TILE_WIDTH) { //进了半格
                setGoBack(true);
            }
            if (xyInMapTile[0] * Values.MAP_TILE_WIDTH >= xyInMapPx[0]) {//退回了原位,攻击过程结束
                setGoBack(false);
                setCanAttack(false);
                Log.d("BATTLE_INFO", this.getName() + "对" + target.getName() + "发动攻击!");
                target.beAttacked(this);
                setNowAnim(Values.MAP_ANIM_NORMAL);
                return true;//攻击完成
            }
        } else if (target.getXyInMapTile()[1] < getXyInMapTile()[1]) {//敌人在上，向上进半格攻击
            xyInMapPx[1] -= goBack ? -5 : 5;
            setNowAnim(Values.MAP_ANIM_UP);
            if (xyInMapTile[1] * Values.MAP_TILE_HEIGHT - xyInMapPx[1] >= 0.5 * Values.MAP_TILE_HEIGHT) { //进了半格
                setGoBack(true);
            }
            if (xyInMapTile[1] * Values.MAP_TILE_HEIGHT <= xyInMapPx[1]) {//退回了原位,攻击过程结束
                setGoBack(false);
                setCanAttack(false);
                Log.d("BATTLE_INFO", this.getName() + "对" + target.getName() + "发动攻击!");
                target.beAttacked(this);
                setNowAnim(Values.MAP_ANIM_NORMAL);
                return true;//攻击完成
            }
        } else {//敌人在下，向下进半格攻击
            xyInMapPx[1] += goBack ? -5 : 5;
            setNowAnim(Values.MAP_ANIM_DOWN);
            if (xyInMapPx[1] - xyInMapTile[1] * Values.MAP_TILE_HEIGHT >= 0.5 * Values.MAP_TILE_HEIGHT) { //进了半格
                setGoBack(true);
            }
            if (xyInMapTile[1] * Values.MAP_TILE_HEIGHT >= xyInMapPx[1]) {//退回了原位,攻击过程结束
                setGoBack(false);
                setCanAttack(false);
                Log.d("BATTLE_INFO", this.getName() + "对" + target.getName() + "发动攻击!");
                target.beAttacked(this);
                setNowAnim(Values.MAP_ANIM_NORMAL);
                return true;//攻击完成
            }
        }
        return false;
    }

    //被攻击
    public void beAttacked(Actor actor) {
        int dmg, hit, crt;
        if (actor.getEquipedWeapon().getDmgType() == Values.DAMAGE_TYPE_PHYSICS)
            dmg = actor.getAtk() - this.getDef();
        else
            dmg = actor.getMat() - this.getRes();
        dmg = dmg < 0 ? 0 : dmg;

        hit = actor.getHit() - this.getAvd();
        hit = hit > 100 ? 100 : hit;
        hit = hit < 0 ? 0 : hit;

        crt = actor.getCrt() - this.getLuc();
        crt = crt > 100 ? 100 : crt;
        crt = crt < 0 ? 0 : crt;

        Random random = new Random();
        int hitNum = 0, crtNum = 0;
        if ((hitNum = random.nextInt(100)) < hit) {
            if ((crtNum = random.nextInt(100)) < crt) {
                this.injured(dmg * 3);
            } else {
                this.injured(dmg);
            }
        }
        Log.d("BATTLE_INFO", "命中随机数：" + hitNum + "命中：" + hit + "；必杀随机数：" + crtNum + "必杀：" + crt);
    }


    //受伤
    public void injured(int dmg) {
        this.setHP(this.getHP() - dmg);
        if (this.getHP() <= 0) {
            this.setVisible(false);
        }
    }

    public boolean equals(Actor actor) {
        if (actor == null)
            return false;
        return xyInMapTile[0] == actor.getXyInMapTile()[0] && xyInMapTile[1] == actor.getXyInMapTile()[1] && actorKey.equals(actor.getActorKey());
    }

    public int getParty() {
        return party;
    }

    public void setParty(int party) {
        this.party = party;
    }

    public int[] getXyInMapTile() {
        return xyInMapTile;
    }

    public void setXyInMapTile(int[] xyInMapTile) {
        this.xyInMapTile[0] = xyInMapTile[0];
        this.xyInMapTile[1] = xyInMapTile[1];
        this.xyInMapPx[0] = xyInMapTile[0] * Values.MAP_TILE_WIDTH;
        this.xyInMapPx[1] = xyInMapTile[1] * Values.MAP_TILE_WIDTH;
    }

    public int[] getXyInScrPx() {
        return xyInScrPx;
    }

    public void setXyInScrPx(int[] xyInScrPx) {
        this.xyInScrPx = xyInScrPx;
    }

    public String getActorKey() {
        return actorKey;
    }

    public void setActorKey(String actorKey) {
        this.actorKey = actorKey;
    }

    public String getCareerKey() {
        return careerKey;
    }

    public void setCareerKey(String careerKey) {
        this.careerKey = careerKey;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public Bitmap getFace() {
        return face;
    }

    public void setFace(Bitmap face) {
        this.face = face;
    }

    public ArrayList<Item> getItems() {
        return items;
    }

    public void setItems(ArrayList<Item> items) {
        this.items = items;
    }

    public boolean addItem(Item item) {
        if (items.size() == Values.ITEM_MAX_NUM)
            return false;
        items.add(item);
        return true;
    }

    public boolean delItem(int index) {
        items.remove(index - 1);
        return true;
    }

    public boolean canEquip(Item item) {
        if (!item.canEquip())
            return false;
        int itemLv = item.getLv();
        int actorExp = 0;
        switch (item.getType()) {
            case Values.ITEM_TYPE_SWORD:
                actorExp = getExpSwd();
                break;
            case Values.ITEM_TYPE_LANCE:
                actorExp = getExpLan();
                break;
            case Values.ITEM_TYPE_AXE:
                actorExp = getExpAxe();
                break;
            case Values.ITEM_TYPE_BOW:
                actorExp = getExpBow();
                break;
            case Values.ITEM_TYPE_STAFF:
                actorExp = getExpStf();
                break;
            case Values.ITEM_TYPE_ANIMA:
                actorExp = getExpAnm();
                break;
            case Values.ITEM_TYPE_LIGHT:
                actorExp = getExpLgt();
                break;
            case Values.ITEM_TYPE_DARK:
                actorExp = getExpDrk();
                break;
        }
        return actorExp >= itemLv;
    }

    public Item getEquipedWeapon() {
        return equipedWeapon;
    }

    public void setEquipedWeapon() {
        for (Item item : items) {
            if (item != null && this.canEquip(item)) {
                setEquipedWeapon(item);
                Log.d("ACTOR_INFO", "正装备的武器：" + item.getName());
                return;
            }
        }
    }

    public void setEquipedWeapon(Item equipedWeapon) {
        this.equipedWeapon = equipedWeapon;
    }

    public void gainExp(int exp) {
        if (LV == Careers.careers.get(careerKey).getMaxLV()) {//满级不再获取exp
            return;
        } else if (this.exp + exp >= 100) {
            setExp((this.exp + exp) % 100);
            levelUp();
        } else {
            setExp(this.exp + exp);
        }
    }

    public void levelUp() {
        this.LV += 1;
        if (LV == Careers.careers.get(careerKey).getMaxLV()) {//满级exp清零
            setExp(0);
        }
        Random random = new Random();
        if (random.nextInt(100) < growMHP) {
            setMHP(getMHP() + 1);
            Log.d("ACTOR_LEVEL_UP", "生命+1");
        }
        if (random.nextInt(100) < growStr) {
            setStr(getStr() + 1);
            Log.d("ACTOR_LEVEL_UP", "力量+1");
        }
        if (random.nextInt(100) < growMag) {
            setMag(getMag() + 1);
            Log.d("ACTOR_LEVEL_UP", "魔力+1");
        }
        if (random.nextInt(100) < growSkl) {
            setSkl(getSkl() + 1);
            Log.d("ACTOR_LEVEL_UP", "技术+1");
        }
        if (random.nextInt(100) < growSpd) {
            setSpd(getSpd() + 1);
            Log.d("ACTOR_LEVEL_UP", "速度+1");
        }
        if (random.nextInt(100) < growLuc) {
            setLuc(getLuc() + 1);
            Log.d("ACTOR_LEVEL_UP", "幸运+1");
        }
        if (random.nextInt(100) < growDef) {
            setDef(getDef() + 1);
            Log.d("ACTOR_LEVEL_UP", "守备+1");
        }
        if (random.nextInt(100) < growRes) {
            setRes(getRes() + 1);
            Log.d("ACTOR_LEVEL_UP", "魔防+1");
        }
    }

    public int getLV() {
        return LV;
    }

    public void setLV(int LV) {
        this.LV = LV;
    }

    public int getExp() {
        return exp;
    }

    public void setExp(int exp) {
        this.exp = exp;
    }

    public int getMHP() {
        return MHP;
    }

    public void setMHP(int MHP) {
        if (MHP <= Careers.careers.get(careerKey).getMaxMHP())
            this.MHP = MHP;
        else
            this.MHP = Careers.careers.get(careerKey).getMaxMHP();
    }

    public int getHP() {
        return HP;
    }

    public void setHP(int HP) {
        if (HP <= MHP)
            this.HP = HP;
        else
            this.HP = this.MHP;
    }

    public int getStr() {
        return str;
    }

    public void setStr(int str) {
        if (str <= Careers.careers.get(careerKey).getMaxStr())
            this.str = str;
        else
            this.str = Careers.careers.get(careerKey).getMaxStr();
    }

    public int getMag() {
        return mag;
    }

    public void setMag(int mag) {
        if (mag <= Careers.careers.get(careerKey).getMaxMag())
            this.mag = mag;
        else
            this.mag = Careers.careers.get(careerKey).getMaxMag();
    }

    public int getSkl() {
        return skl;
    }

    public void setSkl(int skl) {
        if (skl <= Careers.careers.get(careerKey).getMaxSkl())
            this.skl = skl;
        else
            this.skl = Careers.careers.get(careerKey).getMaxSkl();
    }

    public int getSpd() {
        return spd;
    }

    public void setSpd(int spd) {
        if (spd <= Careers.careers.get(careerKey).getMaxSpd())
            this.spd = spd;
        else
            this.spd = Careers.careers.get(careerKey).getMaxSpd();
    }

    public int getLuc() {
        return luc;
    }

    public void setLuc(int luc) {
        if (luc <= Careers.careers.get(careerKey).getMaxLuc())
            this.luc = luc;
        else
            this.luc = Careers.careers.get(careerKey).getMaxLuc();
    }

    public int getDef() {
        return def;
    }

    public void setDef(int def) {
        if (def <= Careers.careers.get(careerKey).getMaxDef())
            this.def = def;
        else
            this.def = Careers.careers.get(careerKey).getMaxDef();
    }

    public int getRes() {
        return res;
    }

    public void setRes(int res) {
        if (res <= Careers.careers.get(careerKey).getMaxRes())
            this.res = res;
        else
            this.res = Careers.careers.get(careerKey).getMaxRes();
    }

    public int getMov() {
        return mov;
    }

    public void setMov(int mov) {
        this.mov = mov;
    }

    public int getCon() {
        return con;
    }

    public void setCon(int con) {
        this.con = con;
    }

    public int getAid() {
        if (Careers.careers.get(careerKey).getMount()>0)
            setAid(Careers.getCareer(careerKey).getMaxCon() - getCon());
        else
            setAid(getCon() - 1);
        return aid;
    }

    public void setAid(int aid) {
        this.aid = aid;
    }

    public int getAffin() {
        return affin;
    }

    public void setAffin(int affin) {
        this.affin = affin;
    }

    public int[] getStatus() {
        return status;
    }

    public void setStatus(int[] status) {
        this.status = status;
    }

    public int getGrowMHP() {
        return growMHP;
    }

    public void setGrowMHP(int growMHP) {
        this.growMHP = growMHP;
    }

    public int getGrowStr() {
        return growStr;
    }

    public void setGrowStr(int growStr) {
        this.growStr = growStr;
    }

    public int getGrowMag() {
        return growMag;
    }

    public void setGrowMag(int growMag) {
        this.growMag = growMag;
    }

    public int getGrowSkl() {
        return growSkl;
    }

    public void setGrowSkl(int growSkl) {
        this.growSkl = growSkl;
    }

    public int getGrowSpd() {
        return growSpd;
    }

    public void setGrowSpd(int growSpd) {
        this.growSpd = growSpd;
    }

    public int getGrowLuc() {
        return growLuc;
    }

    public void setGrowLuc(int growLuc) {
        this.growLuc = growLuc;
    }

    public int getGrowDef() {
        return growDef;
    }

    public void setGrowDef(int growDef) {
        this.growDef = growDef;
    }

    public int getGrowRes() {
        return growRes;
    }

    public void setGrowRes(int growRes) {
        this.growRes = growRes;
    }

    public int getExpSwd() {
        return expSwd;
    }

    public void setExpSwd(int expSwd) {
        this.expSwd = expSwd;
    }

    public int getExpLan() {
        return expLan;
    }

    public void setExpLan(int expLan) {
        this.expLan = expLan;
    }

    public int getExpAxe() {
        return expAxe;
    }

    public void setExpAxe(int expAxe) {
        this.expAxe = expAxe;
    }

    public int getExpBow() {
        return expBow;
    }

    public void setExpBow(int expBow) {
        this.expBow = expBow;
    }

    public int getExpStf() {
        return expStf;
    }

    public void setExpStf(int expStf) {
        this.expStf = expStf;
    }

    public int getExpAnm() {
        return expAnm;
    }

    public void setExpAnm(int expAnm) {
        this.expAnm = expAnm;
    }

    public int getExpLgt() {
        return expLgt;
    }

    public void setExpLgt(int expLgt) {
        this.expLgt = expLgt;
    }

    public int getExpDrk() {
        return expDrk;
    }

    public void setExpDrk(int expDrk) {
        this.expDrk = expDrk;
    }

    public String getRng() {
        String rng = "--";
        if (equipedWeapon != null) {
            rng = equipedWeapon.getRange()[0] + "~" + equipedWeapon.getRange()[1];
        }
        return rng;
    }

    public int getAtk() {
        atk = str;
        if (equipedWeapon != null && equipedWeapon.getDmgType() == Values.DAMAGE_TYPE_PHYSICS) {
            atk += equipedWeapon.getAtk();
        }
        return atk;
    }

    public void setAtk(int atk) {
        this.atk = atk;
    }

    public int getMat() {
        mat = getMag();
        if (equipedWeapon != null && equipedWeapon.getDmgType() == Values.DAMAGE_TYPE_MAGICAL) {
            mat += equipedWeapon.getAtk();
        }
        return mat;
    }

    public void setMat(int mat) {
        this.mat = mat;
    }

    public int getHit() {
        hit = getSkl() * 2 + getLuc() / 2;
        if (equipedWeapon != null) {
            hit += equipedWeapon.getHit();
        }
        return hit;
    }

    public void setHit(int hit) {
        this.hit = hit;
    }

    public int getAvd() {
        avd = getSpd() * 2 + getLuc() + TerrainInfo.TERRAIN_EFFECT[Terrain.getTerrain(xyInMapTile)][0];
        return avd;
    }

    public void setAvd(int avd) {
        this.avd = avd;
    }

    public int getCrt() {
        crt = getSkl() / 2;
        if (equipedWeapon != null) {
            crt += equipedWeapon.getCrt();
        }
        return crt;
    }

    public void setCrt(int crt) {
        this.crt = crt;
    }

    public int getAsp() {
        asp = getSpd();
        if (equipedWeapon != null && getCon() < equipedWeapon.getWgt()) {
            asp -= equipedWeapon.getWgt() - getCon();
        }
        return asp;
    }

    public void setAsp(int asp) {
        this.asp = asp;
    }

    public String getAnimKey() {
        return animKey;
    }

    public void setAnimKey(String animKey) {
        this.animKey = animKey;
    }

    public String getNowAnim() {
        return nowAnim;
    }

    public void setNowAnim(String nowAnim) {
        this.nowAnim = animKey + nowAnim;
    }

    public boolean isCanAttack() {
        return canAttack;
    }

    public void setCanAttack(boolean canAttack) {
        this.canAttack = canAttack;
    }

    public boolean isStandby() {
        return standby;
    }

    public void setStandby(boolean standby) {
        this.standby = standby;
    }

    public boolean isVisible() {
        return visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    public boolean isGoBack() {
        return goBack;
    }

    public void setGoBack(boolean goBack) {
        this.goBack = goBack;
    }
}
