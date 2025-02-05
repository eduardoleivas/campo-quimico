package com.campoquimico.handlers.gameHandlers;

import java.io.File;
import java.net.URISyntaxException;

import com.campoquimico.handlers.optionsHandlers.OptionsHandler;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;

public class GameHandler {

    private static GameHandler instance; //SINGLETON
    private BooleanProperty showNextButton;

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
    }

    //GET SINGLETON INSTANCE
    public static GameHandler getInstance() {
        if (instance == null) {
            instance = new GameHandler();
        }
        return instance;
    }
}
