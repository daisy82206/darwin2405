package darwin1;

import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import main.GamePanel;
import main.KeyHandler;
import object.Box; 

public class player {

    // ==========================================
    // ⚙️ SYSTEM & DEPENDENCY VARIABLES
    // ==========================================
    GamePanel gp;       // Bridge patungo sa puso ng core game configurations
    KeyHandler keyH;    // Tagapakinig sa bawat pindot ng keyboard ng player

    // ==========================================
    // 🏃‍♂️ COORD PHYSICAL GRAPHICS VARIATION
    // ==========================================
    public double worldX;          // Kasalukuyang pahalang na posisyon ng player sa mapa
    public double worldY;          // Kasalukuyang patayong posisyon ng player sa mapa
    public double speed;           // Bilis ng takbo ni Darwin (e.g., 4.9 pixels per frame)
    public double yVelocity = 0;   // Kasalukuyang momentum paitaas o pababa (Vertical Velocity)
    
    // ==========================================
    // 🌍 CONSTANT WORLD ENTITIES
    // ==========================================
    public final double GRAVITY = 1.0;         // Lakas ng hatak pababa bawat frame kapag nasa ere
    public final double JUMP_STRENGTH = -21.0; // Lakas ng sikad paitaas tuwing tatalon
    public static double groundY;              // Static anchor ng default na sahig para sa auto-alignment ng assets
    public boolean onGround = false;           // State Tracker: true kung nakatapak sa solid, false kung nahuhulog
    
    // ==========================================
    // 📦 COLLISION & HITBOX COMPONENT
    // ==========================================
    public Rectangle solidArea;    // Invisible matrix box para sa physical boundary collision checking

    // ==========================================
    // 🎨 SPRITE ANIMATION MANAGEMENT
    // ==========================================
    public BufferedImage imgStraight;
    public BufferedImage imgRight1, imgRight2, imgRight3; 
    public String direction;        // Direksyon ng mukha ni Darwin ("left" o "right")
    public boolean isMoving;        // State Tracker: Para sa animation switch
    public int spriteCounter = 0;   // Frame timer para sa pagpapalit ng hitsura ng character
    public int spriteNum = 1;       // Kasalukuyang frame number ng paa ni Darwin (1, 2, o 3)
    
    // ==========================================
    // 👑 LIFE & STATUS MANAGEMENT SYSTEM
    // ==========================================
    public int lifeCount = 1;       // Bilang ng natitirang Ulo ni Darwin (Darwin x 3)
    public int maxLife = 3;         // Pinakamataas na limitasyon ng lalagyan ng puso
    public int life = 3;            // Kasalukuyang bilang ng Puso - Nababawasan bawat salpok sa payong

    // ==========================================
    // 🛡️ INVINCIBILITY FRAMES (i-FRAMES) CONFIG
    // ==========================================
    public boolean invincible = false;     // State Tracker: True kapag immune tạm-tạm
    public int invincibleCounter = 0;       // Timer tracker para magbilang ng frame duration
    public boolean drawing = true;
    public boolean isKnockedBack = false;   // State controller para sa ligtas na knockback at mataas na talon
    public String knockbackDirection = "";  // Direksyon ng lipad kapag tumalbog
    public int knockbackTimer = 0;          // Binibilang ang frames kung gaano katagal lilipad nang walang gravity
    
    // ==========================================
    // 🛠️ CLASS CONSTRUCTOR
    // ==========================================
    public player(GamePanel gp, KeyHandler keyH) {
        this.gp = gp;
        this.keyH = keyH;
        
        solidArea = new Rectangle(0, 0, gp.tileSize, gp.tileSize);
        
        setDefaultValues(); 
        getPlayerImage();   
    }

    // ==========================================
    // 🌀 UTILITY LOGIC FUNCTION METHODS
    // ==========================================
    
    public void takeDamage() {
        life--;
        if(life < 0) life = 0;
    }

