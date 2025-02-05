package com.campoquimico.game;

import java.util.Random;
import javafx.animation.FadeTransition;
import javafx.animation.PauseTransition;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.util.Duration;
import com.campoquimico.objects.Tile;
import com.campoquimico.handlers.optionsHandlers.OptionsHandler;
import com.campoquimico.database.DatabaseReader;
import javafx.stage.Stage;

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

                // CREATE TILE AND ADD TO GRIDPANE
                Tile tile = new Tile(tileId, tileId, "", "", "", "", "", "", "", actualMolecule);
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

        // CREATE "NEXT" BUTTON
        Button nextButton = new Button("Próximo");
        nextButton.setOnAction(event -> {
            if (boardStage != null && boardStage.isShowing()) {
                boardStage.close(); // Close the current game window before opening a new one
            }

            // Get the new molecule ID
            int newMoleculeId = OptionsHandler.getInstance().isRandomMode() ? 
                new DatabaseReader(OptionsHandler.getInstance().getDatabase()).getRandomMolecule() 
                : sequentialModeId + 1; // Increment if sequential mode

            // Open new game window
            Stage newBoardStage = new Stage();
            newBoardStage.setTitle("JOGO");

            DatabaseReader newDbReader = new DatabaseReader(OptionsHandler.getInstance().getDatabase());
            String[][] newMolecule = newDbReader.processMolecule(newMoleculeId);
            GameScreen newGameScreen = new GameScreen(newMolecule, newDbReader.getMoleculeName(newMoleculeId), newBoardStage, primaryStage, newMoleculeId);

            newBoardStage.setScene(newGameScreen.getGameScreen());
            newBoardStage.setOnCloseRequest(e -> primaryStage.show());
            newBoardStage.show();
        });

        // LAYOUT USING ANCHORPANE
        AnchorPane layout = new AnchorPane();
        layout.getChildren().addAll(gridPane, nextButton);

        // POSITIONING THE ELEMENTS
        AnchorPane.setTopAnchor(gridPane, 0.0);
        AnchorPane.setLeftAnchor(gridPane, 0.0);
        AnchorPane.setRightAnchor(gridPane, 0.0);

        AnchorPane.setBottomAnchor(nextButton, 10.0); // 10px from the bottom
        AnchorPane.setLeftAnchor(nextButton, (BOARD_SIZE * TILE_SIZE) / 2.0 - 40); // Centered

        this.boardScene = new Scene(layout, BOARD_SIZE * TILE_SIZE, (BOARD_SIZE * TILE_SIZE) + 50);
    }

    // METHOD TO RECURSIVELY REVEAL EMPTY TILES AROUND
    private void revealEmptyTiles(int x, int y) {
        if (x < 0 || y < 0 || x >= BOARD_SIZE || y >= BOARD_SIZE) return;
        Tile tile = boardTiles[y][x];

        if (tile.isRevealed() || !tile.isEmpty()) return;

        tile.revealTile(actualMolecule);

        // FADE-IN EFFECT FOR THE TILES
        FadeTransition fadeIn = new FadeTransition(Duration.seconds(0.3), tile);
        fadeIn.setFromValue(0);
        fadeIn.setToValue(1);
        fadeIn.play();

        // REVEAL ADJACENT EMPTY TILES RECURSIVELY
        PauseTransition delay = new PauseTransition(Duration.seconds(0.05));
        delay.setOnFinished(event -> {
            revealEmptyTiles(x - 1, y - 1); // TOP-LEFT
            revealEmptyTiles(x, y - 1);     // TOP
            revealEmptyTiles(x + 1, y - 1); // TOP-RIGHT
            revealEmptyTiles(x + 1, y);     // RIGHT
            revealEmptyTiles(x + 1, y + 1); // BOTTOM-RIGHT
            revealEmptyTiles(x, y + 1);     // BOTTOM
            revealEmptyTiles(x - 1, y + 1); // BOTTOM-LEFT
            revealEmptyTiles(x - 1, y);     // LEFT
        });
        delay.play();
    }
}
