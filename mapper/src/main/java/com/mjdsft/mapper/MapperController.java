package com.mjdsft.mapper;

import com.mjdsft.mapper.info.DataTranslateDefinitionInfo;
import com.mjdsft.mapper.info.MapperInfoFactory;
import com.mjdsft.mapper.model.DataTranslateDefinition;
import com.mjdsft.mapper.persistence.MapperServiceFacade;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/mapper")
@Slf4j
public class MapperController {

    @Getter(AccessLevel.PRIVATE) @Setter(AccessLevel.PRIVATE)
    private MapperServiceFacade mapperService;

    @Getter(AccessLevel.PRIVATE) @Setter(AccessLevel.PRIVATE)
    private MapperInfoFactory infoFactory;

    /**
     * Answer my logger
     *
     * @return org.slf4j.Logger
     */
    private static Logger getLogger() {
        return log;
    }

    /**
     * Answer an instance of me for aMapperService
     * @param aService MapperService
     */
    @Autowired
    public MapperController(MapperServiceFacade aService) {

        super();
        this.setMapperService(aService);
        this.setInfoFactory(new MapperInfoFactory());

    }

    /**
     * Create data translate definition for aProfileId
     * @param aProfileId String
     * @param aTargetClassname String
     * @param aVersionId int
     */
    @PostMapping("/create")
    public String createDataTranslateDefinition(@RequestParam("profileId") String aProfileId,
                                                @RequestParam("versionId") int aVersionId,
                                                @RequestParam("targetClass") String aTargetClassname) {

        String  tempId;

        this.validateNonZeroPositiveVersionId(aVersionId);
        tempId = this.getMapperService().createNewDataTranslateDefinition(aProfileId,
                                                                          aVersionId,
                                                                          aTargetClassname);
        return tempId;

    }

    /**
     * Delete a data translate definition for anId
     * @param anId String
     */
    @DeleteMapping("/delete")
    public void deleteDataTranslateDefinition(@RequestParam("id") String anId) {

        this.getMapperService().deleteDataTranslation(anId);
    }

    /**
     * Answer my data translate definition for anId
     * @param anId String
     * @return DataTranslateDefinitionInfo
     */
    @GetMapping("/dataTranslatorById")
    public DataTranslateDefinitionInfo findDataTranslatorById(@RequestParam("id") String anId) {

        DataTranslateDefinition         tempObj;

        tempObj = this.getMapperService().findDataTranslateDefinitionById(anId);
        return this.asDataTranslateDefinitionInfo(tempObj);



    }

    /**
     * Add or update node translation for aName and anInputStream
     * @param aUserProfileId String
     * @param aVersionId int
     * @param aFile MultipartFile
     * @return ResponseEntity
     */
    @PostMapping("/addOrUpdateNodeFile")
    public ResponseEntity<DataTranslateDefinitionInfo>
                addOrUpdateNodeTranslateFor(@RequestParam("profileId")  String aUserProfileId,
                                            @RequestParam("versionId") int aVersionId,
                                            @RequestParam("file") MultipartFile aFile) {

        DataTranslateDefinition tempDataTranslateDef = null;

        this.logFile(aFile);
        this.validateFileNotNullOrEmpty(aFile);
        this.validateNonZeroPositiveVersionId(aVersionId);

        try {

            this.getMapperService().addOrUpdateNodeTranslateFor(aUserProfileId,
                                                                aVersionId,
                                                                aFile.getOriginalFilename(),
                                                                aFile.getInputStream());
            tempDataTranslateDef =
                    this.getMapperService().findDataTranslateDefinitionForProfileIdAndVersion(aUserProfileId,
                                                                                              aVersionId);

        }
        catch (IOException e) {

            val tempMsg = "Error parsing or reading uploaded file: ";
            this.logAndThrowException(tempMsg,
                                      new RuntimeException(tempMsg, e));
        }

        return
                this.produceSuccessfulResponseState(this.asDataTranslateDefinitionInfo(tempDataTranslateDef));
    }

    /**
     * Add source fields to a data translate definition
     * @param aUserProfileId String
     * @param aVersionId int
     * @param aFields List
     * @return ResponseEntity
     */
    @PostMapping("/addSourceFields")
    public ResponseEntity<DataTranslateDefinitionInfo>
                addSourceFieldsForDataTranslateDefinition(@RequestParam("profileId")  String aUserProfileId,
                                                          @RequestParam("versionId") int aVersionId,
                                                          @RequestParam("fields") List<String> aFields) {

        DataTranslateDefinition tempDef;

        this.validateNonZeroPositiveVersionId(aVersionId);
        tempDef = this.getMapperService().addSourceFieldsForDataTranslateDefinition(aUserProfileId,
                                                                                    aVersionId,
                                                                                    aFields);
        return this.produceSuccessfulResponseState(this.asDataTranslateDefinitionInfo(tempDef));

    }


