# Root module

This module is the entry point of the framework.
It is build into an executable jar which start the application in three steps.

### Initialization

The first step parse the arguments given in order to initialize resources.
Arguments can be called with the short name syntax (-letter argument1, argument2) or long name (--longName=argument1,argument2).
The following arguments are available  :
 - h/help : Display the help message
 - d/debug : Launch OrbisGIS into the 'debug' mode. A wait time can be set as argument in order to give time to start debug client.
 - noFail : Removes the system workspace in order to start with a fresh installation
 - noUI : Start the application without any user interface.
 - w/workspace : Sets the path of the workspace to use. The path is given as argument.
 - configProperties : Sets the path of the config.properties file to use for the Apache Felix framework. The path is given as argument.

### Configuration

The second step configure the `SystemWorkspace` in order to start the application.
In the workspace path set with the argument `workspace` which can be cleared with the argument `noFail`,
the `ArchetypeLoader` download all the bundle specified if the archetype to use.
By default, if the argument `noUI` has been set, the archetype `minArchetype` is loaded and disable the `GUI` bundles.

### Start

Once the `SystemWorkspace` configured and ready, the downloaded bundles are installed in the OSGI framework and started.
The frameword used is Apache Felix.