package main;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.imageio.ImageIO;

public class SeaSideBackground {

    GamePanel gp;
    BufferedImage sky, oceanSand;
    
    // Parallax Speeds
    double skySpeed = 0.1;       // Sobrang bagal para sa ulap
    double oceanSandSpeed = 0.8; // Gumagalaw pero may kaunting parallax effect
    int designWidth; 

    public SeaSideBackground(GamePanel gp) {
        this.gp = gp;
        this.designWidth = 100 * gp.tileSize; // Haba ng shoreline (100 tiles)
        getImages();
    }

    public void getImages() {
        try {
            sky = ImageIO.read(getClass().getResourceAsStream("/background/sky.png"));
            oceanSand = ImageIO.read(getClass().getResourceAsStream("/background/oceanSand.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void draw(Graphics2D g2) {
        
        // --- LAYER 1: INFINITE SKY ---
        if (sky != null) {
            // PINALITAN: gp.cameraX na ang gagamitin imbes na player1.x
            int skyX = (int)(gp.cameraX * skySpeed) % gp.screenWidth;
            
            g2.drawImage(sky, -skyX, 0, gp.screenWidth, gp.screenHeight, null);
            g2.drawImage(sky, -skyX + gp.screenWidth, 0, gp.screenWidth, gp.screenHeight, null);
        }
        
        // --- LAYER 2: OCEANSAND TRANSITION ---
        if (oceanSand != null) {
            // PINALITAN: gp.cameraX na rin ang magpapatakbo dito
            int designOffsetX = 0 - (int)(gp.cameraX * oceanSandSpeed);
            
            // I-draw lang kung hindi pa tapos ang shoreline
            if (designOffsetX + designWidth > 0) {
                g2.drawImage(oceanSand, designOffsetX, 0, gp.screenWidth, gp.screenHeight, null);
            }
        }
    }
}