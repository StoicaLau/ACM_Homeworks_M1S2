package com.h3_fractal_image_coder.controller;

import com.h3_fractal_image_coder.testcases.CoderTestCase;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
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

public class AppController implements Initializable {

    //Buttons
    @FXML
    private Button btnLoadOriginalImage;

    @FXML
    private Button btnProcess;

    //Images
    @FXML
    private ImageView iwOriginalImage;

    @FXML
    private ImageView iwRangeBlock;

    @FXML
    private ImageView iwDecodedImage;

    //Others
    @FXML
    private Pane pOriginalImage;

    /**
     * frames
     */
    private Rectangle rangeFrame;

    private Rectangle domainFrame;

    /**
     * coder test case
     */
    private CoderTestCase coderTestCase;

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

    }


    /***
     * Open a dialog for choosing a bmp image
     * Load the image in the interface
     *
     * @throws IOException an exception
     */
    @FXML
    protected void onBtnLoadOriginalImageClick() throws IOException {
        this.rangeFrame.setVisible(false);
        this.domainFrame.setVisible(false);
        this.iwRangeBlock.setImage(null);

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
            this.coderTestCase = new CoderTestCase(selectedFile);
            this.changeInterfaceAfterLoadOriginalImageEvent();
            this.drawImage();
            this.iwOriginalImage.setOnMouseClicked(this::onMouseClickedOriginalImageEvent);
        }



    }

    /**
     * mouse click on original image event
     *
     * @param mouseEvent mouse event
     */
    protected void onMouseClickedOriginalImageEvent(MouseEvent mouseEvent) {
        int layout=10;
        this.rangeFrame.setVisible(false);
        int xIndex = (int)( mouseEvent.getX()-layout) / 8;
        int yIndex = (int) (mouseEvent.getY()-layout) / 8;
        System.out.println(xIndex+":"+yIndex);
        int x = xIndex * 8+layout-1;
        int y = yIndex * 8+layout-1;

        this.rangeFrame.setX((double) x);
        this.rangeFrame.setY((double) y);
        this.rangeFrame.setVisible(true);

        WritableImage writableImage=this.coderTestCase.getRangeImage(xIndex,yIndex);
        this.iwRangeBlock.setImage(writableImage);

    }

    /**
     * change the interface after load original image Event
     */
    private void changeInterfaceAfterLoadOriginalImageEvent() {
        //Disable
        this.btnProcess.setDisable(false);

    }

    public void drawImage(){
        WritableImage writableImage=this.coderTestCase.writeImage();
        this.iwDecodedImage.setImage(writableImage);
    }
}