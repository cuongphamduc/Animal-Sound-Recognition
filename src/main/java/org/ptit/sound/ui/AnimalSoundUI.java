package org.ptit.sound.ui;

import org.ptit.sound.audio.JSoundCapture;
import org.ptit.sound.classify.resnet.ResNetAudioClassifier;
import org.ptit.sound.db.DataBase;
import org.ptit.sound.db.ObjectIODataBase;
import org.ptit.sound.db.TrainingTestingWaveFiles;
import org.ptit.sound.mediator.Operations;
import org.ptit.sound.util.ErrorManager;
import org.ptit.sound.util.ResourceUtils;
import org.ptit.sound.util.Utils;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.*;
import java.util.List;
import java.util.Objects;


public class AnimalSoundUI extends JFrame {

    private static final long serialVersionUID = 1L;
    private static ResNetAudioClassifier classifier;
    private JPanel jContentPane = null;
    private JSoundCapture soundCapture = null;
    private JTabbedPane jTabbedPane = null;
    private JPanel verifyPanel = null;
    private JPanel trainPanel = null;
    private JPanel runTrainingPanel = null;
    private JButton getAnimalButton = null;
    private JButton getAnimalButtonn = null;
    private JButton btnVerify = null;
    private JButton btnVerifyy = null;
    private JComboBox AnimalsComboBoxVerify = null;
    private JComboBox AnimalsComboBoxVerifyy = null;
    private JComboBox AnimalsComboBoxAddAnimal = null;
    private JButton updateAnimalForVerify;
    private JButton updateAnimalForVerifyy;
    private JButton getAnimalButton1 = null;
    private JButton getAnimalButtonn1 = null;
    private Operations opr = new Operations();
    private JLabel aboutLBL;
    private JLabel statusLBLRecognize;
    private JLabel statusLBLRecognizee;
    private JTextField addAnimalToCombo = null;
    private JButton addAnimalToComboBtn = null;
    private JLabel lblChooseAnAnimal;
    private JLabel lblAddANew;
    private JButton generateCodeBookBtn;
    private JButton btnNewButton_2;
    private JButton btnNewButton_3;
    private JButton evalButton;
    private JLabel evalResult;
    private JButton evalButtonn;
    private JLabel evalResultt;
    private JLabel imageAnimal;
    private JLabel imageAnimall;
    private JButton addRecord;
    private JButton addFile;

    public AnimalSoundUI() {
        super();
        initialize();
        ErrorManager.setMessageLbl(getStatusLblRecognize());
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            System.out.println(e.toString());
        }

