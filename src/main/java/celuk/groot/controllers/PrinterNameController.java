package celuk.groot.controllers;

import celuk.groot.remote.RootPrinter;
import celuk.language.I18n;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.StackPane;
import javafx.scene.robot.Robot;

public class PrinterNameController implements Initializable, Page {
    
    @FXML
    private StackPane printerNamePane;
    @FXML
    private Label printerNameTitle;

    @FXML
    private Label nameLabel;    
    @FXML
    private TextField nameField;
    @FXML
    private Button nameClear;
    @FXML
    private Label savePrompt;    
    
    @FXML
    private Button zeroKey;
    @FXML
    private Button oneKey;
    @FXML
    private Button twoKey;
    @FXML
    private Button threeKey;
    @FXML
    private Button fourKey;
    @FXML
    private Button fiveKey;
    @FXML
    private Button sixKey;
    @FXML
    private Button sevenKey;
    @FXML
    private Button eightKey;
    @FXML
    private Button nineKey;

    @FXML
    private Button aKey;
    @FXML
    private Button bKey;
    @FXML
    private Button cKey;
    @FXML
    private Button dKey;
    @FXML
    private Button eKey;
    @FXML
    private Button fKey;
    @FXML
    private Button gKey;
    @FXML
    private Button hKey;
    @FXML
    private Button iKey;
    @FXML
    private Button jKey;
    @FXML
    private Button kKey;
    @FXML
    private Button lKey;
    @FXML
    private Button mKey;
    @FXML
    private Button nKey;
    @FXML
    private Button oKey;
    @FXML
    private Button pKey;
    @FXML
    private Button qKey;
    @FXML
    private Button rKey;
    @FXML
    private Button sKey;
    @FXML
    private Button tKey;
    @FXML
    private Button uKey;
    @FXML
    private Button vKey;
    @FXML
    private Button wKey;
    @FXML
    private Button xKey;
    @FXML
    private Button yKey;
    @FXML
    private Button zKey;
    @FXML
    private Button pointKey;
    @FXML
    private Button spaceKey;
    @FXML
    private Button dashKey;
    @FXML
    private Button shiftKey;
    @FXML
    private Button deleteKey;
    @FXML
    private Button returnKey;

    @FXML
    private Button leftButton;
    @FXML
    private Button middleButton;
    @FXML
    private Button rightButton;

    @FXML
    void nameClearAction(ActionEvent event) {
        if (rootController != null && event.getSource() instanceof Button) {
            nameField.clear();
        }
    }

    @FXML
    void keypadAction(ActionEvent event) {
        if (rootController != null && event.getSource() instanceof Button) {
            Button b = (Button)event.getSource();
            if (!nameField.isFocused()) {
                nameField.requestFocus();
                // Setting the caret position clears the selection, so the key
                // does not replace the selected text.
                nameField.positionCaret(nameField.getText().length());
            }
            KeyCode k = (KeyCode)(b.getUserData());
            // Simulate holding down shift key for letter keys,
            // to get capital letters.
            if (upperCaseLetters && k.isLetterKey())
                keyRobot.keyPress(KeyCode.SHIFT);
            keyRobot.keyType(k);
            if (upperCaseLetters && k.isLetterKey())
                keyRobot.keyRelease(KeyCode.SHIFT);
        }
    }
    
    @FXML
    void shiftAction(ActionEvent event) {
        if (rootController != null && event.getSource() instanceof Button) {
            upperCaseLetters = !upperCaseLetters;
            if (upperCaseLetters) {
                shiftKey.setText(keypadLowercaseText);
                for (Button key : alphaKeys)
                    key.setText(key.getText().toUpperCase());
            }
            else {
                shiftKey.setText(keypadUppercaseText);
                for (Button key : alphaKeys)
                    key.setText(key.getText().toLowerCase());
            }

        }
    }

    @FXML
    void returnAction(ActionEvent event) {
        if (rootController != null && event.getSource() instanceof Button) {
        }
    }

    @FXML
    void leftButtonAction(ActionEvent event) {
        if (rootController != null && event.getSource() instanceof Button) {
            rootController.showMainMenu(this, printer);
        }
    }
    
    @FXML
    void middleButtonAction(ActionEvent event) {
    }

    @FXML
    void rightButtonAction(ActionEvent event) {
        if (rootController != null && event.getSource() instanceof Button) {
            // Save printer name
            String printerName = nameField.getText().trim();
            if (!printerName.isEmpty() && !printerName.equals(currentName)) {
                printer.runRenamePrinterTask(printerName);
                rootController.showHomePage(this, printer);
            }
        }
    }

