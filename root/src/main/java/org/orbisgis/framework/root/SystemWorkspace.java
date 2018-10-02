package org.orbisgis.framework.root;

import org.apache.felix.framework.Logger;

import java.io.File;

/**
 * Implementation of the ISystemWorkspace interface.
 */
public class SystemWorkspace implements ISystemWorkspace {

    /** Path of the config.properties file to give to Felix.*/
    private String felixConfigPath;

    /** OrbisGIS major version.*/
    private final int versionMajor;
    /** OrbisGIS minor version.*/
    private final int versionMinor;
    /** Path of the workspace.*/
    private final String workspacePath;
    /** Logger.*/
    private final Logger logger;

    private static final String TEMP_FOLDER_NAME = "tmp";
    private static final String APPLICATION_FOLDER_NAME = "app";
    private static final String BUNDLE_FOLDER_NAME = "bundle";
    private static final String CACHE_FOLDER_NAME = "cache";
    private static final String LOG_FILE_NAME = "orbisgis.log";

    /**
     * Main constructor. It initialize all the mandatory information about the workspace. Once done, the method
     * {@link SystemWorkspace#getDefaultWorkspacePath()} can be called to start the workspace.
     *
     * @param workspacePath Path of the workspace to use. If the folder doesn't exist, a new one will be created.
     * @param versionMajor OrbisGIS major version.
     * @param versionMinor OrbisGIS minor version.
     * @param logger Logger.
     */
    public SystemWorkspace(String workspacePath, int versionMajor, int versionMinor, Logger logger) {
        this.workspacePath = workspacePath==null||workspacePath.isEmpty() ? getDefaultWorkspacePath() : workspacePath;
        this.versionMajor = versionMajor;
        this.versionMinor = versionMinor;
        this.logger = logger;
    }

    private String getDefaultWorkspacePath(){
        return new File(System.getProperty("user.home"))
                .getAbsolutePath() + File.separator + ".OrbisGIS" + File.separator
                + versionMajor + "." + versionMinor;
    }

    private static boolean deleteSubFile(File folder){
        boolean ret = true;
        File[] files = folder.listFiles();
        if(files != null){
            for(File file : files){
                if(file.isFile()){
                    ret &= file.delete();
                }
                if(file.isDirectory()){
                    ret &= deleteSubFile(file);
                    ret &= file.delete();
                }
            }
        }
        return ret;
    }

    @Override
    public boolean loadWorkspace(boolean clear) {
        logger.log(Logger.LOG_DEBUG, "Loading workspace '"+getWorkspaceFolderPath()+"'");
        File workspaceFile = new File(getWorkspaceFolderPath());
        if(clear && workspaceFile.exists()){
            if(!deleteSubFile(workspaceFile)) {
                logger.log(Logger.LOG_ERROR, "Unable to clear workspace '" + getWorkspaceFolderPath() + "'");
                return false;
            }
            logger.log(Logger.LOG_DEBUG, "Workspace '"+getWorkspaceFolderPath()+"' cleared");
        }
        if(!createFolder(workspaceFile, "workspace")) {
            logger.log(Logger.LOG_ERROR, "Unable to create workspace '"+getWorkspaceFolderPath()+"'");
            return false;
        }
        boolean success = true;
        success &= createFolder(new File(getTempFolderPath()), TEMP_FOLDER_NAME);
        success &= createFolder(new File(getApplicationFolderPath()), APPLICATION_FOLDER_NAME);
        success &= createFolder(new File(getBundleFolderPath()), BUNDLE_FOLDER_NAME);
        success &= createFolder(new File(getCacheFolderPath()), CACHE_FOLDER_NAME);
        if(success){
            logger.log(Logger.LOG_DEBUG, "Workspace '" + getWorkspaceFolderPath() + "' successfully loaded");
        }
        return success;
    }

    private boolean createFolder(File folder, String alias){
        if(!folder.exists()){
            if(!folder.mkdirs()){
                logger.log(Logger.LOG_ERROR, "Unable to create the '" + alias + "' folder");
                return false;
            }
        }
        return true;
    }

    @Override
    public String getWorkspaceFolderPath() {
        return workspacePath;
    }

    @Override
    public String getTempFolderPath() {
        return workspacePath + File.separator + TEMP_FOLDER_NAME;
    }

    @Override
    public String getApplicationFolderPath() {
        return workspacePath + File.separator + APPLICATION_FOLDER_NAME;
    }

    @Override
    public String getBundleFolderPath() {
        return workspacePath + File.separator + BUNDLE_FOLDER_NAME;
    }

    @Override
    public String getCacheFolderPath() {
        return workspacePath + File.separator + CACHE_FOLDER_NAME;
    }

    @Override
    public String getLogFilePath() {
        return workspacePath + File.separator + LOG_FILE_NAME;
    }

    @Override
    public void setFelixConfigPath(String felixConfigPath) {
        this.felixConfigPath = felixConfigPath;
    }

    @Override
    public String getFelixConfigPath() {
        if(felixConfigPath == null && workspacePath != null && new File(workspacePath, "config.properties").exists()){
            setFelixConfigPath(new File(workspacePath, "config.properties").getAbsolutePath());
        }
        return felixConfigPath;
    }
}
