package tile;

import java.awt.Graphics2D;
import javax.imageio.ImageIO;
import main.GamePanel;

public class TileManager {
    
    GamePanel gp;
    public Tile[] tile;
    public int mapTileNum[][];

    public TileManager(GamePanel gp) {
        this.gp = gp; 
        this.tile = new Tile[10]; 
        this.mapTileNum = new int[gp.maxWorldCol][gp.maxWorldRow];
        
        getTileImage();
        
        // ✨ Ipasa ang numero 1 para Stage 1 ang iguhit ng system
        loadMap(1); 
    }

    // ✨ INAYOS: Tinanggal natin ang (String tilePath) para pumasok sa constructor
    public void getTileImage() {
        try {
            // Tile 0: Air / Void (Invisible, walang banggaan)
            tile[0] = new Tile();
            tile[0].collision = false;

            // Tile 1: Ang Solid Tile (Buhangin)
            tile[1] = new Tile();
            
            // ✨ I-DOUBLE CHECK: Tiyaking nasa "tile" folder at "sandTiles.png" ang pangalan ng picture mo
            tile[1].image = ImageIO.read(getClass().getResourceAsStream("/tile/sandTiles.png"));
            tile[1].collision = true; 
            
        } catch (Exception e) {
            System.out.println("⚠️ ERROR TILE: Hindi ma-load ang tileset!");
            e.printStackTrace();
        }
    }

    public void loadMap(int stageLevel) {
        // Linisin muna ang buong blueprint array pabalik sa 0
        for (int col = 0; col < gp.maxWorldCol; col++) {
            for (int row = 0; row < gp.maxWorldRow; row++) {
                mapTileNum[col][row] = 0;
            }
        }

        // ==========================================
        // DITO NATIN PAGHIHIWALAYIN ANG MGA STAGES
        // ==========================================
        
        if (stageLevel == 1) {
            // ✨ STAGE 1: SEASIDE OBSTACLES (Buhangin)
            
        	// [row] [column]
            // Isang Pader sa Column 6 (Harang sa daan)
            mapTileNum[20][6] = 1; 
            mapTileNum[21][6] = 1; 
            mapTileNum[22][6] = 1; 
            mapTileNum[23][6] = 1; 
            mapTileNum[24][6] = 1; 
            mapTileNum[25][6] = 1; 

            
            mapTileNum[59][6] = 1; 
            mapTileNum[60][6] = 1; 
            mapTileNum[61][6] = 1; 
            mapTileNum[62][6] = 1; 
            mapTileNum[63][6] = 1;
            mapTileNum[64][6] = 1;
            mapTileNum[65][6] = 1;
            mapTileNum[66][6] = 1;
            mapTileNum[67][6] = 1;

            
            
            
        } 
        else if (stageLevel == 2) {
            // ✨ STAGE 2: (Halimbawa: Forest o City)
            
//            // Iba naman ang latag ng pader dito!
//            mapTileNum[10][9] = 1; 
//            mapTileNum[11][9] = 1; 
//            mapTileNum[12][9] = 1; 
//            
//            // Lumulutang na platform
//            mapTileNum[15][6] = 1; 
//            mapTileNum[16][6] = 1; 
        }
    }
    public void draw(Graphics2D g2) {
        int worldCol = 0;
        int worldRow = 0;

        while (worldCol < gp.maxWorldCol && worldRow < gp.maxWorldRow) {
            int tileNum = mapTileNum[worldCol][worldRow];

            int worldX = worldCol * gp.tileSize;
            int worldY = worldRow * gp.tileSize;

            int screenX = worldX - gp.cameraX;
            int screenY = worldY - gp.cameraY;

            // I-render lang kapag nakikita sa screen para iwas lag
            if (worldX + gp.tileSize > gp.cameraX && 
                worldX - gp.tileSize < gp.cameraX + gp.getWidth()) {
                
                if (tileNum != 0 && tile[tileNum] != null && tile[tileNum].image != null) {
                    g2.drawImage(tile[tileNum].image, screenX, screenY, gp.tileSize, gp.tileSize, null);
                }
            }
            
            worldCol++;
            if (worldCol == gp.maxWorldCol) {
                worldCol = 0;
                worldRow++;
            }
        }
    }
}