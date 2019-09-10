package celuk.groot.remote;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadFactory;
import java.util.function.BiFunction;
import java.util.stream.Collectors;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableMap;

public class RootServer extends Updater {
    
    public static final int CONNECT_TIMEOUT_LONG = 2000;
    public static final int CONNECT_TIMEOUT_SHORT = 300;
    public static final int READ_TIMEOUT_LONG = 15000;
    public static final int READ_TIMEOUT_SHORT = 1500;
    protected static final String USER_AGENT = "CEL Robox";
    protected static final String HTTP_PREFIX = "http://";
    //protected static final String LOCAL_HOST = "localhost";
    //protected static final String HTTP_ADDRESS = "192.168.1.141";
    //protected static final String HTTP_ADDRESS = "localhost";
    //protected static final String HTTP_PORT = "8080";
    private static final String ACCESS_PIN_COMMAND = "/api/admin/updatePIN";
    private static final String RESET_PIN_COMMAND = "/api/admin/resetPIN";
    private static final String LIST_PRINTERS_COMMAND = "/api/discovery/listPrinters";
    private static final String WHO_ARE_YOU_COMMAND = "/api/discovery/whoareyou?pc";
    
    protected final ObjectMapper mapper = new ObjectMapper();
    protected final ExecutorService executorService;
    protected SimpleStringProperty pinProperty = new SimpleStringProperty("1111");

    private final SimpleObjectProperty<ServerStatusResponse> currentStatusProperty = new SimpleObjectProperty<>();
    private final ObservableMap<String, RootPrinter> currentPrinterMap = FXCollections.observableMap(new HashMap<>());

    private final String hostAddress;
    private final String hostPort;
    
    public RootServer(String hostAddress, String hostPort) {
        super();
        this.hostAddress = hostAddress;
        this.hostPort = hostPort;
        ThreadFactory threadFactory = (Runnable runnable) -> {
            Thread thread = Executors.defaultThreadFactory().newThread(runnable);
            thread.setDaemon(true);
            return thread;
        };
        executorService = Executors.newFixedThreadPool(6, threadFactory);
    }
    
    protected ExecutorService getExecutorService() {
        return executorService;
    }
    
    protected ObjectMapper getMapper() {
        return mapper;
    }
    
    public ObservableMap<String, RootPrinter> getCurrentPrinterMap() {
        return currentPrinterMap;
    }

    public SimpleObjectProperty<ServerStatusResponse> getCurrentStatusProperty() {
        return currentStatusProperty;
    }

    public String getName() {
        return currentStatusProperty.get().getName();
    }
    
    public String getServerIP() {
        return currentStatusProperty.get().getServerIP();
    }

    public String getServerVersion() {
        return currentStatusProperty.get().getServerVersion();
    }

    public List<String> getPrinterColours() {
        return currentStatusProperty.get().getPrinterColours();
    }
    
        public SimpleStringProperty getPINProperty() {
        return pinProperty;
    }
    
    public String getPIN() {
        return pinProperty.get();
    }
    
    public void setPIN(String pin) {
        pinProperty.set(pin);
    }

    public String getHostAddress() {
        return hostAddress;
    }

    public String getHostPort() {
        return hostPort;
    }

    protected byte[] makeHTTPRequest(String request, boolean isGetRequest, String content) {
        String url = HTTP_PREFIX + hostAddress + ":" + hostPort + request;
        byte[] requestData = null;

        long t1 = System.currentTimeMillis();
        try {
            URL obj = new URL(url);
            HttpURLConnection con = (HttpURLConnection) obj.openConnection();

            con.setRequestMethod(isGetRequest ? "GET" : "POST");

            //add request header
            con.setRequestProperty("User-Agent", USER_AGENT);
            con.setRequestProperty("Authorization", "Basic " + StringToBase64Encoder.encode("root:" + getPIN()));

            con.setConnectTimeout(CONNECT_TIMEOUT_SHORT);
            con.setReadTimeout(READ_TIMEOUT_SHORT);

            if (content != null) {
                con.setDoOutput(true);
                con.setRequestProperty("Content-Type", "application/json");
                con.setRequestProperty("Content-Length", "" + content.length());
                con.getOutputStream().write(content.getBytes());
            }

            int responseCode = con.getResponseCode();

            if (responseCode == 200) {
                int availChars = con.getInputStream().available();
                requestData = new byte[availChars];
                con.getInputStream().read(requestData, 0, availChars);
            }
            else if (responseCode == 204) {
                requestData = new byte[0];
            }
            else {
                System.out.println("Invalid response (" + Integer.toString(responseCode) +") after making request \"" + request + "\" from @" + hostAddress + ":" + hostPort);
            }
        } 
        catch (java.net.SocketTimeoutException ex) {
            long t2 = System.currentTimeMillis();
            System.out.println("Timeout whilst making request \"" + request + "\" from @" + hostAddress + ":" + hostPort + " - time taken = " + Long.toString(t2 - t1));
        }
        catch (IOException ex) {
            System.out.println("Error whilst making request \"" + request + "\" from @" + hostAddress + ":" + hostPort + " - " + ex);
        }
        
        return requestData;
    }

