package celuk.groot.remote;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;
import java.util.stream.Collectors;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;

public class RootPrinter extends Updater {
    
    private static final String CANCEL_COMMAND = "/remoteControl/cancel";
    private static final String EJECT_FILAMENT_COMMAND = "/remoteControl/ejectFilament";
    private static final String EXECUTE_GCODE_COMMAND = "/remoteControl/executeGCode";
    private static final String MACRO_COMMAND = "/remoteControl/runMacro";
    private static final String PAUSE_COMMAND = "/remoteControl/pause";
    private static final String PRINT_ADJUST_COMMAND = "/remoteControl/printAdjust";
    private static final String SET_PRINT_ADJUST_COMMAND = "/remoteControl/setPrintAdjust";
    private static final String REMOTE_CONTROL_COMMAND = "/remoteControl";
    private static final String RESUME_COMMAND = "/remoteControl/resume";
    private static final String SWITCH_AMBIENT_LIGHT_COMMAND = "/remoteControl/setAmbientLED";
    private static final String ERROR_STATUS_COMMAND = "/remoteControl/activeErrorStatus";
    
    private static final String COMMAND_PREFIX = "/api/";
    private final RootServer rootServer;
    private final String printerId;
    private final SimpleObjectProperty<PrinterStatusResponse> currentStatusProperty = new SimpleObjectProperty<>();
    private final SimpleObjectProperty<PrintAdjustData> currentPrintAdjustDataProperty = new SimpleObjectProperty<>();
    private final SimpleObjectProperty<Map<Integer, ErrorDetails>> activeErrorMapProperty = new SimpleObjectProperty<>();
    private Map<Integer, ErrorDetails> acknowledgedErrorMap = new HashMap<>();
    private boolean safetiesOn = true;
    
    public RootPrinter(RootServer rootServer, String printerId) {
        super();
        this.rootServer = rootServer;
        this.printerId = printerId;
    }
    
    public String getPrinterId() {
        return printerId;
    }
    
    public SimpleObjectProperty<PrinterStatusResponse> getCurrentStatusProperty() {
        return currentStatusProperty;
    }
    
    public SimpleObjectProperty<PrintAdjustData> getCurrentPrintAdjustDataProperty() {
        return currentPrintAdjustDataProperty;
    }

    public RootServer getRootServer() {
        return rootServer;
    }

    public Future<PrinterStatusResponse> runRequestPrinterStatusTask() {
        //System.out.println("Requesting status of printer \"" + printerId + "\"");
        return rootServer.runRequestTask(COMMAND_PREFIX + printerId + REMOTE_CONTROL_COMMAND, true, null,
            (byte[] requestData, ObjectMapper jMapper) -> {
                PrinterStatusResponse statusResponse = null;
                try {
                    if (requestData.length > 0) {
                        //System.out.println("Updating printer status of \"" + printerId + "\" - \"" + new String(requestData) + "\"");
                        statusResponse = jMapper.readValue(requestData, PrinterStatusResponse.class);
                        currentStatusProperty.set(statusResponse);
                        if (statusResponse.getPrinterStatusEnumValue().startsWith("PRINTING_PROJECT")
                            || statusResponse.getPrinterStatusEnumValue().startsWith("RUNNING_MACRO")
                            || statusResponse.getPrinterStatusEnumValue().startsWith("PAUSED")
                            || statusResponse.getPrinterStatusEnumValue().startsWith("PAUSE_PENDING")
                            || statusResponse.getPrinterStatusEnumValue().startsWith("RESUME_PENDING")
                            || statusResponse.getPrinterStatusEnumValue().startsWith("HEATING"))
                        {
                            runRequestPrintAdjustDataTask();
                        }
                        else
                        {
                            // There is a race condition between this and any activ.e requestPrintAdjustDataTask
                            // which could result in out-of-date data being available. I don't think this matters
                            // and should be cleared by the next update.
                            currentPrintAdjustDataProperty.set(null);
                        }
                    }
                }
                catch (IOException ex) {
                    System.err.println("Error whilst decoding status response from @" + rootServer.getHostAddress() + ":" + rootServer.getHostPort() + " - " + ex);;
                }
                return statusResponse;
            });
    }
    
    private Future<PrintAdjustData> runRequestPrintAdjustDataTask() {
        //System.out.println("Requesting status of printer \"" + printerId + "\"");
        return rootServer.runRequestTask(COMMAND_PREFIX + printerId + PRINT_ADJUST_COMMAND, true, null,
            (byte[] requestData, ObjectMapper jMapper) -> {
                PrintAdjustData adjustData = null;
                try {
                    if (requestData.length > 0) {
                        //System.out.println("Updating print adjust data of \"" + getPrinterId() + "\" - \"" + new String(requestData) + "\"");
                        adjustData = jMapper.readValue(requestData, PrintAdjustData.class);
                    }
                }
                catch (IOException ex) {
                    System.err.println("Error whilst decoding print adjust data from @" + getRootServer().getHostAddress() + ":" + getRootServer().getHostPort() + " - " + ex);;
                }
                currentPrintAdjustDataProperty.set(adjustData);
                return adjustData;
            });
    }

