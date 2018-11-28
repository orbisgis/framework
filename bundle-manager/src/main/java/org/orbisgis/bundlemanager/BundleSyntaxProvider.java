package org.orbisgis.bundlemanager;

import org.orbisgis.bundlemanagerapi.IBundleUtils;
import org.orbisgis.syntaxmanager.SyntaxObject;
import org.orbisgis.syntaxmanager.SyntaxProvider;
import org.orbisgis.syntaxmanagerapi.ISyntaxObject;
import org.orbisgis.syntaxmanagerapi.ISyntaxProvider;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * Extension of the SyntaxProvider class which register the BundleUtils component under the name 'bundle'.
 *
 * @author Sylvain PALOMINOS (UBS 2018)
 * @author Erwan Bocher (CNRS)
 */
@Component(immediate = true, service = {ISyntaxProvider.class})
public class BundleSyntaxProvider extends SyntaxProvider {

    /**
     * Main constructor
     */
    public BundleSyntaxProvider() {
        super("BundleSyntaxProvider");
    }

    /**
     * Set the IBundleUtils in order to expose it as a SyntaxObject.
     *
     * @param bundleUtils IBundleUtils to expose it as a SyntaxObject and to register this ISyntaxProvider.
     */
    @Reference
    public void setIBundleUtils(IBundleUtils bundleUtils) {
        ISyntaxObject syntaxObject = new SyntaxObject("bundle", bundleUtils);
        this.addSyntaxObject(syntaxObject);
    }

    /**
     * Unset the IBundleUtils.
     *
     * @param bundleUtils IBundleUtils unset.
     */
    public void unsetIBundleUtils(IBundleUtils bundleUtils) {
        ISyntaxObject syntaxObject = null;
        for (ISyntaxObject so : getISyntaxObjectCollection()) {
            if (so.getObject().equals(bundleUtils)) {
                syntaxObject = so;
            }
        }
        if(syntaxObject != null) {
            this.removeSyntaxObject(syntaxObject);
        }
    }
}
