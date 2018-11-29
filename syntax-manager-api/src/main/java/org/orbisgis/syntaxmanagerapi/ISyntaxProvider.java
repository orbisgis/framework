package org.orbisgis.syntaxmanagerapi;

import java.util.Collection;

/**
 * Container grouping a collection of ISyntaxObject which can be register into a ISyntaxProviderManager in order to
 * expose the ISyntaxObject in other bundles.
 *
 * The main usage is to give to language consoles (like Groovy console) predefined properties to access easily to
 * bundle classes/utils/services
 *
 * @author Sylvain PALOMINOS (UBS 2018)
 * @author Erwan Bocher (CNRS)
 */
public interface ISyntaxProvider {

    /**
     * Add a ISyntaxObject.
     *
     * @param syntaxObject ISyntaxObject to add.
     */
    void add(ISyntaxObject syntaxObject);

    /**
     * Remove a ISyntaxObject.
     *
     * @param syntaxObject ISyntaxObject to remove.
     */
    void remove(ISyntaxObject syntaxObject);

    /**
     * Return the Collection of ISyntaxObject.
     *
     * @return The collection of ISyntaxObject.
     */
    Collection<ISyntaxObject> getISyntaxObjectCollection();

    /**
     * Return the name of the ISyntaxProvider.
     *
     * @return The name of the ISyntaxProvider.
     */
    String getName();
}
