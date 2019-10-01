package celuk.groot.controllers;

import celuk.groot.remote.ErrorDetails;
import celuk.groot.remote.RootPrinter;
import celuk.groot.remote.RootServer;
import celuk.language.I18n;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.collections.MapChangeListener;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;

/**
 *
 * @author Tony
 */
public class ErrorAlertController {
    private final Map<String, ChangeListener<Boolean>> listenerMap = new HashMap<>();
    private final RootServer server;
    private String abortText = "error.abort";
    private String clearText = "error.clear";
    private String continueText = "error.continue";
    private String dialogTitleText = "error.dialogTitle";
    private String retryText = "error.retry";
    private String ejectFilament1Text = "error.ejectFilament1";
    private String ejectFilament2Text = "error.ejectFilament2";
    private final Alert errorAlert = new Alert(Alert.AlertType.NONE); 
    MapChangeListener<String, RootPrinter> printerMapListener = (var c) -> {
        System.out.println("");
        processPrinterMap(c.getMap());
    };
    
    public ErrorAlertController(RootServer server) {
        this.server = server;
    }
    
    public void prepareDialog() {
        abortText = I18n.t(abortText);
        clearText = I18n.t(clearText);
        continueText = I18n.t(continueText);
        retryText = I18n.t(retryText);
        ejectFilament1Text = I18n.t(ejectFilament1Text);
        ejectFilament2Text = I18n.t(ejectFilament2Text);
        dialogTitleText = I18n.t(dialogTitleText);
        server.getCurrentPrinterMap().addListener(printerMapListener);
        processPrinterMap(server.getCurrentPrinterMap());
    }
    
    public RootServer getServer() {
        return server;
    }
    
    private void processPrinterMap(Map<? extends String, ? extends RootPrinter> printerMap) {
        List<String> lostPrinters = listenerMap.keySet()
                                               .stream()
                                               .filter(pid -> !printerMap.containsKey(pid))
                                               .collect(Collectors.toList());
        
        lostPrinters.forEach(pid -> {
            var p = printerMap.get(pid);
            if (p != null) {
                var l = listenerMap.get(pid);
                p.getActiveErrorHeartbeatProperty().removeListener(l);
                listenerMap.remove(pid);
            }
        });
        
        // Get a list of printers that are in the printer map, but do not have error listeners.        
        List<String> foundPrinters = printerMap.keySet()
                                               .stream()
                                               .filter(pid -> !listenerMap.containsKey(pid))
                                               .collect(Collectors.toList());
        foundPrinters.forEach(pid -> {
            RootPrinter printer = printerMap.get(pid);
            //System.out.println("Adding printer " + p.getPrinterName());
            ChangeListener<Boolean> l = (pr, ov, nv) -> {
                displayError(printer);
            };
            printer.getActiveErrorHeartbeatProperty().addListener(l);
            listenerMap.put(pid, l);
        });
    }
    
    private void displayError(RootPrinter printer) {
        Map<Integer, ErrorDetails> errorMap = printer.getActiveErrorMapProperty().get();
        //errorMap.forEach((errorCode, error) -> {
        //    System.err.println("Error code " 
        //                       + Integer.toString(errorCode)
        //                       + " - "
        //                       + error.getErrorTitle());
        //});
        if (errorMap != null) {
            Platform.runLater(() -> {
                handleError(printer, errorMap);
            });
        }
    }

