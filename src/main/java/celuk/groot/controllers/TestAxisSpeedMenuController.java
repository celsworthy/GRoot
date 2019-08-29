package celuk.groot.controllers;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;

public class TestAxisSpeedMenuController extends MenuController {
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        super.initialize(url, rb);
        menuPane.getStylesheets().add("styles/TestAxisSpeedMenu.css");
        translateMenuText("testAxisSpeedMenu.title",
                          "testAxisSpeedMenu.testSpeed",
                          "testAxisSpeedMenu.testXAxis",
                          "testAxisSpeedMenu.testYAxis",
                          "testAxisSpeedMenu.testZAxis");
        rightButton.setVisible(false);
    }

    @Override
    protected void menu1Action(ActionEvent event) {
        if (validButtonAction(event)) {
            System.out.println("test axis speed  menu 1");
            //rootController.showTestSpeedPage(this, printer);
        }
    }

    @Override
    protected void menu2Action(ActionEvent event) {
        if (validButtonAction(event)) {
            System.out.println("test axis speed menu 2");
            //rootController.showTestXAxisPage(this, printer);
        }
    }

    @Override
    protected void menu3Action(ActionEvent event) {
        if (validButtonAction(event)) {
            System.out.println("test axis speed menu 3");
            //rootController.showTestYAxisPage(this, printer);
        }
    }

    @Override
    protected void menu4Action(ActionEvent event) {
        if (validButtonAction(event)) {
            System.out.println("test axis speed menu 4");
            //rootController.showTestZAxisPage(this, printer);
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
