package com.mjdsft.ingester.model;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Document(collection="ingesterCollection")
@ToString(of = {"id", "userProfileIdentifier", "creationDate", "runState"})
public class DataRun {

    @Getter() @Setter()
    @Id
    private String id;

    @Getter() @Setter(AccessLevel.PRIVATE)
    @Indexed
    private String userProfileIdentifier;

    @Getter() @Setter(AccessLevel.PRIVATE)
    private Date creationDate;

    @Getter() @Setter(AccessLevel.PRIVATE)
    private DataRunState runState;

    @Getter() @Setter(AccessLevel.PRIVATE)
    private int numberOfSuccessfulRecordConversions;

    @Getter() @Setter(AccessLevel.PRIVATE)
    private int numberOfFailedRecordConversions;

    @Getter() @Setter(AccessLevel.PRIVATE)
    private List<ConversionRecordFailure> conversionFailures;

    @Getter() @Setter(AccessLevel.PRIVATE)
    private boolean stopOnFailure;

    /**
     * Answer a default instance
     */
    public DataRun() {

        super();
        this.setCreationDate(new Date());
        this.setConversionFailures(new ArrayList<ConversionRecordFailure>());
        this.setNumberOfFailedRecordConversions(0);
        this.setNumberOfSuccessfulRecordConversions(0);
        this.setRunState(DataRunState.NEW);
    }

    /**
     * Answer an instance for the arguments below
     * @param aUserProfileId String
     * @param isStopOnFailure booelan
     */
    public DataRun(String aUserProfileId,
                   boolean isStopOnFailure) {

        this();
        this.setUserProfileIdentifier(aUserProfileId);
        this.setStopOnFailure(isStopOnFailure);
    }

    /**
     * Add conversion failure aFailure
     * @param aFailure ConversionRecordFailure
     */
    public void addFailure(ConversionRecordFailure aFailure) {

        this.getConversionFailures().add(aFailure);
    }

    /**
     * Increment successful conversions
     * @return int
     */
    public int incrementNumberOfSuccessfulRecordConversions() {

        int tempValue;

        tempValue = this.getNumberOfSuccessfulRecordConversions();
        tempValue++;
        this.setNumberOfSuccessfulRecordConversions(tempValue);

        return tempValue;
    }

    /**
     * Increment successful conversions
     * @return int
     */
    public int incrementNumberOfFailedRecordConversions() {

        int tempValue;

        tempValue = this.getNumberOfFailedRecordConversions();
        tempValue++;
        this.setNumberOfFailedRecordConversions(tempValue);

        return tempValue;
    }

    /**
     * Mark as running
     */
    public void markAsRunning() {

        this.setRunState(DataRunState.RUNNING);
    }

    /**
     * Answer whether I am running
     * @return boolean
     */
    public boolean isRunning() {

        return this.getRunState() != null
                && this.getRunState().equals(DataRunState.RUNNING);
    }

    /**
     * Answer whether I am cancelled
     * @return boolean
     */
    public boolean isCancelled() {

        return this.getRunState() != null
                && this.getRunState().equals(DataRunState.CANCELLED);
    }

    /**
     * Mark as completed
     */
    public void markAsCompleted() {

        this.setRunState(DataRunState.COMPLETED);
    }

    /**
     * Mark as failed
     */
    public void markAsFailed() {

        this.setRunState(DataRunState.FAILED);
    }

    /**
     * Mark as cancelled
     */
    public void markAsCancelled() {

        this.setRunState(DataRunState.CANCELLED);
    }


    /**
     * Update myself from anotherDataRun
     * @param anotherDataRun DataRun
     */
    public void updateFrom(DataRun anotherDataRun) {

        this.setRunState(anotherDataRun.getRunState());
        this.setNumberOfSuccessfulRecordConversions(
                anotherDataRun.getNumberOfSuccessfulRecordConversions());
        this.setNumberOfFailedRecordConversions(
                anotherDataRun.getNumberOfFailedRecordConversions());
        this.setConversionFailures(anotherDataRun.getConversionFailures());

    }



}
