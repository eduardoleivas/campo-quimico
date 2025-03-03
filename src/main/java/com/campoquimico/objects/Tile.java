package com.campoquimico.objects;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import com.campoquimico.EnvVariables;
import com.campoquimico.handlers.gameHandlers.GameHandler;

public class Tile extends StackPane {
    private String atomId;
    private String symbol;
    private String name;
    private String tip1;
    private String tip2;
    private String tip3;
    private String tip4;
    private String tip5;
    private String tip6;
    private List<String> molecule;
    private Text text;
    public Rectangle overlay;
    private boolean isEmpty;
    private boolean isCounted;
    private boolean isRevealed = false;
    private boolean isGuessed = false;

    public String getAtomId() {
        return atomId;
    }
    public void setSAtomId(String atomId) {
        this.atomId = atomId;
    }
    public String getSymbol() {
        return symbol;
    }
    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getTip1() {
        return tip1;
    }
    public void setTip1(String tip1) {
        this.tip1 = tip1;
    }
    public String getTip2() {
        return tip2;
    }
    public void setTip2(String tip2) {
        this.tip2 = tip2;
    }
    public String getTip3() {
        return tip3;
    }
    public void setTip3(String tip3) {
        this.tip3 = tip3;
    }
    public String getTip4() {
        return tip4;
    }
    public void setTip4(String tip4) {
        this.tip4 = tip4;
    }
    public String getTip5() {
        return tip5;
    }
    public void setTip5(String tip5) {
        this.tip5 = tip5;
    }
    public String getTip6() {
        return tip6;
    }
    public void setTip6(String tip6) {
        this.tip6 = tip6;
    }
    public void setMolecule(List<String> molecule) {
        this.molecule = molecule;
    }
    public boolean isEmpty() {
        return this.isEmpty;
    }
    public boolean isRevealed() {
        return this.isRevealed;
    }
    public boolean isCounted() {
        return this.isCounted;
    }
    public boolean hasOverlay() {
        return this.getChildren().contains(overlay);
    }

    //CONSTRUCTOR
    public Tile(String atomId, String symbol, String name, 
                String tip1, String tip2, String tip3, 
                String tip4, String tip5, String tip6, List<String> molecule) {
        this.atomId = atomId;
        this.symbol = symbol;
        this.name = name;
        this.tip1 = tip1;
        this.tip2 = tip2;
        this.tip3 = tip3;
        this.tip4 = tip4;
        this.tip5 = tip5;
        this.tip6 = tip6;
        this.isEmpty = atomId.equals("X");
        this.isCounted = false;

        //BACKGROUND
        Rectangle background = new Rectangle(EnvVariables.TILE_SIZE, EnvVariables.TILE_SIZE);
        background.setFill(Color.web("#CAD2CA"));
        background.setStroke(Color.BLACK);
        background.setStrokeWidth(1);

        //ADD SYMBOL TO THE TILE, OR SET IT AS EMPTY
        text = new Text();
        if (!isEmpty) {
            background.setFill(Color.web("#F2F4F2"));
            text.setText(symbol);
            text.setFill(Color.BLACK);
        }

        if(symbol != "") {
            switch(symbol) {
                case "|":
                    text.setFont(new Font(10));
                    text.setStyle("-fx-font-weight: bold;");
                    break;
                case "‖":
                    text.setFont(new Font(11));
                    text.setStyle("-fx-font-weight: bold;");
                    break;
                case "-":
                    text.setTranslateY(-2);
                    break;
                case "=":
                    text.setTranslateY(-1);
                    break;
                case "≡":
                    text.setTranslateY(-2.5);
                    break;
            }
        }

        text = resizeTextToFit(text);

        //OVERLAY RECTANGLE
        overlay = new Rectangle(EnvVariables.TILE_SIZE, EnvVariables.TILE_SIZE);
        overlay.setFill(Color.DARKGREEN);
        overlay.setStroke(Color.BLACK);
        overlay.setStrokeWidth(1);

        //HIDE RECTANGLE ON-CLICK
        this.setOnMouseClicked(event -> revealTile(event, molecule, this));

        //STACK LAYERS
        getChildren().addAll(background, text, overlay);
        StackPane.setAlignment(text, Pos.CENTER);
    }

