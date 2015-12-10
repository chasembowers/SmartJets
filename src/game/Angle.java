package game;

/**
 *Represents an Angle in radians from [0, 2*PI). Supports operations between Angles that produce new 
 *Angles in this interval and conversion to/from String.
 */
public class Angle {
	
	private double theta;
	private static final double TWO_PI = 2 * Math.PI;

	/**
	 * Create an angle from arrow keys. Calculate the angle between origin and vector sum of 
	 * directions which are True.
	 */
	public Angle(boolean up, boolean down, boolean left, boolean right) {
		
		int x = (right ? 1:0) - (left ? 1:0);
		int y = (up ? 1:0) - (down ? 1:0);
		
		if (x == 0 && y == 0) {
			theta = Double.NaN;
			return;
		}
		
		theta = normalize(Math.atan2(y, x));
		
	}
	
	/**
	 * Return an equivalent angle in the interval [0, 2*PI).
	 */
	public static double normalize(double angle) {
		angle = angle % TWO_PI;
		if (angle < 0) angle += TWO_PI;
		if (angle >= TWO_PI) angle -= TWO_PI;
		return angle;
	}
	
	public Angle(double theta) {
		this.theta = normalize(theta);
	}
	
	/**
	 * Construct Angle with String representation of number of radians 
	 */
	public Angle(String s) {
		this.theta = normalize(Double.valueOf(s));
	}
	
	/**
	 * Return new angle rounded to nearest multiple of Angle a
	 */
	public Angle round(Angle a) {
		if (Double.isNaN(this.theta)) return new Angle(Double.NaN);
		return new Angle(a.getTheta() * Math.round(theta / a.getTheta()));
	}

	public Angle substract(Angle a) {
		return new Angle(theta - a.getTheta());
	}
	
	public Angle add(Angle a) {
		return new Angle(theta + a.getTheta());
	}
	
	public double getTheta() {return theta;}
	
	public String toString() {return Double.toString(theta);}
	
}
