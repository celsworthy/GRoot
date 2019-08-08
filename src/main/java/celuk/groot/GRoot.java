package celuk.groot;

import celuk.groot.controllers.RootStackController;
import celuk.groot.controllers.PrinterSelectController;
import celuk.groot.remote.RootServer;
import celuk.language.I18n;
import com.beust.jcommander.JCommander;
import javafx.application.Application;
import static javafx.application.Application.launch;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.scenicview.ScenicView;

public class GRoot extends Application {

    private static GRootCommandLineArgs commandLineArgs = new GRootCommandLineArgs();
    private Parent root = null;
    RootStackController rootController = null;
    RootServer server = null;
    
    @Override
    public void start(Stage stage) throws Exception {
        server = new RootServer(commandLineArgs.hostName, commandLineArgs.portNumber);
        server.startUpdating(commandLineArgs.updateInterval);
        FXMLLoader rootLoader =  new FXMLLoader(getClass().getResource("/fxml/RootStack.fxml"), null);
        rootController = new RootStackController();
        rootController.setRootServer(server);
        rootLoader.setController(rootController);
        Parent root = rootLoader.load();

        Scene scene = new Scene(root);
        scene.getStylesheets().add("/styles/Styles.css");
        
        stage.setTitle("Groot");
        stage.setScene(scene);
        stage.show();
        
        //ScenicView.show(scene);
    }

    @Override
    public void stop() {
        System.out.println("stop");
        rootController.stop();
    }

    /**
     * The main() method is ignored in correctly deployed JavaFX application.
     * main() serves only as fallback in case the application can not be
     * launched through deployment artifacts, e.g., in IDEs with limited FX
     * support. NetBeans ignores main().
     *
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        
        new JCommander(commandLineArgs).parse(args);
        I18n.loadMessages(commandLineArgs.installDirectory,
                          I18n.getDefaultApplicationLocale(commandLineArgs.languageTag));
        launch(args);
    }
}
