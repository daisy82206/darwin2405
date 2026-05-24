package object; // Tumuturo sa object package mo

import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.imageio.ImageIO;
import main.GamePanel; // Kailangan i-import si GamePanel dahil magkaiba na kayo ng package ngayon

public class Box {
	GamePanel gp;
    public int worldX, worldY;
    public int width, height;
    public boolean isPushable; 
    public Rectangle solidArea;
    private BufferedImage image;

    public Box(int worldX, int worldY, int tileSize, boolean isPushable) {
    	
        this.worldX = worldX;
        this.worldY = worldY;
        
        // Sinasabi kay Java na sakupin ang saktong space ng 2 tiles sa screen
        this.width = tileSize * 2;  
        this.height = tileSize * 2;
        this.isPushable = isPushable;
        
        // Hitbox area para sa banggaan (sumasagad sa laki ng box)
        this.solidArea = new Rectangle(0, 0, width, height);
        
        getBoxImage();
    }

    public void getBoxImage() {
        try {
            // Saktong-sakto sa bago mong ginawang folder setup sa res!
            image = ImageIO.read(getClass().getResourceAsStream("/object/stage1/box.png"));
        } catch (IOException e) {
            System.out.println("Error: Hindi mahanap ang box.png sa /object/stage1/ path!");
            e.printStackTrace();
        }
    }

    public void draw(Graphics2D g2, GamePanel gp) {
        // Render gamit ang worldX minus cameraX para sumabay sa scrolling kapag umuusad si Darwin
        int screenX = worldX - gp.cameraX;
        int screenY = worldY; 

        if (image != null) {
            g2.drawImage(image, screenX, screenY, width, height, null);
        }
    }
}