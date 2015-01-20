package com.couchbase.workshop.main;

import com.couchbase.client.java.AsyncBucket;
import com.couchbase.workshop.pojo.User;
import com.couchbase.workshop.conn.BucketFactory;
import com.couchbase.workshop.dao.CompanyDao;
import com.couchbase.workshop.dao.DAOFactory;
import com.couchbase.workshop.pojo.Company;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import rx.Observable;

/**
 *
 * @author David Maier <david.maier at couchbase.com>
 */
public class Main {

    public static final Logger LOG = Logger.getLogger(Main.class.getName());

    /**
     * Main entry point of the application
     * 
     * @param args
     * @throws Exception 
     */
    public static void main(String[] args) throws Exception {
        
        demoConnect();
        demoCreateUsers();
        demoCreateCompany();
        demoAddUserToComp();
        demoGetComp();

        //Wait because the results are returned async.
        Thread.sleep(60000);
    }

    /**
     * (1) Establishes a connection by creating an async bucket via the 
     * previously implemented BucketFactory
     * (2) Logs out the bucket name
     */
    private static void demoConnect()
    {
        AsyncBucket bucket = BucketFactory.getAsyncBucket();
        LOG.log(Level.INFO, "bucket = {0}", bucket.name());
    }
    
    /**
     * (1) Creates a list of Users
     * (2) Creates a new Observable from the User list
     * (3) Transforms the User to a UserDAO
     * (4) Persists all users by using the UserDAO
     * (5) Logs the result or the error (when an exception occourred)
     * 
     */
    private static void demoCreateUsers() {
        LOG.info("DEMO - Create user");

        //Create a user object
        List<User> users = new ArrayList<>();

        User dmaier = new User("dmaier", "David", "Maier", "david.maier@couchbase.com", new Date());
        User mmustermann = new User("mmustermann", "Max", "Mustermann", "max.mustermann@mm.de", new Date());

        users.add(dmaier);
        users.add(mmustermann);
        //...

        
        Observable.from(users)
                .map(
                    // Same as: u -> DAOFactory.createUserDao(u)
                    DAOFactory::createUserDao
                )
                .flatMap(
          
                  u -> u.persist()
                    
                )
                .subscribe(
                    (u -> LOG.log(Level.INFO, "Wrote user {0}", u.getUid())),
                    (e -> LOG.log(Level.SEVERE, "Could not write the user!: {0}", e.toString()))
                );
                
                /* FYI: As blocking variant
                .doOnNext(u -> LOG.log(Level.INFO, "Wrote user {0}", u.getUid()))
                .doOnError(e -> LOG.log(Level.SEVERE, "Could not write the user!: {0}", e.toString()))
                .toBlocking()
                .last();
                */
                   
    }

    /**
     * (1) Persists a company by using a CompanyDAO
     * (2) Logs the result or the error (when an exception occourred)
     */
    private static void demoCreateCompany() {
        LOG.info("DEMO - Create company");

        Company comp = new Company("couchbase", "Couchbase Ltd.", "Couchbase Ltd. Address");
        DAOFactory.createCompanyDao(comp).persist().subscribe(
                (c -> LOG.log(Level.INFO, "Wrote company {0}", c.getId())),
                (e -> LOG.log(Level.SEVERE, "Could not write the company!: {0}", e.toString()))
        );

    }

    /**
     * (1) Gets a company by using a CompanyDAO
     * (2) Then adds some users to the returned company
     * (3) Persists the modified company by using a CompanyDAO
     * (4) Logs the result or the error (when an exception occoured)
     */
    private static void demoAddUserToComp() {
        LOG.info("DEMO - Add user to company");

        Company comp = new Company("couchbase");
        CompanyDao compDao = DAOFactory.createCompanyDao(comp);

        compDao.get()
                .map(
                        c -> {
                            
                            List<User> users = new ArrayList<>();
                            users.add(new User("dmaier", "David", "Maier", "david.new@couchbase.com", new Date()));
                            users.add(new User("mnitschinger", "Michael", "Nitschinger", "michael.nitschinger@couchbase.com", new Date()));
                            c.setUsers(users);

                            return c;
                        })
                .flatMap(
                       c -> DAOFactory.createCompanyDao(c).persist())
                .subscribe(
                        c -> LOG.log(Level.INFO, "Wrote company {0}", c.getId()),
                        e -> LOG.log(Level.SEVERE, "Could not write the company!: {0}", e.toString())
                );
    }
    
    private static void demoGetComp()
    {
        Company cb = new Company("couchbase");
        DAOFactory.createCompanyDao(cb).get().subscribe(
        
                 c -> LOG.log(Level.INFO, "Got company {0}", c.getName()),
                 e -> LOG.log(Level.SEVERE, "Could not get the company!: {0}", e.toString())
        );
        
    }
}
