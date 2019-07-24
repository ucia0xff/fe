package io.ucia0xff.fe.actor;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;

import io.ucia0xff.fe.Paints;
import io.ucia0xff.fe.R;
import io.ucia0xff.fe.Values;
import io.ucia0xff.fe.anim.Anim;
import io.ucia0xff.fe.career.Career;
import io.ucia0xff.fe.career.Careers;
import io.ucia0xff.fe.item.Item;

public class ActorInfo {

    private Actor actor;
    private Career career;

    //背景图片
    private Bitmap bgBrief;//简略信息的背景
    private Bitmap bgDetail;//详细信息的背景

    //图标
    private Bitmap mounts;
    private Bitmap affins;
    private Bitmap weapons;

    //页码
    private int allPage = 3;
    private int nowPage = 0;

    //源图像要显示区域
    private Rect bgBriefSrc;
    private Rect bgDetailSrc;
    private Rect faceSrc;

    //绘制在画布上的区域
    private Rect bgBriefDst;//绘制简略信息面板的区域
    private Rect bgDetailDst;//绘制详细信息面板的区域
    private Rect faceBriefDst;//详细信息面板上绘制头像的区域
    private Rect faceDetailDst;//详细信息面板上绘制头像的区域
    private Rect basicDst;//详细信息面板上绘制基本信息的区域

    //构造方法
    public ActorInfo() {
        bgBrief = Anim.readBitmap(R.drawable.bg_actor_info_brief);
        bgBriefSrc = new Rect(0, 0, bgBrief.getWidth(), bgBrief.getHeight());
        bgBriefDst = new Rect(0, 0, 5*Values.MAP_TILE_WIDTH, 3*Values.MAP_TILE_HEIGHT);

        bgDetail = Anim.readBitmap(R.drawable.bg_actor_info_detail);
        bgDetailSrc = new Rect(0, 0, bgDetail.getWidth(), bgDetail.getHeight());
        bgDetailDst = new Rect(0, 0, Values.SCREEN_WIDTH, Values.SCREEN_HEIGHT);

        faceSrc = new Rect(0, 0, Values.RES_FACE_WIDTH, Values.RES_FACE_HEIGHT);
        faceBriefDst = new Rect(0, 0, 2*Values.MAP_TILE_WIDTH, 2*Values.MAP_TILE_HEIGHT);
        faceDetailDst = new Rect(0, 0, Values.SCREEN_WIDTH / 2, Values.SCREEN_WIDTH / 2);

        basicDst = new Rect(Values.SCREEN_WIDTH / 2, 0, Values.SCREEN_WIDTH, Values.SCREEN_WIDTH / 2);

        mounts = Anim.readBitmap(R.drawable.icon_mounts);
        affins = Anim.readBitmap(R.drawable.icon_affins);
        weapons = Anim.readBitmap(R.drawable.icon_weapons);
    }

    //显示角色简略信息
    /**
     * 显示地形信息
     * @param canvas
     * @param paint
     * @param isLeft 光标所在的格子是否在屏幕左边
     */
    public void show(Canvas canvas, Paint paint, Boolean isLeft) {
        if (actor==null || canvas==null) return;
        if (isLeft) {//光标在屏幕左边，则在屏幕右边显示角色简略信息
            bgBriefDst.offsetTo(Values.SCREEN_WIDTH - bgBriefDst.width(), 0);
            faceBriefDst.offsetTo( bgBriefDst.left + (int) (6.0 / bgBriefSrc.width() * bgBriefDst.width()), (int) (8.0 / bgBriefSrc.height()*bgBriefDst.height()));
        }
        else {//光标在屏幕右边，则在屏幕左边显示角色简略信息
            bgBriefDst.offsetTo(0, 0);
            faceBriefDst.offsetTo( (int) (6.0 / bgBriefSrc.width() * bgBriefDst.width()), (int) (8.0 / bgBriefSrc.height()*bgBriefDst.height()));
        }

        //背景
        canvas.drawBitmap(bgBrief, bgBriefSrc, bgBriefDst, paint);

        //头像
        canvas.drawBitmap(actor.getFace(), faceSrc, faceBriefDst, paint);

        //角色名
        String text = actor.getName();
        int startX = ((bgBriefDst.right - (int) (5.0 / bgBriefSrc.width() * bgBriefDst.width()) - faceBriefDst.right) / 2) + faceBriefDst.right;
        canvas.drawText(text, startX, bgBriefDst.height() / 3, Paints.paints.get("actor_name"));

        //LV、Exp
        text = "LV  E  ";
        canvas.drawText(text, startX, bgBriefDst.height() / 5 * 3, Paints.paints.get("lv_exp_hp"));
        int num = actor.getLV();
        text = (num > 9 ? "  " : "   ") + num + "   ";
        canvas.drawText(text, startX, bgBriefDst.height() / 5 * 3, Paints.paints.get("lv_exp_hp_num_blue"));
        num = actor.getExp();
        text = (num > 9 ? "     " : "      ") + num;
        canvas.drawText(text, startX, bgBriefDst.height() / 5 * 3, Paints.paints.get("lv_exp_hp_num_blue"));

        //HP、MaxHP
        int startY = bgBriefDst.height() - (int) (9.0 / bgBriefSrc.height() * bgBriefDst.height());
        text = "HP  /  ";
        canvas.drawText(text, startX, startY, Paints.paints.get("lv_exp_hp"));
        num = actor.getHP();
        text = (num > 99 ? "  --   " : num > 9 ? "  " + num + "   " : "   " + num + "   ");
        if (actor.getHP() < actor.getMHP() * 0.3)
            canvas.drawText(text, startX, startY, Paints.paints.get("lv_exp_hp_num_red"));
        else if (actor.getHP() < actor.getMHP() * 0.7)
            canvas.drawText(text, startX, startY, Paints.paints.get("lv_exp_hp_num_yellow"));
        else
            canvas.drawText(text, startX, startY, Paints.paints.get("lv_exp_hp_num_blue"));
        num = actor.getMHP();
        text = (num > 99 ? "     --" : num > 9 ? "     " + num : "      " + num);
        canvas.drawText(text, startX, startY, Paints.paints.get("lv_exp_hp_num_green"));
    }

