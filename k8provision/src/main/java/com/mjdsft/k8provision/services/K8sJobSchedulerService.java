package com.mjdsft.k8provision.services;

import com.mjdsft.k8provision.info.DeleteJobInfo;

public interface K8sJobSchedulerService {

    /**
     * Create and schedule periodic job for
     * @param aNamespace String
     * @param isInsideCluster boolean
     */
    void createAndScheduleJobForNamespaceScan(String aNamespace,
                                              boolean isInsideCluster);

    /**
     * Cancel and delete namespace scan job
     * @param aNamespace String
     * @return DeleteJobInfo
     */
    public DeleteJobInfo cancelAndDeleteNamespaceScanJob(String aNamespace);


}
