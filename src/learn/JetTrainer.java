package learn;

import java.util.List;

import jet.Jet;

import game.Angle;
import weka.classifiers.Classifier;
import weka.core.Instance;
import weka.core.Instances;

/**
 *Collects samples of game states represented by a List of Jets and the index of the perceiving Jet and 
 *the corresponding action taken. Trains a machine learning classifier in order to produce an action for
 *new game states.
 */
public class JetTrainer {
	
	/**
	 * Responsible for generating a set of features from some game state
	 */
	private FeatureGenerator fg;
	
	/**
	 * Holds Samples added to JetTrainer before they are added to trainSamples or discarded
	 */
	private Samples sampleBuffer;
	
	/**
	 * Holds Samples that will be used for training
	 */
	private Samples trainSamples;
	
	/**
	 * The set of Samples is converted to an Instances for use with Weka
	 */
	private Instances instances;
	
	private Classifier classifier;
	
	private boolean trained = false;
	
	/**
	 * Construct JetTrainer from a FeatureGenerator, Weka Classifier, and a maximum
	 * capacity for the number of Samples to train with.
	 */
	public JetTrainer(FeatureGenerator fg, Classifier classifier, int numSamples) {
		this.fg = fg;
		this.classifier = classifier;
		sampleBuffer = new Samples(numSamples);
		trainSamples = new Samples(numSamples);
	}

	/**
	 * Add to sampleBuffer a Sample of the game state, represented by a List of jets and the index in
	 * that List of the perceiving Jet, and of the corresponding action, represented by
	 * the angle between the path the Jet has taken and the Jet's path to the origin.
	 */
	public void addSample(List<Jet> jets, int index, Angle a) {
		double[] features = fg.generate(jets, index);
		Sample s = new Sample(features, a.toString());
		sampleBuffer.addSample(s);
	}
	
	/**
	 * Remove all Samples added after last training
	 */
	public void flushSampleBuffer() {sampleBuffer.clear();}
	
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
			String aString = instances.classAttribute().value(l);
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
		
		//Move Samples from sampleBuffer to trainSamples
		trainSamples.addSamples(sampleBuffer);
		sampleBuffer.clear();
		instances = trainSamples.toInstances();
		
		try {
			
			//Train the classifier
			System.out.println("Training with " + Integer.toString(trainSamples.size()) + " samples...");
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
