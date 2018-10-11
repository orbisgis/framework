/*
 * Bundle Manager API is part of the OrbisGIS platform
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
package org.orbisgis.bundlemanagerapi;

import java.util.List;
import java.util.Map;

/**
 * Wrapper for a bundle.
 *
 * It contains all the methods used to manage the life cycle of the represented bundle.
 *
 *                      install()                                start()
 * State:Uninstalled ──────────────▷ State:Installed/Stopped ──────────────▷ State:Started ───┬───┬───┐
 *       △                                    △                                △              │   │   │
 *       │                                    │                                │update()      │   │   │
 *       │uninstall()                         │stop()                          └──────────────┘   │   │
 *       │                                    └───────────────────────────────────────────────────┘   │
 *       └────────────────────────────────────────────────────────────────────────────────────────────┘
 *
 * It also contains method to access to the properties of the bundle like the SymbolicName, the Version ...
 *
 * @author Sylvain PALOMINOS (UBS 2018)
 * @author Erwan Bocher (CNRS)
 */
public interface IBundleItem {

    /**
     * Indicates if the bundle is ready for installation.
     *
     * @return True if the bundle is ready for installation, false otherwise.
     */
    boolean isInstallReady();

    /**
     * Install the bundle.
     */
    void install();

    /**
     * Indicates if the bundle has been successfully installed.
     *
     * @return True if the bundle has been successfully installed, false otherwise.
     */
    boolean isInstalled();


    /**
     * Indicates if the bundle is ready for starting.
     *
     * @return True if the bundle is ready for starting, false otherwise.
     */
    boolean isStartReady();

    /**
     * Start the bundle.
     */
    void start();

    /**
     * Indicates if the bundle is currently starting.
     *
     * @return True if the bundle is currently starting, false otherwise.
     */
    boolean isStarting();

    /**
     * Indicates if the bundle has been successfully started.
     *
     * @return True if the bundle has been successfully started, false otherwise.
     */
    boolean isStarted();


    boolean isUpdateReady();
    void update();


    /**
     * Indicates if the bundle is ready for stopping.
     *
     * @return True if the bundle is ready for stopping, false otherwise.
     */
    boolean isStopReady();

    /**
     * Stop the bundle.
     */
    void stop();

    /**
     * Indicates if the bundle is currently stopping.
     *
     * @return True if the bundle is currently stopping, false otherwise.
     */
    boolean isStopping();

    /**
     * Indicates if the bundle has been successfully stopped.
     *
     * @return True if the bundle has been successfully stopped, false otherwise.
     */
    boolean isStopped();


    /**
     * Indicates if the bundle is ready for uninstalling.
     *
     * @return True if the bundle is ready for uninstalling, false otherwise.
     */
    boolean isUninstallReady();

    /**
     * Uninstall the bundle
     */
    void uninstall();

    /**
     * Indicates if the bundle has been successfully uninstalled.
     *
     * @return True if the bundle has been successfully uninstalled, false otherwise.
     */
    boolean isUninstalled();


    /**
     * Return the bundle unique symbolic name with the pattern : groupId.artifactId.
     *
     * @return The bundle symbolic name.
     */
    String getSymbolicName();

    /**
     * Return the bundle version.
     *
     * @return The bundle version.
     */
    String getVersion();

    /**
     * Return the bundle human readable name.
     *
     * @return The bundle human readable name.
     */
    String getName();

    /**
     * Return the bundle description.
     * @return The bundle description.
     */
    String getDescription();

    /**
     * Return the list of categories of the bundle.
     * @return The list of categories of the bundle.
     */
    List<String> getBundleCategories();

    /**
     * Return the Map of the properties of the bundle with the property name as key and the property value as value.
     *
     * @return The Map of the properties of the bundle.
     */
    Map<String, String> getProperties();

    /**
     * Return the value of the given property.
     *
     * @param propertyName Name of the property.
     *
     * @return The value of the property of the given name.
     */
    String getProperty(String propertyName);

}
