/*
 * Syntax Manager API is part of the OrbisGIS platform
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
 * Syntax Manager API is distributed under GPL 3 license.
 *
 * Copyright (C) 2018 CNRS (Lab-STICC UMR CNRS 6285)
 *
 *
 * Syntax Manager API is free software: you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * Syntax Manager API is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * Syntax Manager API. If not, see <http://www.gnu.org/licenses/>.
 *
 * For more information, please consult: <http://www.orbisgis.org/>
 * or contact directly:
 * info_at_ orbisgis.org
 */
package org.orbisgis.syntaxmanagerapi;

import java.util.Collection;

/**
 * Container grouping a collection of ISyntaxObject which can be register into a ISyntaxProviderManager in order to
 * expose the ISyntaxObject in other bundles.
 *
 * The main usage is to give to language consoles (like Groovy console) predefined properties to access easily to
 * bundle classes/utils/services
 *
 * @author Sylvain PALOMINOS (UBS 2018)
 * @author Erwan Bocher (CNRS)
 */
public interface ISyntaxProvider {

    /**
     * Add a ISyntaxObject.
     *
     * @param syntaxObject ISyntaxObject to add.
     */
    void add(ISyntaxObject syntaxObject);

    /**
     * Remove a ISyntaxObject.
     *
     * @param syntaxObject ISyntaxObject to remove.
     */
    void remove(ISyntaxObject syntaxObject);

    /**
     * Return the Collection of ISyntaxObject.
     *
     * @return The collection of ISyntaxObject.
     */
    Collection<ISyntaxObject> getISyntaxObjectCollection();

    /**
     * Return the name of the ISyntaxProvider.
     *
     * @return The name of the ISyntaxProvider.
     */
    String getName();
}
