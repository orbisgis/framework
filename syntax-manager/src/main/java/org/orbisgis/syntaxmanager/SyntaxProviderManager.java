/*
 * Syntax Manager is part of the OrbisGIS platform
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
 * Syntax Manager is distributed under GPL 3 license.
 *
 * Copyright (C) 2018 CNRS (Lab-STICC UMR CNRS 6285)
 *
 *
 * Syntax Manager is free software: you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * Syntax Manager is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * Syntax Manager. If not, see <http://www.gnu.org/licenses/>.
 *
 * For more information, please consult: <http://www.orbisgis.org/>
 * or contact directly:
 * info_at_ orbisgis.org
 */
package org.orbisgis.syntaxmanager;

import org.orbisgis.syntaxmanagerapi.ISyntaxObject;
import org.orbisgis.syntaxmanagerapi.ISyntaxProvider;
import org.orbisgis.syntaxmanagerapi.ISyntaxProviderManager;
import org.osgi.service.component.annotations.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Implementation of ISyntaxProviderManager.
 *
 * @author Sylvain PALOMINOS (UBS 2018)
 * @author Erwan Bocher (CNRS)
 */
@Component(immediate = true, service = {ISyntaxProviderManager.class})
public class SyntaxProviderManager implements ISyntaxProviderManager, ISyntaxObject {

    /** Name of the SyntaxObject */
    private static final String NAME = "syntaxManager";
    private static final Logger LOGGER = LoggerFactory.getLogger(SyntaxProviderManager.class);

    /** List of ISyntaxProvider */
    private List<ISyntaxProvider> syntaxProviderList = new ArrayList<>();

    @Activate
    public void activate(){
        SyntaxProvider syntaxProvider = new SyntaxProvider();
        syntaxProvider.add(this);
        this.registerSyntaxProvider(syntaxProvider);
    }

    @Override
    @Reference(service = ISyntaxProvider.class, cardinality = ReferenceCardinality.MULTIPLE, policy = ReferencePolicy.DYNAMIC)
    public void registerSyntaxProvider(ISyntaxProvider syntaxProvider){
        syntaxProviderList.add(syntaxProvider);
        LOGGER.debug("SyntaxProvider '" + syntaxProvider.getName() + "' registered");
    }

    @Override
    public void unregisterSyntaxProvider(ISyntaxProvider syntaxProvider){
        syntaxProviderList.remove(syntaxProvider);
        LOGGER.debug("SyntaxProvider '" + syntaxProvider.getName() + "' unregistered");
    }

    @Override
    public List<ISyntaxProvider> getSyntaxProviderList(){
        return syntaxProviderList;
    }

    @Override
    public String getName() {
        return NAME;
    }

    private class SyntaxProvider implements ISyntaxProvider {

        private static final String NAME = "SyntaxManager Provider";
        private List<ISyntaxObject> syntaxObjectList = new ArrayList<>();

        @Override
        public void add(ISyntaxObject syntaxObject) {
            syntaxObjectList.add(syntaxObject);
        }

        @Override
        public void remove(ISyntaxObject syntaxObject) {
            syntaxObjectList.remove(syntaxObject);
        }

        @Override
        public Collection<ISyntaxObject> getISyntaxObjectCollection() {
            return syntaxObjectList;
        }

        @Override
        public String getName() {
            return null;
        }
    }
}