    //显示角色详细信息
    public void showDetail(Canvas canvas, Paint paint) {
        canvas.drawBitmap(bgDetail, bgDetailSrc, bgDetailDst, paint);//背景

        String text;
        int num;

        //头像
        canvas.drawBitmap(actor.getFace(), faceSrc, faceDetailDst, paint);


        //角色名
        text = actor.getName();
        canvas.drawText(text, Values.SCREEN_WIDTH / 4 * 3, basicDst.height()/4, Paints.paints.get("actor_name"));

       //职业名
        text = Careers.getCareer(actor.getCareerKey()).getName();
        canvas.drawText(text, Values.SCREEN_WIDTH / 4 * 3, faceDetailDst.height()/ 5 * 2, Paints.paints.get("career_name"));

        //LV、Exp
        text = "LV  E  ";
        canvas.drawText(text, Values.SCREEN_WIDTH / 4 * 3, faceDetailDst.height() / 5 * 3, Paints.paints.get("lv_exp_hp"));
        num = actor.getLV();
        text = (num > 9 ? "  " : "   ") + num + "   ";
        canvas.drawText(text, Values.SCREEN_WIDTH / 4 * 3, faceDetailDst.height() / 5 * 3, Paints.paints.get("lv_exp_hp_num_blue"));
        num = actor.getExp();
        text = (num > 9 ? "     " : "      ") + num;
        canvas.drawText(text, Values.SCREEN_WIDTH / 4 * 3, faceDetailDst.height() / 5 * 3, Paints.paints.get("lv_exp_hp_num_blue"));

        //HP、MaxHP
        text = "HP  /  ";
        canvas.drawText(text, Values.SCREEN_WIDTH / 4 * 3, faceDetailDst.height() / 5 * 4, Paints.paints.get("lv_exp_hp"));
        num = actor.getHP();
        text = (num > 99 ? "  --   " : num > 9 ? "  " + num + "   " : "   " + num + "   ");
        if (actor.getHP() < actor.getMHP() * 0.3)
            canvas.drawText(text, Values.SCREEN_WIDTH / 4 * 3, faceDetailDst.height() / 5 * 4, Paints.paints.get("lv_exp_hp_num_red"));
        else if (actor.getHP() < actor.getMHP() * 0.7)
            canvas.drawText(text, Values.SCREEN_WIDTH / 4 * 3, faceDetailDst.height() / 5 * 4, Paints.paints.get("lv_exp_hp_num_yellow"));
        else
            canvas.drawText(text, Values.SCREEN_WIDTH / 4 * 3, faceDetailDst.height() / 5 * 4, Paints.paints.get("lv_exp_hp_num_blue"));
        num = actor.getMHP();
        text = (num > 99 ? "     --" : num > 9 ? "     " + num : "      " + num);
        canvas.drawText(text, Values.SCREEN_WIDTH / 4 * 3, faceDetailDst.height() / 5 * 4, Paints.paints.get("lv_exp_hp_num_green"));

        //页面指示
        canvas.drawText(nowPage + 1 + "/" + allPage, Values.SCREEN_WIDTH, basicDst.bottom /5 * 6, Paints.paints.get("page_indicator"));

        switch (nowPage) {
            case 0:
                showData(canvas, paint);
                break;
            case 1:
                showItems(canvas, paint);
                break;
            case 2:
                showLevel(canvas, paint);
                break;
        }
    }

