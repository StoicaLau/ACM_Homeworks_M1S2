package com.h4_wavelet;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.javatuples.Pair;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.LinkedList;
import java.util.List;
import java.util.ResourceBundle;

public class AppController implements Initializable {

    /**
     * previous levels for horizontal
     */
    List<Integer> previousLevelsH;
    /**
     * previous Levels for vertical
     */
    List<Integer> previousLevelsV;
    //Labels
    @FXML
    private Label lblMin;
    @FXML
    private Label lblMax;
    //Images
    @FXML
    private ImageView iwOriginalImage;
    @FXML
    private ImageView iwWaveletImage;
    //Text Fields
    @FXML
    private TextField tfScale;
    @FXML
    private TextField tfOffset;
    @FXML
    private TextField tfX;
    @FXML
    private TextField tfY;
    //Buttons
    @FXML
    private Button btnLoadOriginalImage;
    @FXML
    private Button btnSaveWaveletImage;
    @FXML
    private Button btnRefresh;
    @FXML
    private Button btnComputeError;
    //Analysis Buttons
    @FXML
    private Button btnAnH1;
    @FXML
    private Button btnAnV1;
    @FXML
    private Button btnAnH2;
    @FXML
    private Button btnAnV2;
    @FXML
    private Button btnAnH3;
    @FXML
    private Button btnAnV3;
    @FXML
    private Button btnAnH4;
    @FXML
    private Button btnAnV4;
    @FXML
    private Button btnAnH5;
    @FXML
    private Button btnAnV5;
    //Synthesis Buttons
    @FXML
    private Button btnSyH1;
    @FXML
    private Button btnSyV1;
    @FXML
    private Button btnSyH2;
    @FXML
    private Button btnSyV2;
    @FXML
    private Button btnSyH3;
    @FXML
    private Button btnSyV3;
    @FXML
    private Button btnSyH4;
    @FXML
    private Button btnSyV4;
    @FXML
    private Button btnSyH5;
    @FXML
    private Button btnSyV5;
    /**
     * current level on horizontal
     */
    private int currentLevelH;
    /**
     * current level on vertical
     */
    private int currentLevelV;
    /**
     * backend of the app
     */
    private AppBackend backend;

    /**
     * Used for preset some components
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        this.setDisableAnalysisAndSynthesisButtons(true);
        this.restartLevels();


        this.btnAnH1.setOnAction(event -> {
            try {
                onBtnAnClick(1, "horizontal");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });

        this.btnAnV1.setOnAction(event -> {
            try {
                onBtnAnClick(1, "vertical");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });

        this.btnAnH2.setOnAction(event -> {
            try {
                onBtnAnClick(2, "horizontal");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });

        this.btnAnV2.setOnAction(event -> {
            try {
                onBtnAnClick(2, "vertical");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });

        this.btnAnH3.setOnAction(event -> {
            try {
                onBtnAnClick(3, "horizontal");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });

        this.btnAnV3.setOnAction(event -> {
            try {
                onBtnAnClick(3, "vertical");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });

        this.btnAnH4.setOnAction(event -> {
            try {
                onBtnAnClick(4, "horizontal");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });

        this.btnAnV4.setOnAction(event -> {
            try {
                onBtnAnClick(4, "vertical");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });

        this.btnAnH5.setOnAction(event -> {
            try {
                onBtnAnClick(5, "horizontal");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });

        this.btnAnV5.setOnAction(event -> {
            try {
                onBtnAnClick(5, "vertical");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });

        this.btnSyH1.setOnAction(event -> {
            try {
                onBtnSyClick(1, "horizontal");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });

        this.btnSyV1.setOnAction(event -> {
            try {
                onBtnSyClick(1, "vertical");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });

        this.btnSyH2.setOnAction(event -> {
            try {
                onBtnSyClick(2, "horizontal");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });

        this.btnSyV2.setOnAction(event -> {
            try {
                onBtnSyClick(2, "vertical");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
        this.btnSyH3.setOnAction(event -> {
            try {
                onBtnSyClick(3, "horizontal");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });

        this.btnSyV3.setOnAction(event -> {
            try {
                onBtnSyClick(3, "vertical");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });

        this.btnSyH4.setOnAction(event -> {
            try {
                onBtnSyClick(4, "horizontal");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });

        this.btnSyV4.setOnAction(event -> {
            try {
                onBtnSyClick(4, "vertical");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });

        this.btnSyH5.setOnAction(event -> {
            try {
                onBtnSyClick(5, "horizontal");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });

        this.btnSyV5.setOnAction(event -> {
            try {
                onBtnSyClick(5, "vertical");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

    /**
     * on btn load original image click event
     *
     * @throws IOException an exception
     */
    @FXML
    protected void onBtnLoadOriginalImageClick() throws IOException {
        this.restartLevels();
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select an bmp image");

        File defaultDirectory = new File("C:\\Users\\stoic\\Desktop\\master\\Sem2\\ACM\\Laburi\\ACM_Homeworks_M1S2\\H4_Wavelet\\Images512");
        fileChooser.setInitialDirectory(defaultDirectory);
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("BMP (*.bmp)", "*.bmp");
        fileChooser.getExtensionFilters().add(extFilter);

        Stage stage = (Stage) this.btnLoadOriginalImage.getScene().getWindow();
        File selectedFile = fileChooser.showOpenDialog(stage);

        if (selectedFile != null) {
            this.backend = new AppBackend(selectedFile);

            WritableImage writableImage = this.backend.getOriginalImage();
            this.iwOriginalImage.setImage(writableImage);

            writableImage = this.backend.getWaveletLvlImage(currentLevelH, currentLevelH, 1, 0);
            this.iwWaveletImage.setImage(writableImage);

            this.btnRefresh.setDisable(false);
            this.btnComputeError.setDisable(false);
            this.setDisableAnalysisAndSynthesisButtons(false);
            this.btnSaveWaveletImage.setDisable(false);
        }

    }

