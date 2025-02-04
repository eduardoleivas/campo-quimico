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
            int moleculeId = dbReader.getRandomMolecule();
            String[][] molecule = dbReader.processMolecule(moleculeId);
            GameScreen gameScreen = new GameScreen(molecule, dbReader.getMoleculeName(moleculeId));
            Stage boardStage = new Stage();
            boardStage.setTitle("JOGO");
            boardStage.setScene(gameScreen.getGameScreen());
            boardStage.show();

            if(primaryStage.isShowing())
                primaryStage.hide(); //HIDE THE MAIN MENU INSTEAD OF CLOSING

            //RETURNS TO MENU WHEN THE GAME IS CLOSED
            boardStage.setOnCloseRequest(e -> primaryStage.show());

        } catch (URISyntaxException e) {
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