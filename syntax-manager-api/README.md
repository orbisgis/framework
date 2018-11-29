# Syntax Manager API

The syntax manager API contains the interfaces used for the exposition
of the syntax object. This exposition allow to reuse object instances
(such as utilities, services or classes) under a variable name. The
main usage for this API is to permit to register Object as predefined
properties in languages script engine/shell (like Groovy, R ...).

### ISyntaxObject

A `ISyntaxObject` represents a Object with its variable name. This
Object can be exposed to other bundle after being registered into a
`ISyntaxProvider`.

### ISyntaxProvider

Container grouping a collection of ISyntaxObject which can be register
into a ISyntaxProviderManager in order to expose the ISyntaxObject in
other bundles.


### ISyntaxProviderManager

The main usage is to give to languages script engine/shell
(like Groovy, R ...) predefined properties to access easily to bundle
classes/utils/services