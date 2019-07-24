package io.ucia0xff.fe.util;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;

import java.util.ArrayList;

import io.ucia0xff.fe.R;
import io.ucia0xff.fe.Values;
import io.ucia0xff.fe.actor.Actor;
import io.ucia0xff.fe.actor.Actors;
import io.ucia0xff.fe.anim.Anim;
import io.ucia0xff.fe.career.Careers;
import io.ucia0xff.fe.item.Item;
import io.ucia0xff.fe.map.Map;
import io.ucia0xff.fe.map.TerrainInfo;

public class RangeHelper {
    private Map map;
    private Actor srcActor;
    private Actor dstActor;
    private Node startNode;
    private Node nowNode;
    private Node newNode;
    private NodeList moveRange;//移动范围
    private NodeList movePath;//移动路径
    private NodeList attackRange;//攻击范围
    private NodeList attackRangeAt;//在一个坐标的攻击范围
    private Bitmap block;
    private Bitmap blockMove;//可移动范围的色块
    private Bitmap blockStaff;//可攻击范围的色块
    private Bitmap blockAttack;//杖使用范围的色块
    private Rect src;
    private Rect dst;
    private static int[][] DIRECTION = {//探索方向
            {0, -1},//上
            {1, 0},//右
            {0, 1},//下
            {-1, 0}//左
    };

    public RangeHelper() {
        moveRange = new NodeList();
        movePath = new NodeList();
        attackRange = new NodeList();
        attackRangeAt = new NodeList();

        blockMove = Anim.readBitmap(R.drawable.block_move);
        blockStaff = Anim.readBitmap(R.drawable.block_staff);
        blockAttack = Anim.readBitmap(R.drawable.block_attack);

        src = new Rect(0, 0, blockMove.getWidth(), blockMove.getHeight());
        dst = new Rect(0, 0, Values.MAP_TILE_WIDTH, Values.MAP_TILE_HEIGHT);
    }

    public RangeHelper(Map map) {
        this();
        this.setMap(map);
    }


    //绘制移动范围
    public void drawMoveRange(Canvas canvas, Paint paint, int[] xyOffset) {
        if (canvas == null) return;
        int[] xyTile;
        for (Node node:moveRange) {
            xyTile = node.getXy();
            dst.offsetTo(xyTile[0] * Values.MAP_TILE_WIDTH + xyOffset[0],xyTile[1] * Values.MAP_TILE_HEIGHT + xyOffset[1]);
            canvas.drawBitmap(blockMove, src, dst, paint);
        }
    }

    //绘制攻击范围
    public void drawAttackRange(Canvas canvas, Paint paint, int[] xyOffset) {
        if (canvas == null) return;
        int[] xyTile;
        for (Node node:attackRange) {
            if (moveRange.contains(node))//攻击范围与移动范围重合，只绘制移动范围
                continue;
            xyTile = node.getXy();
            dst.offsetTo(xyTile[0] * Values.MAP_TILE_WIDTH + xyOffset[0],xyTile[1] * Values.MAP_TILE_HEIGHT + xyOffset[1]);
            canvas.drawBitmap(blockAttack, src, dst, paint);
        }
    }

    //绘制在一个坐标的攻击范围
    public void drawAttackRangeAt(Canvas canvas, Paint paint, int[] xyOffset) {
        if (canvas == null) return;
        int[] xyTile;
        for (Node node:attackRangeAt) {
            xyTile = node.getXy();
            dst.offsetTo(xyTile[0] * Values.MAP_TILE_WIDTH + xyOffset[0],xyTile[1] * Values.MAP_TILE_HEIGHT + xyOffset[1]);
            canvas.drawBitmap(blockAttack, src, dst, paint);
        }
    }

    public Map getMap() {
        return map;
    }

    public void setMap(Map map) {
        this.map = map;
    }

    public Actor getSrcActor() {
        return srcActor;
    }

    public void setSrcActor(Actor srcActor) {
        this.srcActor = srcActor;
        this.setMoveRange();
        this.setAttackRange();
    }

    public Actor getDstActor() {
        return dstActor;
    }

    public void setDstActor(Actor dstActor) {
        this.dstActor = dstActor;
    }

    public Node getStartNode() {
        return startNode;
    }

    public void setStartNode(Node startNode) {
        this.startNode = startNode;
    }

    public Node getNowNode() {
        return nowNode;
    }

    public void setNowNode(Node nowNode) {
        this.nowNode = nowNode;
    }

    public Node getNewNode() {
        return newNode;
    }

    public void setNewNode(Node newNode) {
        this.newNode = newNode;
    }

