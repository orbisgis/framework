package org.orbisgis.syntaxmanager;

import org.orbisgis.syntaxmanagerapi.ISyntaxObject;
import org.orbisgis.syntaxmanagerapi.ISyntaxProvider;
import org.orbisgis.syntaxmanagerapi.ISyntaxProviderManager;
import org.osgi.service.component.annotations.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * Implementation of ISyntaxProviderManager.
 *
 * @author Sylvain PALOMINOS (UBS 2018)
 * @author Erwan Bocher (CNRS)
 */
@Component(immediate = true, service = {ISyntaxProviderManager.class})
public class SyntaxProviderManager implements ISyntaxProviderManager {

    private Logger LOGGER = LoggerFactory.getLogger(SyntaxProvider.class);

    /** List of ISyntaxProvider */
    private List<ISyntaxProvider> syntaxProviderList = new ArrayList<>();

    @Activate
    public void activate(){
        ISyntaxProvider syntaxProvider = new SyntaxProvider("Syntax Manager SyntaxProvider");
        ISyntaxObject syntaxObject = new SyntaxObject("syntaxManager", this);
        syntaxProvider.addSyntaxObject(syntaxObject);
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
}