    public <R> Future<R> runRequestTask(String command, boolean isGetRequest, String content, BiFunction<byte[], ObjectMapper, R> responseMapper) {
        return executorService.submit(() -> {
            R response = null;
            try {
                byte[] requestData = makeHTTPRequest(command, isGetRequest, content);
                if (requestData != null) {
                    // System.out.println("Calling response mapper for \"" + command + "\"");
                    response = responseMapper.apply(requestData, mapper);
                }
            }
            catch (Exception ex) {
                System.out.println("Error whilst requesting \"" + command + "\" from @" + hostAddress + ":" + hostPort + " - " + ex);
            }
            return response;
        });
    }

    public Future<ObservableMap<String, RootPrinter>> runListAttachedPrintersTask() {
        return runRequestTask(LIST_PRINTERS_COMMAND, true, null,
            (byte[] requestData, ObjectMapper jMapper) -> {
                try {
                    if (requestData.length > 0) {
                        //System.out.println("Updating server printer list");
                        ListPrintersResponse listPrintersResponse = jMapper.readValue(requestData, ListPrintersResponse.class);
                        listPrintersResponse.getPrinterIDs()
                                .stream()
                                    .filter(pid -> !currentPrinterMap.containsKey(pid))
                                    .forEach( pid -> {
                                        RootPrinter p = currentPrinterMap.get(pid);
                                        if (p == null) {
                                            //System.out.println("Creating new printer for \"" + pid + "\"");
                                            p = new RootPrinter(this, pid);
                                            p.startUpdating(updateInterval);
                                            currentPrinterMap.put(pid, p);
                                        }
                                    });
                        List<Entry<String, RootPrinter>> lostPrinters = currentPrinterMap.entrySet()
                                .stream()
                                .filter(e -> !listPrintersResponse.getPrinterIDs().contains(e.getKey()))
                                .collect(Collectors.toList());
                        lostPrinters.forEach(e -> {
                            e.getValue().stopUpdating();
                            currentPrinterMap.remove(e.getKey());
                        });
                    }
                }
                catch (IOException ex) {
                    System.out.println("Error whilst decoding printer list from @" + hostAddress + ":" + hostPort + " - " + ex);
                }
                return currentPrinterMap;
            });
    }

    public Future<ServerStatusResponse> runRequestServerStatusTask() {
        return runRequestTask(WHO_ARE_YOU_COMMAND, true, null,
            (byte[] requestData, ObjectMapper jMapper) -> {
                ServerStatusResponse serverStatus = null;
                try {
                    if (requestData.length > 0) {
                        //System.out.println("Updating server status");
                        serverStatus = mapper.readValue(requestData, ServerStatusResponse.class);
                        currentStatusProperty.set(serverStatus);
                    }
                }
                catch (IOException ex) {
                    System.out.println("Error whilst decoding printer list from @" + hostAddress + ":" + hostPort + " - " + ex);
                }
                return serverStatus;
            });
    }
    
    public Future<Boolean> runSetAccessPINTask(String pin) {
        String data = String.format("\"%s\"", pin);
        return runRequestTask(ACCESS_PIN_COMMAND, false, data,
            (byte[] requestData, ObjectMapper jMapper) -> {
                return true;
            });
    }

    public Future<Boolean> runResetPINTask(String serial) {
        String data = String.format("\"%s\"", serial);
        return runRequestTask(RESET_PIN_COMMAND, false, data,
            (byte[] requestData, ObjectMapper jMapper) -> {
                return true;
            });
    }

    @Override
    protected void update() {
        runRequestServerStatusTask();
        runListAttachedPrintersTask();
    }
    
    public boolean checkPrinterExists(String printerId) {
        return currentPrinterMap.containsKey(printerId);
    }
}
