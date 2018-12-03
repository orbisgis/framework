# Syntax Manager
Implementation of the `syntax-manager-api` module.
`ISyntaxObject` exposed :
 - `SyntaxProviderManager` instance under the name `syntaxManager`

### SyntaxProviderManager

Implementation of `ISyntaxProviderManager` as an OSGi service which
auto register itself as a `ISyntaxObject` under the `syntaxManager`
variable name.