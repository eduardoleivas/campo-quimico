package com.campoquimico.handlers.buttonHandlers;

import java.io.File;
import java.net.URISyntaxException;
import javafx.event.ActionEvent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

import com.campoquimico.database.DatabaseReader;
import com.campoquimico.game.GameScreen;
import com.campoquimico.objects.Tile;

public class MainMenuButtonHandler {

    private final Stage primaryStage;
    private DatabaseReader dbReader;

    public MainMenuButtonHandler(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    //START GAME HANDLER
    public void handleStartButton(ActionEvent event) {
        try {
            dbReader = new DatabaseReader(new File(getClass().getResource("/database/default.xlsx").toURI()).getAbsolutePath());
            String[][] molecule = dbReader.processMolecule(2);
            GameScreen gameScreen = new GameScreen(molecule);
            Stage boardStage = new Stage();
            boardStage.setTitle("Board");
            boardStage.setScene(gameScreen.getGameScreen());
            boardStage.show();

            primaryStage.hide(); //HIDE THE MAIN MENU INSTEAD OF CLOSING

            //RETURNS TO MENU WHEN THE GAME IS CLOSED
            boardStage.setOnCloseRequest(e -> primaryStage.show());

        } catch (URISyntaxException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    //SETTINGS BUTTON HANDLER
    public void handleSettingsButton(ActionEvent event) {
        
    }

    //QUIT BUTTON HANDLER
    public void handleQuitButton(ActionEvent event) {
        System.exit(0);
    }
}