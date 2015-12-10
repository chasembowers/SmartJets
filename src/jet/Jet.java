package jet;

import java.awt.Color;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

public class Jet extends Movable{
	
	/**
	 * x and y coordinates of Jet upon construction
	 */
	private final double x0, y0;
	
	/**
	 * The angle between the path of a fired Projectile and a horizontal line segment extending
	 * to the right of the center of the Jet
	 */
	private double theta = 0;
	
	private Color team;
	
	private boolean dead = false;
	
	private int projectileRadius;
	
	private final List<Projectile> projectiles = new ArrayList<Projectile>();
	
	/**
	 * Construct Jet with Point2D starting location, Color of Jet's team, radius of Jet,
	 * and radius of Jet's Projectiles.
	 */
	public Jet(Point2D loc, Color team, int jetRadius, int projRadius) {
		this.team = team;
		this.radius = jetRadius;
		this.projectileRadius = projRadius;
		x = loc.getX();
		y = loc.getY();
		x0 = x;
		y0 = y;
	}
	
	/**
	 * Move Jet one unit along path which creates angle theta with horizontal line
	 * extending to the right of center of the Jet.
	 */
	public void move(double theta) {
		
		if (dead || Double.isNaN(theta)) return;
		
		x += Math.cos(theta);
		y += Math.sin(theta);
		
	}
	
	public void pointTo(double toX, double toY) {
		this.theta = angleTo(toX, toY);
	}
	
	public void moveProjectiles() {
		for (Projectile p: projectiles) p.move();
	}
	
	public void fireProjectile() {
		if (dead) return;
		projectiles.add(new Projectile(projectileRadius, getX(), getY(), Math.cos(theta), Math.sin(theta)));
	}
	
	public void hit() {
		dead = true;
	}
	
	public void reset() {
		x = x0;
		y = y0;
		dead = false;
		projectiles.clear();
	}
	
	/**
	 * Return reference to List of Projectiles belonging to Jet.
	 */
	public List<Projectile> getProjectiles() {return projectiles;}
	
	public boolean isDead() {return dead;}
	public double getTheta() {return theta;}
	public Color getTeam() {return team;}
}
