package com.couchbase.workshop.dao;

import com.couchbase.client.java.AsyncBucket;
import com.couchbase.client.java.document.JsonDocument;
import com.couchbase.client.java.document.json.JsonArray;
import com.couchbase.client.java.document.json.JsonObject;
import com.couchbase.workshop.pojo.Company;
import com.couchbase.workshop.conn.BucketFactory;
import com.couchbase.workshop.pojo.User;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import rx.Observable;

/**
 * The Data Access Object which wraps a Company object
 *
 * @author David Maier <david.maier at couchbase.com>
 */
public class CompanyDao extends AJsonSerializable implements IAsyncDao {

    /**
     * Constants
     */
    public static final String TYPE = "company";
    public static final String PROP_TYPE = "type";
    public static final String PROP_ID = "id";
    public static final String PROP_NAME = "name";
    public static final String PROP_ADDRESS = "address";
    public static final String PROP_USERS = "users";

    /**
     * Logger
     */
    private static final Logger LOG = Logger.getLogger(CompanyDao.class.getName());

    /**
     * Bucket reference
     */
    private final AsyncBucket bucket = BucketFactory.getAsyncBucket();

    /**
     * Company reference
     */
    private final Company company;

    /**
     * The constructor of the DAO
     *
     * @param company
     */
    public CompanyDao(Company company) {
        this.company = company;
    }

    /**
     * Persist the Company and all the associated Users
     *
     * @return
     */
    @Override
    public Observable<Company> persist() {

        JsonDocument doc = toJson(this.company);

        //Update all users by using a bulk operation and then update the company
        //FYI: Optional error handling via try-catch for block #1
       
        return Observable
                .from(company.getUsers())
                .flatMap( u -> DAOFactory.createUserDao(u).persist())
                .lastOrDefault(null)
                .flatMap(u -> bucket.upsert(doc))
                .map(resultDoc -> (Company) fromJson(resultDoc));
    }

    /**
     * To get a company with all it's users
     *
     *
     * @return
     */
    @Override
    public Observable<Company> get() {

        //Get the company by the id
        String id = TYPE + "::" + company.getId();

        return bucket.get(id)
                .map(resultDoc -> (Company) fromJson(resultDoc))
                .flatMap(c -> Observable.from(c.getUsers())
                        .flatMap(user -> DAOFactory.createUserDao(user)
                                .get()
                                .doOnNext(
                                        u -> {
                                            user.setFirstName(u.getFirstName());
                                            user.setLastName(u.getLastName());
                                            user.setEmail(u.getEmail());
                                            user.setBirthDay(u.getBirthDay());
                                        }
                                )
                        )
                        .lastOrDefault(null)
                        .map(u -> c)
                );
    }

    /**
     * Returns the Json object from the given Company
     *
     * @param obj
     * @return
     */
    @Override
    protected JsonDocument toJson(Object obj) {

        Company tmpComp = (Company) obj;

        //Create an empty JSON document
        JsonObject json = JsonObject.empty();

        json.put(PROP_TYPE, TYPE);
        if (tmpComp.getId() != null) {
            json.put(PROP_ID, tmpComp.getId());
        }
        if (tmpComp.getName() != null) {
            json.put(PROP_NAME, tmpComp.getName());
        }
        if (tmpComp.getAddress() != null) {
            json.put(PROP_ADDRESS, tmpComp.getAddress());
        }

        List<User> users = tmpComp.getUsers();

        JsonArray userArray = JsonArray.create();

        for (User user : users) {

            userArray.add(UserDao.TYPE + "::" + user.getUid());
        }

        json.put(PROP_USERS, userArray);

        JsonDocument doc = JsonDocument.create(TYPE + "::" + tmpComp.getId(), json);

        return doc;
    }

    /**
     * Returns the company object from the existing JSON one.
     *
     * Does not yet resolve the User object references completely
     *
     * @param doc
     * @return
     */
    @Override
    protected Object fromJson(JsonDocument doc) {

        JsonObject json = doc.content();

        Company tmpComp = new Company(json.getString(PROP_ID));

        tmpComp.setName(json.getString(PROP_NAME));
        tmpComp.setAddress(json.getString(PROP_ADDRESS));

        List<User> users = new ArrayList<>();
        JsonArray userArr = json.getArray(PROP_USERS);

        for (int i = 0; i < userArr.size(); i++) {

            String userKey = userArr.getString(i);
            String uid = userKey.split(UserDao.TYPE + "::")[1];

            //Don't fill the users yet
            users.add(new User(uid));
        }

        tmpComp.setUsers(users);

        return tmpComp;
    }

}
