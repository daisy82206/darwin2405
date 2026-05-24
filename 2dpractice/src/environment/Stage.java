package environment;

import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsEnvironment;
import java.awt.Transparency;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import main.GamePanel;

public class Stage {
    
    public GamePanel gp;
    
    public BufferedImage bgStatic;
    public BufferedImage movingLayer;
    
    public int worldWidth; 

    private int skyRenderWidth;
    private int movingRenderWidth;

    public Stage(GamePanel gp) {
        this.gp = gp;
    }

    public void setStaticBackground(String imagePath) {
        try {
            BufferedImage originalSky = ImageIO.read(getClass().getResourceAsStream(imagePath));
            
            double skyRatio = (double) originalSky.getWidth() / originalSky.getHeight();
            skyRenderWidth = (int) (gp.screenHeight * skyRatio);
            if (skyRenderWidth < gp.screenWidth) {
                skyRenderWidth = gp.screenWidth;
            }

            // ✨ GPU ACCELERATION: Ilipat ang Langit sa Video RAM (VRAM)
            GraphicsConfiguration gc = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration();
            bgStatic = gc.createCompatibleImage(skyRenderWidth, gp.screenHeight, Transparency.TRANSLUCENT);
            
            Graphics2D g2 = bgStatic.createGraphics();
            g2.drawImage(originalSky, 0, 0, skyRenderWidth, gp.screenHeight, null);
            g2.dispose();
            
        } catch (Exception e) {
            System.out.println("⚠️ ERROR LANGIT: Hindi mahanap ang -> " + imagePath);
        }
    }

    public void setMovingBackground(String imagePath) {
        try {
            BufferedImage originalOcean = ImageIO.read(getClass().getResourceAsStream(imagePath));
            
            double aspectRatio = (double) originalOcean.getWidth() / originalOcean.getHeight();
            movingRenderWidth = (int) (gp.screenHeight * aspectRatio);
            worldWidth = movingRenderWidth;

            // ✨ GPU ACCELERATION: Ilipat ang napakalaking 4403px na Dagat sa Video RAM (VRAM)
            GraphicsConfiguration gc = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration();
            movingLayer = gc.createCompatibleImage(movingRenderWidth, gp.screenHeight, Transparency.TRANSLUCENT);
            
            Graphics2D g2 = movingLayer.createGraphics();
            g2.drawImage(originalOcean, 0, 0, movingRenderWidth, gp.screenHeight, null);
            g2.dispose();
            
        } catch (Exception e) {
            System.out.println("⚠️ ERROR DAGAT: Hindi mahanap ang -> " + imagePath);
        }
    }

    public void draw(Graphics2D g2) {
        
        if (bgStatic != null) {
            int skyCameraX = (int)(gp.cameraX * 0.1); 
            int skyRenderX = -(skyCameraX % skyRenderWidth);

            g2.drawImage(bgStatic, skyRenderX, 0, null);
            g2.drawImage(bgStatic, skyRenderX + skyRenderWidth, 0, null);
        }

        if (movingLayer != null) {
            int renderX = -gp.cameraX; 
            g2.drawImage(movingLayer, renderX, 0, null);
        }
    }
    
}	