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
     * Get the path of the configuration files used by Felix.
     * @return The path of the configuration files used by Felix.
     */
    String getConfFolderPath();
}
