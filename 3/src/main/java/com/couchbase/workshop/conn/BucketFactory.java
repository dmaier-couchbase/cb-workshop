
package com.couchbase.workshop.conn;

import com.couchbase.client.java.AsyncBucket;
import com.couchbase.client.java.Bucket;
import com.couchbase.client.java.Cluster;
import com.couchbase.workshop.cfg.ConfigManager;
import com.couchbase.workshop.cfg.CouchbaseConfig;

/**
 *
 * @author David Maier <david.maier at couchbase.com>
 */
public class BucketFactory {

    private static Bucket bucket;
    
    public static Bucket getBucket()
    {
    
        if (bucket == null)
            createBucketCon();
        
        return bucket;
    }
    
    public static AsyncBucket getAsyncBucket()
    {
    
        if (bucket == null)
            createBucketCon();
        
        return bucket.async();
    }
    
    
    public static Bucket createBucketCon()
    {

        CouchbaseConfig cfg = ConfigManager.getCBConfig();
        
        Cluster cluster = ClusterFactory.getCluster();
        bucket = cluster.openBucket(cfg.getBucket(), cfg.getPassword());
        
        return bucket;
    }
}
