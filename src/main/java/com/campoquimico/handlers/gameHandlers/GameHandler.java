package com.campoquimico.handlers.gameHandlers;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;

public class GameHandler {

    private static GameHandler instance; //SINGLETON
    private BooleanProperty showNextButton;
    private IntegerProperty sequentialModeId;

    public IntegerProperty sequentialModeId() {
        return sequentialModeId;
    }
    public int getSequentialId() {
        return this.sequentialModeId.get();
    }
    public void setSequentialId(int id) {
        this.sequentialModeId.set(id);
    }

    public BooleanProperty showNextProperty() {
        return showNextButton;
    }
    public boolean getNextButton() {
        return this.showNextButton.get();
    }
    public void setNextButton(boolean showButton) {
        this.showNextButton.set(showButton);
    }

    //PRIVATE CONSTRUCTOR TO PREVENT MULTI-INSTANTIATING
    private GameHandler() {
        this.showNextButton = new SimpleBooleanProperty(false);
        this.sequentialModeId = new SimpleIntegerProperty(0);
    }

    //GET SINGLETON INSTANCE
    public static GameHandler getInstance() {
        if (instance == null) {
            instance = new GameHandler();
        }
        return instance;
    }
}
