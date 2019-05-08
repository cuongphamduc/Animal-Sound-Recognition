package org.ptit.sound.classify;

import org.ptit.sound.db.DataBase;
import org.ptit.sound.db.ObjectIODataBase;
import org.ptit.sound.util.ArrayWriter;

public class HiddenMarkov {

    protected final int delta = 2;
    final double MIN_PROBABILITY = 0.0001;
    public int[] q;
    protected int len_obSeq;
    protected int num_states;
    protected int num_symbols;
    protected int[][] obSeq;
    protected int[] currentSeq;
    protected int num_obSeq;
    protected double[][] transition;
    protected double[][] output;
    protected double[] pi;
    protected double[][] alpha;
    protected double[][] beta;
    protected double[] scaleFactor;
    private int[][] psi;

    public HiddenMarkov(String Animal) throws Exception {
        DataBase db = new ObjectIODataBase();
        db.setType("hmm");
        HMMModel model = new HMMModel();
        model = (HMMModel) db.readModel(Animal);// System.out.println(model.getClass());
        num_obSeq = model.getNum_obSeq();
        output = model.getOutput();// ArrayWriter.print2DTabbedDoubleArrayToConsole(output);
        transition = model.getTransition();
        pi = model.getPi();
        num_states = output.length;
        num_symbols = output[0].length;
        // System.out.println("num states :"+num_states+"num symbols :"+num_symbols);
    }

    public HiddenMarkov(int num_states, int num_symbols) {
        this.num_states = num_states;
        this.num_symbols = num_symbols;
        transition = new double[num_states][num_states];
        output = new double[num_states][num_symbols];
        pi = new double[num_states];

        pi[0] = 1;
        for (int i = 1; i < num_states; i++) {
            pi[i] = 0;
        }

        randomProb();
    }

    /**
     * viterbi algorithm
     */
    public double viterbi(int[] testSeq) {
        setObSeq(testSeq);
        double[][] phi = new double[len_obSeq][num_states];
        psi = new int[len_obSeq][num_states];
        q = new int[len_obSeq];

        for (int i = 0; i < num_states; i++) {
            double temp = pi[i];
            if (temp == 0) {
                temp = MIN_PROBABILITY;
            }

            phi[0][i] = Math.log(temp) + Math.log(output[i][currentSeq[0]]);
            psi[0][i] = 0;
        }

        for (int t = 1; t < len_obSeq; t++) {
            for (int j = 0; j < num_states; j++) {
                double max = phi[t - 1][0] + Math.log(transition[0][j]);
                double temp = 0;
                int index = 0;

                for (int i = 1; i < num_states; i++) {

                    temp = phi[t - 1][i] + Math.log(transition[i][j]);
                    if (temp > max) {
                        max = temp;
                        index = i;
                    }

                }

                phi[t][j] = max + Math.log(output[j][currentSeq[t]]);
                psi[t][j] = index;
            }
        }

        double max = phi[len_obSeq - 1][0];
        double temp = 0;
        int index = 0;
        for (int i = 1; i < num_states; i++) {
            temp = phi[len_obSeq - 1][i];

            if (temp > max) {
                max = temp;
                index = i;
            }
        }

        q[len_obSeq - 1] = index;

        for (int t = len_obSeq - 2; t >= 0; t--) {
            q[t] = psi[t + 1][q[t + 1]];
        }

        return max;
    }

    private void rescaleBeta(int t) {
        for (int i = 0; i < num_states; i++) {
            beta[t][i] *= scaleFactor[t];
        }
    }

    private void rescaleAlpha(int t) {
        // calculate scale coefficients
        for (int i = 0; i < num_states; i++) {
            scaleFactor[t] += alpha[t][i];
        }

        scaleFactor[t] = 1 / scaleFactor[t];

        // apply scale coefficients
        for (int i = 0; i < num_states; i++) {
            alpha[t][i] *= scaleFactor[t];
        }
    }

    public double getProbability(int[] testSeq) {
        setObSeq(testSeq);
        double temp = computeAlpha();

        return temp;
    }

    /**
     * forward
     */
    protected double computeAlpha() {
        double probability = 0;

        for (int t = 0; t < len_obSeq; t++) {
            scaleFactor[t] = 0;
        }

        for (int i = 0; i < num_states; i++) {
            alpha[0][i] = pi[i] * output[i][currentSeq[0]];
        }
        rescaleAlpha(0);

        for (int t = 0; t < len_obSeq - 1; t++) {
            for (int j = 0; j < num_states; j++) {
                double sum = 0;

                for (int i = 0; i < num_states; i++) {
                    sum += alpha[t][i] * transition[i][j];
                }

                alpha[t + 1][j] = sum * output[j][currentSeq[t + 1]];
            }
            rescaleAlpha(t + 1);
        }

        for (int i = 0; i < num_states; i++) {
            probability += alpha[len_obSeq - 1][i];
        }

        probability = 0;

        for (int t = 0; t < len_obSeq; t++) {
            probability += Math.log(scaleFactor[t]);
        }

        return probability;
    }

