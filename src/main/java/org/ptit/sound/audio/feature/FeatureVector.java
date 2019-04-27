package org.ptit.sound.audio.feature;

import java.io.Serializable;


public class FeatureVector implements Serializable {

    private double[][] mfccFeature;
    private double[][] featureVector;

    public FeatureVector() {
    }

    public double[][] getMfccFeature() {
        return mfccFeature;
    }

    public void setMfccFeature(double[][] mfccFeature) {
        this.mfccFeature = mfccFeature;
    }

    public int getNoOfFrames() {
        return featureVector.length;
    }

    public int getNoOfFeatures() {
        return featureVector[0].length;
    }

    public double[][] getFeatureVector() {
        return featureVector;
    }

    public void setFeatureVector(double[][] featureVector) {
        this.featureVector = featureVector;
    }
}
