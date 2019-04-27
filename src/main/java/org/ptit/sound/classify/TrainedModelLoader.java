package org.ptit.sound.classify;

import java.io.IOException;
import java.io.InputStream;

public interface TrainedModelLoader {
    void load_model(InputStream inputStream) throws IOException;
}
