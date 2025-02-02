module com.campoquimico {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.media;
    requires javafx.graphics;

    opens com.campoquimico to javafx.fxml;
    exports com.campoquimico;
}
