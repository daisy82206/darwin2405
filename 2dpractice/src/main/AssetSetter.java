package main;

import object.Box;
import darwin1.player; // Import para makilala ang player class

public class AssetSetter {
    GamePanel gp;

    public AssetSetter(GamePanel gp) {
        this.gp = gp;
    }

    public void setObjects(int stageLevel) {
        // Linisin ang listahan
        for(int i = 0; i < gp.objBox.length; i++) {
            gp.objBox[i] = null;
        }

        if (stageLevel == 1) {
            // Eto ang pinaka-consistent na alignment
            // Gagamitin natin ang static player.groundY
            int boxHeight = gp.tileSize * 2; 
            double surfaceY = player.groundY + gp.tileSize; 

            // BOX 0: Pushable sa X=500
            gp.objBox[0] = new Box(500, (int)(surfaceY - boxHeight), gp.tileSize, true);
            
            // BOX 1 & 2: Stacking sa X=1002
            // Ilalim
            gp.objBox[1] = new Box(1002, (int)(surfaceY - boxHeight), gp.tileSize, false);
            // Ibabaw
            gp.objBox[2] = new Box(1002, (int)(surfaceY - (boxHeight * 2)), gp.tileSize, false);
            gp.objBox[3] = new Box(2000, (int)(surfaceY - boxHeight), gp.tileSize, false);
            gp.objBox[4] = new Box(3400, (int)(surfaceY - boxHeight), gp.tileSize, false);
            gp.objBox[5] = new Box(3470, (int)(surfaceY - boxHeight * 2), gp.tileSize, false);
            gp.objBox[6] = new Box(3550, (int)(surfaceY - boxHeight), gp.tileSize, false);
            
            // ==========================================================
            // ☔ HARM OBJECT: UMBRELLA (🔥 NO LOCATION CHANGES)
            // ==========================================================
            // Pinasa natin ang 'gp' sa dulo para mag-sync sa bagong overloaded constructor natin!
            gp.objUmb[0] = new object.Umbrella(3000, 630, gp.tileSize);
        }
    }
}