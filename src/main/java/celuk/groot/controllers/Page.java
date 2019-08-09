/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package celuk.groot.controllers;

import celuk.groot.remote.RootPrinter;

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
}
