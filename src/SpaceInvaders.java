////////////////////////////////////////////////////////////////////////////////
// Checklist:
// Add comments.
////////////////////////////////////////////////////////////////////////////////
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.*;
import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.event.*;

public class SpaceInvaders extends JPanel implements KeyListener, Runnable, MouseListener {
    // Game States
    public static boolean menu = true;
    public static boolean play = false;
    public static boolean credit = false;
    public static boolean help = false;
    public static boolean lb = false;
    public static boolean win = false;
    public static boolean lose = false;

    // Game Stats
    // Environment
    public static double time = 0;

    // Leaderboard
    public static String leaderboard[] = new String[10];

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
    public static int alien_array[][][] = {{{0, 5},   {100, 5},   {200, 5},   {300, 5}},
                                           {{0, 105}, {100, 105}, {200, 105}, {300, 105}},
                                           {{0, 205}, {100, 205}, {200, 205}, {300, 205}}};
    public static boolean moveLeft = false;

    // Game Images
    public static BufferedImage space;
    public static BufferedImage spaceship;
    public static BufferedImage spaceship_bullet;
    public static BufferedImage alien;
    public static BufferedImage big_alien;

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
            spaceship_bullet = ImageIO.read(new File("spaceship_bullet.png")); // 12 x 34 px
            alien = ImageIO.read(new File("alien.png")); // 50 x 36 px
            big_alien = ImageIO.read(new File("big_alien.png")); // 280 x 204 px
        }
        catch (Exception exception)
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
        retrievingLeaderboard();
        while (true)
        {
            repaint();
            try
            {
                Thread.sleep(10); // The commands after while(true) but before try will execute once every 10 milliseconds
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
    }

    // Environment methods
    public static void timerUpdate() {
        if (play && !win && !lose) {
            time += 0.01;
        }
    }

    public static void gameOver() {
        if (death == 12) {
            play = false; win = true;
            leaderboardUpdate();
        }
        for (int i = 2; i >= 0; --i) {
            for (int j = 0; j < 3; ++j) {
                if (alien_array[i][j][0] == -999 || alien_array[i][j][1] == -999) continue;
                if (alien_array[i][j][1] > 514) {
                    play = false; lose = true;
                    time = 999.99;
                    leaderboardUpdate();
                    break;
                }
            }
        }
    }

    public static void leaderboardUpdate() {
        try {
            PrintWriter outputFile = new PrintWriter(new FileWriter("leaderboard.txt"));
            for (int i = 0; i < 10; ++i) {
                if (time < Double.parseDouble(leaderboard[i])) {
                    for (int j = 8; j >= i; --j) {
                        leaderboard[j + 1] = leaderboard[j];
                    }
                    leaderboard[i] = "" + (double) Math.round(time * 100) / 100;
                    break;
                }
            }
            for (String s : leaderboard) {
                outputFile.println(s);
            }
            outputFile.close();
        }
        catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    public static void retrievingLeaderboard() {
        try {
            Scanner inputFile = new Scanner(new File("leaderboard.txt"));

            int index = 0;
            while (inputFile.hasNextLine()) {
                leaderboard[index] = inputFile.nextLine();
                index ++;
            }
            inputFile.close();
        }
        catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    public static void resetVariables() {
        posxShip = 463; posyShip = 650;
        alien_array[0][0][0] = 0;   alien_array[0][0][1] = 5;
        alien_array[0][1][0] = 100; alien_array[0][1][1] = 5;
        alien_array[0][2][0] = 200; alien_array[0][2][1] = 5;
        alien_array[0][3][0] = 300; alien_array[0][3][1] = 5;
        alien_array[1][0][0] = 0;   alien_array[1][0][1] = 105;
        alien_array[1][1][0] = 100; alien_array[1][1][1] = 105;
        alien_array[1][2][0] = 200; alien_array[1][2][1] = 105;
        alien_array[1][3][0] = 300; alien_array[1][3][1] = 105;
        alien_array[2][0][0] = 0;   alien_array[2][0][1] = 205;
        alien_array[2][1][0] = 100; alien_array[2][1][1] = 205;
        alien_array[2][2][0] = 200; alien_array[2][2][1] = 205;
        alien_array[2][3][0] = 300; alien_array[2][3][1] = 205;
        speed = 1; death = 0;
        moveLeft = false;
        time = 0;
    }

    // Bullet methods
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
                Rectangle alienBox = new Rectangle(j[0], j[1], 50, 36);
                Rectangle bulletBox = new Rectangle(posxBullet, posyBullet, 12, 34);
                if (alienBox.intersects(bulletBox)) {
                    death++; speed++;
                    j[0] = -999; j[1] = -999;
                    shot = false;
                    break;
                }
            }
        }
    }

    // Alien methods
    public static void leftBound() {
        for (int i = 0; i < 4; ++i) {
            for (int j = 0; j < 3; ++j) {
                if (alien_array[j][i][0] == -999 || alien_array[j][i][1] == -999) continue;
                if (alien_array[j][i][0] < 0) {
                    moveLeft = false;
                    weAreGoingDown();
                    return;
                }
            }
        }
    }

    public static void rightBound() {
        for (int i = 3; i >= 0; --i) {
            for (int j = 0; j < 3; ++j) {
                if (alien_array[j][i][0] == -999 || alien_array[j][i][1] == -999) continue;
                if (alien_array[j][i][0] > 974) {
                    moveLeft = true;
                    weAreGoingDown();
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

    public static void weAreGoingDown() {
        for (int[][] i : alien_array) {
            for (int[] j : i) {
                if (j[0] != -999 || j[1] != -999) {
                    j[1] += 48;
                }
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

    // KeyListener Methods
    public void keyPressed(KeyEvent e) {
        //Right Key
        if(e.getKeyCode()==39 || e.getKeyChar() == 'd') {
            System.out.println("Right");
            posxShip += 8;
            if (posxShip > 926) {
                posxShip = 926;
            }
        }
        //Left Key
        else if (e.getKeyCode()==37 || e.getKeyChar() == 'a') {
            System.out.println("Left");
            posxShip -= 8;
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

        if (menu) {
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
                if(y>=405 && y<=465)
                {
                    System.out.println("Help Pressed");
                    menu = false; help = true;
                }
            }

            if(x >=Frame.WIDTH/2+260 && x <= Frame.WIDTH/2+460)
            {
                if(y>=505 && y<=565)
                {
                    System.out.println("Credits Pressed");
                    menu = false; credit = true;
                }

            }

            if(x >=Frame.WIDTH/2+225 && x <= Frame.WIDTH/2+495)
            {
                if(y>=605 && y<=665)
                {
                    System.out.println("HighScore Pressed");
                    menu = false; lb = true;
                }
            }
        }

        if (credit) {
            if(x >=Frame.WIDTH/2+50 && x <= Frame.WIDTH/2+180)
            {
                if(y>=650 && y<=710)
                {
                    System.out.println("Credit Back Pressed");
                    credit = false; menu = true;
                }
            }
        }

        if (help) {
            if(x >=Frame.WIDTH/2+50 && x <= Frame.WIDTH/2+180)
            {
                if(y>=650 && y<=710)
                {
                    System.out.println("Help Back Pressed");
                    help = false; menu = true;
                }
            }
        }

        if (lb) {
            if(x >=Frame.WIDTH/2+50 && x <= Frame.WIDTH/2+180)
            {
                if(y>=650 && y<=710)
                {
                    System.out.println("Leaderboard Back Pressed");
                    lb = false; menu = true;
                }
            }
        }

        if (win) {
            if (x >= 365 && x <= 655) {
                if (y >= 555 && y <= 615) {
                    System.out.println("Play Again Pressed");
                    resetVariables();
                    win = false; play = true;
                }
            }
            if(x >=Frame.WIDTH/2+50 && x <= Frame.WIDTH/2+180)
            {
                if(y>=650 && y<=710)
                {
                    System.out.println("Win Menu Pressed");
                    resetVariables();
                    win = false; menu = true;
                }
            }
        }

        if (lose) {
            if (x >= 365 && x <= 655) {
                if (y >= 555 && y <= 615) {
                    System.out.println("Play Again Pressed");
                    resetVariables();
                    lose = false; play = true;
                }
            }
            if(x >=Frame.WIDTH/2+50 && x <= Frame.WIDTH/2+180)
            {
                if(y>=650 && y<=710)
                {
                    System.out.println("Lose Menu Pressed");
                    resetVariables();
                    lose = false; menu = true;
                }
            }
        }
    }

    public void mouseReleased(MouseEvent e) {

    }

    // PaintComponent
    public void paintComponent(Graphics g) {
        timerUpdate();
        if (menu) {
            super.paintComponent(g);
            g.drawImage(space,0,0,null);
            g.drawImage(big_alien,600,350,null);
            g.setColor(Color.WHITE);
            g.setFont(new Font("Arial", Font.BOLD, 80));
            g.drawString("SPACE INVADERS",Frame.WIDTH/2+150, 190);
            g.setFont(new Font("Arial", Font.BOLD, 50));
            g.drawString("Play",Frame.WIDTH/2+300, 350);
            g.drawString("Help",Frame.WIDTH/2+300, 450);
            g.drawString("Credits",Frame.WIDTH/2+270, 550);
            g.drawString("HighScore",Frame.WIDTH/2+235, 650);
            g.drawRect(Frame.WIDTH/2+290, 305, 120, 60);
            g.drawRect(Frame.WIDTH/2+290, 405, 125, 60);
            g.drawRect(Frame.WIDTH/2+260, 505, 195, 60);
            g.drawRect(Frame.WIDTH/2+225, 605, 270, 60);
        }
        if (play) {
            super.paintComponent(g);
            g.drawImage(space,0,0,null);
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
            super.paintComponent(g);
            g.drawImage(space,0,0,null);
            g.setColor(Color.WHITE);
            g.setFont(new Font("Arial", Font.BOLD, 80));
            g.drawString("Credits",Frame.WIDTH/2+330, 190);
            g.drawString("Thank You For playing :D",Frame.WIDTH/2+20, 330);
            g.setFont(new Font("Arial", Font.BOLD, 30));
            g.drawString("Made by Anthony, and Hanfei",Frame.WIDTH/2+550,700);
            g.setFont(new Font("Arial", Font.BOLD, 50));
            g.drawString("Back",50, 700);
            g.drawRect(Frame.WIDTH/2+50, 650, 130, 60);
        }
        if (help) {
            super.paintComponent(g);
            g.drawImage(space,0,0,null);
            g.drawImage(alien,750,130,null);
            g.setColor(Color.WHITE);
            g.setFont(new Font("Arial", Font.BOLD, 80));
            g.drawString("How To Play",Frame.WIDTH/2+240, 100);
            g.setFont(new Font("Arial", Font.BOLD, 50));
            g.drawString("Game Objective",20,200);
            g.drawString("Player Control",Frame.WIDTH/2+540,480);
            g.setFont(new Font("Arial", Font.BOLD, 25));
            g.drawString("Aliens are real, and they are invading Earth,",20,250);
            g.drawString("we have to kill them before they reaches our atmosphere.",20,300);
            g.drawString("If you killed them before they approach, you win.",20,350);
            g.drawString("If they reaches the atmosphere, you lose",20,400);
            g.drawString("D,> to move right",Frame.WIDTH/2+540,530);
            g.drawString("A,< to move left",Frame.WIDTH/2+540,580);
            g.drawString("Space bar to shoot bullets",Frame.WIDTH/2+540,630);
            g.setFont(new Font("Arial", Font.BOLD, 50));
            g.drawString("Back",50, 700);
            g.drawRect(Frame.WIDTH/2+50, 650, 130, 60);
        }

        if (lb) {
            g.drawImage(space,0,0,null);
    		g.setColor(Color.WHITE);
    		g.setFont(new Font("Arial", Font.BOLD, 80));
    		g.drawString("Leaderboard",Frame.WIDTH/2+310, 150);
    		g.setFont(new Font("Arial", Font.BOLD, 50));
    		g.drawString("Back",55, 700);
    		g.drawRect(Frame.WIDTH/2+50, 650, 130, 60);
    		g.setFont(new Font("Arial", Font.PLAIN, 30));
    		g.drawString("1.", 320, 235);
    		g.drawString("2.", 320, 285);
    		g.drawString("3.", 320, 335);
    		g.drawString("4.", 320, 385);
    		g.drawString("5.", 320, 435);
    		g.drawString("6.", 320, 485);
    		g.drawString("7.", 320, 535);
    		g.drawString("8.",320, 585);
    		g.drawString("9.", 320, 635);
    		g.drawString("10.", 310, 685);

    		g.drawString(leaderboard[0], 380, 235);
    		g.drawString(leaderboard[1], 380, 285);
    		g.drawString(leaderboard[2], 380, 335);
    		g.drawString(leaderboard[3], 380, 385);
    		g.drawString(leaderboard[4], 380, 435);
    		g.drawString(leaderboard[5], 380, 485);
    		g.drawString(leaderboard[6], 380, 535);
    		g.drawString(leaderboard[7], 380, 585);
    		g.drawString(leaderboard[8], 380, 635);
    		g.drawString(leaderboard[9], 380, 685);

    		g.drawRect(Frame.WIDTH/2+300, 200, 520, 50);
    		g.drawRect(Frame.WIDTH/2+300, 250, 520, 50);
    		g.drawRect(Frame.WIDTH/2+300, 300, 520, 50);
    		g.drawRect(Frame.WIDTH/2+300, 350, 520, 50);
    		g.drawRect(Frame.WIDTH/2+300, 400, 520, 50);
    		g.drawRect(Frame.WIDTH/2+300, 450, 520, 50);
    		g.drawRect(Frame.WIDTH/2+300, 500, 520, 50);
    		g.drawRect(Frame.WIDTH/2+300, 550, 520, 50);
    		g.drawRect(Frame.WIDTH/2+300, 600, 520, 50);
    		g.drawRect(Frame.WIDTH/2+300, 650, 520, 50);

    		g.drawRect(Frame.WIDTH/2+300, 200, 60, 50);
    		g.drawRect(Frame.WIDTH/2+300, 250, 60, 50);
    		g.drawRect(Frame.WIDTH/2+300, 300, 60, 50);
    		g.drawRect(Frame.WIDTH/2+300, 350, 60, 50);
    		g.drawRect(Frame.WIDTH/2+300, 400, 60, 50);
    		g.drawRect(Frame.WIDTH/2+300, 450, 60, 50);
    		g.drawRect(Frame.WIDTH/2+300, 500, 60, 50);
    		g.drawRect(Frame.WIDTH/2+300, 550, 60, 50);
    		g.drawRect(Frame.WIDTH/2+300, 600, 60, 50);
    		g.drawRect(Frame.WIDTH/2+300, 650, 60, 50);
        }

        if (win) {
            super.paintComponent(g);
            g.setColor(Color.WHITE);
            g.drawImage(space,0,0,null);
            g.setFont(new Font("Arial", Font.BOLD, 80));
            g.drawString("YOU WON!", 304, 200); // width: 415
            g.setFont(new Font("Arial", Font.BOLD, 50));
            g.drawString("Play Again?", 369, 600); // width: 285
            g.drawRect(360, 550, 295, 60);
            g.drawString("Menu",50, 700);
            g.drawRect(Frame.WIDTH/2+50, 650, 130, 60);
        }

        if (lose) {
            super.paintComponent(g);
            g.setColor(Color.WHITE);
            g.drawImage(space,0,0,null);
            g.setFont(new Font("Arial", Font.BOLD, 80));
            g.drawString("GAME OVER", 269, 200); // width: 485
            g.setFont(new Font("Arial", Font.BOLD, 50));
            g.drawString("Play Again?", 369, 600); // width: 285
            g.drawRect(365, 550, 295, 60);
            g.drawString("Menu",50, 700);
            g.drawRect(Frame.WIDTH/2+50, 650, 130, 60);
        }
    }

    public static void main(String[] args) throws IOException {
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
