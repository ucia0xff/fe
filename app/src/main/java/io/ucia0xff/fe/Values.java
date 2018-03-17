package io.ucia0xff.fe;

import android.content.Context;
import android.graphics.Rect;

public class Values {
    public static Context CONTEXT = null;
    public static int FPS = 50;

    //屏幕宽高
    public static int SCREEN_WIDTH = 1080;
    public static int SCREEN_HEIGHT = 1920;

    //图集中瓦片块的宽高
    public static final int RES_TILE_WIDTH = 16;
    public static final int RES_TILE_HEIGHT = 16;

    //游戏中瓦片块的宽高
    public static final int MAP_TILE_WIDTH = 100;
    public static final int MAP_TILE_HEIGHT = 100;

    //角色地图动画帧大小
    public static final int RES_ANIM_SIZE_16 = 16;
    public static final int RES_ANIM_SIZE_32 = 32;

    //角色地图动画帧大小标识
    public static final int RES_ANIM_SIZE_16x16 = 1616;
    public static final int RES_ANIM_SIZE_16x32 = 1632;
    public static final int RES_ANIM_SIZE_32x32 = 3232;

    //角色地图动画显示区域
    public static Rect MAP_ANIM_SIZE_16x16 = new Rect(0, 0, MAP_TILE_WIDTH, MAP_TILE_HEIGHT);
    public static Rect MAP_ANIM_SIZE_16x32 = new Rect(0, 0, MAP_TILE_WIDTH, MAP_TILE_HEIGHT*2);
    public static Rect MAP_ANIM_SIZE_32x32 = new Rect(0, 0, MAP_TILE_WIDTH*2, MAP_TILE_HEIGHT*2);

    //角色阵营
    public static final String[] PARTIES = {"Player", "Ally", "Enemy", "Unknown"};
    public static final int PARTY_COUNT = 4;
    public static final int PARTY_PLAYER = 0;//我军
    public static final int PARTY_ENEMY = 1;//敌军
    public static final int PARTY_ALLY = 2;//友军
    public static final int PARTY_UNKNOWN = 3;//中立

    //回合
    public static final int PHASE_PLAYER = 0;
    public static final int PHASE_ENEMY = 1;
    public static final int PHASE_ALLY = 2;
    public static final int PHASE_UNKNOWN = 3;

    //游戏进行阶段
    public static final int CASE_NORMAL = 0;//未选中角色
    public static final int CASE_BEFORE_MOVE = 1;//移动前、显示移动范围
    public static final int CASE_MOVING = 2;//移动中
    public static final int CASE_AFTER_MOVE = 3;//移动结束、显示行动菜单
    public static final int CASE_SELECT_ITEM = 4;//选择要使用的武器/杖/道具
    public static final int CASE_BEFORE_ACT = 5;//选择目标/操作
    public static final int CASE_ACTING = 6;//行动中
    public static final int CASE_AFTER_ACT = 7;//行动结束
    public static final int CASE_SHOW_ACTOR_INFO = 8;//显示角色信息
    public static final int CASE_SHOW_GAME_OPTIONS = 9;//显示游戏菜单


    //角色地图动画状态
    public static final String MAP_ANIM_STATIC = "Static";
    public static final String MAP_ANIM_DYNAMIC = "Dynamic";
    public static final String MAP_ANIM_DOWN = "Down";
    public static final String MAP_ANIM_UP = "Up";
    public static final String MAP_ANIM_RIGHT = "Right";
    public static final String MAP_ANIM_LEFT = "Left";
    public static final String MAP_ANIM_STANDBY = "Standby";

    //光标动画状态
    public static final String CURSOR_DYNAMIC="Dynamic";
    public static final String CURSOR_STATIC="Static";
    public static final String CURSOR_TARGET="Target";
    public static final String CURSOR_ALLOWED="Allowed";
    public static final String CURSOR_FORBIDEN="Forbidden";

    //武器熟练度等级
    public static final int WEAPON_LEVEL__ = 0;
    public static final int WEAPON_LEVEL_E = 1;
    public static final int WEAPON_LEVEL_D = 31;
    public static final int WEAPON_LEVEL_C = 71;
    public static final int WEAPON_LEVEL_B = 121;
    public static final int WEAPON_LEVEL_A = 181;
    public static final int WEAPON_LEVEL_S = 251;
    public static final int WEAPON_LEVEL_SS = 255;

    public static final String WEAPON__ = "—";
    public static final String WEAPON_E = "E";
    public static final String WEAPON_D = "D";
    public static final String WEAPON_C = "C";
    public static final String WEAPON_B = "B";
    public static final String WEAPON_A = "A";
    public static final String WEAPON_S = "S";
    public static final String WEAPON_SS = "SS";
    public static final String WEAPON_P = "★";

    public static final int WEAPON_E_TO_D = 30;
    public static final int WEAPON_D_TO_C = 40;
    public static final int WEAPON_C_TO_B = 50;
    public static final int WEAPON_B_TO_A = 60;
    public static final int WEAPON_A_TO_S = 70;

    //使用武器
    public static final String WEAPON_NONE = "None";
    public static final String WEAPON_SWORD = "Sword";
    public static final String WEAPON_LANCE = "Lance";
    public static final String WEAPON_AXE = "Axe";
    public static final String WEAPON_BOW = "Bow";
    public static final String WEAPON_MAGIC = "Magic";

    //攻击方式
    public static final String MELEE_ATK = "MeleeAtk";//攻击
    public static final String MELEE_CRT = "MeleeCrt";//必杀
    public static final String RANGED_ATK = "RangedAtk";//间接攻击
    public static final String RANGED_CRT = "RangedCrt";//间接必杀
    public static final String STAND = "Stand";//站立不动
    public static final String DODGE = "Dodge";//回避

    //人物属性
    public static final String[] AFFIN = {"","炎","雷","风","冰","暗","光","理"};
}
