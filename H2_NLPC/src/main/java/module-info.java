module com.myapp.h2_nlpc {
    requires javafx.controls;
    requires javafx.fxml;
            
            requires com.dlsc.formsfx;
    requires java.desktop;

    opens com.myapp.h2_nlpc to javafx.fxml;
    exports com.myapp.h2_nlpc;
    exports com.myapp.h2_nlpc.Controller;
    opens com.myapp.h2_nlpc.Controller to javafx.fxml;
}