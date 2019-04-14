package io.ucia0xff.fe.actor;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;

import java.util.ArrayList;

import io.ucia0xff.fe.R;
import io.ucia0xff.fe.Values;
import io.ucia0xff.fe.anim.Anim;
import io.ucia0xff.fe.career.Careers;
import io.ucia0xff.fe.map.Map;
import io.ucia0xff.fe.map.Terrain;

public class ActorMoveHelper {
    private Map map;
    private Actor srcActor;
    private Actor dstActor;
    private Node startNode;
    private Node nowNode;
    private Node newNode;
    private NodeList moveRange;//可移动范围
    private NodeList movePath;//移动路径
    private Bitmap block;
    private Bitmap blockMove;//可移动范围的色块
    private Bitmap blockStaff;//可攻击范围的色块
    private Bitmap blockAttack;//杖使用范围的色块
    private Rect src;
    private Rect dst;
    private static int[][] DIRECTION = {//移动方向
            {0, -1},//上
            {1, 0},//右
            {0, 1},//下
            {-1, 0}//左
    };

    public ActorMoveHelper(Map map){
        this.map = map;
        moveRange = new NodeList();
        movePath = new NodeList();
        blockMove = Anim.readBitMap(R.drawable.block_move);
        blockStaff = Anim.readBitMap(R.drawable.block_staff);
        blockAttack = Anim.readBitMap(R.drawable.block_attack);
        src = new Rect(0, 0, blockMove.getWidth(), blockMove.getHeight());
        dst = new Rect(0, 0, Values.MAP_TILE_WIDTH, Values.MAP_TILE_HEIGHT);
    }
    public ActorMoveHelper(Actor srcActor, Map map){
        this(map);
        this.setSrcActor(srcActor);
    }

    //绘制移动范围
    public void drawMoveRange(Canvas canvas, Paint paint, int[] xyOffset) {
        if (canvas==null) return;
        int[] xyTile;
        for (int i = 0; i < moveRange.size(); i++) {
            xyTile = moveRange.get(i).getXy();
            dst.offsetTo(xyTile[0] * Values.MAP_TILE_WIDTH + xyOffset[0],
                    xyTile[1] * Values.MAP_TILE_HEIGHT + xyOffset[1]);
            drawBlock(canvas, paint, 0);
        }
    }

    //绘制一个格子
    private void drawBlock(Canvas canvas, Paint paint, int type) {
        switch (type){
            case 0:
                block = blockMove;
                break;
            case 1:
                block = blockAttack;
                break;
            case 2:
                block = blockStaff;
                break;
        }
        canvas.drawBitmap(block, src, dst, paint);
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
        startNode = new Node(srcActor.getXyInMapTile());
        startNode.setMoveCost(0);
        this.setMoveRange();
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

    public NodeList getMoveRange() {
        return moveRange;
    }

    //设置可移动范围
    public void setMoveRange() {
        moveRange.clear();
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
                    newNode = new Node(newXY);
                    newNode.setParent(nowNode);
                    newNode.setMoveCost(newNode.getMoveCost() + nowNode.getMoveCost());//从起点移动到新节点的移动力消耗
                    if (newNode.getMoveCost() <= srcActor.getMov()) {//如果该节点在距离上可达
                        if ((dstActor = Actors.getActor(newXY))!=null) {//则判断该节点上有没有角色存在
                            switch (srcActor.getParty()){//如果有，则判断两者的阵营关系
                                case Values.PARTY_ALLY:
                                case Values.PARTY_PLAYER:
                                    if (dstActor.getParty()==Values.PARTY_ENEMY)//互为敌对阵营,不能通过该节点
                                        continue;
                                    break;
                                case Values.PARTY_ENEMY:
                                    if (dstActor.getParty()!=Values.PARTY_ENEMY)//互为敌对阵营,不能通过该节点
                                        continue;
                                    break;
                            }
                        }
                        moveRange.add(newNode);//该节点上没有角色，或者有角色但是友好阵营，则可通过该节点，将该节点加入列表
                    }
                }
            }
        }
    }

    public NodeList getMovePath() {
        return movePath;
    }

    public boolean canMoveTo(int[] xy){
        return moveRange.indexOf(xy)!=-1;
    }

    //设置移动路径
    public void setMovePath(int[] xyTile) {
        movePath.clear();
        int index = moveRange.indexOf(new Node(xyTile));
        nowNode = moveRange.get(index);
        while (nowNode != startNode) {
            movePath.add(0, nowNode);
            nowNode = nowNode.getParent();
        }
        movePath.add(0, startNode);
    }

    //内部类——节点
    public class Node implements Comparable<Node> {
        private int[] xy;//当前格子的位置
        private int moveCost;//当前格子的移动力消耗

        private int f;//g和h的和
        private int g;//从起始格子到当前格子需要的移动力
        private int h;//从当前格子到终点格子的预估移动力
        private Node parent;//路径上的父格子

        public Node(int[] xy) {
            this.xy = new int[2];
            this.xy[0] = xy[0];
            this.xy[1] = xy[1];
            this.moveCost = Terrain.MOVE_COST[Careers.getCareer(srcActor.getCareerKey()).getMoveType()][map.getTerrain(this.xy)];
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
                result = this.h - node.h;
            }
            return result;
        }

        public int[] getXy() {
            return xy;
        }

        public void setXy(int[] xy) {
            this.xy = xy;
        }

        public int getMoveCost() {
            return moveCost;
        }

        public void setMoveCost(int moveCost) {
            this.moveCost = moveCost;
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
                    if (node.getMoveCost() < get(index).getMoveCost()) {//如果新的路线更近，则更新该格子
                        remove(index);
                        super.add(node);
                        return true;
                    }
                    return false;
                } else super.add(node);
            }
            return true;
        }

        public int indexOf(int[] xyTile){
            for (int i = size() - 1; i >= 0; i--) {
                Node tmp = get(i);
                if (tmp.xy[0]==xyTile[0] && tmp.xy[1]==xyTile[1]) {
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

        public void sort(Node node) {
            for (int i = size() - 1; i >= 0; i--) {
                Node n = get(i);
                if (n == node) {
                    for (; i > 0; i--) {
                        n = get(i - 1);
                        if (node.compareTo(n) >= 0) {
                            return;
                        } else {
                            set(i, n);
                            set(i - 1, node);
                        }
                    }
                }
            }
        }
    }
}
