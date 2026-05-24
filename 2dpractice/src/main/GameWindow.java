package main;

import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

public class GameWindow extends JFrame {

    private JPanel contentPane;
    private CardLayout cardLayout;
    private JPanel menuPanel;
    private GamePanel gamePanel;

    public GameWindow() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setExtendedState(JFrame.MAXIMIZED_BOTH); // Full Screen setup
        
        cardLayout = new CardLayout();
        contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(0, 0, 0, 0));
        contentPane.setLayout(cardLayout);
        setContentPane(contentPane);

        menuPanel = new JPanel();
        menuPanel.setBackground(new Color(20, 25, 35)); 
        menuPanel.setLayout(null); 
        contentPane.add(menuPanel, "MenuScreen");

        JLabel lblTitle = new JLabel("Darwin's Journey Home");
        lblTitle.setFont(new Font("Tahoma", Font.BOLD, 48)); 
        lblTitle.setForeground(Color.WHITE);
        lblTitle.setBounds(300, 180, 600, 60); 
        menuPanel.add(lblTitle);

        JButton btnNewGame = new JButton("NEW GAME");
        btnNewGame.setFont(new Font("Tahoma", Font.BOLD, 16));
        btnNewGame.setBounds(450, 340, 180, 45); 
        menuPanel.add(btnNewGame);

        JButton btnHelp = new JButton("HELP");
        btnHelp.setFont(new Font("Tahoma", Font.BOLD, 16));
        btnHelp.setBounds(450, 410, 180, 45); 
        menuPanel.add(btnHelp);

        JButton btnExit = new JButton("EXIT");
        btnExit.setFont(new Font("Tahoma", Font.BOLD, 16));
        btnExit.setBounds(450, 480, 180, 45); 
        menuPanel.add(btnExit);

        // INITIALIZE THE GAME PANEL
        gamePanel = new GamePanel(); 
        contentPane.add(gamePanel, "GameScreen");

        btnNewGame.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // 1. Ipakita ang screen ng laro
                cardLayout.show(contentPane, "GameScreen");
                
                // 2. I-load ang mga boxes mo sa objBox array bago mag-start ang graphics loop
                if (gamePanel.aSetter != null) {
                    gamePanel.aSetter.setObjects(1);
                }
                
                // 3. Ibigay ang focus sa keyboard para makagalaw si Darwin gamit ang WASD
                gamePanel.requestFocusInWindow(); 
                
                // 4. Simulan ang thread ng laro
                gamePanel.startGameThread(); 
            }
        });

        btnExit.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });
    }
}