    private Text resizeTextToFit(Text text) {
        double maxWidth = EnvVariables.TILE_SIZE - EnvVariables.PADDING;
        double maxHeight = EnvVariables.TILE_SIZE - EnvVariables.PADDING;
        double fontSize = EnvVariables.MAX_FONT_SIZE;
        
        text.setFont(new Font(fontSize));
        
        while ((text.getBoundsInLocal().getWidth() > maxWidth || text.getBoundsInLocal().getHeight() > maxHeight) && fontSize > EnvVariables.MIN_FONT_SIZE) {
            text.setFont(new Font(--fontSize));
        }

        return text;
    }

    public void revealTile(MouseEvent event, List<String> actualMolecule, Tile tile) {
        if(this.isEmpty) {
            this.isRevealed = true;
            getChildren().remove(overlay);
            return;
        }

        if (!this.isRevealed) {          
            if(!this.isGuessed && !this.isEmpty && this.name == "") {
                this.isRevealed = true;
                getChildren().remove(overlay);
                if(GameHandler.getInstance().getGamemode() != 1) {
                    GameHandler.getInstance().getGameScreen().checkBoardStatus();
                }
                return;
            } else if (this.tip1 == "") {
                this.isRevealed = true;
                getChildren().remove(overlay);
                if(GameHandler.getInstance().getGamemode() != 1) {
                    GameHandler.getInstance().getGameScreen().checkBoardStatus();
                }
                return;
            }

            if(!this.isGuessed && !this.isEmpty) {
                overlay.setFill(Color.web("#F2F4F2"));
                GameHandler.getInstance().addScoreClicks(1);
                Stage infoStage = new Stage();
                infoStage.setTitle("Adivinhe o Átomo");
                VBox vbox = new VBox(10);
                vbox.setPadding(new Insets(10));
                vbox.setAlignment(Pos.CENTER);

                Label symbolLabel = new Label("?");
                symbolLabel.setFont(Font.font("Arial", FontWeight.BOLD, 24));

                Label nameLabel = new Label("?");
                nameLabel.setFont(Font.font("Arial", FontWeight.NORMAL, 14));

                VBox tipsBox = new VBox(5);
                for (String tip : Arrays.asList(this.tip1, this.tip2, this.tip3, this.tip4, this.tip5, this.tip6)) {
                    TextField tipField = new TextField(tip);
                    tipField.setEditable(false);
                    tipsBox.getChildren().add(tipField);
                }

                TextField answerField = new TextField();
                answerField.setPromptText("Digite sua resposta");

                Button submitButton = new Button("Verificar Resposta");
                submitButton.setOnAction(e -> {
                    String answer = answerField.getText().trim();
                    if (this.name.equalsIgnoreCase(answer)) {
                        showAlert(Alert.AlertType.INFORMATION, "Parabéns", "Você acertou o Átomo!");
                        getChildren().remove(overlay);
                        if(GameHandler.getInstance().getGamemode() != 1) {
                            GameHandler.getInstance().getGameScreen().checkBoardStatus();
                        }
                        this.isRevealed = true;
                        this.isGuessed = true;
                        infoStage.close();
                    } else {
                        showAlert(AlertType.WARNING, "Errado!", "Sua resposta está errada! Tente novamente.");
                        GameHandler.getInstance().addScoreWrong(1);
                    }
                });
                vbox.getChildren().addAll(symbolLabel, nameLabel, new Label("Dicas:"), tipsBox, answerField, submitButton);
                Scene scene = new Scene(vbox, 300, 400);
                infoStage.setScene(scene);
                //OVERLAY
                scene.setOnMouseEntered(mouseEnterEvent -> {
                    tile.overlay.setFill(Color.LIGHTGREEN);
                    tile.text.setFill(Color.GREEN);
                });
                
                scene.setOnMouseExited(mouseLeaveEvent -> {
                    tile.overlay.setFill(Color.web("#F2F4F2"));
                    tile.text.setFill(Color.BLACK);
                });
                infoStage.show();
            } else {
                overlay.setFill(Color.web("#CAD2CA"));
            }
            return;
        }

        if(event == null) {
            return;
        }

        if(this.isGuessed) {
            //RIGHT CLICK HANDLER
            if (event.getButton() == MouseButton.SECONDARY && !this.isEmpty) {
                Stage infoStage = new Stage();
                infoStage.setTitle("Informações do Átomo");
                VBox vbox = new VBox(10);
                vbox.setPadding(new Insets(10));
                vbox.setAlignment(Pos.CENTER);

                Label symbolLabel = new Label(this.symbol);
                symbolLabel.setFont(Font.font("Arial", FontWeight.BOLD, 24));

                Label nameLabel = new Label(this.name);
                nameLabel.setFont(Font.font("Arial", FontWeight.NORMAL, 14));

                VBox tipsBox = new VBox(5);
                for (String tip : Arrays.asList(this.tip1, this.tip2, this.tip3, this.tip4, this.tip5, this.tip6)) {
                    TextField tipField = new TextField(tip);
                    tipField.setEditable(false);
                    tipsBox.getChildren().add(tipField);
                }

                vbox.getChildren().addAll(symbolLabel, nameLabel, new Label("Dicas:"), tipsBox);
                Scene scene = new Scene(vbox, 300, 350);
                infoStage.setScene(scene);
                //OVERLAY
                scene.setOnMouseEntered(mouseEnterEvent -> {
                    tile.overlay.setFill(Color.LIGHTGREEN);
                    tile.text.setFill(Color.GREEN);
                });
                
                scene.setOnMouseExited(mouseLeaveEvent -> {
                    tile.overlay.setFill(Color.web("#F2F4F2"));
                    tile.text.setFill(Color.BLACK);
                });
                infoStage.show();
                infoStage.show();

                //CASO SEJA A PRIMEIRA VEZ VENDO AS DICAS
                if(!this.isCounted) {
                    GameHandler.getInstance().addScoreTips(1);
                    this.isCounted = true; //DEFINE DICA COMO JÁ VISTA
                }

            //LEFT CLICK HANDLER
            } else if (event.getButton() == MouseButton.PRIMARY && !this.isEmpty && !GameHandler.getInstance().isRoundFinished() && (GameHandler.getInstance().getGamemode()!= 2)) {
                TextInputDialog dialog = new TextInputDialog();
                dialog.setTitle("Adivinhe");
                dialog.setHeaderText("Qual é o nome desta molécula?");
                dialog.setContentText("Insira sua Resposta:");

                Optional<String> result = dialog.showAndWait();
                if (result.isPresent()) {
                    String answer = result.get().trim(); // RECONHECER COMPOSTO SEM ESPAÇO
                    boolean isCorrect = false;
                
                    for (String guessedMol : actualMolecule) { // Loop through the list
                        if (answer.equalsIgnoreCase(guessedMol) || answer.equalsIgnoreCase(guessedMol.replaceAll("\\s+", ""))) {
                            isCorrect = true;
                            break;
                        }
                    }
                
                    if (isCorrect) {
                        showAlert(AlertType.INFORMATION, "Certo!", "Sua resposta está certa! A molécula é:\n" + String.join(",\n", actualMolecule));
                        GameHandler.getInstance().setNextButton(true);
                    } else {
                        showAlert(AlertType.WARNING, "Errado!", "Sua resposta está errada! Tente novamente.");
                        GameHandler.getInstance().addScoreWrong(1); // ADD TENTATIVA ERRADA
                    }
                }
            }
        }
    }

    private void showAlert(AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
