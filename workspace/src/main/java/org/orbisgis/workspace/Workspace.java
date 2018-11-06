/*
 * Bundle Workspace API is part of the OrbisGIS platform
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
 * Workspace API is distributed under GPL 3 license.
 *
 * Copyright (C) 2018 CNRS (Lab-STICC UMR CNRS 6285)
 *
 *
 * Workspace API is free software: you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * OrbisWPS is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * Workspace API. If not, see <http://www.gnu.org/licenses/>.
 *
 * For more information, please consult: <http://www.orbisgis.org/>
 * or contact directly:
 * info_at_ orbisgis.org
 */
package org.orbisgis.workspace;

import java.io.File;
import org.orbisgis.workspaceapi.IWorkspace;

/**
 * Interface for Workspace bundle.
 * 
 * A Workspace is  directory that contains files or sub directories to store
 * all data required by the bundle(s)
 * 
 * @author Sylvain PALOMINOS (UBS 2018)
 * @author Erwan Bocher (CNRS)
 */
public class Workspace implements IWorkspace {

    private final File path;
    
    /**
     * Create a default tmp directory 
     */
    public Workspace() {
        this(System.getProperty("java.io.tmpdir") + File.separator + "OrbisGIS");
    }

    /**
     * Create a new directory or use the existing one
     *
     * @param path
     */
    public Workspace(String path) {
        this.path = new File(path);
        if (this.path.isDirectory()) {
            if (this.path.exists()) {                
            } else {
                this.path.mkdir();
            }
        } else {
            throw new IllegalArgumentException("Invalid directory path");
        }

    }

    @Override
    public File getPath() {
        return path;
    }

}
