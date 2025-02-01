module com.campoquimico {
    requires javafx.controls;
    requires javafx.fxml;

    opens com.campoquimico to javafx.fxml;
    exports com.campoquimico;
}
