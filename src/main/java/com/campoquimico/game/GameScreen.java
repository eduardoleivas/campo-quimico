package com.campoquimico.game;

import java.util.List;
import java.util.Random;
import javafx.animation.FadeTransition;
import javafx.animation.PauseTransition;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.util.Duration;
import com.campoquimico.objects.Tile;
import com.campoquimico.handlers.gameHandlers.GameHandler;
import com.campoquimico.handlers.optionsHandlers.OptionsHandler;
import com.campoquimico.database.DatabaseReader;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class GameScreen {
    private Scene boardScene;
    private static final int BOARD_SIZE = 15;
    private static final int TILE_SIZE = 40;
    private static final Random random = new Random();
    private GridPane gridPane = new GridPane();
    private Stage boardStage;
    private String actualMolecule;
    private Tile[][] boardTiles = new Tile[BOARD_SIZE][BOARD_SIZE];

    public String getActualMolecule() {
        return actualMolecule;
    }

    public void setActualMolecule(String molecule) {
        this.actualMolecule = molecule;

        //RUN THROUGH ALL TILES CHANGING THE ACTUAL MOLECULE NAME
        for (javafx.scene.Node node : gridPane.getChildren()) {
            if (node instanceof Tile) { // Check if the node is a Tile object
                Tile tile = (Tile) node;
                tile.setMolecule(actualMolecule);
            }
        }
    }

    public Scene getGameScreen() {
        return this.boardScene;
    }

    public GameScreen(String[][] molecule, String actualMolecule, Stage boardStage, Stage primaryStage, int sequentialModeId) {
        this.boardStage = boardStage;
        int moleculeWidth = molecule[0].length;
        int moleculeHeight = molecule.length;
        DatabaseReader dbReader = new DatabaseReader(OptionsHandler.getInstance().getDatabase());

        List<List<String>> atomInfos = dbReader.getInfos(sequentialModeId);
        
        // RANDOM INDEX GENERATORS
        int indexPosX = random.nextInt(BOARD_SIZE - moleculeWidth + 1);
        int indexPosY = random.nextInt(BOARD_SIZE - moleculeHeight + 1);

        System.out.println("Random Position: (" + indexPosX + ", " + indexPosY + ")");

        // FILLS THE GRIDPANE WITH TILES
        for (int i = 0; i < BOARD_SIZE; i++) {
            for (int j = 0; j < BOARD_SIZE; j++) {
                String tileId = "X"; // DEFAULT EMPTY TILE

                if (i >= indexPosY && i < indexPosY + moleculeHeight &&
                    j >= indexPosX && j < indexPosX + moleculeWidth) {

                    String moleculeValue = molecule[i - indexPosY][j - indexPosX];
                    if (!moleculeValue.equals("X")) {
                        tileId = moleculeValue; // ASSIGN MOLECULE VALUE
                    }
                }

                // FIND TILE INFO FROM ATOM INFOS
                List<String> tileInfo = null;
                for (List<String> row : atomInfos) {
                    if (!row.isEmpty() && row.get(0).equals(tileId)) {
                        tileInfo = row;
                        break;
                    }
                }

                // CREATE TILE WITH INFO
                Tile tile;
                if (tileInfo != null) {
                    String symbol = tileInfo.size() > 1 ? tileInfo.get(1) : tileId;
                    String name = tileInfo.size() > 2 ? tileInfo.get(2) : "";
                    String tip1 = tileInfo.size() > 3 ? tileInfo.get(3) : "";
                    String tip2 = tileInfo.size() > 4 ? tileInfo.get(4) : "";
                    String tip3 = tileInfo.size() > 5 ? tileInfo.get(5) : "";
                    String tip4 = tileInfo.size() > 6 ? tileInfo.get(6) : "";
                    String tip5 = tileInfo.size() > 7 ? tileInfo.get(7) : "";
                    String tip6 = tileInfo.size() > 8 ? tileInfo.get(8) : "";

                    tile = new Tile(tileId, symbol, name, tip1, tip2, tip3, tip4, tip5, tip6, actualMolecule);
                } else {
                    tile = new Tile(tileId, tileId, "", "", "", "", "", "", "", actualMolecule);
                }
                
                boardTiles[i][j] = tile;
                gridPane.add(tile, j, i);

                // FINAL VARIABLES FOR LAMBDA SCOPE
                final int tileX = j;
                final int tileY = i;

                if (tile.isEmpty()) {
                    tile.setOnMouseClicked(event -> revealEmptyTiles(tileX, tileY));
                }
            }
        }

        //CREATE "NEXT" BUTTON
        Button nextButton = new Button("Próximo");
        nextButton.setVisible(false);
        GameHandler.getInstance().showNextProperty().addListener((obs, oldVal, newVal) -> {
            nextButton.setVisible(newVal.booleanValue());
            System.out.println("ShowButton Updated: " + newVal);
        });
        nextButton.setOnAction(event -> {
            if (boardStage != null && boardStage.isShowing()) {
                boardStage.close(); //CLOSE CURRENT GAME BEFORE OPENING NEXT ONE
            }

            //GET NEW MOLECULE ID
            int newMoleculeId;
            if(OptionsHandler.getInstance().isRandomMode()) {
                newMoleculeId = new DatabaseReader(OptionsHandler.getInstance().getDatabase()).getRandomMolecule();
            } else {
                GameHandler.getInstance().setSequentialId(GameHandler.getInstance().getSequentialId() + 1);
                newMoleculeId = GameHandler.getInstance().getSequentialId();
            }

            //OPEN NEW GAME WINDOW
            DatabaseReader newDbReader = new DatabaseReader(OptionsHandler.getInstance().getDatabase());
            if(!newDbReader.checkMoleculeExists(newMoleculeId)) {
                showAlert(AlertType.INFORMATION, "Parabéns!", "Você completou todas as moléculas deste Banco de Dados.");
                boardStage.close();
                GameHandler.getInstance().setNextButton(false);
                GameHandler.getInstance().setSequentialId(0);
                if(!primaryStage.isShowing()) {
                    primaryStage.show();
                }
                return;
                
            } else {
                Stage newBoardStage = new Stage();
                newBoardStage.setTitle("JOGO");
                String[][] newMolecule = newDbReader.processMolecule(newMoleculeId);
                GameScreen newGameScreen = new GameScreen(newMolecule, newDbReader.getMoleculeName(newMoleculeId), newBoardStage, primaryStage, newMoleculeId);
    
                newBoardStage.setScene(newGameScreen.getGameScreen());
                GameHandler.getInstance().setNextButton(false);
                newBoardStage.setOnCloseRequest(closeEvent -> onCloseGame(closeEvent, primaryStage));
                newBoardStage.show();
            }
        });

        // LAYOUT USING ANCHORPANE
        AnchorPane layout = new AnchorPane();
        layout.getChildren().addAll(gridPane, nextButton);

        // POSITIONING THE ELEMENTS
        AnchorPane.setTopAnchor(gridPane, 0.0);
        AnchorPane.setLeftAnchor(gridPane, 0.0);
        AnchorPane.setRightAnchor(gridPane, 0.0);

        AnchorPane.setBottomAnchor(nextButton, 10.0);
        AnchorPane.setLeftAnchor(nextButton, (BOARD_SIZE * TILE_SIZE) / 2.0 - 40); // Centered

        this.boardScene = new Scene(layout, BOARD_SIZE * TILE_SIZE, (BOARD_SIZE * TILE_SIZE) + 50);
    }

    // METHOD TO RECURSIVELY REVEAL EMPTY TILES AROUND
    private void revealEmptyTiles(int x, int y) {
        if (x < 0 || y < 0 || x >= BOARD_SIZE || y >= BOARD_SIZE) return;
        Tile tile = boardTiles[y][x];

        if (tile.isRevealed() || !tile.isEmpty()) return;

        tile.revealTile(null, actualMolecule);

        // FADE-IN EFFECT FOR THE TILES
        FadeTransition fadeIn = new FadeTransition(Duration.seconds(0.3), tile);
        fadeIn.setFromValue(0);
        fadeIn.setToValue(1);
        fadeIn.play();

        //REVEAL ADJACENT EMPTY TILES RECURSIVELY
        PauseTransition delay = new PauseTransition(Duration.seconds(0.05));
        delay.setOnFinished(event -> {
            revealEmptyTiles(x - 1, y - 1); //TOP-LEFT
            revealEmptyTiles(x, y - 1);     //TOP
            revealEmptyTiles(x + 1, y - 1); //TOP-RIGHT
            revealEmptyTiles(x + 1, y);     //RIGHT
            revealEmptyTiles(x + 1, y + 1); //BOTTOM-RIGHT
            revealEmptyTiles(x, y + 1);     //BOTTOM
            revealEmptyTiles(x - 1, y + 1); //BOTTOM-LEFT
            revealEmptyTiles(x - 1, y);     //LEFT
        });
        delay.play();
    }

    public void onCloseGame(WindowEvent event, Stage primaryStage) {
        GameHandler.getInstance().setSequentialId(0);
        GameHandler.getInstance().setNextButton(false);
        primaryStage.show();
    }

    private void showAlert(AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
