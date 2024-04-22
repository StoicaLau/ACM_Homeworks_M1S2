module com.example.h3_fractal_image_coder {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;

    opens com.h3_fractal_image_coder to javafx.fxml;
    exports com.h3_fractal_image_coder;
    exports com.h3_fractal_image_coder.controller;
    opens com.h3_fractal_image_coder.controller to javafx.fxml;
}