package com.couchbase.workshop.dao;

import rx.Observable;

/**
 *
 * @author David Maier <david.maier at couchbase.com>
 */
public interface IAsyncDao {

    /**
     * To persist by getting the result asynchronously as Observable
     *
     * @return
     */
    public Observable persist();

    /**
     * To get the result asynchronously as Observable
     *
     * @return
     */
    public Observable get();

}
