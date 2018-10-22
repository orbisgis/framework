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

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.felix.framework.Logger;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleException;
import org.osgi.framework.Constants;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.*;
import java.util.jar.Attributes;
import java.util.jar.JarFile;
import java.util.jar.Manifest;

/**
 * Class able to load an archetype.
 *
 * @author Sylvain PALOMINOS (UBS 2018)
 * @author Erwan Bocher (CNRS)
 */
public class ArchetypeLoader {

    /**
     * Load the given archetype by downloading the bundle into the given  {@link org.orbisgis.framework.root.ISystemWorkspace}
     * @param coreWorkspace
     * @param bundleContext
     * @param archetypePath
     * @param logger
     */
    public static void loadArchetype(ISystemWorkspace coreWorkspace, BundleContext bundleContext, String archetypePath, Logger logger){
        logger.log(Logger.LOG_DEBUG, "Loading the archetype : "+archetypePath);
        if(!new File(archetypePath).exists()){
            logger.log(Logger.LOG_ERROR, "The archetype path is not valid");
            return;
        }
        Properties properties = new Properties();
        try {
            properties.load(new FileInputStream(new File(archetypePath)));
        } catch (IOException e) {
            logger.log(Logger.LOG_ERROR, "Unable to load the archetype : "+e.getLocalizedMessage());
            return;
        }
        Map<String, String> bundleNameSourceMap = new HashMap<>();
        Collections.list(properties.propertyNames()).forEach((bundle)->{
            if(bundle.toString().startsWith("bundle.")){
                String bundleSource = properties.getProperty(bundle.toString());
                String bundleLocation = properties.containsKey("location."+bundle)?properties.getProperty("location."+bundle):"";
                String path = coreWorkspace.getBundleFolderPath();
                if(bundleLocation.equals("..")){
                    path = coreWorkspace.getWorkspaceFolderPath();
                }
                else if(!bundleLocation.isEmpty()){
                    path = new File(coreWorkspace.getWorkspaceFolderPath(), bundleLocation).getAbsolutePath();
                }

                bundleNameSourceMap.put(bundleSource, path);
            }
        });

        //Download all the bundle which are not in the coreworkspace bundle folder.
        for(Iterator<Map.Entry<String, String>> it = bundleNameSourceMap.entrySet().iterator(); it.hasNext();){
            Map.Entry<String, String> entry = it.next();
            URL url;
            try {
                url = new URL(entry.getKey());
            } catch (MalformedURLException e) {
                logger.log(Logger.LOG_WARNING, "Cannot open the URL :"+entry.getKey());
                it.remove();
                continue;
            }
            String fileName = FilenameUtils.getName(url.getPath());
            File bundleFile = new File(coreWorkspace.getBundleFolderPath(), fileName);
            if(bundleFile.exists() && url.getQuery() == null){
                logger.log(Logger.LOG_DEBUG, "Bundle '"+entry.getKey()+"' is already in bundle folder.");
                it.remove();
            }
            else{
                if(!downloadBundle(entry.getValue(), url, logger)){
                    it.remove();
                }
            }
        }

        if (bundleContext != null) {
            //Launch All the bundle in not already present
            for(File file : new File(coreWorkspace.getBundleFolderPath()).listFiles()){
                URL url;
                try {
                    url = file.toURL();
                } catch (MalformedURLException e) {
                    logger.log(Logger.LOG_WARNING, "Cannot open the URL :"+file);
                    return;
                }
                String fileName = FilenameUtils.getName(url.getPath());
                if (isBundleInCache(url, coreWorkspace.getBundleFolderPath(), bundleContext)) {
                    logger.log(Logger.LOG_DEBUG, "The bundle '" + fileName + "' is already installed");
                }
                else{
                    try {
                        logger.log(Logger.LOG_DEBUG, "Installing bundle '" + fileName + "'");
                        bundleContext.installBundle(fileName, url.openStream());
                        bundleContext.getBundle(fileName).start();
                        logger.log(Logger.LOG_DEBUG, "The bundle '" + fileName + "' has been installed");
                    } catch (BundleException e) {
                        logger.log(Logger.LOG_ERROR, "An error occurred while installing the bundle '" + fileName + "'");
                    } catch (FileNotFoundException e) {
                        logger.log(Logger.LOG_ERROR, "An error occurred while finding the file '" + fileName + "'");
                    } catch (IOException e) {
                        logger.log(Logger.LOG_ERROR, "An error occurred while reading the file '" + fileName + "'");
                    }
                }
            }
        }
    }

    private static boolean downloadBundle(String bundleFolder, URL url, Logger logger){

        String fileName;
        String query = url.getQuery();
        if(query == null){
            fileName = FilenameUtils.getName(url.getPath());
        }
        else if(query.contains("a=")){
            fileName = query.substring(query.indexOf("a=")+2);
            if(fileName.contains("&")) {
                fileName = fileName.substring(0, fileName.indexOf("&"));
            }
        }
        else{
            logger.log(Logger.LOG_DEBUG, "Unable to downloading the bundle '"+url+"'");
            return false;
        }
        logger.log(Logger.LOG_DEBUG, "Downloading the bundle '"+url+"'");

        InputStream inputStream;
        try {
            inputStream = url.openStream();
        } catch (IOException e) {
            logger.log(Logger.LOG_WARNING, "Cannot open the URL :"+url);
            return false;
        }
        boolean result = true;
        try {
            if(!new File(bundleFolder).exists() && !new File(bundleFolder).mkdirs()){
                logger.log(Logger.LOG_WARNING, "Cannot create directory '"+bundleFolder+"'");
                return false;
            }
            ReadableByteChannel rbc = Channels.newChannel(inputStream);
            FileOutputStream fos = new FileOutputStream(new File(bundleFolder, fileName));
            fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
        } catch (IOException e) {
            logger.log(Logger.LOG_WARNING, "Cannot download the bundle '"+fileName+"'");
            result = false;
        }
        try {
            inputStream.close();
        } catch (IOException e) {
            logger.log(Logger.LOG_WARNING, "Cannot close the stream for the download of the bundle '"+fileName+"'");
        }
        return result;
    }

    private static boolean isBundleInCache(URL url, String bundleFolder, BundleContext bundleContext){
        String fileName = FilenameUtils.getName(url.getPath());
        try(JarFile jar = new JarFile(new File(bundleFolder, fileName))){
            Manifest manifest = jar.getManifest();
            Attributes attributes = manifest.getMainAttributes();
            String symbolicName = attributes.getValue(Constants.BUNDLE_SYMBOLICNAME);
            return bundleContext.getBundle(symbolicName) != null;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return true;
    }
}
