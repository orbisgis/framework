package org.orbisgis.framework.root;

import org.apache.felix.framework.Logger;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

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
