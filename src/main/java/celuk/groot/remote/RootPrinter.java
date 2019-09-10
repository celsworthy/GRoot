package celuk.groot.remote;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;

public class RootPrinter extends Updater {
    
    private static final String CANCEL_COMMAND = "/remoteControl/cancel";
    private static final String COMMAND_PREFIX = "/api/";
    private static final String CHANGE_PRINTER_COLOUR_COMMAND = "/remoteControl/changePrinterColour";
    private static final String CLEAN_NOZZLE_COMMAND = "/remoteControl/cleanNozzle";
    private static final String EJECT_FILAMENT_COMMAND = "/remoteControl/ejectFilament";
    private static final String EJECT_STUCK_MATERIAL_COMMAND = "/remoteControl/ejectStuckMaterial";
    private static final String ERROR_STATUS_COMMAND = "/remoteControl/activeErrorStatus";
    private static final String EXECUTE_GCODE_COMMAND = "/remoteControl/executeGCode";
    private static final String HEAD_EEPROM_COMMAND = "/remoteControl/headEEPROM";
    private static final String LIST_REPRINTABLE_JOBS_COMMAND = "/remoteControl/listReprintableJobs";
    private static final String LIST_USB_PRINTABLE_JOBS_COMMAND = "/remoteControl/listUSBPrintableJobs";
    private static final String MATERIAL_STATUS_COMMAND = "/remoteControl/materialStatus";
    private static final String MACRO_COMMAND = "/remoteControl/runMacro";
    private static final String PAUSE_COMMAND = "/remoteControl/pause";
    private static final String PRINT_ADJUST_COMMAND = "/remoteControl/printAdjust";
    private static final String PRINT_USB_JOB_COMMAND = "/remoteControl/printUSBJob";
    private static final String PURGE_TO_TARGET_COMMAND = "/remoteControl/purgeToTarget";
    private static final String RENAME_PRINTER_COMMAND = "/remoteControl/renamePrinter";
    private static final String REMOTE_CONTROL_COMMAND = "/remoteControl";
    private static final String REMOVE_HEAD_COMMAND = "/remoteControl/removeHead";
    private static final String REPRINT_JOB_COMMAND = "/remoteControl/reprintJob";
    private static final String RESUME_COMMAND = "/remoteControl/resume";
    private static final String SET_PRINT_ADJUST_COMMAND = "/remoteControl/setPrintAdjust";
    private static final String SWITCH_AMBIENT_LIGHT_COMMAND = "/remoteControl/setAmbientLED";
    private final RootServer rootServer;
    private final String printerId;
    private final SimpleObjectProperty<PrinterStatusResponse> currentStatusProperty = new SimpleObjectProperty<>();
    private final SimpleObjectProperty<PrintAdjustData> currentPrintAdjustDataProperty = new SimpleObjectProperty<>();
    private final SimpleObjectProperty<HeadEEPROMData> currentHeadEEPROMDataProperty = new SimpleObjectProperty<>();
    private final SimpleObjectProperty<MaterialStatusData> currentMaterialStatusDataProperty = new SimpleObjectProperty<>();
    private final SimpleObjectProperty<PurgeData> currentPurgeDataProperty = new SimpleObjectProperty<>();
    private final SimpleObjectProperty<Map<Integer, ErrorDetails>> activeErrorMapProperty = new SimpleObjectProperty<>();
    private final SimpleBooleanProperty safetiesOnProperty = new SimpleBooleanProperty(true);
    private Set<Integer> acknowledgedErrorSet = new HashSet<>();
    
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

    public SimpleObjectProperty<HeadEEPROMData> getCurrentHeadEEPROMDataProperty() {
        return currentHeadEEPROMDataProperty;
    }

    public SimpleObjectProperty<MaterialStatusData> getCurrentMaterialStatusDataProperty() {
        return currentMaterialStatusDataProperty;
    }

    public SimpleObjectProperty<PurgeData> getCurrentPurgeDataProperty() {
        return currentPurgeDataProperty;
    }

    public SimpleBooleanProperty getSafetiesOnProperty() {
        return safetiesOnProperty;
    }

    public SimpleObjectProperty<Map<Integer, ErrorDetails>> getActiveErrorMapProperty() {
        return activeErrorMapProperty;
    }

    public void acknowledgeError(ErrorDetails error) {
        acknowledgedErrorSet.add(error.getErrorCode());
    }

    public RootServer getRootServer() {
        return rootServer;
    }

