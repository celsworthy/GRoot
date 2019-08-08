package celuk.groot.controllers;

import celuk.groot.remote.PrinterStatusResponse;
import celuk.groot.remote.RootPrinter;
import celuk.groot.remote.RootServer;
import celuk.groot.remote.ServerStatusResponse;
import celuk.language.I18n;
import java.net.URL;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.stream.Collectors;
import javafx.application.Platform;
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
import javafx.scene.paint.Color;

public class PrinterSelectController implements Initializable {
    
    @FXML
    private StackPane printerSelectPane;
    @FXML
    private VBox printerVBox;
    @FXML
    private GridPane printerGrid;
    @FXML
    private Button printer00Button;
    @FXML
    private Button printer01Button;
    @FXML
    private Button printer10Button;
    @FXML
    private Button printer11Button;
    @FXML
    private Button printer20Button;
    @FXML
    private Button printer21Button;
    @FXML
    private VBox noPrintersVBox;
    @FXML
    private Label noPrintersLabel;
    @FXML
    private Label noPrintersDetailLabel;
    @FXML
    private HBox bottomBarHBox;
    @FXML
    private VBox rootIDVBox;
    @FXML
    private Label rootNameLabel;
    @FXML
    private Label rootAddressLabel;
    @FXML
    private Button settingsButton;
    private Button[] buttonArray = null;
    
    @FXML
    void selectPrinterAction(ActionEvent event)
    {
        if (rootController != null && event.getSource() instanceof Button)
        {
            Button b = (Button)event.getSource();
            stopUpdates();
            rootController.showHomePage((RootPrinter)b.getUserData());
        }
    }
    
    private RootStackController rootController = null;
    private RootServer rootServer = null;
    
    protected void setRootStackController(RootStackController rootController) {
        this.rootController = rootController;
        this.rootServer = rootController.getRootServer();
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        noPrintersLabel.setText(I18n.t(noPrintersLabel.getText()));
        noPrintersDetailLabel.setText(I18n.t(noPrintersDetailLabel.getText()));
        printerGrid.setManaged(false);
        printerGrid.setVisible(false);
        noPrintersVBox.setManaged(true);
        noPrintersVBox.setVisible(true);
        buttonArray = new Button[] {printer00Button, printer01Button, printer10Button, printer11Button, printer20Button, printer21Button};
        for (Button button : buttonArray)
            button.setGraphic(new ImageView());
    }
    
    public void stop() {
        stopUpdates();
    }       

    private ChangeListener<ServerStatusResponse> serverStatusListener = (ob, ov, nv) -> {
        //System.out.println("RemoteServer::serverStatusListener");
        updateServerStatus(nv);
    };
    
    private MapChangeListener<String, RootPrinter> printerMapListener = (c) ->  {
        //System.out.println("RemoteServer::printerMapListener");
        updatePrinterGrid();
    };
    
    private ChangeListener<PrinterStatusResponse> printerStatusListener = (ob, ov, nv) -> {
        //System.out.println("RemoteServer::printerStatusListener");
        updatePrinterStatus(nv);
    };

    public void startUpdates() {
        rootServer.getCurrentStatusProperty().addListener(serverStatusListener);
        rootServer.getCurrentPrinterMap().addListener(printerMapListener);
        updateServerStatus(rootServer.getCurrentStatusProperty().get());
        updatePrinterGrid();
    }
    
    public void stopUpdates() {
        rootServer.getCurrentStatusProperty().removeListener(serverStatusListener);
        rootServer.getCurrentPrinterMap().removeListener(printerMapListener);
    }

    private void updateServerStatus(ServerStatusResponse response) {
        Platform.runLater(() -> {
            if (response != null) {
                rootNameLabel.setText(response.getName());
                rootAddressLabel.setText(response.getServerIP());
            }
            else {
                rootNameLabel.setText("*");
                rootAddressLabel.setText("---.---.---.---");
            }
        });
    }