    //获取角色在原地的攻击范围
    public NodeList getAttackRange(Actor actor) {
        NodeList maxAttackRange = new NodeList();
        NodeList attackRange = new NodeList();
        Item weapon = actor.getEquipedWeapon();
        if (weapon == null)
            return null;
        int[] range = weapon.getRange();
        int[] srcXY = actor.getXyInMapTile();
        int[] nowXY;
        int[] newXY = new int[2];
        maxAttackRange.add(new Node(srcXY));
        for (int i = 1; i <= range[1]; i++) {//一圈一圈往外遍历，得到最大攻击范围
            for (int j = 0; j < maxAttackRange.size(); j++) {
                nowXY = maxAttackRange.get(j).getXy();
                for (int k = 0; k < DIRECTION.length; k++) {//遍历该节点的4个方向得到新节点的坐标
                    newXY[0] = nowXY[0] + DIRECTION[k][0];
                    newXY[1] = nowXY[1] + DIRECTION[k][1];
                    if (newXY[0] < 0 || newXY[1] < 0 ||
                            newXY[0] >= map.getMapWidthTileCount() ||
                            newXY[1] >= map.getMapHeightTileCount())//新坐标超出地图边界则不处理
                        continue;
                    else if (maxAttackRange.indexOf(newXY) != -1)//新坐标已在列表中则不处理
                        continue;
                    else if ((Math.abs(newXY[0] - srcXY[0]) + Math.abs(newXY[1] - srcXY[1])) == i) {//将新坐标加入列表
                        newNode = new Node(newXY);
                        newNode.distance = i;//新坐标与起始位置的距离
                        maxAttackRange.add(newNode);
                    }
                }
            }
        }
        for (Node node : maxAttackRange) {
            if (range[0] <= node.distance && node.distance <= range[1]) {
                attackRange.add(node);
//                Log.d("AttackRange", node.getXy()[0]+","+node.getXy()[1]+":"+node.distance);
            }
        }
        return attackRange;
    }


    //设置在一个坐标的攻击范围
    public void setAttackRangeAt(int[] xy) {
        NodeList maxAttackRange = new NodeList();//最大攻击范围
        attackRangeAt.clear();
        Item weapon = srcActor.getEquipedWeapon();
        if (weapon == null)
            return;
        int[] range = weapon.getRange();
        maxAttackRange.add(new Node(xy));
        int[] nowXY;
        int[] newXY = new int[2];
        for (int i = 1; i <= range[1]; i++) {//一圈一圈往外遍历，得到最大攻击范围
            for (int j = 0; j < maxAttackRange.size(); j++) {
                nowXY = maxAttackRange.get(j).getXy();
                for (int k = 0; k < DIRECTION.length; k++) {//遍历该节点的4个方向得到新节点的坐标
                    newXY[0] = nowXY[0] + DIRECTION[k][0];
                    newXY[1] = nowXY[1] + DIRECTION[k][1];
                    if (newXY[0] < 0 || newXY[1] < 0 ||
                            newXY[0] >= map.getMapWidthTileCount() ||
                            newXY[1] >= map.getMapHeightTileCount())//新坐标超出地图边界则不处理
                        continue;
                    else if (maxAttackRange.contains(newXY))//新坐标已在列表中则不处理
                        continue;
                    else if ((Math.abs(newXY[0] - xy[0]) + Math.abs(newXY[1] - xy[1])) == i) {//新坐标与起始位置的距离等于外循环次数
                        newNode = new Node(newXY);
                        newNode.distance = i;//新坐标与起始位置的距离
                        maxAttackRange.add(newNode);//将新坐标加入最大攻击范围列表
                    }
                }
            }
        }
        for (Node node : maxAttackRange) {//将在实际攻击范围内的节点加入列表
            if (range[0] <= node.distance && node.distance <= range[1])
                attackRangeAt.add(node);
        }
    }

    //设置攻击范围
    public void setAttackRange() {
        attackRange.clear();
        Item weapon = srcActor.getEquipedWeapon();
        if (weapon == null)
            return;
        if (moveRange.isEmpty())
            setMoveRange();
        for (Node move : moveRange) {//求移动范围内每个坐标的攻击范围，将结果加入列表
            setAttackRangeAt(move.getXy());
            for (Node node : attackRangeAt){
                if (!attackRange.contains(node))
                    attackRange.add(node);
            }
        }
    }


    public NodeList getMoveRange() {
        return moveRange;
    }

