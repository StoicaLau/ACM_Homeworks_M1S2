package com.myapp.h2_nlpc.Controller;

import com.myapp.h2_nlpc.CoderAndDecoder.CoderAndDecoderTools;
import com.myapp.h2_nlpc.testcases.CoderTestCase;
import com.myapp.h2_nlpc.testcases.DecoderTestCase;
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

//TODO display a clean interface with clean animation
//TODO (optional) work more at the histogram to look better
//TODO 3 display the error image
//TODO Compute Error ,Compute error la ce?
//TODO Save 9b bafta :(
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
    private CoderTestCase coderTestCase;

    /**
     * Decoder test cases
     */
    private DecoderTestCase decoderTestCase;


    /**
     * Used for preset some components
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        this.coderTestCase = new CoderTestCase();
        this.decoderTestCase = new DecoderTestCase();
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
                scale = Double.parseDouble(scaleAsString);
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
                    histogramData = this.coderTestCase.getHistogram(histogramSource);
                    imageName = this.coderTestCase.getFileName();
                    predictorType = this.coderTestCase.getPredictorType();
                    k = this.coderTestCase.getK();
                } else {
                    histogramData = this.decoderTestCase.getHistogram(histogramSource);
                    imageName = this.decoderTestCase.getFileName();
                    predictorType = this.decoderTestCase.getPredictorType();
                    k = this.decoderTestCase.getK();
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
                String message = "The scale parameter is null!";
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

        double maxCount = 1000 * scale;
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
                double barHeight = (double) count / maxCount / scale * (height - 10);
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

        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("Imagini BMP (*.bmp)", "*.bmp");
        fileChooser.getExtensionFilters().add(extFilter);
        Stage stage = (Stage) this.btnLoadOriginalImage.getScene().getWindow();
        File selectedFile = fileChooser.showOpenDialog(stage);

        if (selectedFile != null) {
            Image image = new Image(selectedFile.toURI().toString());
            this.iwOriginalImage.setImage(image);
            this.coderTestCase.initCoder(selectedFile);
            this.changeInterfaceAfterLoadOriginalImageEvent();
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
            int k = Integer.parseInt(kString);
            if (k >= 0 && k <= 10) {
                String predictorType = this.cbPredictionSelection.getValue();
                this.coderTestCase.startEncoding(predictorType, k);
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

        File defaultDirectory = new File("C:\\Users\\stoic\\Desktop\\master\\Sem2\\ACM\\Laburi\\ACM_Homework\\H2_NLPC");
        directoryChooser.setInitialDirectory(defaultDirectory);

        Stage stage = (Stage) this.btnLoadOriginalImage.getScene().getWindow();

        File selectedDirectory = directoryChooser.showDialog(stage);

        if (selectedDirectory != null) {
            String saveMode = this.cbSaveMode.getValue();
            this.coderTestCase.saveQuantizedErrorAsNLC(selectedDirectory, saveMode);

        }
    }

    /**
     * The btnLoadQuantizedError Event
     * open a dialog and load the nlc file
     */
    @FXML
    protected void onBtnLoadQuantizedErrorClick() throws IOException {


        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select an bmp image");

        File defaultDirectory = new File("C:\\Users\\stoic\\Desktop\\master\\Sem2\\ACM\\Laburi\\ACM_Homeworks_M1S2\\H2_NLPC\\nlcFile");
        fileChooser.setInitialDirectory(defaultDirectory);

        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("NLC Files (*.nlc)", "*.nlc");
        fileChooser.getExtensionFilters().add(extFilter);
        Stage stage = (Stage) this.btnLoadOriginalImage.getScene().getWindow();
        File selectedFile = fileChooser.showOpenDialog(stage);

        if (selectedFile != null) {
            this.decoderTestCase.initDecoder(selectedFile);
            this.changeInterfaceAfterLoadQuantizedErrorEvent();
        }

    }

    /**
     * The btnDecode Event
     * open a dialog and load the nlc file
     */
    @FXML
    protected void onBtnDecodeClick() throws IOException {
        WritableImage writableImage = this.decoderTestCase.startDecoding();
        this.iwDecodedImage.setImage(writableImage);

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
            this.decoderTestCase.saveDecodedFile(selectedDirectory);

        }
    }


    /***
     * refresh error image
     *
     * @throws IOException an exception
     */
    @FXML
    protected void onBtnRefreshClick() throws IOException {
        String contrastString = this.tfContrast.getText();
        if (!contrastString.equals("")) {
            double contrast = Double.parseDouble(contrastString);
            String imageType = this.cbError.getValue();
            WritableImage writableImage = this.coderTestCase.createErrorImage(imageType, contrast);
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
        int[] minMaxValue = this.coderTestCase.computeError();

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
        this.setDisableEncodeGroup(false);
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
        cbHistogramSource.getItems().remove("Image prediction from from decoder");


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

        if (!cbHistogramSource.getItems().contains("Image prediction from from decoder")) {
            cbHistogramSource.getItems().add("Image prediction from from decoder");
        }

        this.cbHistogramSource.setValue("Q predictor error image from decoder");

    }

    /**
     * set disable parameter of each item from encode group
     *
     * @param state the state that will be set
     */
    private void setDisableEncodeGroup(boolean state) {
        this.cbPredictionSelection.setDisable(state);
        this.tfK.setDisable(state);
        this.btnEncode.setDisable(state);
        this.cbHistogramSource.setDisable(state);
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
        this.btnComputeError.setDisable(state);

        this.tfContrast.setDisable(state);

        this.lblMaxError.setText("Max=");
        this.lblMinError.setText("Min=");

        this.iwErrorImage.setImage(null);
    }

}