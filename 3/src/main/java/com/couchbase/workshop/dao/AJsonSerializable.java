package com.couchbase.workshop.dao;

import com.couchbase.client.java.document.JsonDocument;
import com.couchbase.workshop.pojo.User;

/**
 * Describes methods to serialize classes
 * 
 * @author David Maier <david.maier at couchbase.com>
 */
public abstract class AJsonSerializable {
    
    /**
     * Serialize to JSON
     * 
     * @param user
     * @return 
     */
    abstract protected JsonDocument toJson(Object obj);
    
    /**
     * De-serialize from JSON
     * 
     * @param doc 
     * @return  
     */
    abstract protected Object fromJson(JsonDocument doc);
    
}
