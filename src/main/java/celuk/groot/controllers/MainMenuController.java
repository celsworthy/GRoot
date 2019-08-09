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

public class MainMenuController implements Initializable, Page {
    
    @FXML
    private StackPane mainMenuPane;
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
    void menuAction(ActionEvent event) {
        if (rootController != null && event.getSource() instanceof Button) {
            Button b = (Button)event.getSource();
            switch (b.getId()) {
                case "calibration":
                    rootController.showCalibrationPage(this, printer);
                    break;
                case "control":
                    rootController.showControlPage(this, printer);
                    break;
                case "console":
                    rootController.showConsolePage(this, printer);
                    break;
                case "maintenance":
                    rootController.showTweakPage(this, printer);
                    break;
                case "print":
                    rootController.showPrintMenu(this, printer);
                    break;
                case "purge":
                    rootController.showPurgePage(this, printer);
                    break;
                default:
                    rootController.showHomePage(this, printer);
                    break;
            }
        }
    }
    
    @FXML
    void leftButtonAction(ActionEvent event) {
        if (rootController != null && event.getSource() instanceof Button)
            rootController.showHomePage(this, printer);
    }
    
    @FXML
    void middleButtonAction(ActionEvent event) {
        if (rootController != null && event.getSource() instanceof Button)
            rootController.showHomePage(this, printer);
    }

    @FXML
    void rightButtonAction(ActionEvent event) {
        if (rootController != null && event.getSource() instanceof Button)
            rootController.showSettingsMenu(this, printer);
    }

    private RootStackController rootController = null;
    private RootPrinter printer = null;
    
    @Override
    public void setRootStackController(RootStackController rootController) {
        this.rootController = rootController;
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
    
    @Override
    public void startUpdates() {
    }
    
    @Override
    public void stopUpdates() {
        printer = null;
    }

    @Override
    public void displayPage(RootPrinter printer) {
        this.printer = printer;
        startUpdates();
        mainMenuPane.setVisible(true);
    }

    @Override
    public void hidePage() {
        stopUpdates();
        mainMenuPane.setVisible(false);
    }
}
