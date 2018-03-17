package io.ucia0xff.fe.actor;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.util.Log;

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
    private NodeList canMove;
    private NodeList movePath;
    private Bitmap areaMov;
    private Bitmap area;
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
        canMove = new NodeList();
        movePath = new NodeList();
        areaMov = Anim.readBitMap(R.drawable.area_mov);
        src = new Rect(0, 0, areaMov.getWidth(), areaMov.getHeight());
        dst = new Rect(0, 0, Values.MAP_TILE_WIDTH, Values.MAP_TILE_HEIGHT);
    }
    public ActorMoveHelper(Actor srcActor, Map map){
        this(map);
        this.setSrcActor(srcActor);
    }

    //显示移动区域
    public void drawCanMove(Canvas canvas, Paint paint, int[] xyOffset) {
        if (canvas==null) return;
        int[] xyTile;
        for (int i = 0; i < canMove.size(); i++) {
            xyTile = canMove.get(i).getXyTile();
            dst.offsetTo(xyTile[0] * Values.MAP_TILE_WIDTH + xyOffset[0], xyTile[1] * Values.MAP_TILE_HEIGHT + xyOffset[1]);
            drawBlock(canvas, paint, 0);
        }
    }

    //绘制一个格子
    private void drawBlock(Canvas canvas, Paint paint, int type) {
        switch (type){
            case 0:
                area = areaMov;
                break;
        }
        canvas.drawBitmap(area, src, dst, paint);
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
        startNode = new Node(srcActor.getXyTile());
        startNode.setMoveCost(0);
        this.setCanMove();
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

    public NodeList getCanMove() {
        return canMove;
    }

    public void setCanMove() {
        canMove.clear();
        canMove.add(startNode);
        int[] nowXY;
        int[] newXY = new int[2];
        for (int i = 0; i < canMove.size(); i++) {
            nowNode = canMove.get(i);
            nowXY = nowNode.getXyTile();
            for (int j = 0; j < DIRECTION.length; j++) {
                newXY[0] = nowXY[0] + DIRECTION[j][0];
                newXY[1] = nowXY[1] + DIRECTION[j][1];
                if (newXY[0] < 0 || newXY[1] < 0 || newXY[0] >= map.getMapWidthTileCount() || newXY[1] >= map.getMapHeightTileCount())//检查是否超出地图边界
                    continue;
                else {
                    newNode = new Node(newXY);
                    newNode.setParent(nowNode);
                    newNode.setMoveCost(newNode.getMoveCost() + nowNode.getMoveCost());
                    if (newNode.getMoveCost() <= srcActor.getMov()) {//如果该格子在距离上可达
                        if ((dstActor = Actors.getActor(newXY))!=null) {//则判断该格子上有没有角色存在
                            switch (srcActor.getParty()){//如果有，则判断两者的阵营关系，友好阵营可以通过，敌对阵营不能通过
                                case Values.PARTY_ALLY:
                                case Values.PARTY_PLAYER:
                                    if (dstActor.getParty()==Values.PARTY_ENEMY)
                                        continue;
                                    break;
                                case Values.PARTY_ENEMY:
                                    if (dstActor.getParty()!=Values.PARTY_ENEMY)
                                        continue;
                                    break;
                            }
                        }
                        canMove.add(newNode);
                    }
                }
            }
        }
    }

    public NodeList getMovePath() {
        return movePath;
    }

    public void setMovePath(int[] xyTile) {
        movePath.clear();
        int index = canMove.indexOf(new Node(xyTile));
        nowNode = canMove.get(index);
        while (nowNode != startNode) {
            movePath.add(0, nowNode);
            nowNode = nowNode.getParent();
        }
    }

    //内部类——节点
    public class Node implements Comparable<Node> {
        private int[] xyTile;//当前格子的位置
        private int moveCost;//当前格子的移动力消耗

        private int f;//g和h的和
        private int g;//从起始格子到当前格子需要的移动力
        private int h;//从当前格子到终点格子的预估移动力
        private Node parent;//路径上的父格子

        public Node(int[] xyTile) {
            this.xyTile = new int[2];
            this.xyTile[0] = xyTile[0];
            this.xyTile[1] = xyTile[1];
            this.moveCost = Terrain.MOVE_COST[Careers.careers.get(srcActor.getCareerKey()).getMoveType()][map.getTerrain(this.xyTile)];
            this.parent = null;
        }

        public boolean equals(Node node) {
            return (this.xyTile[0] == node.getXyTile()[0]) && (this.xyTile[1] == node.getXyTile()[1]);
        }

        @Override
        public int compareTo(Node node) {
            int result = this.f - node.f;
            if (result == 0) {
                result = this.h - node.h;
            }
            return result;
        }

        public int[] getXyTile() {
            return xyTile;
        }

        public void setXyTile(int[] xyTile) {
            this.xyTile = xyTile;
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
