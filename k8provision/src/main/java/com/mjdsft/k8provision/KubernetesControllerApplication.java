package com.mjdsft.k8provision;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 *
 *
 */
@SpringBootApplication
public class KubernetesControllerApplication {

    /**
     * Answer an instance for the following arguments
     */
    public KubernetesControllerApplication() {
        super();
    }

    /**
     * @param args
     */
    public static void main(String[] args) {

        SpringApplication.run(KubernetesControllerApplication.class, args);

    }

}
