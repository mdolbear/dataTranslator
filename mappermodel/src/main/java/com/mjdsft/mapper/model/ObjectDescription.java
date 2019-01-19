package com.mjdsft.mapper.model;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Document(collection="mapperCollection")
public class ObjectDescription {

    @Getter() @Setter()
    @Id
    private String id;

    @Getter() @Setter(AccessLevel.PRIVATE)
    private List<ObjectField> fields;

    /**
     * Answer a default instance
     */
    public ObjectDescription() {

        super();
        this.setId(new ObjectId().toHexString());
        this.clearFields();
    }

    /**
     * Clear fields
     */
    public void clearFields() {

        this.setFields(new ArrayList<ObjectField>());
    }


    /**
     * Add a field to me
     * @param aField ObjectField
     */
    public void addField(ObjectField aField) {

        ObjectField tempField;

        tempField = this.getFieldNamed(aField.getFieldName());
        this.validateFieldDoesNotExist(aField.getFieldName(), tempField);

        this.getFields().add(aField);
    }

    /**
     * Validate field does not exist for aName
     * @param aName String
     * @param aField ObjectField
     */
    private void validateFieldDoesNotExist(String aName, ObjectField aField) {

        if (aField != null) {

            throw new IllegalStateException("Field already exists for name: " + aName);
        }
    }

    /**
     * Answer my field named aName or null
     * @param aName String
     * @return ObjectField
     */
    private ObjectField getFieldNamed(String aName) {

        Optional<ObjectField> tempOpt;
        ObjectField           tempResult = null;

        tempOpt = this.getFields().stream()
                                  .filter((aField) -> aField.isForName(aName))
                                  .findFirst();

        if (tempOpt.isPresent()) {
            tempResult = tempOpt.get();
        }

        return tempResult;

    }

    /**
     * Answer my fields ordered by their index
     * @return List
     */
    public List<ObjectField> getFieldsSortedByIndex() {

        List<ObjectField> tempResults;

        tempResults = new ArrayList<ObjectField>(this.getFields());

        return tempResults.stream()
                          .sorted((f1,f2) -> f1.getColumnNumber() - f2.getColumnNumber())
                          .collect(Collectors.toList());

    }

    /**
     * Answer mny fields ordered by their index as strings
     * @return String[]
     */
    public String[] getFieldsSortedByIndexAsStrings() {

        List<String> tempValues;

        tempValues = this.getFieldsSortedByIndex().stream()
                                                  .map(objectField -> objectField.getFieldName())
                                                  .collect(Collectors.toList());

        return tempValues.toArray(new String[tempValues.size()]);
    }

}
