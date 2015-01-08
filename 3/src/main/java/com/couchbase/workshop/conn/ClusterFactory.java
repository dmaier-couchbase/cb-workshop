
package com.couchbase.workshop.conn;

import com.couchbase.client.java.Cluster;
import com.couchbase.client.java.CouchbaseCluster;
import com.couchbase.workshop.cfg.ConfigManager;
import java.util.Arrays;
import java.util.List;

/**
 *
 * @author David Maier <david.maier at couchbase.com>
 */
public class ClusterFactory {
    
    private static Cluster cluster;
    
    
    public static Cluster getCluster()
    {
        if (cluster == null)
            createCluster();
       
        return cluster;
    }
    
    public static Cluster createCluster()
    {
        String[] hosts = ConfigManager.getCBConfig().getHosts();
        
        List<String> nodes = Arrays.asList(hosts);
      
        cluster = CouchbaseCluster.create(nodes); 
        
        return cluster;
    }
}
