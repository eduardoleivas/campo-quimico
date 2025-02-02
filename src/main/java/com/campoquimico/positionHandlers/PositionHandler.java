package com.campoquimico.positionHandlers;

import java.util.List;

import com.campoquimico.EnvVariables;

public class PositionHandler {
    
    //MULTI ELEMENT CENTER HORIZONTALLY
    public double centerX (List<javafx.scene.Node> elements) {
        double centerValue = 0;

        //GET ALL ELEMENTS WIDTH
        for (javafx.scene.Node element : elements) {
            centerValue += element.prefWidth(-1);
        }

        //HORIZONTAL VALUE
        return (EnvVariables.MENU_WIDTH - centerValue) / 2;
    }

    //MULTI ELEMENT CENTER HORIZONTALLY WITH SPACING
    public double centerX (List<javafx.scene.Node> elements, double spacing) {
        double centerValue = 0;

        //GET ALL ELEMENTS WIDTH
        for (javafx.scene.Node element : elements) {
            centerValue += element.prefWidth(-1);
        }
        centerValue += spacing * (elements.size()-1);

        //HORIZONTAL VALUE
        return (EnvVariables.MENU_WIDTH - centerValue) / 2;
    }

    //SINGLE ELEMENT CENTER HORIZONTALLY
    public double centerX (javafx.scene.Node element) {

        //HORIZONTAL VALUE
        return (EnvVariables.MENU_WIDTH - element.prefWidth(-1)) / 2;
    }

    
}