    private void updatePrinterGrid() {
        //System.out.println("RemoteServer::updatePrinterGrid");
        Platform.runLater(() -> {
            Map<String, RootPrinter> currentPrinterMap = rootServer.getCurrentPrinterMap();
               
            // Remove the lost printers.
            for (int index = 0; index < 6; ++index) {
                Button b = buttonArray[index];
                RootPrinter printer = (RootPrinter)b.getUserData();
                if (printer != null && !currentPrinterMap.containsKey(printer.getPrinterId()))
                {
                    printer.getCurrentStatusProperty().removeListener(printerStatusListener);
                    b.setUserData(null);
                }
            }
            
            if (currentPrinterMap.isEmpty()) {
                printerGrid.setManaged(false);
                printerGrid.setVisible(false);
                noPrintersVBox.setManaged(true);
                noPrintersVBox.setVisible(true);
            }
            else {
                printerGrid.setManaged(true);
                printerGrid.setVisible(true);
                noPrintersVBox.setManaged(false);
                noPrintersVBox.setVisible(false);

                Set<RootPrinter> knownPrinterSet = new HashSet<RootPrinter>();
                for (int index = 0; index < 6; ++index) {
                    Button b = buttonArray[index];
                    RootPrinter printer = (RootPrinter)b.getUserData();
                    if (printer != null)
                        knownPrinterSet.add(printer);
                }
                
                List<RootPrinter> foundPrinterList = currentPrinterMap.entrySet()
                                 .stream()
                                 .filter(e -> !knownPrinterSet.contains(e.getValue()))
                                 .map(e -> e.getValue())
                                 .collect(Collectors.toList());
                
                if (!foundPrinterList.isEmpty()) {
                    int fIndex = 0;
                    for (int index = 0; index < 6; ++index) {
                        Button b = buttonArray[index];
                        RootPrinter printer = (RootPrinter)b.getUserData();
                        if (printer == null) {
                            if (fIndex < foundPrinterList.size()) {
                                printer = foundPrinterList.get(fIndex);
                                b.setUserData(printer);
                                printer.getCurrentStatusProperty().addListener(printerStatusListener);
                                updatePrinterStatus(printer.getCurrentStatusProperty().get());
                                b.setVisible(true);
                                ++fIndex;
                            }
                            else
                            {
                                b.setVisible(false);
                                b.setUserData(null);
                                b.setText("-");
                                b.setStyle("");
                            }
                        }
                    }
                }
            }
        });
    }
    
    private void updatePrinterStatus(PrinterStatusResponse printerStatus) {
        for (int index = 0; index < 6; ++index) {
            Button b = buttonArray[index];
            RootPrinter p = (RootPrinter)b.getUserData();
            if (p != null && printerStatus != null && p.getPrinterId().equalsIgnoreCase(printerStatus.getPrinterID())) {
                MachineDetails md = MachineDetails.machineDetailsMap
                                                  .getOrDefault(printerStatus.getPrinterTypeCode(),
                                                                MachineDetails.defaultDetails);
                String statusIcon = md.getStatusIcon(printerStatus.getPrinterWebColourString());
                Image statusImage = new Image(getClass().getResourceAsStream(statusIcon));
                String buttonStyle = md.getComplimentaryOption(printerStatus.getPrinterWebColourString(), "printer-button-dark", "printer-button-light");
                Platform.runLater(() -> {
                    b.setText(printerStatus.getPrinterName());
                    ((ImageView)b.getGraphic()).setImage(statusImage);
                    b.setStyle("-fx-background-color: " + printerStatus.getPrinterWebColourString() +";");
                    List styleList = b.getStyleClass();
                    styleList.remove("printer-button-dark");
                    styleList.remove("printer-button-light");
                    styleList.add(buttonStyle);
                });
                break;
            }
        }
    }
/*
    var statusClass = null;
    if (printerData.printerStatusEnumValue.match("^PRINTING_PROJECT"))
    {
        statusClass = 'printing';
    }
    else if (printerData.printerStatusEnumValue.match("^PAUSED") ||
             printerData.printerStatusEnumValue.match("^PAUSE_PENDING"))
    {
        statusClass = 'paused';
    }
    else if (printerData.printerStatusEnumValue.match("^RESUME")
            || printerData.printerStatusEnumValue.match("^RESUME_PENDING"))
    {
        statusClass = 'printing';
    }
    else
    {
        statusClass = 'ready';
    }

    psel.addClass(colourClass)
        .find('.icon-status')
        .removeClass(colourClassToRemove)
        .addClass(colourClass + ' ' + statusClass);
    
    if (!printerData.printerStatusEnumValue.match("^IDLE")
        && printerData.totalDurationSeconds > 0)
    {
        var timeElapsed = printerData.totalDurationSeconds - printerData.etcSeconds;
        if (timeElapsed < 0)
        {
            timeElapsed = 0;
        }
        var progressPercent = (timeElapsed * 1.0 / printerData.totalDurationSeconds) * 100;
        psel.find('.rbx-progress')
            .removeClass('rbx-invisible')
            .find('.progress-bar')
            .css('width', progressPercent + '%');
    } 
    else
    {
        psel.find('.rbx-progress')
            .addClass('rbx-invisible')
            .find('.progress-bar')
            .css('width', '0%');
    }
*/


}
