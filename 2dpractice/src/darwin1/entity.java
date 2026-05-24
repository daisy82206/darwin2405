package darwin1;

import java.awt.Rectangle;
import java.awt.image.BufferedImage;

public class entity {

    public int x, y;           // World position
    public int width, height;  // Size
    public int speed;
    
    public BufferedImage up1,up2,left1,left2,right1,right2;
    public String direction;
    
    

    // For collision detection
    public Rectangle getBounds() {
        return new Rectangle(x, y, width, height);
    }
}
