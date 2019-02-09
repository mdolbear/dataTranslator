package com.mjdsft.mapper.model;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Document(collection="mapperCollection")
@ToString(of = {"id", "userProfileIdentifier", "version"})
public class DataTranslateDefinition {

    @Getter() @Setter(AccessLevel.PRIVATE)
    @Id
    private String id;

    @Getter() @Setter(AccessLevel.PRIVATE)
    @Indexed
    private String userProfileIdentifier;

    @Getter() @Setter(AccessLevel.PRIVATE)
    private String targetClassName;

    @Getter() @Setter(AccessLevel.PRIVATE)
    private List<NodeTranslateDefinition> translatorNodes;

    @Getter() @Setter(AccessLevel.PRIVATE)
    private ObjectDescription sourceObjectDescription;

    @Getter() @Setter(AccessLevel.PRIVATE)
    private Date creationDate;

    @Getter() @Setter()
    private int version;


    /**
     * Answer a default instance
     */
    public DataTranslateDefinition() {

        super();
        this.setTranslatorNodes(new ArrayList<NodeTranslateDefinition>());
        this.setCreationDate(new Date());
    }

    /**
     * Answer an instance of me on the arguments below
     * @param anIdentifier String
     * @param aVersionId int
     * @param aSourceObjectDescription ObjectDescription
     */
    public DataTranslateDefinition(String anIdentifier,
                                   int aVersionId,
                                   String aTargetClassName,
                                   ObjectDescription aSourceObjectDescription) {

        this();
        this.setUserProfileIdentifier(anIdentifier);
        this.setVersion(aVersionId);
        this.setSourceObjectDescription(aSourceObjectDescription);
        this.setTargetClassName(aTargetClassName);

    }

    /**
     * Add translation node to me
     * @param aNode NodeTranslateDefinition
     */
    public void addTranslationNode(NodeTranslateDefinition aNode) {

        this.getTranslatorNodes().add(aNode);

    }

    /**
     * Answer my translator node named aName or null
     * @param aName String
     * @return NodeTranslateDefinition
     */
    public NodeTranslateDefinition getTranslatorNodeNamed(String aName) {

        Optional<NodeTranslateDefinition> tempOpt;
        NodeTranslateDefinition           tempResult = null;

        tempOpt = this.getTranslatorNodes().stream()
                                           .filter((aField) -> aField.isForName(aName))
                                           .findFirst();

        if (tempOpt.isPresent()) {
            tempResult = tempOpt.get();
        }

        return tempResult;

    }

    /**
     * Answer whether I have a translator node named aName
     * @param aName String
     * @return boolean
     */
    public boolean hasTranslatorNodeNamed(String aName) {

        return this.getTranslatorNodeNamed(aName) != null;
    }

    /**
     * Update a translator named aName with aBytes. Throw an exception if a translator
     * does not exist
     * @param aName String
     * @param aBytes byte[]
     * @return NodeTranslateDefinition
     */
    public NodeTranslateDefinition updateTranslatorNamed(String aName,
                                                         byte[] aBytes) {

        NodeTranslateDefinition tempDef;

        tempDef = this.getTranslatorNodeNamed(aName);
        this.validateTranslatorNotNull(tempDef, aName);

        tempDef.setContents(aBytes);

        return tempDef;

    }

    /**
     * Update source object fields from aFieldNames
     * @param aFieldNames List
     */
    public void updateSourceObjectFields(List<String> aFieldNames) {

        this.validateSourceObjectExists();
        this.getSourceObjectDescription().clearFields();
        this.basicAddObjectFieldsFrom(aFieldNames);

    }

    /**
     * Add fields from a unique set of names
     * @param aUniqueNames Set
     */
    private void basicAddObjectFieldsFrom(List<String> aUniqueNames) {

        int tempColumnNumber = 1;

        for (String aName: aUniqueNames) {

            this.getSourceObjectDescription().addField(new ObjectField(aName,
                                                                       tempColumnNumber++));
        }

    }


    /**
     * Validate source object exists
     */
    private void validateSourceObjectExists() {

        if (this.getSourceObjectDescription() == null) {

            throw new IllegalStateException("Source ObjectDescription does not exist for a DataTranslateDefinition" +
                    " for profileId: " + this.getUserProfileIdentifier());
        }

    }

    /**
     * Validate translator not null
     * @param aTranslator NodeTranslateDefinition
     * @param aName String
     */
    private void validateTranslatorNotNull(NodeTranslateDefinition aTranslator,
                                           String aName) {

        if (aTranslator == null) {

            throw new IllegalStateException("No translator exists for name: " + aName);
        }

    }

}
