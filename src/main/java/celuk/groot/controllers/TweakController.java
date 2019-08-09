package celuk.groot.controllers;

import celuk.groot.remote.FilamentDetails;
import celuk.groot.remote.PrinterStatusResponse;
import celuk.groot.remote.RootPrinter;
import celuk.language.I18n;
import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.collections.MapChangeListener;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

public class TweakController implements Initializable, Page {
    
    static final String NO_HEAD_KEY = "<none>";
    static final String DEFAULT_HEAD_KEY = "<default>";

    @FXML
    private StackPane tweakPane;

    @FXML
    void tweakAction(ActionEvent event) {
        if (rootController != null && event.getSource() instanceof Button) {
            try {
                Button b = (Button)event.getSource();
                switch(b.getId()) {
                    default:
                        break;
                }
            }
            catch (Exception ex) {
            }
        }
    }
    
    @FXML
    void leftButtonAction(ActionEvent event) {
    }
    
    @FXML
    void middleButtonAction(ActionEvent event) {
        if (rootController != null && event.getSource() instanceof Button)
            rootController.showHomePage(this, printer);
    }

    @FXML
    void rightButtonAction(ActionEvent event) {
    }

    private RootStackController rootController = null;
    private RootPrinter printer = null;
    
    private final MapChangeListener<String, RootPrinter> printerMapListener = (c) ->  {
        //System.out.println("RemoteServer::printerMapListener");
        checkPrinterExists();
    };

    private final ChangeListener<PrinterStatusResponse> printerStatusListener = (ob, ov, nv) -> {
        //System.out.println("RemotePrinter \"" + printer.getPrinterId() + "\"printerStatusListener");
        updatePrinterStatus(nv);
    };
    
    @Override
    public void setRootStackController(RootStackController rootController) {
        this.rootController = rootController;
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
    }
    
    @Override
    public void startUpdates() {
        printer.getRootServer().getCurrentPrinterMap().addListener(printerMapListener);
        printer.getCurrentStatusProperty().addListener(printerStatusListener);
        updatePrinterStatus(printer.getCurrentStatusProperty().get());
        checkPrinterExists();
    }
    
    @Override
    public void stopUpdates() {
        // Printer can be null if
        // the home page has never been shown.
        if (printer != null) {
            printer.getRootServer().getCurrentPrinterMap().removeListener(printerMapListener);
            printer.getCurrentStatusProperty().removeListener(printerStatusListener);
            printer = null;
        }
    }
    
    @Override
    public void displayPage(RootPrinter printer) {
        this.printer = printer;
        startUpdates();
        tweakPane.setVisible(true);
    }
    
    @Override
    public void hidePage() {
        stopUpdates();
        tweakPane.setVisible(false);
    }

    private void checkPrinterExists() {
        if (!rootController.getRootServer().checkPrinterExists(printer.getPrinterId())) {
            rootController.showPrinterSelectPage(this);
        }
    }

    private void updatePrinterStatus(PrinterStatusResponse printerStatus) {
        Platform.runLater(() -> {
        });
    }
}
