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
package org.orbisgis.bundlemanager;

import org.orbisgis.bundlemanagerapi.IBundleUtils;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.obr.Repository;
import org.osgi.service.obr.RepositoryAdmin;
import org.osgi.service.obr.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implementation of the interface {@link org.orbisgis.bundlemanagerapi.IBundleUtils} interface with the OSGI framework.
 */
@Component(immediate=true, service = {IBundleUtils.class})
public class BundleUtils implements IBundleUtils {

    /** Logger */
    private static final Logger LOGGER = LoggerFactory.getLogger(BundleUtils.class);
    /** {@link org.osgi.service.obr.RepositoryAdmin} used for the bundle resolution. */
    private RepositoryAdmin repositoryAdmin;

    @Activate
    public void init(){
        LOGGER.debug("Component started");
    }

    @Override
    public boolean install(String bundleId){
        String[] split = bundleId.split(":");
        if(split.length >=2) {
            return install(bundleId.split(":")[0], bundleId.split(":")[1]);
        }
        else{
            LOGGER.error("The id of the bundle to install should have the following pattern : groupId:artifactId");
        }
        return false;
    }

    @Override
    public boolean install(String groupId, String artifactId){
        LOGGER.debug("Trying to install bundle '"+groupId+"."+artifactId+"'");
        for(Repository repository : repositoryAdmin.listRepositories()){
            for(Resource resource : repository.getResources()){
                if(resource.getSymbolicName().equals(groupId+"."+artifactId)) {
                    LOGGER.debug("Bundle '"+groupId+"."+artifactId+"' found");
                    BundleItem bundleItem = new BundleItem(repositoryAdmin.resolver(), resource);
                    bundleItem.install();
                    if(bundleItem.isInstalled()){
                        LOGGER.debug("Bundle '"+groupId+"."+artifactId+"' installed");
                    }
                    else{
                        LOGGER.debug("Bundle '"+groupId+"."+artifactId+"' not installed");
                    }
                    return bundleItem.isInstalled();
                }
            }
        }
        return false;
    }

    @Override
    public boolean start(String bundleId){
        String[] split = bundleId.split(":");
        if(split.length >=2) {
            return start(bundleId.split(":")[0], bundleId.split(":")[1]);
        }
        else{
            LOGGER.error("The id of the bundle to install should have the following pattern : groupId:artifactId");
        }
        return false;
    }

    @Override
    public boolean start(String groupId, String artifactId){
        LOGGER.debug("Trying to install bundle '"+groupId+"."+artifactId+"'");
        for(Repository repository : repositoryAdmin.listRepositories()){
            for(Resource resource : repository.getResources()){
                if(resource.getSymbolicName().equals(groupId+"."+artifactId)) {
                    LOGGER.debug("Bundle "+groupId+"."+artifactId+" found");
                    BundleItem bundleItem = new BundleItem(repositoryAdmin.resolver(), resource);
                    if(!bundleItem.isInstalled()){
                        LOGGER.error("The bundle '"+groupId+"."+artifactId+"' is not installed yet");
                    }
                    else {
                        bundleItem.start();
                        if (bundleItem.isStarted()) {
                            LOGGER.debug("Bundle '" + groupId + "." + artifactId + "' started");
                        } else {
                            LOGGER.debug("Bundle '" + groupId + "." + artifactId + "' not started");
                        }
                        return bundleItem.isStarted();
                    }
                }
            }
        }
        return false;
    }

    @Override
    public boolean update(String bundleId) {
        String[] split = bundleId.split(":");
        if(split.length >=2) {
            return update(bundleId.split(":")[0], bundleId.split(":")[1]);
        }
        else{
            LOGGER.error("The id of the bundle to install should have the following pattern : groupId:artifactId");
        }
        return false;
    }