    public void showData(Canvas canvas, Paint paint) {
        int leftNameStartX = Values.SCREEN_WIDTH / 20;//左侧各项能力名称显示的起始坐标
        int leftBarStartX = leftNameStartX + (int) Paints.paints.get("ability_name").measureText("能力 ");//左侧各项能力数值条显示的起始坐标
        int rightNameStartX = leftNameStartX + Values.SCREEN_WIDTH / 2;//右侧各项能力名称显示的起始坐标
        int rightBarStartX = leftBarStartX + Values.SCREEN_WIDTH / 2;//右侧各项能力数值条显示的起始坐标

        int valueStep = (Values.SCREEN_WIDTH /2 - leftBarStartX) / 30;
        int maxValue;
        int nowValue;

        int startY = basicDst.bottom;
        int rowStep = (Values.SCREEN_HEIGHT - basicDst.bottom) / 10;

        //第一行标题
        startY += rowStep;
        canvas.drawText("角色数据", Values.SCREEN_WIDTH/2, startY, Paints.paints.get("page_title"));

        //第二行第一列力量
        startY += rowStep;
        maxValue = career.getMaxStr();
        nowValue = actor.getStr();
        //绘制能力名称
        canvas.drawText("力量", leftNameStartX, startY, Paints.paints.get("ability_name"));
        //绘制数值条边框
        canvas.drawLine(leftBarStartX - 4, startY -16, maxValue * valueStep + leftBarStartX + 4, startY - 16, Paints.paints.get("ability_bar_border"));
        //绘制数值条最大值
        canvas.drawLine(leftBarStartX, startY - 16, maxValue * valueStep + leftBarStartX, startY - 16, Paints.paints.get("ability_bar_blank"));
        //绘制数值条当前值
        canvas.drawLine(leftBarStartX, startY - 16, nowValue * valueStep + leftBarStartX, startY - 16, Paints.paints.get("ability_bar_yellow"));
        //绘制能力数值
        canvas.drawText(nowValue + "", 10* valueStep+leftBarStartX, startY, Paints.paints.get("ability_value"));
        //第二行第二列移动
        maxValue = career.getMaxMov();
        nowValue = actor.getMov();
        canvas.drawText("移动", rightNameStartX, startY, Paints.paints.get("ability_name"));
        canvas.drawLine(rightBarStartX - 4, startY  - 16,  maxValue * valueStep + rightBarStartX + 4, startY - 16, Paints.paints.get("ability_bar_border"));
        canvas.drawLine(rightBarStartX, startY - 16, maxValue * valueStep + rightBarStartX, startY - 16, Paints.paints.get("ability_bar_blank"));
        canvas.drawLine(rightBarStartX, startY - 16, nowValue * valueStep + rightBarStartX, startY - 16, Paints.paints.get("ability_bar_yellow"));
        canvas.drawText(nowValue + "", 10* valueStep+rightBarStartX, startY, Paints.paints.get("ability_value"));


        //第三行第一列魔力
        startY += rowStep;
        maxValue = career.getMaxMag();
        nowValue = actor.getMag();
        canvas.drawText("魔力", leftNameStartX, startY, Paints.paints.get("ability_name"));
        canvas.drawLine(leftBarStartX - 4, startY -16, maxValue * valueStep + leftBarStartX + 4, startY - 16, Paints.paints.get("ability_bar_border"));
        canvas.drawLine(leftBarStartX, startY - 16, maxValue * valueStep + leftBarStartX, startY - 16, Paints.paints.get("ability_bar_blank"));
        canvas.drawLine(leftBarStartX, startY - 16, nowValue * valueStep + leftBarStartX, startY - 16, Paints.paints.get("ability_bar_yellow"));
        canvas.drawText(nowValue + "", 10* valueStep+leftBarStartX, startY, Paints.paints.get("ability_value"));
        //第三行第二列体格
        maxValue = career.getMaxCon();
        nowValue = actor.getCon();
        canvas.drawText("体格", rightNameStartX, startY, Paints.paints.get("ability_name"));
        canvas.drawLine(rightBarStartX - 4, startY  - 16,  maxValue * valueStep + rightBarStartX + 4, startY - 16, Paints.paints.get("ability_bar_border"));
        canvas.drawLine(rightBarStartX, startY - 16, maxValue * valueStep + rightBarStartX, startY - 16, Paints.paints.get("ability_bar_blank"));
        canvas.drawLine(rightBarStartX, startY - 16, nowValue * valueStep + rightBarStartX, startY - 16, Paints.paints.get("ability_bar_yellow"));
        canvas.drawText(nowValue + "", 10* valueStep+rightBarStartX, startY, Paints.paints.get("ability_value"));

        //第四行第一列技术
        startY += rowStep;
        maxValue = career.getMaxSkl();
        nowValue = actor.getSkl();
        canvas.drawText("技术", leftNameStartX, startY, Paints.paints.get("ability_name"));
        canvas.drawLine(leftBarStartX - 4, startY -16, maxValue * valueStep + leftBarStartX + 4, startY - 16, Paints.paints.get("ability_bar_border"));
        canvas.drawLine(leftBarStartX, startY - 16, maxValue * valueStep + leftBarStartX, startY - 16, Paints.paints.get("ability_bar_blank"));
        canvas.drawLine(leftBarStartX, startY - 16, nowValue * valueStep + leftBarStartX, startY - 16, Paints.paints.get("ability_bar_yellow"));
        canvas.drawText(nowValue + "", 10* valueStep+leftBarStartX, startY, Paints.paints.get("ability_value"));
        //第四行第二列坐骑
        nowValue = Careers.getCareer(actor.getCareerKey()).getMount();
        canvas.drawText("坐骑", rightNameStartX, startY, Paints.paints.get("ability_name"));
//        canvas.drawBitmap(mounts, new Rect(nowValue * 32, 0, nowValue * 32 + 32, 32), new Rect(rightBarStartX, startY-60, rightBarStartX + 100, startY + 40), paint);
        canvas.drawText(Values.MOUNT_NAME[nowValue], 10* valueStep+rightBarStartX, startY, Paints.paints.get("ability_value"));

        //第五行第一列速度
        startY += rowStep;
        maxValue = career.getMaxSpd();
        nowValue = actor.getSpd();
        canvas.drawText("速度", leftNameStartX, startY, Paints.paints.get("ability_name"));
        canvas.drawLine(leftBarStartX - 4, startY -16, maxValue * valueStep + leftBarStartX + 4, startY - 16, Paints.paints.get("ability_bar_border"));
        canvas.drawLine(leftBarStartX, startY - 16, maxValue * valueStep + leftBarStartX, startY - 16, Paints.paints.get("ability_bar_blank"));
        canvas.drawLine(leftBarStartX, startY - 16, nowValue * valueStep + leftBarStartX, startY - 16, Paints.paints.get("ability_bar_yellow"));
        canvas.drawText(nowValue + "", 10* valueStep+leftBarStartX, startY, Paints.paints.get("ability_value"));
        //第五行第二列救出
        nowValue = actor.getAid();
        canvas.drawText("救出", rightNameStartX, startY, Paints.paints.get("ability_name"));
        canvas.drawText(nowValue + "", 10* valueStep+rightBarStartX, startY, Paints.paints.get("ability_value"));


        //第六行第一列幸运
        startY += rowStep;
        maxValue = career.getMaxLuc();
        nowValue = actor.getLuc();
        canvas.drawText("幸运", leftNameStartX, startY, Paints.paints.get("ability_name"));
        canvas.drawLine(leftBarStartX - 4, startY -16, maxValue * valueStep + leftBarStartX + 4, startY - 16, Paints.paints.get("ability_bar_border"));
        canvas.drawLine(leftBarStartX, startY - 16, maxValue * valueStep + leftBarStartX, startY - 16, Paints.paints.get("ability_bar_blank"));
        canvas.drawLine(leftBarStartX, startY - 16, nowValue * valueStep + leftBarStartX, startY - 16, Paints.paints.get("ability_bar_yellow"));
        canvas.drawText(nowValue + "", 10* valueStep+leftBarStartX, startY, Paints.paints.get("ability_value"));
        //第六行第二列同行
        canvas.drawText("同行", rightNameStartX, startY, Paints.paints.get("ability_name"));


        //第七行第一列守备
        startY += rowStep;
        maxValue = career.getMaxDef();
        nowValue = actor.getDef();
        canvas.drawText("守备", leftNameStartX, startY, Paints.paints.get("ability_name"));
        canvas.drawLine(leftBarStartX - 4, startY -16, maxValue * valueStep + leftBarStartX + 4, startY - 16, Paints.paints.get("ability_bar_border"));
        canvas.drawLine(leftBarStartX, startY - 16, maxValue * valueStep + leftBarStartX, startY - 16, Paints.paints.get("ability_bar_blank"));
        canvas.drawLine(leftBarStartX, startY - 16, nowValue * valueStep + leftBarStartX, startY - 16, Paints.paints.get("ability_bar_yellow"));
        canvas.drawText(nowValue + "", 10* valueStep+leftBarStartX, startY, Paints.paints.get("ability_value"));
        //第七行第二列相性
        nowValue = actor.getAffin();
        canvas.drawText("相性", rightNameStartX, startY, Paints.paints.get("ability_name"));
//        canvas.drawBitmap(affins, new Rect(actor.getAffin() * 16, 0, actor.getAffin() * 16 + 16, 16), new Rect(rightBarStartX, startY - 60, rightBarStartX + 100, startY + 40), paint);
        canvas.drawText(Values.AFFIN_NAME[nowValue], 10* valueStep+rightBarStartX, startY, Paints.paints.get("ability_value"));

        //第八行第一列魔防
        startY += rowStep;
        maxValue = career.getMaxRes();
        nowValue = actor.getRes();
        canvas.drawText("魔防", leftNameStartX, startY, Paints.paints.get("ability_name"));
        canvas.drawLine(leftBarStartX - 4, startY -16, maxValue * valueStep + leftBarStartX + 4, startY - 16, Paints.paints.get("ability_bar_border"));
        canvas.drawLine(leftBarStartX, startY - 16, maxValue * valueStep + leftBarStartX, startY - 16, Paints.paints.get("ability_bar_blank"));
        canvas.drawLine(leftBarStartX, startY - 16, nowValue * valueStep + leftBarStartX, startY - 16, Paints.paints.get("ability_bar_yellow"));
        canvas.drawText(nowValue + "", 10* valueStep+leftBarStartX, startY, Paints.paints.get("ability_value"));
        //第八行第二列状态
        canvas.drawText("状态", rightNameStartX, startY, Paints.paints.get("ability_name"));
    }


