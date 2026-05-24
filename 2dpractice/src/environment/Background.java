package environment;

import java.awt.Graphics2D;
import main.GamePanel;

public abstract class Background {
    protected GamePanel gp;

    public Background(GamePanel gp) {
        this.gp = gp;
    }

    // Pinipilit nito na lahat ng susunod na stage ay magkaroon ng sariling draw at update method
    public abstract void update();
    public abstract void draw(Graphics2D g2);
}