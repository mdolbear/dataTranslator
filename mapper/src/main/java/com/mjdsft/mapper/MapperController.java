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
     */
    @PostMapping("/create")
    public String createDataTranslateDefinition(@RequestParam("profileId") String aProfileId,
                                                @RequestParam("targetClass") String aTargetClassname) {

        String  tempId;

        tempId = this.getMapperService().createNewDataTranslateDefinition(aProfileId,
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
     * @param aFile MultipartFile
     * @return ResponseEntity
     */
    @PostMapping("/addOrUpdateNodeFile")
    public ResponseEntity<DataTranslateDefinitionInfo>
                addOrUpdateNodeTranslateFor(@RequestParam("profileId")  String aUserProfileId,
                                            @RequestParam("file") MultipartFile aFile) {

        DataTranslateDefinition tempDataTranslateDef = null;

        this.logFile(aFile);
        this.validateFileNotNullOrEmpty(aFile);
        try {

            this.getMapperService().addOrUpdateNodeTranslateFor(aUserProfileId,
                                                                aFile.getOriginalFilename(),
                                                                aFile.getInputStream());
            tempDataTranslateDef =
                    this.getMapperService().findDataTranslateDefinitionForProfileId(aUserProfileId);

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
     * @param aFields List
     * @return ResponseEntity
     */
    @PostMapping("/addSourceFields")
    public ResponseEntity<DataTranslateDefinitionInfo>
                addSourceFieldsForDataTranslateDefinition(@RequestParam("profileId")  String aUserProfileId,
                                                          @RequestParam("fields") List<String> aFields) {

        DataTranslateDefinition tempDef;


        tempDef = this.getMapperService().addSourceFieldsForDataTranslateDefinition(aUserProfileId,
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
     * Answer a successful response for HttpStatus with the state of the NodeTranslateDefinition
     * @param anInfo DataTranslateDefinitionInfo
     * @return RepositoryEntity
     */
    private ResponseEntity<DataTranslateDefinitionInfo>
                produceSuccessfulResponseState(DataTranslateDefinitionInfo anInfo) {

        return new ResponseEntity(anInfo, HttpStatus.OK);
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
    public DataTranslateDefinitionInfo findDataTranslatorByProfileId(@RequestParam("profileId") String aProfileId) {

        DataTranslateDefinition         tempObj;

        tempObj = this.getMapperService().findDataTranslateDefinitionForProfileId(aProfileId);
        return  this.asDataTranslateDefinitionInfo(tempObj);

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
