package main;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import javax.swing.JPanel;
import environment.Stage1Seaside;
import darwin1.player;
import object.Box;
import tile.TileManager; 

public class GamePanel extends JPanel implements Runnable {

    // SCREEN SETTINGS
    public final int originalTileSize = 16; 
    public final int scale = 4;
    public final int tileSize = originalTileSize * scale; // 64px
    public final int maxScreenCol = 16;
    public final int maxScreenRow = 12;
    public final int screenWidth = tileSize * maxScreenCol; 
    public final int screenHeight = tileSize * maxScreenRow; 

    // GRID SETTINGS (Pang-Matrix Collision)
    public final int maxWorldCol = 100; 
    public final int maxWorldRow = 12;  

    // SYSTEM VARIABLES
    Thread gameThread;
    public KeyHandler keyH = new KeyHandler(this);
    
    // Pure declarations para iwas sa circular dependency crash loops
    public Stage1Seaside stageSeaside;
    public TileManager tileM; 
    public player player1; 
    public UI ui = new UI(this);
    
    public Box objBox[] = new Box[10]; 
    public object.Umbrella objUmb[] = new object.Umbrella[10];
    public AssetSetter aSetter = new AssetSetter(this);

    // CAMERA POSITIONS
    public int cameraX = 0;
    public int cameraY = 0; 
    
    public java.awt.image.BufferedImage sandTile;  
    
 // ✨ GAME STATES CONFIGURATION
    public int gameState;
    public final int titleState = 0; // Main Menu Screen
    public final int playState = 1;  // Actual Game Loop
    
    public GamePanel() {
        // 1. PANEL CONTEXT SETUP
        this.setPreferredSize(new Dimension(screenWidth, screenHeight));
        this.setBackground(Color.black);
        this.setDoubleBuffered(true);
        this.addKeyListener(keyH);
        this.setFocusable(true);
        
        // ✨ FIX: Gawing playState agad ang laro para hindi mag-blank ang screen sa startup
        gameState = playState; 
        
        // 2. SUNOD-SUNOD NA PAGBUHAY SA MGA OBJECTS (ORDER IS CRITICAL)
        this.stageSeaside = new Stage1Seaside(this);
        this.tileM = new TileManager(this); 
        this.player1 = new player(this, keyH); 
        
        // 3. I-SET ANG MGA KAHON AT PAYONG
        aSetter.setObjects(1); 
        
        // 4. LOAD ANG IMAGE CACHE
        try {
            sandTile = javax.imageio.ImageIO.read(getClass().getResourceAsStream("/tile/sandTiles.png"));
        } catch(Exception e) {
            System.out.println("Babala: Hindi ma-load ang sandTiles.png sa GamePanel!");
        }
    }

    public void startGameThread() {
        if (gameThread == null) {
            gameThread = new Thread(this);
            gameThread.start();
        }
    }
 // Sa loob ng constructor ng GamePanel mo (o sa setupGame method kung meron ka):
    public void setupGame() {
        // ✨ Simulan natin ang laro sa Title Screen / Main Menu!
        gameState = titleState; 
    }

    @Override
    public void run() {
        double drawInterval = 1000000000 / 60;
        double delta = 0;
        long lastTime = System.nanoTime();
        long currentTime;

        while (gameThread != null) {
            currentTime = System.nanoTime();
            delta += (currentTime - lastTime) / drawInterval;
            lastTime = currentTime;

            if (delta >= 1) {
                update();
                repaint();
                delta--;
            }
        }
    }
    

    public void update() {
        if (player1 != null) {
            player1.update();
        }
        
        // 🎥 CAMERA POSITION FIX
        // ✨ FIX: Ginamit natin ang screenWidth variable ng GamePanel para consistent sa coordinate calculations mo
        if (player1 != null && player1.worldX > (screenWidth / 3)) {
            cameraX = (int)player1.worldX - (screenWidth / 3);
        } else {
            cameraX = 0;
        }
        
        // LEFT BOUNDARY
        if (cameraX < 0) {
            cameraX = 0;
        }
        
        // RIGHT BOUNDARY
        if (stageSeaside != null && stageSeaside.worldWidth > 0 && cameraX > stageSeaside.worldWidth - screenWidth) {
            cameraX = stageSeaside.worldWidth - screenWidth;
        }
        
        cameraY = 0;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D)g;
        
        // 🌅 LAYER 1: BACKGROUND ENVIRONMENT (Dito lapat ang background mo, pre!)
        if (stageSeaside != null) {
            stageSeaside.draw(g2); 
        }
        
        // 🗺️ LAYER 2: MAP TILES
        if (tileM != null) {
            tileM.draw(g2);
        }

        // 📦 LAYER 3: OBJECTS (Boxes)
        for (int i = 0; i < objBox.length; i++) {
            if (objBox[i] != null) {
                objBox[i].draw(g2, this);
            }
        }

        // ☔ LAYER 3.5: OBSTACLES (Umbrellas)
        for (int i = 0; i < objUmb.length; i++) {
            if (objUmb[i] != null) {
                objUmb[i].draw(g2, this);
            }
        }

        // 🏃‍♂️ LAYER 4: PLAYER (Darwin)
        if (player1 != null) {
            player1.draw(g2);
        }

        // 👑 LAYER 5: HUD/UI (Hearts & Heads)
        if (ui != null) {
            ui.draw(g2);
        }

        g2.dispose();
    }
}