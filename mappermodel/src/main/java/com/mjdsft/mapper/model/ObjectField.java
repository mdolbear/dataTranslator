package com.mjdsft.mapper.model;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@ToString
@Document(collection="mapperCollection")
public class ObjectField {

    @Getter() @Setter(AccessLevel.PRIVATE)
    @Id
    private String id;

    @Getter() @Setter(AccessLevel.PRIVATE)
    private String fieldName;

    @Getter() @Setter()
    @Indexed
    private int columnNumber;

    /**
     * Answer a default instance
     */
    public ObjectField() {

        super();
        this.setId(new ObjectId().toHexString());
    }

    /**
     * Answer an instance of me for aField
     * @param aField String
     * @param aColumnNumber int
     */
    public ObjectField(String aField,
                       int aColumnNumber) {

        this();
        this.setFieldName(aField);
        this.setColumnNumber(aColumnNumber);
    }

    /**
     * Answer whether I am for a name
     * @param aName String
     * @return boolean
     */
    public boolean isForName(String aName) {

        return this.getFieldName() != null &&
                this.getFieldName().equals(aName);
    }

}
