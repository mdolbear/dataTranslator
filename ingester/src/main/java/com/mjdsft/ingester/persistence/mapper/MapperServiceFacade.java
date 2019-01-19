package com.mjdsft.ingester.persistence.mapper;

import com.mjdsft.mapper.model.DataTranslateDefinition;
import lombok.NonNull;

public interface MapperServiceFacade {

    /**
     * Answer a data translate definition for aUserProfileId
     * @param aUserProfileId String
     * @return DataTranslateDefinition
     */
    DataTranslateDefinition findDataTranslateDefinitionForProfileId(@NonNull String aUserProfileId);

    /**
     * Answer a data translate definition for anId or null
     * @param anId String
     * @return DataTranslateDefinition
     */
    DataTranslateDefinition findDataTranslateDefinitionById(@NonNull String anId);

    /**
     * Answer a data translate definition for aUserProfileId
     * @param aUserProfileId String
     * @param aVersion int
     * @return DataTranslateDefinition
     */
    DataTranslateDefinition findDataTranslateDefinitionForProfileIdAndVersion(@NonNull String aUserProfileId,
                                                                              int aVersion);
}
