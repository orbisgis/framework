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
import org.osgi.framework.Version;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.obr.Repository;
import org.osgi.service.obr.RepositoryAdmin;
import org.osgi.service.obr.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;

/**
 * Implementation of the interface {@link org.orbisgis.bundlemanagerapi.IBundleUtils} interface with the OSGI framework.
 *
 * @author Sylvain PALOMINOS (UBS 2018)
 * @author Erwan Bocher (CNRS)
 */
@Component(immediate=true, service = {IBundleUtils.class})
public class BundleUtils implements IBundleUtils, ISyntaxObject {

    /** Logger */
    private static final Logger LOGGER = LoggerFactory.getLogger(BundleUtils.class);
    /** SyntaxObject name */
    private static final String NAME = "bundle";
    private static final URI ORBISGIS_OSGI_REPOSITORY = URI.create("http://plugins.orbisgis.org/.meta/obr.xml");
    private static final URI ORBISGIS_OSGI_REPOSITORY_SNAPSHOT =
            URI.create("http://nexus.orbisgis.org/content/shadows/obr-snapshot/.meta/obr.xml");
    /** {@link org.osgi.service.obr.RepositoryAdmin} used for the bundle resolution. */
    private RepositoryAdmin repositoryAdmin;

    @Activate
    public void init(){
        List<URI> serverURIList = new ArrayList<>();
        serverURIList.add(ORBISGIS_OSGI_REPOSITORY);
        serverURIList.add(ORBISGIS_OSGI_REPOSITORY_SNAPSHOT);
        Executors.newSingleThreadExecutor().execute(new RegisterRepositories(serverURIList));
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
        LOGGER.debug("Trying to install bundle '"+groupId+"."+artifactId+"' from OBR repositories");
        Resource higherVersion = null;
        for(Repository repository : repositoryAdmin.listRepositories()){
            for(Resource resource : repository.getResources()){
                if(resource.getSymbolicName().equals(groupId+"."+artifactId)) {
                    if(higherVersion == null || resource.getVersion().compareTo(higherVersion.getVersion()) > 0){
                        higherVersion = resource;
                    }
                    LOGGER.debug("Bundle '"+groupId+"."+artifactId+"' version '"+resource.getVersion()+"' found");
                }
            }
            LOGGER.debug("Bundle '"+groupId+"."+artifactId+"' not found in repository '"+repository.getName()+"'");
        }
        if(higherVersion != null){
            BundleItem bundleItem = new BundleItem(repositoryAdmin.resolver(), higherVersion);
            bundleItem.install();
            if(bundleItem.isInstalled()){
                LOGGER.debug("Bundle '"+groupId+"."+artifactId+"' version '"+higherVersion.getVersion()+"' installed");
            }
            else{
                LOGGER.debug("Bundle '"+groupId+"."+artifactId+"' version '"+higherVersion.getVersion()+"' not installed");
                return false;
            }
            bundleItem.start();
            if(bundleItem.isStarted()){
                LOGGER.debug("Bundle '"+groupId+"."+artifactId+"' version '"+higherVersion.getVersion()+"'  started");
                return true;
            }
            else{
                LOGGER.debug("Bundle '"+groupId+"."+artifactId+"' version '"+higherVersion.getVersion()+"'  not started");
                return false;
            }
        }
        LOGGER.debug("Bundle '"+groupId+"."+artifactId+"' not found");
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

    @Override
    public void addObrRepository(String name, String url) {
        if(!url.endsWith("obr.xml")){
            LOGGER.warn("The URL '" + url + "' doesn't seems to be a valid OBR repository");
        }
        try {
            repositoryAdmin.addRepository(new URL(url));
        } catch (MalformedURLException e) {
            LOGGER.error("Unable to generate the URL '"+url+"'");
        } catch (Exception e) {
            LOGGER.error("Unable to add the repository '"+url+"'");
        }
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

    @Override
    public String getName() {
        return NAME;
    }

    private class RegisterRepositories implements Runnable {

        List<URI> serverURIList;

        public RegisterRepositories(List<URI> serverURIList){
            this.serverURIList = serverURIList;
        }

        @Override
        public void run() {
            for(URI serverURI : serverURIList) {
                try {
                    repositoryAdmin.addRepository(serverURI.toURL());
                } catch (Exception ex) {
                    //Tests if the exception is because of a problem accessing to the OrbisGIS nexus.
                    if (ex.getCause() instanceof UnknownHostException &&
                            ex.getCause().getMessage().equals(serverURI.getAuthority())) {
                        LOGGER.error("Unable to access to " + serverURI.getAuthority() +
                                ". Please check your internet connexion.");
                    } else {
                        LOGGER.error(ex.getLocalizedMessage(), ex);
                    }
                }
            }
        }
    }
}
