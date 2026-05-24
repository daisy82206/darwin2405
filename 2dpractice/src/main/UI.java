package main;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;

public class UI {
    GamePanel gp;
    BufferedImage heart_full, heart_blank, darwin_head;
    Font marfont;

    public UI(GamePanel gp) {
        this.gp = gp;
        // Pwede mong gamitin ang default font ng Java habang wala ka pang .ttf file
        this.marfont = new Font("Arial", Font.BOLD, 30);
        loadImages();
    }

    public void loadImages() {
        // ✨ INAYOS: Tinugma natin ang saktong pangalan ng mga PNG mo (darwinHead at heart)
        try {
            darwin_head = ImageIO.read(getClass().getResourceAsStream("/ui/darwinHead.png"));
        } catch (Exception e) {
            System.out.println("⚠️ Babala: Hindi mahanap ang /ui/darwinHead.png sa res folder!");
        }

        try {
            heart_full = ImageIO.read(getClass().getResourceAsStream("/ui/heart.png"));
        } catch (Exception e) {
            System.out.println("⚠️ Babala: Hindi mahanap ang /ui/heart.png sa res folder!");
        }

        // Pansamantala: Gagamitin muna natin ang 'heart' mo para sa heart_blank 
        // para hindi mag-error habang wala ka pang silhouette o kupas na puso.
        try {
            heart_blank = ImageIO.read(getClass().getResourceAsStream("/ui/heart.png"));
        } catch (Exception e) {
            System.out.println("⚠️ Babala: Hindi mahanap ang backup file para sa heart_blank!");
        }
    }
    
    public void draw(Graphics2D g2) {
        if (gp.player1 != null) {
            
            int x = 20; 
            int y = 20; 
            
            // 1. DRAW DARWIN HEAD & LIFE COUNT (Darwin x 3)
            if (darwin_head != null) {
                g2.drawImage(darwin_head, x, y, gp.tileSize, gp.tileSize, null);
            }
            
            g2.setFont(marfont);
            g2.setColor(Color.WHITE);
            g2.drawString("x " + gp.player1.lifeCount, x + gp.tileSize + 15, y + gp.tileSize - 15);

            // 2. DRAW HEARTS (Dynamic - Mawawala ang puso kapag nabawasan!)
            int heartX = x;
            int heartY = y + gp.tileSize + 10; 
            
            for (int i = 0; i < gp.player1.maxLife; i++) {
                if (i < gp.player1.life) {
                    // BUHAY NA PUSO: I-draw lang kung hindi pansamantalang nakatago sa flicker
                    if (heart_full != null) {
                        if (gp.player1.invincible == true && gp.player1.drawing == false) {
                            // Kukurap lang ang natitirang buhay na puso
                        } else {
                            g2.drawImage(heart_full, heartX, heartY, gp.tileSize, gp.tileSize, null);
                        }
                    }
                } else {
                    // ✨ FIX: SELYADONG BURAL LOGIC
                    // Huwag mag-draw ng kahit ano rito (iwanang bakante) para tuluyang "mabawasan" ang puso visually!
                }
                heartX += gp.tileSize + 5; // Pagtabi-tabihin pa rin ang spacing
            }
        }
    }
    
 // Ilagay ito sa loob ng UI.java mo para mawala ang error sa GamePanel!
    public void drawTitleScreen(Graphics2D g2) {
        g2.setColor(new java.awt.Color(30, 40, 60));
        g2.fillRect(0, 0, gp.screenWidth, gp.screenHeight);

        g2.setFont(marfont.deriveFont(java.awt.Font.BOLD, 50f));
        g2.setColor(java.awt.Color.WHITE);
        
        String titleText = "DARWIN'S ADVENTURE";
        int titleX = gp.screenWidth / 2 - (gp.tileSize * 3); 
        int titleY = gp.screenHeight / 3;
        g2.drawString(titleText, titleX, titleY);

        g2.setFont(marfont.deriveFont(java.awt.Font.PLAIN, 25f));
        g2.setColor(java.awt.Color.YELLOW);
        String instruction = "PRESS ENTER TO START NEW GAME";
        g2.drawString(instruction, titleX + 40, titleY + (gp.tileSize * 2));
    }
}