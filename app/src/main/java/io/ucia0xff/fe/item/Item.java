package io.ucia0xff.fe.item;

import android.graphics.Bitmap;
import android.util.Xml;

import org.xmlpull.v1.XmlPullParser;

import java.io.InputStream;

import io.ucia0xff.fe.Values;
//import io.ucia0xff.fe.ability.Ability;
import io.ucia0xff.fe.anim.Anim;

public class Item {

    //基本信息
    private String key;//道具标识
    private int type;//道具类型
    private String name;//道具名称
    private String info;//道具说明
    private Bitmap icon;//道具图标
    private int uses;//耐久
    private boolean canUse;//可否使用
    private boolean canEquip;//可否装备，可装备的是武器，武器才有下面的数据

    //武器数据
    private int dmgType;//伤害类型
    private int lv;//武器熟练度等级
    private int atk;//攻击
    private int hit;//命中
    private int wgt;//重量
    private int crt;//必杀
    private int[] range;//射程
    private int exp;//使用后获得的武器熟练度
//    protected List<Ability> abilities;//道具能力


    public Item(String itemName) {
        try {
            InputStream in = Values.CONTEXT.getAssets().open("item_config/" + itemName + ".xml");
            XmlPullParser parser = Xml.newPullParser();
            parser.setInput(in, "UTF-8");

            //START_TAG, END_TAG, TEXT等等的节点
            int eventType = parser.getEventType();

            //当还没有解析到结束文档的节点，一直循环
            while (eventType != XmlPullParser.END_DOCUMENT) {
                if (eventType == XmlPullParser.START_TAG) {
                    if ("key".equals(parser.getName())) {
                        key = parser.nextText().trim();
                    } else if ("type".equals(parser.getName())){
                        type = Integer.parseInt(parser.nextText());
                    } else if ("name".equals(parser.getName())){
                        name = parser.nextText().trim();
                    } else if ("info".equals(parser.getName())){
                        info = parser.nextText().trim();
                    } else if ("icon".equals(parser.getName())) {
                        String fileName = parser.nextText();
                        icon = (fileName.trim().length() > 0) ? Anim.readBitmap("item_icon/" + fileName) : null;
                    } else if ("uses".equals(parser.getName())){
                        uses = Integer.parseInt(parser.nextText());
                    } else if ("can-use".equals(parser.getName())){
                        canUse = Integer.parseInt(parser.nextText()) == 1;
                    } else if ("can-equip".equals(parser.getName())){
                        canEquip = Integer.parseInt(parser.nextText()) == 1;
                    } else if ("dmg-type".equals(parser.getName()) && canEquip){
                        dmgType = Integer.parseInt(parser.nextText());
                    } else if ("lv".equals(parser.getName()) && canEquip){
                        lv = Integer.parseInt(parser.nextText());
                    } else if ("atk".equals(parser.getName()) && canEquip){
                        atk = Integer.parseInt(parser.nextText());
                    } else if ("hit".equals(parser.getName()) && canEquip){
                        hit = Integer.parseInt(parser.nextText());
                    } else if ("wgt".equals(parser.getName()) && canEquip){
                        wgt = Integer.parseInt(parser.nextText());
                    } else if ("crt".equals(parser.getName()) && canEquip){
                        crt = Integer.parseInt(parser.nextText());
                    } else if ("range".equals(parser.getName()) && canEquip){
                        String str[] = parser.nextText().split(",");
                        range = new int[2];
                        range[0] = Integer.parseInt(str[0]);
                        range[1] = Integer.parseInt(str[1]);
                    } else if ("exp".equals(parser.getName()) && canEquip){
                        exp = Integer.parseInt(parser.nextText());
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

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
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

    public Bitmap getIcon() {
        return icon;
    }

    public void setIcon(Bitmap icon) {
        this.icon = icon;
    }

/*    public List<Ability> getAbilities() {
        return abilities;
    }*/

/*    public void setAbilities(List<Ability> abilities) {
        this.abilities = abilities;
    }*/

    public int getUses() {
        return uses;
    }

    public void setUses(int uses) {
        this.uses = uses;
    }

    public boolean canUse() {
        return canUse;
    }

    public void setCanUse(boolean canUse) {
        this.canUse = canUse;
    }

    public boolean canEquip() {
        return canEquip;
    }

    public void setCanEquip(boolean canEquip) {
        this.canEquip = canEquip;
    }

    public int getDmgType() {
        return dmgType;
    }

    public void setDmgType(int dmgType) {
        this.dmgType = dmgType;
    }

    public int getLv() {
        return lv;
    }

    public void setLv(int lv) {
        this.lv = lv;
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

    public int getWgt() {
        return wgt;
    }

    public void setWgt(int wgt) {
        this.wgt = wgt;
    }

    public int getCrt() {
        return crt;
    }

    public void setCrt(int crt) {
        this.crt = crt;
    }

    public int[] getRange() {
        return range;
    }

    public void setRange(int[] range) {
        this.range = range;
    }

    public int getExp() {
        return exp;
    }

    public void setExp(int exp) {
        this.exp = exp;
    }

}
