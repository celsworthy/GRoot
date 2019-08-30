package celuk.groot.controllers;

import celuk.groot.remote.HeadEEPROMData;
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
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;

public class HeadParametersController implements Initializable, Page {
        // This is similar to the SpinnerData in TweakController, but that one is an integer spinner. This one is a 
    // float spinner. They should be combined as a numeric spinner.
    
    static final UnaryOperator<TextFormatter.Change> NUMERIC_FILTER = (change) -> {
        String newText = change.getControlNewText();
        if (newText.matches("[-+]?[0-9]*\\.?[0-9]*")) { 
            return change;
        } 
        else if ("-".equals(change.getText()) ) {
            if (change.getControlText().startsWith("-")) {
                change.setText("");
                change.setRange(0, 1);
                change.setCaretPosition(change.getCaretPosition()-2);
                change.setAnchor(change.getAnchor()-2);
                return change ;
            } 
            else {
                change.setRange(0, 0);
                return change ;
            }
        }
        return null;
    };

    private class SpinnerData {
        public String name;
        public TextField valueField;
        public Button decButton;
        public Button incButton;
        public float value;
        public float minValue;
        public float maxValue;
        public float step;
        public String format;
        
        public SpinnerData(String name,
                           TextField valueField,
                           Button decButton,
                           Button incButton,
                           float step,
                           String format) {
            this.name = name;
            this.valueField = valueField;
            this.decButton= decButton;
            this.incButton = incButton;
            this.value = 0.0F;
            this.minValue = 0.0F;
            this.maxValue = 0.0F;
            this.step = step;
            this.format = format;
        }
        
        public void initialize() {
            // Explicit initialisation to avoid leaking "this"
            // in the constructor, which is apparently bad practise.
            this.valueField.setUserData(this);
            this.decButton.setUserData(this);
            this.incButton.setUserData(this);
            
            valueField.setTextFormatter(new TextFormatter<>(NUMERIC_FILTER));
            valueField.focusedProperty().addListener((o, ov, nv) -> {
                if (!nv) { // focus lost
                    //System.out.println("SpinnerData[" + name + "] focusListener");
                    processFieldChange();
                }
            });
        }

        public void decAction(ActionEvent event) {
            //System.out.println("SpinnerData[" + name + "] decAction");
            value -= this.step;
            if (value < minValue)
                value = minValue;
            String v = String.format(format, this.value);
            valueField.setText(v);            
        }
    
        public void fieldAction(ActionEvent event) {
            //System.out.println("SpinnerData[" + name + "] fieldAction");
            processFieldChange();
        }

        public void processFieldChange() {
            //System.out.println("SpinnerData[" + name + "] processFieldChange");
            try {
                float v = Float.parseFloat(valueField.getText());
                if (v < minValue)
                    v = minValue;
                if (v > maxValue)
                    v = maxValue;
                if (v != value) {
                    value = v;
                    modified = true;
                    String vs = String.format(format, this.value);
                    valueField.setText(vs);
                }
            }
            catch (NumberFormatException ex)
            {
            }
        }

        public void incAction(ActionEvent event) {
            //System.out.println("SpinnerData[" + name + "] incAction");
            this.value += this.step;
            if (value > maxValue)
                value = maxValue;
            
            setModified(true);
            String v = String.format(format, this.value);
            valueField.setText(v);
        }

        public void updateSpinnerData(float value, float minValue, float maxValue) {
            this.minValue = minValue;
            this.maxValue = maxValue;
            this.value = value;
            String v = String.format(format, this.value);
            valueField.setText(v);
        }
        
        public void setVisible(boolean visible) {
            valueField.setVisible(visible);
            decButton.setVisible(visible);
            incButton.setVisible(visible);
        }
    };

    @FXML
    protected Pane headIcon;
    @FXML
    protected Label headTitleBold;
    @FXML
    protected Label headTitleLight;
    @FXML
    protected Label headTitleEdition;
    @FXML
    protected Label headDescription;
    @FXML
    protected Label headNozzlesDescription;
    @FXML
    protected Label headFeedsDescription;
    @FXML
    protected Button headChangeButton;

