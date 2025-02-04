package com.campoquimico.game;

import java.util.Random;
import javafx.animation.FadeTransition;
import javafx.animation.PauseTransition;
import javafx.scene.Scene;
import javafx.scene.layout.GridPane;
import javafx.util.Duration;
import com.campoquimico.objects.Tile;

public class GameScreen {
    private Scene boardScene;
    private static final int BOARD_SIZE = 15;
    private static final int TILE_SIZE = 40;
    private static final Random random = new Random();

    private Tile[][] boardTiles = new Tile[BOARD_SIZE][BOARD_SIZE];

    public Scene getGameScreen() {
        return this.boardScene;
    }

    public GameScreen(String[][] molecule) {
        GridPane gridPane = new GridPane();

        int moleculeWidth = molecule[0].length;
        int moleculeHeight = molecule.length;

        // Generate random positions ensuring the molecule fits within the 15x15 board
        int indexPosX = random.nextInt(BOARD_SIZE - moleculeWidth + 1);
        int indexPosY = random.nextInt(BOARD_SIZE - moleculeHeight + 1);

        System.out.println("Random Position: (" + indexPosX + ", " + indexPosY + ")");

        // Fill the 15x15 GridPane with Tile objects
        for (int i = 0; i < BOARD_SIZE; i++) {
            for (int j = 0; j < BOARD_SIZE; j++) {
                String tileId = "X"; // Default empty tile
                if (i >= indexPosY && i < indexPosY + moleculeHeight &&
                    j >= indexPosX && j < indexPosX + moleculeWidth) {
                    String moleculeValue = molecule[i - indexPosY][j - indexPosX];
                    if (!moleculeValue.equals("X")) {
                        tileId = moleculeValue; // Assign molecule value
                    }
                }
        
                Tile tile = new Tile(tileId, tileId, "", "", "", "", "", "", "");
                boardTiles[i][j] = tile;
                gridPane.add(tile, j, i);
        
                // Fix: Use final variables for lambda scope
                final int tileX = j;
                final int tileY = i;
        
                if (tile.isEmpty()) {
                    tile.setOnMouseClicked(event -> revealEmptyTiles(tileX, tileY));
                }
            }
        }

        this.boardScene = new Scene(gridPane, BOARD_SIZE * TILE_SIZE, BOARD_SIZE * TILE_SIZE);
    }

    private void revealEmptyTiles(int x, int y) {
        if (x < 0 || y < 0 || x >= BOARD_SIZE || y >= BOARD_SIZE) return;
        Tile tile = boardTiles[y][x];

        if (tile.isRevealed() || !tile.isEmpty()) return;

        tile.revealTile();

        // Fade-in effect
        FadeTransition fadeIn = new FadeTransition(Duration.seconds(0.3), tile);
        fadeIn.setFromValue(0);
        fadeIn.setToValue(1);
        fadeIn.play();

        //REVEAL ADJACENT EMPTY TILES RECURSIVELLY
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
}