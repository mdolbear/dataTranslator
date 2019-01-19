package com.mjdsft.ingester.persistence.ingester;

import com.mjdsft.ingester.output.LoggingTargetConnection;
import com.mjdsft.ingester.output.TargetConnection;
import com.mjdsft.ingester.transform.SubscriberCache;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

@Configuration
public class IngesterConfiguration {

    /**
     * Create and inject a SubscriberCache. Needs to be a singleton so that all
     * services hold the same instance
     * @return SubscriberCache
     */
    @Scope("singleton")
    @Bean()
    public SubscriberCache createSubscriberCache() {

        return new SubscriberCache();
    }

    /**
     * Create target connection
     * @return TargetConnection
     */
    @Scope("prototype")
    @Bean()
    public TargetConnection createOutputConnection() {

        return new LoggingTargetConnection();

    }

}
