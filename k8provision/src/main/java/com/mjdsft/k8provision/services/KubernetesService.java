package com.mjdsft.k8provision.services;

import com.mjdsft.k8provision.info.ClusterInfo;
import lombok.NonNull;

public interface KubernetesService {

    /**
     * Scale application
     * @param aNamespace
     * @param aDeploymentName
     * @param aNewNumberOfPods
     * @param isInsideCluster
     */
    void scaleApplication(@NonNull String aNamespace,
                          @NonNull String aDeploymentName,
                          int aNewNumberOfPods,
                          boolean isInsideCluster);

    /**
     * Answer my services for aNameSpace
     * @param aNamespace String
     * @return ClusterInfo
     */
    public ClusterInfo getServicesForNamespace(@NonNull String aNamespace,
                                               boolean isInsideCluster);
}