    private void processErrorList(List<ErrorDetails> errorList) {
        Map<Integer, ErrorDetails> activeMap = new HashMap<>();
        Map<Integer, ErrorDetails> ackMap = new HashMap<>();
        if (errorList != null) {
            errorList.forEach(e -> {
                if (acknowledgedErrorMap.containsKey(e.getErrorCode()))
                    ackMap.put(e.getErrorCode(), e);
                else
                    activeMap.put(e.getErrorCode(), e);
            });
        }
        acknowledgedErrorMap = ackMap;
        activeErrorMapProperty.set(activeMap);
        if (!activeMap.isEmpty()) {
            System.err.println("Errors on printer \"" + currentStatusProperty.get().getPrinterName() + "\"");
            activeMap.forEach((ec, e) -> {
                System.err.println("    Error " + Integer.toString(ec) + ": " + e.getErrorTitle() + " - " + e.getErrorMessage());
            });
        }
    }

    private Future<ActiveErrorStatusData> runRequestErrorStatusTask() {
        //System.out.println("Requesting error status of printer \"" + printerId + "\"");
        return rootServer.runRequestTask(COMMAND_PREFIX + printerId + ERROR_STATUS_COMMAND, true, null,
            (byte[] requestData, ObjectMapper jMapper) -> {
                ActiveErrorStatusData activeErrorData = null;
                try {
                    if (requestData.length > 0) {
                        //System.out.println("Updating error status of \"" + getPrinterId() + "\" - \"" + new String(requestData) + "\"");
                        activeErrorData = jMapper.readValue(requestData, ActiveErrorStatusData.class);
                        processErrorList(activeErrorData.getActiveErrors());
                    }
                }
                catch (IOException ex) {
                    System.err.println("Error whilst decoding error status of @" + getRootServer().getHostAddress() + ":" + getRootServer().getHostPort() + " - " + ex);;
                }
                return activeErrorData;
            });
    }

    @Override
    protected void update() {
        runRequestPrinterStatusTask();
        runRequestErrorStatusTask();
    }
    
    public Future<Boolean> runEjectFilamentTask(int filamentNumber) {
        String f = Integer.toString(filamentNumber);
        return rootServer.runRequestTask(COMMAND_PREFIX + printerId + EJECT_FILAMENT_COMMAND,
            false,
            f,
            (byte[] requestData, ObjectMapper jMapper) -> {
                return true;
            });
    }
    
    public Future<String> runSendGCodeTask(String gCode) {
        return rootServer.runRequestTask(COMMAND_PREFIX + printerId + EXECUTE_GCODE_COMMAND,
            false,
            gCode,
            (byte[] requestData, ObjectMapper jMapper) -> {
                String response = "";
                try {
                    if (requestData.length > 0) {
                        //System.out.println("Updating printer status of \"" + printerId + "\"");
                        response = jMapper.readValue(requestData, String.class);
                    }
                }
                catch (IOException ex) {
                    System.err.println("Error when executing GCode command \"" + gCode + "\" on @" + rootServer.getHostAddress() + ":" + rootServer.getHostPort() + " - " + ex);;
                }
                return response;
            });
    }

    public Future<String> runUnlockDoorTask() {
        return runSendGCodeTask("G37 S");
    }

    private Future<Boolean> runBooleanTask(String command, String data) {
        return rootServer.runRequestTask(COMMAND_PREFIX + printerId + command,
            false,
            data,
            (byte[] requestData, ObjectMapper jMapper) -> {
                runRequestPrinterStatusTask();
                return true;
            });
    }

    public Future<Boolean> runPauseTask() {
        return runBooleanTask(PAUSE_COMMAND, null);
    }
    
    public Future<Boolean> runResumeTask() {
        return runBooleanTask(RESUME_COMMAND, null);
    }

    public Future<Boolean> runCancelTask() {
        return runBooleanTask(CANCEL_COMMAND, safetiesOn ? "\"true\"" : "\"false\"");
    }

    public Future<Boolean> runMacroTask(String macro) {
        return runBooleanTask(MACRO_COMMAND, macro);
    }

    public Future<Boolean> runSwitchAmbientLightTask(String state) {
        return runBooleanTask(SWITCH_AMBIENT_LIGHT_COMMAND, state);
    }

    public Future<Boolean> runSetPrintAdjustDataTask(String data) {
        return runBooleanTask(SET_PRINT_ADJUST_COMMAND, data);
    }
}
