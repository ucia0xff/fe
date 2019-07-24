package io.ucia0xff.fe;

import android.content.Context;
import android.graphics.Rect;

public class Values {
    public static Context CONTEXT = null;
    public static int FPS = 60;

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
    public static final String[] PARTY_NAME = {"Player", "Ally", "Enemy"};
    public static final int PARTY_COUNT = 3;
    public static final int PARTY_PLAYER = 0;//我军
    public static final int PARTY_ALLY = 1;//友军
    public static final int PARTY_ENEMY = 2;//敌军

    //回合阶段
    public static final int PHASE_PLAYER = 0;
    public static final int PHASE_ALLY = 1;
    public static final int PHASE_ENEMY = 2;

    //游戏系统状态
    public static final int CASE_NORMAL = 0;//通常状态
    public static final int CASE_BEFORE_MOVE = 1;//移动前状态，显示移动范围
    public static final int CASE_MOVING = 2;//移动中状态，播放移动动画
    public static final int CASE_AFTER_MOVE = 3;//移动后状态，显示角色行动菜单
    public static final int CASE_SELECT_ITEM = 4;//选择要使用的武器/杖/道具
    public static final int CASE_BEFORE_ACT = 5;//行动前状态，选择行动目标
    public static final int CASE_ACTING = 6;//行动中状态，播放战斗动画
    public static final int CASE_AFTER_ACT = 7;//行动后状态，获得经验
    public static final int CASE_SHOW_ACTOR_INFO = 8;//显示角色信息
    public static final int CASE_SHOW_GAME_OPTIONS = 9;//显示游戏菜单


    //角色地图动画状态
    public static final String MAP_ANIM_NORMAL = "Normal";//通常状态
    public static final String MAP_ANIM_STANDBY = "Standby";//待机状态
    public static final String MAP_ANIM_LEFT = "Left";//左移状态
    public static final String MAP_ANIM_RIGHT = "Right";//右移状态
    public static final String MAP_ANIM_UP = "Up";//上移状态
    public static final String MAP_ANIM_DOWN = "Down";//下移状态

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

    public static final String WEAPON__ = "-";
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

    //伤害类型
    public static final int DAMAGE_TYPE_PHYSICS = 0;
    public static final int DAMAGE_TYPE_MAGICAL = 1;

    //攻击方式
    public static final String MELEE_ATK = "MeleeAtk";//攻击
    public static final String MELEE_CRT = "MeleeCrt";//必杀
    public static final String RANGED_ATK = "RangedAtk";//间接攻击
    public static final String RANGED_CRT = "RangedCrt";//间接必杀
    public static final String STAND = "Stand";//站立不动
    public static final String DODGE = "Dodge";//回避

    //坐骑
    public static final String[] MOUNT_NAME = {"无","马","天马","飞龙"};

    //人物属性
    public static final String[] AFFIN_NAME = {"无","炎","雷","风","冰","暗","光","理"};

    //人物移动速度
    public static final int[] MOVE_SPEED = {5, 10, 20};

    //人物头像宽高
    public static final int RES_FACE_WIDTH = 80;
    public static final int RES_FACE_HEIGHT = 80;

    //物品类型
    public static final int ITEM_TYPE_SWORD = 0;
    public static final int ITEM_TYPE_LANCE = 1;
    public static final int ITEM_TYPE_AXE = 2;
    public static final int ITEM_TYPE_BOW = 3;
    public static final int ITEM_TYPE_STAFF = 4;
    public static final int ITEM_TYPE_ANIMA = 5;
    public static final int ITEM_TYPE_LIGHT = 6;
    public static final int ITEM_TYPE_DARK = 7;
    public static final int ITEM_TYPE_OTHERS = 8;
    public static final int ITEM_TYPE_ITEM = 9;

    //物品数量上限
    public static final int ITEM_MAX_NUM = 5;

    //人物头像宽高
    public static final int RES_ITEM_WIDTH = 16;
    public static final int RES_ITEM_HEIGHT = 16;
}
