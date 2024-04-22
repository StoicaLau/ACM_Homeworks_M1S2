package com.h3_fractal_image_coder.controller;

import com.h3_fractal_image_coder.testcases.CoderTestCase;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;

public class AppController {

    //Buttons
    @FXML
    private Button btnLoadOriginalImage;

    @FXML
    private Button btnProcess;

    //Images
    @FXML
    private ImageView iwOriginalImage;

    /**
     * coder test case
     */
    private CoderTestCase coderTestCase;

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
        }

    }

    /**
     * change the interface after load original image Event
     */
    private void changeInterfaceAfterLoadOriginalImageEvent() {
        //Disable
        this.btnProcess.setDisable(false);

    }
}