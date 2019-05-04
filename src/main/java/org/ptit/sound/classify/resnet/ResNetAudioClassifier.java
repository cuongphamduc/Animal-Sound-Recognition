package org.ptit.sound.classify.resnet;

import org.ptit.sound.util.MelSpectrogram;
import org.ptit.sound.util.MelSpectrogramDimension;
import org.ptit.sound.classify.AudioClassifier;
import org.ptit.sound.classify.TrainedModelLoader;
import org.ptit.sound.util.ImageUtils;
import org.ptit.sound.util.InputStreamUtils;
import org.ptit.sound.util.TensorUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tensorflow.Graph;
import org.tensorflow.Session;
import org.tensorflow.Tensor;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

public class ResNetAudioClassifier implements TrainedModelLoader, AudioClassifier, AutoCloseable {

    private Graph graph = new Graph();
    public ResNetAudioClassifier() {

    }

    @Override
    public void load_model(InputStream inputStream) throws IOException {
        byte[] bytes = InputStreamUtils.getBytes(inputStream);
        graph.importGraphDef(bytes);

    }

    private static final Logger logger = LoggerFactory.getLogger(ResNetAudioClassifier.class);

    @Override
    public String predict_image(BufferedImage image) {
        return predict_image(image, MelSpectrogramDimension.Width,
                MelSpectrogramDimension.Height);
    }

    @Override
    public Float predict_image_percent(BufferedImage image, String animal) {
        return predict_image_percent(image, animal, MelSpectrogramDimension.Width,
                MelSpectrogramDimension.Height);
    }

    @Override
    public float[] encode_image(BufferedImage image, int imgWidth, int imgHeight){

        image = ImageUtils.resizeImage(image, imgWidth, imgHeight);

        Tensor<Float> imageTensor = TensorUtils.getImageTensor(image, imgWidth, imgHeight);



        try (Session sess = new Session(graph);
             Tensor<Float> result =
                     sess.runner().feed("input_1:0", imageTensor)
                             .fetch("output_node0:0").run().get(0).expect(Float.class)) {
            final long[] rshape = result.shape();
            if (result.numDimensions() != 2 || rshape[0] != 1) {
                throw new RuntimeException(
                        String.format(
                                "Expected model to produce a [1 N] shaped tensor where N is the number of labels, instead it produced one with shape %s",
                                Arrays.toString(rshape)));
            }
            int nlabels = (int) rshape[1];
            float[] predicted = result.copyTo(new float[1][nlabels])[0];
            return predicted;
        } catch(Exception ex) {
            logger.error("Failed to predict image", ex);
            ex.printStackTrace();
        }

        return null;
    }

    @Override
    public float[] encode_image(BufferedImage image) {
        return encode_image(image, MelSpectrogramDimension.Width,
                MelSpectrogramDimension.Height);
    }

    @Override
    public void close() throws Exception {
        if(graph != null) {
            graph.close();
            graph = null;
        }
    }

    @Override
    public float[] encode_audio(File f) {
        BufferedImage image = MelSpectrogram.convert_to_image(f);

        if(image != null) {
            return encode_image(image);
        }

        return null;
    }

    @Override
    public String predict_audio(File f) {
        BufferedImage image = MelSpectrogram.convert_to_image(f);

        if(image != null) {
            return predict_image(image);
        }

        return "NA";
    }

    @Override
    public Float predict_audio_percent(File f, String animal) {
        BufferedImage image = MelSpectrogram.convert_to_image(f);

        if(image != null) {
            return predict_image_percent(image, animal);
        }

        return 0.0f;
    }
}
