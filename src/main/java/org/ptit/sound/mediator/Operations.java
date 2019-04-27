package org.ptit.sound.mediator;

import org.ptit.sound.audio.FeatureExtract;
import org.ptit.sound.audio.FormatControlConf;
import org.ptit.sound.audio.PreProcess;
import org.ptit.sound.audio.WaveData;
import org.ptit.sound.audio.feature.FeatureVector;
import org.ptit.sound.classify.HiddenMarkov;
import org.ptit.sound.classify.vq.Codebook;
import org.ptit.sound.classify.vq.Points;
import org.ptit.sound.db.DataBase;
import org.ptit.sound.db.ObjectIODataBase;
import org.ptit.sound.db.TrainingTestingWaveFiles;
import org.ptit.sound.util.ArrayWriter;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


public class Operations {

    TrainingTestingWaveFiles trainTestWavs;
    FormatControlConf fc = new FormatControlConf();
    int samplingRate = (int) fc.getRate();
    int samplePerFrame = 1024;                            // 23.22ms
    int FEATUREDIMENSION = 39;
    List<String> Animals;
    String[] users;
    File[][] wavFiles;
    FeatureExtract fExt;
    WaveData wd;
    PreProcess prp;
    Codebook cb;
    List<double[]> allFeaturesList = new ArrayList<double[]>();
    HiddenMarkov mkv;
    DataBase db;
    private HiddenMarkov hmmModels[];

    public Operations() {
        wd = new WaveData();
    }

    public void generateCodebook() throws Exception {
        trainTestWavs = new TrainingTestingWaveFiles("train");
        int totalFrames = 0;
        wavFiles = trainTestWavs.readWaveFilesList();
        for (int i = 0; i < wavFiles.length; i++) {
            for (int j = 0; j < wavFiles[i].length; j++) {
                System.out.println("Current file ::: " + wavFiles[i][j].getAbsoluteFile());
                FeatureVector feature = extractFeatureFromFile(wavFiles[i][j]);
                for (int k = 0; k < feature.getNoOfFrames(); k++) {
                    allFeaturesList.add(feature.getFeatureVector()[k]);
                    totalFrames++;//
                }
            }
        }
//        System.out.println("total frames  : " + totalFrames + "  allFeaturesList.size: " + allFeaturesList.size());
        double allFeatures[][] = new double[totalFrames][FEATUREDIMENSION];
        for (int i = 0; i < totalFrames; i++) {
            double[] tmp = allFeaturesList.get(i);
            allFeatures[i] = tmp;
        }
        Points pts[] = new Points[totalFrames];
        for (int j = 0; j < totalFrames; j++) {
            pts[j] = new Points(allFeatures[j]);
        }
        System.out.println("Generating Codebook........");
        Codebook cbk = new Codebook(pts);
        cbk.saveToFile();
        System.out.println("Codebook Generation Completed");
    }

    /**
     * @throws Exception
     */
    public void hmmTrain() throws Exception {
//        System.out.println("inside hmm train");
        trainTestWavs = new TrainingTestingWaveFiles("train");
        cb = new Codebook();
        // for each training Animal
        int quantized[][];
        // extract features
        wavFiles = trainTestWavs.readWaveFilesList();
        Animals = trainTestWavs.readAnimalWavFolder();
        for (int i = 0; i < wavFiles.length; i++) {
            quantized = new int[wavFiles[i].length][];// training sequence
            String currentAnimal = Animals.get(i);
            System.out.println("Current Animal ::: " + currentAnimal);
            for (int j = 0; j < wavFiles[i].length; j++) {
                System.out.println("Current file ::: " + wavFiles[i][j].getAbsoluteFile());
                FeatureVector feature = extractFeatureFromFile(wavFiles[i][j]);
                Points[] pts = getPointsFromFeatureVector(feature);
                quantized[j] = cb.quantize(pts);
            }
            mkv = new HiddenMarkov(50, 256);

            System.out.println("Training.......");
            mkv.setTrainSeq(quantized);
            mkv.train();
            mkv.save(currentAnimal);
            System.out.println("Animal " + currentAnimal + " is trained");
        }
        System.out.println("HMM Train Completed");
    }

