package com.h4_wavelet;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class AppController implements Initializable {

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

    //Buttons
    @FXML
    private Button btnLoadOriginalImage;


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


    /**
     * current level on horizontal
     */

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
        this.currentLevelH = 0;
        this.currentLevelV = 0;

        this.btnAnH1.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                try {
                    onBtnAnClick(1, "horizontal");
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });

        this.btnAnV1.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                try {
                    onBtnAnClick(1, "vertical");
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });

        this.btnAnH2.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                try {
                    onBtnAnClick(2, "horizontal");
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });

        this.btnAnV2.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                try {
                    onBtnAnClick(2, "vertical");
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });

        this.btnAnH3.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                try {
                    onBtnAnClick(3, "horizontal");
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });

        this.btnAnV3.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                try {
                    onBtnAnClick(3, "vertical");
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });

        this.btnAnH4.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                try {
                    onBtnAnClick(4, "horizontal");
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });

        this.btnAnV4.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                try {
                    onBtnAnClick(4, "vertical");
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });

        this.btnAnH5.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                try {
                    onBtnAnClick(5, "horizontal");
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });

        this.btnAnV5.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                try {
                    onBtnAnClick(5, "vertical");
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
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
        this.currentLevelH=0;
        this.currentLevelV=0;
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

            WritableImage writableImage = this.backend.getImage("Original");
            this.iwOriginalImage.setImage(writableImage);

            writableImage = this.backend.getImage("Wavelet");
            this.iwWaveletImage.setImage(writableImage);
        }
    }

    /**
     * on btn load original image click event
     *
     * @throws IOException an exception
     */
    @FXML
    protected void onBtnAnClick(int level, String type) throws IOException {
        double scale = Double.parseDouble(this.tfScale.getText());
        double offset = Double.parseDouble(this.tfOffset.getText());
        if (type.equals("horizontal")) {
            this.currentLevelH = level;
            this.backend.analyzeHorizontal(this.currentLevelH);
        } else if (type.equals("vertical")) {
            this.currentLevelV = level;
            this.backend.analyzeVertical(this.currentLevelV);
        }
        WritableImage writableImage = this.backend.getWaveletLvlImage(this.currentLevelH, this.currentLevelV, scale, offset);
        this.iwWaveletImage.setImage(writableImage);

    }
}