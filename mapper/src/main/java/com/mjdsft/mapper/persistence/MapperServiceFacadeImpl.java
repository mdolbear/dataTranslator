package com.mjdsft.mapper.persistence;

import com.mjdsft.mapper.model.DataTranslateDefinition;
import com.mjdsft.mapper.model.NodeTranslateDefinition;
import com.mjdsft.mapper.model.ObjectDescription;
import com.google.common.io.ByteStreams;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class MapperServiceFacadeImpl implements MapperServiceFacade {

    @Getter(AccessLevel.PRIVATE) @Setter(AccessLevel.PRIVATE)
    private MapperRepository repository;

    /**
     * Answer my logger
     * @return Logger
     */
    private static Logger getLogger() {

        return log;
    }

    /**
     * Answer an instance of me for aRepository
     * @param aRepository MapperRepository
     */
    @Autowired
    public MapperServiceFacadeImpl(MapperRepository aRepository) {

        super();
        this.setRepository(aRepository);
    }

    /**
     * Answer a new data translate definition for aUserProfileId. Answer the id
     * @param aUserProfileId String
     * @param aTargetClassname String
     * @return String
     */
    @Override
    //TODO -- Currently do not allow new versions...this needs to be changed
    public String createNewDataTranslateDefinition(@NonNull String aUserProfileId,
                                                   @NonNull String aTargetClassname) {

        DataTranslateDefinition tempDef;

        tempDef = this.findDataTranslateDefinitionForProfileId(aUserProfileId);
        this.validateDataTranslateDoesNotExistFor(aUserProfileId, tempDef);

        return this.basicCreateDataTranslationDefinition(aUserProfileId,
                                                         aTargetClassname);
    }

    /**
     * Create and save Data Translation Definition
     * @param aUserProfileId String
     * @return String
     */
    private String basicCreateDataTranslationDefinition(String aUserProfileId,
                                                        String aTargetClassname) {

        DataTranslateDefinition tempDef;

        tempDef = new DataTranslateDefinition(aUserProfileId,
                                              aTargetClassname,
                                              new ObjectDescription());

        this.getRepository().save(tempDef);

        return tempDef.getId();
    }

    /**
     * Answer a data translate definition for aUserProfileId
     * @param aUserProfileId String
     * @return DataTranslateDefinition
     */
    @Override
    public DataTranslateDefinition findDataTranslateDefinitionForProfileId(@NonNull String aUserProfileId) {

        DataTranslateDefinition             tempResult = null;
        List<DataTranslateDefinition>       tempDefs;
        Optional<DataTranslateDefinition>   tempOpt;

        tempDefs = this.getRepository().findTranslationsByUserProfileIdentifier(aUserProfileId);
        tempOpt = tempDefs.stream().findFirst();

        tempResult = this.getDataTranslateDefinitionIfPresent(tempOpt);

        return tempResult;
    }

    /**
     * Answer a data translate definition for aUserProfileId
     * @param aUserProfileId String
     * @param aVersion int
     * @return DataTranslateDefinition
     */
    @Override
    public DataTranslateDefinition findDataTranslateDefinitionForProfileIdAndVersion(@NonNull String aUserProfileId,
                                                                                     int aVersion) {

        DataTranslateDefinition             tempResult = null;
        List<DataTranslateDefinition>       tempDefs;
        Optional<DataTranslateDefinition>   tempOpt;

        tempDefs =
                this.getRepository().findTranslationsByUserProfileIdentifierAndVersion(aUserProfileId, aVersion);
        tempOpt = tempDefs.stream().findFirst();
        tempResult = this.getDataTranslateDefinitionIfPresent(tempOpt);

        return tempResult;

    }


    /**
     * Answer a data translation definition from anOptional if present
     * @param anOptional
     * @return
     */
    private DataTranslateDefinition
            getDataTranslateDefinitionIfPresent(Optional<DataTranslateDefinition> anOptional) {

        DataTranslateDefinition  tempResult = null;

        if (anOptional.isPresent()) {

            tempResult = anOptional.get();
        }

        return tempResult;
    }

    /**
     * Answer a data translate definition for anId or null
     * @param anId String
     * @return DataTranslateDefinition
     */
    @Override
    public DataTranslateDefinition findDataTranslateDefinitionById(@NonNull String anId) {

        DataTranslateDefinition             tempResult = null;
        Optional<DataTranslateDefinition>   tempOpt;


        tempOpt = this.getRepository().findById(anId);
        tempResult = this.getDataTranslateDefinitionIfPresent(tempOpt);

        return tempResult;

    }

    /**
     * Delete data translation for anId
     * @param anId String
     */
    @Override
    public void deleteDataTranslation(@NonNull String anId) {

        Optional<DataTranslateDefinition>   tempOpt;


        tempOpt = this.getRepository().findById(anId);
        if (tempOpt.isPresent()) {

            this.getRepository().delete(tempOpt.get());
        }

    }

    /**
     * Add or update node translation for aName and anInputStream
     * @param aUserProfileId String
     * @param aName String
     * @param anInputStream InputStream
     */
    @Override
    public NodeTranslateDefinition addOrUpdateNodeTranslateFor(@NonNull String aUserProfileId,
                                                               @NonNull String aName,
                                                               @NonNull InputStream anInputStream) {

        DataTranslateDefinition tempDataTranslateDef;
        NodeTranslateDefinition tempResult;

        tempDataTranslateDef = this.findDataTranslateDefinitionForProfileId(aUserProfileId);
        this.validateDataTranslateExistsFor(aUserProfileId, tempDataTranslateDef);

        tempResult = tempDataTranslateDef.getTranslatorNodeNamed(aName);
        if (tempResult == null) {

            tempResult = this.createAndAddNewNodeTranslate(aName, anInputStream, tempDataTranslateDef);
        }
        else {

            tempResult.setContents(this.safelyConvertInputStreamToByteArray(anInputStream));
        }

        this.getRepository().save(tempDataTranslateDef);

        return tempResult;

    }

    /**
     * Add source fields for a data translate defined for aUserProfileId
     * @param aUserProfileId String
     * @param aFieldNames List
     */
    @Override
    public DataTranslateDefinition addSourceFieldsForDataTranslateDefinition(@NonNull String aUserProfileId,
                                                                             @NonNull List<String> aFieldNames) {

        DataTranslateDefinition tempDataTranslateDef;

        tempDataTranslateDef = this.findDataTranslateDefinitionForProfileId(aUserProfileId);
        this.validateDataTranslateExistsFor(aUserProfileId, tempDataTranslateDef);

        tempDataTranslateDef.updateSourceObjectFields(aFieldNames);
        this.getRepository().save(tempDataTranslateDef);

        return tempDataTranslateDef;
    }

    /**
     * Validate that a data translate definition exists for aUserProfileId
     * @param aUserProfileId String
     * @param aTranslate DataTranslateDefinition
     */
    private void validateDataTranslateExistsFor(String aUserProfileId,
                                                DataTranslateDefinition aTranslate) {

        if (aTranslate == null) {

            throw new IllegalStateException("No Data Translate Definition exists for profileId: " +
                                            aUserProfileId);
        }

    }

    /**
     * Validate that a data translate definition exists for aUserProfileId
     * @param aUserProfileId String
     * @param aTranslate DataTranslateDefinition
     */
    private void validateDataTranslateDoesNotExistFor(String aUserProfileId,
                                                      DataTranslateDefinition aTranslate) {

        if (aTranslate != null) {

            throw new IllegalStateException("Data Translate Definition already exists for profileId: " +
                    aUserProfileId);
        }

    }

    /**
     * Create and add new node translate for the following arguments
     * @param aName String
     * @param anInputStream InputStream
     * @param aDataTranslateDef DataTranslateDefinition
     * @return NodeTranslateDefinition
     */
    private NodeTranslateDefinition createAndAddNewNodeTranslate(@NonNull String aName,
                                                                 @NonNull InputStream anInputStream,
                                                                 DataTranslateDefinition aDataTranslateDef) {
        NodeTranslateDefinition tempResult;
        tempResult = new NodeTranslateDefinition(aName,
                                                 this.safelyConvertInputStreamToByteArray(anInputStream));
        aDataTranslateDef.addTranslationNode(tempResult);
        return tempResult;
    }

    /**
     * Convert anInputStream to byte[]
     * @param anInputStream InputStream
     * @return byte[]
     */
    private byte[] safelyConvertInputStreamToByteArray(@NonNull InputStream anInputStream) {

        byte[]  tempBytes = null;

        try {
            tempBytes = ByteStreams.toByteArray(anInputStream);
        }
        catch (IOException e) {
            this.createAndThrowInputStreamAccessError(e);
        }

        return tempBytes;

    }

    /**
     * Create and throw input stream access error
     * @param e IOException
     * @return
     */
    private void createAndThrowInputStreamAccessError(IOException e) {

        String  tempMsg = "Error converting input stream for NodeTranslateDefinition";

        getLogger().error(tempMsg);
        throw new RuntimeException(tempMsg, e);

    }


}