    public String hmmGetAnimalFromFile(File speechFile) throws Exception {
        FeatureVector feature = extractFeatureFromFile(speechFile);
        return hmmGetAnimalWithFeature(feature);
    }

    public String hmmGetAnimalFromFileByteArray(byte[] byteArray) throws Exception {
        FeatureVector feature = extractFeatureFromFileByteArray(byteArray);
        return hmmGetAnimalWithFeature(feature);
    }

    public String hmmGetAnimalFromAmplitureArray(float[] byteArray) throws Exception {
        FeatureVector feature = extractFeatureFromExtractedAmplitureByteArray(byteArray);
        return hmmGetAnimalWithFeature(feature);
    }

    public String hmmGetAnimalWithFeature(FeatureVector feature) throws Exception {
        Points[] pts = getPointsFromFeatureVector(feature);
        cb = new Codebook();

        int quantized[] = cb.quantize(pts);

        db = new ObjectIODataBase();
        db.setType("hmm");
        Animals = db.readRegistered();
        db = null;
//        System.out.println("Registred Animals ::: count : " + Animals.size());
//        ArrayWriter.printStringArrayToConole(Animals);
        hmmModels = new HiddenMarkov[Animals.size()];

        for (int i = 0; i < Animals.size(); i++) {
            hmmModels[i] = new HiddenMarkov(Animals.get(i));
        }

        double likelihoods[] = new double[Animals.size()];
        for (int j = 0; j < Animals.size(); j++) {
            likelihoods[j] = hmmModels[j].viterbi(quantized);
//            System.out.println("Likelihood with " + Animals.get(j) + " is " + likelihoods[j]);
        }

        double highest = Double.NEGATIVE_INFINITY;
        int AnimalIndex = -1;
        for (int j = 0; j < Animals.size(); j++) {
            if (likelihoods[j] > highest) {
                highest = likelihoods[j];
                AnimalIndex = j;
            }
        }
        System.out.println("Predict: " + Animals.get(AnimalIndex));
        System.out.println("----------------------------------");
        return Animals.get(AnimalIndex);
    }

    public FeatureVector extractFeatureFromFileByteArray(byte[] byteArray) throws Exception {
        float[] arrAmp;
        arrAmp = wd.extractAmplitudeFromFileByteArray(byteArray);
        return extractFeatureFromExtractedAmplitureByteArray(arrAmp);
    }

    public FeatureVector extractFeatureFromExtractedAmplitureByteArray(float[] arrAmp) {
        prp = new PreProcess(arrAmp, samplePerFrame, samplingRate);
        fExt = new FeatureExtract(prp.framedSignal, samplingRate, samplePerFrame);
        fExt.makeMfccFeatureVector();
        return fExt.getFeatureVector();
    }

    private FeatureVector extractFeatureFromFile(File speechFile) throws Exception {
        float[] arrAmp;
        arrAmp = wd.extractAmplitudeFromFile(speechFile);
        return extractFeatureFromExtractedAmplitureByteArray(arrAmp);
    }

    private Points[] getPointsFromFeatureVector(FeatureVector features) {
        // get Points object from all feature vector
        Points pts[] = new Points[features.getFeatureVector().length];
        for (int j = 0; j < features.getFeatureVector().length; j++) {
            pts[j] = new Points(features.getFeatureVector()[j]);
        }
        return pts;
    }

    public boolean checkAnimal(String Animal) {
        db = new ObjectIODataBase();
        db.setType("hmm");
        Animals = db.readRegistered();
        for (int i = 0; i < Animals.size(); i++) {
            if (Animals.get(i).equalsIgnoreCase(Animal)) {
                return true;// Animal found
            }
        }
        return false;// Animal not found
    }

    public boolean checkSelectedPath() {
        return true;
    }

