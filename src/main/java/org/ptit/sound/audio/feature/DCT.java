package org.ptit.sound.audio.feature;


public class DCT {

    int numCepstral;
    int numMelFilter;


    public DCT(int numCepstral, int numMelFilter) {
        this.numCepstral = numCepstral;
        this.numMelFilter = numMelFilter;
    }

    public double[] performDCT(double[] y) {
        double[] res = new double[numCepstral];

        for (int k = 1; k <= numCepstral; k++) {
            for (int i = 1; i <= numMelFilter; i++) {
                res[k - 1] += y[i - 1] * Math.cos(Math.PI * (k - 1) / numMelFilter * (i - 0.5));
            }
        }

        return res;
    }
}
