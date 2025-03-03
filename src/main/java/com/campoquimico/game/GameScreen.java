package com.campoquimico.game;

import java.util.List;
import java.util.Optional;
import java.util.Random;
import javafx.animation.FadeTransition;
import javafx.animation.PauseTransition;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextInputDialog;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import com.campoquimico.objects.Tile;
import com.campoquimico.handlers.gameHandlers.GameHandler;
import com.campoquimico.handlers.optionsHandlers.OptionsHandler;
import com.campoquimico.handlers.resourceHandlers.ResourceHandler;
import com.campoquimico.EnvVariables;
import com.campoquimico.database.DatabaseReader;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class GameScreen {
    private Scene boardScene;
    private static final Random random = new Random();
    private GridPane gridPane = new GridPane();
    private Stage boardStage;
    private List<String> actualMolecule;
    private Tile[][] boardTiles = new Tile[EnvVariables.BOARD_SIZE][EnvVariables.BOARD_SIZE];
    ResourceHandler resourceHandler = new ResourceHandler();

    public List<String> getActualMolecule() {
        return actualMolecule;
    }

    public void setActualMolecule(List<String> molecule) {
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

    public GameScreen(String[][] molecule, List<String> actualMolecule, Stage boardStage, Stage primaryStage, int sequentialModeId) {
        this.boardStage = boardStage;
        int moleculeWidth = molecule[0].length;
        int moleculeHeight = molecule.length;
        DatabaseReader dbReader = new DatabaseReader(OptionsHandler.getInstance().getDatabase());

        List<List<String>> atomInfos = dbReader.getInfos(sequentialModeId);
        
        // RANDOM INDEX GENERATORS
        int indexPosX = random.nextInt(EnvVariables.BOARD_SIZE - moleculeWidth + 1);
        int indexPosY = random.nextInt(EnvVariables.BOARD_SIZE - moleculeHeight + 1);

        System.out.println("Random Position: (" + indexPosX + ", " + indexPosY + ")");

        // FILLS THE GRIDPANE WITH TILES
        for (int i = 0; i < EnvVariables.BOARD_SIZE; i++) {
            for (int j = 0; j < EnvVariables.BOARD_SIZE; j++) {
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
                    String symbol;
                    switch (tileId) {
                        case "-":
                            symbol = "-";
                            break;
                        case "_":
                            symbol = "-";
                            break;
                        case "--":
                            symbol = "=";
                            break;
                        case "__":
                            symbol = "=";
                            break;
                        case "=":
                            symbol = "=";
                            break;
                        case "---":
                            symbol = "≡";
                            break;
                        case "___":
                            symbol = "≡";
                            break;
                        case "≡":
                            symbol = "≡";
                            break;
                        case "|":
                            symbol = "|";
                            break;
                        case "||":
                            symbol = "‖";
                            break;
                        case "|||":
                            symbol = "⦀";
                            break;
                        default:
                            symbol = tileId;
                            break;
                    }
                    tile = new Tile(tileId, symbol, "", "", "", "", "", "", "", actualMolecule);
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

        //INGAME POINTS
        VBox scoreBox = new VBox(20);
        scoreBox.setAlignment(Pos.CENTER);
        scoreBox.setPrefSize(145, 200);
        scoreBox.setStyle("-fx-background-color: lightgray; -fx-padding: 10; -fx-border-color: black;");

        Label title = new Label("Pontos da Rodada");
        Label tips = new Label("Dicas Usadas: ");
        Label clicks = new Label("Blocos Abertos: ");
        Label wrong = new Label("Tentativas Erradas: ");
        Label points = new Label("Pontuação: ");

        title.setStyle("-fx-font-weight: bold;");
        points.setStyle("-fx-font-weight: bold;");

        scoreBox.getChildren().addAll(title, tips, clicks, wrong, points);
        scoreBox.setVisible(false);

        //CREATE "NEXT" BUTTON
        Button nextButton = new Button("Próximo");
        nextButton.setVisible(false);
        GameHandler.getInstance().showNextProperty().addListener((obs, oldVal, newVal) -> {
            GameHandler.getInstance().setRoundFinished(true);
            
            //DEFINE POINTS
            tips.setText("Dicas Usadas: " + GameHandler.getInstance().getScoreTips());
            clicks.setText("Blocos Abertos: " + GameHandler.getInstance().getScoreClicks());
            wrong.setText("Tentativas Erradas: " + GameHandler.getInstance().getScoreWrong());
            int roundPoints = (500 - (5 * GameHandler.getInstance().getScoreTips()) - (10 * GameHandler.getInstance().getScoreWrong()) - GameHandler.getInstance().getScoreClicks());
            if (roundPoints < 0) {
                roundPoints = 100;
            }
            points.setText("Pontuação: " + roundPoints);

            //OPEN ALL BOARD
            for (Tile[] row : boardTiles) {
                for (Tile tile : row) {
                    tile.getChildren().remove(tile.overlay);
                }
            }
            

            //SET HIDDENS VISIBLE
            scoreBox.setVisible(newVal.booleanValue());
            nextButton.setVisible(newVal.booleanValue());
            System.out.println("ShowButton Updated: " + newVal);
        });
        nextButton.setOnAction(event -> {
            GameHandler.getInstance().resetScore();
            GameHandler.getInstance().setNextButton(false);
            GameHandler.getInstance().setRoundFinished(false);

            if (boardStage != null && boardStage.isShowing()) {
                boardStage.close(); //CLOSE CURRENT GAME BEFORE OPENING NEXT ONE
            }

            //GET NEW MOLECULE ID
            int newMoleculeId;
            DatabaseReader newDbReader = new DatabaseReader(OptionsHandler.getInstance().getDatabase());
            if(OptionsHandler.getInstance().isRandomMode()) {
                if(newDbReader.checkSingleMolecule()) {
                    newMoleculeId = sequentialModeId;
                } else {
                    do {
                        newMoleculeId = new DatabaseReader(OptionsHandler.getInstance().getDatabase()).getRandomMolecule();
                    }while(newMoleculeId == sequentialModeId);
                }
            } else {
                GameHandler.getInstance().setSequentialId(GameHandler.getInstance().getSequentialId() + 1);
                newMoleculeId = GameHandler.getInstance().getSequentialId();
            }

            if(!newDbReader.checkMoleculeExists(newMoleculeId) || newMoleculeId == sequentialModeId) {
                showAlert(AlertType.INFORMATION, "Parabéns!", "Você completou todas as moléculas deste Banco de Dados.");
                boardStage.close();
                GameHandler.getInstance().setSequentialId(0);
                if(!primaryStage.isShowing()) {
                    primaryStage.show();
                }
                return;
            }

            //OPEN NEW GAME WINDOW
            Stage newBoardStage = new Stage();
            newBoardStage.setTitle("JOGO");
            String[][] newMolecule = newDbReader.processMolecule(newMoleculeId);
            GameHandler.getInstance().setGamemode(newDbReader.getMoleculeGamemode(newMoleculeId));
            GameScreen newGameScreen = new GameScreen(newMolecule, newDbReader.getMoleculeName(newMoleculeId), newBoardStage, primaryStage, newMoleculeId);
            GameHandler.getInstance().setGameScreen(newGameScreen);
            newBoardStage.setScene(newGameScreen.getGameScreen());
            newBoardStage.setOnCloseRequest(closeEvent -> onCloseGame(closeEvent, primaryStage));
            newBoardStage.show();
        });

        //INGAME LOGO
        Image image = new Image(resourceHandler.getResourcePath("/img/logo_bnw.png"), true);
        ImageView imageView = new ImageView(image);

        imageView.setSmooth(true);
        imageView.setFitWidth(200);
        imageView.setPreserveRatio(true);

        Button molTipButton = new Button("Dica da Molécula");
        molTipButton.setPrefWidth(130);
        molTipButton.setOnAction(molTipAction -> {
            String tip = dbReader.getMoleculeTip(sequentialModeId);
            showAlert(AlertType.INFORMATION, "Dica da Molécula", tip);
        });

        Button guessMolButton = new Button("Adivinhar Molécula");
        guessMolButton.setPrefWidth(130);
        guessMolButton.setOnAction(molTipAction -> {
            TextInputDialog dialog = new TextInputDialog();
            dialog.setTitle("Adivinhe");
            dialog.setHeaderText("Qual é o nome desta molécula?");
            dialog.setContentText("Insira sua Resposta:");

            Optional<String> result = dialog.showAndWait();
            if (result.isPresent()) {
                String answer = result.get().trim(); //RECOGNIZE ANSWER TRIMMING SPACES
                boolean isCorrect = false;
            
                for (String guessedMol : actualMolecule) { //LOOP THROUGH THE MOLECULE LIST
                    if (answer.equalsIgnoreCase(guessedMol) || answer.equalsIgnoreCase(guessedMol.replaceAll("\\s+", ""))) {
                        isCorrect = true;
                        break;
                    }
                }
            
                if (isCorrect) {
                    showAlert(AlertType.INFORMATION, "Certo!", "Sua resposta está certa! A molécula é:\n" + String.join(",\n", actualMolecule));
                    GameHandler.getInstance().setNextButton(true); //ENABLE NEXT
                } else {
                    showAlert(AlertType.WARNING, "Errado!", "Sua resposta está errada! Tente novamente.");
                    GameHandler.getInstance().addScoreWrong(1); //COUNT +1 WRONG ATTEMPT
                }
            }
        });

        //GAMEMODE = OPEN ALL ATOMS ONLY
        if(GameHandler.getInstance().getGamemode() == 2) {
            guessMolButton.setVisible(false);
        };

        //LAYOUT USING ANCHORPANE
        AnchorPane layout = new AnchorPane();
        layout.getChildren().addAll(gridPane, nextButton, imageView, molTipButton, guessMolButton, scoreBox);

        //POSITIONING THE ELEMENTS
        AnchorPane.setTopAnchor(gridPane, 5.0);
        AnchorPane.setLeftAnchor(gridPane, 5.0);
        AnchorPane.setRightAnchor(gridPane, 0.0);

        AnchorPane.setBottomAnchor(nextButton, 10.0);
        AnchorPane.setLeftAnchor(nextButton, ((700.0 - 465.0)/2) + 435); // Centered

        AnchorPane.setTopAnchor(imageView, 10.0);
        AnchorPane.setLeftAnchor(imageView, 465.0 + 18);

        AnchorPane.setTopAnchor(molTipButton, 130.0);
        AnchorPane.setLeftAnchor(molTipButton, ((700.0 - 465.0)/2) + 400);

        AnchorPane.setTopAnchor(guessMolButton, 170.0);
        AnchorPane.setLeftAnchor(guessMolButton, ((700.0 - 465.0)/2) + 400);

        AnchorPane.setBottomAnchor(scoreBox, 60.0);
        AnchorPane.setLeftAnchor(scoreBox, 510.0);

        this.boardScene = new Scene(layout, 710, 475);
    }

    //METHOD TO RECURSIVELY REVEAL EMPTY TILES AROUND
    private void revealEmptyTiles(int x, int y) {
        if (x < 0 || y < 0 || x >= EnvVariables.BOARD_SIZE || y >= EnvVariables.BOARD_SIZE) return;
        Tile tile = boardTiles[y][x];

        if (tile.isRevealed() || !tile.isEmpty()) return;

        tile.revealTile(null, actualMolecule, tile);

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

    public void checkBoardStatus() {
        boolean allOverlaysRemoved = true;

        for (Tile[] row : boardTiles) {
            for (Tile tile : row) {
                if (!tile.isEmpty() && tile.hasOverlay()) {
                    allOverlaysRemoved = false;
                    return;
                }
            }
        }

        if (allOverlaysRemoved) {
            GameHandler.getInstance().setNextButton(true);
        }
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
