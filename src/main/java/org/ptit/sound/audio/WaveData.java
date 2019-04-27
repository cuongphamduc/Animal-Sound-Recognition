package org.ptit.sound.audio;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;


public class WaveData {
    private byte[] arrFile;
    private byte[] audioBytes;
    private float[] audioData;
    private FileOutputStream fos;
    private ByteArrayInputStream bis;
    private AudioInputStream audioInputStream;
    private AudioFormat format;
    private double durationSec;

    public WaveData() {
    }

    public byte[] getAudioBytes() {
        return audioBytes;
    }

    public double getDurationSec() {
        return durationSec;
    }

    public float[] getAudioData() {
        return audioData;
    }

    public AudioFormat getFormat() {
        return format;
    }

    public float[] extractAmplitudeFromFile(File wavFile) throws Exception {
        FileInputStream fis = new FileInputStream(wavFile);
        arrFile = new byte[(int) wavFile.length()];
        fis.read(arrFile);
        return extractAmplitudeFromFileByteArray(arrFile);
    }

    public float[] extractAmplitudeFromFileByteArray(byte[] arrFile) throws Exception {
        bis = new ByteArrayInputStream(arrFile);
        return extractAmplitudeFromFileByteArrayInputStream(bis);
    }

    public float[] extractAmplitudeFromFileByteArrayInputStream(ByteArrayInputStream bis) throws Exception {
        audioInputStream = AudioSystem.getAudioInputStream(bis);
        float milliseconds = (long) ((audioInputStream.getFrameLength() * 1000) / audioInputStream.getFormat().getFrameRate());
        durationSec = milliseconds / 1000.0;
        return extractFloatDataFromAudioInputStream(audioInputStream);
    }

    public float[] extractFloatDataFromAudioInputStream(AudioInputStream audioInputStream) throws Exception {
        format = audioInputStream.getFormat();
        audioBytes = new byte[(int) (audioInputStream.getFrameLength() * format.getFrameSize())];

        float milliseconds = (long) ((audioInputStream.getFrameLength() * 1000) / audioInputStream.getFormat().getFrameRate());
        durationSec = milliseconds / 1000.0;

        audioInputStream.read(audioBytes);
        return extractFloatDataFromAmplitudeByteArray(format, audioBytes);
    }

    public float[] extractFloatDataFromAmplitudeByteArray(AudioFormat format, byte[] audioBytes) throws Exception {
        audioData = null;

        if (format.getSampleSizeInBits() == 16) {
            int nlengthInSamples = audioBytes.length / 2;
            audioData = new float[nlengthInSamples];
            if (format.isBigEndian()) {
                for (int i = 0; i < nlengthInSamples; i++) {
                    int MSB = audioBytes[2 * i];
                    int LSB = audioBytes[2 * i + 1];
                    audioData[i] = MSB << 8 | (255 & LSB);
                }
            } else {
                for (int i = 0; i < nlengthInSamples; i++) {
                    int LSB = audioBytes[2 * i];
                    int MSB = audioBytes[2 * i + 1];
                    audioData[i] = MSB << 8 | (255 & LSB);
                }
            }
        } else if (format.getSampleSizeInBits() == 8) {
            int nlengthInSamples = audioBytes.length;
            audioData = new float[nlengthInSamples];
            if (format.getEncoding().toString().startsWith("PCM_SIGN")) {
                for (int i = 0; i < audioBytes.length; i++) {
                    audioData[i] = audioBytes[i];
                }
            } else {
                for (int i = 0; i < audioBytes.length; i++) {
                    audioData[i] = audioBytes[i] - 128;
                }
            }
        }

        return audioData;
    }

    public void saveToFile(String name, AudioFileFormat.Type fileType, AudioInputStream audioInputStream) throws Exception {

        System.out.println("WaveData.saveToFile() " + name);

        File myFile = new File(name);
        if (!myFile.exists())
            myFile.mkdirs();

        myFile.delete();

        if (audioInputStream == null) {
            return;
        }

        audioInputStream.reset();
        myFile = new File(name + ".wav");
        int i = 0;

        while (myFile.exists()) {
            String temp = String.format(name + "%d", i++);
            myFile = new File(temp + ".wav");
        }

        if (AudioSystem.write(audioInputStream, fileType, myFile) == -1) {
        }
        System.out.println(myFile.getAbsolutePath());
    }

    public void saveFileByteArray(String fileName, byte[] arrFile) throws Exception {
        fos = new FileOutputStream(fileName);
        fos.write(arrFile);
        fos.close();
        System.out.println("WAV Audio data saved to " + fileName);
    }
}
