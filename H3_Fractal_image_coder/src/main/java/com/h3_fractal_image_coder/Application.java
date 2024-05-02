package com.h3_fractal_image_coder;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
//TODO de cate ori dau decode am limita?
//TODO am facut bine psnr?

public class Application extends javafx.application.Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Application.class.getResource("fractalApp.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        stage.setTitle("Fractal App");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}