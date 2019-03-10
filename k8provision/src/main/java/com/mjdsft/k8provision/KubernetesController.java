package com.mjdsft.k8provision;

import com.mjdsft.k8provision.services.K8sJobSchedulerService;
import com.mjdsft.k8provision.services.KubernetesService;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/k8provision")
@Slf4j
public class KubernetesController {

    @Getter(AccessLevel.PRIVATE)
    @Autowired
    private KubernetesService kubernetesService;

    @Getter(AccessLevel.PRIVATE)
    @Autowired
    private K8sJobSchedulerService jobSchedulerService;

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
    public KubernetesController() {

        super();
    }

    /**
     * Schedule scanner job for aNamespace and boolean isInsideCluster
     * @param aNamespace String
     * @param @param isInsideCluster boolean
     * @return ResponseEntity
     */
    @PostMapping("/scheduleNamespaceScan")
    public ResponseEntity<Boolean> scheduleNamespaceScan(@RequestParam("namespace") String aNamespace,
                                                         @RequestParam("insideCluster") boolean isInsideCluster) {

        this.getJobSchedulerService().createAndScheduleJobForNamespaceScan(aNamespace,isInsideCluster);

        return this.produceSuccessfulResponseState(new Boolean(true));

    }


    /**
     * Scale the application in aNamespace with aDeploymentName to aNewNumberOfPods. Define whether I am
     * isInsideCluster or not
     * @param aNamespace String
     * @param aDeploymentName String
     * @param aNewNumberOfPods int
     * @param isInsideCluster boolean
     */
    @PostMapping("/scale")
    public ResponseEntity<Boolean> scaleApplication(@RequestParam("namespace") String aNamespace,
                                                    @RequestParam("deployment") String aDeploymentName,
                                                    @RequestParam("numpods") int aNewNumberOfPods,
                                                    @RequestParam("insideCluster") boolean isInsideCluster) {

        this.getKubernetesService().scaleApplication(aNamespace,
                                                     aDeploymentName,
                                                     aNewNumberOfPods,
                                                     isInsideCluster);

        return this.produceSuccessfulResponseState(new Boolean(true));
    }


    /**
     * Answer a successful response for HttpStatus
     * @param aSuccess Boolean
     * @return RepositoryEntity
     */
    private ResponseEntity<Boolean> produceSuccessfulResponseState(Boolean aSuccess) {

        return new ResponseEntity(aSuccess, HttpStatus.OK);
    }

}