    @FXML
    protected StackPane headParametersPane;
    @FXML
    protected Label headParametersTitle;
    @FXML
    protected Label serialNumberLabel;
    @FXML
    protected Label serialNumberField;
    @FXML
    protected Label headHoursField;
    @FXML
    protected Label maxTempField;
    
    @FXML
    protected Label printHoursLabel;
    @FXML
    protected Label maxTempLabel;
    @FXML
    protected Label leftNozzleSubtitle;
    @FXML
    protected Label leftNozzleTitle;
    @FXML
    protected Label rightNozzleSubtitle;
    @FXML
    protected Label rightNozzleTitle;

    @FXML
    protected Button leftXDec;
    @FXML
    protected TextField leftXField;
    @FXML
    protected Button leftXInc;
    @FXML
    protected Button leftYDec;
    @FXML
    protected TextField leftYField;
    @FXML
    protected Button leftYInc;
    @FXML
    protected Button leftZDec;
    @FXML
    protected TextField leftZField;
    @FXML
    protected Button leftZInc;
    @FXML
    protected Button leftBDec;
    @FXML
    protected TextField leftBField;
    @FXML
    protected Button leftBInc;

    @FXML
    protected Button rightXDec;
    @FXML
    protected TextField rightXField;
    @FXML
    protected Button rightXInc;
    @FXML
    protected Button rightYDec;
    @FXML
    protected TextField rightYField;
    @FXML
    protected Button rightYInc;
    @FXML
    protected Button rightZDec;
    @FXML
    protected TextField rightZField;
    @FXML
    protected Button rightZInc;
    @FXML
    protected Button rightBDec;
    @FXML
    protected TextField rightBField;
    @FXML
    protected Button rightBInc;
    @FXML
    protected Button rightButton;

    @FXML
    protected void decAction(ActionEvent event) {
        SpinnerData s = (SpinnerData)(((Node)event.getSource()).getUserData());
        s.decAction(event);
    }

    @FXML
    protected void headChangeAction(ActionEvent event) {
        printer.runRemoveHeadTask();
        rootController.showRemoveHeadPage(this, printer);
    }

    @FXML
    protected void incAction(ActionEvent event) {
        SpinnerData s = (SpinnerData)(((Node)event.getSource()).getUserData());
        s.incAction(event);
    }

    @FXML
    protected void leftButtonAction(ActionEvent event) {
        if (rootController != null && event.getSource() instanceof Button)
            rootController.showMainMenu(this, printer);
    }
    
    @FXML
    protected void middleButtonAction(ActionEvent event) {
        if (rootController != null && event.getSource() instanceof Button)
            rootController.showHomePage(this, printer);
    }

    @FXML
    protected void rightButtonAction(ActionEvent event) {
        if (rootController != null && event.getSource() instanceof Button) {
            saveHeadData();
        }
    }

    protected RootStackController rootController = null;
    protected RootPrinter printer = null;
    private Map<String, SpinnerData> spinnerMap = new HashMap<>();
    private int nozzleCount = 0;
    private boolean valveFitted = false;
    protected boolean modified = false;
    
    private final ChangeListener<HeadEEPROMData> headEEPROMDataListener = (ob, ov, nv) -> {
        //System.out.println("RemotePrinter \"" + printer.getPrinterId() + "\"headEEPROMDataListener");
        Platform.runLater(() -> {
            updateHeadEEPROMData(nv);
        });
    };
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
         translateLabels(headParametersTitle,
                         serialNumberLabel,
                         printHoursLabel,
                         maxTempLabel,
                         leftNozzleSubtitle,
                         leftNozzleTitle,
                         rightNozzleSubtitle,
                         rightNozzleTitle);
         
