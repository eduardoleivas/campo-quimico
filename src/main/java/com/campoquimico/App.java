package com.campoquimico;

import javafx.application.Application;
import javafx.scene.ImageCursor;
import javafx.scene.Scene;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import com.campoquimico.handlers.positionHandlers.PositionHandler;
import com.campoquimico.handlers.buttonHandlers.MainMenuButtonHandler;

public class App extends Application {

    public MediaPlayer bgmPlayer;

    @Override
    public void start(Stage primaryStage) {
        MainMenuButtonHandler buttonHandler = new MainMenuButtonHandler(primaryStage); //DEFINES THE BUTTON HANDLER
        StackPane mainMenuRoot = createMainMenu(primaryStage, buttonHandler); //DEFINES THE MENU ROOT
        Scene mainMenuScene = new Scene(mainMenuRoot, EnvVariables.MENU_WIDTH, EnvVariables.MENU_HEIGHT); //DEFINES THE MENU SCENE

        //LOAD CUSTOM CURSOR
        ImageCursor customCursor = new ImageCursor(new Image(getClass().getResource("/img/cursor.png").toExternalForm(), 256, 256, false, false), 128, 128);
        mainMenuScene.setCursor(customCursor);

        //LOAD BGM
        Media bgm = new Media(getClass().getResource("/music/menu_bgm.wav").toExternalForm());
        bgmPlayer = new MediaPlayer(bgm);
        bgmPlayer.setCycleCount(MediaPlayer.INDEFINITE);
        bgmPlayer.setVolume(0.5);
        bgmPlayer.play();
        bgmPlayer.setOnEndOfMedia(() -> {
            bgmPlayer.seek(bgmPlayer.getStartTime());  // Restart from the beginning
            bgmPlayer.play();  // Play the music again
        });

        primaryStage.setTitle(EnvVariables.APP_NAME);
        primaryStage.setScene(mainMenuScene);
        primaryStage.setResizable(false);
        primaryStage.show();
    }

    //CREATE MENU SCREEN METHOD
    private StackPane createMainMenu(Stage primaryStage, MainMenuButtonHandler buttonHandler) {
        //LOAD BACKGROUND VIDEO
        Media bgVideo = new Media(getClass().getResource("/video/menu_bg.mp4").toExternalForm());
        MediaPlayer bgMediaPlayer = new MediaPlayer(bgVideo);
        MediaView mediaView = new MediaView(bgMediaPlayer);
        bgMediaPlayer.setAutoPlay(true);
        bgMediaPlayer.setOnEndOfMedia(() -> {
            bgMediaPlayer.seek(bgMediaPlayer.getStartTime());
            bgMediaPlayer.play();
        });
        mediaView.setPreserveRatio(false);
        mediaView.setFitHeight(EnvVariables.MENU_HEIGHT);
        mediaView.setFitWidth(EnvVariables.MENU_WIDTH);

        //LOAD IMAGE
        Image image = new Image(getClass().getResource("/img/logo.png").toExternalForm(), true);
        ImageView imageView = new ImageView(image);

        imageView.setSmooth(true);
        imageView.setFitWidth(430);
        imageView.setPreserveRatio(true);

        //CREATE BUTTONS
        Button startButton = new Button("JOGAR");
        startButton.getStyleClass().add("menu_btn");
        startButton.setPrefWidth(100);
        startButton.setOnAction(event -> buttonHandler.handleStartButton(event));

        Button settingsButton = new Button("OPÇÕES");
        settingsButton.getStyleClass().add("menu_btn");
        settingsButton.setPrefWidth(100);
        settingsButton.setOnAction(event -> buttonHandler.handleSettingsButton(event));

        Button quitButton = new Button("SAIR");
        quitButton.getStyleClass().add("menu_btn");
        quitButton.setPrefWidth(100);
        quitButton.setOnAction(event -> buttonHandler.handleQuitButton(event));

        //BUTTON GRID
        HBox buttonLayout = new HBox(20);
        buttonLayout.getStylesheets().add(getClass().getResource("/styles/buttons.css").toExternalForm());
        buttonLayout.getChildren().addAll(startButton, settingsButton, quitButton);
        HBox.setHgrow(startButton, Priority.ALWAYS);
        HBox.setHgrow(settingsButton, Priority.ALWAYS);
        HBox.setHgrow(quitButton, Priority.ALWAYS);

        //CREATE ANCHORPANE LAYOUT
        AnchorPane layout = new AnchorPane();
        layout.getChildren().addAll(imageView, buttonLayout);

        //POSITIONING IN ANCHORPANE
        PositionHandler positionHandler = new PositionHandler();
        AnchorPane.setTopAnchor(imageView, 20.0);
        AnchorPane.setLeftAnchor(imageView, positionHandler.centerX(imageView));

        AnchorPane.setBottomAnchor(buttonLayout, 120.0);
        AnchorPane.setLeftAnchor(buttonLayout, positionHandler.centerX(buttonLayout.getChildren(), 20));

        StackPane root = new StackPane();
        root.getChildren().addAll(mediaView, layout);

        return root;
    }

    public static void main(String[] args) {
        launch(args);
    }
}
