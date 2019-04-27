package org.ptit.sound.audio.feature;


public class Energy {

    private int samplePerFrame;

    public Energy(int samplePerFrame) {
        this.samplePerFrame = samplePerFrame;
    }

    public double[] calcEnergy(float[][] framedSignal) {
        double[] energyValue = new double[framedSignal.length];

        for (int i = 0; i < framedSignal.length; i++) {
            float sum = 0;

            for (int j = 0; j < samplePerFrame; j++) {
                sum += Math.pow(framedSignal[i][j], 2);
            }

            energyValue[i] = Math.log(sum);
        }

        return energyValue;
    }
}
