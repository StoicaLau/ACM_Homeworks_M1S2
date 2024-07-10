package com.myapp.h2_nlpc.Controller;

import com.myapp.h2_nlpc.CoderAndDecoder.CoderAndDecoderTools;
import com.myapp.h2_nlpc.testcases.CoderUseCase;
import com.myapp.h2_nlpc.testcases.DecoderUseCase;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.VPos;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;
import javafx.scene.text.TextAlignment;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;


public class AppController implements Initializable {


    //Buttons
    @FXML
    private Button btnLoadOriginalImage;

    @FXML
    private Button btnEncode;

    @FXML
    private Button btnSaveQuantizedError;

    @FXML
    private Button btnRefresh;

    @FXML
    private Button btnComputeError;

    @FXML
    private Button btnDecode;

    @FXML
    private Button btnSaveDecodedImage;


    ///Choice boxes
    @FXML
    private ChoiceBox<String> cbPredictionSelection;

    @FXML
    private ChoiceBox<String> cbSaveMode;

    @FXML
    private ChoiceBox<String> cbError;

    @FXML
    private ChoiceBox<String> cbHistogramSource;


    //Images
    @FXML
    private ImageView iwOriginalImage;

    @FXML
    private ImageView iwErrorImage;

    @FXML
    private ImageView iwDecodedImage;


    //Text Filed
    @FXML
    private TextField tfK;

    @FXML
    private TextField tfContrast;

    @FXML
    private TextField tfRescaling;

    //Labels
    @FXML
    private Label lblHistogramTitle;

    @FXML
    private Label lblMinError;
    @FXML
    private Label lblMaxError;

    //Others
    @FXML
    private Canvas cHistogram;


    //Parameters

    /**
     * coder test cases
     */
    private CoderUseCase coderUseCase;

    /**
     * Decoder test cases
     */
    private DecoderUseCase decoderUseCase;

    /**
     * if the original image exist
     */
    private boolean isOriginalImage;

    /**
     * if the decoded image exist
     */
    private boolean isDecodedImage;


    /**
     * Used for preset some components
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        this.isDecodedImage = false;
        this.isOriginalImage = false;
        this.cbHistogramSource.valueProperty().addListener((observable, oldValue, newValue) -> this.refreshHistogram());
        tfRescaling.setOnAction(event -> this.refreshHistogram());

    }


    /**
     * refresh histogram title
     * refresh the histogram
     */
    private void refreshHistogram() {
        String histogramSource = this.cbHistogramSource.getValue();
        if (histogramSource == null) {
            String title = "No Data";
            String message = "There is no data loaded";
            this.showDialog(title, message);

        } else {
            double scale;
            String scaleAsString = this.tfRescaling.getText();
            if (!scaleAsString.equals("")) {
                try {
                    scale = Double.parseDouble(scaleAsString);
                } catch (NumberFormatException e) {
                    String title = "Wrong scale parameter";
                    String message = "The scale parameter is not a number!";
                    this.showDialog(title, message);
                    return;
                }
                boolean coderOptions = false;
                for (int i = 0; i < CoderAndDecoderTools.CODER_OPTIONS.length; i++) {
                    if (CoderAndDecoderTools.CODER_OPTIONS[i].equals(histogramSource)) {
                        coderOptions = true;
                        break;
                    }
                }

                String imageName, predictorType;
                int k;
                int[] histogramData;

                if (coderOptions) {
                    histogramData = this.coderUseCase.getHistogram(histogramSource);
                    imageName = this.coderUseCase.getFileName();
                    predictorType = this.coderUseCase.getPredictorType();
                    k = this.coderUseCase.getK();
                } else {
                    histogramData = this.decoderUseCase.getHistogram(histogramSource);
                    imageName = this.decoderUseCase.getFileName();
                    predictorType = this.decoderUseCase.getPredictorType();
                    k = this.decoderUseCase.getK();
                }


                String title;
                if (histogramSource.equals("Original image"))
                    title = "Histogram of " + imageName;
                else {
                    title = "Histogram of " + histogramSource + " using " + predictorType + " predictor with k="
                            + k + ".\n The current image being used is " + imageName;
                }
                this.lblHistogramTitle.setText(title);


                this.drawHistogram(histogramData, scale);

            } else {
                String title = "Wrong scale parameter";
                String message = "The scale parameter is not a number!";
                this.showDialog(title, message);
            }
        }


    }

