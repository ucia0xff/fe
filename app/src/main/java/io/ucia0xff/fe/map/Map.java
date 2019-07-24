package io.ucia0xff.fe.map;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import io.ucia0xff.fe.Values;
import io.ucia0xff.fe.anim.Anim;

public class Map {
    //地图瓦片集
    public static Bitmap tileSet = null;

    //地图名
    public static String mapName = "";

    //地图的宽高
    public static int mapWidth = 0;
    public static int mapHeight = 0;

    //图集中横纵向瓦片块的数量
    public static int resWidthTileCount = 0;
    public static int resHeightTileCount = 0;

    //游戏中横纵向瓦片块的数量
    public static int mapWidthTileCount = 0;
    public static int mapHeightTileCount = 0;

    //瓦片在图集中的序号
    public static int[][] tileIndexes;

    //瓦片对应的地形的序号
    public static int[][] terrainIndexes;
    private Terrain terrain;

    private InputStream is;

    public Map() {

    }

    public Map(String mapName) {
        this.mapName = mapName;
        readMap(mapName);
    }

    public void readMap(String mapName) {
        readTiles(mapName);
        readTerrain(mapName);
    }


    /**
     * 读取地图图块数组
     * @param mapName
     */
    private void readTiles(String mapName) {
        try {
            is = Values.CONTEXT.getAssets().open("map_config/" + mapName + ".map");
            BufferedReader br = new BufferedReader(new InputStreamReader(is));

            //第一行是用到的瓦片集文件
            String line = br.readLine();//第一行是瓦片集文件名
            String tileSetName = "map_config/" + line + ".png";
            tileSet = Anim.readBitmap(tileSetName);
            resWidthTileCount = tileSet.getWidth() / Values.RES_TILE_WIDTH;
            resHeightTileCount = tileSet.getHeight() / Values.RES_TILE_HEIGHT;

            //第二行是地图高和宽的格子数
            line = br.readLine();
            String[] mapSize = line.split(" ");
            mapWidthTileCount = Integer.parseInt(mapSize[1]);//地图水平方向格子数
            mapHeightTileCount = Integer.parseInt(mapSize[0]);//地图竖直方向格子数
            mapWidth = mapWidthTileCount * Values.MAP_TILE_WIDTH;///地图总像素宽度
            mapHeight = mapHeightTileCount * Values.MAP_TILE_HEIGHT;//地图总像素高度

            //之后是地图每个格子在瓦片集中的序号
            tileIndexes = new int[mapHeightTileCount][mapWidthTileCount];
            String[] values;
            for (int i = 0; i < mapHeightTileCount && (line = br.readLine()) != null; i++) {
                values = line.split(" ");
                for (int j = 0; j < values.length; j++)
                    tileIndexes[i][j] = Integer.parseInt(values[j]);
            }
            is.close();
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * 读取地图地形数组
     * @param mapName
     */
    private void readTerrain(String mapName) {
        try {
            String line;
            String[] values;
            is = Values.CONTEXT.getAssets().open("map_config/" + mapName + ".ter");
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            terrainIndexes = new int[mapHeightTileCount][mapWidthTileCount];
            for (int i = 0; i < mapHeightTileCount && (line = br.readLine()) != null; i++) {
                values = line.split(" ");
                for (int j = 0; j < values.length; j++)
                    terrainIndexes[i][j] = Integer.parseInt(values[j]);
            }
            is.close();
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * 绘制游戏地图
     * @param canvas
     * @param paint
     * @param xyOffset 游戏地图左上角相对屏幕左上角(0,0)的偏移量，左为负右为正，上为负下为正
     */
    public void drawMap(Canvas canvas, Paint paint, int[] xyOffset) {
        for (int i = 0; i < mapHeightTileCount; i++) {
            for (int j = 0; j < mapWidthTileCount; j++) {
                if ((j * Values.MAP_TILE_WIDTH + xyOffset[0] + Values.MAP_TILE_WIDTH < 0)||//左屏幕外
                        (i * Values.MAP_TILE_HEIGHT + xyOffset[1] + Values.MAP_TILE_HEIGHT < 0)||//上屏幕外
                        (j * Values.MAP_TILE_WIDTH + xyOffset[0] > Values.SCREEN_WIDTH)||//右屏幕外
                        (i * Values.MAP_TILE_HEIGHT + xyOffset[1] > Values.SCREEN_HEIGHT))//下屏幕外
                    continue;//不画屏幕外的地图块
                //算出要绘制的地图块左上角在屏幕上的像素坐标
                int [] xyPos = new int[]{j * Values.MAP_TILE_WIDTH + xyOffset[0], i * Values.MAP_TILE_HEIGHT + xyOffset[1]};
                drawMapTile(tileIndexes[i][j], canvas, paint, tileSet, xyPos);
            }
        }
    }

    /**
     * 根据ID绘制一个地图块
     * @param id 当前地图块在地图块集中的ID
     * @param canvas
     * @param paint
     * @param bitmap 地图块集
     * @param xyPos 绘制在屏幕的像素坐标
     */
    public void drawMapTile(int id, Canvas canvas, Paint paint, Bitmap bitmap, int[] xyPos) {
        if (canvas==null) return;
        //算出要绘制的地图块左上角在地图资源中的像素坐标
        int bitmapX = id % resWidthTileCount * Values.RES_TILE_WIDTH;//需要的地图块在地图资源中的X坐标
        int bitmapY = id / resWidthTileCount * Values.RES_TILE_HEIGHT;//需要的地图块在地图资源中的Y坐标
        canvas.save();
        //将要绘制的矩形截取出来，为了不影响其他区域
        canvas.clipRect(xyPos[0], xyPos[1], xyPos[0] + Values.MAP_TILE_WIDTH, xyPos[1] + Values.MAP_TILE_HEIGHT);
        canvas.drawBitmap(bitmap,
                new Rect(bitmapX, bitmapY, bitmapX + Values.RES_TILE_WIDTH, bitmapY + Values.RES_TILE_HEIGHT),//要绘制的地图块在图集中的矩形
                new Rect(xyPos[0], xyPos[1], xyPos[0] + Values.MAP_TILE_WIDTH, xyPos[1] + Values.MAP_TILE_HEIGHT),//要绘制的地图块在屏幕上的矩形
                paint);
        canvas.restore();
    }

    public int getMapHeight() {
        return mapHeight;
    }

    public void setMapHeight(int mapHeight) {
        Map.mapHeight = mapHeight;
    }

    public int getMapWidth() {
        return mapWidth;
    }

    public void setMapWidth(int mapWidth) {
        Map.mapWidth = mapWidth;
    }

    public int getMapHeightTileCount() {
        return mapHeightTileCount;
    }

    public void setMapHeightTileCount(int mapHeightTileCount) {
        Map.mapHeightTileCount = mapHeightTileCount;
    }

    public int getMapWidthTileCount() {
        return mapWidthTileCount;
    }

    public void setMapWidthTileCount(int mapWidthTileCount) {
        Map.mapWidthTileCount = mapWidthTileCount;
    }

    public int getTerrain(int[] xy) {
        return terrainIndexes[xy[1]][xy[0]];
    }

    public void setTerrain(int[][] terrain) {
        Map.terrainIndexes = terrain;
    }

    public int[][] getMap() {
        return tileIndexes;
    }

    public void setMap(int[][] map) {
        tileIndexes = map;
    }

    public int[][] getTerrain() {
        return terrainIndexes;
    }
}
