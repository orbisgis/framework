# Bundle Manager

Implementation of the `bundle-manager-api` module.
`ISyntaxObject` exposed :
 - `BundleUtils` instance under the name `bundle`

### BundleItem

A `IBundleItem` is the representation of an OSGi bundle.

### BundleUtils

This class is used as an utility which give the mecanism to manage the
bundle lifecicle : repository managing, resolition, installation and
running. It is exposed as an OSGi component.

### BundleSyntaxProvider

This class implements the `ISyntaxProvider` interface and expose :
 - `BundleUtils` instance under the name `bundle`