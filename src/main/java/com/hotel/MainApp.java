package com.hotel;

import com.hotel.ui.MainController;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MainApp extends Application {
    @Override
    public void start(Stage stage) {
        MainController controller = new MainController();
        Scene scene = new Scene(controller.buildUI(), 1050, 700);
        stage.setTitle("Hotel Booking System");
        stage.setScene(scene);
        stage.setMinWidth(900);
        stage.setMinHeight(600);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
