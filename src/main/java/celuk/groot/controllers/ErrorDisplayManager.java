/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package celuk.groot.controllers;

import celuk.groot.remote.ErrorDetails;
import celuk.groot.remote.RootPrinter;
import celuk.groot.remote.RootServer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javafx.beans.value.ChangeListener;
import javafx.collections.MapChangeListener;

/**
 *
 * @author Tony
 */
public class ErrorDisplayManager {
    private final Map<String, ChangeListener<Map<Integer, ErrorDetails>>> listenerMap = new HashMap<>();
    private final RootServer server;
    
    MapChangeListener<String, RootPrinter> printerMapListener = (var c) -> {
        // Get a list of printers with listeners that not in the changed map.
        var printerMap = c.getMap();
        List<String> lostPrinters = listenerMap.keySet()
                                               .stream()
                                               .filter(pid -> !printerMap.containsKey(pid))
                                               .collect(Collectors.toList());
        
        lostPrinters.forEach(pid -> {
            var p = printerMap.get(pid);
            var l = listenerMap.get(pid);
            p.getActiveErrorMapProperty().removeListener(l);
            listenerMap.remove(pid);
        });
        
        // Get a list of printers that are in the printer map, but do not have error listeners.        
        List<String> foundPrinters = printerMap.keySet()
                                               .stream()
                                               .filter(pid -> !listenerMap.containsKey(pid))
                                               .collect(Collectors.toList());
        foundPrinters.forEach(pid -> {
            var p = printerMap.get(pid);
            ChangeListener<Map<Integer, ErrorDetails>> l = (pr, ov, nv) -> {
                displayError(pid, nv);
            };
            p.getActiveErrorMapProperty().removeListener(l);
            listenerMap.put(pid, l);
        });
    };
    
    public ErrorDisplayManager(RootServer server) {
        this.server = server;
        
    }
    
    public RootServer getServer() {
        return server;
    }
    
    private void displayError(String pid, Map<Integer, ErrorDetails> errorMap) {
        errorMap.forEach((errorCode, error) -> {
            System.err.println("Error code " 
                               + Integer.toString(errorCode)
                               + " - "
                               + error.getErrorTitle());
        });
    }    
}
