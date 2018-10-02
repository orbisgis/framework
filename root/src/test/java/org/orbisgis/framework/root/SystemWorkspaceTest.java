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
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Test class for the {@link org.orbisgis.framework.root.SystemWorkspace} class.
 *
 * @author Sylvain PALOMINOS (UBS 2018)
 * @author Erwan Bocher (CNRS)
 */
public class SystemWorkspaceTest {

    private Logger logger = new Logger();

    @Test
    void constructorTest() {
        String path = new File("target", UUID.randomUUID().toString()).getAbsolutePath();
        SystemWorkspace systemWorkspace = new SystemWorkspace(path, 0, 0, logger);
        assertEquals(path, systemWorkspace.getWorkspaceFolderPath());
        systemWorkspace = new SystemWorkspace(null, 0, 0, logger);
        assertTrue(systemWorkspace.getWorkspaceFolderPath().contains(".OrbisGIS"));
    }

    @Test
    void getTest() {
        String path = new File("target", UUID.randomUUID().toString()).getAbsolutePath();
        SystemWorkspace systemWorkspace = new SystemWorkspace(path, 0, 0, logger);
        assertEquals(path + File.separator + "app", systemWorkspace.getApplicationFolderPath());
        assertEquals(path + File.separator + "bundle", systemWorkspace.getBundleFolderPath());
        assertEquals(path + File.separator + "cache", systemWorkspace.getCacheFolderPath());
        assertNull(systemWorkspace.getFelixConfigPath());
        assertEquals(path + File.separator + "orbisgis.log", systemWorkspace.getLogFilePath());
        assertEquals(path + File.separator + "tmp", systemWorkspace.getTempFolderPath());
        assertEquals(path, systemWorkspace.getWorkspaceFolderPath());

        systemWorkspace.setFelixConfigPath(path + File.separator + "config.properties");
        assertEquals(path + File.separator + "config.properties", systemWorkspace.getFelixConfigPath());
    }

    @Test
    void getLoad() {
        File file = new File("target", UUID.randomUUID().toString());
        new File(file, "file1").mkdir();
        new File(file, "subfolder/file2").mkdirs();
        String path = file.getAbsolutePath();
        SystemWorkspace systemWorkspace = new SystemWorkspace(path, 0, 0, logger);
        systemWorkspace.loadWorkspace(true);
    }
}
