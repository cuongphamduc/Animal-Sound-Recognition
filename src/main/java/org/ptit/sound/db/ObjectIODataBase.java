/*
  Please feel free to use/modify this class. 
  If you give me credit by keeping this information or
  by sending me an email before using it or by reporting bugs , i will be happy.
  Email : gtiwari333@gmail.com,
  Blog : http://ganeshtiwaridotcomdotnp.blogspot.com/ 
 */
package org.ptit.sound.db;

import org.ptit.sound.classify.CodeBookDictionary;
import org.ptit.sound.classify.HMMModel;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class ObjectIODataBase implements DataBase {
    String type;
    List<String> modelFiles;
    String CURRENTFOLDER;
    String CODEBOOKFILENAME = "codebook";

    public ObjectIODataBase() {
    }

    public void setType(String type) {
        this.type = type;
        if (this.type.equalsIgnoreCase("hmm")) {
            CURRENTFOLDER = "models" + File.separator + "HMM";
        }
        if (this.type.equalsIgnoreCase("cbk")) {
            CURRENTFOLDER = "models" + File.separator + "codeBook";
        }
    }

    @Override
    public Model readModel(String name) throws Exception {
        Model model = null;
        if (type.equalsIgnoreCase("hmm")) {
            ObjectIO<HMMModel> oio = new ObjectIO<HMMModel>();
            model = new HMMModel();
            model = oio.readModel(CURRENTFOLDER + File.separator + name + "." + type);
            //            System.out.println("Type " + type);
            //            System.out.println("Read ::::: " + DBROOTFOLDER + "\\" + CURRENTFOLDER + "\\" + name + "." + type);
            // System.out.println(model);
        }
        if (type.equalsIgnoreCase("cbk")) {
            ObjectIO<CodeBookDictionary> oio = new ObjectIO<CodeBookDictionary>();
            model = new CodeBookDictionary();
            model = oio.readModel(CURRENTFOLDER + File.separator + CODEBOOKFILENAME + "." + type);
            //            System.out.println("Read ::::: " + DBROOTFOLDER + "\\" + CURRENTFOLDER + "\\" + CODEBOOKFILENAME + "." + type);
        }
        return model;
    }

    @Override
    public List<String> readRegistered() {

        modelFiles = readRegisteredWithExtension();
        //System.out.println("modelFiles length (Oiodb) :" + modelFiles.size());
        return removeExtension(modelFiles);
    }

    @Override
    public void saveModel(Model model, String name) throws Exception {

        if (type.equalsIgnoreCase("hmm")) {
            ObjectIO<HMMModel> oio = new ObjectIO<HMMModel>();
            oio.setModel((HMMModel) model);
            oio.saveModel(CURRENTFOLDER + File.separator + name + "." + type);
        }
        if (type.equalsIgnoreCase("cbk")) {
            ObjectIO<CodeBookDictionary> oio = new ObjectIO<CodeBookDictionary>();
            oio.setModel((CodeBookDictionary) model);
            oio.saveModel(CURRENTFOLDER + File.separator + CODEBOOKFILENAME + "." + type);
        }

    }

    private List<String> readRegisteredWithExtension() {
        File modelPath = new File(CURRENTFOLDER);

        modelFiles = Arrays.asList(modelPath.list());// must return only folders

        return modelFiles;
    }

    private String removeExtension(String fileName) {

        return fileName.substring(0, fileName.lastIndexOf('.'));

    }

    private List<String> removeExtension(List<String> modelFiles) {
        List<String> noExtension = new ArrayList<String>();
        for (String fileName : modelFiles) {
            noExtension.add(removeExtension(fileName));
        }

        return noExtension;
    }
}
