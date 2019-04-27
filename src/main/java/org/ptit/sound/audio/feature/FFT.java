package org.ptit.sound.audio.feature;


public class FFT {

    public float[] real;
    public float[] imag;
    protected int numPoints;

    public void computeFFT(float[] signal) {
        numPoints = signal.length;
        real = new float[numPoints];
        imag = new float[numPoints];
        real = signal;

        for (int i = 0; i < imag.length; i++) {
            imag[i] = 0;
        }

        if (numPoints == 1) {
            return;
        }

        final double pi = Math.PI;
        final int numStages = (int) (Math.log(numPoints) / Math.log(2));
        int halfNumPoints = numPoints >> 1;
        int j = halfNumPoints;
        int k = 0;

        for (int i = 1; i < numPoints - 2; i++) {
            if (i < j) {
                float tempReal = real[j];
                float tempImag = imag[j];
                real[j] = real[i];
                imag[j] = imag[i];
                real[i] = tempReal;
                imag[i] = tempImag;
            }

            k = halfNumPoints;

            while (k <= j) {
                j -= k;
                k >>= 1;
            }

            j += k;
        }

        for (int stage = 1; stage <= numStages; stage++) {
            int LE = 1;

            for (int i = 0; i < stage; i++) {
                LE <<= 1;
            }

            int LE2 = LE >> 1;
            double UR = 1;
            double UI = 0;
            double SR = Math.cos(pi / LE2);
            double SI = -Math.sin(pi / LE2);

            for (int subDFT = 1; subDFT <= LE2; subDFT++) {
                for (int butterfly = subDFT - 1; butterfly <= numPoints - 1; butterfly += LE) {
                    int ip = butterfly + LE2;
                    float tempReal = (float) (real[ip] * UR - imag[ip] * UI);
                    float tempImag = (float) (real[ip] * UI + imag[ip] * UR);
                    real[ip] = real[butterfly] - tempReal;
                    imag[ip] = imag[butterfly] - tempImag;
                    real[butterfly] += tempReal;
                    imag[butterfly] += tempImag;
                }

                double tempUR = UR;
                UR = tempUR * SR - UI * SI;
                UI = tempUR * SI + UI * SR;
            }
        }
    }
}
