package celuk.groot.controllers;

import celuk.groot.remote.RootPrinter;
import celuk.language.I18n;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

public class MainMenuController implements Initializable {
    
    @FXML
    private StackPane MainMenuPane;
    @FXML
    private VBox menuGridVBox;
    @FXML
    private GridPane menuGrid;
    @FXML
    private Button menu00Button;
    @FXML
    private Button menu01Button;
    @FXML
    private Button menu10Button;
    @FXML
    private Button menu11Button;
    @FXML
    private Button menu20Button;
    @FXML
    private Button menu21Button;
    @FXML
    private HBox bottomBarHBox;
    @FXML
    private Button leftButton;
    @FXML
    private Button middleButton;
    @FXML
    private Button rightButton;

    private Button[] buttonArray = null;
    
    @FXML
    void menuAction(ActionEvent event)
    {
        if (rootController != null && event.getSource() instanceof Button)
        {
            try {
                Button b = (Button)event.getSource();
                switch (b.getId()) {
                    case "calibration":
                        rootController.showCalibrationPage(printer);
                        break;
                    case "control":
                        rootController.showControlPage(printer);
                        break;
                    case "console":
                        rootController.showConsolePage(printer);
                        break;
                    case "maintenance":
                        rootController.showMaintenancePage(printer);
                        break;
                    case "print":
                        rootController.showPrintMenu(printer);
                        break;
                    case "purge":
                        rootController.showPurgePage(printer);
                        break;
                    default:
                        rootController.showHomePage(printer);
                        break;
                }
                stopUpdates();
            }
            catch (Exception ex) {
            }
        }
    }
    
    @FXML
    void leftButtonAction(ActionEvent event)
    {
        if (rootController != null && event.getSource() instanceof Button)
        {
            stopUpdates();
            rootController.showHomePage(printer);
        }
    }
    
    @FXML
    void middleButtonAction(ActionEvent event)
    {
        if (rootController != null && event.getSource() instanceof Button)
        {
            stopUpdates();
            rootController.showHomePage(printer);
        }
    }

    @FXML
    void rightButtonAction(ActionEvent event)
    {
        if (rootController != null && event.getSource() instanceof Button)
        {
            stopUpdates();
            rootController.showSettingsMenu(printer);
        }
    }

    private RootStackController rootController = null;
    private RootPrinter printer = null;
    
    protected void setRootStackController(RootStackController rootController) {
        this.rootController = rootController;
    }
    
    protected void setPrinter(RootPrinter printer) {
        this.printer = printer;
        startUpdates();
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        menu00Button.setText(I18n.t(menu00Button.getText()));
        menu01Button.setText(I18n.t(menu01Button.getText()));
        menu10Button.setText(I18n.t(menu10Button.getText()));
        menu11Button.setText(I18n.t(menu11Button.getText()));
        menu20Button.setText(I18n.t(menu20Button.getText()));
        menu21Button.setText(I18n.t(menu21Button.getText()));
        
        leftButton.setVisible(false);
        leftButton.setDisable(true);
    }
    
    public void stop() {
        stopUpdates();
    }       

    public void startUpdates() {
    }
    
    public void stopUpdates() {
    }
}
