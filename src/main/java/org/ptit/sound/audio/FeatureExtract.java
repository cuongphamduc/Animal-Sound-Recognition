package org.ptit.sound.audio;

import org.ptit.sound.audio.feature.Delta;
import org.ptit.sound.audio.feature.Energy;
import org.ptit.sound.audio.feature.FeatureVector;
import org.ptit.sound.audio.feature.MFCC;


public class FeatureExtract {

    private float[][] framedSignal;
    private int samplePerFrame;
    private int numOfFrames;
    private int numCepstral = 12;
    private double[][] featureVector;
    private double[][] mfccFeature;
    private double[][] deltaMfcc;
    private double[][] deltaDeltaMfcc;
    private double[] energyVal;
    private double[] deltaEnergy;
    private double[] deltaDeltaEnergy;
    private FeatureVector fv;
    private MFCC mfcc;
    private Delta delta;
    private Energy en;

    public FeatureExtract(float[][] framedSignal, int samplingRate, int samplePerFrame) {
        this.framedSignal = framedSignal;
        this.numOfFrames = framedSignal.length;
        this.samplePerFrame = samplePerFrame;
        mfcc = new MFCC(this.samplePerFrame, samplingRate, numCepstral);
        en = new Energy(this.samplePerFrame);
        fv = new FeatureVector();
        mfccFeature = new double[numOfFrames][numCepstral];
        deltaMfcc = new double[numOfFrames][numCepstral];
        deltaDeltaMfcc = new double[numOfFrames][numCepstral];
        energyVal = new double[numOfFrames];
        deltaEnergy = new double[numOfFrames];
        deltaDeltaEnergy = new double[numOfFrames];
        featureVector = new double[numOfFrames][3 * numCepstral + 3];
        delta = new Delta();
    }

    public FeatureVector getFeatureVector() {
        return fv;
    }

    public void makeMfccFeatureVector() {
        calculateMFCC();

        delta.setRegressionWindow(2);
        deltaMfcc = delta.performDelta2D(mfccFeature);

        delta.setRegressionWindow(1);
        deltaDeltaMfcc = delta.performDelta2D(deltaMfcc);

        energyVal = en.calcEnergy(framedSignal);

        delta.setRegressionWindow(1);

        deltaEnergy = delta.performDelta1D(energyVal);
        delta.setRegressionWindow(1);

        deltaDeltaEnergy = delta.performDelta1D(deltaEnergy);

        for (int i = 0; i < framedSignal.length; i++) {
            for (int j = 0; j < numCepstral; j++) {
                featureVector[i][j] = mfccFeature[i][j];
            }

            for (int j = numCepstral; j < 2 * numCepstral; j++) {
                featureVector[i][j] = deltaMfcc[i][j - numCepstral];
            }

            for (int j = 2 * numCepstral; j < 3 * numCepstral; j++) {
                featureVector[i][j] = deltaDeltaMfcc[i][j - 2 * numCepstral];
            }

            featureVector[i][3 * numCepstral] = energyVal[i];
            featureVector[i][3 * numCepstral + 1] = deltaEnergy[i];
            featureVector[i][3 * numCepstral + 2] = deltaDeltaEnergy[i];
        }

        fv.setMfccFeature(mfccFeature);
        fv.setFeatureVector(featureVector);
        System.gc();
    }

    private void calculateMFCC() {
        for (int i = 0; i < numOfFrames; i++) {
            mfccFeature[i] = mfcc.doMFCC(framedSignal[i]);
        }
    }

}
