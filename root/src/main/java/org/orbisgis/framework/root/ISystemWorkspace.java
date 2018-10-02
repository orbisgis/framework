package org.orbisgis.framework.root;

/**
 * Workspace for the OrbisGIS application. It is different than project workspace. It is only used to store the file
 * and data related to the application itself (like for caching the oSGI bundles.
 *
 * @author Sylvain PALOMINOS (UBS 2018)
 * @author Erwan Bocher (CNRS)
 */
public interface ISystemWorkspace {

    /**
     * Load the workspace. It should be done once the workspace has been configured.
     *
     * @param clear Clear the workspace to start from a new clean folder.
     *
     * @return True if the workspace has been successfully loaded, false otherwise.
     */
    boolean loadWorkspace(boolean clear);

    /**
     * Return the path of the workspace folder.
     *
     * @return The path of the workspace folder.
     */
    String getWorkspaceFolderPath();

    /**
     * Return the path of the workspace temporary folder.
     *
     * @return The path of the workspace temporary folder.
     */
    String getTempFolderPath();

    /**
     * Return the path of the workspace application folder.
     *
     * @return The path of the workspace application folder.
     */
    String getApplicationFolderPath();

    /**
     * Return the path of the workspace bundle folder, which is used to store the bundle used by Felix
     *
     * @return The path of the workspace bundle folder.
     */
    String getBundleFolderPath();

    /**
     * Return the path of the workspace cache folder, which is used to cache the bundle used by Felix
     *
     * @return The path of the workspace temporary folder.
     */
    String getCacheFolderPath();

    /**
     * Return the path of the workspace log file.
     *
     * @return The path of the workspace log file.
     */
    String getLogFilePath();

    /**
     * Set the path of the config.properties file used by Felix.
     *
     * @param felixConfigPath The path of the config.properties file used by Felix.
     */
    void setFelixConfigPath(String felixConfigPath);

    /**
     * Get the path of the config.properties file used by Felix.
     * @return The path of the config.properties file used by Felix.
     */
    String getFelixConfigPath();
}
