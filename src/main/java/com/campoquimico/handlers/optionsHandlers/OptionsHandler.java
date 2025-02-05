package com.campoquimico.handlers.optionsHandlers;

import java.io.File;
import java.net.URISyntaxException;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;

public class OptionsHandler {
    
    private static OptionsHandler instance; //SINGLETON
    private DoubleProperty volume;
    private boolean randomMode;
    private String database;

    
    public DoubleProperty volumeProperty() {
        //GET VOLUME PROPERTY FOR BINDING
        return volume;
    }
    public double getVolume() {
        //GET VOLUME VALUE
        return volume.get();
    }
    public void setVolume(double volume) {
        //SET VOLUME VALUE
        this.volume.set(volume);
    }
    public boolean isRandomMode() {
        return randomMode;
    }
    public void setRandomMode(boolean randomMode) {
        this.randomMode = randomMode;
    }
    public String getDatabase() {
        return this.database;
    }
    public void setDatabase(String filepath) {
        this.database = filepath;
    }

    public void resetDatabase() {
        try {
            this.database = new File(getClass().getResource("/database/default.xlsx").toURI()).getAbsolutePath();
        } catch (Exception e) {
            System.out.println("Error resetting database: " + e.getMessage());
        }
    }

    //PRIVATE CONSTRUCTOR TO PREVENT MULTI-INSTANTIATING
    private OptionsHandler() {
        try {
            this.volume = new SimpleDoubleProperty(0.5);
            this.randomMode = true;
            this.database = new File(getClass().getResource("/database/default.xlsx").toURI()).getAbsolutePath();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

    //GET SINGLETON INSTANCE
    public static OptionsHandler getInstance() {
        if (instance == null) {
            instance = new OptionsHandler();
        }
        return instance;
    }

}