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

import org.orbisgis.bundlemanagerapi.IBundleItem;
import org.osgi.framework.*;
import org.osgi.service.obr.Requirement;
import org.osgi.service.obr.Resolver;
import org.osgi.service.obr.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Implementation of the {@link org.orbisgis.bundlemanagerapi.IBundleItem} interface with the OSGI framework.
 * While the bundle is not installed, it is referenced with a {@link org.osgi.service.obr.Resource} object which will
 * be used on installation to retrieve the {@link org.osgi.framework.Bundle}.
 *
 * @author Sylvain PALOMINOS (UBS 2018)
 * @author Erwan Bocher (CNRS)
 */
public class BundleItem implements IBundleItem {

    /** Logger */
    private static final Logger LOGGER = LoggerFactory.getLogger(BundleItem.class);
    /** OSGI id of the bundle. It is set once the bundle has been installed */
    private long id = -1;
    /** Resolver used for the bundle installation, coming from the {@link org.osgi.service.obr.RepositoryAdmin}. */
    private final Resolver resolver;
    /** Resource used for the installation of the bundle. */
    private final Resource resource;

    /**
     * Main constructor.
     *
     * @param resolver Resolver used for the bundle installation, coming from the
     * {@link org.osgi.service.obr.RepositoryAdmin}.
     * @param resource Resource used for the installation of the bundle.
     */
    public BundleItem(Resolver resolver, Resource resource){
        this.resolver = resolver;
        this.resolver.add(resource);
        this.resource = resource;
        for(Bundle bundle : FrameworkUtil.getBundle(BundleItem.class).getBundleContext().getBundles()){
            if(bundle.getSymbolicName() != null && bundle.getSymbolicName().equals(resource.getSymbolicName())){
                this.id = bundle.getBundleId();
            }
        }
    }

    /**
     * Return the {@link org.osgi.framework.Bundle} represented by this class.
     *
     * @return The {@link org.osgi.framework.Bundle} represented by this class.
     */
    private Bundle getBundle(){
        return FrameworkUtil.getBundle(BundleItem.class).getBundleContext().getBundle(id);
    }

    @Override
    public boolean isInstallReady() {
        return resolver != null && resolver.getAddedResources().length!=0;
    }

    @Override
    public void install() {
        if(isInstallReady()) {
            resolver.resolve();
            Resource[] resources = resolver.getRequiredResources();
            if ((resources != null) && (resources.length > 0)) {
                StringBuilder sb = new StringBuilder();
                sb.append("Unsatisfied requirement :\n");
                for (Resource resource : resources) {
                    sb.append(resource.getPresentationName());
                    sb.append(" (");
                    sb.append(resource.getVersion());
                    sb.append(")\n");
                }
                resources = resolver.getOptionalResources();
                if ((resources != null) && (resources.length > 0)) {
                    for (Resource resource : resources) {
                        sb.append("Optional, ");
                        sb.append(resource.getPresentationName());
                        sb.append(" (");
                        sb.append(resource.getVersion());
                        sb.append(")\n");
                    }
                }
                LOGGER.info(sb.toString());
            }
            resolver.deploy(true);
            for (Bundle bundle : FrameworkUtil.getBundle(BundleItem.class).getBundleContext().getBundles()) {
                if (bundle.getSymbolicName() != null && bundle.getSymbolicName().equals(resource.getSymbolicName())) {
                    this.id = bundle.getBundleId();
                }
            }
            try {
                getBundle().stop();
            } catch (BundleException e) {
                LOGGER.error("Error on installing the bundle.\n"+e.getLocalizedMessage());
            }
        }
    }

    @Override
    public boolean isInstalled() {
        Bundle bundle = getBundle();
        return (bundle!=null) &&
                (bundle.getState()==Bundle.RESOLVED ||
                        bundle.getState()==Bundle.STARTING ||
                        bundle.getState()==Bundle.STOPPING ||
                        bundle.getState()==Bundle.ACTIVE);
    }

    @Override
    public boolean isStartReady() {
        Bundle bundle = getBundle();
        return (bundle!=null) && (bundle.getState()==Bundle.RESOLVED);
    }

    @Override
    public void start() {
        Bundle bundle = getBundle();
        if(bundle != null && isStartReady()) {
            try {
                bundle.start();
            } catch (BundleException e) {
                LOGGER.error("Unable to start the bundle '" + getBundle().getSymbolicName() +
                        "'.\n" + e.getLocalizedMessage());
            }
        }
        else{
            LOGGER.error("Unable to start the bundle '" + id + "', no bundle found with this id");
        }
    }

    @Override
    public boolean isStarting() {
        Bundle bundle = getBundle();
        return (bundle!=null) && (bundle.getState()==Bundle.STARTING);
    }

