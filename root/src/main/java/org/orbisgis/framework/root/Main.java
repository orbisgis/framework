/*
 * Framework is part of the OrbisGIS platform
 *
 * OrbisGIS is a java GIS application dedicated to research in GIScience.
 * OrbisGIS is developed by the GIS group of the DECIDE team of the
 * Lab-STICC CNRS laboratory, see <http://www.lab-sticc.fr/>.
 *
 * The GIS group of the DECIDE team is located at :
 *
 * Laboratoire Lab-STICC – CNRS UMR 6285
 * Equipe DECIDE
 * UNIVERSITÉ DE BRETAGNE-SUD
 * Institut Universitaire de Technologie de Vannes
 * 8, Rue Montaigne - BP 561 56017 Vannes Cedex
 *
 * Root is distributed under GPL 3 license.
 *
 * Copyright (C) 2018 CNRS (Lab-STICC UMR CNRS 6285)
 *
 *
 * Root is free software: you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * Root is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * Root. If not, see <http://www.gnu.org/licenses/>.
 *
 * For more information, please consult: <http://www.orbisgis.org/>
 * or contact directly:
 * info_at_ orbisgis.org
 */
package org.orbisgis.framework.root;

import org.apache.commons.cli.*;
import org.apache.commons.lang3.SystemUtils;
import org.apache.felix.framework.Logger;
import org.apache.felix.main.AutoProcessor;
import org.osgi.framework.Constants;
import org.osgi.framework.FrameworkEvent;
import org.osgi.framework.Version;
import org.osgi.framework.launch.Framework;
import org.osgi.framework.launch.FrameworkFactory;

import javax.swing.*;
import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * Entry point of the framework. It only launch the OSGI service.
 *
 * @author Sylvain PALOMINOS (UBS 2018)
 * @author Erwan Bocher (CNRS)
 */

public class Main {

    private static Framework m_fwk = null;
    private static SystemWorkspace systemWorkspace;

    private static boolean NO_FAIL_MODE = false;
    private static boolean NO_UI_MODE = true;
    // For test purpose only
    private static boolean TEST_MODE = false;

    private static final Logger LOGGER = new Logger();
    private static final String OBR_REPOSITORY_URL = "obr.repository.url";
    private static final String OBR_REPOSITORY_SNAPSHOT_URL = "obr.repository.snapshot.url";
    private static final String MIN_ARCHETYPE = "minArchetype.properties";
    private static final String UI_ARCHETYPE = "uiArchetype.properties";
    private static final Version VERSION = new Version(6, 0, 0, "SNAPSHOT");

    private static final String FX11_VERSION = "11.0.1";
    private static final String FX11_LINUX = "https://download2.gluonhq.com/openjfx/11.0.1/openjfx-" + FX11_VERSION + "_linux-x64_bin-sdk.zip";
    private static final String FX11_WINDOWS = "https://download2.gluonhq.com/openjfx/11.0.1/openjfx-" + FX11_VERSION + "_windows-x64_bin-sdk.zip";
    private static final String FX11_MAC = "https://download2.gluonhq.com/openjfx/11.0.1/openjfx-" + FX11_VERSION + "_osx-x64_bin-sdk.zip";
    private static final String FX11_SDK = "FX11_SDK";
    private static final String FX11_SDK_LIB = "lib";
    private static final String FX11_SDK_BIN = "bin";
    private static final String FX11_SDK_PREFIX = "javafx-sdk-";

    private static final int[] SUPPORTED_JAVA_VERSION = {11};

    /**
     * Entry method of OrbisGIS. The input argument are parsed and then different checks are done to ensure the good
     * running of OrbisGIS.
     *
     * @param args Arguments passed to OrbisGIS at launch.
     */
    public static void main(String[] args) {
        if(!parseArguments(args)){
            return;
        }
        if(!checkJavaVersion()){
            String message = "The java version "+System.getProperty("java.version")+" is not supported.\n" +
                    "The supported version are : "+ Arrays.toString(SUPPORTED_JAVA_VERSION)+".";
            showError(message, true);
        }
        startFelix();
    }

