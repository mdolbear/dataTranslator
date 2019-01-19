package com.mjdsft.mapper.info;

import com.mjdsft.mapper.model.DataTranslateDefinition;
import com.mjdsft.mapper.model.NodeTranslateDefinition;
import com.mjdsft.mapper.model.ObjectDescription;
import lombok.NonNull;

import java.util.List;
import java.util.stream.Collectors;

public class MapperInfoFactory {

    /**
     * Answer a default instance
     */
    public MapperInfoFactory() {

        super();
    }


    /**
     * Answer a data translate definition info from aDataTranslateDefinition
     * @param aDataTranslateDefinition DataTranslateDefinition
     * @return DataTranslateDefinitionInfo
     */
    public DataTranslateDefinitionInfo asInfo(@NonNull DataTranslateDefinition aDataTranslateDefinition) {

        DataTranslateDefinitionInfo tempResult;
        List<NodeDefinitionInfo>    tempNodeDefinitions;
        SourceObjectDescriptionInfo tempSrcObject;

        tempNodeDefinitions =
                aDataTranslateDefinition.getTranslatorNodes().stream()
                                                             .map((node)->this.asInfo(node))
                                                             .collect(Collectors.toList());
        tempSrcObject = this.asInfo(aDataTranslateDefinition.getSourceObjectDescription());
        return new DataTranslateDefinitionInfo(aDataTranslateDefinition.getId(),
                                               aDataTranslateDefinition.getUserProfileIdentifier(),
                                               aDataTranslateDefinition.getCreationDate(),
                                               aDataTranslateDefinition.getVersion(),
                                               tempNodeDefinitions,
                                               tempSrcObject,
                                               aDataTranslateDefinition.getTargetClassName());

    }

    /**
     * Answer a node definition info from aNode
     * @param aNode NodeTranslateDefinitionInfo
     * @return NodeDefinitionInfo
     */
    private NodeDefinitionInfo asInfo(@NonNull NodeTranslateDefinition aNode) {

        return new NodeDefinitionInfo(aNode.getId(),
                                      aNode.getFilename());

    }


    /**
     * Answer a source description info from anObjectDescription
     * @param aSourceObject ObjectDescription
     * @return SourceObjectDescriptionInfo
     */
    private SourceObjectDescriptionInfo asInfo(@NonNull ObjectDescription aSourceObject) {

        List<String>                tempFieldNames;

        tempFieldNames = aSourceObject.getFieldsSortedByIndex().stream()
                                                  .map((field)->field.getFieldName())
                                                  .collect(Collectors.toList());

        return new SourceObjectDescriptionInfo(aSourceObject.getId(),
                                               tempFieldNames);

    }


}
