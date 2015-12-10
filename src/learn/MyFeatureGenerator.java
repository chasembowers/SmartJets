package learn;


import game.Angle;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jet.Jet;
import jet.Projectile;

import weka.core.Instance;

public class MyFeatureGenerator implements FeatureGenerator{
	
	/**
	 * Features are generated for PROJ_OBSERVED closest Projectiles
	 */
	private final int PROJ_OBSERVED = 3; 
	
	public double[] generate(List<Jet> jets, int index) {
		
		if (index >= jets.size()) throw new IndexOutOfBoundsException("'index' must specifiy index of Jet in 'jets'.");
		Jet j = jets.get(index);
		
		double[] features = new double[2 * PROJ_OBSERVED + 1];
		Arrays.fill(features, Double.NaN);
		
		Projectile[] sorted = sortedProjectiles(jets, j);
		
		for (int k=0; k<Math.min(sorted.length, PROJ_OBSERVED); ++k) {
			Projectile p = sorted[k];

			//get angle between path from Jet to Projectile and path from Jet to origin
			features[2*k] = Angle.normalize(j.angleTo(p) - j.angleToOrigin());
			
			//get distance between Jet and Projectile
			features[2*k + 1] = j.distanceTo(p);
		}
		
		//get distance between Jet and origin
		features[6] = j.distanceToOrigin();
		
		return features;
	}
	
	/**
	 * Return array containing all Projectiles belonging to enemy Jets in jets,
	 * sorted in ascending order by distance from Jet j
	 */
	private Projectile[] sortedProjectiles(List<Jet> jets, Jet j) {
		
		//Collect all enemy Projectiles
		List<Projectile> projectiles = new ArrayList<Projectile>();
		for (Jet k: jets) {
			if (!k.getTeam().equals(j.getTeam())) projectiles.addAll(k.getProjectiles());
		}
		
		//create map from a distance to a List of Projectiles at that distance from Jet j
		Map<Double,ArrayList<Projectile>> distances = new HashMap<Double,ArrayList<Projectile>>();
		for (Projectile p: projectiles) {
			double xDiff = p.getX() - j.getX();
			double yDiff = p.getY() - j.getY();
			double distance = Math.sqrt(Math.pow(xDiff, 2) + Math.pow(yDiff, 2));
			if (distances.get(distance) == null) distances.put(distance, new ArrayList<Projectile>());
			distances.get(distance).add(p);
		}
		
		//Sort keys from distance map
		List<Double> keys = new ArrayList<Double>(distances.keySet());
		Collections.sort(keys);
		
		//Add each Projectile from distance map to array in ascending order of distance
		int i = 0;
		Projectile[] sorted = new Projectile[projectiles.size()];
		for (Double d: keys) {
			for (Projectile p: distances.get(d)) {
				sorted[i] = p;
				++i;
			}
		}
		return sorted;
	}
	
}
