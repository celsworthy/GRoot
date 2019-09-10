package celuk.groot.controllers;

import celuk.groot.remote.RootPrinter;
import celuk.groot.remote.RootServer;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.collections.MapChangeListener;
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
    
    private AboutController aboutPage = null;
    private AccessPINController accessPINPage = null;
    private ConsoleController consolePage = null;
    private ControlController controlPage = null;
    private HeadParametersController headParametersPage = null;
    private HomeController homePage = null;
    private LoginController loginPage = null;
    private PrinterColourController printerColourPage = null;
    private PrinterNameController printerNamePage = null;
    private PrinterSelectController printerSelectPage = null;
    private PrintController printPage = null;
    private PurgeController purgePage = null;
    private PurgeIntroController purgeIntroPage = null;
    private ResetPINController resetPINPage = null;
    private TweakController tweakPage = null;
    private SecurityMenuController securityMenu = null;
    private IdentityMenuController identityMenu = null;
    private MainMenuController mainMenu = null;
    private MaintenanceMenuController maintenanceMenu = null;
    private PrintMenuController printMenu = null;
    private SettingsMenuController settingsMenu = null;
    private ServerSettingsMenuController serverSettingsMenu = null;
    private EjectStuckMenuController ejectStuckMenu = null;
    private CleanNozzlesMenuController cleanNozzlesMenu = null;
    private TestAxisSpeedMenuController testAxisSpeedMenu = null;
    private final List<Page> pages = new ArrayList<>();

    private RootServer server = null;
    private Insets offsets = new Insets(0.0, 0.0, 0.0, 0.0);
    private ErrorDisplayManager errorManager = null;
    private RootPrinter currentPrinter = null;
    
    private final MapChangeListener<String, RootPrinter> printerMapListener = (c) ->  {
        RootPrinter p = currentPrinter;
        if (p != null && !c.getMap().containsKey(p.getPrinterId())) {
            Platform.runLater(() -> {
                currentPrinter = null;
                hidePages(printerSelectPage);
                printerSelectPage.displayPage(null);
            });
        }
    };
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {

        URL aboutPageURL = getClass().getResource(FXML_RESOURCE_PATH + "About.fxml");
        URL accessPINPageURL = getClass().getResource(FXML_RESOURCE_PATH + "AccessPIN.fxml");
        URL consolePageURL = getClass().getResource(FXML_RESOURCE_PATH + "Console.fxml");
        URL controlPageURL = getClass().getResource(FXML_RESOURCE_PATH + "Control.fxml");
        URL headParametersPageURL = getClass().getResource(FXML_RESOURCE_PATH + "HeadParameters.fxml");
        URL homePageURL = getClass().getResource(FXML_RESOURCE_PATH + "Home.fxml");
        URL loginPageURL = getClass().getResource(FXML_RESOURCE_PATH + "Login.fxml");
        URL printerColourPageURL = getClass().getResource(FXML_RESOURCE_PATH + "PrinterColour.fxml");
        URL printerNamePageURL = getClass().getResource(FXML_RESOURCE_PATH + "PrinterName.fxml");
        URL printerSelectPageURL = getClass().getResource(FXML_RESOURCE_PATH + "PrinterSelect.fxml");
        URL printPageURL = getClass().getResource(FXML_RESOURCE_PATH + "Print.fxml");
        URL purgePageURL = getClass().getResource(FXML_RESOURCE_PATH + "Purge.fxml");
        URL purgeIntroPageURL = getClass().getResource(FXML_RESOURCE_PATH + "PurgeIntro.fxml");
        URL resetPINPageURL = getClass().getResource(FXML_RESOURCE_PATH + "ResetPIN.fxml");
        URL tweakPageURL = getClass().getResource(FXML_RESOURCE_PATH + "Tweak.fxml");
        URL mainMenuURL = getClass().getResource(FXML_RESOURCE_PATH + "MainMenu.fxml");
        URL menuURL = getClass().getResource(FXML_RESOURCE_PATH + "Menu.fxml");
        try
        {
            // Pages
            aboutPage = (AboutController)(loadPage(aboutPageURL, null));
            accessPINPage = (AccessPINController)(loadPage(accessPINPageURL, null));
            consolePage = (ConsoleController)(loadPage(consolePageURL, null));
            controlPage = (ControlController)(loadPage(controlPageURL, null));
            headParametersPage = (HeadParametersController)(loadPage(headParametersPageURL, null));
            homePage = (HomeController)(loadPage(homePageURL, null));
            loginPage = (LoginController)(loadPage(loginPageURL, null));
            printerColourPage = (PrinterColourController)(loadPage(printerColourPageURL, null));
            printerNamePage = (PrinterNameController)(loadPage(printerNamePageURL, null));
            printerSelectPage = (PrinterSelectController)(loadPage(printerSelectPageURL, null));
            printPage = (PrintController)(loadPage(printPageURL, null));
            purgePage = (PurgeController)(loadPage(purgePageURL, null));
            purgeIntroPage = (PurgeIntroController)(loadPage(purgeIntroPageURL, null));
            resetPINPage = (ResetPINController)(loadPage(resetPINPageURL, null));
            tweakPage = (TweakController)(loadPage(tweakPageURL, null));
        
            // Menus - all but the main menu use the same FXML page.
            cleanNozzlesMenu = (CleanNozzlesMenuController)(loadPage(menuURL, CleanNozzlesMenuController.class));
            ejectStuckMenu = (EjectStuckMenuController)(loadPage(menuURL, EjectStuckMenuController.class));
            identityMenu = (IdentityMenuController)(loadPage(menuURL, IdentityMenuController.class));
            mainMenu = (MainMenuController)(loadPage(mainMenuURL, null));
            maintenanceMenu = (MaintenanceMenuController)(loadPage(menuURL, MaintenanceMenuController.class));
            printMenu = (PrintMenuController)(loadPage(menuURL, PrintMenuController.class));
            securityMenu = (SecurityMenuController)(loadPage(menuURL, SecurityMenuController.class));
            serverSettingsMenu = (ServerSettingsMenuController)(loadPage(menuURL, ServerSettingsMenuController.class));
            settingsMenu = (SettingsMenuController)(loadPage(menuURL, SettingsMenuController.class));
            testAxisSpeedMenu = (TestAxisSpeedMenuController)(loadPage(menuURL, TestAxisSpeedMenuController.class));

            server.getCurrentPrinterMap().addListener(printerMapListener);
            errorManager = new ErrorDisplayManager(server);

            hidePages(printerSelectPage);
            printerSelectPage.displayPage(null);
        }
        catch (IOException ex) {
            System.out.println(ex);
            ex.printStackTrace(System.err);
            System.exit(1);
        }
    }
    
    public void showAboutPage(Page previousPage, RootPrinter printer) {
        Platform.runLater(() -> {
            previousPage.hidePage();
            aboutPage.displayPage(printer);
        });
    }

    public void showAccessPINPage(Page previousPage, RootPrinter printer) {
        Platform.runLater(() -> {
            previousPage.hidePage();
            accessPINPage.displayPage(printer);
        });
    }

    public void showHeadParametersPage(Page previousPage, RootPrinter printer) {
        Platform.runLater(() -> {
            previousPage.hidePage();
            headParametersPage.displayPage(printer);
        });
    }

    public void showConsolePage(Page previousPage, RootPrinter printer) {
        Platform.runLater(() -> {
            previousPage.hidePage();
            consolePage.setReturnToControl(previousPage == controlPage);
            consolePage.displayPage(printer);
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
            currentPrinter = printer;
            homePage.displayPage(printer);
        });
    }
    
    public void showLoginPage(Page previousPage, RootPrinter printer) {
        Platform.runLater(() -> {
            previousPage.hidePage();
            loginPage.displayPage(printer);
        });
    }

    public void showPrinterColourPage(Page previousPage, RootPrinter printer) {
        Platform.runLater(() -> {
            previousPage.hidePage();
            printerColourPage.displayPage(printer);
        });
    }
    
    public void showPrinterNamePage(Page previousPage, RootPrinter printer) {
        Platform.runLater(() -> {
            previousPage.hidePage();
            printerNamePage.displayPage(printer);
        });
    }
    
    public void showPrinterSelectPage(Page previousPage) {
        Platform.runLater(() -> {
            previousPage.hidePage();
            currentPrinter = null;
            printerSelectPage.displayPage(null);
        });
    }
    
    public void showReprintPage(Page previousPage, RootPrinter printer) {
        Platform.runLater(() -> {
            previousPage.hidePage();
            printPage.setReprintMode(true);
            printPage.displayPage(printer);
        });
    }

    public void showResetPINPage(Page previousPage, RootPrinter printer) {
        Platform.runLater(() -> {
            previousPage.hidePage();
            resetPINPage.displayPage(printer);
        });
    }
    
    public void showUSBPrintPage(Page previousPage, RootPrinter printer) {
        Platform.runLater(() -> {
            previousPage.hidePage();
            printPage.setReprintMode(false);
            printPage.displayPage(printer);
        });
    }

    public void showCleanNozzlesMenu(Page previousPage, RootPrinter printer) {
        Platform.runLater(() -> {
            previousPage.hidePage();
            cleanNozzlesMenu.displayPage(printer);
        });
    }

    public void showEjectStuckMenu(Page previousPage, RootPrinter printer) {
        Platform.runLater(() -> {
            previousPage.hidePage();
            ejectStuckMenu.displayPage(printer);
        });
    }

    public void showIdentityMenu(Page previousPage, RootPrinter printer) {
        Platform.runLater(() -> {
            previousPage.hidePage();
            identityMenu.displayPage(printer);
        });
    }

    public void showMainMenu(Page previousPage, RootPrinter printer) {
        Platform.runLater(() -> {
            previousPage.hidePage();
            mainMenu.displayPage(printer);
        });
    }

    public void showMaintenanceMenu(Page previousPage, RootPrinter printer) {
        Platform.runLater(() -> {
            previousPage.hidePage();
            maintenanceMenu.displayPage(printer);
        });
    }

    public void showSecurityMenu(Page previousPage, RootPrinter printer) {
        Platform.runLater(() -> {
            previousPage.hidePage();
            securityMenu.displayPage(printer);
        });
    }

    public void showTestAxisSpeedMenu(Page previousPage, RootPrinter printer) {
        Platform.runLater(() -> {
            previousPage.hidePage();
            testAxisSpeedMenu.displayPage(printer);
        });
    }

    public void showPrintMenu(Page previousPage, RootPrinter printer) {
        Platform.runLater(() -> {
            previousPage.hidePage();
            printMenu.displayPage(printer);
        });
    }

    public void showPurgePage(Page previousPage, RootPrinter printer) {
        Platform.runLater(() -> {
            previousPage.hidePage();
            purgePage.displayPage(printer);
        });
    }

    public void showPurgeIntroPage(Page previousPage, RootPrinter printer) {
        Platform.runLater(() -> {
            previousPage.hidePage();
            purgeIntroPage.displayPage(printer);
        });
    }

    public void showRemoveHeadPage(Page previousPage, RootPrinter printer) {
        Platform.runLater(() -> {
            // previousPage.hidePage();
            //purgePage.displayPage(printer);
        });
    }
    public void showSettingsMenu(Page previousPage, RootPrinter printer) {
        Platform.runLater(() -> {
            previousPage.hidePage();
            settingsMenu.displayPage(printer);
        });
    }
    
    public void showServerSettingsMenu(Page previousPage) {
        Platform.runLater(() -> {
            previousPage.hidePage();
            serverSettingsMenu.displayPage(null);
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
        pages.forEach(Page::stopUpdates);
    }
        
    private void hidePages(Page pageToShow) {
        pages.stream()
             .filter(p -> p != pageToShow)
             .forEach(p -> p.hidePage());
    }
    
    private Page loadPage(URL pageURL, Class controller) throws IOException {
            Page page = null;
            FXMLLoader pageLoader =  new FXMLLoader(pageURL, null);
            if (controller != null) {
                
                try {
                    page = (Page)controller.getDeclaredConstructor().newInstance();
                }
                catch (Exception ex) {
                    System.err.println("Exception thown when instantiating page controller");
                }
                pageLoader.setController(page);
            }
            StackPane pagePane = pageLoader.load();
            setAnchors(pagePane);
            if (page == null)
                page = (Page)(pageLoader.getController());

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
