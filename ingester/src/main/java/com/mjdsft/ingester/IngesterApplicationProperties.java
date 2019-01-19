package com.mjdsft.ingester;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 *
 */
@Component
@ConfigurationProperties("app")
public class IngesterApplicationProperties {

    @Getter @Setter
    private String applicationName;

    @Getter @Setter
    private String applicationDescription;

    /**
     * Answer my default instance
     */
    public IngesterApplicationProperties() {

        super();
    }


}