        SwingUtilities.invokeLater(() -> {
            InputStream inputStream = ResourceUtils.getInputStream("pre_train/resnet-v2.pb");
            classifier = new ResNetAudioClassifier();
            try {
                classifier.load_model(inputStream);
            } catch (IOException e) {
                e.printStackTrace();
            }
            AnimalSoundUI test = new AnimalSoundUI();

            test.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            test.setResizable(false);
            test.setVisible(true);
        });
    }

    private void initialize() {
        this.setSize(900, 775);
        this.setContentPane(getJContentPane());
        this.setTitle("Animal Sound Recognition System");
    }

    private JTabbedPane getJTabbedPane() {
        if (jTabbedPane == null) {
            jTabbedPane = new JTabbedPane();

            jTabbedPane.setBounds(new Rectangle(0, 200, 900, 525));
            jTabbedPane.addTab("Verify Animal", null, getVerifyAnimalPanel(), null);
            jTabbedPane.addTab("Add Animal", null, getAddSamplePanel(), null);
            jTabbedPane.addTab("Train & Test", null, getRunTrainingPanel(), null);

            jTabbedPane.addChangeListener(new ChangeListener() {

                @Override
                public void stateChanged(ChangeEvent e) {
                    System.out.println("state changed");
                    if (jTabbedPane.getSelectedIndex() == 0) {
                        soundCapture.setSaveFileName(null);
                    } else if (jTabbedPane.getSelectedIndex() == 1) {
                        soundCapture.setSaveFileName("data" + File.separator + getAnimalsComboBoxAddAnimal().getSelectedItem() + File.separator + getAnimalsComboBoxAddAnimal().getSelectedItem());
                    }
                }
            });
        }
        return jTabbedPane;
    }

    private File getTestFile() {
        JFileChooser jfc = new JFileChooser("Select WAV file to verify");

        jfc.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
        jfc.setSize(new Dimension(541, 326));

        jfc.setFileFilter(new javax.swing.filechooser.FileFilter() {

            @Override
            public String getDescription() {
                return ".WAV & .WAVE Files";
            }

            @Override
            public boolean accept(File f) {
                return (f.getName().toLowerCase().endsWith("wav") || f.getName().toLowerCase().endsWith("wave") || f.isDirectory());
            }
        });
        int chooseOpt = jfc.showOpenDialog(this);
        if (chooseOpt == JFileChooser.APPROVE_OPTION) {
            File file = jfc.getSelectedFile();
            System.out.println("Selected file : " + file);
            return file;
        }
        return null;
    }

    private JPanel getVerifyAnimalPanel() {
        if (verifyPanel == null) {
            JLabel jLabel = new JLabel();
            jLabel.setBounds(new Rectangle(13, 55, 245, 20));
            jLabel.setText("Or select an animal from the list to verify");
            JLabel jLabel1 = new JLabel();
            jLabel1.setBounds(new Rectangle(13, 180, 450, 30));
            jLabel1.setHorizontalAlignment(SwingConstants.CENTER);
            jLabel1.setFont(new Font("Tahoma", Font.BOLD, 30));
            jLabel1.setText("Use HMM");
            verifyPanel = new JPanel();
            verifyPanel.setLayout(null);
            verifyPanel.add(getGetAnimalButton(), null);
            verifyPanel.add(getAnimalsComboBoxVerify(), null);
            verifyPanel.add(jLabel, null);
            verifyPanel.add(jLabel1, null);
            verifyPanel.add(getGetAnimalButton1(), null);
            verifyPanel.add(getBtnVerify());
            verifyPanel.add(getStatusLblRecognize());
            verifyPanel.add(updateAnimalForVerify(), null);
            verifyPanel.add(getImageAnimal(), null);

            JLabel jLabel2 = new JLabel();
            jLabel2.setBounds(new Rectangle(13, 305, 245, 20));
            jLabel2.setText("Or select an animal from the list to verify");
            JLabel jLabel3 = new JLabel();
            jLabel3.setBounds(new Rectangle(13, 430, 450, 30));
            jLabel3.setHorizontalAlignment(SwingConstants.CENTER);
            jLabel3.setFont(new Font("Tahoma", Font.BOLD, 30));
            jLabel3.setText("Use ResNet");
            verifyPanel.add(getGetAnimalButtonn(), null);
            verifyPanel.add(getAnimalsComboBoxVerifyy(), null);
            verifyPanel.add(jLabel2, null);
            verifyPanel.add(jLabel3, null);
            verifyPanel.add(getGetAnimalButtonn1(), null);
            verifyPanel.add(getBtnVerifyy());
            verifyPanel.add(getStatusLblRecognizee());
            verifyPanel.add(updateAnimalForVerifyy(), null);
            verifyPanel.add(getImageAnimall(), null);
        }
        return verifyPanel;
    }

    private JButton getBtnVerify() {
        if (btnVerify == null) {
            btnVerify = new JButton("Verify");
            btnVerify.addActionListener(e -> {
                if (soundCapture.isSoundDataAvailable() && getAnimalsComboBoxVerify().getItemCount() > 0) {

                    try {

                        String recAnimal = opr.hmmGetAnimalFromAmplitureArray(soundCapture.getAudioData());
                        String selectAnimal = getAnimalsComboBoxVerify().getSelectedItem().toString();

                        getImageAnimal().setIcon(new ImageIcon(new ImageIcon("image" + File.separator + selectAnimal + ".jpg").
                                getImage().getScaledInstance(200, 200, Image.SCALE_DEFAULT)));

                        if (recAnimal.equalsIgnoreCase(selectAnimal)) {
                            getStatusLblRecognize().setText("Selected animal is CORRECT");
                        } else {
                            getStatusLblRecognize().setText("Selected animal is INCORRECT");
                        }
                    } catch (Exception e2) {
                        e2.printStackTrace();
                    }
                }
            });
            btnVerify.setBounds(145, 111, 70, 24);
        }
        return btnVerify;
    }

    private JButton getBtnVerifyy() {
        if (btnVerifyy == null) {
            btnVerifyy = new JButton("Verify");
            btnVerifyy.addActionListener(e -> {
                if (soundCapture.isSoundDataAvailable() && getAnimalsComboBoxVerify().getItemCount() > 0) {

                    try {

                        int i = 0;
                        soundCapture.setSaveFileName("F:\\tmp\\tmp");
                        soundCapture.getFileNameAndSaveFile();
                        String name = "F:\\tmp\\tmp";
                        File f = new File(name);
                        if (!f.exists())
                            f.mkdirs();

                        f.delete();

                        f = new File(name + ".wav");

                        while (f.exists()) {
                            String temp = String.format(name + "_%d", i++);
                            f = new File(temp + ".wav");
                            continue;
                        }

                        if (i >= 2) {
                            String temp = String.format(name + "_%d", i - 2);
                            f = new File(temp + ".wav");
                        }
                        if (i == 1) {
                            String temp = String.format(name);
                            f = new File(temp + ".wav");
                        }

                        String selectAnimal = getAnimalsComboBoxVerifyy().getSelectedItem().toString();

//                        System.out.println(classifier.predict_audio_percent(f, getAnimalsComboBoxVerifyy().getSelectedItem().toString()));
//
//                        String recAnimal = classifier.predict_audio(f);
//
//
                        getImageAnimall().setIcon(new ImageIcon(new ImageIcon("image" + File.separator + selectAnimal + ".jpg").
                                getImage().getScaledInstance(200, 200, Image.SCALE_DEFAULT)));
//
//                        if (recAnimal.equalsIgnoreCase(selectAnimal)) {
//                            getStatusLblRecognizee().setText("Selected animal is CORRECT");
//                        } else {
//                            getStatusLblRecognizee().setText("Selected animal is INCORRECT");
                        Double res = Math.ceil(1.0 * classifier.predict_audio_percent(f, selectAnimal) * 100000) / 1000;
                        getStatusLblRecognizee().setText("Percent of just recorded is the " + selectAnimal + " : " + res + "%");
                    } catch (Exception e2) {
                        e2.printStackTrace();
                    }
                }
            });
            btnVerifyy.setBounds(145, 361, 70, 24);
        }
        return btnVerifyy;
    }

    private JPanel getAddSamplePanel() {
        if (trainPanel == null) {
            trainPanel = new JPanel();
            trainPanel.setLayout(null);
            trainPanel.add(getAnimalsComboBoxAddAnimal(), null);
            trainPanel.add(getAddAnimalToCombo(), null);
            trainPanel.add(getAddAnimalToComboBtn(), null);
            trainPanel.add(getAddFile(), null);
            trainPanel.add(getAddRecord(), null);
            trainPanel.add(getLblChooseAnAnimal());
            trainPanel.add(getLblAddANew());
        }
        return trainPanel;
    }

    private JPanel getRunTrainingPanel() {
        if (runTrainingPanel == null) {
            runTrainingPanel = new JPanel();
            runTrainingPanel.setLayout(null);
            runTrainingPanel.add(getGenerateCodeBookBtn());
            runTrainingPanel.add(getBtnNewButton_2());
            runTrainingPanel.add(getBtnNewButton_3());
            runTrainingPanel.add(getEvalButton());
            runTrainingPanel.add(getEvalResult());
            runTrainingPanel.add(getEvalButtonn());
            runTrainingPanel.add(getEvalResultt());
        }
        return runTrainingPanel;
    }

    private JButton getGetAnimalButton() {
        if (getAnimalButton == null) {
            getAnimalButton = new JButton("Recognize with just recorded");
            getAnimalButton.addActionListener(arg0 -> {
                if (soundCapture.isSoundDataAvailable() && getAnimalsComboBoxVerify().getItemCount() > 0) {

                    try {
                        String label = opr.hmmGetAnimalFromAmplitureArray(soundCapture.getAudioData());
                        getStatusLblRecognize().setText("Animal in record : " + label);
                        getImageAnimal().setIcon(new ImageIcon(new ImageIcon("image" + File.separator + label + ".jpg").
                                getImage().getScaledInstance(200, 200, Image.SCALE_DEFAULT)));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
            });
            getAnimalButton.setBounds(new Rectangle(13, 8, 202, 24));
        }
        return getAnimalButton;
    }

    private JButton getGetAnimalButtonn() {
        if (getAnimalButtonn == null) {
            getAnimalButtonn = new JButton("Recognize with just recorded");
            getAnimalButtonn.addActionListener(arg0 -> {
                if (soundCapture.isSoundDataAvailable() && getAnimalsComboBoxVerify().getItemCount() > 0) {

                    try {
                        soundCapture.setSaveFileName("F:\\tmp\\tmp");
                        soundCapture.getFileNameAndSaveFile();
                        String name = soundCapture.getSaveFileName();
                        File f = new File(name);
                        if (!f.exists())
                            f.mkdirs();

                        f.delete();

                        int i = 0;


                        f = new File(name + ".wav");
                        while (f.exists() == true) {
                            String temp = String.format(name + "_%d", i++);
                            f = new File(temp + ".wav");
                        }

//                        System.out.println("i == " + i);

                        if (i >= 2) {
                            String temp = String.format(name + "_%d", i - 2);
                            f = new File(temp + ".wav");
                        } else {
                            String temp = String.format(name);
                            f = new File(temp + ".wav");
                        }

                        System.out.println(f.getAbsolutePath());

                        String label = classifier.predict_audio(f);
                        getStatusLblRecognizee().setText("Animal in record : " + label);
                        getImageAnimall().setIcon(new ImageIcon(new ImageIcon("image" + File.separator + label + ".jpg").
                                getImage().getScaledInstance(200, 200, Image.SCALE_DEFAULT)));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
            });
            getAnimalButtonn.setBounds(new Rectangle(13, 258, 202, 24));
        }
        return getAnimalButtonn;
    }

    private JComboBox getAnimalsComboBoxVerify() {
        if (AnimalsComboBoxVerify == null) {
            DataBase db = new ObjectIODataBase();
            db.setType("hmm");
            AnimalsComboBoxVerify = new JComboBox();
            try {
                List<String> regs = db.readRegistered();
                for (String string : regs) {
                    AnimalsComboBoxVerify.addItem(string);
                }
            } catch (Exception e) {
            }
            AnimalsComboBoxVerify.setBounds(new Rectangle(13, 75, 202, 24));
        }
        return AnimalsComboBoxVerify;
    }

    private JComboBox getAnimalsComboBoxVerifyy() {
        if (AnimalsComboBoxVerifyy == null) {
            DataBase db = new ObjectIODataBase();
            db.setType("hmm");
            AnimalsComboBoxVerifyy = new JComboBox();
            try {
                List<String> regs = db.readRegistered();
                for (String string : regs) {
                    AnimalsComboBoxVerifyy.addItem(string);
                }
            } catch (Exception e) {
            }
            AnimalsComboBoxVerifyy.setBounds(new Rectangle(13, 325, 202, 24));
        }
        return AnimalsComboBoxVerifyy;
    }

    private JComboBox getAnimalsComboBoxAddAnimal() {
        if (AnimalsComboBoxAddAnimal == null) {
            TrainingTestingWaveFiles ttwf = new TrainingTestingWaveFiles("train");
            AnimalsComboBoxAddAnimal = new JComboBox();
            try {
                List<String> regs = ttwf.readAnimalWavFolder();
                for (int i = 0; i < regs.size(); i++) {
                    AnimalsComboBoxAddAnimal.addItem(regs.get(i));
                }
            } catch (Exception e) {
            }
            AnimalsComboBoxAddAnimal.setBounds(new Rectangle(11, 103, 202, 24));
            AnimalsComboBoxAddAnimal.addItemListener(new ItemListener() {

                @Override
                public void itemStateChanged(ItemEvent e) {
                    soundCapture.setSaveFileName("data" + File.separator + getAnimalsComboBoxAddAnimal().getSelectedItem() + File.separator + getAnimalsComboBoxAddAnimal().getSelectedItem());
                }
            });
        }
        return AnimalsComboBoxAddAnimal;
    }

    private JButton getGetAnimalButton1() {
        if (getAnimalButton1 == null) {
            getAnimalButton1 = new JButton("Recognize a saved WAV file");
            getAnimalButton1.addActionListener(e -> {
                File f = getTestFile();
                if (f != null) {

                    try {
                        String label = opr.hmmGetAnimalFromFile(f);
                        getStatusLblRecognize().setText("Animal in file : " + label);
                        getImageAnimal().setIcon(new ImageIcon(new ImageIcon("image" + File.separator + label + ".jpg").
                                getImage().getScaledInstance(200, 200, Image.SCALE_DEFAULT)));
                    } catch (Exception e2) {
                        e2.printStackTrace();
                    }

                }
            });
            getAnimalButton1.setBounds(new Rectangle(225, 8, 200, 24));
        }
        return getAnimalButton1;
    }

    private JButton getGetAnimalButtonn1() {
        if (getAnimalButtonn1 == null) {
            getAnimalButtonn1 = new JButton("Recognize a saved WAV file");
            getAnimalButtonn1.addActionListener(e -> {
                File f = getTestFile();
                if (f != null) {

                    try {
                        String label = classifier.predict_audio(f);
                        getStatusLblRecognizee().setText("Animal in file : " + label);
                        getImageAnimall().setIcon(new ImageIcon(new ImageIcon("image" + File.separator + label + ".jpg").
                                getImage().getScaledInstance(200, 200, Image.SCALE_DEFAULT)));
                    } catch (Exception e2) {
                        e2.printStackTrace();
                    }

                }
            });
            getAnimalButtonn1.setBounds(new Rectangle(225, 258, 200, 24));
        }
        return getAnimalButtonn1;
    }

    private JPanel getJContentPane() {
        if (jContentPane == null) {
            jContentPane = new JPanel();
            jContentPane.setLayout(null);
            jContentPane.add(getJTabbedPane());
            jContentPane.add(getSoundCapture());
            jContentPane.add(getAboutLBL());
        }
        return jContentPane;
    }

    private JSoundCapture getSoundCapture() {
        if (soundCapture == null) {
            soundCapture = new JSoundCapture(true, true);
            soundCapture.setBounds(10, 10, 860, 160);
        }
        return soundCapture;
    }

    private JLabel getAboutLBL() {
        if (aboutLBL == null) {
            aboutLBL = new JLabel("Contact our team if you have any technical questions ^_^");
            aboutLBL.setHorizontalAlignment(SwingConstants.CENTER);
            aboutLBL.setFont(new Font("Tahoma", Font.BOLD, 12));
            aboutLBL.setBounds(0, 725, 900, 20);
        }
        return aboutLBL;
    }

    private JLabel getStatusLblRecognize() {
        if (statusLBLRecognize == null) {
            statusLBLRecognize = new JLabel("");
            statusLBLRecognize.setHorizontalAlignment(SwingConstants.CENTER);
            statusLBLRecognize.setFont(new Font("Tahoma", Font.BOLD, 20));
            statusLBLRecognize.setBounds(400, 200, 500, 40);
        }
        return statusLBLRecognize;
    }

    private JLabel getStatusLblRecognizee() {
        if (statusLBLRecognizee == null) {
            statusLBLRecognizee = new JLabel("");
            statusLBLRecognizee.setHorizontalAlignment(SwingConstants.CENTER);
            statusLBLRecognizee.setFont(new Font("Tahoma", Font.BOLD, 20));
            statusLBLRecognizee.setBounds(400, 450, 500, 40);
        }
        return statusLBLRecognizee;
    }

    private JTextField getAddAnimalToCombo() {
        if (addAnimalToCombo == null) {
            addAnimalToCombo = new JTextField();
            addAnimalToCombo.setBounds(new Rectangle(10, 42, 202, 24));
        }
        return addAnimalToCombo;
    }

    private JButton updateAnimalForVerify() {
        if (updateAnimalForVerify == null) {
            updateAnimalForVerify = new JButton("Update Animal");
            updateAnimalForVerify.addActionListener(e -> {
                TrainingTestingWaveFiles ttwf = new TrainingTestingWaveFiles("train");
                List<String> regs = ttwf.readAnimalWavFolder();
                AnimalsComboBoxVerify.removeAllItems();
                for (int i = 0; i < regs.size(); i++) {
                    AnimalsComboBoxVerify.addItem(regs.get(i));
                }
            });
            updateAnimalForVerify.setBounds(new Rectangle(13, 111, 110, 24));
        }
        return updateAnimalForVerify;
    }

    private JButton updateAnimalForVerifyy() {
        if (updateAnimalForVerifyy == null) {
            updateAnimalForVerifyy = new JButton("Update Animal");
            updateAnimalForVerifyy.addActionListener(e -> {
                TrainingTestingWaveFiles ttwf = new TrainingTestingWaveFiles("train");
                List<String> regs = ttwf.readAnimalWavFolder();
                AnimalsComboBoxVerifyy.removeAllItems();
                for (int i = 0; i < regs.size(); i++) {
                    AnimalsComboBoxVerifyy.addItem(regs.get(i));
                }
            });
            updateAnimalForVerifyy.setBounds(new Rectangle(13, 361, 110, 24));
        }
        return updateAnimalForVerifyy;
    }

    private JButton getAddAnimalToComboBtn() {
        if (addAnimalToComboBtn == null) {
            addAnimalToComboBtn = new JButton("Add Animal");
            addAnimalToComboBtn.addActionListener(e -> {
                String newAnimal = Utils.clean(getAddAnimalToCombo().getText());
                boolean isAlreadyRegistered = false;
                if (!newAnimal.isEmpty()) {

                    for (int i = 0; i < getAnimalsComboBoxAddAnimal().getItemCount(); i++) {
                        if (getAnimalsComboBoxAddAnimal().getItemAt(i).toString().equalsIgnoreCase(newAnimal)) {
                            isAlreadyRegistered = true;
                            break;
                        }
                    }

                    if (!isAlreadyRegistered) {
                        getAnimalsComboBoxAddAnimal().addItem(getAddAnimalToCombo().getText());
                        getAnimalsComboBoxAddAnimal().repaint();
                        getAddAnimalToCombo().setText("");
                    }
                }
            });
            addAnimalToComboBtn.setBounds(new Rectangle(222, 42, 142, 24));
        }
        return addAnimalToComboBtn;
    }

    private JLabel getLblChooseAnAnimal() {
        if (lblChooseAnAnimal == null) {
            lblChooseAnAnimal = new JLabel("Choose an animal to record sound and save to correct folder");
            lblChooseAnAnimal.setBounds(11, 77, 350, 14);
        }
        return lblChooseAnAnimal;
    }

    private JLabel getLblAddANew() {
        if (lblAddANew == null) {
            lblAddANew = new JLabel("Add an new animal");
            lblAddANew.setBounds(11, 11, 126, 14);
        }
        return lblAddANew;
    }

    private JButton getGenerateCodeBookBtn() {
        if (generateCodeBookBtn == null) {
            generateCodeBookBtn = new JButton("Generate CodeBook");
            generateCodeBookBtn.addActionListener(e -> {
                try {
                    opr.generateCodebook();
                } catch (Exception e2) {
                    e2.printStackTrace();
                }
            });
            generateCodeBookBtn.setBounds(10, 20, 167, 23);
        }
        return generateCodeBookBtn;
    }

    private JButton getBtnNewButton_2() {
        if (btnNewButton_2 == null) {
            btnNewButton_2 = new JButton("Train HMM");
            btnNewButton_2.addActionListener(e -> {
                try {
                    opr.hmmTrain();
                } catch (Exception e2) {
                    e2.printStackTrace();

                }
                getAnimalsComboBoxVerify().repaint();
            });
            btnNewButton_2.setBounds(10, 60, 167, 23);
        }

        return btnNewButton_2;
    }

    private JButton getBtnNewButton_3() {
        if (btnNewButton_3 == null) {
            btnNewButton_3 = new JButton("Train ResNet");
            btnNewButton_3.setBounds(10, 100, 167, 23);
        }

        return btnNewButton_3;
    }

    private JButton getEvalButton() {
        if (evalButton == null) {
            evalButton = new JButton("Evaluate HMM Model");
            evalButton.addActionListener(e -> {
                int correct = 0;
                int total = 0;
                File folder = new File("test");
                for (final File fileEntry : folder.listFiles()) {
                    System.out.println(fileEntry.getPath());
                    total++;

                    String label = fileEntry.getName();
                    label = label.replaceAll("\\d", "").split("_")[0];

                    System.out.println("Real: " + label);

                    File tmp = new File(fileEntry.getPath());
                    try {
                        if (opr.hmmGetAnimalFromFile(tmp).equalsIgnoreCase(label)) {
                            correct++;
                        }
                    } catch (Exception e1) {
                        e1.printStackTrace();
                    }
                }
                try {
                    double ans = Math.ceil(1.0 * correct / total * 100000) / 1000;
                    String result = "Accuracy of HMM Model is : " + ans + "%";
                    getEvalResult().setText(result);
                } catch (Exception ee) {
                    ee.printStackTrace();

                }
                getAnimalsComboBoxVerify().repaint();
            });
            evalButton.setBounds(10, 140, 167, 23);
        }

        return evalButton;
    }

    private JButton getEvalButtonn() {
        if (evalButtonn == null) {
            evalButtonn = new JButton("Evaluate ResNet Model");
            evalButtonn.addActionListener(e -> {
                int correct = 0;
                int total = 0;
                File folder = new File("test");
                for (final File fileEntry : folder.listFiles()) {
                    System.out.println(fileEntry.getPath());

                    String label = fileEntry.getName();
                    label = label.replaceAll("\\d", "").split("_")[0];

                    System.out.println("Real: " + label);

                    total++;

                    File tmp = new File(fileEntry.getPath());
                    try {
                        if (classifier.predict_audio(tmp).equalsIgnoreCase(label)) {
                            correct++;
                        }
                    } catch (Exception e1) {
                        e1.printStackTrace();
                    }
                }
                try {
                    double ans = Math.ceil(1.0 * correct / total * 100000) / 1000;
                    String result = "Accuracy of ResNet Model is : " + ans + "%";
                    getEvalResultt().setText(result);
                } catch (Exception ee) {
                    ee.printStackTrace();

                }
                getAnimalsComboBoxVerifyy().repaint();
            });
            evalButtonn.setBounds(10, 180, 167, 23);
        }

        return evalButtonn;
    }

    private JLabel getEvalResult() {
        if (evalResult == null) {
            evalResult = new JLabel("");
            evalResult.setHorizontalAlignment(SwingConstants.CENTER);
            evalResult.setFont(new Font("Tahoma", Font.BOLD, 30));
            evalResult.setBounds(225, 25, 600, 100);
        }
        return evalResult;
    }

    private JLabel getEvalResultt() {
        if (evalResultt == null) {
            evalResultt = new JLabel("");
            evalResultt.setHorizontalAlignment(SwingConstants.CENTER);
            evalResultt.setFont(new Font("Tahoma", Font.BOLD, 30));
            evalResultt.setBounds(225, 75, 600, 100);
        }
        return evalResultt;
    }

    private JLabel getImageAnimal() {
        if (imageAnimal == null) {
            imageAnimal = new JLabel(new ImageIcon());
            imageAnimal.setBounds(450, 5, 400, 200);
        }
        return imageAnimal;
    }

    private JLabel getImageAnimall() {
        if (imageAnimall == null) {
            imageAnimall = new JLabel(new ImageIcon());
            imageAnimall.setBounds(450, 255, 400, 200);
        }
        return imageAnimall;
    }

    private JButton getAddRecord() {
        if (addRecord == null) {
            addRecord = new JButton("Add just recorded to correct folder");
            addRecord.addActionListener(arg0 -> {
                if (soundCapture.isSoundDataAvailable() && getAnimalsComboBoxVerify().getItemCount() > 0) {

                    try {
                        soundCapture.setSaveFileName("F:\\tmp\\tmp");
                        soundCapture.getFileNameAndSaveFile();
                        String name = soundCapture.getSaveFileName();

                        File f = new File(name);
                        int i = 0;
                        if (!f.exists())
                            f.mkdirs();

                        f.delete();

                        f = new File(name + ".wav");

                        while (f.exists()) {
                            String temp = String.format(name + "_%d", i++);
                            f = new File(temp + ".wav");
                        }

                        if (i > 1) {
                            String temp = String.format(name + "_%d", i - 2);
                            f = new File(temp + ".wav");
                        } else {
                            String temp = String.format(name);
                            f = new File(temp + ".wav");
                        }

                        File ff = new File("F:\\tmpp\\tmpp.wav");

                        if (!ff.exists()) {
                            ff.mkdirs();
                        }

                        Path FROM = Paths.get(f.getPath());
                        Path TO = Paths.get(ff.getPath());
                        CopyOption[] options = new CopyOption[]{
                                StandardCopyOption.REPLACE_EXISTING,
                                StandardCopyOption.COPY_ATTRIBUTES
                        };
                        Files.copy(FROM, TO, options);

                        String label = classifier.predict_audio(ff);
                        int count = Objects.requireNonNull(new File("data" + File.separator + label).list()).length;

                        FROM = Paths.get(f.getPath());
                        TO = Paths.get(String.format("data" + File.separator + label + File.separator + label + "_%d.wav", count));
                        Files.copy(FROM, TO, options);
                        JOptionPane.showMessageDialog(null, String.format("Success add just recorded to : %s", TO));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
            });
            addRecord.setBounds(new Rectangle(11, 143, 202, 24));
        }
        return addRecord;
    }

    private JButton getAddFile() {
        if (addFile == null) {
            addFile = new JButton("Add saved WAV to correct folder");
            addFile.addActionListener(e -> {
                File f = getTestFile();
                File ff = new File("F:\\tmpp\\tmpp.wav");

                if (ff.exists() == false) {
                    ff.mkdirs();
                }

                Path FROM = Paths.get(f.getPath());
                Path TO = Paths.get(ff.getPath());
                CopyOption[] options = new CopyOption[]{
                        StandardCopyOption.REPLACE_EXISTING,
                        StandardCopyOption.COPY_ATTRIBUTES
                };
                try {
                    Files.copy(FROM, TO, options);
                } catch (IOException e1) {
                    e1.printStackTrace();
                }

                String label = classifier.predict_audio(ff);
                int count = Objects.requireNonNull(new File("data" + File.separator + label).list()).length;

                TO = Paths.get(String.format("data" + File.separator + label + File.separator + label + "_%d.wav", count));
                FROM = Paths.get(f.getPath());

                try {
                    Files.copy(FROM, TO, options);
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
                JOptionPane.showMessageDialog(null, String.format("Success add saved WAV to : %s", TO));
            });
            addFile.setBounds(new Rectangle(11, 183, 202, 24));
        }
        return addFile;
    }
}