    //显示物品和装备信息
    public void showItems(Canvas canvas, Paint paint) {
        int leftIconStartX = Values.SCREEN_WIDTH / 20;//左侧各种物品图标显示的起始坐标
        int leftNameStartX = leftIconStartX+100;//左侧文字显示的起始坐标
        int leftNameStopX = Values.SCREEN_WIDTH / 10 * 4;//左侧文字显示的终止坐标
        int rightNameStartX = leftNameStartX + Values.SCREEN_WIDTH / 2;//右侧文字显示的起始坐标
        int rightNameStopX = Values.SCREEN_WIDTH / 10 * 9;//右侧各种道具耐久显示的终止坐标

        int startY = basicDst.bottom;
        int rowStep = (Values.SCREEN_HEIGHT - basicDst.bottom) / 10;

        //第一行标题
        startY += rowStep;
        canvas.drawText("武器道具", Values.SCREEN_WIDTH/2, startY, Paints.paints.get("page_title"));

        for (Item item : actor.getItems()) {
            startY += rowStep;
            canvas.drawBitmap(item.getIcon(), new Rect(0, 0, Values.RES_ITEM_WIDTH, Values.RES_ITEM_HEIGHT), new Rect(leftIconStartX, startY - 60, leftIconStartX + 100, startY + 40), Paints.paints.get("normal"));
            paint = Paints.paints.get("item_name_white");
            switch (item.getType()){
                case Values.ITEM_TYPE_SWORD:
                    if (item.getLv()>actor.getExpSwd())
                        paint = Paints.paints.get("item_name_grey");
                    break;
                case Values.ITEM_TYPE_LANCE:
                    if (item.getLv()>actor.getExpLan())
                        paint = Paints.paints.get("item_name_grey");
                    break;
                case Values.ITEM_TYPE_AXE:
                    if (item.getLv()>actor.getExpAxe())
                        paint = Paints.paints.get("item_name_grey");
                    break;
                case Values.ITEM_TYPE_BOW:
                    if (item.getLv()>actor.getExpBow())
                        paint = Paints.paints.get("item_name_grey");
                    break;
                case Values.ITEM_TYPE_STAFF:
                    if (item.getLv()>actor.getExpStf())
                        paint = Paints.paints.get("item_name_grey");
                    break;
                case Values.ITEM_TYPE_ANIMA:
                    if (item.getLv()>actor.getExpAnm())
                        paint = Paints.paints.get("item_name_grey");
                    break;
                case Values.ITEM_TYPE_LIGHT:
                    if (item.getLv()>actor.getExpLgt())
                        paint = Paints.paints.get("item_name_grey");
                    break;
                case Values.ITEM_TYPE_DARK:
                    if (item.getLv()>actor.getExpDrk())
                        paint = Paints.paints.get("item_name_grey");
                    break;
                case Values.ITEM_TYPE_OTHERS:
//                        paint = Paints.paints.get("item_name_grey");
                    break;
            }
            canvas.drawText(item.getName(), leftNameStartX, startY, paint);
            canvas.drawText(item.getUses()+"", rightNameStopX, startY, Paints.paints.get("item_uses_blue"));
        }

        startY = basicDst.bottom + 7 * rowStep;

        canvas.drawText("射程", leftNameStartX, startY, Paints.paints.get("ability_name"));
        canvas.drawText(""+actor.getRng(), leftNameStopX, startY, Paints.paints.get("item_uses_blue"));
        canvas.drawText("命中", rightNameStartX, startY, Paints.paints.get("ability_name"));
        canvas.drawText(""+actor.getHit(), rightNameStopX, startY, Paints.paints.get("item_uses_blue"));
        startY += rowStep;
        canvas.drawText("攻击", leftNameStartX, startY, Paints.paints.get("ability_name"));
        canvas.drawText(""+actor.getAtk(), leftNameStopX, startY, Paints.paints.get("item_uses_blue"));
        canvas.drawText("回避", rightNameStartX, startY, Paints.paints.get("ability_name"));
        canvas.drawText(""+actor.getAvd(), rightNameStopX, startY, Paints.paints.get("item_uses_blue"));
        startY += rowStep;
        canvas.drawText("魔攻", leftNameStartX, startY, Paints.paints.get("ability_name"));
        canvas.drawText(""+actor.getMat(), leftNameStopX, startY, Paints.paints.get("item_uses_blue"));
        canvas.drawText("必杀", rightNameStartX, startY, Paints.paints.get("ability_name"));
        canvas.drawText(""+actor.getCrt(), rightNameStopX, startY, Paints.paints.get("item_uses_blue"));
    }

