package com.h3_fractal_image_coder.controller;

import com.h3_fractal_image_coder.RangeDetails;
import com.h3_fractal_image_coder.coderanddecoder.Tools;
import com.h3_fractal_image_coder.usecases.CoderUseCase;
import com.h3_fractal_image_coder.usecases.DecoderUseCase;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class AppController implements Initializable {

    @FXML
    ProgressBar pbProgress;
    //Buttons
    @FXML
    private Button btnLoadOriginalImage;
    @FXML
    private Button btnProcess;
    @FXML
    private Button btnSaveOriginalImage;
    @FXML
    private Button btnLoadInitialImage;
    @FXML
    private Button btnLoadFic;
    @FXML
    private Button btnDecode;
    @FXML
    private Button btnSaveDecodedImage;
    //Images
    @FXML
    private ImageView iwOriginalImage;
    @FXML
    private ImageView iwRangeBlock;
    @FXML
    private ImageView iwDomainBlock;
    @FXML
    private ImageView iwDecodedImage;
    //Labels
    @FXML
    private Label lblXDomain;
    @FXML
    private Label lblYDomain;
    @FXML
    private Label lblIzo;
    @FXML
    private Label lblScale;
    @FXML
    private Label lblOffset;
    @FXML
    private Label lblPSNR;
    //Others
    @FXML
    private Pane pOriginalImage;
    @FXML
    private TextField tfSteps;
    /**
     * range frame
     */
    private Rectangle rangeFrame;
    /**
     * domain frame
     */
    private Rectangle domainFrame;
    /**
     * coder test case
     */
    private CoderUseCase coderUseCase;

    /**
     * decoder test case
     */
    private DecoderUseCase decoderUseCase;

    /**
     * Thread for  process
     */
    private Task processTask;


    /**
     * Used for preset some components
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        this.rangeFrame = new Rectangle(0, 0, 10, 10);
        this.rangeFrame.setFill(Color.TRANSPARENT);
        this.rangeFrame.setStroke(Color.BLACK);
        this.rangeFrame.setVisible(false);

        this.domainFrame = new Rectangle(0, 0, 18, 18);
        this.domainFrame.setFill(Color.TRANSPARENT);
        this.domainFrame.setStroke(Color.BLACK);
        this.domainFrame.setVisible(false);

        pOriginalImage.getChildren().add(rangeFrame);
        pOriginalImage.getChildren().add(domainFrame);

        this.processTask = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                return null;
            }
        };

    }


    /***
     * Open a dialog for choosing a bmp image
     * Load the image in the interface
     *
     * @throws IOException an exception
     */
    @FXML
    protected void onBtnLoadOriginalImageClick() throws IOException {
        this.prepareInterfaceForProcessEvent();

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select an bmp image");

        File defaultDirectory = new File("C:\\Users\\stoic\\Desktop\\master\\Sem2\\ACM\\Laburi\\ACM_Homeworks_M1S2\\H3_Fractal_image_coder\\Images512");
        fileChooser.setInitialDirectory(defaultDirectory);
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("BMP (*.bmp)", "*.bmp");
        fileChooser.getExtensionFilters().add(extFilter);

        Stage stage = (Stage) this.btnLoadOriginalImage.getScene().getWindow();
        File selectedFile = fileChooser.showOpenDialog(stage);

        if (selectedFile != null) {
            Image image = new Image(selectedFile.toURI().toString());
            this.iwOriginalImage.setImage(image);
            this.coderUseCase = new CoderUseCase(selectedFile);
            this.changeInterfaceAfterLoadOriginalImageEvent();

        }
    }

    /**
     * Process event click
     */
    @FXML
    protected void onBtnProcessClick() {
        this.prepareInterfaceForProcessEvent();
        int maxValue = 4096;
        this.processTask = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                int progress = 0;
                for (int yRange = 0; yRange < Tools.NUMBER_OF_RANGES; yRange++) {
                    for (int xRange = 0; xRange < Tools.NUMBER_OF_RANGES; xRange++) {
                        final int finalXRange = xRange;
                        final int finalYRange = yRange;
                        coderUseCase.createRangeDetailsCorrespondingToRangeCoordinates(finalXRange, finalYRange);
                        progress++;
                        double currentProgressPercentage = (double) progress / maxValue;
                        updateProgress(currentProgressPercentage, 1);
                    }
                }
                return null;
            }
        };


        pbProgress.progressProperty().bind(this.processTask.progressProperty());
        Thread thread = new Thread(processTask);
        thread.setDaemon(true);//secondary thread , daemon can be intrerupted when main thread is done
        thread.start();

        this.iwOriginalImage.setOnMouseClicked(this::onMouseClickedOriginalImageEvent);
        this.btnSaveOriginalImage.setDisable(false);
    }


    /**
     * on btn save original image click
     *
     * @throws IOException an exception
     */
    @FXML
    protected void onBtnSaveOriginalImageClick() throws IOException {
        if (pbProgress.getProgress() != 1) {
            String title = "The process is not ready";
            String message = "The original image is still being processed";
            this.showDialog(title, message);
            return;
        }

        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("Select an Directory");

        File defaultDirectory = new File("C:\\Users\\stoic\\Desktop\\master\\Sem2\\ACM\\Laburi\\ACM_Homeworks_M1S2\\H3_Fractal_image_coder");
        directoryChooser.setInitialDirectory(defaultDirectory);

        Stage stage = (Stage) this.btnLoadOriginalImage.getScene().getWindow();

        File selectedDirectory = directoryChooser.showDialog(stage);

        if (selectedDirectory != null) {
            this.coderUseCase.saveRangeDetailsAsFic(selectedDirectory);

        }
    }

    /**
     * on btn load initial image click event
     *
     * @throws IOException an exception
     */
    @FXML
    protected void onBtnLoadInitialImageClick() throws IOException {
        this.changeInterfaceBeforeLoadInitialImageEvent();

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select an bmp image");

        File defaultDirectory = new File("C:\\Users\\stoic\\Desktop\\master\\Sem2\\ACM\\Laburi\\ACM_Homeworks_M1S2\\H3_Fractal_image_coder\\Images512");
        fileChooser.setInitialDirectory(defaultDirectory);
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("BMP (*.bmp)", "*.bmp");
        fileChooser.getExtensionFilters().add(extFilter);

        Stage stage = (Stage) this.btnLoadOriginalImage.getScene().getWindow();
        File selectedFile = fileChooser.showOpenDialog(stage);

        if (selectedFile != null) {
            this.decoderUseCase = new DecoderUseCase(selectedFile);
            this.changeInterfaceAfterLoadInitialImageEvent();
        }
    }

    /**
     * on btn  load fic click event
     *
     * @throws IOException an exception
     */
    @FXML
    protected void onBtnLoadFicClick() throws IOException {

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select an bmp image");

        File defaultDirectory = new File("C:\\Users\\stoic\\Desktop\\master\\Sem2\\ACM\\Laburi\\ACM_Homeworks_M1S2\\H3_Fractal_image_coder\\Images512");
        fileChooser.setInitialDirectory(defaultDirectory);
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter(" FIC File (*.fic)", "*.fic");
        fileChooser.getExtensionFilters().add(extFilter);

        Stage stage = (Stage) this.btnLoadOriginalImage.getScene().getWindow();
        File selectedFile = fileChooser.showOpenDialog(stage);

        if (selectedFile != null) {
            this.decoderUseCase.getRangesDetailsFromFile(selectedFile);

            this.tfSteps.setText("1");
            this.lblPSNR.setText("");
            this.btnDecode.setDisable(false);
            this.btnSaveDecodedImage.setDisable(true);

        }
    }

    /**
     * on btn decode click event
     *
     * @throws IOException an exception
     */
    @FXML
    protected void onBtnDecodeClick() throws IOException {
        String stepsString = this.tfSteps.getText();
        if (!stepsString.equals("")) {
            int steps = -1;
            try {
                steps = Integer.parseInt(stepsString);
            } catch (NumberFormatException e) {
                String title = "Wrong steps parameter";
                String message = "The steps parameter is not a number!";
                this.showDialog(title, message);
                return;
            }
            if (steps <= 0) {
                String title = "Wrong steps parameter";
                String message = "The steps parameter is negative or zero.";
                this.showDialog(title, message);
                return;
            }

            for (int i = 0; i < steps; i++) {
                this.decoderUseCase.decode();

            }
            this.drawDecodedImage();
            String message = "";
            if (this.coderUseCase == null)
                message = "The original image is not loaded";
            else {
                double psnr = this.decoderUseCase.computePSNR(this.coderUseCase.getOriginalImageValues());
                message = "PSNR= " + psnr;
            }
            this.lblPSNR.setText(message);
            this.btnSaveDecodedImage.setDisable(false);

        } else {
            String title = "Wrong steps parameter";
            String message = "The steps parameter is null!";
            this.showDialog(title, message);
        }

    }

    /**
     * on btn save decoded image click event
     *
     * @throws IOException an exception
     */
    @FXML
    protected void onBtnSaveDecodedImageClick() throws IOException {


        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("Select an Directory");

        File defaultDirectory = new File("C:\\Users\\stoic\\Desktop\\master\\Sem2\\ACM\\Laburi\\ACM_Homeworks_M1S2\\H3_Fractal_image_coder");
        directoryChooser.setInitialDirectory(defaultDirectory);

        Stage stage = (Stage) this.btnLoadOriginalImage.getScene().getWindow();

        File selectedDirectory = directoryChooser.showDialog(stage);

        if (selectedDirectory != null) {
            this.decoderUseCase.saveDecodeImage(selectedDirectory);
        }
    }


    /**
     * mouse click on original image event
     *
     * @param mouseEvent mouse event
     */
    protected void onMouseClickedOriginalImageEvent(MouseEvent mouseEvent) {
        if (pbProgress.getProgress() != 1) {
            return;
        }

        int layout = 10;
        this.rangeFrame.setVisible(false);
        this.domainFrame.setVisible(false);

        //Range
        int xIndex = (int) (mouseEvent.getX() - layout) / 8;
        int yIndex = (int) (mouseEvent.getY() - layout) / 8;
        int x = xIndex * 8 + layout - 1;
        int y = yIndex * 8 + layout - 1;

        this.rangeFrame.setX(x);
        this.rangeFrame.setY(y);
        this.rangeFrame.setVisible(true);

        WritableImage writableImage = this.coderUseCase.getRangeImage(xIndex, yIndex);
        this.iwRangeBlock.setImage(writableImage);

        //Domain
        RangeDetails rangeDetails = this.coderUseCase.getRangeDetailsCorrespondingToRankCoordinates(xIndex, yIndex);
        String xDomain = String.valueOf(rangeDetails.getXDomain());
        String yDomain = String.valueOf(rangeDetails.getYDomain());
        String izo = String.valueOf(rangeDetails.getIzoType());
        String scale = String.valueOf(rangeDetails.getScale());
        String offset = String.valueOf(rangeDetails.getOffset());

        lblXDomain.setText(xDomain);
        lblYDomain.setText(yDomain);
        lblIzo.setText(izo);
        lblScale.setText(scale);
        lblOffset.setText(offset);

        xIndex = rangeDetails.getXDomain();
        yIndex = rangeDetails.getYDomain();

        x = xIndex * 8 + layout - 1;
        y = yIndex * 8 + layout - 1;

        this.domainFrame.setX(x);
        this.domainFrame.setY(y);
        this.domainFrame.setVisible(true);

        writableImage = this.coderUseCase.getDomainImage(xIndex, yIndex);
        this.iwDomainBlock.setImage(writableImage);
    }

    /**
     * prepare interface for process event
     */
    private void prepareInterfaceForProcessEvent() {
        this.btnSaveOriginalImage.setDisable(true);

        this.rangeFrame.setVisible(false);
        this.domainFrame.setVisible(false);

        this.iwRangeBlock.setImage(null);
        this.iwDomainBlock.setImage(null);

        this.lblXDomain.setText("");
        this.lblYDomain.setText("");
        this.lblIzo.setText("");
        this.lblScale.setText("");
        this.lblOffset.setText("");

        this.pbProgress.progressProperty().unbind();
        this.pbProgress.setProgress(0);
        this.processTask.cancel();

    }


    /**
     * change the interface after load original image Event
     */
    private void changeInterfaceAfterLoadOriginalImageEvent() {
        //Disable
        this.btnProcess.setDisable(false);
    }

    /**
     * change interface after load initial image
     */
    private void changeInterfaceBeforeLoadInitialImageEvent() {
        this.tfSteps.setText("");
        this.lblPSNR.setText("");
        this.tfSteps.setDisable(true);
        this.btnLoadFic.setDisable(true);
        this.btnDecode.setDisable(true);
        this.btnSaveDecodedImage.setDisable(true);
    }

    /**
     * change interface after load initial image
     */
    private void changeInterfaceAfterLoadInitialImageEvent() {
        this.tfSteps.setText("1");
        this.tfSteps.setDisable(false);
        this.btnLoadFic.setDisable(false);
        this.drawDecodedImage();

    }


    /**
     * draw decoded Image
     */
    public void drawDecodedImage() {
        WritableImage writableImage = this.decoderUseCase.createDecodedImage();
        this.iwDecodedImage.setImage(writableImage);
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
}