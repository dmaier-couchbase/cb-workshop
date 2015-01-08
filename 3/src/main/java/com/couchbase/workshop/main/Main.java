
package com.couchbase.workshop.main;

import com.couchbase.client.java.AsyncBucket;
import com.couchbase.workshop.conn.BucketFactory;

/**
 *
 * @author David Maier <david.maier at couchbase.com>
 */
public class Main {
    
    public static void main(String[] args) throws Exception
    {
        
        AsyncBucket bucket = BucketFactory.getAsyncBucket();
        
        System.out.println("bucket = " + bucket.name());
    }
}
