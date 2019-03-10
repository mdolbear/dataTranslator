package com.mjdsft.k8provision.services;

import com.mjdsft.k8provision.KubernetesUtilities;
import com.mjdsft.k8provision.info.ClusterInfo;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class KubernetesServiceImpl implements KubernetesService {

    /**
     * Answer my logger
     *
     * @return org.slf4j.Logger
     */
    private static Logger getLogger() {
        return log;
    }


    /**
     * Answer a default instance
     *
     */
    public KubernetesServiceImpl() {

        super();
    }

    /**
     * Scale application using the following parameters
     * @param aNamespace String
     * @param aDeploymentName String
     * @param aNewNumberOfPods boolean
     * @param isInsideCluster boolean
     */
    @Override
    public void scaleApplication(@NonNull String aNamespace,
                                 @NonNull String aDeploymentName,
                                 int aNewNumberOfPods,
                                 boolean isInsideCluster) {

        KubernetesUtilities tempUtils = new KubernetesUtilities(isInsideCluster);

        try {
            tempUtils.initializeClient();
            tempUtils.scaleDeployment(aNamespace, aDeploymentName, aNewNumberOfPods);
        }
        catch (Exception e) {

            this.logMessageAndThrowExceptionOnFailure(e,
                                                     "Failed to successful perform scale operation");

        }

    }

    /**
     * Answer my services for aNameSpace
     * @param aNamespace String
     * @return ClusterInfo
     */
    @Override
    public ClusterInfo getServicesForNamespace(String aNamespace,
                                               boolean isInsideCluster) {

        KubernetesUtilities tempUtils = new KubernetesUtilities(isInsideCluster);
        ClusterInfo         tempInfo = null;

        try {
            tempUtils.initializeClient();
            tempInfo = new ClusterInfo(tempUtils.getServices(aNamespace).toString());

        }
        catch (Exception e) {

            this.logMessageAndThrowExceptionOnFailure(e,
                    "Failed to successful perform scale operation");

        }

        return tempInfo;

    }

    /**
     * Log and throw exception on failed scale.
     * @param aMessage String
     * @param e ApiException
     */
    private void logMessageAndThrowExceptionOnFailure(Exception e,
                                                      String aMessage) {

        getLogger().error(aMessage, e);
        throw new RuntimeException(aMessage, e);

    }


}
