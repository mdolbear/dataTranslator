package com.mjdsft.ingester.persistence.mapper;

import com.mjdsft.mapper.model.DataTranslateDefinition;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface MapperRepository extends MongoRepository<DataTranslateDefinition, String> {

    /**
     * Find a translate definition by aUserProfileIdentifier. Even though this
     * query returns a list, we currently enforce that there is only one translation
     * for aUserProfileIdentifier
     * @param aProfileId String
     * @return List
     */
    public List<DataTranslateDefinition> findTranslationsByUserProfileIdentifier(String aProfileId);

    /**
     * Find a translate definition by aUserProfileIdentifier and aVersion. Even though this
     * query returns a list, we currently enforce that there is only one translation
     * for aUserProfileIdentifier
     * @param aProfileId String
     * @param aVersion int
     * @return List
     */
    public List<DataTranslateDefinition> findTranslationsByUserProfileIdentifierAndVersion(String aProfileId,
                                                                                           int aVersion);


}
