package jet;

/**
 *Represents a circular moving object which has a fixed velocity.
 */
public class Projectile extends Movable{
	
	private final double radius, dx, dy;
	
	/**
	 *Construct Projectile with radius length, horizontal/vertical position x/y and 
	 *horizontal/vertical velocity dx/dy. 
	 */
	public Projectile(int radius, double x, double y, double dx, double dy){
		this.radius = radius;
		this.x = x;
		this.y = y;
		this.dx = dx;
		this.dy = dy;
	}
	
	public Projectile(Projectile other) {
		this.x = other.x;
		this.y = other.y;
		this.dx = other.dx;
		this.dy = other.dy;
		this.radius = other.radius;
	}
	
	/**
	 * Move Projectile one unit of time.
	 */
	public void move() {
		x += dx;
		y += dy;
	}
	
	public double getXVel() {return dx;}
	public double getYVel() {return dy;}
}

