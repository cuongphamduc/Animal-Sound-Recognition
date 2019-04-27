package org.ptit.sound.audio;


public class EndPointDetection {

    private float[] originalSignal;
    private float[] silenceRemovedSignal;
    private int samplingRate;
    private int firstSamples;
    private int samplePerFrame;

    public EndPointDetection(float[] originalSignal, int samplingRate) {
        this.originalSignal = originalSignal;
        this.samplingRate = samplingRate;
        samplePerFrame = this.samplingRate / 1000 * 25;
        firstSamples = samplePerFrame * 200;
    }

    public float[] doEndPointDetection() {
        float[] voiced = new float[originalSignal.length];
        float sum = 0;
        double sd = 0.0;
        double m = 0.0;

        for (int i = 0; i < firstSamples; i++) {
            sum += originalSignal[i];
        }

        m = sum / firstSamples;
        sum = 0;

        for (int i = 0; i < firstSamples; i++) {
            sum += Math.pow((originalSignal[i] - m), 2);
        }

        sd = Math.sqrt(sum / firstSamples);

        for (int i = 0; i < originalSignal.length; i++) {
            if ((Math.abs(originalSignal[i] - m) / sd) > 2) {
                voiced[i] = 1;
            } else {
                voiced[i] = 0;
            }
        }

        int frameCount = 0;
        int usefulFramesCount = 1;
        int count_voiced;
        int count_unvoiced;
        int voicedFrame[] = new int[originalSignal.length / samplePerFrame];
        int loopCount = originalSignal.length - (originalSignal.length % samplePerFrame);

        for (int i = 0; i < loopCount; i += samplePerFrame) {
            count_voiced = 0;
            count_unvoiced = 0;

            for (int j = i; j < i + samplePerFrame; j++) {
                if (voiced[j] == 1) {
                    count_voiced++;
                } else {
                    count_unvoiced++;
                }
            }

            if (count_voiced > count_unvoiced / 2) {
                usefulFramesCount++;
                voicedFrame[frameCount++] = 1;
            } else {
                voicedFrame[frameCount++] = 0;
            }
        }

        silenceRemovedSignal = new float[usefulFramesCount * samplePerFrame];
        int k = 0;

        for (int i = 0; i < frameCount; i++) {
            if (voicedFrame[i] == 1) {
                for (int j = i * samplePerFrame; j < i * samplePerFrame + samplePerFrame; j++) {
                    silenceRemovedSignal[k++] = originalSignal[j];
                }
            }
        }

        return silenceRemovedSignal;
    }
}
