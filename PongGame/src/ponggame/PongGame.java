/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ponggame;

import javax.swing.*;
import java.awt.event.*;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics;
import java.lang.Math;

public class PongGame extends JFrame
{
    private static final int frameWidth = 500;
    private static final int frameHeight = 500;
    
    public static void main(String[] args)
    {
      JFrame frame = new JFrame("Pong Game");
      frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
      
      BorderLayout layout = new BorderLayout();
      frame.setLayout(layout);
      
      JPanel settingsPanel = new JPanel();
      settingsPanel.setBackground(Color.YELLOW);
      
      JLabel heading = new JLabel("Heading: ");
      JButton startButton = new JButton("Start");
      JButton stopButton = new JButton("Stop");
      settingsPanel.add(startButton);
      settingsPanel.add(stopButton);
      settingsPanel.add(heading);
      
            
      Ball ball = new Ball(20, 20, 30, 5);
      Paddle leftPaddle = new Paddle(0, 100, 20, 100);
      Paddle rightPaddle = new Paddle(460, 100, 20, 100);
      GamePanel gamePanel = new GamePanel(ball, leftPaddle, rightPaddle, heading);
      
      
      Listener listener = new Listener(gamePanel);
      Timer timer = new Timer(50, listener);
      gamePanel.addMouseMotionListener(listener);
      gamePanel.requestFocusInWindow();
      
      ButtonListener buttonListener = new ButtonListener(startButton, stopButton, timer);
      startButton.addActionListener(buttonListener);
      stopButton.addActionListener(buttonListener);
      
            
      frame.add(settingsPanel, BorderLayout.NORTH);
      frame.add(gamePanel, BorderLayout.CENTER);
      frame.setSize(frameWidth, frameHeight);
      frame.setVisible(true);
  }
}

class GamePanel extends JPanel
{
    // local variables
    private Ball ball;
    public Paddle leftPaddle;
    public Paddle rightPaddle;
    private JLabel head;
    
    // constructor
    public GamePanel(Ball ball, Paddle leftPaddle, Paddle rightPaddle, JLabel head)
    {
        this.ball = ball;
        this.leftPaddle = leftPaddle;
        this.rightPaddle = rightPaddle;
        this.head = head;
    }
    
    // methods
    @Override
    public void paintComponent(Graphics g)  // called automatically whenever a component needs to be painted
    {
        super.paintComponent(g);
        
        g.setColor(Color.RED);
        g.fillRect(leftPaddle.xcor, leftPaddle.ycor, leftPaddle.width, leftPaddle.height);
        
        g.setColor(Color.YELLOW);
        g.fillRect(rightPaddle.xcor, rightPaddle.ycor, rightPaddle.width, rightPaddle.height);
        
        g.setColor(Color.BLUE);
        g.fillOval(ball.xcor, ball.ycor, ball.ballDiameter, ball.ballDiameter);
    }
    
    public void animate()
    {
        ball.move();
        head.setText("heading: " + Math.round(Math.toDegrees(ball.heading)));
        if(ball.xcor < 0 || ball.xcor > getWidth() - ball.ballDiameter )
        {
           
            ball.heading = Math.PI - ball.heading;
        }
        // bounce vertically
        if(ball.ycor < 0 || ball.ycor > getHeight() - ball.ballDiameter)
        {
            ball.heading = -ball.heading;
        }
        
        // right paddle tracks ball
        rightPaddle.ycor = ball.ycor - rightPaddle.height/2;
        
        repaint();
    }
}

class Listener implements ActionListener, MouseMotionListener
{
    // local variables
    private GamePanel gamePanel;
    
    // constructor
    public Listener(GamePanel gamePanel)
    {
        this.gamePanel = gamePanel;
    }
    
    // methods
    @Override
    public void actionPerformed(ActionEvent e)
    {
        gamePanel.animate();
    }
    
     @Override
    public void mouseMoved(MouseEvent e) {
        System.out.println("Mouse moved: " + e.getX() + ", " + e.getY());
        if(e.getY() <= gamePanel.leftPaddle.height/2)
        {
           gamePanel.leftPaddle.ycor = 0; 
        }
        else if(e.getY() >= gamePanel.getHeight() - gamePanel.leftPaddle.height/2)
        {
            gamePanel.leftPaddle.ycor = gamePanel.getHeight() - gamePanel.leftPaddle.height;
        }
        else
        {
            gamePanel.leftPaddle.ycor = e.getY() - gamePanel.leftPaddle.height/2;
        }
    }
    
    @Override
    public void mouseDragged(MouseEvent e) { }
}

class ButtonListener implements ActionListener
{
    private final Timer timer;
    private final JButton startButton;
    private final JButton stopButton;
    
    public ButtonListener(JButton startButton, JButton stopButton, Timer timer)
    {
        this.timer = timer;
        this.startButton = startButton;
        this.stopButton = stopButton;
    }
    
    @Override
    public void actionPerformed(ActionEvent e)
    {
        if(e.getSource() == startButton )
        {
            timer.start();
        }
        
        if(e.getSource() == stopButton )
        {
            timer.stop();
        }
        
    }
    
}
class Ball
{
    // local variables
    public int xcor;
    public int ycor;
    public double heading;
    public int speed;
    public int ballDiameter = 25;
        
    // contructor
    public Ball(int xcor, int ycor, double heading, int speed)
    {
        this.xcor = xcor;
        this.ycor = ycor;
        this.heading = Math.toRadians(heading);
        this.speed = speed;
    }
    
    //methods
    public void move()
    {
        xcor += ( speed * Math.cos(heading) );
        ycor += ( speed * Math.sin(heading) );
    }
    
}

class Paddle
{
   // local variables
    public int ycor;
    public int xcor;
    public int width;
    public int height;
    
    // constructor
    public Paddle(int xcor, int ycor, int width, int height)
    {
        this.xcor = xcor;
        this.ycor = ycor;
        this.width = width;
        this.height = height;
    } 
}