    /**
     * Draw a histogram
     *
     * @param histogramData an frequencies array of image pixels
     */
    private void drawHistogram(int[] histogramData, double scale) {

        double height = cHistogram.getHeight();//450
        double width = cHistogram.getWidth();//530


        GraphicsContext histogram = cHistogram.getGraphicsContext2D();
        histogram.clearRect(0, 0, width, height);

        double barWidth = 1;
        double gap = 0;
        double startX = 40;
        double startY = height - 40;

        double maxCount = 5000;
//

        histogram.setFill(Color.BLACK);
        histogram.setStroke(Color.BLACK);
        histogram.setTextBaseline(VPos.CENTER);
        histogram.setTextAlign(TextAlignment.LEFT);
        histogram.setLineWidth(1);

        //Label Y
        histogram.strokeLine(40, startY, 40, 0);
        for (int i = 0; i <= maxCount; i += maxCount / 20) {
            double y = startY - ((double) i / maxCount * (startY));
            histogram.strokeLine(33, y, 39, y);
            histogram.fillText(String.valueOf(i), 5, y + 5);
        }


        //Label X
        histogram.setTextAlign(TextAlignment.CENTER);
        histogram.setTextBaseline(VPos.TOP);
        histogram.strokeLine(40, startY, width, startY);
        int[] labelX = new int[]{-255, -200, -100, 0, 100, 200, 255};
        for (int value : labelX) {
            double x = startX + (255 + value) * (barWidth + gap);
            histogram.strokeLine(x, startY, x, startY + 10);
            histogram.fillText(String.valueOf(value), x, startY + 12);
        }

        //bars/lines
        histogram.setFill(Color.BLUE);
        for (int count : histogramData) {
            if (count != 0) {
                double barHeight = (double) count / maxCount * scale * (height - 10);
                histogram.fillRect(startX, startY - barHeight - 1, barWidth, barHeight);
            }
            startX += barWidth + gap;
        }
//        System.out.println("done");

    }

    /***
     * Open a dialog for choosing a bmp image
     * Load the image in the interface
     *
     * @throws IOException an exception
     */
    @FXML
    protected void onBtnLoadOriginalImageClick() throws IOException {

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select an bmp image");

        File defaultDirectory = new File("C:\\Users\\stoic\\Desktop\\master\\Sem2\\ACM\\Laburi\\ACM_Homeworks_M1S2\\H2_NLPC\\lab_NL_PRED");
        fileChooser.setInitialDirectory(defaultDirectory);

        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("BMP Image (*.bmp)", "*.bmp");
        fileChooser.getExtensionFilters().add(extFilter);
        Stage stage = (Stage) this.btnLoadOriginalImage.getScene().getWindow();
        File selectedFile = fileChooser.showOpenDialog(stage);

        if (selectedFile != null) {
            Image image = new Image(selectedFile.toURI().toString());
            this.iwOriginalImage.setImage(image);
            this.coderUseCase = new CoderUseCase(selectedFile);
            this.changeInterfaceAfterLoadOriginalImageEvent();

            this.isOriginalImage = true;
            if (this.isDecodedImage) {
                this.btnComputeError.setDisable(false);
            }
        }

    }

    /***
     * Start de encoder
     *
     */
    @FXML
    protected void onBtnEncodeClick() {
        String kString = this.tfK.getText();
        if (!kString.equals("")) {
            int k;
            try {
                k = Integer.parseInt(kString);
            } catch (NumberFormatException e) {
                String title = "Wrong near-lossless parameter";
                String message = "The near-lossless parameter is not a number!";
                this.showDialog(title, message);
                return;
            }


            if (k >= 0 && k <= 10) {
                String predictorType = this.cbPredictionSelection.getValue();
                this.coderUseCase.startEncoding(predictorType, k);
                this.changeInterfaceAfterEncodeEvent();
            } else {
                String title = "Wrong near-lossless parameter";
                String message = "The near-lossless parameter is not between 0 and 10!";
                this.showDialog(title, message);
            }

        } else {
            String title = "Wrong near-lossless parameter";
            String message = "The near-lossless parameter is null!";
            this.showDialog(title, message);
        }


    }

