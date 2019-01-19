package com.mjdsft.ingester;

import com.mjdsft.ingester.info.DataRunInfo;
import com.mjdsft.ingester.info.IngesterInfoFactory;
import com.mjdsft.ingester.model.DataRun;
import com.mjdsft.ingester.model.DataRunState;
import com.mjdsft.ingester.persistence.ingester.IngesterServiceFacade;
import com.mjdsft.ingester.persistence.mapper.MapperServiceFacade;
import com.mjdsft.ingester.transform.FluxRepository;
import com.mjdsft.mapper.model.DataTranslateDefinition;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/ingester")
@Slf4j
public class IngesterController {

    @Getter(AccessLevel.PRIVATE) @Setter(AccessLevel.PRIVATE)
    private IngesterServiceFacade ingesterService;

    @Getter(AccessLevel.PRIVATE) @Setter(AccessLevel.PRIVATE)
    private FluxRepository fluxRepository;

    @Getter(AccessLevel.PRIVATE) @Setter(AccessLevel.PRIVATE)
    private MapperServiceFacade mapperService;

    @Getter(AccessLevel.PRIVATE) @Setter(AccessLevel.PRIVATE)
    private IngesterInfoFactory infoFactory;

    /**
     * Answer my logger
     *
     * @return org.slf4j.Logger
     */
    protected static Logger getLogger() {
        return log;
    }

    /**
     * Answer an instance on the following arguments
     * @param aService IngesterServiceFacade
     * @param aRepository FluxRepository
     * @param aMapper  MapperServiceFacade
     */
    @Autowired
    public IngesterController(IngesterServiceFacade aService,
                              FluxRepository aRepository,
                              MapperServiceFacade aMapper) {

        super();
        this.setIngesterService(aService);
        this.setFluxRepository(aRepository);
        this.setMapperService(aMapper);
        this.setInfoFactory(new IngesterInfoFactory());

    }

    /**
     * Create and start a data run based on the arguments below
     * @param aProfileId String
     * @param aVersion int
     * @param aDirectoryPath String
     */
    @PostMapping("/create")
    public String createAndStartDataRun(@RequestParam("profileId") String aProfileId,
                                        @RequestParam("version") int aVersion,
                                        @RequestParam("directory") String aDirectoryPath,
                                        @RequestParam("stopOnFail") boolean isStopOnFailure) {

        DataTranslateDefinition tempDef;
        String                  tempDataRunId;

        tempDef =
                this.getMapperService().findDataTranslateDefinitionForProfileIdAndVersion(aProfileId,
                                                                                          aVersion);
        this.validateDataTranslateDefinitionExists(aProfileId, aVersion, tempDef);

        tempDataRunId = this.getIngesterService().createNewDataRun(aProfileId, isStopOnFailure);
        this.getFluxRepository().createDataIngester(aDirectoryPath, tempDataRunId, tempDef);

        return tempDataRunId;

    }

    /**
     * Delete data run for anId
     * @param anId String
     */
    @DeleteMapping("/delete")
    public void deleteDataRun(@RequestParam("id") String anId) {

        this.getIngesterService().deleteDataRun(anId);
    }

    /**
     * Mark for cancellation data run for anId
     * @param anId String
     */
    @PostMapping("/cancel")
    public ResponseEntity<String> markDataRunForCancellation(String anId) {

        DataRun tempRun;

        tempRun = this.getIngesterService().markForCancellation(anId);
        return this.produceDataRunCancellationMessage(tempRun);
    }

    /**
     * Find data runs by aProfileId
     * @param aUserProfileId String
     * @return List
     */
    @GetMapping("/dataRunsByProfileId")
    public List<DataRunInfo> findDataRunsByUserProfileId(@RequestParam("profileId")  String aUserProfileId) {

        List<DataRun>   tempRuns;

        tempRuns = this.getIngesterService().findDataRunsByUserProfileId(aUserProfileId);
        return this.asDataRunInfos(tempRuns);

    }

    /**
     * Find DataRun by its id
     * @param anId String
     * @return DataRunInfo
     */
    @GetMapping("/dataRunById")
    public DataRunInfo findDataRunById(@RequestParam("id") String anId) {

        DataRun tempRun;

        tempRun = this.getIngesterService().findDataRunById(anId);
        return this.asDataRunInfo(tempRun);

    }

    /**
     * Find data runs by aProfileId
     * @param aUserProfileId String
     * @return List
     */
    @GetMapping("/dataRunsByProfileIdAndState")
    public List<DataRunInfo> findDataRunsByUserProfileId(@RequestParam("profileId")  String aUserProfileId,
                                                         @RequestParam("runState") DataRunState aState) {

        List<DataRun>   tempRuns;

        tempRuns = this.getIngesterService().findDataRunsByUserProfileIdAndState(aUserProfileId, aState);
        return this.asDataRunInfos(tempRuns);

    }



    /**
     * Answer a successful response with a message
     * @return RepositoryEntity
     */
    private ResponseEntity<String> produceDataRunCancellationMessage(DataRun aRun) {

        String  tempMsg;

        if (aRun == null) {
            tempMsg = "No data run encountered for this id";
        }
        else {

            tempMsg = "Data run marked for cancellation";
        }

        return new ResponseEntity(tempMsg, HttpStatus.OK);
    }


    /**
     * Validate data trnalsate definition exists for aProfileId and aVersion
     * @param aProfileId String
     * @param aVersion int
     * @param aDef DataTranslateDefinition
     */
    private void validateDataTranslateDefinitionExists(String aProfileId,
                                                       int aVersion,
                                                       DataTranslateDefinition aDef) {


        if (aDef == null) {

            this.throwExceptionForDataTranlateDefinitionDoesNotExist(aProfileId, aVersion);
        }

    }

    /**
     * Throw exception for data translate definition does not exist
     * @param aProfileId String
     * @param aVersion int
     */
    private void throwExceptionForDataTranlateDefinitionDoesNotExist(String aProfileId,
                                                                     int aVersion) {

        String          tempMsg;

        tempMsg = this.createDataTranslateDoesNotExistErrorMessage(aProfileId, aVersion);
        getLogger().error(tempMsg);
        throw new IllegalStateException(tempMsg);
    }

    /**
     * Create data translate definition does not exist error message
     * @param aProfileId String
     * @param aVersion int
     * @return String
     */
    private String createDataTranslateDoesNotExistErrorMessage(String aProfileId,
                                                               int aVersion) {

        String          tempMsg = "DataTranslateDefinition does not exist for profileId: ";
        StringBuilder   tempBuilder = new StringBuilder();

        tempBuilder.append(tempMsg);
        tempBuilder.append((aProfileId != null) ? aProfileId : "null");
        tempBuilder.append(" and version: ");
        tempBuilder.append(aVersion);

        return tempBuilder.toString();

    }


    /**
     * Answer a list of data run infos for aDataRuns
     * @param aDataRuns List
     * @return List
     */
    private List<DataRunInfo> asDataRunInfos(List<DataRun> aDataRuns) {

        return aDataRuns.stream()
                        .map((aDataRun) -> this.asDataRunInfo(aDataRun))
                        .collect(Collectors.toList());

    }


    /**
     * Answer aDataRun as an info
     * @param aDataRun DataRun
     * @return DataRunInfo
     */
    private DataRunInfo asDataRunInfo(DataRun aDataRun) {

        DataRunInfo tempInfo = null;

        if (aDataRun != null) {

            tempInfo = this.getInfoFactory().asInfo(aDataRun);
        }

        return tempInfo;
    }

}
