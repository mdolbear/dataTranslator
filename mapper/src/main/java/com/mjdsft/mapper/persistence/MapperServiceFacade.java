package com.mjdsft.mapper.persistence;

import com.mjdsft.mapper.model.DataTranslateDefinition;
import com.mjdsft.mapper.model.NodeTranslateDefinition;
import lombok.NonNull;

import java.io.InputStream;
import java.util.List;

public interface MapperServiceFacade {

    /**
     * Answer a new data translate definition for aUserProfileId. Answer the id
     * @param aUserProfileId String
     * @param aTargetClassName String
     * @return String
     */
    String createNewDataTranslateDefinition(@NonNull String aUserProfileId,
                                            @NonNull String aTargetClassName);

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
     * Delete data translation for anId
     * @param anId String
     */
    void deleteDataTranslation(String anId);

    /**
     * Add or update node translation for aName and anInputStream
     * @param aUserProfileId String
     * @param aName String
     * @param anInputStream InputStream
     */
    NodeTranslateDefinition addOrUpdateNodeTranslateFor(@NonNull String aUserProfileId,
                                                        @NonNull String aName,
                                                        @NonNull InputStream anInputStream);

    /**
     * Add source fields for a data translate defined for aUserProfileId
     * @param aUserProfileId String
     * @param aFieldNames List
     */
    DataTranslateDefinition addSourceFieldsForDataTranslateDefinition(@NonNull String aUserProfileId,
                                                                      @NonNull List<String> aFieldNames);

    /**
     * Answer a data translate definition for aUserProfileId
     * @param aUserProfileId String
     * @param aVersion int
     * @return DataTranslateDefinition
     */
    public DataTranslateDefinition findDataTranslateDefinitionForProfileIdAndVersion(@NonNull String aUserProfileId,
                                                                                     int aVersion);

}
