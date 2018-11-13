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
 * Framework is distributed under GPL 3 license.
 *
 * Copyright (C) 2018 CNRS (Lab-STICC UMR CNRS 6285)
 *
 *
 * Framework is free software: you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * Framework is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * Framework. If not, see <http://www.gnu.org/licenses/>.
 *
 * For more information, please consult: <http://www.orbisgis.org/>
 * or contact directly:
 * info_at_ orbisgis.org
 */
package org.orbisgis.framework.root;

import org.apache.commons.cli.Options;
import org.apache.felix.framework.Logger;
import org.junit.Rule;
import org.junit.contrib.java.lang.system.ExpectedSystemExit;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.osgi.framework.launch.FrameworkFactory;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.URISyntaxException;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for the {@link org.orbisgis.framework.root.Main} class.
 *
 * @author Sylvain PALOMINOS (UBS 2018)
 * @author Erwan Bocher (CNRS)
 */
class MainTest {

    @Rule
    public final ExpectedSystemExit exit = ExpectedSystemExit.none();

    @Test
    void testConfigParameterMethods()
            throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {

        Main main = new Main();
        Method method = main.getClass().getDeclaredMethod("configFirstParameter");
        method.setAccessible(true);
        Object result = method.invoke(main);
        assertTrue(result instanceof Options);
        Options options = (Options)result;
        assertEquals(1, options.getOptions().size());
        assertTrue(options.hasLongOption("help"));


        method = main.getClass().getDeclaredMethod("configParameter", Options.class);
        method.setAccessible(true);
        result = method.invoke(main, options);
        assertTrue(result instanceof Options);
        options = (Options)result;
        assertEquals(7, options.getOptions().size());
        assertTrue(options.hasLongOption("help"));
        assertTrue(options.hasLongOption("archetype"));
        assertTrue(options.hasLongOption("debug"));
        assertTrue(options.hasLongOption("noFail"));
        assertTrue(options.hasLongOption("noUI"));
        assertTrue(options.hasLongOption("workspace"));
        assertTrue(options.hasLongOption("configProperties"));
    }

