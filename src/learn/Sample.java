package learn;

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
	
}
