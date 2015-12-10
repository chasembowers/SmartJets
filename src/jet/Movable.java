package jet;

/**
 *Represents a circular object that has modifiable horizontal and vertical coordinates. 
 */
public class Movable {

	protected double x, y;
	
	protected int radius;
	
	public double getX() {return x;}
	public double getY() {return y;}
	
	public void setLocation(double x, double y) {
		this.x = x;
		this.y = y;
	}
	
	public double angleTo(double toX, double toY) {
		return Math.atan2(toY - y, toX - x);
	}
	
	public double angleTo(Movable m) {
		return angleTo(m.getX(), m.getY());
	}
	
	public double angleToOrigin() {return angleTo(0,0);}
	
	public double angleFrom(double fromX, double fromY) {
		return Math.atan2(y - fromY, x - fromX);
	}
	
	public double angleFromOrigin() {return angleFrom(0,0);}
	
	public double distanceTo(double toX, double toY) {
		double xDiff = toX - x;
    	double yDiff = toY - y;
    	return Math.sqrt(Math.pow(xDiff,2) + Math.pow(yDiff,2));
	}
	
	public double distanceTo(Movable m) {
		return distanceTo(m.getX(), m.getY());
	}
	
	public double distanceToOrigin() {return distanceTo(0,0);}
	
}
