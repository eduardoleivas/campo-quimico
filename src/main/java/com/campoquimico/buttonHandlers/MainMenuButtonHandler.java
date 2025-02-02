package com.campoquimico.buttonHandlers;

import com.campoquimico.App;

import javafx.event.ActionEvent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class MainMenuButtonHandler {

    private final Stage primaryStage;

    public MainMenuButtonHandler(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    //START GAME HANDLER
    public void handleStartButton(ActionEvent event) {
        //SET UP GAME WINDOW
        Stage gameStage = new Stage();
        gameStage.setTitle("Game Window");
        gameStage.setScene(new Scene(new StackPane(new Button("Game Started")), 800, 600));
        gameStage.show();

        
        primaryStage.hide(); //HIDE THE MAIN MENU INSTEAD OF CLOSING

        //RETURNS TO MENU WHEN THE GAME IS CLOSED
        gameStage.setOnCloseRequest(e -> primaryStage.show());
    }

    //SETTINGS BUTTON HANDLER
    public void handleSettingsButton(ActionEvent event) {
        System.out.println("Settings button clicked");
    }

    //QUIT BUTTON HANDLER
    public void handleQuitButton(ActionEvent event) {
        System.exit(0);
    }
}