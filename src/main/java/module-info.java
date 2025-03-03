module com.campoquimico {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.media;
    requires javafx.graphics;
    
    requires org.apache.poi.ooxml;
    requires javafx.base;
    requires org.apache.poi.poi;
    

    opens com.campoquimico to javafx.fxml;
    exports com.campoquimico;
}