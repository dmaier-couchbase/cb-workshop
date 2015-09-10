 /*
  * Copyright 2015 Couchbase, Inc.
  *
  * Licensed under the Apache License, Version 2.0 (the "License");
  * you may not use this file except in compliance with the License.
  * You may obtain a copy of the License at
  *
  *      http://www.apache.org/licenses/LICENSE-2.0
  *
  * Unless required by applicable law or agreed to in writing, software
  * distributed under the License is distributed on an "AS IS" BASIS,
  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  * See the License for the specific language governing permissions and
  * limitations under the License.
  */

package com.couchbase.workshop.dao;

import com.couchbase.client.java.AsyncBucket;
import com.couchbase.client.java.document.JsonDocument;
import com.couchbase.client.java.document.json.JsonArray;
import com.couchbase.client.java.document.json.JsonObject;
import com.couchbase.client.java.view.ViewQuery;
import com.couchbase.workshop.pojo.User;
import com.couchbase.workshop.conn.BucketFactory;
import java.util.Date;
import java.util.logging.Logger;
import rx.Observable;
//import rx.functions.Action1;
//import rx.functions.Func1;

/**
 * The Data Access Object which wraps a User object
 *
 * @author David Maier <david.maier at couchbase.com>
 */
public class UserDao extends AJsonSerializable implements IAsyncDao {

    private static final Logger LOG = Logger.getLogger(UserDao.class.getName());
    
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

    public static final String DDOC_PERSONS = "persons";
    public static final String VIEW_BYBIRTHDAY = "by_birthday";
    
    /**
     * Bucket reference
     */
    private static final AsyncBucket bucket = BucketFactory.getAsyncBucket();

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

        return bucket.upsert(doc)
                     .map(resultDoc -> (User) fromJson(resultDoc));
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
                .map( resultDoc -> (User) fromJson(resultDoc));

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
    
    /**
     * A helper method to query for users by birthday
     * 
     * The following View needs to be defined:
     * 
     * function (doc, meta) {
     *
     *   if (doc.type == "user")
     *   {
     *       if ( typeof doc.bday != undefined)
     *       {
     *           emit(dateToArray(doc.bday), {"firstname" : doc.firstname, "lastname" : doc.lastname});  
     *       }
     *   }
     *
     *  }
     * 
     * @param from
     * @param to
     * @return 
     */
    public static Observable<User> queryByBirthDay(Date from, Date to)
    {
        //Prepare the query
        ViewQuery q = ViewQuery.from(DDOC_PERSONS, VIEW_BYBIRTHDAY).reduce(false);
        
        if (from != null)
            q = q.startKey(dateToDateArr(from));
     
        if (to != null)
            q = q.endKey(dateToDateArr(to));
        
        return  
              bucket.query(q)  
              .flatMap(r -> r.rows())
              .flatMap(row -> row.document())
              .map(d -> d.id())
              .map(i -> i.split("::")[1])
              .flatMap(i -> DAOFactory.createUserDao(i).get());             
    }

    /**
     * Helper to convert a Java date into a JSON date array
     * 
     * @param date 
     */
    private static JsonArray dateToDateArr(Date date)
    {
        JsonArray arr = JsonArray.empty();
        
        arr.add(date.getYear() + 1900);
        arr.add(date.getMonth() + 1);
        arr.add(date.getDate());
        arr.add(date.getHours());
        arr.add(date.getMinutes());
        arr.add(date.getSeconds());
        
        
        return arr;
    }
    
    /**
     * A helper method to query by name
     * 
     * The following index is required: CREATE PRIMARY INDEX ON workshop
     * 
     * @param name
     * @return 
     */
    /*
    public static Observable<String> queryByName(String name)
    {
        StringBuilder q = new StringBuilder("SELECT * FROM workshop");
        q.append(" WHERE ");
        q.append(PROP_LASTNAME);
        q.append(" = ");
        q.append("'");
        q.append(name);
        q.append("'");
               
        return bucket.query(q.toString())
              .flatMap(r -> r.rows())
              .map(row -> row.value())
              .map(v -> v.toString());
             
    }*/
}
