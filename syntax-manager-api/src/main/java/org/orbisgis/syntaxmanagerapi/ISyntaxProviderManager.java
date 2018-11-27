package org.orbisgis.syntaxmanagerapi;

/**
 * Manages the ISyntaxProvider in order to make the available to all the other bundles.
 *
 * The main usage is to give to language consoles (like Groovy console) predefined properties to access easily to
 * bundle classes/utils/services
 *
 * @author Sylvain PALOMINOS (UBS 2018)
 * @author Erwan Bocher (CNRS)
 */
public interface ISyntaxProviderManager {

    /**
     * Register a ISyntaxProvider.
     *
     * @param syntaxProvider ISyntaxProvider to register.
     */
    void registerSyntaxProvider(ISyntaxProvider syntaxProvider);


    /**
     * Unregister a ISyntaxProvider.
     *
     * @param syntaxProvider ISyntaxProvider to unregister.
     */
    void unregisterSyntaxProvider(ISyntaxProvider syntaxProvider);
}