    /**
     * backward
     */
    protected void computeBeta() {
        for (int i = 0; i < num_states; i++) {
            beta[len_obSeq - 1][i] = 1;
        }
        rescaleBeta(len_obSeq - 1);

        for (int t = len_obSeq - 2; t >= 0; t--) {
            for (int i = 0; i < num_states; i++) {
                for (int j = 0; j < num_states; j++) {
                    beta[t][i] += transition[i][j] * output[j][currentSeq[t + 1]] * beta[t + 1][j];
                }
            }
            rescaleBeta(t);
        }
    }

    public void setNumObSeq(int k) {
        num_obSeq = k;
        obSeq = new int[k][];
    }

    public void setTrainSeq(int k, int[] trainSeq) {
        obSeq[k] = trainSeq;
    }

    public void setTrainSeq(int[][] trainSeq) {
        num_obSeq = trainSeq.length;
        obSeq = new int[num_obSeq][];
        for (int k = 0; k < num_obSeq; k++) {
            obSeq[k] = trainSeq[k];
        }
    }

    public void train() {
        for (int i = 0; i < 20; i++) {
            reestimate();
            System.out.println("reestimating.....");
        }
    }

    /**
     * Baum-Welch algorithm
     */
    private void reestimate() {
        double[][] newTransition = new double[num_states][num_states];
        double[][] newOutput = new double[num_states][num_symbols];
        double[] numerator = new double[num_obSeq];
        double[] denominator = new double[num_obSeq];

        // calculate new transition probability matrix
        double sumP = 0;

        for (int i = 0; i < num_states; i++) {
            for (int j = 0; j < num_states; j++) {

                if (j < i || j > i + delta) {
                    newTransition[i][j] = 0;
                } else {
                    for (int k = 0; k < num_obSeq; k++) {
                        numerator[k] = denominator[k] = 0;
                        setObSeq(obSeq[k]);

                        sumP += computeAlpha();
                        computeBeta();
                        for (int t = 0; t < len_obSeq - 1; t++) {
                            numerator[k] += alpha[t][i] * transition[i][j] * output[j][currentSeq[t + 1]] * beta[t + 1][j];
                            denominator[k] += alpha[t][i] * beta[t][i];
                        }
                    }
                    double denom = 0;
                    for (int k = 0; k < num_obSeq; k++) {
                        newTransition[i][j] += (1 / sumP) * numerator[k];
                        denom += (1 / sumP) * denominator[k];
                    }
                    newTransition[i][j] /= denom;
                    newTransition[i][j] += MIN_PROBABILITY;
                }
            }
        }

        // calculate new output probability matrix
        sumP = 0;
        for (int i = 0; i < num_states; i++) {
            for (int j = 0; j < num_symbols; j++) {
                for (int k = 0; k < num_obSeq; k++) {
                    numerator[k] = denominator[k] = 0;
                    setObSeq(obSeq[k]);

                    sumP += computeAlpha();
                    computeBeta();

                    for (int t = 0; t < len_obSeq - 1; t++) {
                        if (currentSeq[t] == j) {
                            numerator[k] += alpha[t][i] * beta[t][i];
                        }
                        denominator[k] += alpha[t][i] * beta[t][i];
                    }
                }

                double denom = 0;
                for (int k = 0; k < num_obSeq; k++) {
                    denom += (1 / sumP) * denominator[k];
                    newOutput[i][j] += (1 / sumP) * numerator[k];
                }

                newOutput[i][j] /= denom;
                newOutput[i][j] += MIN_PROBABILITY;
            }
        }

        // replace old matrices after re-estimate
        transition = newTransition;
        output = newOutput;
    }

    public void setObSeq(int[] observationSeq) {
        currentSeq = observationSeq;
        len_obSeq = observationSeq.length;
        // System.out.println("len_obSeq<<setObSeq()   "+len_obSeq);

        alpha = new double[len_obSeq][num_states];
        beta = new double[len_obSeq][num_states];
        scaleFactor = new double[len_obSeq];
    }

    private void randomProb() {
        for (int i = 0; i < num_states; i++) {
            for (int j = 0; j < num_states; j++) {
                if (j < i || j > i + delta) {
                    transition[i][j] = 0;
                } else {
                    double randNum = Math.random();
                    transition[i][j] = randNum;
                }
            }
            for (int j = 0; j < num_symbols; j++) {
                double randNum = Math.random();
                output[i][j] = randNum;
            }

        }
    }

    public void save(String modelName) throws Exception {
        DataBase db = new ObjectIODataBase();
        db.setType("hmm");
        HMMModel model = new HMMModel();
        model.setOutput(output);
        ArrayWriter.print2DTabbedDoubleArrayToConsole(output);
        model.setPi(pi);
        ArrayWriter.printDoubleArrayToConsole(pi);
        model.setTransition(transition);
        ArrayWriter.print2DTabbedDoubleArrayToConsole(transition);
        db.saveModel(model, modelName);
    }
}