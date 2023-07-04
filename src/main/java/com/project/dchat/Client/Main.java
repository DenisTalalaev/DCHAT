package com.project.dchat.Client;

import com.project.dchat.Entities.PythonInstaller;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class Main extends Application {
    public static Stage mainStage;

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("/com/project/dchat/register.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 540, 420);
        stage.setTitle("DChat");
        stage.setScene(scene);
//        stage.setResizable(false);
        stage.setOnCloseRequest(event -> {
            if(RegistrationController.connection != null) RegistrationController.connection.disconnect();
        });
        stage.show();
        mainStage = stage;
    }

    public static void main(String[] args) {
        PythonInstaller.checkAndInstallPython();
        launch();
    }
}