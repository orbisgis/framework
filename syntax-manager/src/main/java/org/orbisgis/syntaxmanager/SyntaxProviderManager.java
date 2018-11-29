package org.orbisgis.syntaxmanager;

import org.orbisgis.syntaxmanagerapi.ISyntaxObject;
import org.orbisgis.syntaxmanagerapi.ISyntaxProvider;
import org.orbisgis.syntaxmanagerapi.ISyntaxProviderManager;
import org.osgi.service.component.annotations.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Implementation of ISyntaxProviderManager.
 *
 * @author Sylvain PALOMINOS (UBS 2018)
 * @author Erwan Bocher (CNRS)
 */
@Component(immediate = true, service = {ISyntaxProviderManager.class})
public class SyntaxProviderManager implements ISyntaxProviderManager, ISyntaxObject {

    /** Name of the SyntaxObject */
    private static final String NAME = "syntaxManager";
    private static final Logger LOGGER = LoggerFactory.getLogger(SyntaxProviderManager.class);

    /** List of ISyntaxProvider */
    private List<ISyntaxProvider> syntaxProviderList = new ArrayList<>();

    @Activate
    public void activate(){
        SyntaxProvider syntaxProvider = new SyntaxProvider();
        syntaxProvider.add(this);
        this.registerSyntaxProvider(syntaxProvider);
    }

    @Override
    @Reference(service = ISyntaxProvider.class, cardinality = ReferenceCardinality.MULTIPLE, policy = ReferencePolicy.DYNAMIC)
    public void registerSyntaxProvider(ISyntaxProvider syntaxProvider){
        syntaxProviderList.add(syntaxProvider);
        LOGGER.debug("SyntaxProvider '" + syntaxProvider.getName() + "' registered");
    }

    @Override
    public void unregisterSyntaxProvider(ISyntaxProvider syntaxProvider){
        syntaxProviderList.remove(syntaxProvider);
        LOGGER.debug("SyntaxProvider '" + syntaxProvider.getName() + "' unregistered");
    }

    @Override
    public List<ISyntaxProvider> getSyntaxProviderList(){
        return syntaxProviderList;
    }

    @Override
    public String getName() {
        return NAME;
    }

    private class SyntaxProvider implements ISyntaxProvider {

        private static final String NAME = "SyntaxManager Provider";
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
            return null;
        }
    }
}