    private RootStackController rootController = null;
    private RootPrinter printer = null;
    private final Robot keyRobot = new Robot();
    private boolean upperCaseLetters = false;
    private String keypadUppercaseText = "keypad.uppercase";
    private String keypadLowercaseText = "keypad.lowercase";
    private Button alphaKeys[];
    private String currentName = null;
    
    @Override
    public void setRootStackController(RootStackController rootController) {
        this.rootController = rootController;
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        translateLabels(deleteKey, nameLabel, printerNameTitle, returnKey, savePrompt, shiftKey, spaceKey);
        keypadLowercaseText = I18n.t(keypadLowercaseText);
        keypadUppercaseText = I18n.t(keypadUppercaseText);
        nameField.setPromptText(I18n.t("printerName.enterName"));
        zeroKey.setUserData(KeyCode.DIGIT0);
        oneKey.setUserData(KeyCode.DIGIT1);
        twoKey.setUserData(KeyCode.DIGIT2);
        threeKey.setUserData(KeyCode.DIGIT3);
        fourKey.setUserData(KeyCode.DIGIT4);
        fiveKey.setUserData(KeyCode.DIGIT5);
        sixKey.setUserData(KeyCode.DIGIT6);
        sevenKey.setUserData(KeyCode.DIGIT7);
        eightKey.setUserData(KeyCode.DIGIT8);
        nineKey.setUserData(KeyCode.DIGIT9);

        aKey.setUserData(KeyCode.A);
        bKey.setUserData(KeyCode.B);
        cKey.setUserData(KeyCode.C);
        dKey.setUserData(KeyCode.D);
        eKey.setUserData(KeyCode.E);
        fKey.setUserData(KeyCode.F);
        gKey.setUserData(KeyCode.G);
        hKey.setUserData(KeyCode.H);
        iKey.setUserData(KeyCode.I);
        jKey.setUserData(KeyCode.J);
        kKey.setUserData(KeyCode.K);
        lKey.setUserData(KeyCode.L);
        mKey.setUserData(KeyCode.M);
        nKey.setUserData(KeyCode.N);
        oKey.setUserData(KeyCode.O);
        pKey.setUserData(KeyCode.P);
        qKey.setUserData(KeyCode.Q);
        rKey.setUserData(KeyCode.R);
        sKey.setUserData(KeyCode.S);
        tKey.setUserData(KeyCode.T);
        uKey.setUserData(KeyCode.U);
        vKey.setUserData(KeyCode.V);
        wKey.setUserData(KeyCode.W);
        xKey.setUserData(KeyCode.X);
        yKey.setUserData(KeyCode.Y);
        zKey.setUserData(KeyCode.Z);

        alphaKeys = new Button[] {
            aKey,
            bKey,
            cKey,
            dKey,
            eKey,
            fKey,
            gKey,
            hKey,
            iKey,
            jKey,
            kKey,
            lKey,
            mKey,
            nKey,
            oKey,
            pKey,
            qKey,
            rKey,
            sKey,
            tKey,
            uKey,
            vKey,
            wKey,
            xKey,
            yKey,
            zKey
        };

        deleteKey.setUserData(KeyCode.DELETE);
        shiftKey.setUserData(KeyCode.SHIFT);
        dashKey.setUserData(KeyCode.MINUS);
        pointKey.setUserData(KeyCode.PERIOD);
        returnKey.setUserData(KeyCode.ENTER);
        spaceKey.setUserData(KeyCode.SPACE);
        
        middleButton.setVisible(false);
        
        nameField.textProperty().addListener((observable, oldValue, newValue) -> {
            String v = newValue.trim();
            rightButton.setDisable(v.isBlank() || v.equals(currentName));
        });
    }
    
    @Override
    public void startUpdates() {
        Platform.runLater( () -> {
            currentName = printer.getCurrentStatusProperty().get().getPrinterName();
            nameField.setText(currentName);
            shiftKey.setText(keypadLowercaseText);
            upperCaseLetters = true;
            for (Button key : alphaKeys)
                key.setText(key.getText().toUpperCase());
        });
    }
    
    @Override
    public void stopUpdates() {
        printer = null;
    }

    @Override
    public void displayPage(RootPrinter printer) {
        this.printer = printer;
        startUpdates();
        printerNamePane.setVisible(true);
    }

    @Override
    public void hidePage() {
        stopUpdates();
        printerNamePane.setVisible(false);
    }
}
