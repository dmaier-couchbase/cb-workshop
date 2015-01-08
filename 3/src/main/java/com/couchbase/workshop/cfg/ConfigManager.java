
package com.couchbase.workshop.cfg;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * To access the configurations
 * 
 * @author David Maier <david.maier at couchbase.com>
 */
public class ConfigManager {
    
    
    private static CouchbaseConfig cbConfig;    
    private static Logger LOG = Logger.getLogger(ConfigManager.class.getName());
    
    
    public static CouchbaseConfig getCBConfig()
    {
        if (cbConfig == null)
            try {
                cbConfig = new CouchbaseConfig();
        } catch (IOException ex) {
            LOG.severe("Could not load the Couchbase configuration");
        }
        
        return cbConfig;
    }
    
    
}
