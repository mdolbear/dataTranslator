package com.mjdsft.mapper.graphql;

import com.mjdsft.mapper.model.DataTranslateDefinition;
import com.mjdsft.mapper.persistence.MapperServiceFacade;
import graphql.kickstart.tools.GraphQLQueryResolver;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DataTranslationDefinitionResolver implements GraphQLQueryResolver {

    @Getter(AccessLevel.PRIVATE) @Setter(AccessLevel.PRIVATE)
    private MapperServiceFacade mapperService;


    /**
     * Answer an instance of me for aMapperService
     * @param aMapperService MapperServiceFacade
     */
    @Autowired
    public DataTranslationDefinitionResolver(MapperServiceFacade aMapperService) {

        super();
        this.setMapperService(aMapperService);

    }

    /**
     * Find data translate definitions by aProfileId
     * @param userProfileId String
     * @return List
     */
    public List<DataTranslateDefinition> dataTranslationDefinitions(String userProfileId) {

        List<DataTranslateDefinition>         tempObjs;

        tempObjs = this.getMapperService().findDataTranslateDefinitionForProfileId(userProfileId);

        return tempObjs;

    }


}
