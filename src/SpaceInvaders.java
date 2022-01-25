////////////////////////////////////////////////////////////////////////////////
// Checklist:
// Currently none :D
////////////////////////////////////////////////////////////////////////////////
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.event.*;

public class SpaceInvaders extends JPanel implements KeyListener, Runnable, MouseListener {
    // Game States
    public static boolean menu = true;
    public static boolean play = false;
    public static boolean credit = false;
    public static boolean help = false;
    public static boolean win = false;
    public static boolean lose = false;

    // Game Stats
    // Spaceship
    public static int posxShip = 463;
    public static int posyShip = 650;

    // Spaceship bullet
    public static int posxBullet;
    public static int posyBullet;
    public static boolean shot = false;

    // Alien
    public static int speed = 1;
    public static int death = 0;
    public static int turn_count = 0;
    public static int alien_array[][][] = {{{0, 0},   {100, 0},   {200, 0},   {300, 0}},
                                           {{0, 100}, {100, 100}, {200, 100}, {300, 100}},
                                           {{0, 200}, {100, 200}, {200, 200}, {300, 200}}};
    public static boolean moveLeft = false;
    public static boolean flag = true;

    // Game Images
    public static BufferedImage space;
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
            space = ImageIO.read(new File("space.png"));
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

    public static void collision_detection() {
        for (int[][] i : alien_array) {
            for (int[] j : i) {
                // Rectangle(x-coord, y-coord, width, height)
                Rectangle alienBox = new Rectangle(j[0], j[1], 32, 32);
                Rectangle bulletBox = new Rectangle(posxBullet, posyBullet, 10, 32);
                if (alienBox.intersects(bulletBox)) {
                    death++; speed++;
                    j[0] = -999; j[1] = -999;
                    shot = false;
                    break;
                }
            }
        }
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

    public static void leftBound() {
        for (int i = 0; i < 4; ++i) {
            for (int j = 0; j < 3; ++j) {
                if (alien_array[j][i][0] == -999 || alien_array[j][i][1] == -999) continue;
                if (alien_array[j][i][0] < 0) {
                    moveLeft = false;
                    weAreGoingDown();
                    turn_count++;
                    return;
                }
            }
        }
    }

    public static void rightBound() {
        for (int i = 3; i >= 0; --i) {
            for (int j = 0; j < 3; ++j) {
                if (alien_array[j][i][0] == -999 || alien_array[j][i][1] == -999) continue;
                if (alien_array[j][i][0] > 992) {
                    moveLeft = true;
                    weAreGoingDown();
                    turn_count++;
                    return;
                }
            }
        }
    }

    public static void movingLeft() {
        for (int[][] i : alien_array) {
            for (int[] j : i) {
                if (j[0] == -999 || j[1] == -999) continue;
                j[0] -= speed;
            }
        }
    }

    public static void movingRight() {
        for (int[][] i : alien_array) {
            for (int[] j : i) {
                if (j[0] == -999 || j[1] == -999) continue;
                j[0] += speed;
            }
        }
    }

    public static void alienUpdate() {
        if (moveLeft) {
            leftBound();
        }
        else {
            rightBound();
        }
        if (moveLeft) {
            movingLeft();
        }
        else {
            movingRight();
        }
    }

    public static void gameOver() {
        if (death == 12) {
            play = false;
            win = true;
        }
        for (int i = 2; i >= 0; --i) {
            for (int j = 0; j < 3; ++j) {
                if (alien_array[i][j][0] == -999 || alien_array[i][j][1] == -999) continue;
                if (alien_array[i][j][1] > 600) {
                    play = false;
                    lose = true;
                    break;
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
        // Space key
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

    }

    public void mouseEntered(MouseEvent e) {

    }

    public void mouseExited(MouseEvent e) {

    }

    public void mousePressed(MouseEvent e) {
        int x = e.getX();
        int y = e.getY();

        if(x >=Frame.WIDTH/2+290 && x <= Frame.WIDTH/2+410)
        {
            if(y>=305 && y<=365)
            {
                System.out.println("Play Pressed");
                menu = false; play = true;
            }
        }

        if(x >=Frame.WIDTH/2+290 && x <= Frame.WIDTH/2+415)
        {
            if(y>=435 && y<=495)
            {
                System.out.println("Help Pressed");
                menu = false; help = true;
            }
        }

        if(x >=Frame.WIDTH/2+275 && x <= Frame.WIDTH/2+460)
        {
            if(y>=565 && y<=625)
            {
                System.out.println("Credits Pressed");
                menu = false; credit = true;
            }

        }
    }

    public void mouseReleased(MouseEvent e) {

    }

    // PaintComponent
    public void paintComponent(Graphics g) {
        if (menu) {
            super.paintComponent(g);
            g.drawImage(space,0,0,null);
            g.drawImage(alien,600,350,null);
            g.setColor(Color.WHITE);
            g.setFont(new Font("Arial", Font.BOLD, 80));
            g.drawString("SPACE INVADERS",Frame.WIDTH/2+150, 190);
            g.setFont(new Font("Arial", Font.BOLD, 50));
            g.drawString("Play",Frame.WIDTH/2+300, 350);
            g.drawString("Help",Frame.WIDTH/2+300, 480);
            g.drawString("Credits",Frame.WIDTH/2+280, 610);
            g.drawRect(Frame.WIDTH/2+290, 305, 120, 60);
            g.drawRect(Frame.WIDTH/2+290, 435, 125, 60);
            g.drawRect(Frame.WIDTH/2+275, 565, 185, 60);
        }
        if (play) {
            super.paintComponent(g);
            g.drawImage(spaceship, posxShip, posyShip, this);
            if (shot) {
                bulletUpdate();
                collision_detection();
                g.drawImage(spaceship_bullet, posxBullet, posyBullet, this);
            }
            alienUpdate();
            for (int[][] i : alien_array) {
                for (int[] j : i) {
                    if (j[0] == -999 || j[1] == -999) continue;
                    g.drawImage(alien, j[0], j[1], this);
                }
            }
            gameOver();
        }
        if (credit) {

        }
        if (help) {

        }
        if (win) {

        }
        if (lose) {

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
