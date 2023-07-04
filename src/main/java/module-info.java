module com.project.dchat {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;
    requires javafx.swing;
    requires org.apache.commons.io;
    requires javafx.media;
    requires java.mail;

    opens com.project.dchat to javafx.fxml;
    exports com.project.dchat.Server;
    opens com.project.dchat.Server to javafx.fxml;
    exports com.project.dchat.Client;
    opens com.project.dchat.Client to javafx.fxml;
    exports com.project.dchat.Entities;
    opens com.project.dchat.Entities to javafx.fxml;
}
