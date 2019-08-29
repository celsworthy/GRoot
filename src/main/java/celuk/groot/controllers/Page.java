/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package celuk.groot.controllers;

import celuk.groot.remote.RootPrinter;
import celuk.language.I18n;
import javafx.scene.control.Labeled;

/**
 *
 * @author Tony
 */
public interface Page {
    
    public void setRootStackController(RootStackController rootController);
    public void startUpdates();
    public void stopUpdates();
    public void displayPage(RootPrinter printer);
    public void hidePage();
    
    default String secondsToHMS(int secondsInput) {
        int minutes = (int)Math.floor(secondsInput / 60);
        int seconds = (int)Math.floor(secondsInput - (minutes * 60));
        int hours = (int)Math.floor(minutes / 60);
        minutes = minutes - (60 * hours);
        
        String hms = String.format("%d:%02d:%02d", hours, minutes, seconds);
        return hms;
    }
    
    default void translateLabels(Labeled ... labels) {
        for (Labeled l : labels)
            l.setText(I18n.t(l.getText()));
    }
}
