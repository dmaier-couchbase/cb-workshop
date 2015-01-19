package com.couchbase.workshop.dao;

import com.couchbase.client.java.AsyncBucket;
import com.couchbase.client.java.document.JsonDocument;
import com.couchbase.client.java.document.json.JsonObject;
import com.couchbase.workshop.pojo.User;
import com.couchbase.workshop.conn.BucketFactory;
import java.util.Date;
import rx.Observable;
//import rx.functions.Action1;
//import rx.functions.Func1;

/**
 * The Data Access Object which wraps a User object
 *
 * @author David Maier <david.maier at couchbase.com>
 */
public class UserDao extends AJsonSerializable implements IAsyncDao {

    /**
     * Constants
     */
    public static final String TYPE = "user";
    public static final String PROP_TYPE = "type";
    public static final String PROP_UID = "uid";
    public static final String PROP_FIRSTNAME = "firstname";
    public static final String PROP_LASTNAME = "lastname";
    public static final String PROP_EMAIL = "email";
    public static final String PROP_BDAY = "bday";

    /**
     * Bucket reference
     */
    private final AsyncBucket bucket = BucketFactory.getAsyncBucket();

    /**
     * Inner object
     */
    private final User user;

    /**
     * The constructor of the DAO
     *
     * @param user
     */
    public UserDao(User user) {
        this.user = user;
    }

    /**
     * To persist an user object
     *
     * The lamda expression could be also realized an anonymous implementation
     * of Func1.
     *
     * return bucket.upsert(doc) .map(
     *
     * new Func1<JsonDocument, User>() {
     *
     * public User call(JsonDocument resultDoc) {
     *
     * return fromJson(resultDoc);
     *
     * }
     * }
     * );
     */
    @Override
    public Observable<User> persist() {

        JsonDocument doc = toJson(this.user);

        return bucket.upsert(doc).map((JsonDocument resultDoc) -> (User) fromJson(resultDoc));
    }

    /**
     * To get the user object
     * 
     * @return 
     */
    @Override
    public Observable<User> get() {

        String key = TYPE + "::" + user.getUid();

        return bucket.get(key)
                .map((JsonDocument resultDoc) -> (User) fromJson(resultDoc));

        //Optionally handle errors here by using on of the onError* functions
        //https://github.com/ReactiveX/RxJava/wiki/Error-Handling-Operators
        //If an error occours it will be thrown anyway and then can be handled
        //finally in the subscribe
    }

    @Override
    protected JsonDocument toJson(Object obj) {

        User tmpUser = (User) obj;

        //Create an empty JSON document
        JsonObject json = JsonObject.empty();

        json.put(PROP_TYPE, TYPE);
        if (tmpUser.getUid() != null) {
            json.put(PROP_UID, tmpUser.getUid());
        }
        if (tmpUser.getFirstName() != null) {
            json.put(PROP_FIRSTNAME, tmpUser.getFirstName());
        }
        if (tmpUser.getLastName() != null) {
            json.put(PROP_LASTNAME, tmpUser.getLastName());
        }
        if (tmpUser.getEmail() != null) {
            json.put(PROP_EMAIL, tmpUser.getEmail());
        }
        if (tmpUser.getBirthDay() != null) {
            json.put(PROP_BDAY, tmpUser.getBirthDay().getTime());
        }

        JsonDocument doc = JsonDocument.create(TYPE + "::" + tmpUser.getUid(), json);

        return doc;
    }

    @Override
    protected Object fromJson(JsonDocument doc) {

        JsonObject json = doc.content();

        User tmpUser = new User(json.getString(PROP_UID));

        tmpUser.setFirstName(json.getString(PROP_FIRSTNAME));
        tmpUser.setLastName(json.getString(PROP_LASTNAME));
        tmpUser.setEmail(json.getString(PROP_EMAIL));

        if (json.containsKey(PROP_BDAY)) {
            tmpUser.setBirthDay(new Date(json.getLong(PROP_BDAY)));
        }

        return tmpUser;
    }

}