    /**
     * The btnSaveQuantizedError Event
     * open a dialog an save the quantized error
     */
    @FXML
    protected void onBtnSaveQuantizedErrorClick() throws IOException {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("Select an Directory");

        File defaultDirectory = new File("C:\\Users\\stoic\\Desktop\\master\\Sem2\\ACM\\Laburi\\ACM_Homeworks_M1S2\\H2_NLPC");
        directoryChooser.setInitialDirectory(defaultDirectory);

        Stage stage = (Stage) this.btnLoadOriginalImage.getScene().getWindow();

        File selectedDirectory = directoryChooser.showDialog(stage);

        if (selectedDirectory != null) {
            String saveMode = this.cbSaveMode.getValue();
            this.coderUseCase.saveQuantizedErrorAsNLC(selectedDirectory, saveMode);

        }
    }

    /**
     * The btnLoadQuantizedError Event
     * open a dialog and load the nlc file
     */
    @FXML
    protected void onBtnLoadQuantizedErrorClick() throws IOException {
        this.isDecodedImage = false;
        this.btnComputeError.setDisable(true);
        this.lblMaxError.setText("Max=");
        this.lblMinError.setText("Min=");
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select an bmp image");

        File defaultDirectory = new File("C:\\Users\\stoic\\Desktop\\master\\Sem2\\ACM\\Laburi\\ACM_Homeworks_M1S2\\H2_NLPC\\nlcFile");
        fileChooser.setInitialDirectory(defaultDirectory);

        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("NLC Files (*.nlc)", "*.nlc");
        fileChooser.getExtensionFilters().add(extFilter);
        Stage stage = (Stage) this.btnLoadOriginalImage.getScene().getWindow();
        File selectedFile = fileChooser.showOpenDialog(stage);

        if (selectedFile != null) {
            this.decoderUseCase = new DecoderUseCase(selectedFile);
            this.changeInterfaceAfterLoadQuantizedErrorEvent();
        }

    }

    /**
     * The btnDecode Event
     * open a dialog and load the nlc file
     */
    @FXML
    protected void onBtnDecodeClick() throws IOException {
        WritableImage writableImage = this.decoderUseCase.startDecoding();
        this.iwDecodedImage.setImage(writableImage);

        this.isDecodedImage = true;
        if (this.isOriginalImage) {
            this.btnComputeError.setDisable(false);
        }
        this.changeInterfaceAfterDecodeEvent();


    }

    /**
     * the btnSaveDecodedImage Event
     * open a dialog to choose the directory for saving decoded image
     */
    @FXML
    protected void onBtnSaveDecodedImage() throws IOException {

        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("Select an Directory");

        File defaultDirectory = new File("C:\\Users\\stoic\\Desktop\\master\\Sem2\\ACM\\Laburi\\ACM_Homeworks_M1S2\\H2_NLPC\\decodedImages");
        directoryChooser.setInitialDirectory(defaultDirectory);

        Stage stage = (Stage) this.btnLoadOriginalImage.getScene().getWindow();

        File selectedDirectory = directoryChooser.showDialog(stage);

        if (selectedDirectory != null) {
            this.decoderUseCase.saveDecodedFile(selectedDirectory);

        }
    }


    /***
     * refresh error image
     *
     */
    @FXML
    protected void onBtnRefreshClick() {
        String contrastString = this.tfContrast.getText();
        double contrast;
        if (!contrastString.equals("")) {
            try {
                contrast = Double.parseDouble(contrastString);
            } catch (NumberFormatException e) {
                String title = "Wrong contrast parameter";
                String message = "The contrast parameter is not a number!";
                this.showDialog(title, message);
                return;
            }

            String imageType = this.cbError.getValue();
            WritableImage writableImage = this.coderUseCase.createErrorImage(imageType, contrast);
            this.iwErrorImage.setImage(writableImage);


        } else {
            String title = "Wrong contrast parameter";
            String message = "The contrast parameter is null!";
            this.showDialog(title, message);
        }

    }

