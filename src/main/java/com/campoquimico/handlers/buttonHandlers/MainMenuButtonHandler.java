package com.campoquimico.handlers.buttonHandlers;

import java.io.File;
import java.net.URISyntaxException;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;

import com.campoquimico.database.DatabaseReader;
import com.campoquimico.game.GameScreen;
import com.campoquimico.handlers.gameHandlers.GameHandler;
import com.campoquimico.handlers.optionsHandlers.OptionsHandler;

public class MainMenuButtonHandler {

    private final Stage primaryStage;
    private DatabaseReader dbReader;

    public MainMenuButtonHandler(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    //START GAME HANDLER
    public void handleStartButton(ActionEvent event) {
        dbReader = new DatabaseReader(OptionsHandler.getInstance().getDatabase());
        int moleculeId;
        if (OptionsHandler.getInstance().isRandomMode()) {
            moleculeId = dbReader.getRandomMolecule();
        } else {
            moleculeId = GameHandler.getInstance().getSequentialId();
        }
    
        String[][] molecule = dbReader.processMolecule(moleculeId);
        Stage boardStage = new Stage();
        GameScreen gameScreen = new GameScreen(molecule, dbReader.getMoleculeName(moleculeId), boardStage, primaryStage, moleculeId);
        boardStage.setTitle("JOGO");
        boardStage.setScene(gameScreen.getGameScreen());
        boardStage.show();
    
        if (primaryStage.isShowing()) {
            primaryStage.hide(); // Hide the main menu instead of closing
        }
    
        // Close the main menu window when the game window is closed
        boardStage.setOnCloseRequest(closeEvent -> gameScreen.onCloseGame(closeEvent, primaryStage));
    }

    //SETTINGS BUTTON HANDLER
    public void handleSettingsButton(ActionEvent event) {
        // Create a new stage (window)
        Stage settingsStage = new Stage();
        settingsStage.setTitle("Configurações");

        // Volume Slider
        Label volumeLabel = new Label("Volume");
        Slider volumeSlider = new Slider(0, 1.0, 5); // Min=1, Max=10, Default=5
        volumeSlider.setValue(OptionsHandler.getInstance().getVolume());
        volumeSlider.setShowTickMarks(true);
        volumeSlider.setShowTickLabels(true);
        volumeSlider.setMajorTickUnit(0.1);
        volumeSlider.setBlockIncrement(0.1);
        volumeSlider.valueProperty().addListener((obs, oldVal, newVal) -> 
            OptionsHandler.getInstance().setVolume(newVal.doubleValue()));

        // Random Mode Checkbox
        CheckBox randomModeCheckBox = new CheckBox("Modo Aleatório: Ativado");
        randomModeCheckBox.setSelected(true);
        randomModeCheckBox.setOnAction(e -> {
            String status = randomModeCheckBox.isSelected() ? "Ativado" : "Desativado";
            randomModeCheckBox.setText("Modo Aleatório: " + status);
            OptionsHandler.getInstance().setRandomMode(randomModeCheckBox.isSelected());
        });

        // File Chooser Button
        Button selectDatabaseButton = new Button("Selecionar Banco de Dados");
        Label filePathLabel = new Label("Nenhum arquivo selecionado");

        selectDatabaseButton.setOnAction(e -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Escolha um arquivo .xlsx");
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Excel Files", "*.xlsx"));
            File selectedFile = fileChooser.showOpenDialog(settingsStage);
            if (selectedFile != null) {
                filePathLabel.setText(selectedFile.getAbsolutePath());
                OptionsHandler.getInstance().setDatabase(selectedFile.getAbsolutePath());
            } else {
                OptionsHandler.getInstance().resetDatabase();
            }
        });

        // OK Button (Close Window)
        Button okButton = new Button("OK");
        okButton.setOnAction(e -> settingsStage.close());
        if(filePathLabel.getText().equals("Nenhum arquivo selecionado")) {
            OptionsHandler.getInstance().resetDatabase();
        }

        // Layout
        VBox layout = new VBox(10);
        layout.setPadding(new Insets(20));
        layout.setAlignment(Pos.CENTER);
        layout.getChildren().addAll(
            volumeLabel, volumeSlider, 
            randomModeCheckBox, 
            selectDatabaseButton, filePathLabel, 
            okButton
        );

        // Scene and Stage Setup
        Scene scene = new Scene(layout, 350, 300);
        settingsStage.setScene(scene);
        settingsStage.initModality(Modality.APPLICATION_MODAL); // Blocks interaction with main window
        settingsStage.showAndWait();
    }

    //QUIT BUTTON HANDLER
    public void handleQuitButton(ActionEvent event) {
        System.exit(0);
    }
}