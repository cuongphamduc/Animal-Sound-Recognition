package org.ptit.sound.classify;

import java.awt.image.BufferedImage;
import java.io.File;

public interface AudioEncoder {
    float[] encode_image(BufferedImage image);
    float[] encode_audio(File f);

    float[] encode_image(BufferedImage image, int imgWidth, int imgHeight);
}
