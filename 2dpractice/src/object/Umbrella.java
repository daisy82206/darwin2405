package object;

import java.awt.Rectangle;
import javax.imageio.ImageIO;
import main.GamePanel;

public class Umbrella {

    public int worldX, worldY;
    public int width, height; 
    public java.awt.image.BufferedImage image;
    public Rectangle solidArea;

    // CONSTRUCTOR 1: Default call
    public Umbrella(GamePanel gp) {
        this.width = gp.tileSize;
        this.height = gp.tileSize;
        loadUmbrellaImage();
        setPixelPerfectHitbox();
    }

    // ==========================================================
    // ☔ THE UNIVERSAL ACCURATE CONSTRUCTOR
    // ==========================================================
    public Umbrella(int worldX, int worldY, int size) {
        this.worldX = worldX;
        this.worldY = worldY;
        
        // Pinalaki ang sukat base sa input mo sa AssetSetter
        this.width = (int)(size * 1.3);   // Width scale ng sprite
        this.height = (int)(size * 1.8);  // Height scale ng sprite
        
        loadUmbrellaImage();
        setPixelPerfectHitbox();
    }
    
    // ✨ PIXEL ART ACCURATE SCALING MATRIX
    private void setPixelPerfectHitbox() {
        // 1. Ang base canvas ng orihinal mong drawing ay 16x16 pixels.
        // Kunin natin ang saktong sukat kung saan nakatapak ang payong sa loob ng 16x16 grid:
        double basePNGWidth = 16.0;
        double basePNGHeight = 16.0;
        
        // 2. Saktong sukat ng payong sa loob ng 16x16 frame (Tinatapyas ang blankong hangin ng PNG):
        double originalX = 4.0;   // Simula ng guhit ng payong mula sa kaliwa ng 16x16 frame
        double originalY = 0.0;   // Simula ng guhit mula sa taas
        double originalW = 8.0;   // Ang mismong lapad ng guhit ng payong (manipis lang talaga)
        double originalH = 16.0;  // Sagad hanggang ilalim ng hawakan
        
        // 3. ✨ THE LOGICAL BRIDGE: I-scale natin ang hitbox base sa kung gaano pinalaki ang imahe sa screen!
        double scaleX = (double)this.width / basePNGWidth;
        double scaleY = (double)this.height / basePNGHeight;
        
        int finalX = (int)(originalX * scaleX);
        int finalY = (int)(originalY * scaleY);
        int finalW = (int)(originalW * scaleX);
        int finalH = (int)(originalH * scaleY);

        // Garantisadong nakadikit at hindi hihiwalay kung nasaan ang asul na sprite!
        solidArea = new Rectangle(finalX, finalY, finalW, finalH); 
    }
    
    private void loadUmbrellaImage() {
        try {
            image = ImageIO.read(getClass().getResourceAsStream("/object/stage1/broken_umbrella.png")); 
        } catch(Exception e) {
            System.out.println("Babala: Hindi ma-load ang imahe ng broken_umbrella!");
            e.printStackTrace();
        }
    }
    
    public void draw(java.awt.Graphics2D g2, GamePanel gp) {
        int screenX = worldX - gp.cameraX;
        int screenY = worldY - gp.cameraY;
        
        if (image != null) {
            g2.drawImage(image, screenX, screenY, width, height, null);
        }
    }
}