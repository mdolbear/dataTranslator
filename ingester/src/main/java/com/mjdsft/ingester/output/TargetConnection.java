package com.mjdsft.ingester.output;

import com.mjdsft.ingester.modelinterface.TargetObject;

public interface TargetConnection {

    /**
     * Post aTargetObject to me
     * @param aTargetObject
     */
    public void postTargetObject(TargetObject aTargetObject);
}
