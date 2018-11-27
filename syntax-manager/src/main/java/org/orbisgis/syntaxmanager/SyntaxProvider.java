package org.orbisgis.syntaxmanager;

import org.orbisgis.syntaxmanagerapi.ISyntaxObject;
import org.orbisgis.syntaxmanagerapi.ISyntaxProvider;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Implementation of ISyntaxProvider.
 *
 * @author Sylvain PALOMINOS (UBS 2018)
 * @author Erwan Bocher (CNRS)
 */
public class SyntaxProvider implements ISyntaxProvider {

    Collection<ISyntaxObject> collection = new ArrayList<>();

    @Override
    public void addSyntaxObject(ISyntaxObject syntaxObject){
        collection.add(syntaxObject);
    }

    @Override
    public void removeSyntaxObject(ISyntaxObject syntaxObject){
        collection.remove(syntaxObject);
    }

    @Override
    public Collection<ISyntaxObject> getISyntaxObjectCollection(){
        return collection;
    }
}
