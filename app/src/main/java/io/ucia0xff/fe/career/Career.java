package io.ucia0xff.fe.career;

import android.graphics.Bitmap;
import android.util.Xml;

import org.xmlpull.v1.XmlPullParser;

import java.io.InputStream;

import io.ucia0xff.fe.Values;
import io.ucia0xff.fe.anim.Anim;

public class Career {
    //基本信息
    private String key;//职业标识
    private String name;//职业名
    private String info;//职业说明
    private Bitmap face;//职业通用头像

    private int type = 0;//兵种类型
    private int moveSpd = 0;//移动速度

    //初始能力
    private int initLV = 0;//等级
    private int initMHP = 0;//HP
    private int initStr = 0;//力量
    private int initMag = 0;//魔力
    private int initSkl = 0;//技术
    private int initSpd = 0;//速度
    private int initLuc = 0;//幸运
    private int initDef = 0;//守备
    private int initRes = 0;//魔防
    private int initMov = 0;//移动
    private int initCon = 0;//体格
    private int initMnt = 0;//坐骑
    private int initAff = 0;//属性

    //能力上限
    private int maxLV = 0;//等级上限
    private int maxMHP = 0;//HP上限
    private int maxStr = 0;//力量上限
    private int maxMag = 0;//魔力上限
    private int maxSkl = 0;//技术上限
    private int maxSpd = 0;//速度上限
    private int maxLuc = 0;//幸运上限
    private int maxDef = 0;//守备上限
    private int maxRes = 0;//魔防上限
    private int maxMov = 0;//移动上限
    private int maxCon = 0;//体格上限

    //成长率
    private int growMHP = 0;//HP
    private int growStr = 0;//力量
    private int growMag = 0;//魔力
    private int growSkl = 0;//技术
    private int growSpd = 0;//速度
    private int growLuc = 0;//幸运
    private int growDef = 0;//守备
    private int growRes = 0;//魔防

    //初始武器熟练度
    private int initSwd = 0;//剑
    private int initLan = 0;//枪
    private int initAxe = 0;//斧
    private int initBow = 0;//弓
    private int initStf = 0;//杖
    private int initAnm = 0;//理
    private int initLgt = 0;//光
    private int initDrk = 0;//暗

    //最大武器熟练度
    private int maxSwd = 0;//剑
    private int maxLan = 0;//枪
    private int maxAxe = 0;//斧
    private int maxBow = 0;//弓
    private int maxStf = 0;//杖
    private int maxAnm = 0;//理
    private int maxLgt = 0;//光
    private int maxDrk = 0;//暗