    @Test
    @Disabled
    public void testParseArgumentsArchetype()
            throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Main main = new Main();
        Method method = main.getClass().getDeclaredMethod("parseArguments", String[].class);
        method.setAccessible(true);
        //Test long
        Object result = method.invoke(main, new Object[]{new String[]{"--archetype"}});
    }

    @Test
    void testParseArgumentsDebug()
            throws NoSuchMethodException, InvocationTargetException, IllegalAccessException, NoSuchFieldException {
        Main main = new Main();
        Field field = main.getClass().getDeclaredField("LOGGER");
        field.setAccessible(true);
        Object obj = field.get(main);
        assertTrue(obj instanceof Logger);
        Logger logger = (Logger)obj;
        logger.setLogLevel(Logger.LOG_ERROR);

        Method method = main.getClass().getDeclaredMethod("parseArguments", String[].class);
        method.setAccessible(true);

        //Test long
        method.invoke(main, new Object[]{new String[]{"--debug=0"}});
        assertEquals(Logger.LOG_DEBUG, logger.getLogLevel());
        logger.setLogLevel(Logger.LOG_ERROR);

        //Test short
        method.invoke(main, new Object[]{new String[]{"-d 0"}});
        assertEquals(Logger.LOG_DEBUG, logger.getLogLevel());
    }

    @Test
    void testParseArgumentsNoFail()
            throws NoSuchMethodException, InvocationTargetException, IllegalAccessException, NoSuchFieldException {
        Main main = new Main();
        Field field = main.getClass().getDeclaredField("NO_FAIL_MODE");
        field.setAccessible(true);
        boolean bool = field.getBoolean(main);
        assertFalse(bool);
        Method method = main.getClass().getDeclaredMethod("parseArguments", String[].class);
        method.setAccessible(true);

        method.invoke(main, new Object[]{new String[]{}});
        bool = field.getBoolean(main);
        assertFalse(bool);
        method.invoke(main, new Object[]{new String[]{"--noFail"}});
        bool = field.getBoolean(main);
        assertTrue(bool);
    }

    @Test
    void testParseArgumentsNoUI()
            throws NoSuchMethodException, InvocationTargetException, IllegalAccessException, NoSuchFieldException {
        Main main = new Main();
        Field field = main.getClass().getDeclaredField("NO_UI_MODE");
        field.setAccessible(true);
        Method method = main.getClass().getDeclaredMethod("parseArguments", String[].class);
        method.setAccessible(true);

        method.invoke(main, new Object[]{new String[]{}});
        boolean bool = field.getBoolean(main);
        assertFalse(bool);
        method.invoke(main, new Object[]{new String[]{"--noUI"}});
        bool = field.getBoolean(main);
        assertTrue(bool);
    }

    @Test
    void testParseArgumentsWorkspace()
            throws NoSuchMethodException, InvocationTargetException, IllegalAccessException, NoSuchFieldException {
        Main main = new Main();
        Method method = main.getClass().getDeclaredMethod("parseArguments", String[].class);
        method.setAccessible(true);
        method.invoke(main, new Object[]{new String[]{}});
        Field field = main.getClass().getDeclaredField("systemWorkspace");
        field.setAccessible(true);
        Object obj = field.get(main);
        assertTrue(obj instanceof SystemWorkspace);
        SystemWorkspace workspace = (SystemWorkspace)obj;
        assertEquals(new File(System.getProperty("user.home")).getAbsolutePath() + File.separator + ".OrbisGIS" +
                        File.separator + "0.0", workspace.getWorkspaceFolderPath());

        main = new Main();
        method = main.getClass().getDeclaredMethod("parseArguments", String[].class);
        method.setAccessible(true);
        method.invoke(main, new Object[]{new String[]{"-w /tmp/tata"}});
        field = main.getClass().getDeclaredField("systemWorkspace");
        field.setAccessible(true);
        obj = field.get(main);
        assertTrue(obj instanceof SystemWorkspace);
        workspace = (SystemWorkspace)obj;
        assertEquals("/tmp/tata", workspace.getWorkspaceFolderPath());

        main = new Main();
        method = main.getClass().getDeclaredMethod("parseArguments", String[].class);
        method.setAccessible(true);
        method.invoke(main, new Object[]{new String[]{"--workspace=/tmp/tata"}});
        field = main.getClass().getDeclaredField("systemWorkspace");
        field.setAccessible(true);
        obj = field.get(main);
        assertTrue(obj instanceof SystemWorkspace);
        workspace = (SystemWorkspace)obj;
        assertEquals("/tmp/tata", workspace.getWorkspaceFolderPath());
    }

    @Test
    void testParseArgumentsConfigProperties()
            throws NoSuchMethodException, InvocationTargetException, IllegalAccessException, NoSuchFieldException, URISyntaxException {
        Main main = new Main();
        Method method = main.getClass().getDeclaredMethod("parseArguments", String[].class);
        method.setAccessible(true);
        method.invoke(main, new Object[]{new String[]{}});
        Field field = main.getClass().getDeclaredField("systemWorkspace");
        field.setAccessible(true);
        Object obj = field.get(main);
        assertTrue(obj instanceof SystemWorkspace);
        SystemWorkspace workspace = (SystemWorkspace) obj;
        assertEquals(new File(workspace.getWorkspaceFolderPath(), "conf").getAbsolutePath(),
                workspace.getConfFolderPath());

        File file = new File(Main.class.getResource("config.properties").toURI());
        main = new Main();
        method = main.getClass().getDeclaredMethod("parseArguments", String[].class);
        method.setAccessible(true);
        method.invoke(main, new Object[]{new String[]{"--configProperties="+file.getAbsolutePath()}});
        field = main.getClass().getDeclaredField("systemWorkspace");
        field.setAccessible(true);
        obj = field.get(main);
        assertTrue(obj instanceof SystemWorkspace);
        workspace = (SystemWorkspace) obj;
        assertEquals(new File(workspace.getWorkspaceFolderPath(), "conf").getAbsolutePath(), workspace.getConfFolderPath());
    }

    @Test
    void testParseArgumentsHelp()
            throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Main main = new Main();
        Method method = main.getClass().getDeclaredMethod("parseArguments", String[].class);
        method.setAccessible(true);
        method.invoke(main, new Object[]{new String[]{"-h"}});

        main = new Main();
        method = main.getClass().getDeclaredMethod("parseArguments", String[].class);
        method.setAccessible(true);
        method.invoke(main, new Object[]{new String[]{"-h"}});
    }

    @Test
    void testCheckJavaVersion()
            throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Main main = new Main();
        Method method = main.getClass().getDeclaredMethod("checkJavaVersion");
        method.setAccessible(true);
        assertTrue(method.invoke(main, new Object[]{}) instanceof Boolean);
        assertTrue((Boolean)method.invoke(main, new Object[]{}));
    }

    @Test
    void testCheckBadJavaVersion()
            throws NoSuchMethodException, InvocationTargetException, IllegalAccessException, NoSuchFieldException {
        Main main = new Main();

        Field field = main.getClass().getDeclaredField("SUPPORTED_JAVA_VERSION");
        field.setAccessible(true);
        Field modifiersField = Field.class.getDeclaredField("modifiers");
        modifiersField.setAccessible(true);
        modifiersField.setInt(field, field.getModifiers() & ~Modifier.FINAL);
        field.set(main, new int[]{-1});

        Method method = main.getClass().getDeclaredMethod("checkJavaVersion");
        method.setAccessible(true);
        assertTrue(method.invoke(main, new Object[]{}) instanceof Boolean);
        assertFalse((Boolean)method.invoke(main, new Object[]{}));
    }

    @Test
    void testGetFrameworkFactory()
            throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Main main = new Main();
        Method method = main.getClass().getDeclaredMethod("getFrameworkFactory");
        method.setAccessible(true);
        assertTrue(method.invoke(main, new Object[]{}) instanceof FrameworkFactory);
        FrameworkFactory frameworkFactory = (FrameworkFactory) method.invoke(main, new Object[]{});
        assertNotNull(frameworkFactory);
    }

    @Test
    void testStartFelix()
            throws NoSuchMethodException, InvocationTargetException, IllegalAccessException, NoSuchFieldException {
        Main main = new Main();

        Field field = main.getClass().getDeclaredField("TEST_MODE");
        field.setAccessible(true);
        field.set(main, true);

        Method method = main.getClass().getDeclaredMethod("parseArguments", String[].class);
        method.setAccessible(true);
        method.invoke(main, new Object[]{new String[]{"--noUI"}});

        method = main.getClass().getDeclaredMethod("startFelix");
        method.setAccessible(true);
        method.invoke(main, new Object[]{});
    }

    @Test
    void testMain()
            throws NoSuchMethodException, InvocationTargetException, IllegalAccessException, NoSuchFieldException {
        Main main = new Main();

        Field field = main.getClass().getDeclaredField("TEST_MODE");
        field.setAccessible(true);
        field.set(main, true);

        Method method = main.getClass().getDeclaredMethod("main", String[].class);
        method.setAccessible(true);
        method.invoke(main, new Object[]{new String[]{"--noUI"}});
    }

    @Test
    void testMainWithHelpArguments()
            throws NoSuchMethodException, InvocationTargetException, IllegalAccessException, NoSuchFieldException {
        Main main = new Main();

        Field field = main.getClass().getDeclaredField("TEST_MODE");
        field.setAccessible(true);
        field.set(main, true);

        Method method = main.getClass().getDeclaredMethod("main", String[].class);
        method.setAccessible(true);
        method.invoke(main, new Object[]{new String[]{"-h"}});
    }

    @Test
    void testMainWithBadJavaVersion()
            throws NoSuchMethodException, IllegalAccessException, NoSuchFieldException, InvocationTargetException {
        Main main = new Main();

        Field field = main.getClass().getDeclaredField("TEST_MODE");
        field.setAccessible(true);
        field.set(main, true);

        field = main.getClass().getDeclaredField("SUPPORTED_JAVA_VERSION");
        field.setAccessible(true);
        Field modifiersField = Field.class.getDeclaredField("modifiers");
        modifiersField.setAccessible(true);
        modifiersField.setInt(field, field.getModifiers() & ~Modifier.FINAL);
        field.set(main, new int[]{-1});

        Method method = main.getClass().getDeclaredMethod("main", String[].class);
        method.setAccessible(true);
        method.invoke(main, new Object[]{new String[]{"--noUI"}});
    }


    @Test
    void testBadArguments()
            throws NoSuchMethodException, IllegalAccessException, NoSuchFieldException, InvocationTargetException {
        Main main = new Main();

        Field field = main.getClass().getDeclaredField("TEST_MODE");
        field.setAccessible(true);
        field.set(main, true);

        Method method = main.getClass().getDeclaredMethod("parseArguments", String[].class);
        method.setAccessible(true);
        method.invoke(main, new Object[]{new String[]{"--noFail --workspace=https://test.fail"}});
    }
}
