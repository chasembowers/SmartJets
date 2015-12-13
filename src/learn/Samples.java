package learn;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Queue;

import weka.core.Attribute;
import weka.core.FastVector;
import weka.core.Instances;

/**
 * Stores Samples and converts them to an Instances object. When number of Samples exceeds
 * capacity, Samples that were added first are discarded first.
 */
public class Samples implements Iterable<Sample>{

	/**
	 * Samples are stored in a queue so that Samples can be easily removed from the head
	 */
	private Queue<Sample> samples = new ArrayDeque<Sample>();
	
	/**
	 * Number of features in each Sample. Must be consistent.
	 */
	private Integer numFeatures;
	
	private int capacity;
	
	public Samples(int capacity) {
		if (capacity <= 0) throw new IllegalArgumentException("Capacity must be greater than zero.");
		this.capacity = capacity;
	}
	
	public void addSample(Sample s) {
		
		//Check that Sample's number of features is consistent with that of stored Samples
		Integer sNumFeatures = s.getFeatures().length;
		if (numFeatures == null) numFeatures = sNumFeatures;
		if (!sNumFeatures.equals(numFeatures))
			throw new IllegalArgumentException("Number of features must be consistent for added Samples.");
		
		samples.add(s);
		if (samples.size() > capacity) samples.remove();
	}
	
	public void addSamples(Iterable<Sample> other) {
		for (Sample s : other) {
			addSample(s);
		}
	}
	
	public void clear() {
		samples.clear();
		numFeatures = null;
	}
	
	/**
	 * Convert Samples into a Weka Instances object
	 */
	public Instances toInstances() {
		
		if (samples.isEmpty()) throw new IllegalStateException("This Samples object contains 0 Samples.");
		
		int n = numFeatures + 1;
		FastVector attributes = new FastVector(n);
		
		//Add a numeric attribute for every feature
		for (int k=0; k<numFeatures; ++k) {
			attributes.addElement(new Attribute("Numeric " + Integer.toString(k)));
		}
		
		//Add the final String attribute which can take the value of any Label of all Samples
		List<String> labels = new ArrayList<String>();
		for (Sample s: samples) {
			if (!labels.contains(s.getLabel())) labels.add(s.getLabel());
		}
		FastVector classValues = new FastVector(labels.size());
		for (String v: labels) classValues.addElement(v);
		Attribute classes = new Attribute("class", classValues);
		attributes.addElement(classes);
		
		//create Instaces with last attribute as class index
		Instances instances = new Instances("", attributes, 0);
		instances.setClassIndex(numFeatures);
		
		//Convert each Sample to a Weka Instance and add to instances
		for (Sample s: samples) instances.add(s.toInstance(classes));
		
		return instances;
	}
	
	public int size() {return samples.size();}
	
	public boolean isEmpty() {return samples.isEmpty();}

	@Override
	public Iterator<Sample> iterator() {
		return samples.iterator();
	}

}