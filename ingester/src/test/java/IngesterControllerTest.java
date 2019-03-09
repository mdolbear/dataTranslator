import com.google.common.io.ByteStreams;
import com.mjdsft.ingester.IngesterApplication;
import com.mjdsft.ingester.info.DataRunInfo;
import com.mjdsft.ingester.model.DataRunState;
import com.mjdsft.mapper.model.DataTranslateDefinition;
import com.mjdsft.mapper.model.NodeTranslateDefinition;
import com.mjdsft.mapper.model.ObjectDescription;
import com.mjdsft.mapper.model.ObjectField;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static junit.framework.TestCase.assertTrue;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = IngesterApplication.class,
                webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class IngesterControllerTest {


    @Getter(AccessLevel.PRIVATE) @Setter(AccessLevel.PRIVATE)
    @Autowired
    private MongoTemplate mongoTemplate;

    @Getter(AccessLevel.PRIVATE) @Setter(AccessLevel.PRIVATE)
    private DataTranslateDefinition dataTranslateDefinition;

    @LocalServerPort
    private int port;


    //Constants
    private static final String URL_PREFIX = "http://localhost:";
    private static final String USER_PROFILE_ID = "carrierXMappings";
    private static final int VERSION_ID = 1;
    private static String TEST_FILE_PATH = "/testdata";
    private static String CREATE_AND_START_DATA_RUN_URI = "/ingester/create";
    private static String FIND_DATA_RUN_BY_PROFILE_ID = "/ingester/dataRunsByProfileId";
    private static String FIND_DATA_RUN_BY_PROFILE_ID_AND_STATE = "/ingester/dataRunsByProfileIdAndState";
    private static String FIND_DATA_RUN_BY_ID = "/ingester/dataRunById";
    private static final String DELETE_DATA_TRANS_DEF_URI = "/ingester/delete";
    public static final int WAIT_COMPLETION_TIMEOUT = 2 * 1000;

    private static int NUMBER_OF_WAIT_ITERATIONS = 20;

    //Constants for creating mapper data
    private static String PARSER_FILE_PATH = "/dozer";
    private static String PARSER_TEST_FILE1 = PARSER_FILE_PATH + "/instrumentMapping.xml";
    private static String PARSER_TEST_FILE2 = PARSER_FILE_PATH + "/futureMapping.xml";
    private static final String TARGET_CLASS_NAME = "com.mjdsft.dozerexample.Future";



    /**
     * Answer a default instance
     */
    public IngesterControllerTest() {

        super();
    }


    /**
     * Setup -- insert data into mondgo embedded db
     */
    @Before
    public void setup() throws Exception {

        this.setDataTranslateDefinition(this.getMongoTemplate().save(this.createDataTranslateDefinition(),
                          "mapperCollection"));
    }

    /**
     * Persistent test
     */
    @Test
    public void simpleCreateAndDeleteRunTest() throws Exception {

        String              tempDataRunId;
        RestTemplate        tempTemplate = new RestTemplate();
        List<DataRunInfo>   tempRuns;
        DataRunInfo         tempRun;

        assertTrue("data translator not in mongo", this.getDataTranslateDefinition() != null);


        //create data run
        tempDataRunId =
                this.performCreateDataTranslateDefTest(tempTemplate,
                                                       USER_PROFILE_ID,
                                               1, this.getAbsolutePathForTestData(),
                                            true);
        assertTrue("Data run not created", tempDataRunId != null);



        //Find data runs by profile id
        tempRuns = this.performFindDataRunsByProfileId(tempTemplate, USER_PROFILE_ID);
        assertTrue("No data runs", tempRuns != null && !tempRuns.isEmpty());

        //Wait while completed
        assertTrue("Data run did not complete",
                    this.waitWhileNotCompleted(tempTemplate, tempDataRunId));

        //Find data runs by profile id and state
        tempRuns = this.performFindDataRunsByProfileIdAndState(tempTemplate,
                                                                USER_PROFILE_ID,
                                                                DataRunState.COMPLETED);
        assertTrue("No running data runs", tempRuns != null && !tempRuns.isEmpty());

        //Delete data translate definition
        this.performDeleteDataRunTest(tempTemplate, tempDataRunId);

        //Should have been deleted
        tempRun = this.performFindDataRunInfoById(tempTemplate, tempDataRunId);
        assertTrue("Data run does not exist", tempRun == null);



    }


    /**
     * Perform delete data transalate test
     * @param aTemplate RestTemplate
     * @param anId String
     */
    private void performDeleteDataRunTest(RestTemplate aTemplate,
                                           String anId)
            throws IOException {

        String                  tempUrl;
        Map<String,String> tempMap = new HashMap<String, String>();

        tempUrl = this.getUrlPrefixWithPort() + DELETE_DATA_TRANS_DEF_URI + "?id=" + anId;

        tempMap.put("id", anId);
        aTemplate.delete(tempUrl);



    }


    /**
     * Wait while not completed
     * @param aTemplate RestTemlate
     * @param anId String
     * @return boolean is completed
     */
    private boolean waitWhileNotCompleted(RestTemplate aTemplate,
                                          String anId) throws Exception {

        boolean     tempCompleted = false;
        int         tempIterationCount = 0;
        DataRunInfo tempRun;

        while (!tempCompleted &&
                    tempIterationCount < NUMBER_OF_WAIT_ITERATIONS) {

            tempRun = this.performFindDataRunInfoById(aTemplate, anId);
            assertTrue("Data run does not exist", tempRun != null);
            tempCompleted = tempRun.getRunState().equals(DataRunState.COMPLETED);
            if (!tempCompleted) {

                this.safelySleepFor(WAIT_COMPLETION_TIMEOUT);
            }
            tempIterationCount++;
        }

        return tempCompleted;
    }

    /**
     * Safely sleep for aMilliseconds
     * @param aMilliseconds long
     */
    private void safelySleepFor(long aMilliseconds) {

        try {
            Thread.sleep(aMilliseconds);
        }
        catch (InterruptedException e) {


        }
    }

    /**
     * Perform create and start data run
     * @param aTemplate RestTemplate
     * @param aProfileId String
     * @param aVersion int
     * @param aDirectoryPath Strinbg
     * @param isStopOnFail boolean
     * @return String
     * @throws IOException
     */
    private String performCreateDataTranslateDefTest(RestTemplate aTemplate,
                                                     String aProfileId,
                                                     int aVersion,
                                                     String aDirectoryPath,
                                                     boolean isStopOnFail)
            throws IOException {

        String  tempResultId;
        String  tempUrl;

        tempUrl = this.getUrlPrefixWithPort() + CREATE_AND_START_DATA_RUN_URI;

        tempResultId = aTemplate.postForObject(tempUrl,
                        this.createFormDataForCreateAndStartDataRun(aProfileId,
                                                                    aVersion,
                                                                    aDirectoryPath,
                                                                    isStopOnFail),
                        String.class);

        assertTrue("Result is null or wrong status code", tempResultId != null);

        return tempResultId;

    }

    /**
     * Find DatRunInfo by profile id and state
     * @param aTemplate RestTemlate
     * @param aProfileId String
     * @param aState DataRunState
     * @return List
     * @throws Exception
     */
    private List<DataRunInfo>
    performFindDataRunsByProfileIdAndState(RestTemplate aTemplate,
                                           String aProfileId,
                                           DataRunState aState) throws Exception {

        List<DataRunInfo> tempResult;

        tempResult =
                aTemplate.getForObject(this.getUrlPrefixWithPort()
                                        + FIND_DATA_RUN_BY_PROFILE_ID_AND_STATE
                                        +"?profileId="+ aProfileId + "&runState=" + aState,
                                        List.class);

        return tempResult;

    }

    /**
     * Find DatRunInfo by profile id and state
     * @param aTemplate RestTemplate
     * @param aProfileId String
     * @return DataRunInfo
     * @throws Exception
     */
    private List<DataRunInfo>
    performFindDataRunsByProfileId(RestTemplate aTemplate,
                                   String aProfileId) throws Exception {

        List<DataRunInfo> tempResult;

        tempResult =
                aTemplate.getForObject(this.getUrlPrefixWithPort()
                                + FIND_DATA_RUN_BY_PROFILE_ID
                                +"?profileId="+ aProfileId,
                        List.class);

        return tempResult;

    }

    /**
     * Perform find by id
     * @param aTemplate RestTemplate
     * @param anId String
     * @return DataRunInfo
     * @throws Exception
     */
    private DataRunInfo
                performFindDataRunInfoById(RestTemplate aTemplate, String anId) throws Exception {

        DataRunInfo                     tempResult;

        tempResult =
                aTemplate.getForObject(this.getUrlPrefixWithPort()
                                        + FIND_DATA_RUN_BY_ID
                                        +"?id="+ anId,
                                        DataRunInfo.class);

        return tempResult;

    }

    /**
     * Create form data for profile id, version , directory path, and stop on fail
     * @param aProfileId String
     * @param aVersion int
     * @param aDirectoryPath String
     * @param isStopOnFail boolean
     * @return HttpEntity
     */
    private HttpEntity<MultiValueMap<String, String>>
          createFormDataForCreateAndStartDataRun(String aProfileId,
                                                 int aVersion,
                                                 String aDirectoryPath,
                                                 boolean isStopOnFail) {

        MultiValueMap<String, String>     tempParams = new LinkedMultiValueMap<>();
        HttpHeaders tempHeaders = new HttpHeaders();

        tempHeaders.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        tempParams.add("profileId", aProfileId);
        tempParams.add("version", String.valueOf(aVersion));
        tempParams.add("directory", aDirectoryPath);
        tempParams.add("stopOnFail", String.valueOf(isStopOnFail));

        return new HttpEntity<>(tempParams, tempHeaders);

    }



    /**
     * Answer the absolute path to my test data
     * @return String
     * @throws IOException File load failure
     */
    private String getAbsolutePathForTestData() throws IOException {

        return new ClassPathResource(TEST_FILE_PATH).getFile().getAbsolutePath();
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




    //Methods to create data translate definition

    /**
     * Create a data translate definition
     */
    private DataTranslateDefinition createDataTranslateDefinition() throws Exception {

        DataTranslateDefinition tempDef;

        tempDef =
                new DataTranslateDefinition(USER_PROFILE_ID,
                                            VERSION_ID,
                                            TARGET_CLASS_NAME,
                                            this.createSourceObjectDescription());

        tempDef.addTranslationNode(this.createNodeTranslateDefinitionFromFilePath((PARSER_TEST_FILE1)));
        tempDef.addTranslationNode(this.createNodeTranslateDefinitionFromFilePath((PARSER_TEST_FILE2)));

        return tempDef;

    }


    /**
     * Create first translate node definition
     * @param aFilePath String
     * @return NodeTranslateDefinition
     */
    private NodeTranslateDefinition
    createNodeTranslateDefinitionFromFilePath(String aFilePath) throws Exception {

        return  new NodeTranslateDefinition(aFilePath, this.getBytesFromFile(aFilePath));
    }

    /**
     * Answer the inpt stream for aFilePath
     * @param aFilePath String
     * @return byte[]
     */
    private byte[] getBytesFromFile(String aFilePath)
            throws Exception {

        InputStream tempStream;
        ClassPathResource   tempResource;

        tempResource = new ClassPathResource(aFilePath);
        tempStream = tempResource.getInputStream();
        return ByteStreams.toByteArray(tempStream);

    }


    /**
     * Create source object description
     * @return ObjectDescription
     */
    private ObjectDescription createSourceObjectDescription() {

        ObjectDescription tempDesc = new ObjectDescription();

        tempDesc.addField(new ObjectField("instrumentType",1));
        tempDesc.addField(new ObjectField("externalId",2));
        tempDesc.addField(new ObjectField("id",3));
        tempDesc.addField(new ObjectField("symbol",4));
        tempDesc.addField(new ObjectField("underlyingId",5));
        tempDesc.addField(new ObjectField("expirationDate",6));
        tempDesc.addField(new ObjectField("effectiveDate",7));
        tempDesc.addField(new ObjectField("contractSize",8));
        tempDesc.addField(new ObjectField("futureType",9));
        tempDesc.addField(new ObjectField("firstNoticeDate",10));
        tempDesc.addField(new ObjectField("lastTradingDate",11));

        return tempDesc;
    }


}
