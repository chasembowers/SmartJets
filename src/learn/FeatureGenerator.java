package learn;


import java.util.List;

import jet.Jet;

import weka.core.Instance;

/**
 *A FeatureGenerator must implement only one public function which returns an array of features
 *for a given game state in the form of a List of Jets jets and the index of the Jet in jets 
 *from whose perspective the features must be generated. 
 */
public interface FeatureGenerator {

	public double[] generate(List<Jet> jets, int index);
	
}
