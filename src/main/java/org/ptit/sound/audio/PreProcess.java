package org.ptit.sound.audio;


public class PreProcess {

    public int numOfFrames;
    public float[][] framedSignal;
    float[] originalSignal;
    float[] afterEndPtDetection;
    int samplePerFrame;
    int framedArrayLength;
    float[] hammingWindow;
    EndPointDetection epd;
    int samplingRate;

    public PreProcess(float[] originalSignal, int samplePerFrame, int samplingRate) {
        this.originalSignal = originalSignal;
        this.samplePerFrame = samplePerFrame;
        this.samplingRate = samplingRate;

        normalizePCM();
        epd = new EndPointDetection(this.originalSignal, this.samplingRate);
        afterEndPtDetection = epd.doEndPointDetection();

        doFraming();
        doWindowing();
    }

    private void normalizePCM() {
        float max = originalSignal[0];

        for (int i = 1; i < originalSignal.length; i++) {
            if (max < Math.abs(originalSignal[i])) {
                max = Math.abs(originalSignal[i]);
            }
        }

        for (int i = 0; i < originalSignal.length; i++) {
            originalSignal[i] = originalSignal[i] / max;
        }
    }

    private void doFraming() {
        numOfFrames = 2 * afterEndPtDetection.length / samplePerFrame - 1;
//        System.out.println("numOfFrames    " + numOfFrames + "\nsamplePerFrame    " + samplePerFrame + "\nEPD length    " + afterEndPtDetection.length);
        framedSignal = new float[numOfFrames][samplePerFrame];
        for (int i = 0; i < numOfFrames; i++) {
            int startIndex = (i * samplePerFrame / 2);
            for (int j = 0; j < samplePerFrame; j++) {
                framedSignal[i][j] = afterEndPtDetection[startIndex + j];
            }
        }
    }

    /**
     * does hamming window on each frame
     */
    private void doWindowing() {
        // prepare hammingWindow
        hammingWindow = new float[samplePerFrame + 1];
        // prepare for through out the data
        for (int i = 1; i <= samplePerFrame; i++) {

            hammingWindow[i] = (float) (0.54 - 0.46 * (Math.cos(2 * Math.PI * i / samplePerFrame)));
        }
        // do windowing
        for (int i = 0; i < numOfFrames; i++) {
            for (int j = 0; j < samplePerFrame; j++) {
                framedSignal[i][j] = framedSignal[i][j] * hammingWindow[j + 1];
            }
        }
    }
}
