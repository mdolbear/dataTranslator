package com.mjdsft.mapper.info;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Value;

import java.util.Date;
import java.util.List;

@Value
@NoArgsConstructor(force = true)
@AllArgsConstructor
public class DataTranslateDefinitionInfo {

    private String id;
    private String userProfileIdentifier;
    private Date creationDate;
    private int version;
    private List<NodeDefinitionInfo> translatorNodes;
    private SourceObjectDescriptionInfo sourceObjectDescription;
    private String targetClassName;
}
