package celuk.groot.remote;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadFactory;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class RootPrinter extends Updater {
    
    private static final String CANCEL_COMMAND = "/remoteControl/cancel";
    private static final String EJECT_FILAMENT_COMMAND = "/remoteControl/ejectFilament";
    private static final String EXECUTE_GCODE_COMMAND = "/remoteControl/executeGCode";
    private static final String MACRO_COMMAND = "/remoteControl/runMacro";
    private static final String PAUSE_COMMAND = "/remoteControl/pause";
    private static final String REMOTE_CONTROL_COMMAND = "/remoteControl";
    private static final String RESUME_COMMAND = "/remoteControl/resume";
    private static final String SWITCH_AMBIENT_LIGHT_COMMAND = "/remoteControl/setAmbientLED";
    
    private static final String COMMAND_PREFIX = "/api/";
    private final RootServer rootServer;
    private final String printerId;
    private final SimpleObjectProperty<PrinterStatusResponse> currentStatusProperty = new SimpleObjectProperty<>();
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
    
    public RootServer getRootServer() {
        return rootServer;
    }

    private Future<PrinterStatusResponse> runRequestPrinterStatusTask() {
        //System.out.println("Requesting status of printer \"" + printerId + "\"");
        return rootServer.runRequestTask(COMMAND_PREFIX + printerId + REMOTE_CONTROL_COMMAND, true, null,
            (byte[] requestData, ObjectMapper jMapper) -> {
                PrinterStatusResponse statusResponse = null;
                try {
                    if (requestData.length > 0) {
                        //System.out.println("Updating printer status of \"" + printerId + "\" - \"" + new String(requestData) + "\"");
                        statusResponse = rootServer.getMapper().readValue(requestData, PrinterStatusResponse.class);
                        currentStatusProperty.set(statusResponse);
                    }
                }
                catch (IOException ex) {
                    System.err.println("Error whilst decoding status response from @" + rootServer.getHostAddress() + ":" + rootServer.getHostPort() + " - " + ex);;
                }
                return statusResponse;
            });
    }
    
    @Override
    protected void update() {
        runRequestPrinterStatusTask();
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
}