  /**
   * Validate file not null or empty
   * @param aFile MultipartFile
   */
  private void validateFileNotNullOrEmpty(MultipartFile aFile) {

        if (aFile == null || aFile.isEmpty()) {

            val tempMsg = "Uploaded file either null or empty";

            this.logAndThrowException(tempMsg,
                    new RuntimeException(tempMsg));


        }

    }

    /**
     * Validate non-zero version id
     * @param aVersionId int
     */
    private void validateNonZeroPositiveVersionId(int aVersionId) {

        if (aVersionId <= 0) {

            val tempMsg = "Version id must be > 0";
            this.logAndThrowException(tempMsg,
                    new RuntimeException(tempMsg));

        }

    }

    /**
     * Answer a successful response for HttpStatus with the state of the NodeTranslateDefinition
     * @param anInfo DataTranslateDefinitionInfo
     * @return RepositoryEntity
     */
    private ResponseEntity<DataTranslateDefinitionInfo>
                produceSuccessfulResponseState(DataTranslateDefinitionInfo anInfo) {

        return new ResponseEntity(anInfo, HttpStatus.OK);
    }


    /**
     * Answer a list of data translate definitions
     * @param aDefinitions List
     * @return List
     */
    private List<DataTranslateDefinitionInfo>
        asDataTranslateDefinitionInfos(List<DataTranslateDefinition> aDefinitions) {

        List<DataTranslateDefinitionInfo> tempResult = new ArrayList<>();

        for (DataTranslateDefinition aDef: aDefinitions) {

            tempResult.add(this.asDataTranslateDefinitionInfo(aDef));
        }

        return tempResult;
    }


    /**
     * Answer a data translate definition info for non null data translate definition
     * @param aDataTranslateDefinition  DataTranslateDefinition
     * @return DataTranslateDefinitionInfo
     */
    private DataTranslateDefinitionInfo
                    asDataTranslateDefinitionInfo(DataTranslateDefinition aDataTranslateDefinition) {

        DataTranslateDefinitionInfo     tempResult = null;

        if (aDataTranslateDefinition != null) {

            tempResult = this.getInfoFactory().asInfo(aDataTranslateDefinition);
        }

        return tempResult;

    }

    /**
     * Answer my data translate definition for aProfileId
     * @param aProfileId String
     * @return DataTranslateDefinitionInfo
     */
    @GetMapping("/dataTranslatorByProfileId")
    public List<DataTranslateDefinitionInfo> findDataTranslatorByProfileId(@RequestParam("profileId") String aProfileId) {

        List<DataTranslateDefinition>         tempObj;

        tempObj = this.getMapperService().findDataTranslateDefinitionForProfileId(aProfileId);
        return  this.asDataTranslateDefinitionInfos(tempObj);

    }

    /**
     * Answer my data translate definition for aProfileId
     * @param aProfileId String
     * @return DataTranslateDefinitionInfo
     */
    @GetMapping("/dataTranslatorByProfileIdAndVersion")
    public DataTranslateDefinitionInfo
                findDataTranslatorByProfileIdAndVersion(@RequestParam("profileId") String aProfileId,
                                                        @RequestParam("version") int aVersion) {

        DataTranslateDefinition         tempObj;

        tempObj =
                this.getMapperService().findDataTranslateDefinitionForProfileIdAndVersion(aProfileId, aVersion);
        return  this.asDataTranslateDefinitionInfo(tempObj);

    }



    /**
     * Log aFile
     * @param aFile MultipartFile
     */
    private void logFile(MultipartFile aFile) {


        if (aFile != null) {

            getLogger().debug("File to be uplaoded: {} isEmpty: {} ",
                              aFile.getOriginalFilename(),
                              aFile.isEmpty());
        }
        else {

            getLogger().error("Uploaded file is null");
        }


    }


    /**
     * Log an exception
     * @param aMessage String
     * @param e Exception
     */
    private void logAndThrowException(String aMessage,
                                      RuntimeException e) {

        getLogger().error(aMessage, e);
        throw e;
    }

}
