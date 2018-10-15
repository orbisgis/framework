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
 * OrbisWPS is distributed under GPL 3 license.
 *
 * Copyright (C) 2018 CNRS (Lab-STICC UMR CNRS 6285)
 *
 *
 * OrbisWPS is free software: you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * OrbisWPS is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * OrbisWPS. If not, see <http://www.gnu.org/licenses/>.
 *
 * For more information, please consult: <http://www.orbisgis.org/>
 * or contact directly:
 * info_at_ orbisgis.org
 */
package org.orbisgis.framework.root;

import org.apache.felix.framework.Logger;

import java.io.File;

/**
 * Implementation of the ISystemWorkspace interface.
 *
 * @author Sylvain PALOMINOS (UBS 2018)
 * @author Erwan Bocher (CNRS)
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
    private static final String CONF_FOLDER_NAME = "conf";
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
        success &= createFolder(new File(getConfFolderPath()), CONF_FOLDER_NAME);
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
    public String getConfFolderPath() {
        return workspacePath + File.separator + CONF_FOLDER_NAME;
    }
}