    public Career(String careerName) {
        try {
            InputStream in = Values.CONTEXT.getAssets().open("career_config/" + careerName +".xml");
            XmlPullParser parser = Xml.newPullParser();
            parser.setInput(in, "UTF-8");
            //START_TAG, END_TAG, TEXT等等的节点
            int eventType = parser.getEventType();
            //当还没有解析到结束文档的节点，一直循环
            while (eventType != XmlPullParser.END_DOCUMENT) {
                if (eventType == XmlPullParser.START_TAG) {
                    if ("key".equals(parser.getName())) {
                        key = parser.nextText();
                    }else if ("name".equals(parser.getName())){
                        name = parser.nextText();
                    }else if ("info".equals(parser.getName())){
                        info = parser.nextText();
                    }else if ("face".equals(parser.getName())){
                        String faceName = parser.nextText();
                        if (faceName.trim().length() > 0) {
                            face = Anim.readBitMap("faces/" + faceName);
                        } else {
                            face = Anim.readBitMap("faces/Unknown.png");
                        }
                    }else if ("type".equals(parser.getName())){
                        type = Integer.parseInt(parser.nextText());
                    }else if ("move-spd".equals(parser.getName())){
                        moveSpd = Values.MOVE_SPEED[Integer.parseInt(parser.nextText())];
                    }else if ("init-lv".equals(parser.getName())){
                        initLV = Integer.parseInt(parser.nextText());
                    }else if ("init-mhp".equals(parser.getName())){
                        initMHP = Integer.parseInt(parser.nextText());
                    }else if ("init-str".equals(parser.getName())){
                        initStr = Integer.parseInt(parser.nextText());
                    }else if ("init-mag".equals(parser.getName())){
                        initMag = Integer.parseInt(parser.nextText());
                    }else if ("init-skl".equals(parser.getName())){
                        initSkl = Integer.parseInt(parser.nextText());
                    }else if ("init-spd".equals(parser.getName())){
                        initSpd = Integer.parseInt(parser.nextText());
                    }else if ("init-luc".equals(parser.getName())){
                        initLuc = Integer.parseInt(parser.nextText());
                    }else if ("init-def".equals(parser.getName())){
                        initDef = Integer.parseInt(parser.nextText());
                    }else if ("init-res".equals(parser.getName())){
                        initRes = Integer.parseInt(parser.nextText());
                    }else if ("init-mov".equals(parser.getName())){
                        initMov = Integer.parseInt(parser.nextText());
                    }else if ("init-con".equals(parser.getName())){
                        initCon = Integer.parseInt(parser.nextText());
                    }else if ("init-mnt".equals(parser.getName())){
                        initMnt = Integer.parseInt(parser.nextText());
                    }else if ("init-aff".equals(parser.getName())){
                        initAff = Integer.parseInt(parser.nextText());
                    }else if ("max-lv".equals(parser.getName())){
                        maxLV = Integer.parseInt(parser.nextText());
                    }else if ("max-mhp".equals(parser.getName())){
                        maxMHP = Integer.parseInt(parser.nextText());
                    }else if ("max-str".equals(parser.getName())){
                        maxStr = Integer.parseInt(parser.nextText());
                    }else if ("max-mag".equals(parser.getName())){
                        maxMag = Integer.parseInt(parser.nextText());
                    }else if ("max-skl".equals(parser.getName())){
                        maxSkl = Integer.parseInt(parser.nextText());
                    }else if ("max-spd".equals(parser.getName())){
                        maxSpd = Integer.parseInt(parser.nextText());
                    }else if ("max-luc".equals(parser.getName())){
                        maxLuc = Integer.parseInt(parser.nextText());
                    }else if ("max-def".equals(parser.getName())){
                        maxDef = Integer.parseInt(parser.nextText());
                    }else if ("max-res".equals(parser.getName())){
                        maxRes = Integer.parseInt(parser.nextText());
                    }else if ("max-mov".equals(parser.getName())){
                        maxMov = Integer.parseInt(parser.nextText());
                    }else if ("max-con".equals(parser.getName())){
                        maxCon = Integer.parseInt(parser.nextText());
                    }else if ("grow-mhp".equals(parser.getName())){
                        growMHP = Integer.parseInt(parser.nextText());
                    }else if ("grow-str".equals(parser.getName())){
                        growStr = Integer.parseInt(parser.nextText());
                    }else if ("grow-mag".equals(parser.getName())){
                        growMag = Integer.parseInt(parser.nextText());
                    }else if ("grow-skl".equals(parser.getName())){
                        growSkl = Integer.parseInt(parser.nextText());
                    }else if ("grow-spd".equals(parser.getName())){
                        growSpd = Integer.parseInt(parser.nextText());
                    }else if ("grow-luc".equals(parser.getName())){
                        growLuc = Integer.parseInt(parser.nextText());
                    }else if ("grow-def".equals(parser.getName())){
                        growDef = Integer.parseInt(parser.nextText());
                    }else if ("grow-res".equals(parser.getName())){
                        growRes = Integer.parseInt(parser.nextText());
                    }else if ("init-swd".equals(parser.getName())){
                        initSwd = Integer.parseInt(parser.nextText());
                    }else if ("init-lan".equals(parser.getName())){
                        initLan = Integer.parseInt(parser.nextText());
                    }else if ("init-axe".equals(parser.getName())){
                        initAxe = Integer.parseInt(parser.nextText());
                    }else if ("init-bow".equals(parser.getName())){
                        initBow = Integer.parseInt(parser.nextText());
                    }else if ("init-stf".equals(parser.getName())){
                        initStf = Integer.parseInt(parser.nextText());
                    }else if ("init-anm".equals(parser.getName())){
                        initAnm = Integer.parseInt(parser.nextText());
                    }else if ("init-lgt".equals(parser.getName())){
                        initLgt = Integer.parseInt(parser.nextText());
                    }else if ("init-drk".equals(parser.getName())){
                        initDrk = Integer.parseInt(parser.nextText());
                    }else if ("max-swd".equals(parser.getName())){
                        maxSwd = Integer.parseInt(parser.nextText());
                    }else if ("max-lan".equals(parser.getName())){
                        maxLan = Integer.parseInt(parser.nextText());
                    }else if ("max-axe".equals(parser.getName())){
                        maxAxe = Integer.parseInt(parser.nextText());
                    }else if ("max-bow".equals(parser.getName())){
                        maxBow = Integer.parseInt(parser.nextText());
                    }else if ("max-stf".equals(parser.getName())){
                        maxStf = Integer.parseInt(parser.nextText());
                    }else if ("max-anm".equals(parser.getName())){
                        maxAnm = Integer.parseInt(parser.nextText());
                    }else if ("max-lgt".equals(parser.getName())){
                        maxLgt = Integer.parseInt(parser.nextText());
                    }else if ("max-drk".equals(parser.getName())){
                        maxDrk = Integer.parseInt(parser.nextText());
                    }
                }
                //获取到下一个节点，再触发解析动作
                eventType = parser.next();
            }
            in.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String getKey() {
        return key;
    }

    public String getName() {
        return name;
    }

    public String getInfo() {
        return info;
    }

    public Bitmap getFace() {
        return face;
    }

    public int getType() {
        return type;
    }

    public int getMoveSpd() {
        return moveSpd;
    }

    public int getInitLV() {
        return initLV;
    }

    public int getInitMHP() {
        return initMHP;
    }

    public int getInitStr() {
        return initStr;
    }

    public int getInitMag() {
        return initMag;
    }

    public int getInitSkl() {
        return initSkl;
    }

    public int getInitSpd() {
        return initSpd;
    }

    public int getInitLuc() {
        return initLuc;
    }

    public int getInitDef() {
        return initDef;
    }

    public int getInitRes() {
        return initRes;
    }

    public int getInitMov() {
        return initMov;
    }

    public int getInitCon() {
        return initCon;
    }

    public int getInitMnt() {
        return initMnt;
    }

    public int getInitAff() {
        return initAff;
    }

    public int getMaxLV() {
        return maxLV;
    }

    public int getMaxMHP() {
        return maxMHP;
    }

    public int getMaxStr() {
        return maxStr;
    }

    public int getMaxMag() {
        return maxMag;
    }

    public int getMaxSkl() {
        return maxSkl;
    }

    public int getMaxSpd() {
        return maxSpd;
    }

    public int getMaxLuc() {
        return maxLuc;
    }

    public int getMaxDef() {
        return maxDef;
    }

    public int getMaxRes() {
        return maxRes;
    }

    public int getMaxMov() {
        return maxMov;
    }

    public int getMaxCon() {
        return maxCon;
    }

    public int getGrowMHP() {
        return growMHP;
    }

    public int getGrowStr() {
        return growStr;
    }

    public int getGrowMag() {
        return growMag;
    }

    public int getGrowSkl() {
        return growSkl;
    }

    public int getGrowSpd() {
        return growSpd;
    }

    public int getGrowLuc() {
        return growLuc;
    }

    public int getGrowDef() {
        return growDef;
    }

    public int getGrowRes() {
        return growRes;
    }

    public int getInitSwd() {
        return initSwd;
    }

    public int getInitLan() {
        return initLan;
    }

    public int getInitAxe() {
        return initAxe;
    }

    public int getInitBow() {
        return initBow;
    }

    public int getInitStf() {
        return initStf;
    }

    public int getInitAnm() {
        return initAnm;
    }

    public int getInitLgt() {
        return initLgt;
    }

    public int getInitDrk() {
        return initDrk;
    }

    public int getMaxSwd() {
        return maxSwd;
    }

    public int getMaxLan() {
        return maxLan;
    }

    public int getMaxAxe() {
        return maxAxe;
    }

    public int getMaxBow() {
        return maxBow;
    }

    public int getMaxStf() {
        return maxStf;
    }

    public int getMaxAnm() {
        return maxAnm;
    }

    public int getMaxLgt() {
        return maxLgt;
    }

    public int getMaxDrk() {
        return maxDrk;
    }
}
