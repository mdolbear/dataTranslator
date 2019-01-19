package com.mjdsft.ingester.model;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection="ingesterCollection")
public class ConversionRecordFailure {

    @Getter() @Setter(AccessLevel.PRIVATE)
    @Id
    private String id;

    @Getter() @Setter()
    private byte[] failure;

    /**
     * Answer a instance for the arguments below
     * @param aFailureContents byte[]
     */
    public ConversionRecordFailure(byte[] aFailureContents) {

        super();
        this.setId(new ObjectId().toHexString());
        this.setFailure(aFailureContents);
    }

}