    double test0[][] =                                                                                                                                                                                                                                                                                                                                    // user0
            {{1.0, 2.5, 5.0, 10, 3.0, 8.0, 4.0, 45.0}, {1.0, 2.0, 4.0, 10, 3.0, 9.0, 3.5, 52.0}, {1.0, 2.2, 5.0, 11, 3.0, 9.0, 4.0, 52.0}, {1.0, 3.0, 5.0, 9, 3.0, 10.0, 3.1, 51.0}, {1.0, 2.0, 6.0, 10, 3.0, 9.0, 4.0, 54.0}, {1.0, 2.2, 5.0, 12, 3.0, 8.0, 4.0, 52.0}};
    double test1[][] =                                                                                                                                                                                                                                                                                                                                    // user1
            {{2.0, 2.5, 5.0, 20, 3.0, 18.0, 4.0, 150.0}, {2.0, 2.0, 4.0, 19, 3.0, 19.0, 3.5, 142.0}, {2.0, 2.5, 5.0, 20, 3.0, 19.0, 4.0, 150.0}, {2.0, 3.0, 5.0, 20, 3.0, 18.0, 3.1, 151.0}, {2.0, 2.0, 6.0, 20, 3.0, 19.0, 4.0, 150.0}, {2.0, 2.7, 5.0, 22, 3.0, 18.0, 4.0, 145.0}};
    double test2[][] =                                                                                                                                                                                                                                                                                                                                    // suer
            {{12.0, 2.5, 5.0, 30, 13.0, 18.0, 4.0, 10.0}, {10.0, 2.0, 4.0, 30, 13.0, 19.0, 3.5, 12.0}, {12.0, 2.5, 5.0, 33, 13.0, 19.0, 4.0, 10.0}, {9.0, 3.0, 5.0, 30, 13.0, 18.0, 3.1, 11.0}, {11.0, 2.0, 6.0, 30, 13.0, 19.0, 4.0, 10.0}, {12.0, 2.7, 5.0, 31, 13.0, 18.0, 4.0, 12.0}};
    double test3[][] =                                                                                                                                                                                                                                                                                                                                    // suer
            {{322.0, 2.5, 5.0, 30, 303.0, 18.0, 4.0, 300.0}, {312.0, 2.0, 4.0, 30, 353.0, 18.0, 3.5, 312.0}, {312.0, 2.5, 5.0, 30, 313.0, 19.0, 4.0, 300.0}, {322.0, 3.0, 5.0, 30, 303.0, 16.0, 3.1, 311.0}, {312.0, 2.0, 6.0, 30, 313.0, 19.0, 4.0, 300.0}, {332.0, 2.7, 5.0, 30, 313.0, 12.0, 4.0, 302.0}};
    double test4[][] = {{412.0, 2.5, 5.0, 30, 400.0, 18.0, 41.0, 14.0}, {412.0, 2.0, 8.0, 30, 413.0, 19.0, 43.5, 12.0}, {400.0, 1.5, 5.0, 30, 413.0, 19.0, 44.0, 9.0}, {412.0, 3.0, 3.0, 30, 413.0, 18.0, 43.1, 11.0}, {412.0, 2.0, 6.0, 30, 433.0, 19.0, 44.0, 15.0}, {400.0, 1.7, 9.0, 30, 433.0, 28.0, 40.0, 12.0}};
    double train[][][] = {{                                                                                                                                                                                                                                                                                                                                // user0
            {1.0, 2.5, 5.0, 10, 3.0, 8.0, 4.0, 50.0}, {1.0, 2.0, 4.0, 10, 3.0, 9.0, 3.5, 52.0}, {1.0, 2.5, 5.0, 10, 3.0, 9.0, 4.0, 40.0}, {1.0, 3.0, 5.0, 10, 3.0, 10.0, 3.1, 51.0}, {1.0, 2.0, 6.0, 10, 3.0, 9.0, 4.0, 50.0}, {1.0, 2.7, 5.0, 10, 3.0, 8.0, 4.0, 59.0}}, {                                            // user1
            {2.0, 2.5, 5.0, 20, 3.0, 18.0, 4.0, 150.0}, {2.0, 2.0, 4.0, 20, 3.0, 19.0, 3.5, 152.0}, {2.0, 2.5, 5.0, 20, 3.0, 19.0, 4.0, 150.0}, {2.0, 3.0, 5.0, 20, 3.0, 18.0, 3.1, 151.0}, {2.0, 2.0, 6.0, 20, 3.0, 19.0, 4.0, 150.0}, {2.0, 2.7, 5.0, 20, 3.0, 18.0, 4.0, 152.0}}, {                                    // user2
            {12.0, 2.5, 5.0, 30, 13.0, 18.0, 4.0, 10.0}, {12.0, 2.0, 4.0, 30, 13.0, 19.0, 3.5, 12.0}, {12.0, 2.5, 5.0, 30, 13.0, 19.0, 4.0, 10.0}, {12.0, 3.0, 5.0, 30, 13.0, 18.0, 3.1, 11.0}, {12.0, 2.0, 6.0, 30, 13.0, 19.0, 4.0, 10.0}, {12.0, 2.7, 5.0, 30, 13.0, 18.0, 4.0, 12.0}}, {                            // user3
            {312.0, 2.5, 5.0, 30, 313.0, 18.0, 4.0, 310.0}, {312.0, 2.0, 4.0, 30, 313.0, 19.0, 3.5, 312.0}, {312.0, 2.5, 5.0, 30, 313.0, 19.0, 4.0, 310.0}, {312.0, 3.0, 5.0, 30, 313.0, 18.0, 3.1, 311.0}, {312.0, 2.0, 6.0, 30, 313.0, 19.0, 4.0, 310.0}, {312.0, 2.7, 5.0, 30, 313.0, 18.0, 4.0, 312.0}}, {            // user4
            {412.0, 2.5, 5.0, 30, 413.0, 18.0, 44.0, 10.0}, {412.0, 2.0, 4.0, 30, 413.0, 19.0, 43.5, 12.0}, {412.0, 2.5, 5.0, 30, 413.0, 19.0, 44.0, 10.0}, {412.0, 3.0, 5.0, 30, 413.0, 18.0, 43.1, 11.0}, {412.0, 2.0, 6.0, 30, 413.0, 19.0, 44.0, 10.0}, {412.0, 2.7, 5.0, 30, 413.0, 18.0, 44.0, 12.0}}, {            // user5
            {152.0, 52.5, 55.0, 30, 13.0, 18.0, 4.0, 10.0}, {152.0, 52.0, 54.0, 30, 13.0, 19.0, 3.5, 12.0}, {152.0, 52.5, 55.0, 30, 13.0, 19.0, 4.0, 10.0}, {152.0, 53.0, 55.0, 30, 13.0, 18.0, 3.1, 11.0}, {152.0, 52.0, 56.0, 30, 13.0, 19.0, 4.0, 10.0}, {152.0, 52.7, 55.0, 30, 13.0, 18.0, 4.0, 12.0}}, {            // suer6
            {162.0, 2.5, 56.0, 30, 13.0, 18.0, 64.0, 10.0}, {162.0, 2.0, 46.0, 30, 13.0, 19.0, 63.5, 12.0}, {162.0, 2.5, 56.0, 30, 13.0, 19.0, 64.0, 10.0}, {162.0, 3.0, 56.0, 30, 13.0, 18.0, 63.1, 11.0}, {162.0, 2.0, 66.0, 30, 13.0, 19.0, 64.0, 10.0}, {162.0, 2.7, 56.0, 30, 13.0, 18.0, 64.0, 12.0}}, {            // user7
            {12.0, 72.5, 5.0, 30, 13.0, 18.0, 74.0, 170.0}, {12.0, 72.0, 4.0, 30, 19.0, 19.0, 73.5, 172.0}, {12.0, 72.5, 5.0, 30, 13.0, 19.0, 74.0, 170.0}, {12.0, 73.0, 5.0, 30, 18.0, 18.0, 73.1, 171.0}, {12.0, 72.0, 6.0, 30, 13.0, 19.0, 74.0, 170.0}, {12.0, 72.7, 5.0, 30, 13.0, 18.0, 74.0, 172.0}}, {            // user8
            {12.0, 82.5, 5.0, 30, 823.0, 11.0, 42.0, 180.0}, {12.0, 82.0, 4.0, 30, 813.0, 19.0, 32.5, 182.0}, {12.0, 85.5, 5.0, 30, 823.0, 20.0, 42.0, 180.0}, {12.0, 83.0, 6.0, 30, 813.0, 18.0, 32.1, 188.0}, {12.0, 82.0, 6.0, 30, 813.0, 19.0, 42.0, 180.0}, {12.0, 82.7, 5.0, 30, 823.0, 21.0, 42.0, 182.0}},};
    // ///////////////////////////////////////////////////////////////////////////////////
}
