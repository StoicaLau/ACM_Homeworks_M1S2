package com.h4_wavelet;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

//TODO trebuie cum va sa imi dau seama unde este ll cand incarc iamginea?
//TODO era vorba de o aproximare?

//TODO fixare iamgini

public class Application extends javafx.application.Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Application.class.getResource("waveletApp.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        stage.setTitle("Wavelet App");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}