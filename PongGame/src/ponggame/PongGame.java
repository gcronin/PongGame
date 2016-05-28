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
import net.java.games.input.Component;
import net.java.games.input.Controller;
import net.java.games.input.ControllerEnvironment;


public class PongGame extends JFrame
{
    private static final int frameWidth = 500;
    private static final int frameHeight = 500;
    
    // global variable instances created
    JLabel player1ScoreLabel;
    JLabel player2ScoreLabel;
    JLabel endOfGameField;
    JButton startButton; 
    JButton stopButton;
    Ball ball;
    Paddle leftPaddle;
    Paddle rightPaddle;
    JPanel settingsPanel;
    JPanel startupPanel;
    JFrame frame;
    JoystickController readJoystick ;
    Timer timer;
    GamePanel gamePanel;
    PongGame game;
    
    public static void main(String[] args)
    {
       PongGame game = new PongGame();
       game.setupPlayers();
       game.initializeGame();
    }

    public void initializeGame()
    {
      // variable definions
      ball = new Ball(200, 100, 30, 10);
      leftPaddle = new Paddle(0, 100, 20, 100, ball);
      rightPaddle =  new Paddle(460, 100, 20, 100, ball);
      readJoystick = new JoystickController();
      player1ScoreLabel = new JLabel("Player 1: 0");
      player2ScoreLabel = new JLabel("Player 2: 0");
      endOfGameField = new JLabel("");
      startButton = new JButton("Start");
      stopButton =  new JButton("Stop");
      
      gamePanel = new GamePanel(ball, leftPaddle, rightPaddle, player1ScoreLabel, player2ScoreLabel, readJoystick, game);
      
      readJoystick.SetController();
             
      // Outer frame for controla
      settingsPanel = new JPanel();
      settingsPanel.setBackground(Color.YELLOW);
      settingsPanel.add(player1ScoreLabel);
      settingsPanel.add(startButton);
      settingsPanel.add(stopButton);
      settingsPanel.add(player2ScoreLabel);
      
      // Inner frame is where the actual game is played
      frame = new JFrame("Pong Game"); 
      frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
      BorderLayout layout = new BorderLayout();
      frame.setLayout(layout);
      frame.add(settingsPanel, BorderLayout.NORTH);
      frame.add(gamePanel, BorderLayout.CENTER);
      frame.setSize(frameWidth, frameHeight);
      frame.setVisible(true);

      //Listeners
      Listener listener = new Listener(gamePanel);
      timer = new Timer(50, listener);
      gamePanel.addMouseMotionListener(listener);
      gamePanel.add(endOfGameField);
      gamePanel.requestFocusInWindow();
      
      ButtonListener buttonListener = new ButtonListener(startButton, stopButton, timer);
      startButton.addActionListener(buttonListener);
      stopButton.addActionListener(buttonListener);
                  
    }
    
    public void setupPlayers()
    {
        
    
    }
    
    public void endGame()
    {
        //timer.stop();
        //endOfGameField.setText("GAME OVER!!!");
        //startButton.setText("Play Again");
    }
}

class GamePanel extends JPanel
{
    // local variables
    private final Ball ball;
    public Paddle leftPaddle;
    public Paddle rightPaddle;
    private final JLabel scoreLabel1;
    private final JLabel scoreLabel2;
    private final JoystickController readJoystick;
    private int score1 = 0;
    private int score2 = 0;
    private final PongGame game;
    
