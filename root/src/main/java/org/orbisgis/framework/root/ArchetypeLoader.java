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

import org.apache.felix.framework.Logger;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleException;
import org.osgi.framework.Constants;

import java.io.*;
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
        Collections.list(properties.propertyNames()).forEach((source)->{
            if(source.toString().startsWith("source.")){
                String bundleName = properties.getProperty("bundle."+source);
                Arrays.stream(bundleName.split(","))
                        .forEachOrdered(str -> bundleNameSourceMap.put(str,  properties.getProperty(source.toString())));
            }
        });

        //Download all the bundle which are not in the coreworkspace bundle folder.
        for(Iterator<Map.Entry<String, String>> it = bundleNameSourceMap.entrySet().iterator(); it.hasNext();){
            Map.Entry<String, String> entry = it.next();
            File bundleFile = new File(coreWorkspace.getBundleFolderPath()+File.separator+entry.getKey());
            if(bundleFile.exists()){
                logger.log(Logger.LOG_DEBUG, "Bundle '"+entry.getKey()+"' is already in bundle folder.");
                it.remove();
            }
            else{
                if(!downloadBundle(coreWorkspace.getBundleFolderPath(), entry.getKey(), entry.getValue(), logger)){
                    it.remove();
                }
            }
        }

        //Launch All the bundle in not already present
        bundleNameSourceMap.forEach((key, value) -> {
            if (isBundleInCache(key, coreWorkspace.getBundleFolderPath(), bundleContext)) {
                logger.log(Logger.LOG_DEBUG, "The bundle '" + key + "' is already installed");
            }
            else{
                try {
                    logger.log(Logger.LOG_DEBUG, "Installing bundle '" + key + "'");
                    bundleContext.installBundle(key, new FileInputStream(new File(coreWorkspace.getBundleFolderPath(), key)));
                    bundleContext.getBundle(key).start();
                    logger.log(Logger.LOG_DEBUG, "The bundle '" + key + "' has been installed");
                } catch (BundleException e) {
                    logger.log(Logger.LOG_ERROR, "An error occurred while installing the bundle '" + key + "'");
                } catch (FileNotFoundException e) {
                    logger.log(Logger.LOG_ERROR, "An error occurred while finding the file '" + key + "'");
                }
            }
        });
    }

    private static boolean downloadBundle(String bundleFolder, String fileName, String source, Logger logger){
        logger.log(Logger.LOG_DEBUG, "Downloading the bundle '"+fileName+"' from '"+source+"'");

        InputStream inputStream;
        try {
            inputStream = new URL(source + "/" + fileName).openStream();
        } catch (IOException e) {
            logger.log(Logger.LOG_WARNING, "Cannot open the URL :"+source+"/"+fileName);
            return false;
        }
        boolean result = true;
        try {
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

    private static boolean isBundleInCache(String bundleFileName, String bundleFolder, BundleContext bundleContext){
        try(JarFile jar = new JarFile(new File(bundleFolder, bundleFileName))){
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
