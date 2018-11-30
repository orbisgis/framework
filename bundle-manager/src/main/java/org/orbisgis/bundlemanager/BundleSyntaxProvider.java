/*
 * Bundle Manager is part of the OrbisGIS platform
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
 * Bundle Manager is distributed under GPL 3 license.
 *
 * Copyright (C) 2018 CNRS (Lab-STICC UMR CNRS 6285)
 *
 *
 * Bundle Manager is free software: you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * Bundle Manager is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * Bundle Manager. If not, see <http://www.gnu.org/licenses/>.
 *
 * For more information, please consult: <http://www.orbisgis.org/>
 * or contact directly:
 * info_at_ orbisgis.org
 */
package org.orbisgis.bundlemanager;

import org.orbisgis.bundlemanagerapi.IBundleUtils;
import org.orbisgis.syntaxmanagerapi.ISyntaxObject;
import org.orbisgis.syntaxmanagerapi.ISyntaxProvider;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Extension of the SyntaxProvider class which register the BundleUtils component under the name 'bundle'.
 *
 * @author Sylvain PALOMINOS (UBS 2018)
 * @author Erwan Bocher (CNRS)
 */
@Component(immediate = true, service = {ISyntaxProvider.class})
public class BundleSyntaxProvider implements ISyntaxProvider {

    /** SyntaxProvider name */
    private static final String NAME = "BundleSyntaxProvider";

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
        return NAME;
    }

    /**
     * Set the IBundleUtils in order to expose it as a SyntaxObject.
     *
     * @param bundleUtils IBundleUtils to expose it as a SyntaxObject and to register this ISyntaxProvider.
     */
    @Reference
    public void setIBundleUtils(IBundleUtils bundleUtils) {
        if(bundleUtils instanceof ISyntaxObject) {
            this.add((ISyntaxObject) bundleUtils);
        }
    }

    /**
     * Unset the IBundleUtils.
     *
     * @param bundleUtils IBundleUtils unset.
     */
    public void unsetIBundleUtils(IBundleUtils bundleUtils) {
        if(bundleUtils instanceof ISyntaxObject) {
            this.remove((ISyntaxObject) bundleUtils);
        }
    }
}
