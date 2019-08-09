package celuk.groot.controllers;

import celuk.groot.remote.RootPrinter;
import celuk.groot.remote.RootServer;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;

public class RootStackController implements Initializable {

    private static final String FXML_RESOURCE_PATH = "/fxml/";

    @FXML
    private AnchorPane rootAnchorPane;
    
    private ControlController controlPage = null;
    private HomeController homePage = null;
    private MainMenuController mainMenu = null;
    private PrinterSelectController printerSelectPage = null;
    private TweakController tweakPage = null;
    private final List<Page> pages = new ArrayList<>();

    private RootServer server = null;
    private Insets offsets = new Insets(0.0, 0.0, 0.0, 0.0); 
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {

        //System.out.println("Starting server updating");
        URL controlPageURL = getClass().getResource(FXML_RESOURCE_PATH + "Control.fxml");
        URL homePageURL = getClass().getResource(FXML_RESOURCE_PATH + "Home.fxml");
        URL mainMenuURL = getClass().getResource(FXML_RESOURCE_PATH + "MainMenu.fxml");
        URL printerSelectPageURL = getClass().getResource(FXML_RESOURCE_PATH + "PrinterSelect.fxml");
        URL tweakPageURL = getClass().getResource(FXML_RESOURCE_PATH + "Tweak.fxml");
        try
        {
            controlPage = (ControlController)(loadPage(controlPageURL));
            homePage = (HomeController)(loadPage(homePageURL));
            mainMenu = (MainMenuController)(loadPage(mainMenuURL));
            printerSelectPage = (PrinterSelectController)(loadPage(printerSelectPageURL));
            tweakPage = (TweakController)(loadPage(tweakPageURL));
            hidePages(printerSelectPage);
            printerSelectPage.displayPage(null);
        }
        catch (IOException ex) {
            System.out.println(ex);
            ex.printStackTrace(System.err);
            System.exit(1);
        }
    }
    
    public void showCalibrationPage(Page previousPage, RootPrinter printer) {
        Platform.runLater(() -> {
            //previousPage.hidePage();
            //calibrationPage.displayPage(null);
        });
    }

    public void showConsolePage(Page previousPage, RootPrinter printer) {
        Platform.runLater(() -> {
            //previousPage.hidePage();
            //consolePage.displayPage(null);
        });
    }

    public void showControlPage(Page previousPage, RootPrinter printer) {
        Platform.runLater(() -> {
            previousPage.hidePage();
            controlPage.displayPage(printer);
        });
    }

    public void showHomePage(Page previousPage, RootPrinter printer) {
        Platform.runLater(() -> {
            previousPage.hidePage();
            homePage.displayPage(printer);
        });
    }
    
    public void showPrinterSelectPage(Page previousPage) {
        Platform.runLater(() -> {
            previousPage.hidePage();
            printerSelectPage.displayPage(null);
        });
    }
    
    public void showMainMenu(Page previousPage, RootPrinter printer) {
        Platform.runLater(() -> {
            previousPage.hidePage();
            mainMenu.displayPage(printer);
        });
    }

    public void showMaintenancePage(Page previousPage, RootPrinter printer) {
        Platform.runLater(() -> {
            //previousPage.hidePage();
            //maintenancePage.displayPage(null);
        });
    }

    public void showPrintMenu(Page previousPage, RootPrinter printer) {
        Platform.runLater(() -> {
            //previousPage.hidePage();
            //printMenuPage.displayPage(null);
        });
    }

    public void showPurgePage(Page previousPage, RootPrinter printer) {
        Platform.runLater(() -> {
            // previousPage.hidePage();
            //purgePage.displayPage(null);
        });
    }

    public void showSettingsMenu(Page previousPage, RootPrinter printer) {
        Platform.runLater(() -> {
            // previousPage.hidePage();
            //settingsMenuPage.displayPage(null);
        });
    }
    
    public void showTweakPage(Page previousPage, RootPrinter printer) {
        Platform.runLater(() -> {
            previousPage.hidePage();
            tweakPage.displayPage(printer);
        });
    }

    public void setOffsets(double top, double right, double bottom, double left) {
        offsets = new Insets(top, right, bottom, left);
    }
    
    public RootServer getRootServer() {
        return server;
    }

    public void setRootServer(RootServer server) {
        this.server = server;
    }

    public void stop() {
        if (printerSelectPage != null)
            printerSelectPage.stopUpdates();
        if (homePage != null)
            homePage.stopUpdates();
    }
    
    private void hidePages(Page pageToShow) {
        pages.stream()
             .filter(p -> p != pageToShow)
             .forEach(p -> p.hidePage());
    }
    
    private Page loadPage(URL pageURL) throws IOException {
            FXMLLoader pageLoader =  new FXMLLoader(pageURL, null);
            StackPane pagePane = pageLoader.load();
            setAnchors(pagePane);
            Page page = (Page)(pageLoader.getController());
            page.setRootStackController(this);
            pages.add(page);
            rootAnchorPane.getChildren().add(pagePane);
            return page;
    }
    
    private void setAnchors(Node n) {
        AnchorPane.setBottomAnchor(n, offsets.getBottom());
        AnchorPane.setTopAnchor(n, offsets.getTop());
        AnchorPane.setLeftAnchor(n, offsets.getLeft());
        AnchorPane.setRightAnchor(n, offsets.getRight());
    }
}
