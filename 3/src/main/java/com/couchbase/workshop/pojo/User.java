package com.couchbase.workshop.pojo;

import java.util.Date;

/**
 * A simple User bean
 * 
 * @author David Maier <david.maier at couchbase.com>
 */
public class User {

    private String uid;
    private String firstName;
    private String lastName;
    private String email;
    private Date birthDay;

    /**
     * The constructor which only takes the uid
     * @param uid 
     */
    public User(String uid)
    {
        this.uid = uid;
    }
    
    /**
     * The full constructor 
     * 
     * @param uid
     * @param firstName
     * @param lastName
     * @param email
     * @param birthDay 
     */
    public User(String uid, String firstName, String lastName, String email, Date birthDay) {
        this.uid = uid;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.birthDay = birthDay;
    }

    //Getters
    public String getUid() {
        return uid;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getEmail() {
        return email;
    }

    public Date getBirthDay() {
        return birthDay;
    }
    
    //Setters
    /**
     * @param uid the uid to set
     */
    public void setUid(String uid) {
        this.uid = uid;
    }

    /**
     * @param firstName the firstName to set
     */
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    /**
     * @param lastName the lastName to set
     */
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    /**
     * @param email the email to set
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * @param birthDay the birthDay to set
     */
    public void setBirthDay(Date birthDay) {
        this.birthDay = birthDay;
    }
    
}
