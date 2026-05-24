package environment;
import main.GamePanel;

public class Stage1Seaside extends Stage {

    public Stage1Seaside(GamePanel gp) {
        super(gp); 
        
        // 1. I-set ang Backgrounds
        setStaticBackground("/background/stage1/bg_static.png"); 
        setMovingBackground("/background/stage1/sand_and_ocean.png"); 
        
        
    }
}