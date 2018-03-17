package io.ucia0xff.fe.actor;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.Xml;

import org.xmlpull.v1.XmlPullParser;

import java.io.InputStream;

import io.ucia0xff.fe.Values;
import io.ucia0xff.fe.anim.ActorAnims;
import io.ucia0xff.fe.anim.Anim;
import io.ucia0xff.fe.career.Career;
import io.ucia0xff.fe.career.Careers;

public class Actor {
    //配置信息
    private int party;//阵营
    private int[] xyTile;//角色在地图数组中的坐标
    private int[] xyPos;//角色动画左上角在屏幕的像素坐标

    //角色信息
    private String actorKey;//角色标识
    private String careerKey;//职业标识
    private String name;//角色名
    private String info;//角色说明
    private Bitmap face;//角色头像

    //能力信息
    private int LV;//等级
    private int exp;//经验
    private int MHP;//最大HP
    private int HP;//HP
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
    private int mnt;//坐骑
    private int aff;//属性

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
    private int atk;//攻击
    private int hit;//命中
    private int avd;//回避
    private int crt;//必杀
    private int asp;//攻速

    private String animKey;//角色动画标识
    private String nowAnim;//当前动画

    private boolean standby;//是否待机
    private boolean visible;//是否可见

    public Actor(String actorName) {
        try {
            InputStream in = Values.CONTEXT.getAssets().open("actor_config/" + actorName + ".xml");
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
                        xyTile = new int[2];
                        xyPos = new int[2];
                        xyTile[0] = Integer.parseInt(xyText[0]);
                        xyTile[1] = Integer.parseInt(xyText[1]);
                        xyPos[0] = xyTile[0] * Values.MAP_TILE_WIDTH;
                        xyPos[1] = xyTile[1] * Values.MAP_TILE_HEIGHT;
                    } else if ("actor-key".equals(parser.getName())) {
                        String key = parser.nextText().trim();
                        actorKey = (key.length() > 0)?key:"";//角色标识
                    } else if ("career-key".equals(parser.getName())) {
                        String key = parser.nextText().trim();
                        if (!Careers.careers.containsKey(key))
                            Careers.careers.put(key, new Career(key));
                        careerKey = key;//职业标识
                        animKey = careerKey + Values.PARTIES[party];//角色动画标识
                        setNowAnim(Values.MAP_ANIM_STATIC);//角色当前动画状态为“静止地图动画”
                    } else if ("name".equals(parser.getName())) {
                        name = parser.nextText();
                    } else if ("info".equals(parser.getName())) {
                        info = parser.nextText();
                    } else if ("face".equals(parser.getName())) {
                        String fileName = parser.nextText();
                        face = (fileName.trim().length() > 0) ? Anim.readBitMap("faces/" + fileName) : Careers.careers.get(careerKey).getFace();
                    } else if ("adj-lv".equals(parser.getName())) {
                        String adjust = parser.nextText();
                        LV = Careers.careers.get(careerKey).getInitLV();
                        if (adjust.length() > 0) LV += Integer.parseInt(adjust);
                    } else if ("adj-mhp".equals(parser.getName())) {
                        String adjust = parser.nextText();
                        MHP = Careers.careers.get(careerKey).getInitMHP();
                        if (adjust.length() > 0) MHP += Integer.parseInt(adjust);
                        HP = MHP;
                    } else if ("adj-str".equals(parser.getName())) {
                        String adjust = parser.nextText();
                        str = Careers.careers.get(careerKey).getInitStr();
                        if (adjust.length() > 0) str += Integer.parseInt(adjust);
                    } else if ("adj-mag".equals(parser.getName())) {
                        String adjust = parser.nextText();
                        mag = Careers.careers.get(careerKey).getInitMag();
                        if (adjust.length() > 0) mag += Integer.parseInt(adjust);
                    } else if ("adj-skl".equals(parser.getName())) {
                        String adjust = parser.nextText();
                        skl = Careers.careers.get(careerKey).getInitSkl();
                        if (adjust.length() > 0) skl += Integer.parseInt(adjust);
                    } else if ("adj-spd".equals(parser.getName())) {
                        String adjust = parser.nextText();
                        spd = Careers.careers.get(careerKey).getInitSpd();
                        if (adjust.length() > 0) spd += Integer.parseInt(adjust);
                    } else if ("adj-luc".equals(parser.getName())) {
                        String adjust = parser.nextText();
                        luc = Careers.careers.get(careerKey).getInitLuc();
                        if (adjust.length() > 0) luc += Integer.parseInt(adjust);
                    } else if ("adj-def".equals(parser.getName())) {
                        String adjust = parser.nextText();
                        def = Careers.careers.get(careerKey).getInitDef();
                        if (adjust.length() > 0) def += Integer.parseInt(adjust);
                    } else if ("adj-res".equals(parser.getName())) {
                        String adjust = parser.nextText();
                        res = Careers.careers.get(careerKey).getInitRes();
                        if (adjust.length() > 0) res += Integer.parseInt(adjust);
                    } else if ("adj-mov".equals(parser.getName())) {
                        String adjust = parser.nextText();
                        mov = Careers.careers.get(careerKey).getInitMov();
                        if (adjust.length() > 0) mov += Integer.parseInt(adjust);
                    } else if ("adj-con".equals(parser.getName())) {
                        String adjust = parser.nextText();
                        con = Careers.careers.get(careerKey).getInitCon();
                        if (adjust.length() > 0) con += Integer.parseInt(adjust);
                    } else if ("adj-mnt".equals(parser.getName())) {
                        String adjust = parser.nextText();
                        mnt = Careers.careers.get(careerKey).getInitMnt();
                        if (adjust.length() > 0) mnt = Integer.parseInt(adjust);
                        aid = (mnt == 0)? con - 1 : Careers.careers.get(careerKey).getMaxCon() - con;
                    } else if ("adj-aff".equals(parser.getName())) {
                        String adjust = parser.nextText();
                        aff = Careers.careers.get(careerKey).getInitAff();
                        if (adjust.length() > 0) aff = Integer.parseInt(adjust);
                    } else if ("grow-mhp".equals(parser.getName())) {
                        String rate = parser.nextText();
                        growMHP = (rate.length() > 0) ? Integer.parseInt(rate) :  Careers.careers.get(careerKey).getGrowMHP();
                    } else if ("grow-str".equals(parser.getName())) {
                        String rate = parser.nextText();
                        growStr = (rate.length() > 0) ? Integer.parseInt(rate) :  Careers.careers.get(careerKey).getGrowStr();
                    } else if ("grow-mag".equals(parser.getName())) {
                        String rate = parser.nextText();
                        growMag = (rate.length() > 0) ? Integer.parseInt(rate) :  Careers.careers.get(careerKey).getGrowMag();
                    } else if ("grow-skl".equals(parser.getName())) {
                        String rate = parser.nextText();
                        growSkl = (rate.length() > 0) ? Integer.parseInt(rate) :  Careers.careers.get(careerKey).getGrowSkl();
                    } else if ("grow-spd".equals(parser.getName())) {
                        String rate = parser.nextText();
                        growSpd = (rate.length() > 0) ? Integer.parseInt(rate) :  Careers.careers.get(careerKey).getGrowSpd();
                    } else if ("grow-luc".equals(parser.getName())) {
                        String rate = parser.nextText();
                        growLuc = (rate.length() > 0) ? Integer.parseInt(rate) :  Careers.careers.get(careerKey).getGrowLuc();
                    } else if ("grow-def".equals(parser.getName())) {
                        String rate = parser.nextText();
                        growDef = (rate.length() > 0) ? Integer.parseInt(rate) :  Careers.careers.get(careerKey).getGrowDef();
                    } else if ("grow-res".equals(parser.getName())) {
                        String rate = parser.nextText();
                        growRes = (rate.length() > 0) ? Integer.parseInt(rate) :  Careers.careers.get(careerKey).getGrowRes();
                    } else if ("exp-swd".equals(parser.getName())) {
                        String exp = parser.nextText();
                        expSwd = (exp.length() > 0) ? Integer.parseInt(exp) : Careers.careers.get(careerKey).getInitSwd();
                    } else if ("exp-lan".equals(parser.getName())) {
                        String exp = parser.nextText();
                        expLan = (exp.length() > 0) ? Integer.parseInt(exp) : Careers.careers.get(careerKey).getInitLan();
                    } else if ("exp-axe".equals(parser.getName())) {
                        String exp = parser.nextText();
                        expAxe = (exp.length() > 0) ? Integer.parseInt(exp) : Careers.careers.get(careerKey).getInitAxe();
                    } else if ("exp-bow".equals(parser.getName())) {
                        String exp = parser.nextText();
                        expBow = (exp.length() > 0) ? Integer.parseInt(exp) : Careers.careers.get(careerKey).getInitBow();
                    } else if ("exp-stf".equals(parser.getName())) {
                        String exp = parser.nextText();
                        expStf = (exp.length() > 0) ? Integer.parseInt(exp) : Careers.careers.get(careerKey).getInitStf();
                    } else if ("exp-anm".equals(parser.getName())) {
                        String exp = parser.nextText();
                        expAnm = (exp.length() > 0) ? Integer.parseInt(exp) : Careers.careers.get(careerKey).getInitAnm();
                    } else if ("exp-lgt".equals(parser.getName())) {
                        String exp = parser.nextText();
                        expLgt = (exp.length() > 0) ? Integer.parseInt(exp) : Careers.careers.get(careerKey).getInitLgt();
                    } else if ("exp-drk".equals(parser.getName())) {
                        String exp = parser.nextText();
                        expDrk = (exp.length() > 0) ? Integer.parseInt(exp) : Careers.careers.get(careerKey).getInitDrk();
                    }
                }
                //获取到下一个节点，在触发解析动作
                eventType = parser.next();
            }
            in.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void drawAnim(Canvas canvas, Paint paint, int[] xyOffset) {
        if (isStandby())
            setNowAnim(Values.MAP_ANIM_STANDBY);
        ActorAnims.actorAnims.get(nowAnim).drawAnim(canvas, paint, xyTile, xyOffset);
    }

    public void lostCursor() {
        if (isStandby()) {
            setNowAnim(Values.MAP_ANIM_STANDBY);
        } else {
            setNowAnim(Values.MAP_ANIM_STATIC);
        }
    }

    public void getCursor() {
        if (isStandby()) {
            setNowAnim(Values.MAP_ANIM_STANDBY);
        } else if (party != Values.PARTY_PLAYER) {
            setNowAnim(Values.MAP_ANIM_STATIC);
        } else {
            setNowAnim(Values.MAP_ANIM_DYNAMIC);
        }
    }

    public boolean equals(Actor actor) {
        if (actor == null)
            return false;
        return xyTile[0] == actor.getXyTile()[0] && xyTile[1] == actor.getXyTile()[1] && actorKey.equals(actor.getActorKey());
    }

    public int getParty() {
        return party;
    }

    public void setParty(int party) {
        this.party = party;
    }

    public int[] getXyTile() {
        return xyTile;
    }

    public void setXyTile(int[] xyTile) {
        this.xyTile = xyTile;
    }

    public int[] getXyPos() {
        return xyPos;
    }

    public void setXyPos(int[] xyPos) {
        this.xyPos = xyPos;
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
        this.MHP = MHP;
    }

    public int getHP() {
        return HP;
    }

    public void setHP(int HP) {
        this.HP = HP;
    }

    public int getStr() {
        return str;
    }

    public void setStr(int str) {
        this.str = str;
    }

    public int getMag() {
        return mag;
    }

    public void setMag(int mag) {
        this.mag = mag;
    }

    public int getSkl() {
        return skl;
    }

    public void setSkl(int skl) {
        this.skl = skl;
    }

    public int getSpd() {
        return spd;
    }

    public void setSpd(int spd) {
        this.spd = spd;
    }

    public int getLuc() {
        return luc;
    }

    public void setLuc(int luc) {
        this.luc = luc;
    }

    public int getDef() {
        return def;
    }

    public void setDef(int def) {
        this.def = def;
    }

    public int getRes() {
        return res;
    }

    public void setRes(int res) {
        this.res = res;
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
        return aid;
    }

    public void setAid(int aid) {
        this.aid = aid;
    }

    public int getMnt() {
        return mnt;
    }

    public void setMnt(int mnt) {
        this.mnt = mnt;
    }

    public int getAff() {
        return aff;
    }

    public void setAff(int aff) {
        this.aff = aff;
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

    public int getAtk() {
        return atk;
    }

    public void setAtk(int atk) {
        this.atk = atk;
    }

    public int getHit() {
        return hit;
    }

    public void setHit(int hit) {
        this.hit = hit;
    }

    public int getAvd() {
        return avd;
    }

    public void setAvd(int avd) {
        this.avd = avd;
    }

    public int getCrt() {
        return crt;
    }

    public void setCrt(int crt) {
        this.crt = crt;
    }

    public int getAsp() {
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
}