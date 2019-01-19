package com.mjdsft.ingester.persistence.ingester;

import com.mjdsft.ingester.model.DataRun;
import com.mjdsft.ingester.model.DataRunState;
import lombok.NonNull;

import java.util.List;

public interface IngesterServiceFacade {

    /**
     * Create a data run and answer its identifier
     * @param aUserProfileId String
     * @param isStopOnFailure boolean
     * @return String
     */
    String createNewDataRun(@NonNull String aUserProfileId,
                            boolean isStopOnFailure);

    /**
     * Delete a data run for anId
     * @param anId String
     *
     */
    void deleteDataRun(@NonNull String anId);

    /**
     * Mark data run for cancellation. Note that this changes the persistent state
     * of the data run, but the thread must see this and stop itself
     * @param anId String
     */
    DataRun markForCancellation(@NonNull String anId);

    /**
     * Update data run from anotherDataRun
     * @param anotherDataRun DataRun
     */
    DataRun updateDataRun(@NonNull DataRun anotherDataRun);

    /**
     * Find DataRun by its id
     * @param anId String
     * @return DataRun
     */
    DataRun findDataRunById(@NonNull String anId);

    /**
     * Find data runs by aProfileId
     * @param aUserProfileId String
     * @return List
     */
    List<DataRun> findDataRunsByUserProfileId(@NonNull String aUserProfileId);

    /**
     * Find data runs by aProfileId
     * @param aUserProfileId String
     * @param aState DataRunState
     * @return List
     */
    List<DataRun> findDataRunsByUserProfileIdAndState(@NonNull String aUserProfileId,
                                                      @NonNull DataRunState aState);

}
