package com.mjdsft.ingester.output;

import com.mjdsft.ingester.modelinterface.TargetObject;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;

@Slf4j
public class LoggingTargetConnection implements TargetConnection {

    /**
     * Answer my logger
     * @return Logger
     */
    private static Logger getLogger() {

        return log;
    }

    /**
     * Answer a default instance
     */
    public LoggingTargetConnection() {

        super();

    }

    /**
     * Post aTargetObject to me
     *
     * @param aTargetObject
     */
    @Override
    public void postTargetObject(TargetObject aTargetObject) {

        getLogger().debug("Target object output: " + aTargetObject.toString());
    }

}
