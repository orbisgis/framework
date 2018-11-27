package org.orbisgis.syntaxmanager;

import org.orbisgis.syntaxmanagerapi.ISyntaxProvider;
import org.orbisgis.syntaxmanagerapi.ISyntaxProviderManager;
import org.osgi.service.component.annotations.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Implementation of ISyntaxProviderManager.
 *
 * @author Sylvain PALOMINOS (UBS 2018)
 * @author Erwan Bocher (CNRS)
 */
@Component(immediate = true)
public class SyntaxProviderManager implements ISyntaxProviderManager {

    List<ISyntaxProvider> syntaxProviderList = new ArrayList<>();

    @Override
    public void registerSyntaxProvider(ISyntaxProvider syntaxProvider){
        syntaxProviderList.add(syntaxProvider);
    }

    @Override
    public void unregisterSyntaxProvider(ISyntaxProvider syntaxProvider){
        syntaxProviderList.remove(syntaxProvider);
    }

    @Override
    public List<ISyntaxProvider> getSyntaxProviderList(){
        return syntaxProviderList;
    }
}
