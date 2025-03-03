package com.campoquimico.handlers.gameHandlers;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;

public class TutorialHandler {
    private static TutorialHandler instance; //SINGLETON
    private BooleanProperty isTutorialOpen;

    public BooleanProperty isTutorialOpenProperty() {
        return isTutorialOpen;
    }
    public boolean isTutorialOpen() {
        return this.isTutorialOpen.get();
    }
    public void setTutorialOpen(boolean value) {
        this.isTutorialOpen.set(value);
    }
    
    //PRIVATE CONSTRUCTOR TO PREVENT MULTI-INSTANTIATING
    private TutorialHandler() {
        this.isTutorialOpen = new SimpleBooleanProperty(false);
    }

    //GET SINGLETON INSTANCE
    public static TutorialHandler getInstance() {
        if (instance == null) {
            instance = new TutorialHandler();
        }
        return instance;
    }
}
