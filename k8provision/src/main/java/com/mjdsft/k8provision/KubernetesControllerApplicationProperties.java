package com.mjdsft.k8provision;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 *
 */
@Component
@ConfigurationProperties("app")
public class KubernetesControllerApplicationProperties {

    @Getter @Setter
    private String applicationName;

    @Getter @Setter
    private String applicationDescription;

    /**
     * Answer my default instance
     */
    public KubernetesControllerApplicationProperties() {

        super();
    }


}
