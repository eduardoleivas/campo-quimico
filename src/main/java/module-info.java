module com.campoquimico {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.media;
    requires javafx.graphics;
    
    requires org.apache.poi.ooxml;
    

    opens com.campoquimico to javafx.fxml;
    exports com.campoquimico;
}
