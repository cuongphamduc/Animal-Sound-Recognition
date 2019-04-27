package org.ptit.sound.db;

import java.io.File;
import java.util.Arrays;
import java.util.List;

public class TrainingTestingWaveFiles {

    protected List<String> folderNames;
    protected File[][] waveFiles;
    protected File wavPath;

    public TrainingTestingWaveFiles(String testOrTrain) {
        if (testOrTrain.equalsIgnoreCase("test")) {
            setWavPath(new File("test"));
        } else if (testOrTrain.equalsIgnoreCase("train")) {
            setWavPath(new File("data"));
        }

    }

    private void readFolder() {
        folderNames = Arrays.asList(getWavPath().list());
    }

    public List<String> readAnimalWavFolder() {
        readFolder();
        return folderNames;
    }

    public File[][] readWaveFilesList() {
        readFolder();
        waveFiles = new File[folderNames.size()][];
        for (int i = 0; i < folderNames.size(); i++) {

            System.out.println(folderNames.get(i));
            File AnimalDir = new File(getWavPath() + File.separator + folderNames.get(i) + File.separator);
            waveFiles[i] = AnimalDir.listFiles();
        }
        System.out.println("++++++Folder's Content+++++");
        for (int i = 0; i < waveFiles.length; i++) {
            for (int j = 0; j < waveFiles[i].length; j++) {
                System.out.print(waveFiles[i][j].getName() + "\t\t");
            }
            System.out.println();
        }
        return waveFiles;

    }

    public File getWavPath() {
        return wavPath;
    }

    public void setWavPath(File wavPath) {
        this.wavPath = wavPath;
        System.out.println("Current path: " + this.wavPath.getName());
    }
}
