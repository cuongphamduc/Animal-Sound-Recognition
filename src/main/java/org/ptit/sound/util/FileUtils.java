package org.ptit.sound.util;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class FileUtils {
    public static List<String> getAudioFiles() {
        List<String> result = new ArrayList<>();
        File dir = new File("F:\\test");
        System.out.println(dir.getAbsolutePath());
        if (dir.isDirectory()) {

            for (File f : dir.listFiles()) {
                String file_path = f.getAbsolutePath();
                if (file_path.toLowerCase().endsWith("wav")) {
                    result.add(file_path);

                }
            }
        }

        return result;
    }
}
