# Framework

This framework is used to manage all the basic OrbisGIS OSGi modules
in order to set a basic framework for the creation of other modules.
It requires JAVA 11.

### Utility modules

The utility modules are not aimed to be extended or reuse in other
bundles. Their are used for a specific usage such ad the bundleling of
the application (`dist`) or the launching of the application (root).

##### Root

This module is the entry point of the framework. It is build into an
executable jar which start the application in three steps :
initialization, configuration, start

##### Dist

This module bundle the jar of the root module and its basic
dependencies under a .zip file.

### API modules

Those modules are used to define the APIs used in the framework and
available for the creation of new module.

##### Bundle Manager API

The bundle manager API contains the interfaces used for managing of
the lifecycle of OSGi bundle.

##### Syntax Manager API

The syntax manager API contains the interfaces used for the exposition
of the syntax object. This exposition allows to reuse object instances
(such as utilities, services or classes) under a variable name. The
main usage for this API is to permit to register Object as predefined
properties in languages script engine/shell (like Groovy, R ...).

##### Workspace API

The workspace API contains the interfaces used for the creation of the
classes used for the creation and the access to user and project
workspace.

### Implementation modules

Implementation of the API modules.

##### Bundle Manager

Implementation of the `bundle-manager-api` module.
`ISyntaxObject` exposed :
 - `BUndleUtils` instance under the name `bundle`

##### Syntax Manager

Implementation of the `syntax-manager-api` module.
`ISyntaxObject` exposed :
 - `SyntaxProviderManager` instance under the name `bundleManager`

##### Workspace

Implementation of the `workspace-api` module.
