package com.campoquimico.handlers.gameHandlers;

import com.campoquimico.game.GameScreen;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;

public class GameHandler {

    private static GameHandler instance; //SINGLETON
    private GameScreen actualGameScreen;
    private BooleanProperty showNextButton;
    private BooleanProperty isRoundFinished;
    private IntegerProperty sequentialModeId;
    private IntegerProperty gamemode;
    private IntegerProperty scoreTips;
    private IntegerProperty scoreClicks;
    private IntegerProperty scoreWrong;

    public GameScreen getGameScreen() {
        return this.actualGameScreen;
    }
    public void setGameScreen(GameScreen gameScreen) {
        this.actualGameScreen = gameScreen;
    }

    public BooleanProperty showNextProperty() {
        return showNextButton;
    }
    public boolean getNextButton() {
        return this.showNextButton.get();
    }
    public void setNextButton(boolean showNextButton) {
        this.showNextButton.set(showNextButton);
    }

    public BooleanProperty roundFinishedProperty() {
        return isRoundFinished;
    }
    public boolean isRoundFinished() {
        return this.isRoundFinished.get();
    }
    public void setRoundFinished(boolean isFinished) {
        this.isRoundFinished.set(isFinished);
    }

    public IntegerProperty sequentialModeId() {
        return sequentialModeId;
    }
    public int getSequentialId() {
        return this.sequentialModeId.get();
    }
    public void setSequentialId(int id) {
        this.sequentialModeId.set(id);
    }

    public IntegerProperty gamemode() {
        return gamemode;
    }
    public int getGamemode() {
        return this.gamemode.get();
    }
    public void setGamemode(int id) {
        this.gamemode.set(id);
    }

    public IntegerProperty scoreTips() {
        return scoreTips;
    }
    public int getScoreTips() {
        return this.scoreTips.get();
    }
    public void setScoreTips(int id) {
        this.scoreTips.set(id);
    }
    public void addScoreTips(int amount) {
        int now = this.scoreTips.get();
        this.scoreTips.set(now += amount);
    }

    public IntegerProperty scoreClicks() {
        return scoreClicks;
    }
    public int getScoreClicks() {
        return this.scoreClicks.get();
    }
    public void setScoreClicks(int id) {
        this.scoreClicks.set(id);
    }
    public void addScoreClicks(int amount) {
        int now = this.scoreClicks.get();
        this.scoreClicks.set(now += amount);
    }

    public IntegerProperty scoreWrong() {
        return scoreWrong;
    }
    public int getScoreWrong() {
        return this.scoreWrong.get();
    }
    public void setScoreWrong(int id) {
        this.scoreWrong.set(id);
    }
    public void addScoreWrong(int amount) {
        int now = this.scoreWrong.get();
        this.scoreWrong.set(now += amount);
    }

    public void resetScore() {
        this.scoreClicks.set(0);
        this.scoreTips.set(0);
        this.scoreWrong.set(0);
    }

    //PRIVATE CONSTRUCTOR TO PREVENT MULTI-INSTANTIATING
    private GameHandler() {
        this.actualGameScreen = null;
        this.showNextButton = new SimpleBooleanProperty(false);
        this.isRoundFinished = new SimpleBooleanProperty(false);
        this.sequentialModeId = new SimpleIntegerProperty(0);
        this.gamemode = new SimpleIntegerProperty(1);
        this.scoreTips = new SimpleIntegerProperty(0);
        this.scoreClicks = new SimpleIntegerProperty(0);
        this.scoreWrong = new SimpleIntegerProperty(0);
    }

    //GET SINGLETON INSTANCE
    public static GameHandler getInstance() {
        if (instance == null) {
            instance = new GameHandler();
        }
        return instance;
    }
}