    public void showLevel(Canvas canvas, Paint paint) {

        int leftIconStartX = Values.SCREEN_WIDTH / 20;//左侧各种武器图标显示的起始坐标
        int leftNameStartX = leftIconStartX+100;//左侧各种武器名称显示的起始坐标
        int leftBarStartX = leftNameStartX + (int) Paints.paints.get("ability_name").measureText("剑 ");//左侧各种武器经验条显示的起始坐标
        int leftBarStopX = Values.SCREEN_WIDTH/2 - leftIconStartX;//左侧各种武器经验条显示的终止坐标
        int rightIconStartX = leftIconStartX + Values.SCREEN_WIDTH / 2;//右侧各种武器图标显示的起始坐标
        int rightNameStartX = leftNameStartX + Values.SCREEN_WIDTH / 2;//右侧各种武器名称显示的起始坐标
        int rightBarStartX = leftBarStartX + Values.SCREEN_WIDTH / 2;//右侧各种武器名称显示的起始坐标
        int rightBarStopX = Values.SCREEN_WIDTH - leftIconStartX;//右侧各种武器经验条显示的终止坐标

        int startY = basicDst.bottom;
        int rowStep = (Values.SCREEN_HEIGHT - basicDst.bottom) / 10;

        //第一行标题
        startY += rowStep;
        canvas.drawText("熟练等级", Values.SCREEN_WIDTH/2, startY, Paints.paints.get("page_title"));


        int barLength = leftBarStopX - leftBarStartX;
        int exp;
        int next;
        int rest;
        String level;


        //第二行第一列剑
        startY += rowStep;
        exp = actor.getExpSwd();
        next = getNext(exp);//武器熟练度到下一级需要的经验
        rest = getRest(exp);//武器熟练度当前级多出的经验
        level = getLevel(exp);//武器熟练度当前等级
        canvas.drawBitmap(weapons, new Rect(0 * 32, 0, 0 * 32 + 32, 32), new Rect(leftIconStartX, startY - 60, leftIconStartX + 100, startY + 40), paint);
        canvas.drawText("剑", leftNameStartX, startY, Paints.paints.get("weapon_type"));
        canvas.drawLine(leftBarStartX - 4, startY - 16, leftBarStopX + 4, startY - 16, Paints.paints.get("ability_bar_border"));
        canvas.drawLine(leftBarStartX, startY - 16, leftBarStopX, startY - 16, Paints.paints.get("ability_bar_blank"));
        canvas.drawLine(leftBarStartX, startY - 16, leftBarStartX + (float) rest / next * barLength, startY - 16, Paints.paints.get("ability_bar_yellow"));
        canvas.drawText(level, leftBarStartX + barLength/2, startY, Paints.paints.get("weapon_level_blue"));

        //第二行第二列杖
        exp = actor.getExpStf();
        next = getNext(exp);
        rest = getRest(exp);
        level = getLevel(exp);
        canvas.drawBitmap(weapons, new Rect(4 * 32, 0, 4 * 32 + 32, 32), new Rect(rightIconStartX, startY - 60, rightIconStartX + 100, startY + 40), paint);
        canvas.drawText("杖", rightNameStartX, startY, Paints.paints.get("weapon_type"));
        canvas.drawLine(rightBarStartX - 4, startY - 16, rightBarStopX + 4, startY - 16, Paints.paints.get("ability_bar_border"));
        canvas.drawLine(rightBarStartX, startY - 16, rightBarStopX, startY - 16, Paints.paints.get("ability_bar_blank"));
        canvas.drawLine(rightBarStartX, startY - 16, rightBarStartX + (float) rest / next * barLength, startY - 16, Paints.paints.get("ability_bar_yellow"));
        canvas.drawText(level, rightBarStartX + barLength/2, startY, Paints.paints.get("weapon_level_blue"));


        //第三行第一列枪
        startY += rowStep;
        exp = actor.getExpLan();
        next = getNext(exp);//武器熟练度到下一级需要的经验
        rest = getRest(exp);//武器熟练度当前级多出的经验
        level = getLevel(exp);//武器熟练度当前等级
        canvas.drawBitmap(weapons, new Rect(1 * 32, 0, 1 * 32 + 32, 32), new Rect(leftIconStartX, startY - 60, leftIconStartX + 100, startY + 40), paint);
        canvas.drawText("枪", leftNameStartX, startY, Paints.paints.get("weapon_type"));
        canvas.drawLine(leftBarStartX - 4, startY - 16, leftBarStopX + 4, startY - 16, Paints.paints.get("ability_bar_border"));
        canvas.drawLine(leftBarStartX, startY - 16, leftBarStopX, startY - 16, Paints.paints.get("ability_bar_blank"));
        canvas.drawLine(leftBarStartX, startY - 16, leftBarStartX + (float) rest / next * barLength, startY - 16, Paints.paints.get("ability_bar_yellow"));
        canvas.drawText(level, leftBarStartX + barLength/2, startY, Paints.paints.get("weapon_level_blue"));

        //第三行第二列理
        exp = actor.getExpAnm();
        next = getNext(exp);
        rest = getRest(exp);
        level = getLevel(exp);
        canvas.drawBitmap(weapons, new Rect(5 * 32, 0, 5 * 32 + 32, 32), new Rect(rightIconStartX, startY - 60, rightIconStartX + 100, startY + 40), paint);
        canvas.drawText("理", rightNameStartX, startY, Paints.paints.get("weapon_type"));
        canvas.drawLine(rightBarStartX - 4, startY - 16, rightBarStopX + 4, startY - 16, Paints.paints.get("ability_bar_border"));
        canvas.drawLine(rightBarStartX, startY - 16, rightBarStopX, startY - 16, Paints.paints.get("ability_bar_blank"));
        canvas.drawLine(rightBarStartX, startY - 16, rightBarStartX + (float) rest / next * barLength, startY - 16, Paints.paints.get("ability_bar_yellow"));
        canvas.drawText(level, rightBarStartX + barLength/2, startY, Paints.paints.get("weapon_level_blue"));


        //第四行第一列斧
        startY += rowStep;
        exp = actor.getExpAxe();
        next = getNext(exp);//武器熟练度到下一级需要的经验
        rest = getRest(exp);//武器熟练度当前级多出的经验
        level = getLevel(exp);//武器熟练度当前等级
        canvas.drawBitmap(weapons, new Rect(2 * 32, 0, 2 * 32 + 32, 32), new Rect(leftIconStartX, startY - 60, leftIconStartX + 100, startY + 40), paint);
        canvas.drawText("斧", leftNameStartX, startY, Paints.paints.get("weapon_type"));
        canvas.drawLine(leftBarStartX - 4, startY - 16, leftBarStopX + 4, startY - 16, Paints.paints.get("ability_bar_border"));
        canvas.drawLine(leftBarStartX, startY - 16, leftBarStopX, startY - 16, Paints.paints.get("ability_bar_blank"));
        canvas.drawLine(leftBarStartX, startY - 16, leftBarStartX + (float) rest / next * barLength, startY - 16, Paints.paints.get("ability_bar_yellow"));
        canvas.drawText(level, leftBarStartX + barLength/2, startY, Paints.paints.get("weapon_level_blue"));

        //第四行第二列光
        exp = actor.getExpLgt();
        next = getNext(exp);
        rest = getRest(exp);
        level = getLevel(exp);
        canvas.drawBitmap(weapons, new Rect(6 * 32, 0, 6 * 32 + 32, 32), new Rect(rightIconStartX, startY - 60, rightIconStartX + 100, startY + 40), paint);
        canvas.drawText("光", rightNameStartX, startY, Paints.paints.get("weapon_type"));
        canvas.drawLine(rightBarStartX - 4, startY - 16, rightBarStopX + 4, startY - 16, Paints.paints.get("ability_bar_border"));
        canvas.drawLine(rightBarStartX, startY - 16, rightBarStopX, startY - 16, Paints.paints.get("ability_bar_blank"));
        canvas.drawLine(rightBarStartX, startY - 16, rightBarStartX + (float) rest / next * barLength, startY - 16, Paints.paints.get("ability_bar_yellow"));
        canvas.drawText(level, rightBarStartX + barLength/2, startY, Paints.paints.get("weapon_level_blue"));


        //第五行第一列弓
        startY += rowStep;
        exp = actor.getExpBow();
        next = getNext(exp);//武器熟练度到下一级需要的经验
        rest = getRest(exp);//武器熟练度当前级多出的经验
        level = getLevel(exp);//武器熟练度当前等级
        canvas.drawBitmap(weapons, new Rect(3 * 32, 0, 3 * 32 + 32, 32), new Rect(leftIconStartX, startY - 60, leftIconStartX + 100, startY + 40), paint);
        canvas.drawText("弓", leftNameStartX, startY, Paints.paints.get("weapon_type"));
        canvas.drawLine(leftBarStartX - 4, startY - 16, leftBarStopX + 4, startY - 16, Paints.paints.get("ability_bar_border"));
        canvas.drawLine(leftBarStartX, startY - 16, leftBarStopX, startY - 16, Paints.paints.get("ability_bar_blank"));
        canvas.drawLine(leftBarStartX, startY - 16, leftBarStartX + (float) rest / next * barLength, startY - 16, Paints.paints.get("ability_bar_yellow"));
        canvas.drawText(level, leftBarStartX + barLength/2, startY, Paints.paints.get("weapon_level_blue"));

        //第五行第二列暗
        exp = actor.getExpDrk();
        next = getNext(exp);
        rest = getRest(exp);
        level = getLevel(exp);
        canvas.drawBitmap(weapons, new Rect(7 * 32, 0, 7 * 32 + 32, 32), new Rect(rightIconStartX, startY - 60, rightIconStartX + 100, startY + 40), paint);
        canvas.drawText("暗", rightNameStartX, startY, Paints.paints.get("weapon_type"));
        canvas.drawLine(rightBarStartX - 4, startY - 16, rightBarStopX + 4, startY - 16, Paints.paints.get("ability_bar_border"));
        canvas.drawLine(rightBarStartX, startY - 16, rightBarStopX, startY - 16, Paints.paints.get("ability_bar_blank"));
        canvas.drawLine(rightBarStartX, startY - 16, rightBarStartX + (float) rest / next * barLength, startY - 16, Paints.paints.get("ability_bar_yellow"));
        canvas.drawText(level, rightBarStartX + barLength/2, startY, Paints.paints.get("weapon_level_blue"));

    }

