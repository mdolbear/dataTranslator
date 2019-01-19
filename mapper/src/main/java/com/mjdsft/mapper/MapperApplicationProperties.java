package com.mjdsft.mapper;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 *
 */
@Component
@ConfigurationProperties("app")
public class MapperApplicationProperties {

    @Getter @Setter
    private String applicationName;

    @Getter @Setter
    private String applicationDescription;

    /**
     * Answer my default instance
     */
    public MapperApplicationProperties() {

        super();
    }


}