    public void setDefaultValues() {
        worldX = gp.tileSize * 2; // Panimulang X ng mapa
        
        int baseFloorY = gp.screenHeight - (int)(gp.tileSize * 0.7);
        groundY = baseFloorY - gp.tileSize; 
        worldY = groundY; 
        
        speed = 4.9; 
        
        direction = "right";
        isMoving = false;
        onGround = true;
        yVelocity = 0; // Patayin ang kahit anong lumang momentum sa ere

        // I-clear ang controls sa KeyHandler para walang maiwang nakabaong pindot pagka-restart
        if (keyH != null) {
            keyH.upPressed = false;
            keyH.downPressed = false;
            keyH.leftPressed = false;
            keyH.rightPressed = false;
        }

        life = maxLife; 
    }
    
    public void getPlayerImage() {
        try {
            imgStraight = ImageIO.read(getClass().getResourceAsStream("/player/darwin-straight.png"));
            imgRight1   = ImageIO.read(getClass().getResourceAsStream("/player/darwin-right1.png"));
            imgRight2   = ImageIO.read(getClass().getResourceAsStream("/player/darwin-right2.png"));
            imgRight3   = ImageIO.read(getClass().getResourceAsStream("/player/darwin-right3.png"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ==========================================
    // 🔄 CORE PHYSICS GAME LOOP TICK
    // ==========================================
    public void update() {
        isMoving = false;
        double oldX = worldX; // Itinatala ang lumang posisyon para sa hard boundary pushbacks

        // ==========================================================
        // 🚀 THE MOMENTUM OVERRIDE ENGINE (ANTI-KAPOS JUMP)
        // ==========================================================
        if (isKnockedBack == true) {
            knockbackTimer++;
            
            if (knockbackTimer == 1) {
                worldY -= 30;       // I-angat agad ng malaki para lumayo sa floor checks
                yVelocity = -25.0;  // 🔥 Pwersahang sipa paitaas laban sa grabidad!
                onGround = false;
            }
            
            if (knockbackDirection.equals("left")) {
                worldX -= 7.0; 
            } else if (knockbackDirection.equals("right")) {
                worldX += 7.0; 
            }
            
            if (worldX < 0) worldX = 0;
            if (gp.stageSeaside != null && worldX > gp.stageSeaside.worldWidth - gp.tileSize) {
                worldX = gp.stageSeaside.worldWidth - gp.tileSize;
            }
            
            worldY += yVelocity;
            yVelocity += 0.9; // Mas mababa para maging magaan ang lipad habang knockback
            
            if (worldY >= groundY) {
                worldY = groundY;
                yVelocity = 0;
                onGround = true;
            }

            if (knockbackTimer > 25) {
                isKnockedBack = false;
                knockbackTimer = 0;
            }
            
            // BYPASS: Laktawan ang natitirang keyboard inputs habang lumilipad si Darwin
            return; 
        }

        // ------------------------------------------
        // 1. HORIZONTAL MOVEMENTS & BOUNDARIES
        // ------------------------------------------
        if (keyH.leftPressed == true) {
            worldX -= speed;
            direction = "left";
            isMoving = true;
        }
        else if (keyH.rightPressed == true) {
            worldX += speed;
            direction = "right";
            isMoving = true;
        }

        if (worldX < 0) worldX = 0;
        if (gp.stageSeaside != null && worldX > gp.stageSeaside.worldWidth - gp.tileSize) {
            worldX = gp.stageSeaside.worldWidth - gp.tileSize;
        }

        // ------------------------------------------
        // 2. HORIZONTAL BOX INTERSECTION BLOCK
        // ------------------------------------------
        for (int i = 0; i < gp.objBox.length; i++) {
            if (gp.objBox[i] != null) {
                Box box = gp.objBox[i];
                Rectangle playerBoundsX = new Rectangle((int)worldX + solidArea.x, (int)worldY + solidArea.y, solidArea.width, solidArea.height);
                Rectangle boxBounds = new Rectangle(box.worldX + box.solidArea.x, box.worldY + box.solidArea.y, box.width, box.height);

                if (playerBoundsX.intersects(boxBounds)) {
                    if (box.isPushable) {
                        int nextBoxX = box.worldX;
                        if ("right".equals(direction)) nextBoxX += (int)speed;
                        else if ("left".equals(direction)) nextBoxX -= (int)speed;
                        
                        Rectangle nextBoxBounds = new Rectangle(nextBoxX + box.solidArea.x, box.worldY + box.solidArea.y, box.width, box.height);
                        boolean canPush = true;
                        
                        for (int j = 0; j < gp.objBox.length; j++) {
                            if (gp.objBox[j] != null && i != j) { 
                                Box otherBox = gp.objBox[j];
                                Rectangle otherBoxBounds = new Rectangle(otherBox.worldX + otherBox.solidArea.x, otherBox.worldY + otherBox.solidArea.y, otherBox.width, otherBox.height);
                                if (nextBoxBounds.intersects(otherBoxBounds)) {
                                    canPush = false; break;
                                }
                            }
                        }
                        
                        if (canPush) {
                            box.worldX = nextBoxX;
                            if ("right".equals(direction)) worldX = oldX + (int)speed;
                            else if ("left".equals(direction)) worldX = oldX - (int)speed;
                        } else {
                            worldX = oldX; 
                        }
                    } else {
                        worldX = oldX; 
                    }
                }
            }
        }
        
        // ------------------------------------------
        // 3. HORIZONTAL SOLID TILES COLLISION
        // ------------------------------------------
        int pLeftCol   = ((int)worldX + solidArea.x) / gp.tileSize;
        int pRightCol  = ((int)worldX + solidArea.x + solidArea.width - 1) / gp.tileSize;
        int pTopRow    = ((int)worldY + solidArea.y) / gp.tileSize;
        int pBottomRow = ((int)worldY + solidArea.y + solidArea.height - 1) / gp.tileSize; 

        if (gp.tileM != null && gp.tileM.mapTileNum != null && pLeftCol >= 0 && pRightCol < gp.maxWorldCol) {
            if (direction.equals("left")) {
                int tile1 = gp.tileM.mapTileNum[pLeftCol][pTopRow];    
                int tile2 = gp.tileM.mapTileNum[pLeftCol][pBottomRow]; 
                if ((gp.tileM.tile[tile1] != null && gp.tileM.tile[tile1].collision) || 
                    (gp.tileM.tile[tile2] != null && gp.tileM.tile[tile2].collision)) {
                    worldX = oldX; 
                }
            }
            if (direction.equals("right")) {
                int tile1 = gp.tileM.mapTileNum[pRightCol][pTopRow];    
                int tile2 = gp.tileM.mapTileNum[pRightCol][pBottomRow]; 
                if ((gp.tileM.tile[tile1] != null && gp.tileM.tile[tile1].collision) || 
                    (gp.tileM.tile[tile2] != null && gp.tileM.tile[tile2].collision)) {
                    worldX = oldX; 
                }
            }
        }

        // ------------------------------------------
        // 4. VERTICAL JUMP & GRAVITY PHYSICS ENGINE
        // ------------------------------------------
        if (keyH.upPressed == true && onGround == true) {
            yVelocity = JUMP_STRENGTH; 
            onGround = false; 
        }

        worldY += yVelocity; 
        yVelocity += GRAVITY; 
        onGround = false;     

        // ------------------------------------------
        // 5. VERTICAL BOX HITBOX PROJECTIONS
        // ------------------------------------------
        for (int i = 0; i < gp.objBox.length; i++) {
            if (gp.objBox[i] != null) {
                Box box = gp.objBox[i];
                Rectangle playerBoundsY = new Rectangle((int)worldX + solidArea.x, (int)worldY + solidArea.y, solidArea.width, solidArea.height);
                Rectangle boxBounds = new Rectangle(box.worldX + box.solidArea.x, box.worldY + box.solidArea.y, box.width, box.height);

                if (playerBoundsY.intersects(boxBounds)) {
                    if (yVelocity > 0) { 
                        worldY = box.worldY - solidArea.height; 
                        yVelocity = 0; 
                        onGround = true; 
                    } else if (yVelocity < 0) { 
                        worldY = box.worldY + box.height; 
                        yVelocity = 0; 
                    }
                }
            }
        }

     // ------------------------------------------
        // 6. VERTICAL SOLID TILE MATRIX DETECTOR (✨ SYSTEM SAFEGUARD CLAMPING)
        // ------------------------------------------
        pLeftCol   = ((int)worldX + solidArea.x) / gp.tileSize;
        pRightCol  = ((int)worldX + solidArea.x + solidArea.width - 1) / gp.tileSize;
        pTopRow    = ((int)worldY + solidArea.y) / gp.tileSize;
        pBottomRow = ((int)worldY + solidArea.y + solidArea.height - 1) / gp.tileSize;

        // 🔥 THE ULTIMATE HARD BOUNDARY CLAMP (ANTI-CRASH)
        // Kahit biglang magbago ang coordinates habang nag-re-contact o nag-blink,
        // selyadong ikukulong nito ang mga indexes sa safe bounds ng array (0 hanggang length - 1).
        pTopRow    = Math.max(0, Math.min(pTopRow, gp.maxWorldRow - 1));
        pBottomRow = Math.max(0, Math.min(pBottomRow, gp.maxWorldRow - 1));
        pLeftCol   = Math.max(0, Math.min(pLeftCol, gp.maxWorldCol - 1));
        pRightCol  = Math.max(0, Math.min(pRightCol, gp.maxWorldCol - 1));

        if (gp.tileM != null && gp.tileM.mapTileNum != null) {
            if (yVelocity > 0) { 
                int tile1 = gp.tileM.mapTileNum[pLeftCol][pBottomRow];
                int tile2 = gp.tileM.mapTileNum[pRightCol][pBottomRow];
                
                if ((gp.tileM.tile[tile1] != null && gp.tileM.tile[tile1].collision) || 
                    (gp.tileM.tile[tile2] != null && gp.tileM.tile[tile2].collision)) {
                    worldY = (pBottomRow * gp.tileSize) - (solidArea.y + solidArea.height);
                    yVelocity = 0;
                    onGround = true; 
                }
            }
            else if (yVelocity < 0) { 
                int tile1 = gp.tileM.mapTileNum[pLeftCol][pTopRow];
                int tile2 = gp.tileM.mapTileNum[pRightCol][pTopRow];
                
                if ((gp.tileM.tile[tile1] != null && gp.tileM.tile[tile1].collision) || 
                    (gp.tileM.tile[tile2] != null && gp.tileM.tile[tile2].collision)) {
                    worldY = (pTopRow * gp.tileSize) + gp.tileSize - solidArea.y;
                    yVelocity = 0;
                }
            }
        }
	
	        if (worldY >= groundY) {
	            worldY = groundY;
	            yVelocity = 0;
	            onGround = true; 
	        }

        // ------------------------------------------
        // 7. ANIMATION TICK COUNTER
        // ------------------------------------------
        if (isMoving == true) {
            spriteCounter++;
            if (spriteCounter > 10) {
                if (spriteNum == 1) spriteNum = 2;
                else if (spriteNum == 2) spriteNum = 3;
                else if (spriteNum == 3) spriteNum = 1;
                spriteCounter = 0;
            }
        }
        
        // ------------------------------------------
        // 8. INVINCIBILITY FRAMES TICK FLICKER
        // ------------------------------------------
        if (invincible == true) {
            invincibleCounter++;
            
            if (invincibleCounter % 5 == 0) {
                drawing = !drawing; 
            }
            
            if (invincibleCounter > 150) {
                invincible = false;
                drawing = true; 
                invincibleCounter = 0;
            }
        }

        // ==========================================
        // 9. ☔ SONIC-STYLE SOLID HARD UMBRELLA MATRIX (✨ PIXEL-PERFECT REAL-TIME)
        // ==========================================
        for (int i = 0; i < gp.objUmb.length; i++) {
            if (gp.objUmb[i] != null) {
                object.Umbrella umb = gp.objUmb[i];
                
                // 1. Sukat at posisyon ni Darwin sa mundo
                Rectangle playerBounds = new Rectangle(
                    (int)worldX + solidArea.x, 
                    (int)worldY + solidArea.y, 
                    solidArea.width, 
                    solidArea.height
                );
                
                // 2. Kunin ang pinalaking solidArea ng payong at idikit sa worldX at worldY nito
                Rectangle umbBounds = new Rectangle(
                    umb.worldX + umb.solidArea.x, 
                    umb.worldY + umb.solidArea.y, 
                    umb.solidArea.width, 
                    umb.solidArea.height
                );

                // 3. Real-time intersection checking
                if (playerBounds.intersects(umbBounds)) {
                    
                    // A. PHYSICAL COLLISION CHECKS
                    if (yVelocity > 0 && worldY + solidArea.y + solidArea.height - yVelocity <= umb.worldY + umb.solidArea.y + 15) {
                        // ✨ ACCURATE HEIGHT LANDING DETECTOR: Bawasan ang height variables para perpektong lapat sa top edge!
                        worldY = umb.worldY + umb.solidArea.y - (solidArea.y + solidArea.height); 
                        yVelocity = 0;
                        onGround = true; 
                    } else if (yVelocity < 0) {
                        worldY = umb.worldY + umb.solidArea.y + umb.solidArea.height;
                        yVelocity = 0;
                    } else {
                        worldX = oldX; 
                    }
                    
                    // B. DAMAGE & RE-ROUTE LAYER
                    if (invincible == false) {
                        life--; // Bawas isang puso
                        
                        this.isKnockedBack = true;
                        if ((worldX + (solidArea.width / 2)) < (umb.worldX + (umb.solidArea.width / 2))) {
                            this.knockbackDirection = "left"; 
                        } else {
                            this.knockbackDirection = "right"; 
                        }

                        if (life <= 0) {
                            lifeCount--; 
                            if (lifeCount > 0) {
                                setDefaultValues(); 
                                this.invincible = false;
                                this.invincibleCounter = 0;
                                this.drawing = true; 
                                this.isKnockedBack = false;
                            } else {
                                System.out.println("🚨 GAME OVER: Bumabalik sa WindowBuilder...");
                                gp.setVisible(false); 
                                resetAll(); 
                            }
                        } else {
                            invincible = true; 
                        }
                    }
                }
            }
        }
    }
    
    // ==========================================
    // 🎨 RENDER DRAW COMPONENT GRAPHICS
    // ==========================================
    public void draw(Graphics2D g2) {
        if (drawing == false) {
            return;
        }
        
        BufferedImage image = null;
        
        if (isMoving == false) {
            image = imgStraight;
        } else {
            if (spriteNum == 1) image = imgRight1;
            if (spriteNum == 2) image = imgRight2;
            if (spriteNum == 3) image = imgRight3;
        }

        int screenX = (int)worldX - gp.cameraX;
        int screenY = (int)worldY - gp.cameraY;

        if (image != null) {
            int width = gp.tileSize + 26; 
            int height = gp.tileSize;     

            if ("left".equals(direction)) {
                g2.drawImage(image, screenX + width, screenY, -width, height, null);
            } else {
                g2.drawImage(image, screenX, screenY, width, height, null);
            }
        }
    }

    // ==========================================
    // 💥 EXTERNAL OBSTACLE TRIGGER METHOD HOOKS
    // ==========================================
    public void damagePlayer() {
        if (invincible == false) {
            life--; 
            invincible = true;
            
            if ("right".equals(direction)) {
                worldX -= (gp.tileSize * 1.5); 
            } else if ("left".equals(direction)) {
                worldX += (gp.tileSize * 1.5); 
            }
            
            yVelocity = -8.0; 
            onGround = false;
            
            if (life < 0) life = 0; 
        }
    }

    public void resetAll() {
        this.lifeCount = 3;
        setDefaultValues(); 
        this.invincible = false;
        this.invincibleCounter = 0;
        this.drawing = true;
        this.isKnockedBack = false;
        this.knockbackTimer = 0;

        // Ligtas na linisin ang keyboard buffer state tuwing mag-re-reset ang game profile
        if (gp != null && gp.keyH != null) {
            gp.keyH.upPressed = false;
            gp.keyH.downPressed = false;
            gp.keyH.leftPressed = false;
            gp.keyH.rightPressed = false;
        }
    }
}