    private void handleError(RootPrinter printer, Map<Integer, ErrorDetails> errorMap) {
 
        if (errorMap.isEmpty()) {
            if (errorAlert.isShowing()) {
                RootPrinter alertPrinter = (RootPrinter)errorAlert.getDialogPane().getUserData();
                if (alertPrinter != null && alertPrinter.getPrinterId().equals(printer.getPrinterId())) {
                    errorAlert.hide();
                    errorAlert.getDialogPane().setUserData(null);
                }
            }
        }
        else {
            if (errorAlert.isShowing()) {
                // An alert is already being shown. Do nothing.
                // If the alert is for another error, this one
                // will be shown once that one has been acknowledged.
            }
            else {
                // Select the highest priority error.
                String printerName = printer.getPrinterName();

                Map.Entry<Integer, ErrorDetails> errorEntry = errorMap.entrySet()
                    .stream()
                    .reduce(null, (a, b) -> (a != null && a.getKey() < b.getKey() ? a : b));

                int errorCode = errorEntry.getKey();
                ErrorDetails errorData = errorEntry.getValue();

                // Error needs to be raised for the user
                String errorMessage = errorData.getErrorMessage();
                if (errorMessage.length() > 504)
                    errorMessage = errorMessage.substring(0, 500).concat(" ...");
                errorAlert.setHeaderText(errorData.getErrorTitle());
                errorAlert.setContentText(errorMessage);
                errorAlert.setTitle(dialogTitleText.replace("#1", printerName));
                errorAlert.getDialogPane().setUserData(printer);
                List<ButtonType> buttonList = errorAlert.getButtonTypes();
                buttonList.clear();
                int options = errorData.getOptions();
                // ABORT(1),
                // CLEAR_CONTINUE(2),
                // RETRY(4),
                // OK(8),
                // OK_ABORT(16),
                // OK_CONTINUE(32);
                if ((options & 17) != 0) { // ABORT or OK_ABORT
                    buttonList.add(ButtonType.CANCEL);
                    Button b = (Button)errorAlert.getDialogPane().lookupButton(ButtonType.CANCEL);
                    b.setText(abortText);
                }
                else {
                }

                if (options == 0 || // Nothing or
                    (options & 46) != 0) { // CLEAR_CONTINUE or RETRY or OK or OK_CONTINUE.
                    String buttonText = ((options & 4) != 0 ? retryText : continueText);
                    buttonList.add(ButtonType.OK);
                    Button b = (Button)errorAlert.getDialogPane().lookupButton(ButtonType.OK);
                    b.setText(buttonText);
                }

                if (errorCode == 28) { // E_UNLOAD_ERROR
                    buttonList.add(ButtonType.APPLY);
                    Button b = (Button)errorAlert.getDialogPane().lookupButton(ButtonType.APPLY);
                    b.setText(ejectFilament2Text);
                }
                else if (errorCode == 29) { // D_UNLOAD_ERROR
                    // D_UNLOAD_ERROR
                    buttonList.add(ButtonType.APPLY);
                    Button b = (Button)errorAlert.getDialogPane().lookupButton(ButtonType.APPLY);
                    b.setText(ejectFilament1Text);
                }

                // Show the dialog.
                Optional<ButtonType> result = errorAlert.showAndWait();
                errorAlert.hide();
                if (result.isPresent()) {
                    ButtonType bt = result.get();
                    String typeCode = result.get().getButtonData().getTypeCode();
                    //System.out.println("Returned " + typeCode);
                    switch(typeCode) {
                        case "O":
                        {
                            // OK/Continue button.
                            printer.runResumeTask();
                            try {
                                printer.runClearErrorTask(errorCode).get();
                            }
                            catch (InterruptedException ex) {
                                System.err.println("Interrupt exception while clearing error");
                            } 
                            catch (ExecutionException ex) {
                                System.err.println("Execution exception while clearing error");
                            }
                            // OK/Continue button.
                            //System.out.println("OK");
                            //server.runBackgroundTask(() -> {
                            //    printer.runResumeTask();
                            //    printer.runClearErrorTask(errorCode);
                            //    return null;
                            //});
                            break;
                        }

                        case "A":
                        {
                            // Apply/Eject button.
                            printer.runEjectFilamentTask(errorCode == 28 ? 1 : 0);
                            try {
                                printer.runClearErrorTask(errorCode).get();
                            }
                            catch (InterruptedException ex) {
                                System.err.println("Interrupt exception while clearing error");
                            } 
                            catch (ExecutionException ex) {
                                System.err.println("Execution exception while clearing error");
                            }
                            // Apply/Eject button.
                            //System.out.println("Eject");
                            //server.runBackgroundTask(() -> {
                            //    printer.runEjectFilamentTask(errorCode == 28 ? 1 : 0);
                            //    printer.runClearErrorTask(errorCode);
                            //    return null;
                            //});
                            break;
                        }
                        
                        case "C":
                        {
                            // Cancel/Abort button.
                            printer.runCancelTask();
                            try {
                                printer.runClearErrorTask(errorCode).get();
                            }
                            catch (InterruptedException ex) {
                                System.err.println("Interrupt exception while clearing error");
                            } 
                            catch (ExecutionException ex) {
                                System.err.println("Execution exception while clearing error");
                            }
                            // Cancel/Abort button.
                            //System.out.println("Abort");
                            //server.runBackgroundTask(() -> {
                            //    printer.runCancelTask();
                            //    printer.runClearErrorTask(errorCode);
                            //    return null;
                            //});
                            break;
                        }
                        
                        default:
                            break;
                    }
                }

                printer.acknowledgeError(errorData);
            }
        }
    }
}
