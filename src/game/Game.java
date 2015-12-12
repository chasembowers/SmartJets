package game;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JPanel;
import javax.swing.Timer;

import weka.classifiers.trees.RandomForest;

import jet.Jet;
import jet.Projectile;

import learn.JetTrainer;
import learn.MyFeatureGenerator;

/**
 *Core class for the Smart Jets game. Responsible for retrieving and processing user input
 *and displaying graphics.
 */
public class Game extends JPanel implements ActionListener {
	
	/**
	 * Given samples of game states and user actions and produces actions for non-user Jets 
	 * via machine learning.
	 */
    private JetTrainer jt = new JetTrainer(new MyFeatureGenerator(), new RandomForest(), 10000); 

	/**
	 * Radius of circular arena that Jets fight inside
	 */
    private final int ARENA_RADIUS = 250;
    
    /**
     * Interval in milliseconds with which the timer calls actionPerformed 
     */
    private final int DELAY = 5;
    private Timer timer;
    
    /**
     * Number of times the game has been updated by a Timer
     */
    private int ticks = 0;
    
    /**
     * Jets fire every FIRE_INTERVAL ticks
     */
    private final int FIRE_INTERVAL = 100;
    
    /**
     * Number of Jets that are currently alive
     */
    private int alive;
    
    /**
     * Represents whether each arrow/wasd key is pressed by user
     */
    private volatile boolean left = false;
    private volatile boolean right = false;
    private volatile boolean up = false;
    private volatile boolean down = false;
    
    /**
     * Coordinates of user mouse position
     */
    private volatile int mouseX = 0;
    private volatile int mouseY = 0;
    
    private Jet userJet;
    private List<Jet> jets = new ArrayList<Jet>();
    
    private final int JET_RADIUS = 10;
    private final int PROJECTILE_RADIUS = 2;
    

    public Game() {

        addKeyListener(new MyKeyAdapter());
        addMouseMotionListener(new MyMouseAdapter());
        setFocusable(true);
        setBackground(Color.BLACK);
        
        setPreferredSize(new Dimension(2*ARENA_RADIUS, 2*ARENA_RADIUS));

        //Add user Jet
    	userJet = new Jet(new Point2D.Double(0,0), Color.BLUE, JET_RADIUS, PROJECTILE_RADIUS);
    	jets.add(userJet);
    	
    	//Add two non-user Jets
    	Jet j;
    	j = new Jet(new Point2D.Double(ARENA_RADIUS - JET_RADIUS, 0), Color.RED, JET_RADIUS, PROJECTILE_RADIUS);
    	jets.add(j);
    	j = new Jet(new Point2D.Double(-ARENA_RADIUS + JET_RADIUS,0), Color.RED, JET_RADIUS, PROJECTILE_RADIUS);
    	jets.add(j);
        
        alive = jets.size();
        
    	timer = new Timer(DELAY, this);
        timer.start();
    }
    
    /**
     * Restarts the game. JetTrainer trains non-user Jets if train is True.
     * Samples of recent round are discarded if train is False.
     */
    private void restart(boolean train) {
    	
    	timer.stop();
    	
    	//train JetTrainer or remove Samples from last round
    	if (train) jt.train();
    	else jt.removeNewSamples(ticks);
    	
    	for (Jet j: jets) {
    		j.reset();
    	}
    	ticks = 0;
    	alive = jets.size();
	    	
    	timer.start();
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        
        g.setColor(Color.WHITE);
    	g.fillOval(0, 0, 2*ARENA_RADIUS, 2*ARENA_RADIUS);
        
    	for (Jet j: jets) {
        	paintJet(g, j);
        }
    }
    
    public void paintJet(Graphics g, Jet j) {
    	
    	g.setColor(j.getTeam());
    	
    	//Paint Jet j
    	double jetX = j.getX() - JET_RADIUS + ARENA_RADIUS;
        double jetY = -j.getY() - JET_RADIUS + ARENA_RADIUS;
    	if (!j.isDead()) g.fillOval((int) jetX, (int) jetY, 2*JET_RADIUS, 2*JET_RADIUS);
        
    	//Paint each of Jet j's Projectiles
    	for (Projectile p: j.getProjectiles()) {
        	double pX = p.getX() - PROJECTILE_RADIUS + ARENA_RADIUS;
            double pY = -p.getY() - PROJECTILE_RADIUS + ARENA_RADIUS;
        	g.fillOval((int) pX, (int) pY, 2*PROJECTILE_RADIUS, 2*PROJECTILE_RADIUS);
        }
    }

