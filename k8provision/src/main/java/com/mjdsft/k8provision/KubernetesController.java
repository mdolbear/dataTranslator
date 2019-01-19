package com.mjdsft.k8provision;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
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


    /**
     * Answer my logger
     *
     * @return org.slf4j.Logger
     */
    private static Logger getLogger() {
        return log;
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

        KubernetesUtilities tempUtils = new KubernetesUtilities(isInsideCluster);

        try {
            tempUtils.initializeClient();
            tempUtils.scaleDeployment(aNamespace, aDeploymentName, aNewNumberOfPods);
        }
        catch (Exception e) {

            this.logMessageAndThrowExceptionOnScaleFailure(e);

        }

        return this.produceSuccessfulResponseState(new Boolean(true));
    }

    /**
     * Log and throw exception on failed scale.
     * @param e ApiException
     * @return
     */
    private ResponseEntity<Boolean> logMessageAndThrowExceptionOnScaleFailure(Exception e) {

        String  tempMessage = "Failed to successful perform scale operation";

        getLogger().error(tempMessage, e);
        throw new RuntimeException(tempMessage, e);
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
