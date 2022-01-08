import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.event.*;


// Runnable is the interface which allows you to control
// the time until some processes get executed
public class SpaceInvaders extends JPanel implements KeyListener, Runnable
{
    // Game States
    public static boolean intro = false;
    public static boolean play = true;

    // Game Stats


    // Game Images
    public static BufferedImage alien;
    public static BufferedImage spaceship;
    public static BufferedImage alien_bullet;
    public static Bufferedimage spaceship_bullet;

    // JPanel Constructor
    public SpaceInvaders()
    {
        setPreferredSize(new Dimension(640,480));
        setBackground(new Color(255,255,255));
        this.setFocusable(true);
        addKeyListener(this);
        try
        {
            mario = ImageIO.read(new File("mario.png"));
            bullet = ImageIO.read(new File("bullet.png"));
        }
        catch (Exception e)
        {
            System.out.println("File cannot be found");
        }
        // When using Runnable, you have to
        // create a new Thread
        Thread thread = new Thread(this);
        thread.start();
    }

    // Threading Method
    public void run()
    {
        while(true)
        {
            repaint();
            try
            {
                Thread.sleep(80); // The commands after while(true) but before try will execute once every 80 milliseconds
            }
            catch(Exception e)
            {
                e.printStackTrace();
            }
        }
    }


    // KeyListener Methods
    public void keyPressed(KeyEvent e) {

    }
    public void keyReleased(KeyEvent e) {

    }

    public void keyTyped(KeyEvent e) {

    }

    // PaintComponent
    public void paintComponent(Graphics g) {

    }

    public static void main(String[] args) {
        // Create a frame
        JFrame myFrame = new JFrame("ALIENS ARE REAL");

        // Create a panel to put inside the frame
        SpaceInvaders myPanel = new SpaceInvaders();
        myFrame.add(myPanel);

        // Maximize your frame to the size of the panel
        myFrame.pack();

        // Set the visibility of the frame to visible
        myFrame.setVisible(true);
    }
}
