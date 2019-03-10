package com.mjdsft.k8provision.services;

import com.mjdsft.k8provision.jobs.LogCurrentServicesJob;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.quartz.*;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.time.ZonedDateTime;
import java.util.UUID;

@Service
@Slf4j
public class K8sJobSchedulerServiceImpl implements K8sJobSchedulerService {



    @Getter(AccessLevel.PRIVATE)
    @Autowired
    private Scheduler scheduler;

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
     */
    public K8sJobSchedulerServiceImpl() {

        super();
    }

    /**
     * Create and schedule job for namespace scan
     * @param aNamespace String
     * @param isInsideCluster boolean
     */
    @Override
    public void createAndScheduleJobForNamespaceScan(String aNamespace,
                                                     boolean isInsideCluster) {

        ZonedDateTime   tempTime = ZonedDateTime.now();
        JobDetail       tempDetail;
        Trigger         tempTrigger;

        try {

            tempDetail = this.createJobDetailFor(aNamespace, isInsideCluster);
            tempTrigger = this.createTriggerFor(tempDetail, tempTime);

            this.getScheduler().scheduleJob(tempDetail, tempTrigger);

        }
        catch (SchedulerException e) {

            getLogger().error("Failed to schedule job", e);
            throw new RuntimeException(e);
        }

    }

    /**
     * Create job detail for aNamespace and isInsideCluster
     * @param aNamespace String
     * @param isInsideCluster boolean
     * @return JobDetail
     */
    private JobDetail createJobDetailFor(String aNamespace,
                                         boolean isInsideCluster) {

        JobDataMap tempMap = new JobDataMap();

        tempMap.put("namespace", aNamespace);
        tempMap.put("isInsideCluster", new Boolean(isInsideCluster));

        return JobBuilder.newJob(LogCurrentServicesJob.class)
                         .withIdentity(UUID.randomUUID().toString(), "logCurrentServicesJob")
                         .withDescription("Log Current pods for a Namespace")
                         .usingJobData(tempMap)
                         .storeDurably()
                         .build();

    }

    /**
     * Create trigger for job detail
     * @param aDetail JobDetail
     * @param aStartTime ZonedDataTime
     * @return Trigger
     */
    private Trigger createTriggerFor(JobDetail aDetail,
                                     ZonedDateTime aStartTime) {

        return TriggerBuilder.newTrigger()
                             .forJob(aDetail)
                             .withIdentity(aDetail.getKey().getName())
                             .withDescription("Log Current pods for a Namespace Trigger")
                             .startAt(Date.from(aStartTime.toInstant()))
                             .withSchedule(SimpleScheduleBuilder.repeatSecondlyForever(30))
                             .build();

    }


}
