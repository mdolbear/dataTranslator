package com.mjdsft.ingester.persistence.ingester;


import com.mjdsft.ingester.model.DataRun;
import com.mjdsft.ingester.model.DataRunState;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface IngesterRepository extends MongoRepository<DataRun, String> {

    /**
     * Answer a list of data runs by user profile id
     * @param aUserProfileId String
     * @return List
     */
    public List<DataRun> findDataRunByUserProfileIdentifier(String aUserProfileId);

    /**
     * Answer a list of data runs by user profile id and run state
     * @param aProfileId String
     * @param aRunState DataRunState
     * @return List
     */
    public List<DataRun> findDataRunByUserProfileIdentifierAndRunState(String aProfileId,
                                                                       DataRunState aRunState);
}
