package com.mjdsft.k8provision.jobs;

import com.mjdsft.k8provision.info.ClusterInfo;
import com.mjdsft.k8provision.services.KubernetesService;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class LogCurrentServicesJob extends QuartzJobBean {

    @Getter(AccessLevel.PRIVATE)
    @Autowired
    private KubernetesService kubernetesService;


    /**
     * Answer my logger
     *
     * @return org.slf4j.Logger
     */
    protected static Logger getLogger() {
        return log;
    }


    /**
     * Perform execute internally
     * @param aJobExecutionContext JobExecutionContext
     * @throws JobExecutionException
     */
    @Override
    protected void executeInternal(JobExecutionContext aJobExecutionContext) throws JobExecutionException {

        String      tempNamespace;
        Boolean     tempIsInsideCluster;
        ClusterInfo tempInfo;

        getLogger().info("Executing job for key {}",
                          aJobExecutionContext.getJobDetail().getKey());

        tempNamespace = this.getNamespaceFromDataMap(aJobExecutionContext);
        tempIsInsideCluster = this.getIsInsideClusterFromDataMap(aJobExecutionContext);

        tempInfo =
                this.getKubernetesService().getServicesForNamespace(tempNamespace,
                                                                    tempIsInsideCluster.booleanValue());

        getLogger().info("Results for current namespace scan {}",
                         tempInfo);

    }

    /**
     * Answer my namespace from a data map
     * @param aJobExecutionContext JobExecutionContext
     * @return String
     */
    private String getNamespaceFromDataMap(JobExecutionContext aJobExecutionContext) {

        JobDataMap  tempMap;
        String      tempNamespace;

        tempMap = aJobExecutionContext.getMergedJobDataMap();
        tempNamespace = tempMap.getString("namespace");

        return tempNamespace;
    }

    /**
     * Answer my isInsideCluster booleabn from a data map
     * @param aJobExecutionContext JobExecutionContext
     * @return Boolean
     */
    private Boolean getIsInsideClusterFromDataMap(JobExecutionContext aJobExecutionContext) {

        JobDataMap  tempMap;
        Boolean     tempIsInsideCluster;

        tempMap = aJobExecutionContext.getMergedJobDataMap();
        tempIsInsideCluster = tempMap.getBoolean("isInsideCluster");

        return tempIsInsideCluster;
    }

}