    //设置移动范围
    public void setMoveRange() {
        moveRange.clear();
        startNode = new Node(srcActor.getXyInMapTile());
        moveRange.add(startNode);
        int[] nowXY;
        int[] newXY = new int[2];
        for (int i = 0; i < moveRange.size(); i++) {//遍历可移动范围里的每个节点
            nowNode = moveRange.get(i);
            nowXY = nowNode.getXy();
            for (int j = 0; j < DIRECTION.length; j++) {//遍历该节点的4个方向得到新节点的坐标
                newXY[0] = nowXY[0] + DIRECTION[j][0];
                newXY[1] = nowXY[1] + DIRECTION[j][1];
                if (newXY[0] < 0 || newXY[1] < 0 ||
                        newXY[0] >= map.getMapWidthTileCount() ||
                        newXY[1] >= map.getMapHeightTileCount())//新坐标超出地图边界则不处理
                    continue;
                else {
                    dstActor = Actors.getActor(newXY);//则判断该坐标上有没有角色存在
                    if (dstActor != null) {
                        switch (srcActor.getParty()) {//如果有，则判断两者的阵营关系
                            case Values.PARTY_ALLY:
                            case Values.PARTY_PLAYER:
                                if (dstActor.getParty() == Values.PARTY_ENEMY)//互为敌对阵营,不能通过该节点
                                    continue;
                                break;
                            case Values.PARTY_ENEMY:
                                if (dstActor.getParty() != Values.PARTY_ENEMY)//互为敌对阵营,不能通过该节点
                                    continue;
                                break;
                        }
                    } //该坐标上没有角色，或是友好阵营的角色，可以通过
                    newNode = new Node(newXY);
                    newNode.setParent(nowNode);
                    newNode.setG(TerrainInfo.MOVE_COST[Careers.getCareer(srcActor.getCareerKey()).getType()][map.getTerrain(newXY)] + nowNode.getG());//从起点到新节点的移动力消耗
                    if (newNode.getG() <= srcActor.getMov()) {//可以到达该节点
                        moveRange.add(newNode);//该节点上没有角色，或者有角色但是友好阵营，并且可以到达该节点，将该节点加入移动范围列表
                    }
                }
            }
        }
    }

    public NodeList getMovePath() {
        return movePath;
    }

    public boolean canMoveTo(int[] xy) {
        return moveRange.contains(xy);
    }

    //设置移动路径
    public void setMovePath(int[] xyTile) {
        movePath.clear();
        int index = moveRange.indexOf(xyTile);
        nowNode = moveRange.get(index);
        do {
            movePath.add(0, nowNode);
            nowNode = nowNode.getParent();
        } while (nowNode != null);
    }

    //内部类——节点
    public class Node implements Comparable<Node> {
        private int[] xy;//当前格子的位置
        private int distance;//与起始坐标的距离
        private int f;//g和h的和
        private int g;//从起始格子到当前格子需要的移动力
        private int h;//从当前格子到终点格子的预估移动力
        private Node parent;//路径上的父格子

        public Node(int[] xy) {
            this.xy = new int[2];
            this.xy[0] = xy[0];
            this.xy[1] = xy[1];
            this.distance = 0;
            this.f = 0;
            this.g = 0;
            this.h = 0;
            this.parent = null;
        }

        public boolean equals(Node node) {
            return (this.xy[0] == node.getXy()[0]) && (this.xy[1] == node.getXy()[1]);
        }

        public boolean equals(int[] xy) {
            return (this.xy[0] == xy[0]) && (this.xy[1] == xy[1]);
        }

        @Override
        public int compareTo(Node node) {
            int result = this.f - node.f;
            if (result == 0) {
                result = this.g - node.g;
            }
            return result;
        }

        public int[] getXy() {
            return xy;
        }

        public void setXy(int[] xy) {
            this.xy = xy;
        }

        public int getF() {
            return f;
        }

        public void setF(int f) {
            this.f = f;
        }

        public int getG() {
            return g;
        }

        public void setG(int g) {
            this.g = g;
        }

        public int getH() {
            return h;
        }

        public void setH(int h) {
            this.h = h;
        }

        public Node getParent() {
            return parent;
        }

        public void setParent(Node parent) {
            this.parent = parent;
        }
    }

    //内部类——节点链表
    public static class NodeList extends ArrayList<Node> {
        public boolean add(Node node) {
            if (size() == 0)
                super.add(node);
            else {
                int index = indexOf(node);
                if (index != -1) {//该格子已遍历过
                    if (node.getG() < get(index).getG()) {//如果新的路线更近，则更新该格子
                        remove(index);
                        super.add(node);
                        return true;
                    }
                    return false;
                } else super.add(node);
            }
            return true;
        }

        public int indexOf(int[] xyTile) {
            for (int i = size() - 1; i >= 0; i--) {
                Node tmp = get(i);
                if (tmp.xy[0] == xyTile[0] && tmp.xy[1] == xyTile[1]) {
                    return i;
                }
            }
            return -1;
        }

        public int indexOf(Node node) {
            for (int i = size() - 1; i >= 0; i--) {
                Node tmp = get(i);
                if (node.equals(tmp)) {
                    return i;
                }
            }
            return -1;
        }

        public boolean contains(int[] xyTile) {
            for (int i = size() - 1; i >= 0; i--) {
                Node tmp = get(i);
                if (tmp.equals(xyTile)) {
                    return true;
                }
            }
            return false;
        }

        public boolean contains(Node node) {
            for (int i = size() - 1; i >= 0; i--) {
                Node tmp = get(i);
                if (tmp.equals(node)) {
                    return true;
                }
            }
            return false;
        }

        public void insert(Node node) {
            for (int i = size(); i > 0; i--) {
                Node n = get(i - 1);
                if (node.compareTo(n) >= 0) {
                    add(i, node);
                    return;
                }
            }
            add(0, node);
        }
    }
}
