package org.ptit.sound.classify;

import org.ptit.sound.classify.resnet.ResNetAudioClassifier;
import org.ptit.sound.util.FileUtils;
import org.ptit.sound.util.ResourceUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.List;

public class ResNetAudioClassifierRun {

    private static final Logger logger = LoggerFactory.getLogger(ResNetAudioClassifierRun.class);



    public static void main(String[] args) throws IOException {
        InputStream inputStream = ResourceUtils.getInputStream("pre_train/resnet-v2.pb");
        ResNetAudioClassifier classifier = new ResNetAudioClassifier();
        classifier.load_model(inputStream);

        List<String> paths = FileUtils.getAudioFiles();

        Collections.shuffle(paths);

        int correct = 0, total = 0;

        for (String path : paths) {
            total++;
            System.out.println("Predicting " + path + " ...");
            File f = new File(path);
            String label = classifier.predict_audio(f);

            String reallabel = path.replace("\\", "/").split("/")[2];
            reallabel = reallabel.replaceAll("\\d", "").split("_")[0];

            if(label.equalsIgnoreCase(reallabel)) {
                correct++;
            }

            System.out.println("Real: " + reallabel);
            System.out.println("Predict: " + label);
            System.out.println("----------------------------------");
        }

        System.out.println("All correct is " + correct + " / " + total);
    }
}
