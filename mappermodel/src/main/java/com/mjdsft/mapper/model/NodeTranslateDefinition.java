package com.mjdsft.mapper.model;

import lombok.*;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@ToString(of = {"id", "filename"})
@Document(collection="mapperCollection")
@EqualsAndHashCode(of={"id", "filename"})
public class NodeTranslateDefinition {

    @Getter() @Setter()
    @Id
    private String id;

    @Getter() @Setter(AccessLevel.PRIVATE)
    private String filename;

    @Getter() @Setter()
    private byte[] contents;

    /**
     * Answer a default instance
     *
     */
    public NodeTranslateDefinition() {

        super();
        this.setId(new ObjectId().toHexString());
    }
    /**
     * Answer an instance of me for aFilename and aBytes
     * @param aFilename String
     * @param aBytes byte[]
     */
    public NodeTranslateDefinition(String aFilename, byte[] aBytes) {


        this();
        this.setFilename(aFilename);
        this.setContents(aBytes);

    }

    /**
     * Answer whether I am for a name
     * @param aName String
     * @return boolean
     */
    public boolean isForName(String aName) {

        return this.getFilename() != null &&
                this.getFilename().equals(aName);
    }

}
