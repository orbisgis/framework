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
 * Bundle Manager API is distributed under GPL 3 license.
 *
 * Copyright (C) 2018 CNRS (Lab-STICC UMR CNRS 6285)
 *
 *
 * Bundle Manager API is free software: you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * Bundle Manager API is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * Bundle Manager API. If not, see <http://www.gnu.org/licenses/>.
 *
 * For more information, please consult: <http://www.orbisgis.org/>
 * or contact directly:
 * info_at_ orbisgis.org
 */
package org.orbisgis.bundlemanagerapi;

/**
 * Utility class providing methods to install, start, stop and uninstall bundles.
 *
 * @author Sylvain PALOMINOS (UBS 2018)
 * @author Erwan Bocher (CNRS)
 */
public interface IBundleUtils {

    /**
     * Install the bundle with the given bundle id with the following pattern : groupId:artifactId
     *
     * @param bundleId Id of the bundle to install
     *
     * @return True if the bundle has been installed, false otherwise.
     */
    boolean install(String bundleId);

    /**
     * Install the bundle with the given group id and artifact id
     *
     * @param groupId Group id of the bundle
     * @param artifactId Artifact id of the bundle
     *
     * @return True if the bundle has been installed, false otherwise.
     */
    boolean install(String groupId, String artifactId);

    /**
     * Start the bundle with the given bundle id with the following pattern : groupId:artifactId
     *
     * @param bundleId Id of the bundle to start
     *
     * @return True if the bundle has been started, false otherwise.
     */
    boolean start(String bundleId);

    /**
     * Start the bundle with the given group id and artifact id
     *
     * @param groupId Group id of the bundle
     * @param artifactId Artifact id of the bundle
     *
     * @return True if the bundle has been started, false otherwise.
     */
    boolean start(String groupId, String artifactId);

    /**
     * Update the bundle with the given bundle id with the following pattern : groupId:artifactId
     *
     * @param bundleId Id of the bundle to update
     *
     * @return True if the bundle has been updated, false otherwise.
     */
    boolean update(String bundleId);

    /**
     * Update the bundle with the given group id and artifact id
     *
     * @param groupId Group id of the bundle
     * @param artifactId Artifact id of the bundle
     *
     * @return True if the bundle has been updated, false otherwise.
     */
    boolean update(String groupId, String artifactId);

    /**
     * Stop the bundle with the given bundle id with the following pattern : groupId:artifactId
     *
     * @param bundleId Id of the bundle to stop
     *
     * @return True if the bundle has been stopped, false otherwise.
     */
    boolean stop(String bundleId);

    /**
     * Stop the bundle with the given group id and artifact id
     *
     * @param groupId Group id of the bundle
     * @param artifactId Artifact id of the bundle
     *
     * @return True if the bundle has been stopped, false otherwise.
     */
    boolean stop(String groupId, String artifactId);

    /**
     * Uninstall the bundle with the given bundle id with the following pattern : groupId:artifactId
     *
     * @param bundleId Id of the bundle to uninstall
     *
     * @return True if the bundle has been uninstalled, false otherwise.
     */
    boolean uninstall(String bundleId);


    /**
     * Uninstall the bundle with the given group id and artifact id
     *
     * @param groupId Group id of the bundle
     * @param artifactId Artifact id of the bundle
     *
     * @return True if the bundle has been uninstalled, false otherwise.
     */
    boolean uninstall(String groupId, String artifactId);

    /**
     * Adds an OBR repository.
     *
     * @param name Name of the repository.
     * @param url Url of the repository.
     */
    void addObrRepository(String name, String url);
}