    @Override
    public boolean isStarted() {
        Bundle bundle = getBundle();
        return (bundle!=null) && (bundle.getState()==Bundle.ACTIVE);
    }

    @Override
    public boolean isStopReady() {
        Bundle bundle = getBundle();
        return (bundle!=null) && (bundle.getState()==Bundle.ACTIVE);
    }

    @Override
    public boolean isUpdateReady() {
        Bundle bundle = getBundle();
        return (bundle!=null) && (bundle.getState()!=Bundle.UNINSTALLED);
    }

    @Override
    public void update() {
        Bundle bundle = getBundle();
        if(bundle != null && isUpdateReady()) {
            try {
                bundle.update();
            } catch (BundleException e) {
                LOGGER.error("Unable to update the bundle '" + getBundle().getSymbolicName() +
                        "'.\n" + e.getLocalizedMessage());
            }
        }
        else{
            LOGGER.error("Unable to update the bundle '" + id + "', no bundle found with this id");
        }
    }

    @Override
    public void stop() {
        Bundle bundle = getBundle();
        if(bundle != null && isStopReady()) {
            try {
                bundle.stop();
            } catch (BundleException e) {
                LOGGER.error("Unable to stop the bundle '" + getBundle().getSymbolicName() +
                        "'.\n" + e.getLocalizedMessage());
            }
        }
        else{
            LOGGER.error("Unable to stop the bundle '" + id + "', no bundle found with this id");
        }
    }

    @Override
    public boolean isStopping() {
        Bundle bundle = getBundle();
        return (bundle!=null) &&
                (bundle.getState()!=Bundle.STOPPING);
    }

    @Override
    public boolean isStopped() {
        Bundle bundle = getBundle();
        return (bundle!=null) &&
                (bundle.getState()!=Bundle.ACTIVE) &&
                (bundle.getState()!=Bundle.STOPPING) &&
                (bundle.getState()!=Bundle.STARTING);
    }

    @Override
    public boolean isUninstallReady() {
        Bundle bundle = getBundle();
        return (bundle!=null) && (bundle.getState()!=Bundle.UNINSTALLED);
    }

    @Override
    public void uninstall() {
        Bundle bundle = getBundle();
        if(bundle != null && isUninstallReady()) {
            try {
                bundle.uninstall();
                id = -1;
            } catch (BundleException e) {
                LOGGER.error("Unable to start the bundle '" + getBundle().getSymbolicName() +
                        "'.\n" + e.getLocalizedMessage());
            }
        }
        else{
            LOGGER.error("Unable to start the bundle '" + id + "', no bundle found with this id");
        }
    }

    @Override
    public boolean isUninstalled() {
        Bundle bundle = getBundle();
        return (bundle==null) || (bundle.getState()!=Bundle.UNINSTALLED);
    }

    @Override
    public String getSymbolicName() {
        return getProperty(Constants.BUNDLE_SYMBOLICNAME);
    }

    @Override
    public String getVersion() {
        return getProperty(Constants.BUNDLE_VERSION);
    }

    @Override
    public String getName() {
        return getProperty(Constants.BUNDLE_NAME);
    }

    @Override
    public String getDescription() {
        return getProperty(Constants.BUNDLE_DESCRIPTION);
    }

    @Override
    public List<String> getBundleCategories() {
        String categories = getProperty(Constants.BUNDLE_CATEGORY);
        if(categories != null){
            return Arrays.asList(categories.split(","));
        }
        return null;
    }

    @Override
    public Map<String, String> getProperties() {
        Bundle bundle = getBundle();
        if(bundle != null && bundle.getHeaders()!= null){
            Dictionary<String, String> dict = bundle.getHeaders();
            List<String> keys = Collections.list(dict.keys());
            return keys.stream().collect(Collectors.toMap(Function.identity(), dict::get));
        }
        else if(resource.getProperties() != null){
            return (Map<String, String>)resource.getProperties();
        }
        return null;
    }

    @Override
    public String getProperty(String propertyName) {
        Bundle bundle = getBundle();
        if(bundle != null && bundle.getHeaders()!= null){
            return bundle.getHeaders().get(propertyName);
        }
        else {
            switch(propertyName){
                case Constants.BUNDLE_SYMBOLICNAME:
                    return resource.getSymbolicName();
                case Constants.BUNDLE_NAME:
                    return resource.getPresentationName();
                case Constants.BUNDLE_CATEGORY:
                    return String.join(",", resource.getCategories());
                case Constants.BUNDLE_VERSION:
                    return resource.getVersion().toString();
            }
        }
        return null;
    }
}