    @Override
    public boolean update(String groupId, String artifactId) {
        LOGGER.debug("Trying to install bundle '"+groupId+"."+artifactId+"'");
        for(Repository repository : repositoryAdmin.listRepositories()){
            for(Resource resource : repository.getResources()){
                if(resource.getSymbolicName().equals(groupId+"."+artifactId)) {
                    LOGGER.debug("Bundle "+groupId+"."+artifactId+" found");
                    BundleItem bundleItem = new BundleItem(repositoryAdmin.resolver(), resource);
                    if(!bundleItem.isInstalled()){
                        LOGGER.error("The bundle '"+groupId+"."+artifactId+"' is not installed yet");
                    }
                    else {
                        bundleItem.update();
                        if (bundleItem.isStarted()) {
                            LOGGER.debug("Bundle '" + groupId + "." + artifactId + "' updated");
                        } else {
                            LOGGER.debug("Bundle '" + groupId + "." + artifactId + "' not updated");
                        }
                        return bundleItem.isStarted();
                    }
                }
            }
        }
        return false;
    }

    @Override
    public boolean stop(String bundleId){
        String[] split = bundleId.split(":");
        if(split.length >=2) {
            return stop(bundleId.split(":")[0], bundleId.split(":")[1]);
        }
        else{
            LOGGER.error("The id of the bundle to install should have the following pattern : groupId:artifactId");
        }
        return false;
    }

    @Override
    public boolean stop(String groupId, String artifactId){
        LOGGER.debug("Trying to install bundle '"+groupId+"."+artifactId+"'");
        for(Repository repository : repositoryAdmin.listRepositories()){
            for(Resource resource : repository.getResources()){
                if(resource.getSymbolicName().equals(groupId+"."+artifactId)) {
                    LOGGER.debug("Bundle "+groupId+"."+artifactId+" found");
                    BundleItem bundleItem = new BundleItem(repositoryAdmin.resolver(), resource);
                    if(!bundleItem.isInstalled()){
                        LOGGER.error("The bundle '"+groupId+"."+artifactId+"' is not installed yet");
                    }
                    else {
                        bundleItem.stop();
                        if (bundleItem.isStopped()) {
                            LOGGER.debug("Bundle '" + groupId + "." + artifactId + "' stopped");
                        } else {
                            LOGGER.debug("Bundle '" + groupId + "." + artifactId + "' not stopped");
                        }
                        return bundleItem.isStopped();
                    }
                }
            }
        }
        return false;
    }

    @Override
    public boolean uninstall(String bundleId){
        String[] split = bundleId.split(":");
        if(split.length >=2) {
            return uninstall(bundleId.split(":")[0], bundleId.split(":")[1]);
        }
        else{
            LOGGER.error("The id of the bundle to install should have the following pattern : groupId:artifactId");
        }
        return false;
    }

    @Override
    public boolean uninstall(String groupId, String artifactId){
        LOGGER.debug("Trying to install bundle '"+groupId+"."+artifactId+"'");
        for(Repository repository : repositoryAdmin.listRepositories()){
            for(Resource resource : repository.getResources()){
                if(resource.getSymbolicName().equals(groupId+"."+artifactId)) {
                    LOGGER.debug("Bundle "+groupId+"."+artifactId+" found");
                    BundleItem bundleItem = new BundleItem(repositoryAdmin.resolver(), resource);
                    if(!bundleItem.isInstalled()){
                        LOGGER.error("The bundle '"+groupId+"."+artifactId+"' is not installed yet");
                    }
                    else {
                        bundleItem.uninstall();
                        if (bundleItem.isUninstalled()) {
                            LOGGER.debug("Bundle '" + groupId + "." + artifactId + "' uninstalled");
                        } else {
                            LOGGER.debug("Bundle '" + groupId + "." + artifactId + "' not uninstalled");
                        }
                        return bundleItem.isUninstalled();
                    }
                }
            }
        }
        return false;
    }

    /**
     * Set the {@link org.osgi.service.obr.RepositoryAdmin} to use for the bundle resolution.
     *
     * @param repositoryAdmin {@link org.osgi.service.obr.RepositoryAdmin} to use for the bundle resolution.
     */
    @Reference
    public void setRepositoryAdmin(RepositoryAdmin repositoryAdmin) {
        LOGGER.debug("RepositoryAdmin set");
        this.repositoryAdmin = repositoryAdmin;
    }

    /**
     * Unset the {@link org.osgi.service.obr.RepositoryAdmin}.
     *
     * @param repositoryAdmin {@link org.osgi.service.obr.RepositoryAdmin} unset.
     */
    public void unsetRepositoryAdmin(RepositoryAdmin repositoryAdmin) {
        this.repositoryAdmin = null;
    }
}
