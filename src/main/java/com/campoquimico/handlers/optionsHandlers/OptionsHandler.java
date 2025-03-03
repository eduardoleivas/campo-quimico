package com.campoquimico.handlers.optionsHandlers;

import com.campoquimico.handlers.resourceHandlers.ResourceHandler;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;

public class OptionsHandler {
    
    private static OptionsHandler instance; //SINGLETON
    private DoubleProperty volume;
    private boolean randomMode;
    private String database;
    ResourceHandler resourceHandler = new ResourceHandler();

    
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
            this.database = resourceHandler.getExcelFilePath("/database/default.xlsx");
        } catch (Exception e) {
            System.out.println("Error resetting database: " + e.getMessage());
        }
    }

    //PRIVATE CONSTRUCTOR TO PREVENT MULTI-INSTANTIATING
    private OptionsHandler() {
        try {
            this.volume = new SimpleDoubleProperty(0.5);
            this.randomMode = true;
            this.database = resourceHandler.getExcelFilePath("/database/default.xlsx");
        } catch (Exception e) {
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