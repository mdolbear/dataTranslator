package com.mjdsft.ingester.modelinterface;

/**
 * This interface defines the contract the the target model implements so it can be validated and reconciled
 */
public interface TargetObject {

    /**
     * Pre-validate. This is the first step to be invoked, and provides a hook to determine
     * that data looks good after conversion process.
     */
    public void preValidate();

    /**
     * This provides a reconciliation step for the target model. If there is dependent data in the model,
     * this can be used as a place to generate it
     */
    public void reconcile();

    /**
     * This does the full validation step after preValidate and reconciliation have been completed
     */
    public void validate();

    /**
     * Answer my string representation
     * @return String
     */
    public String toString();

}
