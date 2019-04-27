package org.ptit.sound.db;

import java.io.*;


public class ObjectIO<T> {

    private ObjectInputStream input;
    private ObjectOutputStream output;
    T model;

    public ObjectIO() {
    }

    public void setModel(T model) {
        this.model = model;
    }

    public void saveModel(String filePath) throws Exception {

        File file = new File(filePath);
        if (!file.exists()) {
            file.getParentFile().mkdirs();
        }


        // open file
        output = new ObjectOutputStream(new FileOutputStream(file));
        // save model
        output.writeObject(model);
        output.close();
    }

    public T readModel(String filePath) throws Exception {
        // open file
        input = new ObjectInputStream(new FileInputStream(filePath));
        // read
        model = (T) input.readObject();
        input.close();
        return model;
    }
}