    /**
     * on btn load wavelet image click event
     */
    @FXML
    protected void onBtnLoadWaveletImageClick() {
        this.restartLevels();
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select an bmp image");

        File defaultDirectory = new File("C:\\Users\\stoic\\Desktop\\master\\Sem2\\ACM\\Laburi\\ACM_Homeworks_M1S2\\H4_Wavelet\\Output");
        fileChooser.setInitialDirectory(defaultDirectory);
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("WVL (*.wvl)", "*.wvl");
        fileChooser.getExtensionFilters().add(extFilter);

        Stage stage = (Stage) this.btnLoadOriginalImage.getScene().getWindow();
        File selectedFile = fileChooser.showOpenDialog(stage);

        if (selectedFile != null) {
            if (this.backend == null)
                this.backend = new AppBackend();

            this.backend.loadWaveletImageValues(selectedFile);

            String fileName = selectedFile.getName();
            int hIndex = fileName.lastIndexOf("H");
            int vIndex = fileName.lastIndexOf("V");
            this.currentLevelH = Integer.parseInt(String.valueOf(fileName.charAt(hIndex + 1)));
            this.currentLevelV = Integer.parseInt(String.valueOf(fileName.charAt(vIndex + 1)));

            this.drawWaveletImage();

            this.btnRefresh.setDisable(false);
            this.setDisableAnalysisAndSynthesisButtons(false);
            this.btnSaveWaveletImage.setDisable(false);
        }

    }

    /**
     * on btn save wavelet image click event
     */
    @FXML
    protected void onBtnSaveWaveletImageClick() {


        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("Select an Directory");

        File defaultDirectory = new File("C:\\Users\\stoic\\Desktop\\master\\Sem2\\ACM\\Laburi\\ACM_Homeworks_M1S2\\H4_Wavelet");
        directoryChooser.setInitialDirectory(defaultDirectory);

        Stage stage = (Stage) this.btnLoadOriginalImage.getScene().getWindow();

        File selectedDirectory = directoryChooser.showDialog(stage);

        if (selectedDirectory != null) {
            String additionalName = "H" + this.currentLevelH + "V" + this.currentLevelV;
            this.backend.saveWaveletImageValues(selectedDirectory, additionalName);
        }
    }


    /**
     * on btn Analysis  click event
     *
     * @throws IOException an exception
     */
    @FXML
    protected void onBtnAnClick(int level, String type) throws IOException {
        if (type.equals("horizontal")) {
            this.currentLevelH = level;
            this.previousLevelsH.add(level);
            this.backend.analyzeHorizontal(this.currentLevelH);
        } else if (type.equals("vertical")) {
            this.currentLevelV = level;
            this.previousLevelsV.add(level);
            this.backend.analyzeVertical(this.currentLevelV);
        }
        this.drawWaveletImage();

    }

