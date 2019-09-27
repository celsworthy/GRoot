package celuk.groot;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.converters.FileConverter;
import com.beust.jcommander.converters.PathConverter;
import java.io.File;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Command line arguments for Groot.
 * 
 * @author Tony Aldhous
 */
public class GRootCommandLineArgs {
    @Parameter(names={"--language-tag", "-l"}, description = "tag to indicate the interface language")
    public String languageTag = "en";

    @Parameter(names={"--install-directory", "-i"}, description = "Directory in which application is installed")
    public String installDirectory = "./";

    @Parameter(names={"--host", "-h"}, description = "Host name of Root server")
    public String hostName = "localhost";

    @Parameter(names={"--port", "-p"}, description = "Port number of Root server")
    public String portNumber = "8080";

    @Parameter(names={"--show-splash-screen", "-s"}, description = "Show splash screen during startup")
    boolean showSplashScreen = false;

    @Parameter(names={"--update-interval", "-u"}, description = "Update interval in microseconds")
    public int updateInterval = 2000;

    public GRootCommandLineArgs() {
        // Default to the system locale.
        Locale appLocale = Locale.getDefault();
        if (appLocale != null)
            languageTag = appLocale.getLanguage();
    }
}
