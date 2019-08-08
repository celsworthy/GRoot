package celuk.groot.controllers;

import celuk.groot.remote.RootPrinter;
import celuk.groot.remote.RootServer;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;

public class RootStackController implements Initializable {

    private static final String FXML_RESOURCE_PATH = "/fxml/";

    @FXML
    private AnchorPane rootAnchorPane;
    StackPane printerSelectPane = null;
    PrinterSelectController printerSelectController = null;
    StackPane homePane = null;
    HomeController homeController = null;
    StackPane mainMenuPane = null;
    MainMenuController mainMenuController = null;
    StackPane controlPane = null;
    ControlController controlController = null;
    RootServer server = null;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {

        //System.out.println("Starting server updating");
        URL printerSelectPaneURL = getClass().getResource(FXML_RESOURCE_PATH + "PrinterSelect.fxml");
        URL homePaneURL = getClass().getResource(FXML_RESOURCE_PATH + "Home.fxml");
        URL mainMenuPaneURL = getClass().getResource(FXML_RESOURCE_PATH + "MainMenu.fxml");
        URL controlPaneURL = getClass().getResource(FXML_RESOURCE_PATH + "Control.fxml");
        try
        {
            FXMLLoader printerSelectPaneLoader =  new FXMLLoader(printerSelectPaneURL, null);
            printerSelectPane = printerSelectPaneLoader.load();
            printerSelectController = (PrinterSelectController)(printerSelectPaneLoader.getController());
            printerSelectController.setRootStackController(this);

            FXMLLoader homePaneLoader =  new FXMLLoader(homePaneURL, null);
            homePane = homePaneLoader.load();
            homeController = (HomeController)(homePaneLoader.getController());
            homeController.setRootStackController(this);

            FXMLLoader mainMenuPaneLoader =  new FXMLLoader(mainMenuPaneURL, null);
            mainMenuPane = mainMenuPaneLoader.load();
            mainMenuController = (MainMenuController)(mainMenuPaneLoader.getController());
            mainMenuController.setRootStackController(this);

            FXMLLoader controlPaneLoader =  new FXMLLoader(controlPaneURL, null);
            controlPane = controlPaneLoader.load();
            controlController = (ControlController)(controlPaneLoader.getController());
            controlController.setRootStackController(this);

            rootAnchorPane.getChildren().addAll(printerSelectPane, homePane, mainMenuPane, controlPane);
            printerSelectPane.setVisible(true);
            printerSelectController.startUpdates();
            homePane.setVisible(false);
            mainMenuPane.setVisible(false);
            controlPane.setVisible(false);
        } catch (Exception ex)
        {
            System.out.println(ex);
            ex.printStackTrace(System.err);
        }
    }
    
    public void showCalibrationPage(RootPrinter printer) {
    }

    public void showConsolePage(RootPrinter printer) {
    }

    public void showControlPage(RootPrinter printer) {
        printerSelectPane.setVisible(false);
        mainMenuPane.setVisible(false);
        homePane.setVisible(false);
        controlPane.setVisible(true);
        controlController.setPrinter(printer);
    }

    public void showHomePage(RootPrinter printer) {
        printerSelectPane.setVisible(false);
        mainMenuPane.setVisible(false);
        controlPane.setVisible(false);
        homePane.setVisible(true);
        homeController.setPrinter(printer);
    }
    
    public void showPrinterSelectPage() {
        homePane.setVisible(false);
        mainMenuPane.setVisible(false);
        controlPane.setVisible(false);
        printerSelectPane.setVisible(true);
        printerSelectController.startUpdates();
    }
    
    public void showMaintenancePage(RootPrinter printer) {
    }

    public void showPrintMenu(RootPrinter printer) {
    }

    public void showPurgePage(RootPrinter printer) {
    }

    public void showMainMenu(RootPrinter printer) {
        homePane.setVisible(false);
        controlPane.setVisible(false);
        printerSelectPane.setVisible(false);
        mainMenuPane.setVisible(true);
        mainMenuController.setPrinter(printer);
    }

    public void showSettingsMenu(RootPrinter printer) {
    }
    
    public RootServer getRootServer() {
        return server;
    }

    public void setRootServer(RootServer server) {
        this.server = server;
    }

    public void stop() {
        if (printerSelectController != null)
            printerSelectController.stop();
        if (homeController != null)
            homeController.stop();
    }
}
