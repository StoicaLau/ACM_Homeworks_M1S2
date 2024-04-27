package com.h3_fractal_image_coder.controller;

import com.h3_fractal_image_coder.RangeDetails;
import com.h3_fractal_image_coder.testcases.CoderUseCase;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

public class AppController implements Initializable {

    /**
     * number of ranges
     */
    private static final int NUMBER_OF_RANGES = 64;

    private static final int MAX_VALUE = 4096;
    @FXML
    ProgressBar pbProgress;
    /**
     * current state of progress barr
     * atomic integer good for threads
     */
    AtomicInteger progress;
    /**
     * executor service
     */
    ExecutorService executor;
    //Buttons
    @FXML
    private Button btnLoadOriginalImage;
    @FXML
    private Button btnProcess;
    @FXML
    private Button btnSaveOriginalImage;
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

    //Others
    @FXML
    private Pane pOriginalImage;
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

        this.progress = null;
        this.executor = Executors.newFixedThreadPool(5);

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
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("Imagini BMP (*.bmp)", "*.bmp");
        fileChooser.getExtensionFilters().add(extFilter);

        Stage stage = (Stage) this.btnLoadOriginalImage.getScene().getWindow();
        File selectedFile = fileChooser.showOpenDialog(stage);

        if (selectedFile != null) {
            Image image = new Image(selectedFile.toURI().toString());
            this.iwOriginalImage.setImage(image);
            this.coderUseCase = new CoderUseCase(selectedFile);
            this.changeInterfaceAfterLoadOriginalImageEvent();
            this.drawImage();
        }
    }

    /**
     * Process event click
     */
    @FXML
    protected void onBtnProcessClick() {
        this.prepareInterfaceForProcessEvent();
        AtomicInteger progress = new AtomicInteger(0);//atomic int good for threads
        ExecutorService executor = Executors.newFixedThreadPool(5);

        for (int yRange = 0; yRange < NUMBER_OF_RANGES; yRange++) {
            for (int xRange = 0; xRange < NUMBER_OF_RANGES; xRange++) {
                final int finalXRange = xRange;
                final int finalYRange = yRange;

                executor.submit(() -> {
                    coderUseCase.createRangeDetailsCorrespondingToRangeCoordinates(finalXRange, finalYRange);
                    int currentProgress = progress.incrementAndGet();
                    double currentProgressPercentage = (double) currentProgress / MAX_VALUE;
                    Platform.runLater(() -> pbProgress.setProgress(currentProgressPercentage));
                });
            }
        }

        executor.shutdown();
        this.iwOriginalImage.setOnMouseClicked(this::onMouseClickedOriginalImageEvent);
        this.btnSaveOriginalImage.setDisable(false);
    }

    /**
     * on btn save original image click
     */
    @FXML
    protected void onBtnSaveOriginalImageClick() {
        if (pbProgress.getProgress() != 1) {
            String title = "The process is not ready";
            String message = "The original image is still being processed";
            this.showDialog(title, message);
            return;
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

        this.pbProgress.setProgress(0);
        this.executor.shutdownNow();

    }

    /**
     * change the interface after load original image Event
     */
    private void changeInterfaceAfterLoadOriginalImageEvent() {
        //Disable
        this.btnProcess.setDisable(false);
    }


    public void drawImage() {
        WritableImage writableImage = this.coderUseCase.writeImage();
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