package com.mjdsft.mapper;

import com.mjdsft.mapper.info.DataTranslateDefinitionInfo;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.*;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertTrue;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = MapperApplication.class,
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class MapperControllerTest {

    @LocalServerPort
    private int port;

    //Constants
    private static final String URL_PREFIX = "http://localhost:";
    private static final String UPLOAD_NODE_FILE_URI = "/mapper/addOrUpdateNodeFile";
    private static final String CREATE_DATA_TRANS_DEF_URI = "/mapper/create";
    private static final String DELETE_DATA_TRANS_DEF_URI = "/mapper/delete";
    private static final String GET_DATA_TRANS_DEF_BY_PROFILEID_URI = "/mapper/dataTranslatorByProfileId";
    private static final String GET_DATA_TRANS_DEF_BY_PROFILEID_AND_VERSION_URI =
                                                    "/mapper/dataTranslatorByProfileIdAndVersion";
    private static final String GET_DATA_TRANS_DEF_BY_ID_URI = "/mapper/dataTranslatorById";
    private static final String ADD_SOURCE_FIELDS_URI = "/mapper/addSourceFields";

    private static final String TEST_FILE1_NAME = "instrumentMapping.xml";
    private static final String TEST_FILE1_PATH = "/dozer/" + TEST_FILE1_NAME;

    private static final String TEST_FILE2_NAME = "futureMapping.xml";
    private static final String TEST_FILE2_PATH = "/dozer/" + TEST_FILE2_NAME;

    private static final String TARGET_CLASS_NAME = "com.mjdsft.dozerexample.Future";

    private static final String USER_PROFILE_ID = "carrierXMappings";

    /**
     * Simple data translate definition create and delete test
     */
    @Test
    public void simplePersistenceTest() throws Exception {

        RestTemplate                        tempTemplate  = new RestTemplate();
        String                              tempProfileId = USER_PROFILE_ID;
        String                              tempId;
        DataTranslateDefinitionInfo         tempDataTranslateDefInfo;
        List<String>                        tempSourceFields;

        //Create Data Translate Definition and save off returned persistent id
        tempId = this.performCreateDataTranslateDefTest(tempTemplate,
                                                        tempProfileId,
                                                        TARGET_CLASS_NAME);

        //Make sure it exists -- find by id
        tempDataTranslateDefInfo = this.performFindDataTranslateDefById(tempTemplate, tempId);
        assertTrue("Data translate not found for id", tempDataTranslateDefInfo != null);

        //Make sure it exists -- find by profile id
        tempDataTranslateDefInfo = this.performFindDataTranslateDefByProfileId(tempTemplate, tempProfileId);
        assertTrue("Data translate not found for id", tempDataTranslateDefInfo != null);

        //Make sure it exists -- find by profile id and version
        tempDataTranslateDefInfo =
                this.performFindDataTranslateDefByProfileIdAndVersion(tempTemplate, tempProfileId, 1);
        assertTrue("Data translate not found for id and version", tempDataTranslateDefInfo != null);



        //Add source fields
        tempSourceFields = this.getDataSourceFields();
        tempDataTranslateDefInfo =
                this.performAddSourceFieldsTest(tempTemplate,
                                                tempProfileId,
                                                tempSourceFields);
        assertTrue("Source field lists are not equal",
                    tempDataTranslateDefInfo.getSourceObjectDescription() != null
                                && tempDataTranslateDefInfo.getSourceObjectDescription().getFields() != null
                                && this.areSourceFieldsEqual(tempDataTranslateDefInfo));

        //Add a node file to the data translate definition
        tempDataTranslateDefInfo = this.performFileUploadTest(tempTemplate,
                                                              tempProfileId,
                                                              TEST_FILE1_PATH,
                                                   true);
        assertTrue("File was not loaded",
                tempDataTranslateDefInfo.getTranslatorNodes() != null);

        //Add a second node file to the data translate definition
        tempDataTranslateDefInfo = this.performFileUploadTest(tempTemplate,
                                                              tempProfileId,
                                                              TEST_FILE2_PATH,
                                                            true);
        assertTrue("File was not loaded",
                tempDataTranslateDefInfo.getTranslatorNodes() != null);

        //Delete data translate definition
        this.performDeleteDataTranslateDefTest(tempTemplate, tempId);

        //Make sure it does not exist -- find by id
        tempDataTranslateDefInfo = this.performFindDataTranslateDefById(tempTemplate, tempId);
        assertTrue("Data translate not found for id", tempDataTranslateDefInfo == null);


    }

    /**
     * Answer whether source fields are equal. Currently not ordered....
     * @param aDef DataTranslateDefinitionInfo
     */
    private boolean areSourceFieldsEqual(DataTranslateDefinitionInfo aDef) {


        return aDef.getSourceObjectDescription().getFields().equals(this.getDataSourceFields());

    }

    /**
     * Answer the source fields for the data to be ingested
     * @return List
     */
    private List<String> getDataSourceFields() {

        String[] tempFields = {"instrumentType",
                                "externalId",
                                 "id",
                                 "symbol",
                                 "underlyingId",
                                 "expirationDate",
                                 "effectiveDate",
                                 "contractSize",
                                 "futureType",
                                 "firstNoticeDate",
                                 "lastTradingDate"};

        return Arrays.asList(tempFields);

    }


    /**
     * Perform create data translate test
     * @param aTemplate RestTemplate
     * @param aProfileId String
     * @param aTargetClassname String
     * @return String
     */
    private String performCreateDataTranslateDefTest(RestTemplate aTemplate,
                                                     String aProfileId,
                                                     String aTargetClassname)
            throws IOException {

        String  tempResultId;
        String  tempUrl;

        tempUrl = this.getUrlPrefixWithPort() + CREATE_DATA_TRANS_DEF_URI;

        tempResultId = aTemplate.postForObject(tempUrl,
                                               this.createFormDataForProfileIdAndTargetClass(aProfileId,
                                                                                             aTargetClassname),
                                               String.class);

        assertTrue("Result is null or wrong status code", tempResultId != null);

        return tempResultId;

    }

    /**
     * Perform delete data transalate test
     * @param aTemplate RestTemplate
     * @param anId String
     */
    private void performDeleteDataTranslateDefTest(RestTemplate aTemplate,
                                                   String anId)
            throws IOException {

        String                  tempUrl;
        Map<String,String>      tempMap = new HashMap<String, String>();

        tempUrl = this.getUrlPrefixWithPort() + DELETE_DATA_TRANS_DEF_URI + "?id=" + anId;

        tempMap.put("id", anId);
        aTemplate.delete(tempUrl);



    }



    /**
     * Perform add source fields test
     * @param aTemplate RestTemplate
     * @param aProfileId String
     * @param aSourceFields List
     * @return DataTranslateDefinitionInfo
     */
    private DataTranslateDefinitionInfo performAddSourceFieldsTest(RestTemplate aTemplate,
                                                                   String aProfileId,
                                                                   List<String> aSourceFields)
                        throws IOException {

        ResponseEntity<DataTranslateDefinitionInfo>  tempResponse;
        String                                       tempUrl;

        tempUrl = this.getUrlPrefixWithPort()
                    + ADD_SOURCE_FIELDS_URI
                    + "?profileId="+ aProfileId +"&fields=" + String.join(",", aSourceFields);

        tempResponse = aTemplate
                            .postForEntity(tempUrl,
                                           null,
                                           DataTranslateDefinitionInfo.class);

        assertEquals("Result is null or wrong status code",
                     HttpStatus.OK,
                     tempResponse.getStatusCode());

        return tempResponse.getBody();

    }

    /**
     * Perform file upload test
     * @param aTemplate RestTemplate
     * @param aFilename String
     */
    private DataTranslateDefinitionInfo performFileUploadTest(RestTemplate aTemplate,
                                                              String aProfileId,
                                                              String aFilename,
                                                              boolean isExistsFile) throws IOException {

        ResponseEntity<DataTranslateDefinitionInfo>   tempResponse;
        String                                        tempUrl;

        tempUrl = this.getUrlPrefixWithPort() + UPLOAD_NODE_FILE_URI;

        tempResponse = aTemplate
                .postForEntity(tempUrl,
                        this.createFormDataForUploadNode(aProfileId,
                                                         aFilename, isExistsFile),
                        DataTranslateDefinitionInfo.class);
        assertEquals("Result is null or wrong status code",
                     HttpStatus.OK,
                     tempResponse.getStatusCode());

        return tempResponse.getBody();
    }

    /**
     * Create form data with a file
     * @param aProfileId String
     * @param aFilename String
     * @parqm isFileExists boolean
     * @return HttpEntity
     */
    private HttpEntity<MultiValueMap<String, Object>> createFormDataForUploadNode(String aProfileId,
                                                                                  String aFilename,
                                                                                  boolean isFileExists)
            throws IOException {

        MultiValueMap<String, Object>     tempParams = new LinkedMultiValueMap<>();
        HttpHeaders                       tempHeaders = new HttpHeaders();

        tempHeaders.setContentType(MediaType.MULTIPART_FORM_DATA);
        tempParams.add("profileId", aProfileId);
        tempParams.add("file", this.createFileResource(aFilename, isFileExists));

        return new HttpEntity<>(tempParams, tempHeaders);


    }

    /**
     * Create file resource for file
     * @param aFilename String
     * @param isFileExists boolean
     */
    private FileSystemResource createFileResource(String aFilename,
                                                  boolean isFileExists)
            throws IOException {

        FileSystemResource  tempResource;

        if (isFileExists) {

            tempResource = new FileSystemResource(new ClassPathResource(aFilename).getFile());
        }
        else {

            tempResource = new FileSystemResource(aFilename);
        }

        return tempResource;
    }

    /**
     * Create form data for profileId
     * @param aProfileId String
     * @param aTargetClassname String
     * @return HttpEntity
     */
    private HttpEntity<MultiValueMap<String, String>>
                createFormDataForProfileIdAndTargetClass(String aProfileId, String aTargetClassname) {

        MultiValueMap<String, String>     tempParams = new LinkedMultiValueMap<>();
        HttpHeaders                       tempHeaders = new HttpHeaders();

        tempHeaders.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        tempParams.add("profileId", aProfileId);
        tempParams.add("targetClass", aTargetClassname);

        return new HttpEntity<>(tempParams, tempHeaders);

    }

    /**
     * Create form data for profileId
     * @param aProfileId String
     * @return HttpEntity
     */
    private HttpEntity<MultiValueMap<String, String>>  createFormDataForProfileId(String aProfileId) {

        MultiValueMap<String, String>     tempParams = new LinkedMultiValueMap<>();
        HttpHeaders                       tempHeaders = new HttpHeaders();

        tempHeaders.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        tempParams.add("profileId", aProfileId);

        return new HttpEntity<>(tempParams, tempHeaders);

    }

    /**
     * Create form data for id
     * @param anId String
     * @return HttpEntity
     */
    private HttpEntity<MultiValueMap<String, String>> createFormDataForId(String anId) {

        MultiValueMap<String, String>     tempParams = new LinkedMultiValueMap<>();
        HttpHeaders                       tempHeaders = new HttpHeaders();

        tempHeaders.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        tempParams.add("id", anId);

        return new HttpEntity<>(tempParams, tempHeaders);

    }

    /**
     * Perform find bu profile id
     * @param aProfileId String
     * @return DataTranslateDefinitionInfo
     * @throws Exception Failed rest invocation
     */
    private DataTranslateDefinitionInfo
                    performFindDataTranslateDefByProfileId(RestTemplate aTemplate,
                                                           String aProfileId) throws Exception {

        DataTranslateDefinitionInfo                     tempResult;

        tempResult =
                aTemplate.getForObject(this.getUrlPrefixWithPort()
                                        + GET_DATA_TRANS_DEF_BY_PROFILEID_URI
                                        +"?profileId="+ aProfileId,
                                        DataTranslateDefinitionInfo.class);

        return tempResult;

    }


    /**
     * Perform find bu profile id
     * @param aProfileId String
     * @return DataTranslateDefinitionInfo
     * @throws Exception Failed rest invocation
     */
    private DataTranslateDefinitionInfo
        performFindDataTranslateDefByProfileIdAndVersion(RestTemplate aTemplate,
                                                         String aProfileId,
                                                         int aVersion) throws Exception {

        DataTranslateDefinitionInfo                     tempResult;

        tempResult =
                aTemplate.getForObject(this.getUrlPrefixWithPort()
                                        + GET_DATA_TRANS_DEF_BY_PROFILEID_AND_VERSION_URI
                                        +"?profileId="+ aProfileId + "&version=" + aVersion,
                                        DataTranslateDefinitionInfo.class);

        return tempResult;

    }

    /**
     * Perform find bu profile id
     * @param anId String
     * @return DataTranslateDefinitionInfo
     * @throws Exception Failed rest invocation
     */
    private DataTranslateDefinitionInfo
            performFindDataTranslateDefById(RestTemplate aTemplate,
                                            String anId) throws Exception {

        DataTranslateDefinitionInfo                     tempResult;

        tempResult =
                aTemplate.getForObject(this.getUrlPrefixWithPort()
                                        + GET_DATA_TRANS_DEF_BY_ID_URI
                                        +"?id="+ anId,
                                        DataTranslateDefinitionInfo.class);

        return tempResult;

    }



    /**
     * Answer my url prefix with port appended
     * @return String
     */
    private String getUrlPrefixWithPort() {

        return URL_PREFIX + this.getPort();

    }

    /**
     * Answer the random port I need to connect to the endpoint
     * @return int
     */
    private int getPort() {
        return port;
    }


}
