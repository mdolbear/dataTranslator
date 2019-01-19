package com.mjdsft.ingester.persistence.mapper;

import com.mjdsft.mapper.model.DataTranslateDefinition;
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
public class MapperServiceFacadeImpl implements MapperServiceFacade {

    @Getter(AccessLevel.PRIVATE) @Setter(AccessLevel.PRIVATE)
    private MapperRepository repository;

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
    public MapperServiceFacadeImpl(MapperRepository aRepository) {

        super();
        this.setRepository(aRepository);
    }

    /**
     * Answer a data translate definition for aUserProfileId
     * @param aUserProfileId String
     * @return DataTranslateDefinition
     */
    @Override
    public DataTranslateDefinition findDataTranslateDefinitionForProfileId(@NonNull String aUserProfileId) {

        DataTranslateDefinition             tempResult = null;
        List<DataTranslateDefinition>       tempDefs;
        Optional<DataTranslateDefinition>   tempOpt;

        tempDefs = this.getRepository().findTranslationsByUserProfileIdentifier(aUserProfileId);
        tempOpt = tempDefs.stream().findFirst();
        tempResult = this.getDataTranslateDefinitionIfPresent(tempOpt);

        return tempResult;
    }

    /**
     * Answer a data translation definition from anOptional if present
     * @param anOptional
     * @return
     */
    private DataTranslateDefinition
    getDataTranslateDefinitionIfPresent(Optional<DataTranslateDefinition> anOptional) {

        DataTranslateDefinition  tempResult = null;

        if (anOptional.isPresent()) {

            tempResult = anOptional.get();
        }

        return tempResult;
    }


    /**
     * Answer a data translate definition for anId or null
     * @param anId String
     * @return DataTranslateDefinition
     */
    @Override
    public DataTranslateDefinition findDataTranslateDefinitionById(@NonNull String anId) {

        DataTranslateDefinition             tempResult = null;
        Optional<DataTranslateDefinition>   tempOpt;


        tempOpt = this.getRepository().findById(anId);
        tempResult = this.getDataTranslateDefinitionIfPresent(tempOpt);

        return tempResult;

    }

    /**
     * Answer a data translate definition for aUserProfileId
     * @param aUserProfileId String
     * @param aVersion int
     * @return DataTranslateDefinition
     */
    @Override
    public DataTranslateDefinition findDataTranslateDefinitionForProfileIdAndVersion(@NonNull String aUserProfileId,
                                                                                     int aVersion) {

        DataTranslateDefinition             tempResult = null;
        List<DataTranslateDefinition>       tempDefs;
        Optional<DataTranslateDefinition>   tempOpt;

        tempDefs =
                this.getRepository().findTranslationsByUserProfileIdentifierAndVersion(aUserProfileId, aVersion);
        tempOpt = tempDefs.stream().findFirst();
        tempResult = this.getDataTranslateDefinitionIfPresent(tempOpt);

        return tempResult;

    }

}
