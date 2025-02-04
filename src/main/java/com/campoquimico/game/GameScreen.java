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
    GridPane gridPane = new GridPane();
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

    public GameScreen(String[][] molecule, String actualMolecule) {
        int moleculeWidth = molecule[0].length;
        int moleculeHeight = molecule.length;

        //RANDOM INDEX GENERATORS
        int indexPosX = random.nextInt(BOARD_SIZE - moleculeWidth + 1);
        int indexPosY = random.nextInt(BOARD_SIZE - moleculeHeight + 1);

        System.out.println("Random Position: (" + indexPosX + ", " + indexPosY + ")");

        //FILLS THE GRIDPANE WITH TILES
        for (int i = 0; i < BOARD_SIZE; i++) {
            for (int j = 0; j < BOARD_SIZE; j++) {
                String tileId = "X"; //DEFAULT EMPTY TILE

                if (i >= indexPosY && i < indexPosY + moleculeHeight &&
                    j >= indexPosX && j < indexPosX + moleculeWidth) {

                    String moleculeValue = molecule[i - indexPosY][j - indexPosX];
                    if (!moleculeValue.equals("X")) {
                        tileId = moleculeValue; //ASSIGN MOLECULE VALUE
                    }
                }
        
                //CREATE TILE AND ADD TO GRIDPANE
                Tile tile = new Tile(tileId, tileId, "", "", "", "", "", "", "", actualMolecule);
                boardTiles[i][j] = tile;
                gridPane.add(tile, j, i);
        
                //FINAL VARIABLES FOR LAMBDA SCOPE
                final int tileX = j;
                final int tileY = i;
        
                if (tile.isEmpty()) {
                    tile.setOnMouseClicked(event -> revealEmptyTiles(tileX, tileY));
                }
            }
        }

        this.boardScene = new Scene(gridPane, BOARD_SIZE * TILE_SIZE, BOARD_SIZE * TILE_SIZE);
    }

    //METHOD TO RECURSIVELY REVEAL EMPTY TILES AROUND
    private void revealEmptyTiles(int x, int y) {
        if (x < 0 || y < 0 || x >= BOARD_SIZE || y >= BOARD_SIZE) return;
        Tile tile = boardTiles[y][x];

        if (tile.isRevealed() || !tile.isEmpty()) return;

        tile.revealTile(actualMolecule);

        //FADE-IN EFFECT FOR THE TILES
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