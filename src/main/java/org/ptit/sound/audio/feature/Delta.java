package org.ptit.sound.audio.feature;


public class Delta {

    int windowLen;

    public void setRegressionWindow(int windowLen) {
        this.windowLen = windowLen;
    }

    public double[][] performDelta2D(double[][] data) {
        int numMFCC = data[0].length;
        int size = data.length;
        double sqrSum = 0;

        for (int i = 1; i <= windowLen; i++) {
            sqrSum += Math.pow(i, 2);
        }
        sqrSum *= 2;

        double[][] delta = new double[size][numMFCC];

        if (size < windowLen) {
            double[][] dataNew = new double[windowLen][numMFCC];
            delta = new double[windowLen][numMFCC];

            for (int i = 0; i < size; i++) {
                for (int j = 0; j < numMFCC; j++) {
                    dataNew[i][j] = data[i][j];
                }
            }

            for (int i = size; i < windowLen; i++) {
                for (int j = 0; j < numMFCC; j++) {
                    dataNew[i][j] = 0;
                }
            }

            size = windowLen;
            data = dataNew;
        }

        for (int i = 0; i < numMFCC; i++) {
            if (size == 1) {
                System.out.println("What the size!!!!");
            }

            for (int k = 0; k < windowLen; k++) {
                delta[k][i] = data[k][i];
            }

            for (int k = size - windowLen; k < size; k++) {
                delta[k][i] = data[k][i];
            }

            for (int j = windowLen; j < size - windowLen; j++) {
                double tmp = 0;

                for (int k = 1; k <= windowLen; k++) {
                    tmp += k * (data[j + k][i] - data[j - k][i]);
                }

                delta[j][i] = tmp / sqrSum;
            }
        }

        return delta;
    }

    public double[] performDelta1D(double[] data) {
        int size = data.length;
        double sqrSum = 0;

        for (int i = 1; i <= windowLen; i++) {
            sqrSum += Math.pow(i, 2);
        }

        sqrSum *= 2;

        double[] delta = new double[size];

        for (int k = 0; k < windowLen; k++) {
            delta[k] = data[k];
        }

        for (int k = size - windowLen; k < size; k++) {
            delta[k] = data[k];
        }

        for (int j = windowLen; j < size - windowLen; j++) {
            double tmp = 0;

            for (int k = 1; k <= windowLen; k++) {
                tmp += k * (data[j + k] - data[j - k]);
            }

            delta[j] = tmp / sqrSum;
        }

        return delta;
    }
}
