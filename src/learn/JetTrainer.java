package learn;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;

import jet.Jet;

import game.Angle;
import weka.classifiers.Classifier;
import weka.core.Attribute;
import weka.core.FastVector;
import weka.core.Instance;
import weka.core.Instances;

/**
 *Collects samples of game states represented by a List of Jets and the index of the perceiving Jet and 
 *the corresponding action taken. Trains a machine learning classifier in order to produce an action for
 *new game states.
 */
//TODO: Move samples, labels, and conversion to Instances to separate Samples class
public class JetTrainer {
	
	/**
	 * Responsible for generating a set of features from some game state
	 */
	private FeatureGenerator fg;
	
	/**
	 * Samples consisting of an array of double features and a String label
	 * are stored here. An ArrayDeque is used to allow fast removal of old Samples
	 */
	private ArrayDeque<Sample> samples = new ArrayDeque<Sample>();
	
	/**
	 * Holds the List of unique String labels in samples 
	 */
	private List<String> labels;
	
	/**
	 * The set of samples is converted to an Instances for use with Weka
	 */
	private Instances instances;
	
	/**
	 * The maximum number of Samples to be trained with. Oldest samples are purged when
	 * numSamples is exceeded.
	 */
	private int numSamples;
	
	private Classifier classifier;
	
	private boolean trained = false;
	
	/**
	 * Construct JetTrainer from a FeatureGenerator, Weka Classifier, and a maximum
	 * capacity for the number of Samples to train with.
	 */
	public JetTrainer(FeatureGenerator fg, Classifier classifier, int numSamples) {
		this.fg = fg;
		this.classifier = classifier;
		this.numSamples = numSamples;
	}

	/**
	 * Add a Sample of the game state, represented by a List of jets and the index in
	 * that List of the perceiving Jet, and of the corresponding action, represented by
	 * the angle between the path the Jet has taken and the Jet's path to the origin.
	 */
	public void addSample(List<Jet> jets, int index, Angle a) {
		double[] features = fg.generate(jets, index);
		Sample s = new Sample(features, a.toString());
		samples.add(s);
	}
	
	/**
	 * Remove n of the Samples that have been most recently added.
	 */
	public void removeNewSamples(int n) {
		for (int i=0; i<n; ++i) {
			if (!samples.isEmpty()) samples.removeLast();
		}
	}
	
	/**
	 * Produce an action in the form of the angle between the path of the Jet to the origin
	 * and the path that the Jet should take from a given game state, in the form of a List of
	 * Jets and the index of the perceiving Jet in that List.
	 */
	public Angle getAngle(List<Jet> jets, int index) {
		
		if (!trained) throw new IllegalStateException("JetTrainer must be trained before calling getMovement().");
		
		double[] features = fg.generate(jets, index);
		
		//convert features array to Instance for use with Weka
		Instance i = new Instance(features.length + 1);
		i.setDataset(instances);
		for (int k=0; k<features.length; ++k) i.setValue(k, features[k]);
		
		try {
		
			//classify Instance and return corresponding Angle
			int l = (int) classifier.classifyInstance(i);
			String aString = labels.get(l);
			return new Angle(aString);
		} 
		
		catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
			return null;
		}
	}
	
	/**
	 * Train the machine learning classifier on the set of stored Samples.
	 * This can be time intensive.
	 */
	public void train() {
		
		//Remove oldest samples such that numSamples Samples remain
		while (samples.size() > numSamples) samples.removeFirst();
		
		//Assume that all Samples have the same number of features
		int numFeatures = samples.getFirst().getFeatures().length;
		int n = numFeatures + 1;
		FastVector attributes = new FastVector(n);
		
		//Add a numeric attribute for every feature
		for (int k=0; k<numFeatures; ++k) {
			attributes.addElement(new Attribute("Numeric " + Integer.toString(k)));
		}
		
		//Add the final String attribute which can take the value of any Label of all Samples
		labels = new ArrayList<String>();
		for (Sample s: samples) {
			if (!labels.contains(s.getLabel())) labels.add(s.getLabel());
		}
		int nClasses = labels.size();
		FastVector classValues = new FastVector(nClasses);
		for (String v: labels) classValues.addElement(v);
		Attribute classes = new Attribute("class", classValues);
		attributes.addElement(classes);
		
		//create Instaces with last attribute as class index
		instances = new Instances("", attributes, 0);
		instances.setClassIndex(numFeatures);
		
		for (Sample s: samples) {
			//Convert each Sample to a Weka Instance and add to instances
			Instance i = new Instance(n);
			double[] features = s.getFeatures();
			for (int k=0; k<numFeatures; ++k) i.setValue(k, features[k]);
			i.setValue((Attribute)attributes.elementAt(numFeatures), s.getLabel());
			instances.add(i);
		}
		
		try {
			
			//Train the classifier
			System.out.println("Training..." + Integer.toString(samples.size()));
			classifier.buildClassifier(instances);
			System.out.println("Done.");
		} 
		
		catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}
		
		trained = true;
	}
	
	public boolean isTrained() {return trained;}
	
}