         spinnerMap.put("leftX",
            new SpinnerData("leftX", leftXField, leftXDec, leftXInc, 0.05F, "%.2f"));
         spinnerMap.get("leftX").initialize();
         spinnerMap.put("leftY",
            new SpinnerData("leftY", leftYField, leftYDec, leftYInc, 0.05F, "%.2f"));
         spinnerMap.get("leftY").initialize();
         spinnerMap.put("leftZ",
            new SpinnerData("leftZ", leftZField, leftZDec, leftZInc, 0.05F, "%.2f"));
         spinnerMap.get("leftZ").initialize();
         spinnerMap.put("leftB",
            new SpinnerData("leftB", leftBField, leftBDec, leftBInc, 0.05F, "%.2f"));
         spinnerMap.get("leftB").initialize();
         spinnerMap.put("rightX",
            new SpinnerData("rightX", rightXField, rightXDec, rightXInc, 0.05F, "%.2f"));
         spinnerMap.get("rightX").initialize();
         spinnerMap.put("rightY",
            new SpinnerData("rightY", rightYField, rightYDec, rightYInc, 0.05F, "%.2f"));
         spinnerMap.get("rightY").initialize();
         spinnerMap.put("rightZ",
            new SpinnerData("rightZ", rightZField, rightZDec, rightZInc, 0.05F, "%.2f"));
         spinnerMap.get("rightZ").initialize();
         spinnerMap.put("rightB",
            new SpinnerData("rightB", rightBField, rightBDec, rightBInc, 0.05F, "%.2f"));
         spinnerMap.get("rightB").initialize();
    }
    
    @Override
    public void setRootStackController(RootStackController rootController) {
        this.rootController = rootController;
    }
    

    @Override
    public void startUpdates() {
        Platform.runLater(() -> {
            preparePage();
        });
        printer.getCurrentHeadEEPROMDataProperty().addListener(headEEPROMDataListener);
        printer.runRequestHeadEEPROMDataTask();
    }
    
    @Override
    public void stopUpdates() {
        if (printer != null) {
            printer.getCurrentHeadEEPROMDataProperty().removeListener(headEEPROMDataListener);
            printer = null;
        }
    }

    @Override
    public void displayPage(RootPrinter printer) {
        this.printer = printer;
        startUpdates();
        headParametersPane.setVisible(true);
    }

    @Override
    public void hidePage() {
        stopUpdates();
        headParametersPane.setVisible(false);
    }

    public void preparePage()
    {
        headTitleBold.setText("");
        headTitleLight.setText("");
        headTitleEdition.setText("");
        headDescription.setText("");
        headNozzlesDescription.setText("");
        headFeedsDescription.setText("");
        String headIconText = "-fx-background-image: null";
        headIcon.setStyle(headIconText);
        serialNumberField.setText("");
        headHoursField.setText("");
        maxTempField.setText("");

        setSpinnerData("rightX", 0.0F, -2.0F, 5.0F);
        setSpinnerData("rightY", 0.0F, -2.0F, 5.0F);
        setSpinnerData("rightZ", 0.0F, -2.0F, 5.0F);
        setSpinnerData("rightB", 0.0F, -2.0F, 5.0F);
        setSpinnerData("leftX", 0.0F, -2.0F, 5.0F);
        setSpinnerData("leftY", 0.0F, -2.0F, 5.0F);
        setSpinnerData("leftZ", 0.0F, -2.0F, 5.0F);
        setSpinnerData("leftB", 0.0F, -2.0F, 5.0F);
    }

    public void updateHeadEEPROMData(HeadEEPROMData headData)
    {
        String typeCode = headData.getTypeCode();
        nozzleCount = headData.getNozzleCount();
        valveFitted = headData.getValveFitted();
        modified = false;
        rightButton.setDisable(true);

        String prefix;
        if (typeCode == null || typeCode.isEmpty()) {
            typeCode = "";
            prefix = "noHead";
        }
        else {
            prefix = typeCode.toLowerCase().replace("-", "");
        }
        headTitleBold.setText(I18n.t(prefix + ".titleBold"));
        headTitleLight.setText(I18n.t(prefix + ".titleLight"));
        headTitleEdition.setText(I18n.t(prefix + ".titleEdition"));
        headDescription.setText(I18n.t(prefix + ".description"));
        headNozzlesDescription.setText(I18n.t(prefix + ".nozzles"));
        headFeedsDescription.setText(I18n.t(prefix + ".feeds"));
        String headIconText = "-fx-background-image: url(\"/image/" + I18n.t(prefix + ".icon") + "\")";
        headIcon.setStyle(headIconText);
        
        if (!typeCode.isEmpty()) {
            headChangeButton.setDisable(false);
            String serialNumber = headData.getTypeCode()
                                   + "-"
                                   + headData.getWeek()
                                   + headData.getYear()
                                   + "-"
                                   + headData.getPONumber()
                                   + "-"
                                   + headData.getSerialNumber()
                                   + "-"
                                   + headData.getChecksum();
            serialNumberField.setText(serialNumber);
            headHoursField.setText(String.format("%d %s", (int)headData.getHourCount(), I18n.t("unit.hours")));
            maxTempField.setText(String.format("%d %s",(int)headData.getMaxTemp(), I18n.t("unit.temp")));

            setSpinnerData("rightX", headData.getRightNozzleXOffset(), -2.0F, 5.0F);
            setSpinnerData("rightY", headData.getRightNozzleYOffset(), -2.0F, 5.0F);
            setSpinnerData("rightZ", headData.getRightNozzleZOverrun(), -2.0F, 5.0F);
        }
        else
        {
            headChangeButton.setDisable(true);
            serialNumberField.setText("");
            headHoursField.setText("");
            maxTempField.setText("");

            spinnerMap.get("rightX").setVisible(false);
            spinnerMap.get("rightY").setVisible(false);
            spinnerMap.get("rightZ").setVisible(false);
        }
        
        if (valveFitted) {
            setSpinnerData("rightB", headData.getRightNozzleBOffset(), -2.0F, 5.0F);
        }
        else {
            spinnerMap.get("rightB").setVisible(false);
        }
        
        if (nozzleCount > 1) {
            setSpinnerData("leftX", headData.getLeftNozzleXOffset(), -2.0F, 5.0F);
            setSpinnerData("leftY", headData.getLeftNozzleYOffset(), -2.0F, 5.0F);
            setSpinnerData("leftZ", headData.getLeftNozzleZOverrun(), -2.0F, 5.0F);
            if (valveFitted) {
                setSpinnerData("leftB", headData.getLeftNozzleBOffset(), -2.0F, 5.0F);
            }
            else {
                spinnerMap.get("leftB").setVisible(false);
            }
        }
        else {
            spinnerMap.get("leftX").setVisible(false);
            spinnerMap.get("leftY").setVisible(false);
            spinnerMap.get("leftZ").setVisible(false);
            spinnerMap.get("leftB").setVisible(false);
        }
    }
    
    private void setModified(boolean modified) {
        this.modified = modified;
        rightButton.setDisable(!modified);
    }
    
    private void setSpinnerData(String fieldName, float value, float minValue, float maxValue) {
        SpinnerData spinner = spinnerMap.get(fieldName);
        if (spinner != null) {
            spinner.setVisible(true);
            spinner.updateSpinnerData(value, minValue, maxValue);
        }
    }

    private void saveHeadData() {
        HeadEEPROMData headData = printer.getCurrentHeadEEPROMDataProperty().get().duplicate();
        headData.setRightNozzleXOffset(spinnerMap.get("rightX").value);
        headData.setRightNozzleYOffset(spinnerMap.get("rightY").value);
        headData.setRightNozzleZOverrun(spinnerMap.get("rightZ").value);
        if (valveFitted)
            headData.setRightNozzleBOffset(spinnerMap.get("rightB").value);
        if (nozzleCount > 1)
        {
            headData.setLeftNozzleXOffset(spinnerMap.get("leftX").value);
            headData.setLeftNozzleYOffset(spinnerMap.get("leftY").value);
            headData.setLeftNozzleZOverrun(spinnerMap.get("leftZ").value);
            if (valveFitted)
                headData.setLeftNozzleBOffset(spinnerMap.get("leftB").value);
        }
        printer.runWriteHeadEEPROMDataTask(headData);
        rootController.showHomePage(this, printer);
    }
}
