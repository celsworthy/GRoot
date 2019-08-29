package celuk.groot.controllers;

import celuk.groot.remote.PrintAdjustData;
import celuk.groot.remote.PrinterStatusResponse;
import celuk.groot.remote.RootPrinter;
import celuk.language.I18n;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.function.UnaryOperator;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.control.TextFormatter.Change;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;

public class TweakController implements Initializable, Page {
    
    static final String NO_HEAD_KEY = "<none>";
    static final String DEFAULT_HEAD_KEY = "<default>";
    static final UnaryOperator<Change> NUMERIC_FILTER = (change) -> {
            String newText = change.getControlNewText();
            if (newText.matches("-?([1-9][0-9]*)?")) { 
                return change;
            } else if ("-".equals(change.getText()) ) {
                if (change.getControlText().startsWith("-")) {
                    change.setText("");
                    change.setRange(0, 1);
                    change.setCaretPosition(change.getCaretPosition()-2);
                    change.setAnchor(change.getAnchor()-2);
                    return change ;
                } else {
                    change.setRange(0, 0);
                    return change ;
                }
            }
            return null;
        };
    
    private class SpinnerData {

        public String name;
        public String tag;
        public String fieldName;
        public TextField valueField;
        public Button decButton;
        public Button incButton;
        public int value;
        public int minValue;
        public int maxValue;
        public int step;
        
        public SpinnerData(String name,
                           String tag,
                           String fieldName,
                           TextField valueField,
                           Button decButton,
                           Button incButton,
                           int step) {
            this.name = name;
            this.tag = tag;
            this.fieldName = fieldName;
            this.valueField = valueField;
            this.decButton= decButton;
            this.incButton = incButton;
            this.value = 0;
            this.minValue = 0;
            this.maxValue = 0;
            this.step = step;
            
            valueField.setTextFormatter(new TextFormatter<>(NUMERIC_FILTER));
            valueField.focusedProperty().addListener((o, ov, nv) -> {
                if (!nv) { // focus lost
                    System.out.println("SpinnerData[" + name + "] focusListener");
                    processFieldChange();
                }
            });
        }
        
        public void decAction(ActionEvent event) {
            System.out.println("SpinnerData[" + name + "] decAction");
            value -= this.step;
            if (value < minValue)
                value = minValue;
            String v = Integer.toString(value);
            //valueField.setText(Integer.toString(this.value));            
            setPrintAdjustData(v);
        }
    
        public void fieldAction(ActionEvent event) {
            System.out.println("SpinnerData[" + name + "] fieldAction");
            processFieldChange();
        }

        public void processFieldChange() {
            System.out.println("SpinnerData[" + name + "] processFieldChange");
            try {
                int v = Integer.parseInt( valueField.getText());
                if (v < minValue)
                    v = minValue;
                if (v > maxValue)
                    v = maxValue;
                value = v;
                //valueField.setText(Integer.toString(this.value));
                setPrintAdjustData(Integer.toString(v));
            }
            catch (NumberFormatException ex)
            {
            }
        }

        public void incAction(ActionEvent event) {
            System.out.println("SpinnerData[" + name + "] incAction");
            this.value += this.step;
            if (value > maxValue)
                value = maxValue;
            
            String v = Integer.toString(this.value);
            //valueField.setText(Integer.toString(this.value));
            setPrintAdjustData(v);
        }

        public void updateSpinnerData(int value, int delta) {
            minValue = value - delta;
            maxValue = value + delta;
            this.value = value;
            if (!valueField.isFocused())
                valueField.setText(Integer.toString(this.value));
        }
        
        private void setPrintAdjustData(String value) {
            String data = String.format("{\"name\":\"%s\",\"tag\":\"%s\",\"value\":%s}", fieldName, tag, value);
//            printer.runSetPrintAdjustDataTask(data);
        }
    };

    @FXML
    private StackPane tweakPane;

    @FXML
    private GridPane m1Pane;
    @FXML
    private Label m1Title;
    @FXML
    private Label m1Description;
    @FXML
    private Label m1Material;
    @FXML
    private Label m1PrintSpeedLabel;
    @FXML
    private Button m1PrintSpeedInc;
    @FXML
    private TextField m1PrintSpeedValue;
    @FXML
    private Button m1PrintSpeedDec;
    @FXML
    private Label m1PrintSpeedSuffix;
    @FXML
    private Label m1FlowRateLabel;
    @FXML
    private Button m1FlowRateInc;
    @FXML
    private TextField m1FlowRateValue;
    @FXML
    private Button m1FlowRateDec;
    @FXML
    private Label m1FlowRateSuffix;
    @FXML
    private Label m1TempLabel;
    @FXML
    private Button m1TempInc;
    @FXML
    private TextField m1TempValue;
    @FXML
    private Button m1TempDec;
    @FXML
    private Label m1TempSuffix;

