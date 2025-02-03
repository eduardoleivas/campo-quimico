package com.campoquimico.handlers.buttonHandlers;

import java.io.File;
import java.net.URISyntaxException;
import javafx.event.ActionEvent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import com.campoquimico.database.DatabaseReader;

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
        DatabaseReader dbReader;
        try {
            dbReader = new DatabaseReader(new File(getClass().getResource("/database/default.xlsx").toURI()).getAbsolutePath());
            dbReader.processMolecule();
        } catch (URISyntaxException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    //QUIT BUTTON HANDLER
    public void handleQuitButton(ActionEvent event) {
        System.exit(0);
    }
}