    public Future<PrinterStatusResponse> runRequestPrinterStatusTask() {
        //System.out.println("Requesting status of printer \"" + printerId + "\"");
        return rootServer.runRequestTask(COMMAND_PREFIX + printerId + REMOTE_CONTROL_COMMAND, true, null,
            (var requestData, var jMapper) -> {
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
                            // There is a race condition between this and any active requestPrintAdjustDataTask
                            // which could result in out-of-date data being available. I don't think this matters
                            // and should be cleared by the next update.
                            currentPrintAdjustDataProperty.set(null);
                        }
                    }
                }
                catch (IOException ex) {
                    System.err.println("Error whilst decoding status response from @" 
                                       + rootServer.getHostAddress()
                                       + ":" 
                                       + rootServer.getHostPort() 
                                       + " - " 
                                       + ex.getMessage());
                }
                return statusResponse;
            });
    }
    
    private Future<PrintAdjustData> runRequestPrintAdjustDataTask() {
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
                    System.err.println("Error whilst decoding print adjust data from @" 
                                       + getRootServer().getHostAddress() 
                                       + ":" + getRootServer().getHostPort()
                                       + " - " 
                                       + ex.getMessage());
                }
                currentPrintAdjustDataProperty.set(adjustData);
                return adjustData;
            });
    }

    private void processErrorList(List<ErrorDetails> errorList) {
        Map<Integer, ErrorDetails> activeMap = new HashMap<>();
        Set<Integer> ackSet = new HashSet<>();
        if (errorList != null) {
            errorList.forEach(e -> {
                if (acknowledgedErrorSet.contains(e.getErrorCode()))
                    ackSet.add(e.getErrorCode());
                else
                    activeMap.put(e.getErrorCode(), e);
            });
        }
        acknowledgedErrorSet = ackSet;
        activeErrorMapProperty.set(activeMap);
        if (!activeMap.isEmpty()) {
            System.err.println("Errors on printer \"" 
                               + currentStatusProperty.get().getPrinterName()
                               + "\"");
            activeMap.forEach((ec, e) -> {
                System.err.println("    Error "
                                   + Integer.toString(ec)
                                   + ": " + e.getErrorTitle()
                                   + " - " 
                                   + e.getErrorMessage());
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
                    System.err.println("Error whilst decoding error status of @"
                                       + getRootServer().getHostAddress() 
                                       + ":" + getRootServer().getHostPort() 
                                       + " - " 
                                       + ex.getMessage());
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
                        String[] r = jMapper.readValue(requestData, String[].class);
                        if (r.length > 0 && r[0] != null)
                            response = r[0].trim();
                    }
                }
                catch (IOException ex) {
                    System.err.println("Error when executing GCode command \""
                                        + gCode
                                        + "\" on @"
                                        + rootServer.getHostAddress()
                                        + ":"
                                        + rootServer.getHostPort()
                                        + " - "
                                        + ex.getMessage());
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
        return runBooleanTask(CANCEL_COMMAND, safetiesOnProperty.get() ? "\"true\""
                                                                       : "\"false\"");
    }

    public Future<Boolean> runMacroTask(String macro) {
        return runBooleanTask(MACRO_COMMAND, macro);
    }

    public Future<Boolean> runSwitchAmbientLightTask(String state) {
        String data = String.format("\"%s\"", state);
        return runBooleanTask(SWITCH_AMBIENT_LIGHT_COMMAND, state);
    }

    public Future<Boolean> runSetPrintAdjustDataTask(String data) {
        return runBooleanTask(SET_PRINT_ADJUST_COMMAND, data);
    }
    
    public Future<Boolean> runRenamePrinterTask(String printerName) {
        String data = String.format("\"%s\"", printerName);
        return runBooleanTask(RENAME_PRINTER_COMMAND, data);
    }

    public Future<Boolean> runReprintJobTask(String printJobID) {
        return runBooleanTask(REPRINT_JOB_COMMAND, printJobID);
    }
    
    public Future<Boolean> runPrintUSBJobTask(String printJobID, String printJobPath) {
        String data = String.format("{\"printJobID\":\"%s\",\"printJobPath\":\"%s\"}", printJobID, printJobPath);
        return runBooleanTask(PRINT_USB_JOB_COMMAND, data);
    }

    public Future<Boolean> runRemoveHeadTask() {
        return runBooleanTask(REMOVE_HEAD_COMMAND, safetiesOnProperty.get() ? "\"true\""
                                                                            : "\"false\"");
    }

    public Future<Boolean> runChangePrinterColourTask(String printerColour) {
        String data = String.format("\"%s\"", printerColour);
        return runBooleanTask(CHANGE_PRINTER_COLOUR_COMMAND, data);
    }

    public Future<Boolean> runCleanNozzleTask(int nozzleNumber) {
        String data = String.format("\"%d\"", nozzleNumber);
        return runBooleanTask(CLEAN_NOZZLE_COMMAND, data);
    }

    public Future<Boolean> runEjectStuckMaterialTask(int materialNumber) {
        String data = String.format("\"%d\"", materialNumber);
        return runBooleanTask(EJECT_STUCK_MATERIAL_COMMAND, data);
    }

    public Future<PrintJobListData> runListPrintableJobsTask(boolean reprintableMode) {
        String command = reprintableMode ? LIST_REPRINTABLE_JOBS_COMMAND : LIST_USB_PRINTABLE_JOBS_COMMAND;
        return rootServer.runRequestTask(COMMAND_PREFIX + printerId + command,
            false,
            null,
            (byte[] requestData, ObjectMapper jMapper) -> {
                PrintJobListData printJobList = null;
                try {
                    if (requestData.length > 0) {
                        //System.out.println("Listing printable jobs from \"" + getPrinterId() + "\" - \"" + new String(requestData) + "\"");
                        printJobList = jMapper.readValue(requestData, PrintJobListData.class);
                    }
                }
                catch (IOException ex) {
                    System.err.println("Error whilst decoding print job list from @"
                                       + getRootServer().getHostAddress()
                                       + ":"
                                       + getRootServer().getHostPort()
                                       + " - "
                                       + ex.getMessage());
                }
                return printJobList;
            });
    }
    
    public Future<HeadEEPROMData> runRequestHeadEEPROMDataTask() {
        //System.out.println("Requesting head EEPROM data from printer \"" + printerId + "\"");
        return rootServer.runRequestTask(COMMAND_PREFIX + printerId + HEAD_EEPROM_COMMAND, true, null,
            (byte[] requestData, ObjectMapper jMapper) -> {
                HeadEEPROMData headData = null;
                try {
                    if (requestData.length > 0) {
                        //System.out.println("Updating print adjust data of \"" + getPrinterId() + "\" - \"" + new String(requestData) + "\"");
                        headData = jMapper.readValue(requestData, HeadEEPROMData.class);
                    }
                }
                catch (IOException ex) {
                    System.err.println("Error whilst decoding print adjust data from @" 
                                       + getRootServer().getHostAddress() 
                                       + ":" + getRootServer().getHostPort()
                                       + " - " 
                                       + ex.getMessage());
                }
                currentHeadEEPROMDataProperty.set(headData);
                return headData;
            });
    }
    
    public Future<Boolean> runWriteHeadEEPROMDataTask(HeadEEPROMData headData) {
        //System.out.println("Writing head EEPROM data from printer \"" + printerId + "\"");
        Future<Boolean> f;
        try {
            String mappedData = rootServer.getMapper().writeValueAsString(headData);
            f = runBooleanTask(RESUME_COMMAND, mappedData);
        } 
        catch (JsonProcessingException ex) {
            f = new CompletableFuture<>();
            ((CompletableFuture)f).completeExceptionally(ex);
        }
        return f;
    }

    public Future<MaterialStatusData> runRequestMaterialStatusTask() {
        //System.out.println("Requesting material status from printer \"" + printerId + "\"");
        return rootServer.runRequestTask(COMMAND_PREFIX + printerId + MATERIAL_STATUS_COMMAND, true, null,
            (byte[] requestData, ObjectMapper jMapper) -> {
                MaterialStatusData materialStatus = null;
                try {
                    if (requestData.length > 0) {
                        //System.out.println("Updating print adjust data of \"" + getPrinterId() + "\" - \"" + new String(requestData) + "\"");
                        materialStatus = jMapper.readValue(requestData, MaterialStatusData.class);
                    }
                }
                catch (IOException ex) {
                    System.err.println("Error whilst decoding print adjust data from @" 
                                       + getRootServer().getHostAddress() 
                                       + ":" + getRootServer().getHostPort()
                                       + " - " 
                                       + ex.getMessage());
                }
                currentMaterialStatusDataProperty.set(materialStatus);
                return materialStatus;
            });
    }

    public Future<PurgeData> runRequestPurgeDataTask() {
        Future<HeadEEPROMData> headFuture = runRequestHeadEEPROMDataTask();
        Future<MaterialStatusData> materialFuture = runRequestMaterialStatusTask();
        PurgeData pData = new PurgeData();
        CompletableFuture<PurgeData> f = new CompletableFuture<>();
        try {
            pData.setMaterialStatus(materialFuture.get());
            pData.setHeadData(headFuture.get());
            currentPurgeDataProperty.set(pData);
            f.complete(pData); 
        } catch (InterruptedException | ExecutionException ex) {
            f.completeExceptionally(ex);
        }

        return f;
    }
    
    public Future<Boolean> runPurgeTask(PurgeTarget targetData) {
        //System.out.println("Writing head EEPROM data from printer \"" + printerId + "\"");
        Future<Boolean> f;
        try {
            String mappedData = rootServer.getMapper().writeValueAsString(targetData);
            f = runBooleanTask(PURGE_TO_TARGET_COMMAND, mappedData);
        } 
        catch (JsonProcessingException ex) {
            f = new CompletableFuture<>();
            ((CompletableFuture)f).completeExceptionally(ex);
        }
        return f;
    }
}