    // constructor
    public GamePanel(Ball ball, Paddle leftPaddle, Paddle rightPaddle, JLabel scoreLabel1, JLabel scoreLabel2, JoystickController readJoystick, PongGame game)
    {
        this.ball = ball;
        this.leftPaddle = leftPaddle;
        this.rightPaddle = rightPaddle;
        this.scoreLabel1 = scoreLabel1;
        this.scoreLabel2 = scoreLabel2;
        this.readJoystick = readJoystick;
        this.game = game;
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
        if( ball.xcor < 0 || ball.xcor > getWidth() ) { game.endGame(); }
        readJoystick.GetYAxesValues();
        scoreLabel1.setText("Player 1: " + score1);
        scoreLabel2.setText("Player 2: " + score2);
        
        if( ball.xcor - 5  < leftPaddle.width  )  // -5 is a hack which prevents ball from getting "stuck" on the paddle
        {
            if(leftPaddle.overlaps(ball.ycor)) {
                ball.heading = Math.PI - ball.heading;         
                score1 += 1;
                                
            }
        }
        if(( ball.xcor + ball.ballDiameter + 5 )  > getWidth() - leftPaddle.width)
        {
            if(rightPaddle.overlaps(ball.ycor)) {
                ball.heading = Math.PI - ball.heading;
                score2 += 1;
            }
        }
        // bounce vertically
        if(ball.ycor < 0 || ball.ycor > getHeight() - ball.ballDiameter)
        {
            ball.heading = -ball.heading;
        }
        
        //leftPaddle moves at speed set by left joystick value
        leftPaddle.ycor += readJoystick.Y1Value*20; 
        if(leftPaddle.ycor < 0) { leftPaddle.ycor = 0; }
        if(leftPaddle.ycor > getHeight()- leftPaddle.height )  { leftPaddle.ycor = getHeight() - leftPaddle.height; }
        
        //rightPaddle moves at speed set by right joystick value
        rightPaddle.ycor += readJoystick.Y2Value*20; 
        if(rightPaddle.ycor < 0) { rightPaddle.ycor = 0; }
        if(rightPaddle.ycor > getHeight()- rightPaddle.height )  { rightPaddle.ycor = getHeight() - rightPaddle.height; }
        
        //right paddle tracks ball
        //rightPaddle.ycor = ball.ycor - rightPaddle.height/2;
        
        repaint();
    }
}

class Listener implements ActionListener, MouseMotionListener
{
    // local variables
    private final GamePanel gamePanel;
    
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
        /*System.out.println("Mouse moved: " + e.getX() + ", " + e.getY());
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
        }*/
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
    public int ballDiameter;
    public int ballRadius;
    
    // constructor
    public Paddle(int xcor, int ycor, int width, int height, Ball ball)
    {
        this.xcor = xcor;
        this.ycor = ycor;
        this.width = width;
        this.height = height;
        this.ballDiameter = ball.ballDiameter;
        this.ballRadius = ballDiameter/2;
    } 
    
    public boolean overlaps(int ballycor)
    {
        if( ballycor + ballRadius >= (this.ycor + height ) || ballycor + ballRadius <= (this.ycor ))  { return false; }
        else return true;
    }
}

class JoystickController
{
    public float Y1Value;
    public float Y2Value;
       
    Controller[] ca = ControllerEnvironment.getDefaultEnvironment().getControllers();
    private final  String controllerType = "Stick";
    private  Controller logitech = null;
    
    public void SetController()
    {
            for(int i=0; i < ca.length && logitech == null; i++) {
                System.out.println(ca[i].getType().toString());
                if(ca[i].getType().toString().equals(controllerType)) {
                // Found a controller
                    logitech = ca[i];
                    break;
                }
            }
        
            if(logitech == null) {
                // Couldn't find a controller
                System.out.println("Found no desired controller!");
                System.exit(0);
            }
        }
    
    public void GetYAxesValues()
    {
        logitech.poll();
        Component[] components = logitech.getComponents();
                
                for(int i=0;i<components.length;i++) {
                    
                    if(components[i].getName().equals("Z Rotation"))
                    {
                        Y2Value = components[i].getPollData();
                        //deadband
                        if(Math.abs(Y2Value) < 0.1) { Y2Value = 0; }
                    }
                    if(components[i].getName().equals("Y Axis"))
                    {
                        Y1Value = components[i].getPollData();
                        //deadband
                        if(Math.abs(Y1Value) < 0.1) { Y1Value = 0; }
                    }
                } 
    }            
               
    
}