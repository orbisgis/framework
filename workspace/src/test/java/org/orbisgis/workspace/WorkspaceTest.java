/*
 * Bundle Workspace is part of the OrbisGIS platform
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
 * Workspace is distributed under GPL 3 license.
 *
 * Copyright (C) 2018 CNRS (Lab-STICC UMR CNRS 6285)
 *
 *
 * Workspace API is free software: you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * Workspace is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * Workspace. If not, see <http://www.gnu.org/licenses/>.
 *
 * For more information, please consult: <http://www.orbisgis.org/>
 * or contact directly:
 * info_at_ orbisgis.org
 */
package org.orbisgis.workspace;

import java.io.File;
import java.io.IOException;
import org.junit.jupiter.api.Assertions;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;


/**
 * @author Erwan Bocher (CNRS)
 * @author Sylvain PALOMINOS (UBS 2018)
 */
public class WorkspaceTest {
    
    @Test
    public void testCreateDefaultWorkspace() {
        Workspace w = new Workspace();
        assertTrue(w.getPath() != null);
    }

    @Test
    public void testCreateWorkspace() {
        File wp = new File("target/myWorkspace");
        Workspace w = new Workspace(wp.getAbsolutePath());
        assertEquals(wp.getAbsolutePath(), w.getPath().getAbsolutePath());
    }

    @Test
    public void testReCreateWorkspace() {
        File wp = new File("target/myWorkspace");
        Workspace w = new Workspace(wp.getAbsolutePath());
        assertEquals(wp.getAbsolutePath(), w.getPath().getAbsolutePath());
        w = new Workspace(wp.getAbsolutePath());
        assertEquals(wp.getAbsolutePath(), w.getPath().getAbsolutePath());
    }
    
    @Test
    public void testBadWorkspace() throws IOException {
        File wp = new File("target/myWorkspace.txt");        
        wp.createNewFile();
        Executable error = ()-> new Workspace(wp.getAbsolutePath());
        Assertions.assertThrows(IllegalArgumentException.class,error);
    }
    
    @Test
    public void testWorkspaceExists() throws IOException {
        File wp = new File("target/myWorkspace");        
        wp.delete();
        wp.mkdir();
        Workspace w = new Workspace(wp.getAbsolutePath());
        assertEquals(wp.getAbsolutePath(), w.getPath().getAbsolutePath());
    }
    
    @Test
    public void testWorkspaceEmpty() throws IOException {
        Executable error = ()-> new Workspace("");
        Assertions.assertThrows(IllegalArgumentException.class,error);
    }
}