    /**
     * The btnComputeError event
     * display in and max value of Origin-Decoder
     */
    @FXML
    protected void onBtnComputeErrorClick() {
        int[][] decodedValues = this.decoderUseCase.getDecodedValues();
        int[] minMaxValue = this.coderUseCase.computeError(decodedValues);

        String message = "Min= " + minMaxValue[0];
        this.lblMinError.setText(message);

        message = "Max= " + minMaxValue[1];
        this.lblMaxError.setText(message);

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
     * change the interface after load original image Event
     */
    private void changeInterfaceAfterLoadOriginalImageEvent() {
        //Disable
        this.setDisableEncodeGroup();
        this.setDisableCoderSaveGroup(true);

        this.cbError.getItems().clear();
        this.cbSaveMode.getItems().clear();

        this.cbPredictionSelection.getItems().clear();
        this.cbPredictionSelection.getItems().addAll("128", "A", "B", "C", "A+B-C", "A+(B-C)/2", "(A+B-C)/2", "B+(A-C)/2", "(A+B)/2", "JPEGLS");
        this.cbPredictionSelection.setValue("128");


        ObservableList<String> items = this.cbHistogramSource.getItems();
        if (!items.contains("Original image")) {
            items.add("Original image");
            this.cbHistogramSource.setValue("Original image");
        } else {
            this.cbHistogramSource.setValue("Original image");
            cbHistogramSource.getItems().remove("Predictor error image from coder");

            cbHistogramSource.getItems().remove("Q predictor error image from coder");

            cbHistogramSource.getItems().remove("Decoded image from coder");

        }

        this.refreshHistogram();
    }

    /**
     * change the interface after encode image Event
     */
    private void changeInterfaceAfterEncodeEvent() {
        //Enable
        this.setDisableCoderSaveGroup(false);

        this.cbHistogramSource.setValue("Original image");
        if (!cbHistogramSource.getItems().contains("Predictor error image from coder")) {
            cbHistogramSource.getItems().add("Predictor error image from coder");
        }

        if (!cbHistogramSource.getItems().contains("Q predictor error image from coder")) {
            cbHistogramSource.getItems().add("Q predictor error image from coder");
        }

        if (!cbHistogramSource.getItems().contains("Decoded image from coder")) {
            cbHistogramSource.getItems().add("Decoded image from coder");
        }

        this.cbError.getItems().clear();
        this.cbError.getItems().addAll("prediction error", "Q prediction error");
        this.cbError.setValue("prediction error");

        this.cbSaveMode.getItems().clear();
        this.cbSaveMode.getItems().addAll("Fixed 9b", "Table", "Arithmetic");
        this.cbSaveMode.setValue("Fixed 9b");


        this.refreshHistogram();

    }

    /**
     * Change the interface after load quantized error event
     */
    private void changeInterfaceAfterLoadQuantizedErrorEvent() {
        this.btnDecode.setDisable(false);
        this.btnSaveDecodedImage.setDisable(true);

        cbHistogramSource.getItems().remove("Q predictor error image from decoder");
        cbHistogramSource.getItems().remove("DQ predictor error image from decoder");

        cbHistogramSource.getItems().remove("Decoded image from decoder");


        ObservableList<String> items = this.cbHistogramSource.getItems();
        if (!items.contains("Original image")) {
            this.cbHistogramSource.setDisable(true);
        }
        this.iwDecodedImage.setImage(null);

    }

    /**
     * Change the interface after decode Event
     */
    private void changeInterfaceAfterDecodeEvent() {
        this.btnSaveDecodedImage.setDisable(false);
        this.cbHistogramSource.setDisable(false);

        if (!cbHistogramSource.getItems().contains("Q predictor error image from decoder")) {
            cbHistogramSource.getItems().add("Q predictor error image from decoder");
        }
        if (!cbHistogramSource.getItems().contains("DQ predictor error image from decoder")) {
            cbHistogramSource.getItems().add("DQ predictor error image from decoder");
        }

        if (!cbHistogramSource.getItems().contains("Decoded image from decoder")) {
            cbHistogramSource.getItems().add("Decoded image from decoder");
        }


        this.cbHistogramSource.setValue("Q predictor error image from decoder");

    }

    /**
     * set disable parameter of each item from encode group
     */
    private void setDisableEncodeGroup() {
        this.cbPredictionSelection.setDisable(false);
        this.tfK.setDisable(false);
        this.btnEncode.setDisable(false);
        this.cbHistogramSource.setDisable(false);
    }


    /**
     * set disable parameter of each item from encode group
     *
     * @param state the state that will be set
     */
    private void setDisableCoderSaveGroup(boolean state) {
        this.cbSaveMode.setDisable(state);
        this.cbError.setDisable(state);

        this.btnSaveQuantizedError.setDisable(state);
        this.btnRefresh.setDisable(state);

        this.tfContrast.setDisable(state);

        this.lblMaxError.setText("Max=");
        this.lblMinError.setText("Min=");

        this.iwErrorImage.setImage(null);
    }

}