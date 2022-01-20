////////////////////////////////////////////////////////////////////////////////
// Checklist:
// refresh rate counter alien
// different variable for it
////////////////////////////////////////////////////////////////////////////////
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.event.*;

public class SpaceInvaders extends JPanel implements KeyListener, Runnable, MouseListener {
    // Game States
    public static boolean intro = true;
    public static boolean play = false;

    // Game Stats
    // Mouse
    public static int posX;
    public static int posY;

    // Spaceship
    public static int posxShip = 463;
    public static int posyShip = 650;

    // Spaceship bullet
    public static int posxBullet;
    public static int posyBullet;
    public static boolean shot = false;

    // Alien
    public static int posxAlien;
    public static int posyAlien;
    public static int speed = 1;
    public static int death = 0;
    public static int alien_array[][][] = {{{0, 0},   {100, 0},   {200, 0},   {300, 0}},
                                           {{0, 100}, {100, 100}, {200, 100}, {300, 100}},
                                           {{0, 200}, {100, 200}, {200, 200}, {300, 200}}};
    public static boolean moveLeft = false;

    // Game Images
    public static BufferedImage spaceship;
    public static BufferedImage spaceship_bullet;
    public static BufferedImage alien;
    // public static BufferedImage alien_bullet;

    // JPanel Constructor
    public SpaceInvaders()
    {
        setPreferredSize(new Dimension(1024, 768));
        setBackground(new Color(0, 0, 0));
        this.setFocusable(true);
        addKeyListener(this);
        addMouseListener(this);
        try
        {
            spaceship = ImageIO.read(new File("spaceship.png")); // 98 x 100 px
            spaceship_bullet = ImageIO.read(new File("spaceship_bullet.png")); // 10 x 32 px
            alien = ImageIO.read(new File("alien.png"));
            // alien_bullet = ImageIO.read(new File("alien_bullet.png"));
        }
        catch (Exception e)
        {
            System.out.println("File cannot be found");
        }
        // When using Runnable, you have to create a new Thread
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
                Thread.sleep(10); // The commands after while(true) but before try will execute once every 80 milliseconds
            }
            catch(Exception e)
            {
                e.printStackTrace();
            }
        }
    }

    public static void bulletUpdate() {
        if (posyBullet <= 0) {
            shot = false;
        }
        posyBullet -= 10;
    }

    public static void weAreGoingDown() {
        for (int[][] i : alien_array) {
            for (int[] j : i) {
                if (j[0] != -999 || j[1] != -999) {
                    j[1] += 50;
                }
            }
        }
    }

    public static void alienUpdate() {
        if (moveLeft) {
            for (int i = 0; i < 4; ++i) {
                for (int j = 0; j < 3; ++j) {
                    if (alien_array[j][i][0] != -999 && alien_array[j][i][0] < 0) {
                        moveLeft = false;
                        weAreGoingDown();
                        break;
                    }
                }
            }
        }
        else {
            for (int i = 3; i >= 0; --i) {
                for (int j = 0; j < 3; ++j) {
                    if (alien_array[j][i][0] != -999 && alien_array[j][i][0] > 992) {
                        moveLeft = true;
                        weAreGoingDown();
                        break;
                    }
                }
            }
        }
        if (moveLeft) {
            for (int[][] i : alien_array) {
                for (int[] j : i) {
                    j[0] -= speed;
                }
            }
        }
        else {
            for (int[][] i : alien_array) {
                for (int[] j : i) {
                    j[0] += speed;
                }
            }
        }
    }

    // KeyListener Methods
    public void keyPressed(KeyEvent e) {
        //Right Key
        if(e.getKeyCode()==39 || e.getKeyChar() == 'd') {
            System.out.println("Right");
            posxShip += 5;
            if (posxShip > 926) {
                posxShip = 926;
            }
        }
        //Left Key
        else if (e.getKeyCode()==37 || e.getKeyChar() == 'a') {
            System.out.println("Left");
            posxShip -= 5;
            if (posxShip < 0) {
                posxShip = 0;
            }
        }
        //Up Key
        else if (e.getKeyCode()==38 || e.getKeyChar() == 'w') {
            System.out.println("Up");
            posyShip -= 5;
            if (posyShip < 0) {
                posyShip = 0;
            }
        }
        //Down Key
        else if (e.getKeyCode()==40 || e.getKeyChar() == 's') {
            System.out.println("Down");
            posyShip += 5;
            if (posyShip > 668) {
                posyShip = 668;
            }
        }
        else if (e.getKeyChar() == ' ' && !shot) {
            System.out.println("Shoot");
            shot = true;
            posxBullet = posxShip + 44;
            posyBullet = posyShip;
        }
    }

    public void keyReleased(KeyEvent e) {

    }

    public void keyTyped(KeyEvent e) {

    }

    // MouseListener Methods
    public void mouseClicked(MouseEvent e) {
        posX = e.getX();
        posY = e.getY();
    }

    public void mouseEntered(MouseEvent e) {

    }

    public void mouseExited(MouseEvent e) {

    }

    public void mousePressed(MouseEvent e) {

    }

    public void mouseReleased(MouseEvent e) {

    }

    // PaintComponent
    public void paintComponent(Graphics g) {
        // if(play)
        // {
        //     super.paintComponent(g);
        //     g.drawImage(spaceship, posxShip, posyShip, this);
        // }
        super.paintComponent(g);
        g.drawImage(spaceship, posxShip, posyShip, this);
        if (shot) {
            bulletUpdate();
            g.drawImage(spaceship_bullet, posxBullet, posyBullet, this);
        }
        alienUpdate();
        for (int[][] i : alien_array) {
            for (int[] j : i) {
                if (j[0] == -999 || j[1] == -999) continue;
                g.drawImage(alien, j[0], j[1], this);
            }
        }
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
