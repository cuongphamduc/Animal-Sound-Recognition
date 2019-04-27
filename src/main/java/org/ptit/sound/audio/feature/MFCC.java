package org.ptit.sound.audio.feature;


public class MFCC {

    FFT fft;
    DCT dct;
    private int numMelFilters = 26;
    private int numCepstra;
    private double preEmphasisAlpha = 0.95;
    private double lowerFilterFreq = 300.00;
    private double samplingRate;
    private double upperFilterFreq;
    private double[] bin;
    private int samplePerFrame;

    public MFCC(int samplePerFrame, int samplingRate, int numCepstra) {
        this.samplePerFrame = samplePerFrame;
        this.samplingRate = samplingRate;
        this.numCepstra = numCepstra;
        upperFilterFreq = samplingRate / 2.0;
        fft = new FFT();
        dct = new DCT(this.numCepstra, numMelFilters);
    }

    public double[] doMFCC(float[] framedSignal) {
        framedSignal = preEmphasis(framedSignal);
        bin = magnitudeSpectrum(framedSignal);
        int[] cbin = fftBinIndices();
        double[] fbank = melFilter(bin, cbin);
        double f[] = nonLinearTransformation(fbank);
        double[] cepc = dct.performDCT(f);
        return cepc;
    }

    private double[] nonLinearTransformation(double fbank[]) {
        double f[] = new double[fbank.length];
        final double FLOOR = -10;
        for (int i = 0; i < fbank.length; i++) {
            f[i] = Math.log(fbank[i]);
            if (f[i] < FLOOR) {
                f[i] = FLOOR;
            }
        }
        return f;
    }

    private double[] magnitudeSpectrum(float[] frame) {
        double[] magSpectrum = new double[frame.length];
        fft.computeFFT(frame);

        for (int k = 0; k < frame.length; k++) {
            magSpectrum[k] = Math.sqrt(fft.real[k] * fft.real[k] + fft.imag[k] * fft.imag[k]);
        }

        return magSpectrum;
    }

    private float[] preEmphasis(float inputSignal[]) {
        // System.err.println(" inside pre Emphasis");
        float outputSignal[] = new float[inputSignal.length];
        // apply pre-emphasis to each sample
        for (int n = 1; n < inputSignal.length; n++) {
            outputSignal[n] = (float) (inputSignal[n] - preEmphasisAlpha * inputSignal[n - 1]);
        }
        return outputSignal;
    }

    private int[] fftBinIndices() {
        int[] cbin = new int[numMelFilters + 2];
        cbin[0] = (int) Math.round(lowerFilterFreq / samplingRate * samplePerFrame);
        cbin[cbin.length - 1] = (samplePerFrame / 2);

        for (int i = 1; i <= numMelFilters; i++) {
            double fc = centerFreq(i);
            cbin[i] = (int) Math.round(fc / samplingRate * samplePerFrame);
        }

        return cbin;
    }

    private double[] melFilter(double[] bin, int[] cbin) {
        double[] temp = new double[numMelFilters + 2];

        for (int k = 1; k <= numMelFilters; k++) {
            double num1 = 0.0, num2 = 0.0;

            for (int i = cbin[k - 1]; i <= cbin[k]; i++) {
                num1 += ((i - cbin[k - 1] + 1) / (cbin[k] - cbin[k - 1] + 1)) * bin[i];
            }

            for (int i = cbin[k] + 1; i <= cbin[k + 1]; i++) {
                num2 += (1 - ((i - cbin[k]) / (cbin[k + 1] - cbin[k] + 1))) * bin[i];
            }

            temp[k] = num1 + num2;
        }

        double[] fbank = new double[numMelFilters];

        for (int i = 0; i < numMelFilters; i++) {
            fbank[i] = temp[i + 1];
        }
        return fbank;
    }

    private double centerFreq(int i) {
        double melFLow, melFHigh;
        melFLow = freqToMel(lowerFilterFreq);
        melFHigh = freqToMel(upperFilterFreq);
        double temp = melFLow + ((melFHigh - melFLow) / (numMelFilters + 1)) * i;
        return inverseMel(temp);
    }

    private double inverseMel(double x) {
        double temp = Math.exp(x / 1125) - 1;
        return 700 * (temp);
    }

    private double freqToMel(double freq) {
        return 1125 * Math.log(1 + freq / 700);
    }

}