    /**
     * on btn Synthesis  click event
     *
     * @throws IOException an exception
     */
    @FXML
    protected void onBtnSyClick(int level, String type) throws IOException {
        if (type.equals("horizontal")) {
            int length = this.previousLevelsH.size();
            if (length != 1) {
                this.previousLevelsH.remove(length - 1);
                length = this.previousLevelsH.size();
            }

            this.currentLevelH = this.previousLevelsH.get(length - 1);
            this.backend.synthesizeHorizontal(level-1);
        } else if (type.equals("vertical")) {
            int length = this.previousLevelsV.size();
            if (length != 1) {
                this.previousLevelsV.remove(length - 1);
                length = this.previousLevelsV.size();
            }
            this.currentLevelV = this.previousLevelsV.get(length - 1);
            this.backend.synthesizeVertical(level-1);
        }

        this.drawWaveletImage();

    }

    /**
     * on btn refresh click event
     */
    @FXML
    protected void onBtnRefreshClick() {
        this.drawWaveletImage();

    }

    /**
     * on btn click error event
     */
    @FXML
    protected void onBtnComputeErrorClick() {
        DecimalFormat format = new DecimalFormat("#.###");

        Pair<Double, Double> result = this.backend.computeError();
        this.lblMin.setText("Min= " + format.format(result.getValue0()));
        this.lblMax.setText("Max= " + format.format(result.getValue1()));
    }


    /**
     * draw Wavelet Image
     */
    private void drawWaveletImage() {
        double scale;
        double offset;
        String scaleString = this.tfScale.getText();
        if (!scaleString.equals("")) {
            try {
                scale = Double.parseDouble(scaleString);
            } catch (NumberFormatException e) {
                String title = "Wrong scale parameter";
                String message = "The scale parameter is not a number";
                this.showDialog(title, message);
                return;
            }
            String offsetString = this.tfOffset.getText();
            if (!offsetString.equals("")) {
                try {
                    offset = Double.parseDouble(offsetString);
                } catch (NumberFormatException e) {
                    String title = "Wrong offset parameter";
                    String message = "The offset parameter is not a number";
                    this.showDialog(title, message);
                    return;
                }

                int lengthH = (int) Math.pow(2, 10 - this.currentLevelH - 1);
                int lengthV = (int) Math.pow(2, 10 - this.currentLevelV - 1);

                tfX.setText(String.valueOf(lengthH));
                tfY.setText(String.valueOf(lengthV));


                WritableImage writableImage = this.backend.getWaveletLvlImage(lengthH, lengthV, scale, offset);
                this.iwWaveletImage.setImage(writableImage);
            } else {
                String title = "Wrong offset parameter";
                String message = "The offset parameter is null!";
                this.showDialog(title, message);
            }
        } else {
            String title = "Wrong scale parameter";
            String message = "The scale parameter is null!";
            this.showDialog(title, message);
        }

    }

    /**
     * Show a dialog with a specific title and specific message
     *
     * @param title   the title of the dialog
     * @param message the message of the dialog
     */
    private void showDialog(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    /**
     * set disable analysis and synthesis buttons
     *
     * @param disable the state of disable
     */
    private void setDisableAnalysisAndSynthesisButtons(boolean disable) {
        this.btnAnH1.setDisable(disable);
        this.btnAnH2.setDisable(disable);
        this.btnAnH3.setDisable(disable);
        this.btnAnH4.setDisable(disable);
        this.btnAnH5.setDisable(disable);

        this.btnAnV1.setDisable(disable);
        this.btnAnV2.setDisable(disable);
        this.btnAnV3.setDisable(disable);
        this.btnAnV4.setDisable(disable);
        this.btnAnV5.setDisable(disable);

        this.btnSyH1.setDisable(disable);
        this.btnSyH2.setDisable(disable);
        this.btnSyH3.setDisable(disable);
        this.btnSyH4.setDisable(disable);
        this.btnSyH5.setDisable(disable);

        this.btnSyV1.setDisable(disable);
        this.btnSyV2.setDisable(disable);
        this.btnSyV3.setDisable(disable);
        this.btnSyV4.setDisable(disable);
        this.btnSyV5.setDisable(disable);
    }

    /**
     * restart the levels
     */
    private void restartLevels() {
        this.previousLevelsH = new LinkedList<>();
        this.previousLevelsV = new LinkedList<>();
        this.currentLevelH = 0;
        this.currentLevelV = 0;

        this.previousLevelsH.add(this.currentLevelH);
        this.previousLevelsV.add(this.currentLevelV);
    }
}