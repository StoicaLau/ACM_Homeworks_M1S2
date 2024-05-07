module com.example.h4_wavelet {
    requires javafx.controls;
    requires javafx.fxml;
            
            requires com.dlsc.formsfx;
                        
    opens com.h4_wavelet to javafx.fxml;
    exports com.h4_wavelet;
}