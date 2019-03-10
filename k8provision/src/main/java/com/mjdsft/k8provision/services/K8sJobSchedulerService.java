package com.mjdsft.k8provision.services;

public interface K8sJobSchedulerService {

    /**
     * Create and schedule periodic job for
     * @param aNamespace String
     * @param isInsideCluster boolean
     */
    void createAndScheduleJobForNamespaceScan(String aNamespace,
                                              boolean isInsideCluster);

}
