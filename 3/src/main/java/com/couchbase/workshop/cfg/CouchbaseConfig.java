/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.couchbase.workshop.cfg;

import java.io.IOException;

/**
 *
 * @author David Maier <david.maier at couchbase.com>
 */
public class CouchbaseConfig extends BaseConfig {

    private static final String FILE_NAME = "cb.properties";
    
    private String[] hosts;
    private int port;
    private String bucket;
    private String password;
    
    
    public CouchbaseConfig() throws IOException {
        super(FILE_NAME);
    }

    public String[] getHosts() {
        
        String propsStr = this.props.getProperty("cb.con.hosts");
        this.hosts = propsStr.split(",");
        return this.hosts;
    }
    
    public int getPort() {
        String portStr = this.props.getProperty("cb.con.port");
        this.port = Integer.parseInt(portStr);
        return this.port;
    }
    
    public String getBucket() {
    
        this.bucket = props.getProperty("cb.con.bucket.name");
        return this.bucket;
    }
    
    public String getPassword() {
        return this.props.getProperty("cb.con.bucket.pwd");
    }
    
}