    /**
     * Called by Timer every DELAY milliseconds. Saves game state and user action to JetTrainer, moves Jets
     * and Projectiles, detects collisions. 
     */
    @Override
    public void actionPerformed(ActionEvent e) {
    	
    	//Calculate user movement angle relative to origin
    	Angle a = new Angle(up, down, left, right);
    	Angle toOrigin = new Angle(userJet.angleToOrigin());
    	Angle relA = a.substract(toOrigin);
    	
    	//Round user movement angle to nearest PI/4 and add to JetTrainer
    	jt.addSample(jets, jets.indexOf(userJet), relA.round(new Angle(Math.PI / 4)));
    	
    	boolean fire = ticks % FIRE_INTERVAL == 0;
    	ticks += 1;
        for (Jet j: jets) {
        	
        	j.moveProjectiles();
        	
        	if (fire) {
        		//Fire projectiles
        		if (j == userJet) j.pointTo(mouseX - ARENA_RADIUS, -(mouseY - ARENA_RADIUS));
	        	else j.pointTo(userJet.getX(), userJet.getY());
	        	j.fireProjectile();
        	}
        	
        	//Move Jets
        	if (j == userJet) j.move(a.getTheta());
        	else if (jt.isTrained()) {
        		
        		//Get angle from JetTrainer and convert from relative angle to absolute angle
        		Angle relB = jt.getAngle(jets, jets.indexOf(j));
        		toOrigin = new Angle(j.angleToOrigin());
        		Angle b = relB.add(toOrigin);
        		
        		j.move(b.getTheta());
        	}
        	returnToBoard(j);
        	removeLostProjectiles(j);
        }
    	
    	collisions();
        
        repaint();
        
        if (userJet.isDead()) restart(false);
    	if (alive <= 1) restart(true);
    }
    
    /**
     * Return Jets that have left board by projecting towards origin
     */
    private void returnToBoard(Jet j) {
    	double locTheta = j.angleFromOrigin();
		double locRadius = j.distanceToOrigin();
		double maxRadius = ARENA_RADIUS - JET_RADIUS;
		if (locRadius > maxRadius) 
			j.setLocation(maxRadius * Math.cos(locTheta), maxRadius * Math.sin(locTheta));
    }
    
    /**
     * Remove projectiles belonging to Jet j if they have left the board
     */
    private void removeLostProjectiles(Jet j) {
    	List<Projectile> projectiles = j.getProjectiles();
    	for (Projectile p: new ArrayList<Projectile>(projectiles)){
        	double px = p.getX();
        	double py = p.getY();
        	if (Math.sqrt(Math.pow(px, 2) + Math.pow(py, 2)) > ARENA_RADIUS + PROJECTILE_RADIUS) projectiles.remove(p);
        }
    }
    
    /**
     * Detect collisions between Jets and Projectiles and other Jets
     */
    private void collisions() {
    	List<Jet>jetsCopy = new ArrayList<Jet>(jets);
    	for (Jet j: jetsCopy) {
    		List<Projectile> projectiles = j.getProjectiles();
    		List<Projectile> projCopy = new ArrayList<Projectile>(projectiles);
    		for (Jet k: jetsCopy) {
    			
    			//separate Jets if they are intersecting
    			if (k.distanceTo(j) < 2 * JET_RADIUS) separateJets(k,j); 
    			
    			//Detect collisions between Jets and enemy Projectiles
    			for (Projectile p: projCopy) {
    				if (!k.isDead() && !k.getTeam().equals(j.getTeam())) {
	    				if (k.distanceTo(p.getX(), p.getY()) < JET_RADIUS + PROJECTILE_RADIUS) {
	    					projectiles.remove(p);	//remove collided Projectiles
	    					k.hit();
	    					if (k.isDead()) alive -= 1;
	    				}
    				}
    			}
    		}
    	}
    }
    
    /**
     * Separate two Jets by moving them along the line between their origins
     */
    private void separateJets(Jet a, Jet b) {
    	
    	double distBetween  = a.distanceTo(b) - 2 * JET_RADIUS;
    	if (distBetween >= 0) return;
    	
    	double aToB = a.angleTo(b);
    	double halfMove = Math.abs(distBetween) / 2.;
    	double horMove = halfMove * Math.cos(aToB);
    	double verMove = halfMove * Math.sin(aToB);
    	a.setLocation(a.getX() - horMove, a.getY() - verMove);
    	b.setLocation(b.getX() + horMove, b.getY() + verMove);
    }

    private class MyKeyAdapter extends KeyAdapter {

        @Override
        public void keyPressed(KeyEvent e) {
        	
        	int key = e.getKeyCode();
            
            switch (key) {
            
            case KeyEvent.VK_A:
            case KeyEvent.VK_LEFT:
            	left = true;
            	break;
            
            case KeyEvent.VK_D:
            case KeyEvent.VK_RIGHT:
            	right = true;
            	break;
            	
            case KeyEvent.VK_W:
            case KeyEvent.VK_UP:
            	up = true;
            	break;
            	
            case KeyEvent.VK_S:
            case KeyEvent.VK_DOWN:
            	down = true;
            	break;
            }
        }
        
        @Override
        public void keyReleased(KeyEvent e) {
        	
        	int key = e.getKeyCode();
            
            switch (key) {
            
            case KeyEvent.VK_A:
            case KeyEvent.VK_LEFT:
            	left = false;
            	break;
            
            case KeyEvent.VK_D:
            case KeyEvent.VK_RIGHT:
            	right = false;
            	break;
            	
            case KeyEvent.VK_W:
            case KeyEvent.VK_UP:
            	up = false;
            	break;
            	
            case KeyEvent.VK_S:
            case KeyEvent.VK_DOWN:
            	down = false;
            	break;
            }
        }
    }
    
    private class MyMouseAdapter extends MouseAdapter {

        @Override
        public void mouseMoved(MouseEvent e) {
        	mouseX = e.getX();
        	mouseY = e.getY();
        }
    }
   
}
