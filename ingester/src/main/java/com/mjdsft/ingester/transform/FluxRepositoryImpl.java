package com.mjdsft.ingester.transform;

import com.mjdsft.ingester.output.TargetConnection;
import com.mjdsft.ingester.persistence.ingester.IngesterServiceFacade;
import com.mjdsft.ingester.persistence.mapper.MapperServiceFacade;
import com.mjdsft.mapper.model.DataTranslateDefinition;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.reactivestreams.Subscriber;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@Slf4j
public class FluxRepositoryImpl implements FluxRepository {

    @Getter(AccessLevel.PROTECTED) @Setter(AccessLevel.PRIVATE)
    private SubscriberCache cache;

    @Getter(AccessLevel.PROTECTED) @Setter(AccessLevel.PRIVATE)
    private IngesterServiceFacade ingesterService;

    @Getter(AccessLevel.PRIVATE) @Setter(AccessLevel.PRIVATE)
    private MapperServiceFacade mapperService;

    @Getter(AccessLevel.PROTECTED) @Setter(AccessLevel.PRIVATE)
    private TargetConnection targetConnection;

    /**
     * Answer my logger
     * @return Logger
     */
    private static Logger getLogger() {

        return log;
    }

    /**
     * Answer an instance of me on a service and a subscriber cache
     * @param aCache SubscriberCache
     * @param aService IngesterServiceFacade
     */
    @Autowired
    public FluxRepositoryImpl(SubscriberCache aCache,
                              IngesterServiceFacade aService,
                              MapperServiceFacade aMapperService,
                              TargetConnection aConnection) {

        super();
        this.setCache(aCache);
        this.setIngesterService(aService);
        this.setMapperService(aMapperService);
        this.setTargetConnection(aConnection);
    }


    /**
     * Create data ingester
     * @param aDirectoryPath String
     * @param aDataRunId String
     * @param aDefinition DataTranslateDefintion
     *
     */
    @Override
    public void createDataIngester(@NonNull String aDirectoryPath,
                                   @NonNull String aDataRunId,
                                   @NonNull DataTranslateDefinition aDefinition) {

        Flux<Path>          tempFlux;
        Subscriber<Path>    tempSubscriber;

        //Create required ingest comnponents
        tempFlux = this.createFluxFrom(aDirectoryPath);
        tempSubscriber = this.createSubscriber(aDataRunId, aDefinition);

        //Start flow of data
        tempFlux.subscribe(tempSubscriber);

    }

    /**
     * Create subscriber
     * @param aDataRunId String
     * @param aDefinition DataTranslateDefinition
     * @return Subscriber
     */
    private Subscriber<Path> createSubscriber(String aDataRunId,
                                              DataTranslateDefinition aDefinition) {

        Subscriber<Path>        tempSubscriber;

        tempSubscriber = new FileParsingSubscriber(aDataRunId,
                                          this,
                                                   aDefinition);
        this.getCache().addSubscriber(aDataRunId, tempSubscriber);

        return tempSubscriber;
    }


    /**
     * Create flux for aFilePath
     * @param aDirectoryPath String
     * @return Flux
     */
    private Flux<Path> createFluxFrom(String aDirectoryPath) {

        Flux<Path>          tempResult  = null;
        List<Path>          tempFluxPaths;

        try (Stream<Path> tempPaths = Files.walk(Paths.get(aDirectoryPath))) {

            tempFluxPaths = tempPaths.filter(Files::isRegularFile)
                                     .collect(Collectors.toList());
            this.validateFilesExist(aDirectoryPath, tempFluxPaths);

            return Flux.fromIterable(tempFluxPaths)
                       .log()
                       .subscribeOn(Schedulers.parallel());

        }
        catch (IOException e) {

            this.logAndThrowIllegalStateException(
                        this.createFailureMessage("Failure attemping to parse paths in directory: ",
                                                  aDirectoryPath),
                        e);

        }

        return tempResult;

    }


    /**
     * Validate data translate definition exists for aUserProfileId and aVersion
     * @param aDefinition DataTranslateDefinition
     * @param aUserProfileId String
     * @param aVersion int
     */
    private void validateDataTranslateDefinitionExists(DataTranslateDefinition aDefinition,
                                                       String aUserProfileId,
                                                       int aVersion) {
        String  tempMsg;

        if (aDefinition == null) {

           tempMsg = this.createErrorMessageForNoDataTranslateDefinition(aUserProfileId, aVersion);
           getLogger().error(tempMsg);

           throw new IllegalStateException(tempMsg);
        }

    }

    /**
     * Answer an error message for the following parameters
     * @param aUserProfileId String
     * @param aVersion int
     * @return String
     */
    private String createErrorMessageForNoDataTranslateDefinition(String aUserProfileId,
                                                                int aVersion) {


        StringBuilder   tempBuilder = new StringBuilder();

        tempBuilder.append("DataTranslateDefinition does not exist for profileId: ");
        tempBuilder.append(((aUserProfileId != null) ? aUserProfileId : "null"));
        tempBuilder.append(" and version: ");
        tempBuilder.append(aVersion);

        return tempBuilder.toString();
    }

    /**
     * Create error message and throw IllegalStateException for e
     * @param aMessage String
     * @param e Exception
     */
    private void logAndThrowIllegalStateException(String aMessage,
                                                  Exception e) {


        getLogger().error(aMessage,e);
        throw new IllegalStateException(aMessage,e);

    }

    /**
     * Validate that there are paths to parse
     * @param aPaths List
     */
    private void validateFilesExist(String aDirectoryPath,
                                    List<Path> aPaths) {

        String  tempMsg = this.createFailureMessage("No files exist for path: ",
                                                    aDirectoryPath);

        if (aPaths == null ||
                aPaths.isEmpty()) {

            getLogger().error(tempMsg);
            throw new IllegalStateException(tempMsg);
        }

    }

    /**
     * Create failure message
     * @param aMessage String
     * @param aParameter String
     */
    private String createFailureMessage(String aMessage,
                                        String aParameter) {

        StringBuilder   tempBuilder = new StringBuilder();

        tempBuilder.append(aMessage);
        tempBuilder.append(((aParameter != null) ? aParameter : "null"));

        return tempBuilder.toString();
    }

}
