package game;

import java.awt.EventQueue;
import javax.swing.JFrame;

/**
 * Launches the Smart Jets game
 */
public class Main extends JFrame {

    public Main() {

        add(new Game());
        setTitle("Smart Jets");
        pack();
        setResizable(false);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }
    
    public static void main(String[] args) {
        
        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {                
                JFrame m = new Main();
                m.setVisible(true);                
            }
        });
    }
}
