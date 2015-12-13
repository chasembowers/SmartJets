package learn;

import weka.core.Attribute;
import weka.core.Instance;

/**
 *A Sample represents an array of features represented as doubles and a corresponding label
 *represented as a String for the purpose of machine learning classification.
 */
public class Sample {

	private final double[] features;
	private final String label;
	
	public Sample(double[] features, String label) {
		this.features = features;
		this.label = label;
	}
	
	public double[] getFeatures() {return features.clone();}
	public String getLabel() {return label;}
	
	/**
	 * Convert Sample to Weka Instance given a Weka Attribute for the class/label 
	 */
	public Instance toInstance(Attribute a) {
		Instance i = new Instance(features.length + 1);
		for (int k=0; k<features.length; ++k) i.setValue(k, features[k]);
		i.setValue(a, label);;
		return i;
	}
	
}
