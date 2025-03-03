package com.campoquimico;

import javafx.application.Application;
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
import com.campoquimico.handlers.resourceHandlers.ResourceHandler;
import com.campoquimico.handlers.buttonHandlers.MainMenuButtonHandler;
import com.campoquimico.handlers.gameHandlers.TutorialHandler;
import com.campoquimico.handlers.optionsHandlers.OptionsHandler;

public class App extends Application {

    private MediaPlayer bgmPlayer;
    ResourceHandler resourceHandler = new ResourceHandler();

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(@SuppressWarnings("exports") Stage primaryStage) {
        
        MainMenuButtonHandler buttonHandler = new MainMenuButtonHandler(primaryStage); //DEFINES THE BUTTON HANDLER
        StackPane mainMenuRoot = createMainMenu(primaryStage, buttonHandler); //DEFINES THE MENU ROOT
        Scene mainMenuScene = new Scene(mainMenuRoot, EnvVariables.MENU_WIDTH, EnvVariables.MENU_HEIGHT); //DEFINES THE MENU SCENE
        
        //LOAD BGM
        Media bgm = new Media(resourceHandler.getResourcePath("/music/menu_bgm.wav"));
        bgmPlayer = new MediaPlayer(bgm);
        bgmPlayer.setCycleCount(MediaPlayer.INDEFINITE);
        bgmPlayer.setVolume(OptionsHandler.getInstance().getVolume());
        bgmPlayer.play();
        bgmPlayer.setOnEndOfMedia(() -> {
            bgmPlayer.seek(bgmPlayer.getStartTime());
            bgmPlayer.play();
        });

        OptionsHandler.getInstance().volumeProperty().addListener((obs, oldVal, newVal) -> {
            bgmPlayer.setVolume(newVal.doubleValue());
            System.out.println("BGM Player Volume Updated: " + newVal);
        });

        primaryStage.setTitle(EnvVariables.APP_NAME);
        primaryStage.setScene(mainMenuScene);
        primaryStage.setResizable(false);
        primaryStage.show();
    }

    //CREATE MENU SCREEN METHOD
    private StackPane createMainMenu(Stage primaryStage, MainMenuButtonHandler buttonHandler) {
        //LOAD BACKGROUND VIDEO
        Media bgVideo = new Media(resourceHandler.getResourcePath("/video/menu_bg.mp4"));
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
        Image image = new Image(resourceHandler.getResourcePath("/img/logo_rainbow.png"), true);
        ImageView imageView = new ImageView(image);

        imageView.setSmooth(true);
        imageView.setFitWidth(430);
        imageView.setPreserveRatio(true);

        //CREATE BUTTONS
        Button startButton = new Button("JOGAR");
        startButton.getStyleClass().add("play_btn");
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
        buttonLayout.getStylesheets().add(resourceHandler.getResourcePath("/styles/buttons.css"));
        buttonLayout.getChildren().addAll(settingsButton, startButton, quitButton);
        HBox.setHgrow(startButton, Priority.ALWAYS);
        HBox.setHgrow(settingsButton, Priority.ALWAYS);
        HBox.setHgrow(quitButton, Priority.ALWAYS);

        //SECONDARY BUTTONS
        Button howToPlay = new Button("COMO JOGAR");
        howToPlay.getStyleClass().add("play_btn");
        howToPlay.setPrefWidth(200);
        howToPlay.setOnAction(event -> {
            if(TutorialHandler.getInstance().isTutorialOpen() == false) {
                buttonHandler.handleHowToPlayButton(event);
                TutorialHandler.getInstance().setTutorialOpen(true);
            }
        });

        Button doItYourself = new Button("FAÇA VOCÊ MESMO");
        doItYourself.getStyleClass().add("play_btn");
        doItYourself.setPrefWidth(200);
        //doItYourself.setOnAction(event -> buttonHandler.handleDoItYourselfButton(event));

        //BUTTON GRID 2
        HBox lowerButtonLayout = new HBox(20);
        lowerButtonLayout.getStylesheets().add(resourceHandler.getResourcePath("/styles/buttons.css"));
        lowerButtonLayout.getChildren().addAll(howToPlay, doItYourself);
        HBox.setHgrow(howToPlay, Priority.ALWAYS);
        HBox.setHgrow(doItYourself, Priority.ALWAYS);

        //CREATE ANCHORPANE LAYOUT
        AnchorPane layout = new AnchorPane();
        layout.getChildren().addAll(imageView, buttonLayout, lowerButtonLayout);

        //POSITIONING IN ANCHORPANE
        PositionHandler positionHandler = new PositionHandler();
        AnchorPane.setTopAnchor(imageView, 20.0);
        AnchorPane.setLeftAnchor(imageView, positionHandler.centerX(imageView));

        AnchorPane.setBottomAnchor(buttonLayout, 120.0);
        AnchorPane.setLeftAnchor(buttonLayout, positionHandler.centerX(buttonLayout.getChildren(), 20));

        AnchorPane.setBottomAnchor(lowerButtonLayout, 40.0);
        AnchorPane.setLeftAnchor(lowerButtonLayout, positionHandler.centerX(lowerButtonLayout.getChildren(), 20));

        StackPane root = new StackPane();
        root.getChildren().addAll(mediaView, layout);

        return root;
    }
}
