package org.orbisgis.syntaxmanager;

import org.orbisgis.syntaxmanagerapi.ISyntaxObject;
import org.orbisgis.syntaxmanagerapi.ISyntaxProvider;
import org.osgi.service.component.annotations.Component;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Implementation of ISyntaxProvider.
 *
 * @author Sylvain PALOMINOS (UBS 2018)
 * @author Erwan Bocher (CNRS)
 */
public class SyntaxProvider implements ISyntaxProvider {

    /** ISyntaxObject collection */
    private Collection<ISyntaxObject> collection = new ArrayList<>();
    /** Name of the SyntaxProvider */
    private String name;

    /**
     * Main constructor.
     *
     * @param name Name of the SyntaxProvider.
     */
    public SyntaxProvider(String name){
        this.name = name;
    }

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

    @Override
    public String getName(){
        return name;
    }
}
