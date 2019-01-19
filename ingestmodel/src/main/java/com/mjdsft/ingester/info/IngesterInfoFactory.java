package com.mjdsft.ingester.info;

import com.mjdsft.ingester.model.DataRun;

public class IngesterInfoFactory {

    /**
     * Answer a default instance
     */
    public IngesterInfoFactory() {

        super();
    }


    /**
     * Answer aDataRun as an info
     * @param aDataRun DataRun
     * @return DataRunInfo
     */
    public DataRunInfo asInfo(DataRun aDataRun) {

        return new DataRunInfo(aDataRun.getId(),
                               aDataRun.getUserProfileIdentifier(),
                               aDataRun.getCreationDate(),
                               aDataRun.getRunState(),
                               aDataRun.getNumberOfSuccessfulRecordConversions(),
                               aDataRun.getNumberOfFailedRecordConversions(),
                               aDataRun.isStopOnFailure());

    }
}