    public void turnRight() {
        nowPage = (nowPage + 1) % allPage;
    }

    public void turnLeft() {
        nowPage = (nowPage - 1 < 0) ? 2 : nowPage - 1;
    }

    public static int getNext(int exp){
        if (exp>=Values.WEAPON_LEVEL_S)
            return 0;
        else if (exp >= Values.WEAPON_LEVEL_A)
            return Values.WEAPON_A_TO_S;
        else if (exp>=Values.WEAPON_LEVEL_B)
            return Values.WEAPON_B_TO_A;
        else if (exp>=Values.WEAPON_LEVEL_C)
            return Values.WEAPON_C_TO_B;
        else if (exp>=Values.WEAPON_LEVEL_D)
            return Values.WEAPON_D_TO_C;
        else if (exp>=Values.WEAPON_LEVEL_E)
            return Values.WEAPON_E_TO_D;
        else
            return 0;
    }

    public static int getRest(int exp){
        if (exp >= Values.WEAPON_LEVEL_S)
            return exp - Values.WEAPON_LEVEL_S;
        else if (exp >= Values.WEAPON_LEVEL_A)
            return exp - Values.WEAPON_LEVEL_A;
        else if (exp >= Values.WEAPON_LEVEL_B)
            return exp - Values.WEAPON_LEVEL_B ;
        else if (exp >= Values.WEAPON_LEVEL_C)
            return exp - Values.WEAPON_LEVEL_C;
        else if (exp >= Values.WEAPON_LEVEL_D)
            return exp - Values.WEAPON_LEVEL_D;
        else if (exp >= Values.WEAPON_LEVEL_E)
            return exp - Values.WEAPON_LEVEL_E;
        else
            return 0;
    }

    public static String getLevel(int exp){
        if (exp>=Values.WEAPON_LEVEL_SS)
            return Values.WEAPON_SS;
        else if (exp>=Values.WEAPON_LEVEL_S)
            return Values.WEAPON_S;
        else if (exp >= Values.WEAPON_LEVEL_A)
            return Values.WEAPON_A;
        else if (exp>=Values.WEAPON_LEVEL_B)
            return Values.WEAPON_B;
        else if (exp>=Values.WEAPON_LEVEL_C)
            return Values.WEAPON_C;
        else if (exp>=Values.WEAPON_LEVEL_D)
            return Values.WEAPON_D;
        else if (exp>=Values.WEAPON_LEVEL_E)
            return Values.WEAPON_E;
        else
            return Values.WEAPON__;
    }


    public Actor getActor() {
        return actor;
    }

    public void setActor(Actor actor) {
        this.actor = actor;
        if (actor!=null) {
            setCareer(Careers.getCareer(actor.getCareerKey()));
        }
    }

    public Career getCareer() {
        return career;
    }

    public void setCareer(Career career) {
        this.career = career;
    }
}