    @FXML
    private GridPane m2Pane;
    @FXML
    private Label m2Title;
    @FXML
    private Label m2Description;
    @FXML
    private Label m2Material;
    @FXML
    private Label m2PrintSpeedLabel;
    @FXML
    private Button m2PrintSpeedInc;
    @FXML
    private TextField m2PrintSpeedValue;
    @FXML
    private Button m2PrintSpeedDec;
    @FXML
    private Label m2PrintSpeedSuffix;
    @FXML
    private Label m2FlowRateLabel;
    @FXML
    private Button m2FlowRateInc;
    @FXML
    private TextField m2FlowRateValue;
    @FXML
    private Button m2FlowRateDec;
    @FXML
    private Label m2FlowRateSuffix;
    @FXML
    private Label m2TempLabel;
    @FXML
    private Button m2TempInc;
    @FXML
    private TextField m2TempValue;
    @FXML
    private Button m2TempDec;
    @FXML
    private Label m2TempSuffix;

    @FXML
    private GridPane bedPane;
    @FXML
    private Label bedTitle;
    @FXML
    private Label bedTempLabel;
    @FXML
    private Button bedTempInc;
    @FXML
    private TextField bedTempValue;
    @FXML
    private Button bedTempDec;
    @FXML
    private Label bedTempSuffix;
    
    @FXML
    private Button leftButton;

    @FXML
    private Button middleButton;

    @FXML
    private Button rightButton;

    @FXML
    void fieldAction(ActionEvent event) {
        if (rootController != null && event.getSource() instanceof TextField) {
            TextField t = (TextField)event.getSource();
            SpinnerData spinner = spinnerMap.get(t.getId());
            if (spinner != null)
                spinner.fieldAction(event);
        }
    }
    
    @FXML
    void incButtonAction(ActionEvent event) {
        if (rootController != null && event.getSource() instanceof Button) {
            Button b = (Button)event.getSource();
            SpinnerData spinner = spinnerMap.get(b.getId());
            if (spinner != null)
                spinner.incAction(event);
        }
    }
    
    @FXML
    void decButtonAction(ActionEvent event) {
        if (rootController != null && event.getSource() instanceof Button) {
            Button b = (Button)event.getSource();
            SpinnerData spinner = spinnerMap.get(b.getId());
            if (spinner != null)
                spinner.decAction(event);
        }
    }

    @FXML
    void leftButtonAction(ActionEvent event) {
        if (rootController != null && event.getSource() instanceof Button)
            rootController.showHomePage(this, printer);
    }
    
    @FXML
    void middleButtonAction(ActionEvent event) {
        if (rootController != null && event.getSource() instanceof Button)
            rootController.showMainMenu(this, printer);
    }

    @FXML
    void rightButtonAction(ActionEvent event) {
    }

    private RootStackController rootController = null;
    private RootPrinter printer = null;
    private Map<String, SpinnerData> spinnerMap = new HashMap<>();
    
    private final ChangeListener<PrinterStatusResponse> printerStatusListener = (ob, ov, nv) -> {
        //System.out.println("RemotePrinter \"" + printer.getPrinterId() + "\"printerStatusListener");
        updatePrinterStatus(nv);
    };
    
    private final ChangeListener<PrintAdjustData> printAdjustDataListener = (ob, ov, nv) -> {
        //System.out.println("RemotePrinter \"" + printer.getPrinterId() + "\"printAdjustDataListener");
        updatePrintAdjustData(nv);
    };

