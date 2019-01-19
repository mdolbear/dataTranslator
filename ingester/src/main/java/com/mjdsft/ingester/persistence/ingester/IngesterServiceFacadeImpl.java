package com.mjdsft.ingester.persistence.ingester;

import com.mjdsft.ingester.model.DataRun;
import com.mjdsft.ingester.model.DataRunState;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class IngesterServiceFacadeImpl implements IngesterServiceFacade {

    @Getter(AccessLevel.PRIVATE) @Setter(AccessLevel.PRIVATE)
    private IngesterRepository repository;

    /**
     * Answer my logger
     * @return Logger
     */
    private static Logger getLogger() {

        return log;
    }

    /**
     * Answer an instance of me for aRepository
     * @param aRepository MapperRepository
     */
    @Autowired
    public IngesterServiceFacadeImpl(IngesterRepository aRepository) {

        super();
        this.setRepository(aRepository);
    }

    /**
     * Create a data run and answer its identifier
     * @param aUserProfileId String
     * @param isStopOnFailure boolean
     * @return String
     */
    @Override
    public String createNewDataRun(@NonNull String aUserProfileId,
                                   boolean isStopOnFailure) {

        DataRun tempResult;

        tempResult = new DataRun(aUserProfileId, isStopOnFailure);
        this.getRepository().save(tempResult);

        return tempResult.getId();
    }

    /**
     * Delete a data run for anId
     * @param anId String
     *
     */
    @Override
    public void deleteDataRun(@NonNull String anId) {

        DataRun tempDataRun;

        tempDataRun = this.findDataRunById(anId);
        if (tempDataRun != null) {

            this.validateNotRunning(tempDataRun);
            this.getRepository().delete(tempDataRun);
        }

    }

    /**
     * Validate that data run is not running
     * @param aRun DataRun
     */
    private void validateNotRunning(DataRun aRun) {

        String tempMessage = "Cannot delete an executing data run. It must first be cancelled";
        if (aRun.isRunning()) {

            getLogger().error(tempMessage);
            throw new IllegalStateException(tempMessage);
        }

    }

    /**
     * Mark data run for cancellation. Note that this changes the persistent state
     * of the data run, but the thread must see this and stop itself
     * @param anId String
     */
    @Override
    public DataRun markForCancellation(@NonNull String anId) {

        DataRun tempResult = null;

        tempResult = this.findDataRunById(anId);
        if (tempResult != null) {

            tempResult.markAsCancelled();
            this.getRepository().save(tempResult);
        }

        return tempResult;
    }

    /**
     * Update data run from anotherDataRun
     * @param anotherDataRun DataRun
     */
    @Override
    public DataRun updateDataRun(@NonNull DataRun anotherDataRun) {

        DataRun tempResult = null;

        tempResult = this.findDataRunById(anotherDataRun.getId());
        if (tempResult != null) {

            tempResult.updateFrom(anotherDataRun);
            this.getRepository().save(tempResult);
        }

        return tempResult;
    }

    /**
     * Find DataRun by its id
     * @param anId String
     * @return DataRun
     */
    @Override
    public DataRun findDataRunById(@NonNull String anId) {

        DataRun           tempResult = null;
        Optional<DataRun> tempOpt;

        tempOpt = this.getRepository().findById(anId);
        tempResult = this.getDataRunIfPresent(tempOpt);

        return tempResult;

    }

    /**
     * Answer a data run if present
     * @param anOptional Optional
     * @return DataRun
     */
    private DataRun getDataRunIfPresent(Optional<DataRun> anOptional) {

        DataRun tempResult = null;

        if (anOptional.isPresent()) {

            tempResult = anOptional.get();
        }

        return tempResult;
    }

    /**
     * Find data runs by aProfileId
     * @param aUserProfileId String
     * @return List
     */
    @Override
    public List<DataRun> findDataRunsByUserProfileId(@NonNull String aUserProfileId) {

        return this.getRepository().findDataRunByUserProfileIdentifier(aUserProfileId);
    }

    /**
     * Find data runs by aProfileId
     * @param aUserProfileId String
     * @param aState DataRunState
     * @return List
     */
    @Override
    public List<DataRun> findDataRunsByUserProfileIdAndState(@NonNull String aUserProfileId,
                                                             @NonNull DataRunState aState) {

        return
            this.getRepository().findDataRunByUserProfileIdentifierAndRunState(aUserProfileId,
                                                                               aState);
    }

}
