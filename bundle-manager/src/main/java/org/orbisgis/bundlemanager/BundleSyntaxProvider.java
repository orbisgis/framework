package org.orbisgis.bundlemanager;

import org.orbisgis.bundlemanagerapi.IBundleUtils;
import org.orbisgis.syntaxmanagerapi.ISyntaxObject;
import org.orbisgis.syntaxmanagerapi.ISyntaxProvider;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Extension of the SyntaxProvider class which register the BundleUtils component under the name 'bundle'.
 *
 * @author Sylvain PALOMINOS (UBS 2018)
 * @author Erwan Bocher (CNRS)
 */
@Component(immediate = true, service = {ISyntaxProvider.class})
public class BundleSyntaxProvider implements ISyntaxProvider {

    /** SyntaxProvider name */
    private static final String NAME = "BundleSyntaxProvider";

    private List<ISyntaxObject> syntaxObjectList = new ArrayList<>();

    @Override
    public void add(ISyntaxObject syntaxObject) {
        syntaxObjectList.add(syntaxObject);
    }

    @Override
    public void remove(ISyntaxObject syntaxObject) {
        syntaxObjectList.remove(syntaxObject);
    }

    @Override
    public Collection<ISyntaxObject> getISyntaxObjectCollection() {
        return syntaxObjectList;
    }

    @Override
    public String getName() {
        return NAME;
    }

    /**
     * Set the IBundleUtils in order to expose it as a SyntaxObject.
     *
     * @param bundleUtils IBundleUtils to expose it as a SyntaxObject and to register this ISyntaxProvider.
     */
    @Reference
    public void setIBundleUtils(IBundleUtils bundleUtils) {
        if(bundleUtils instanceof ISyntaxObject) {
            this.add((ISyntaxObject) bundleUtils);
        }
    }

    /**
     * Unset the IBundleUtils.
     *
     * @param bundleUtils IBundleUtils unset.
     */
    public void unsetIBundleUtils(IBundleUtils bundleUtils) {
        if(bundleUtils instanceof ISyntaxObject) {
            this.remove((ISyntaxObject) bundleUtils);
        }
    }
}
