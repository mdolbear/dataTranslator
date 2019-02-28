package com.mjdsft.ingester.transform;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.Synchronized;
import org.reactivestreams.Subscriber;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

/**
 * I cache active subscribers that are subscribers to a given in-memory flux. They are accessed
 * by data run id.
 */
public class SubscriberCache {

    private final Object accessLock = new Object();

    @Getter(AccessLevel.PRIVATE) @Setter(AccessLevel.PRIVATE)
    private Map<String, Subscriber<Path>> subscribers;
    
    //Constants
    private static final int NUMBER_FOR_OCCUPIED = 0;

    /**
     * Answer a default instance
     */
    public SubscriberCache() {

        super();
        this.setSubscribers(new HashMap<String, Subscriber<Path>>());
    }

    /**
     * Add aSubscriber for anId
     * @param anId String
     * @param aSubscriber Subscriber
     */
    @Synchronized("accessLock")
    public void addSubscriber(String anId, Subscriber<Path> aSubscriber) {

        this.getSubscribers().put(anId, aSubscriber);
    }

    /**
     * Answer the subscriber for anId or null if it doesn't exist
     * @param anId String
     */
    @Synchronized("accessLock")
    public Subscriber<Path> getSubscriber(String anId) {

        return this.getSubscribers().get(anId);
    }

    /**
     * Remove subscriber for anId
     * @param anId String
     */
    @Synchronized("accessLock")
    public void removeSubscriberForId(String anId) {

        this.getSubscribers().remove(anId);
    }

    /**
     * Answer whether or not I have any subscribers
     * @return boolean
     */
    @Synchronized("accessLock")
    public boolean isEmpty() {

        return this.getSubscribers().isEmpty();
    }
    
    /**
     * Answer whether or not I am considered occupied. This will be
     * some load in the cache that is considered a proper number of fluxes
     * per vm that is the optimum load.
     * @return boolean
     */
    @Synchronized("accessLock")
    public boolean isOccupied() {
        
        return this.getSubscribers().size() > 
                    NUMBER_FOR_OCCUPIED;
    }


}
