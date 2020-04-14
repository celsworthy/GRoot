package celuk.groot.controllers;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;

public class CleanNozzlesMenuController extends MenuController {
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        super.initialize(url, rb);
        menuPane.getStylesheets().add("styles/CleanNozzlesMenu.css");
        translateMenuText("cleanNozzlesMenu.title",
                          "cleanNozzlesMenu.leftNozzle",
                          "cleanNozzlesMenu.rightNozzle");
        rightButton.setVisible(false);
    }

    @Override
    protected void menu1Action(ActionEvent event) {
        if (validButtonAction(event)) {
            //System.out.println("clean left nozzle menu 1");
            printer.runCleanNozzleTask(1);
            rootController.showHomePage(this, printer);
        }
    }

    @Override
    protected void menu2Action(ActionEvent event) {
        if (validButtonAction(event)) {
            //System.out.println("clean right nozzle menu 2");
            printer.runCleanNozzleTask(2);
            rootController.showHomePage(this, printer);
        }
    }
    
    @Override
    protected void leftButtonAction(ActionEvent event) {
        if (validButtonAction(event))
            rootController.showMaintenanceMenu(this, printer);
    }
    
    @Override
    protected void middleButtonAction(ActionEvent event) {
        if (validButtonAction(event))
            rootController.showMainMenu(this, printer);
    }
}