    /**
     * Start the felix framework.
     * Code coming from Apache Felix
     * @see <a href=" http://felix.apache.org/documentation/subprojects/apache-felix-framework/apache-felix-framework-launching-and-embedding.html">
     */
    private static void startFelix() {
        // Sets the system properties.
        if(systemWorkspace.getConfFolderPath() != null) {
            System.setProperty("felix.config.properties", "file:" + systemWorkspace.getConfFolderPath() + "config.properties");
        }
        // Load system properties.
        org.apache.felix.main.Main.loadSystemProperties();
        // Read configuration properties.
        Map<String, String> configProps = org.apache.felix.main.Main.loadConfigProperties();

        // If no configuration properties were found, then create an empty properties object.
        if (configProps == null) {
            LOGGER.log(Logger.LOG_WARNING, "No " + org.apache.felix.main.Main.CONFIG_PROPERTIES_FILE_VALUE + " found.");
            configProps = new HashMap<>();
        }

        //If OrbisGIS is in a SNAPSHOT version, uses the release and snapshot orb.
        //Else only uses the release one.
        if(configProps.containsKey(OBR_REPOSITORY_URL) && configProps.containsKey(OBR_REPOSITORY_SNAPSHOT_URL)) {
            if (VERSION.getQualifier().equals("SNAPSHOT")) {
                configProps.remove(OBR_REPOSITORY_URL);
                configProps.put(OBR_REPOSITORY_URL, configProps.get(OBR_REPOSITORY_SNAPSHOT_URL));
            }
            configProps.remove(OBR_REPOSITORY_SNAPSHOT_URL);
        }

        // Copy framework properties from the system properties.
        org.apache.felix.main.Main.copySystemProperties(configProps);

        // If there is a passed in bundle auto-deploy directory, then that overwrites anything in the config file.
        configProps.put(AutoProcessor.AUTO_DEPLOY_DIR_PROPERTY, systemWorkspace.getBundleFolderPath());

        // If there is a passed in bundle cache directory, then that overwrites anything in the config file.
        configProps.put(Constants.FRAMEWORK_STORAGE, systemWorkspace.getCacheFolderPath());

        // If enabled, register a shutdown hook to make sure the framework is cleanly shutdown when the VM exits.
        String enableHook = configProps.get(org.apache.felix.main.Main.SHUTDOWN_HOOK_PROP);
        if ((enableHook == null) || !enableHook.equalsIgnoreCase("false"))  {
            Runtime.getRuntime().addShutdownHook(new Thread("Felix Shutdown Hook") {
                public void run() {
                    try {
                        if (m_fwk != null) {
                            m_fwk.stop();
                            m_fwk.waitForStop(0);
                        }
                    }
                    catch (Exception ex) {
                        System.err.println("Error stopping framework: " + ex);
                    }
                }
            });
        }

        LOGGER.log(Logger.LOG_DEBUG, "Start Felix framework");
        try {
            // Create an instance of the framework.
            FrameworkFactory factory = getFrameworkFactory();
            m_fwk = factory.newFramework(configProps);
            m_fwk.init();
            // Use the system bundle context to process the auto-deploy
            // and auto-install/auto-start properties.
            AutoProcessor.process(configProps, m_fwk.getBundleContext());
            // Register the ISystemWorkspace
            m_fwk.getBundleContext().registerService(ISystemWorkspace.class, systemWorkspace, null);
            // Write the archetype in the workspace
            String archetype;
            if(NO_UI_MODE) {
                archetype = MIN_ARCHETYPE;
            }
            else {
                archetype = UI_ARCHETYPE;
            }
            File archFile = new File(systemWorkspace.getWorkspaceFolderPath(), archetype);
            InputStream inStream = Main.class.getResourceAsStream(archetype);
            java.nio.file.Files.copy(inStream, archFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
            inStream.close();
            ArchetypeLoader.loadArchetype(systemWorkspace, m_fwk.getBundleContext(), archFile.getAbsolutePath(), LOGGER);
            FrameworkEvent event;
            // If the framework was updated, then restart it.
            do {
                // Start the framework.
                m_fwk.start();
                LOGGER.log(Logger.LOG_DEBUG, "Felix framework successfully started");
                //If running in test mode, exit just after starting
                if(TEST_MODE){
                    break;
                }
                // Wait for framework to stop to exit the VM.
                event = m_fwk.waitForStop(0);
            } while (event.getType() == FrameworkEvent.STOPPED_UPDATE);
            LOGGER.log(Logger.LOG_DEBUG, "Stop Felix framework");
            // Otherwise, exit.
            LOGGER.log(Logger.LOG_DEBUG, "Exit");
        } catch (Exception ex) {
            showError("Could not create framework.\n"+ex.getLocalizedMessage(), true);
        }
    }

    /**
     * Get the Felix FrameworkFactory.
     *
     * @return The Felix FrameworkFactory.
     *
     * @throws Exception Exception thrown if the FrameworkFactory is not found.
     */
    private static FrameworkFactory getFrameworkFactory() throws Exception {
        URL url = org.apache.felix.main.Main.class.getClassLoader()
                .getResource("META-INF/services/org.osgi.framework.launch.FrameworkFactory");
        if (url != null) {
            try (BufferedReader br = new BufferedReader(new InputStreamReader(url.openStream()))) {
                for (String s = br.readLine(); s != null; s = br.readLine()) {
                    s = s.trim();
                    // Try to load first non-empty, non-commented line.
                    if ((s.length() > 0) && (s.charAt(0) != '#')) {
                        return (FrameworkFactory) Class.forName(s).getDeclaredConstructor().newInstance();
                    }
                }
            }
        }
        throw new Exception("Could not find framework factory.");
    }

    /**
     * Show the given error message to the user. It is displayed in the System out if
     * {@link org.orbisgis.framework.root.Main#NO_UI_MODE} is set to true, in a UI dialog otherwise.
     *
     * @param message Error message to display to the user.
     * @param exitOnClose Exit on close if true, go on otherwise.
     */
    private static void showError(String message, boolean exitOnClose) {
        LOGGER.log(Logger.LOG_ERROR, message);
        if (!NO_UI_MODE) {
            JOptionPane.showMessageDialog(null, message);
        }
        if(exitOnClose && !TEST_MODE) {
            System.exit(0);
        }
    }

    /**
     * Check if the java version is supported or not. The supported version are stored into {@link org.orbisgis.framework.root.Main#SUPPORTED_JAVA_VERSION}.
     *
     * @return True if the version is supported, false otherwise.
     */
    private static boolean checkJavaVersion() {
        for(int supportedVersion : SUPPORTED_JAVA_VERSION){
            switch (supportedVersion) {
                case 1 :
                    if(SystemUtils.IS_JAVA_1_1){
                        return true;
                    }
                    break;
                case 2 :
                    if(SystemUtils.IS_JAVA_1_2){
                        return true;
                    }
                    break;
                case 3 :
                    if(SystemUtils.IS_JAVA_1_3){
                        return true;
                    }
                    break;
                case 4 :
                    if(SystemUtils.IS_JAVA_1_4){
                        return true;
                    }
                    break;
                case 5 :
                    if(SystemUtils.IS_JAVA_1_5){
                        return true;
                    }
                    break;
                case 6 :
                    if(SystemUtils.IS_JAVA_1_6){
                        return true;
                    }
                    break;
                case 7 :
                    if(SystemUtils.IS_JAVA_1_7){
                        return true;
                    }
                    break;
                case 8 :
                    if(SystemUtils.IS_JAVA_1_8){
                        return true;
                    }
                    break;
                case 9 :
                    if(SystemUtils.IS_JAVA_9){
                        return true;
                    }
                    break;
                case 10 :
                    if(SystemUtils.IS_JAVA_10){
                        return true;
                    }
                    break;
                case 11 :
                    if(SystemUtils.IS_JAVA_11){
                        return true;
                    }
                    break;
            }
        }
        return false;
    }

    /**
     * Parse the incoming argument list and apply them.
     *
     * @param args String array of argument to parse.
     *
     * @return True if the argument parsing when well, false otherwise.
     */
    private static boolean parseArguments(String[] args) {
        LOGGER.log(Logger.LOG_DEBUG, "Parsing arguments");
        final Options firstOptions = configFirstParameter();
        final Options options = configParameter(firstOptions);
        final CommandLineParser parser = new DefaultParser();

        //First check the first options (like the 'help' one).
        CommandLine line;
        try {
            line = parser.parse(firstOptions, args, true);
        } catch (ParseException e) {
            showError("Error on parsing argument.\n"+e.getLocalizedMessage(), true);
            return false;
        }
        //Check the -h/--help argument
        if(line.hasOption("help")) {
            new HelpFormatter().printHelp( "OrbisGIS", options, true);
            return false;
        }

        try {
            line = parser.parse(options, args);
        } catch (ParseException e) {
            showError("Error on parsing argument.\n"+e.getLocalizedMessage()+"\n\n", true);
        }
        //Check the -d/--debug argument
        if(line.hasOption("debug")){
            LOGGER.setLogLevel(Logger.LOG_DEBUG);
            int time = 10;
            try{
                time = Integer.parseInt(line.getOptionValue("debug").trim());
            } catch(Exception ignored){}
            LOGGER.log(Logger.LOG_DEBUG, "Waiting "+time+" seconds for the debug client");
            try {
                Thread.sleep(time*1000);
            } catch (InterruptedException e) {
                String message = "Error while starting the debug mode : "+e.getLocalizedMessage();
                showError(message, true);
            }
        }
        //Check the --noFail argument
        if(line.hasOption("noFail")){
            LOGGER.log(Logger.LOG_INFO, "Clear the system workspace in 5 seconds.");
            NO_FAIL_MODE = true;
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                String message = "Error while cleaning the workspace : "+e.getLocalizedMessage();
                showError(message, true);
            }
        }
        //Check the --workspace argument
        if(line.hasOption("workspace")){
            systemWorkspace = new SystemWorkspace(line.getOptionValue("workspace").trim(), VERSION.getMajor(),
                    VERSION.getMinor(), LOGGER);
        }
        else{
            systemWorkspace = new SystemWorkspace(null, VERSION.getMajor(), VERSION.getMinor(), LOGGER);
        }
        if(!systemWorkspace.loadWorkspace(NO_FAIL_MODE)){
            showError("Error get while loading the workspace", true);
        }
        //Check the --noUI argument
        if(!line.hasOption("noUI")){
            NO_UI_MODE = false;
            if(!isJavaFXInstalled()){
                loadJavaFX();
            }
        }
        else{
            NO_UI_MODE = true;
        }
        //Check the --configProperties argument
        InputStream inStream = null;
        if(line.hasOption("configProperties")){
            File file = new File(line.getOptionValue("configProperties"));
            if(!file.exists()){
                LOGGER.log(Logger.LOG_DEBUG, "File '"+file+"' not found");
            }
            else {
                try {
                    inStream = new FileInputStream(new File(line.getOptionValue("configProperties")));
                } catch (FileNotFoundException e) {
                    LOGGER.log(Logger.LOG_DEBUG, "File '"+file+"' not readable");
                }
            }
        }
        if(inStream == null){
            LOGGER.log(Logger.LOG_DEBUG, "Use default Felix config file.");
            inStream = Main.class.getResourceAsStream("config.properties");
        }
        try{
            File confFile = new File(systemWorkspace.getConfFolderPath(), "config.properties");
            Files.copy(inStream, confFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
            inStream.close();
        } catch (IOException e) {
            String message = "Error while loading the default Felix config file : "+e.getLocalizedMessage();
            showError(message, true);
        }
        return true;
    }

    /**
     * Return true if FX is installed, false otherwise.
     *
     * @return True if FX is installed, false otherwise.
     */
    private static boolean isJavaFXInstalled(){
        return false;
    }

    /**
     * Install the JavaFX library and return its folder.
     *
     * @return The folder of the JavaFX installation.
     */
    private static File installJavaFX(){
        //Create the library URL
        URL sdkUrl;
        try {
            if (SystemUtils.IS_OS_LINUX) {
                sdkUrl = new URL(FX11_LINUX);
            } else if (SystemUtils.IS_OS_WINDOWS) {
                sdkUrl = new URL(FX11_WINDOWS);
            } else if (SystemUtils.IS_OS_MAC) {
                sdkUrl = new URL(FX11_MAC);
            } else {
                LOGGER.log(Logger.LOG_ERROR, "Unable to find FX11 SDK for you operating system.");
                return null;
            }
        } catch (MalformedURLException e) {
            LOGGER.log(Logger.LOG_ERROR, "Unable to create the URL of the FX11 SDK.\n" + e.getLocalizedMessage());
            return null;
        }
        //Download and unzip the library
        LOGGER.log(Logger.LOG_DEBUG, "Downloading JavaFX 11 SDK from '" + sdkUrl + "'");
        File sdkFolder = new File(systemWorkspace.getWorkspaceFolderPath(), FX11_SDK);
        if(!sdkFolder.mkdirs()){
            LOGGER.log(Logger.LOG_ERROR, "Unable to create the folder '" + sdkFolder.getAbsolutePath() + "'");
            return null;
        }
        File unzippedSdk = downloadAndUnzip(sdkUrl, sdkFolder);
        if(unzippedSdk == null || !unzippedSdk.exists() || !unzippedSdk.isDirectory()) {
            LOGGER.log(Logger.LOG_ERROR, "Unable to access to the unzipped FX11 SDK");
            return null;
        }
        return unzippedSdk;
    }

    /**
     * Load JavaFX. If it is not present in the workspace, install it before.
     */
    private static void loadJavaFX(){
        LOGGER.log(Logger.LOG_INFO, "Installation of JavaFX 11 SDK.");
        File lib;
        //Test if FX has been already downloaded
        Path path = Paths.get( systemWorkspace.getWorkspaceFolderPath() + File.separator + FX11_SDK + File.separator +
                        FX11_SDK_PREFIX + FX11_VERSION + File.separator + FX11_SDK_LIB);
        if(!Files.exists(path)) {
            //Install the FX libraries
            File unzippedSdk = installJavaFX();
            LOGGER.log(Logger.LOG_DEBUG, "Loading JavaFX 11 SDK.");
            if(SystemUtils.IS_OS_WINDOWS){
                lib = new File(unzippedSdk, FX11_SDK_BIN);
            }
            else{
                lib = new File(unzippedSdk, FX11_SDK_LIB);
            }
            if(!lib.exists() || !lib.isDirectory()) {
                LOGGER.log(Logger.LOG_ERROR, "Unable to access to the unzipped FX11 SDK lib folder");
                return;
            }
        }
        else{
            lib = new File(path.toUri());
        }
        System.getProperties().put("java.library.path", System.getProperties().get("java.library.path")+":"+lib.getAbsolutePath());
        LOGGER.log(Logger.LOG_DEBUG, "JavaFX 11 SDK successfully installed.");
    }

    /**
     * Download the given URL, unzip it and return the folder.
     *
     * @param dlUrl URL to download.
     * @param destinationFolder Folder where it should be unzipped.
     *
     * @return The unzipped folder.
     */
    private static File downloadAndUnzip(URL dlUrl, File destinationFolder){
        File zipFile = new File(destinationFolder, FX11_SDK+".zip");
        try {
            ReadableByteChannel rbc = Channels.newChannel(dlUrl.openStream());
            FileOutputStream fos = new FileOutputStream(zipFile);
            fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
        } catch (FileNotFoundException e) {
            LOGGER.log(Logger.LOG_ERROR, "Unable to open the destination file '" + zipFile + "'\n" +
                    e.getLocalizedMessage());
        } catch (IOException e) {
            LOGGER.log(Logger.LOG_ERROR, "Unable to download the url '" + dlUrl + "'\n" + e.getLocalizedMessage());
        }

        try {
            File rootDirectory = null;
            byte[] buffer = new byte[1024];
            ZipInputStream zis = new ZipInputStream(new FileInputStream(zipFile));
            ZipEntry zipEntry = zis.getNextEntry();
            while (zipEntry != null) {
                if(zipEntry.isDirectory()){
                    Path path = Paths.get(destinationFolder.getAbsolutePath()+File.separator+zipEntry.getName());
                    Files.createDirectories(path);
                    if(rootDirectory == null){
                        rootDirectory = new File(path.toUri());
                    }
                }
                else {
                    File newFile = new File(destinationFolder, zipEntry.getName());
                    FileOutputStream fos = new FileOutputStream(newFile);
                    int len;
                    while ((len = zis.read(buffer)) > 0) {
                        fos.write(buffer, 0, len);
                    }
                    fos.close();
                }
                zipEntry = zis.getNextEntry();
            }
            zis.closeEntry();
            zis.close();
            return rootDirectory;
        } catch (FileNotFoundException e) {
            LOGGER.log(Logger.LOG_ERROR, "File not found.\n" + e.getLocalizedMessage());
        } catch (IOException e) {
            LOGGER.log(Logger.LOG_ERROR, "Unable to read/write.\n" + e.getLocalizedMessage());
        }
        return null;
    }

    /**
     * Sets the Options object with the parameters to check just before launching OrbisGIS.
     *
     * @return The Options object used to parse the arguments of the {@link org.orbisgis.framework.root.Main#main main} methods
     */
    private static Options configParameter(Options previousParameters) {
        Options options = new Options();

        previousParameters.getOptions().forEach(options::addOption);

        Option archOption = Option.builder("a")
                .longOpt("archetype")
                .desc("Identifier of the OrbisGIS archetype to load")
                .numberOfArgs(1)
                .argName("archetypeId")
                .build();

        Option debugOption = Option.builder("d")
                .longOpt("debug")
                .desc("Launch OrbisGIS into the 'debug' mode. A wait time can be set as argument in order to give " +
                        "time to start debug client.")
                .hasArg(true)
                .numberOfArgs(1)
                .argName("waitTime")
                .optionalArg(true)
                .build();

        Option noFailOption = Option.builder()
                .longOpt("noFail")
                .desc("Launch OrbisGIS into the 'no fail' mode")
                .build();

        Option noUIOption = Option.builder()
                .longOpt("noUI")
                .desc("Launch OrbisGIS into the 'no UI' mode")
                .build();

        Option workspaceOption = Option.builder("w")
                .longOpt("workspace")
                .desc("Sets the path of the workspace to use")
                .numberOfArgs(1)
                .argName("workspacePath")
                .build();

        Option configPropertiesOption = Option.builder()
                .longOpt("configProperties")
                .desc("Sets the path of the config.properties file to use for the Apache Felix framework")
                .numberOfArgs(1)
                .argName("filePath")
                .build();

        options.addOption(archOption);
        options.addOption(debugOption);
        options.addOption(noFailOption);
        options.addOption(noUIOption);
        options.addOption(workspaceOption);
        options.addOption(configPropertiesOption);
        return options;
    }

    /**
     * Sets the Options object with the parameters to check first (like the 'help' one) before those from {@link org.orbisgis.framework.root.Main#configParameter}
     *
     * @return The Options object used to parse the arguments of the {@link org.orbisgis.framework.root.Main#main main} methods
     */
    private static Options configFirstParameter() {
        Options options = new Options();

        Option helpOption = Option.builder("h")
                .longOpt("help")
                .desc("Display this help message")
                .build();

        options.addOption(helpOption);
        return options;
    }
}
