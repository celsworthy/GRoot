package celuk.groot.controllers;

import celuk.groot.remote.RootPrinter;
import celuk.groot.remote.RootServer;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
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
    
    private final URL aboutPageURL = getClass().getResource(FXML_RESOURCE_PATH + "About.fxml");
    private final URL accessPINPageURL = getClass().getResource(FXML_RESOURCE_PATH + "AccessPIN.fxml");
    private final URL connectingPageURL = getClass().getResource(FXML_RESOURCE_PATH + "Connecting.fxml");
    private final URL consolePageURL = getClass().getResource(FXML_RESOURCE_PATH + "Console.fxml");
    private final URL controlPageURL = getClass().getResource(FXML_RESOURCE_PATH + "Control.fxml");
    private final URL headParametersPageURL = getClass().getResource(FXML_RESOURCE_PATH + "HeadParameters.fxml");
    private final URL homePageURL = getClass().getResource(FXML_RESOURCE_PATH + "Home.fxml");
    private final URL loginPageURL = getClass().getResource(FXML_RESOURCE_PATH + "Login.fxml");
    private final URL printerColourPageURL = getClass().getResource(FXML_RESOURCE_PATH + "PrinterColour.fxml");
    private final URL namePageURL = getClass().getResource(FXML_RESOURCE_PATH + "Name.fxml");
    private final URL printerSelectPageURL = getClass().getResource(FXML_RESOURCE_PATH + "PrinterSelect.fxml");
    private final URL printPageURL = getClass().getResource(FXML_RESOURCE_PATH + "Print.fxml");
    private final URL purgePageURL = getClass().getResource(FXML_RESOURCE_PATH + "Purge.fxml");
    private final URL purgeIntroPageURL = getClass().getResource(FXML_RESOURCE_PATH + "PurgeIntro.fxml");
    private final URL resetPINPageURL = getClass().getResource(FXML_RESOURCE_PATH + "ResetPIN.fxml");
    private final URL tweakPageURL = getClass().getResource(FXML_RESOURCE_PATH + "Tweak.fxml");
    private final URL wirelessPageURL = getClass().getResource(FXML_RESOURCE_PATH + "Wireless.fxml");
    private final URL mainMenuURL = getClass().getResource(FXML_RESOURCE_PATH + "MainMenu.fxml");
    private final URL menuURL = getClass().getResource(FXML_RESOURCE_PATH + "Menu.fxml");

    private AboutController aboutPage = null;
    private AccessPINController accessPINPage = null;
    private ConnectingController connectingPage = null;
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
    private ServerNameController serverNamePage = null;
    private TweakController tweakPage = null;
    private WirelessController wirelessPage = null;
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
    private ErrorAlertController errorManager = null;
    private RootPrinter currentPrinter = null;
    
    private ChangeListener<Boolean> printerMapHeartbeatListener = (pr, ov, nv) ->  {
        RootPrinter p = currentPrinter;
        if (p != null && !server.getCurrentPrinterMap().containsKey(p.getPrinterId())) {
            Platform.runLater(() -> {
                currentPrinter = null;
                hidePages(printerSelectPage);
                printerSelectPage.displayPage(null);
            });
        }
    };
    
    private final ChangeListener<Boolean> authorisedListener = (op, ov, nv) ->  {
        // This should be invoked if an HTTP request fails because of lack of authorisation.
        if (nv == false) {
            Platform.runLater(() -> {
                hidePages(loginPage);
                loginPage.displayPage(currentPrinter);
            });            
        }
    };

    @Override
    public void initialize(URL url, ResourceBundle rb) {

        // Most pages are loaded when first shown. The following commonly used
        // pages are loaded immediately.
        connectingPage = (ConnectingController)(loadPage(connectingPageURL, null));
        homePage = (HomeController)(loadPage(homePageURL, null));
        loginPage = (LoginController)(loadPage(loginPageURL, null));
        printerSelectPage = (PrinterSelectController)(loadPage(printerSelectPageURL, null));

        // Menus - all but the main menu use the same FXML page.
        mainMenu = (MainMenuController)(loadPage(mainMenuURL, null));

        server.getCurrentPrinterMapHeartbeatProperty().addListener(printerMapHeartbeatListener);
        server.getAuthorisedProperty().addListener(authorisedListener);
        errorManager = new ErrorAlertController(server);
        errorManager.prepareDialog();

        hidePages(connectingPage);
        connectingPage.displayPage(null);
    }
    
    public void showAboutPage(Page previousPage, RootPrinter printer) {
        Platform.runLater(() -> {
            if (aboutPage == null) {
                aboutPage = (AboutController)(loadPage(aboutPageURL, null));
            }
            previousPage.hidePage();
            aboutPage.displayPage(printer);
        });
    }

    public void showAccessPINPage(Page previousPage, RootPrinter printer) {
        Platform.runLater(() -> {
            if (accessPINPage == null) {
                accessPINPage = (AccessPINController)(loadPage(accessPINPageURL, null));
            }
            previousPage.hidePage();
            accessPINPage.displayPage(printer);
        });
    }

    public void showConnectingPage(Page previousPage, RootPrinter printer) {
        Platform.runLater(() -> {
            if (connectingPage == null) {
                connectingPage = (ConnectingController)(loadPage(connectingPageURL, null));
            }
            if (previousPage != null)
                previousPage.hidePage();
            connectingPage.displayPage(printer);
        });
    }

    public void showConsolePage(Page previousPage, RootPrinter printer) {
        Platform.runLater(() -> {
            if (consolePage == null) {
                consolePage = (ConsoleController)(loadPage(consolePageURL, null));
            }
            previousPage.hidePage();
            consolePage.setReturnToControl(previousPage == controlPage);
            consolePage.displayPage(printer);
        });
    }

    public void showControlPage(Page previousPage, RootPrinter printer) {
        Platform.runLater(() -> {
            if (controlPage == null) {
                controlPage = (ControlController)(loadPage(controlPageURL, null));
            }
            previousPage.hidePage();
            controlPage.displayPage(printer);
        });
    }

    public void showHeadParametersPage(Page previousPage, RootPrinter printer) {
        Platform.runLater(() -> {
            if (headParametersPage == null) {
                headParametersPage = (HeadParametersController)(loadPage(headParametersPageURL, null));
            }
            previousPage.hidePage();
            headParametersPage.displayPage(printer);
        });
    }

    public void showHomePage(Page previousPage, RootPrinter printer) {
        Platform.runLater(() -> {
            if (homePage == null) {
                homePage = (HomeController)(loadPage(homePageURL, null));
            }
            previousPage.hidePage();
            currentPrinter = printer;
            homePage.displayPage(printer);
        });
    }
    
    public void showLoginPage(Page previousPage, RootPrinter printer) {
        Platform.runLater(() -> {
            if (loginPage == null) {
                loginPage = (LoginController)(loadPage(loginPageURL, null));
            }
            previousPage.hidePage();
            loginPage.displayPage(printer);
        });
    }

    public void showPrinterColourPage(Page previousPage, RootPrinter printer) {
        Platform.runLater(() -> {
            if (printerColourPage == null) {
                printerColourPage = (PrinterColourController)(loadPage(printerColourPageURL, null));
            }
            previousPage.hidePage();
            printerColourPage.displayPage(printer);
        });
    }
    
    public void showPrinterNamePage(Page previousPage, RootPrinter printer) {
        Platform.runLater(() -> {
            if (printerNamePage == null) {
                printerNamePage = (PrinterNameController)(loadPage(namePageURL, PrinterNameController.class));
            }
            previousPage.hidePage();
            printerNamePage.displayPage(printer);
        });
    }
    
    public void showPrinterSelectPage(Page previousPage) {
        Platform.runLater(() -> {
            if (printerSelectPage == null) {
                printerSelectPage = (PrinterSelectController)(loadPage(printerSelectPageURL, null));
            }
            previousPage.hidePage();
            currentPrinter = null;
            printerSelectPage.displayPage(null);
        });
    }
    
    public void showReprintPage(Page previousPage, RootPrinter printer) {
        Platform.runLater(() -> {
            if (printPage == null) {
                printPage = (PrintController)(loadPage(printPageURL, null));
            }
            previousPage.hidePage();
            printPage.setReprintMode(true);
            printPage.displayPage(printer);
        });
    }

    public void showResetPINPage(Page previousPage, RootPrinter printer) {
        Platform.runLater(() -> {
            if (resetPINPage == null) {
                resetPINPage = (ResetPINController)(loadPage(resetPINPageURL, null));
            }
            previousPage.hidePage();
            resetPINPage.displayPage(printer);
        });
    }
    
    public void showPurgePage(Page previousPage, RootPrinter printer) {
        Platform.runLater(() -> {
            if (purgePage == null) {
                purgePage = (PurgeController)(loadPage(purgePageURL, null));
            }
            previousPage.hidePage();
            purgePage.displayPage(printer);
        });
    }

    public void showPurgeIntroPage(Page previousPage, RootPrinter printer) {
        Platform.runLater(() -> {
            if (purgeIntroPage == null) {
                purgeIntroPage = (PurgeIntroController)(loadPage(purgeIntroPageURL, null));
            }
            previousPage.hidePage();
            purgeIntroPage.displayPage(printer);
        });
    }

    public void showRemoveHeadPage(Page previousPage, RootPrinter printer) {
        Platform.runLater(() -> {
            //if (removeHeadPage == null) {
            //    removeHeadPage = (RemoveHeadController)(loadPage(removeHeadPageURL, null));
            //}
            // previousPage.hidePage();
            //removeHeadPage.displayPage(printer);
        });
    }

    public void showServerNamePage(Page previousPage) {
        Platform.runLater(() -> {
            if (serverNamePage == null) {
                serverNamePage = (ServerNameController)(loadPage(namePageURL, ServerNameController.class));
            }
            previousPage.hidePage();
            serverNamePage.displayPage(null);
        });
    }
    
    public void showTweakPage(Page previousPage, RootPrinter printer) {
        Platform.runLater(() -> {
            if (tweakPage == null) {
                tweakPage = (TweakController)(loadPage(tweakPageURL, null));
            }
            previousPage.hidePage();
            tweakPage.displayPage(printer);
        });
    }

    public void showWirelessPage(Page previousPage, RootPrinter printer) {
        Platform.runLater(() -> {
            if (wirelessPage == null) {
                wirelessPage = (WirelessController)(loadPage(wirelessPageURL, null));
            }
            previousPage.hidePage();
            wirelessPage.displayPage(printer);
        });
    }

    public void showUSBPrintPage(Page previousPage, RootPrinter printer) {
        Platform.runLater(() -> {
            if (printPage == null) {
                printPage = (PrintController)(loadPage(printPageURL, null));
            }
            previousPage.hidePage();
            printPage.setReprintMode(false);
            printPage.displayPage(printer);
        });
    }

    public void showCleanNozzlesMenu(Page previousPage, RootPrinter printer) {
        Platform.runLater(() -> {
            if (cleanNozzlesMenu == null) {
                cleanNozzlesMenu = (CleanNozzlesMenuController)(loadPage(menuURL, CleanNozzlesMenuController.class));
            }
            previousPage.hidePage();
            cleanNozzlesMenu.displayPage(printer);
        });
    }

    public void showEjectStuckMenu(Page previousPage, RootPrinter printer) {
        Platform.runLater(() -> {
            if (ejectStuckMenu == null) {
                ejectStuckMenu = (EjectStuckMenuController)(loadPage(menuURL, EjectStuckMenuController.class));
            }
            previousPage.hidePage();
            ejectStuckMenu.displayPage(printer);
        });
    }

    public void showIdentityMenu(Page previousPage, RootPrinter printer) {
        Platform.runLater(() -> {
            if (identityMenu == null) {
                identityMenu = (IdentityMenuController)(loadPage(menuURL, IdentityMenuController.class));
            }
            previousPage.hidePage();
            identityMenu.displayPage(printer);
        });
    }

    public void showMainMenu(Page previousPage, RootPrinter printer) {
        Platform.runLater(() -> {
            if (mainMenu == null) {
                mainMenu = (MainMenuController)(loadPage(mainMenuURL, null));
            }
            previousPage.hidePage();
            mainMenu.displayPage(printer);
        });
    }

    public void showMaintenanceMenu(Page previousPage, RootPrinter printer) {
        Platform.runLater(() -> {
            if (maintenanceMenu == null) {
                maintenanceMenu = (MaintenanceMenuController)(loadPage(menuURL, MaintenanceMenuController.class));
            }
            previousPage.hidePage();
            maintenanceMenu.displayPage(printer);
        });
    }

    public void showPrintMenu(Page previousPage, RootPrinter printer) {
        Platform.runLater(() -> {
            if (printMenu == null) {
                printMenu = (PrintMenuController)(loadPage(menuURL, PrintMenuController.class));
            }
            previousPage.hidePage();
            printMenu.displayPage(printer);
        });
    }

    public void showSecurityMenu(Page previousPage, RootPrinter printer) {
        Platform.runLater(() -> {
            if (securityMenu == null) {
                securityMenu = (SecurityMenuController)(loadPage(menuURL, SecurityMenuController.class));
            }
            previousPage.hidePage();
            securityMenu.displayPage(printer);
        });
    }

    public void showSettingsMenu(Page previousPage, RootPrinter printer) {
        Platform.runLater(() -> {
            if (settingsMenu == null) {
                settingsMenu = (SettingsMenuController)(loadPage(menuURL, SettingsMenuController.class));
            }
            previousPage.hidePage();
            settingsMenu.displayPage(printer);
        });
    }
    
    public void showServerSettingsMenu(Page previousPage) {
        Platform.runLater(() -> {
            if (serverSettingsMenu == null) {
                serverSettingsMenu = (ServerSettingsMenuController)(loadPage(menuURL, ServerSettingsMenuController.class));
            }
            previousPage.hidePage();
            serverSettingsMenu.displayPage(null);
        });
    }

    public void showTestAxisSpeedMenu(Page previousPage, RootPrinter printer) {
        Platform.runLater(() -> {
            if (testAxisSpeedMenu == null) {
                testAxisSpeedMenu = (TestAxisSpeedMenuController)(loadPage(menuURL, TestAxisSpeedMenuController.class));
            }
            previousPage.hidePage();
            testAxisSpeedMenu.displayPage(printer);
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
    
    private Page loadPage(URL pageURL, Class controller) {
        Page page = null;
        try {
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
        }
        catch (IOException ex) {
            System.out.println(ex);
            ex.printStackTrace(System.err);
            System.exit(1);
        }
        return page;
    }
    
    private void setAnchors(Node n) {
        AnchorPane.setBottomAnchor(n, offsets.getBottom());
        AnchorPane.setTopAnchor(n, offsets.getTop());
        AnchorPane.setLeftAnchor(n, offsets.getLeft());
        AnchorPane.setRightAnchor(n, offsets.getRight());
    }
}