    @Override
    public void setRootStackController(RootStackController rootController) {
        this.rootController = rootController;
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        translateLabels(m1Title,
                        m1Description,
                        m1PrintSpeedLabel,
                        m1PrintSpeedSuffix,
                        m1FlowRateLabel,
                        m1FlowRateSuffix,
                        m1TempLabel,
                        m1TempSuffix,
                        m2Title,
                        m2Description,
                        m2PrintSpeedLabel,
                        m2PrintSpeedSuffix,
                        m2FlowRateLabel,
                        m2FlowRateSuffix,
                        m2TempLabel,
                        m2TempSuffix,
                        bedTitle,
                        bedTempLabel,
                        bedTempSuffix);

        spinnerMap.put("m1PrintSpeed",
            new SpinnerData("m1PrintSpeed", "r", "feedRate", m1PrintSpeedValue,
                            m1PrintSpeedDec, m1PrintSpeedInc, 10));
        spinnerMap.put("m1FlowRate",
            new SpinnerData("m1FlowRate", "r", "extrusionRate", m1FlowRateValue,
                            m1FlowRateDec, m1FlowRateInc, 2));
        spinnerMap.put("m1Temp",
            new SpinnerData("m1Temp", "r", "temp", m1TempValue,
                            m1TempDec, m1TempInc, 2));
        spinnerMap.put("m2PrintSpeed",
            new SpinnerData("m2PrintSpeed", "l", "feedRate", m2PrintSpeedValue,
                            m2PrintSpeedDec, m2PrintSpeedInc, 10));
        spinnerMap.put("m2FlowRate",
            new SpinnerData("m2FlowRate", "l", "extrusionRate", m2FlowRateValue,
                            m2FlowRateDec, m2FlowRateInc, 2));
        spinnerMap.put("m2Temp",
            new SpinnerData("m2Temp", "l", "temp", m2TempValue,
                            m2TempDec, m2TempInc, 2));
        spinnerMap.put("bedTemp",
            new SpinnerData("bedTemp", "bed", "temp", bedTempValue,
                            bedTempDec, bedTempInc, 5));

        m1Pane.setVisible(false);
        m1Pane.setManaged(false);
        m2Pane.setVisible(false);
        m2Pane.setManaged(false);

        rightButton.setVisible(false);
    }
    
    @Override
    public void startUpdates() {
        //printer.getCurrentStatusProperty().addListener(printerStatusListener);
        updatePrinterStatus(printer.getCurrentStatusProperty().get());
        printer.getCurrentPrintAdjustDataProperty().addListener(printAdjustDataListener);
        updatePrintAdjustData(printer.getCurrentPrintAdjustDataProperty().get());
    }
    
    @Override
    public void stopUpdates() {
        // Printer can be null if
        // the home page has never been shown.
        if (printer != null) {
            //printer.getCurrentStatusProperty().removeListener(printerStatusListener);
            printer.getCurrentPrintAdjustDataProperty().removeListener(printAdjustDataListener);
            printer = null;
        }
    }
    
    @Override
    public void displayPage(RootPrinter printer) {
        if (printer == null) {
            System.out.println("Null printer!");
        }
        this.printer = printer;
        startUpdates();
        tweakPane.setVisible(true);
    }
    
    @Override
    public void hidePage() {
        stopUpdates();
        tweakPane.setVisible(false);
    }

    private void updatePrinterStatus(PrinterStatusResponse printerStatus) {
        Platform.runLater(() -> {
        });
    }
    
    private void setSpinnerData(String fieldName, double value, double delta) {
        SpinnerData spinner = spinnerMap.get(fieldName);
        if (spinner != null)
            spinner.updateSpinnerData((int)Math.round(value), (int)Math.round(delta));
    }

    private void updatePrintAdjustData(PrintAdjustData adjustData) {
        Platform.runLater(() -> {
            if (adjustData == null) {
                // It is possible for this thread to complete after the 
                // thread has been stopped, in which case the printer will be null.
                // A local variable is used to make sure the printer does not
                // disapper between testing it and using it.
                RootPrinter p = printer;
                if (p != null)
                    rootController.showHomePage(this, p);
            }
            else {
                setSpinnerData("bedTemp", adjustData.getBedTargetTemp(), 15.0);
                
                if (adjustData.getUsingMaterial1()) {
                    m1Pane.setVisible(true);
                    m1Pane.setManaged(true);

                    m1Material.setText(adjustData.getMaterial1Name());
                    setSpinnerData("m1PrintSpeed", adjustData.getRightFeedRateMultiplier(), 100.0);
                    setSpinnerData("m1FlowRate", adjustData.getRightExtrusionRateMultiplier(), 100.0);
                    setSpinnerData("m1Temp", adjustData.getRightNozzleTargetTemp(), 15.0);
                }
                else {
                    m1Pane.setVisible(false);
                    m1Pane.setManaged(false);
                }

                if (adjustData.getUsingMaterial2()) {
                    m2Pane.setVisible(true);
                    m2Pane.setManaged(true);

                    m2Material.setText(adjustData.getMaterial2Name());
                    setSpinnerData("m2PrintSpeed", adjustData.getLeftFeedRateMultiplier(), 100.0);
                    setSpinnerData("m2FlowRate", adjustData.getLeftExtrusionRateMultiplier(), 100.0);
                    setSpinnerData("m2Temp", adjustData.getLeftNozzleTargetTemp(), 15.0);
                }
                else {
                    m2Pane.setVisible(false);
                    m2Pane.setManaged(false);
                }
            }
        });
    }
}
