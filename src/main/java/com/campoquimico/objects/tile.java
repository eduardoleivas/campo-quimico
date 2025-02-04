package com.campoquimico.objects;

import javafx.event.EventHandler;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.TextInputDialog;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

import java.util.Optional;

import com.campoquimico.EnvVariables;

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
    private String molecule;
    private Rectangle overlay;
    private boolean isEmpty;
    private boolean isRevealed = false;

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
    public void setMolecule(String molecule) {
        this.molecule = molecule;
    }
    public boolean isEmpty() {
        return this.isEmpty;
    }
    public boolean isRevealed() {
        return this.isRevealed;
    }

    //CONSTRUCTOR
    public Tile(String atomId, String symbol, String name, 
                String tip1, String tip2, String tip3, 
                String tip4, String tip5, String tip6, String molecule) {
        this.atomId = atomId;
        this.symbol = symbol;
        this.name = name;
        this.tip1 = tip1;
        this.tip2 = tip2;
        this.tip3 = tip3;
        this.tip4 = tip4;
        this.tip5 = tip5;
        this.tip6 = tip6;
        this.isEmpty = atomId.equals("X");;

        // White background
        Rectangle background = new Rectangle(EnvVariables.TILE_SIZE, EnvVariables.TILE_SIZE);
        background.setFill(Color.WHITE);
        background.setStroke(Color.BLACK);
        background.setStrokeWidth(1);

        //ADD SYMBOL TO THE TILE, OR SET IT AS EMPTY
        Text text = new Text();
        if (!isEmpty) {
            text.setText(symbol);
            text.setFont(new Font(20));
            text.setFill(Color.BLACK);
        }

        //OVERLAY RECTANGLE
        overlay = new Rectangle(EnvVariables.TILE_SIZE, EnvVariables.TILE_SIZE);
        overlay.setFill(Color.BLUE);
        overlay.setStroke(Color.BLACK);
        overlay.setStrokeWidth(1);

        //HIDE RECTANGLE ON-CLICK
        this.setOnMouseClicked(event -> revealTile(molecule));

        // Stack everything properly
        getChildren().addAll(background, text, overlay);
    }

    public void revealTile(String molecule) {
        //System.out.println("Revealed: " + this.isRevealed + " | " + "Empty: " + this.isEmpty);

        if (!this.isRevealed) {
            this.isRevealed = true;
            getChildren().remove(overlay);
            return;
        }

        System.out.println(molecule);

        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Adivinhe");
        dialog.setHeaderText("Qual é o nome desta molécula?");
        dialog.setContentText("Insira sua Resposta:");

        // Show dialog and wait for user input
        Optional<String> result = dialog.showAndWait();

        // Check if input was provided
        if (result.isPresent()) {
            String answer = result.get().trim(); // Get and trim input

            // Check correctness
            if (answer.equalsIgnoreCase(molecule)) {
                showAlert(AlertType.INFORMATION, "Certo!", "Sua resposta está certa! A molécula é: " + molecule);
            } else {
                showAlert(AlertType.WARNING, "Errado!", "Sua resposta está errada! Tente novamente.");
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
