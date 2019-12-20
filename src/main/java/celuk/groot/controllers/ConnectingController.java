package celuk.groot.controllers;

import celuk.groot.remote.RootPrinter;
import celuk.groot.remote.ServerStatusResponse;
import celuk.language.I18n;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.beans.value.ChangeListener;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;

public class ConnectingController implements Initializable, Page {

    @FXML
    private StackPane connectingPane;
    
    @FXML
    private Label statusLabel;

    private ChangeListener<ServerStatusResponse> serverStatusListener = (ob, ov, nv) -> {
        if (nv != null)
            acknowledgeConnected();
    };

    private RootStackController rootController = null;
    
    @Override
    public void setRootStackController(RootStackController rootController) {
        this.rootController = rootController;
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        statusLabel.setText(I18n.t(statusLabel.getText()));
    }
    
    @Override
    public void startUpdates() {
        rootController.getRootServer().getCurrentStatusProperty().addListener(serverStatusListener);
    }
    
    @Override
    public void stopUpdates() {
        rootController.getRootServer().getCurrentStatusProperty().removeListener(serverStatusListener);
    }

    @Override
    public void displayPage(RootPrinter printer) {
        startUpdates();
        connectingPane.setVisible(true);
    }

    @Override
    public void hidePage() {
        stopUpdates();
        connectingPane.setVisible(false);
    }
    
    public void acknowledgeConnected() {
        rootController.showPrinterSelectPage(this);
    }
}
