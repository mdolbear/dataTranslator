import com.mjdsft.ingester.model.DataRun;
import com.mjdsft.ingester.modelinterface.TargetObject;
import com.mjdsft.ingester.output.TargetConnection;
import com.mjdsft.ingester.persistence.ingester.IngesterServiceFacade;
import com.mjdsft.ingester.transform.FluxRepository;
import com.mjdsft.ingester.transform.FluxRepositoryImpl;
import com.mjdsft.ingester.transform.SubscriberCache;
import com.mjdsft.mapper.model.DataTranslateDefinition;
import com.mjdsft.mapper.model.NodeTranslateDefinition;
import com.mjdsft.mapper.model.ObjectDescription;
import com.mjdsft.mapper.model.ObjectField;
import com.google.common.io.ByteStreams;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

import static org.mockito.Mockito.mock;
import static org.springframework.test.util.AssertionErrors.assertTrue;

@ExtendWith(SpringExtension.class)
public class IngesterTest {

    @Getter(AccessLevel.PRIVATE) @Setter(AccessLevel.PRIVATE)
    private DataRun dataRun;

    @Getter(AccessLevel.PRIVATE) @Setter(AccessLevel.PRIVATE)
    private Semaphore testCompletedSignal;

    @Getter(AccessLevel.PRIVATE) @Setter(AccessLevel.PRIVATE)
    private List<TargetObject> targetObjects;

    @Getter(AccessLevel.PRIVATE) @Setter(AccessLevel.PRIVATE)
    private int numberOfIterations;

    @Getter(AccessLevel.PRIVATE) @Setter(AccessLevel.PRIVATE)
    private SubscriberCache subscriberCache;

    //Constants
    private static String PARSER_FILE_PATH = "/dozer";
    private static String TEST_FILE_PATH = "/testdata";
    private static String PARSER_TEST_FILE1 = PARSER_FILE_PATH + "/instrumentMapping.xml";
    private static String PARSER_TEST_FILE2 = PARSER_FILE_PATH + "/futureMapping.xml";

    private static int   NUMBER_OF_TEST_DATA_FILES = 3;
    private static final String TARGET_CLASS_NAME = "com.mjdsft.dozerexample.Future";
    private static final String USER_PROFILE_ID = "carrierXMappings";
    private static final int VERSION_ID = 1;
    private static final int NUMBER_OF_LINES_TO_PARSE = 1000;

    private static final int PARSER_TIMEOUT = 600 * 1000;


    /**
     * Answer a default instance
     */
    public IngesterTest() {

        super();
        this.setTargetObjects(new ArrayList<TargetObject>());
        this.setTestCompletedSignal(new Semaphore(0));
        this.setNumberOfIterations(0);
    }

    /**
     * Add to target objects
     * @param aTargetObject TargetObject
     */
    private void addTargetObject(TargetObject aTargetObject) {

        this.getTargetObjects().add(aTargetObject);

    }


    /**
     * Setup for test
     */
    @BeforeEach
    public void setup() {

        this.setDataRun(new DataRun("someIdValue",
                                    false));
        this.getDataRun().setId(UUID.randomUUID().toString());
    }

    /**
     * Simple parser test
     */
    @Test
    public void simpleParserTest() throws Exception {

        DataTranslateDefinition tempDef;
        FluxRepository          tempFluxRepo;

        tempDef = this.createDataTranslateDefinition();
        tempFluxRepo = this.createFluxRepository();

        tempFluxRepo.createDataIngester(this.getAbsolutePathForTestData(),
                                        this.getDataRun().getId(),
                                        tempDef);
        this.safelyWaitForSignal(PARSER_TIMEOUT);
        assertTrue("Did not get correct number of iterations",
                (this.getNumberOfIterations()
                        == NUMBER_OF_TEST_DATA_FILES * NUMBER_OF_LINES_TO_PARSE));

    }

    /**
     * Answer the absolute path to my test data
     * @return String
     * @throws IOException Failed to load file from class path
     */
    private String getAbsolutePathForTestData() throws IOException {

        return new ClassPathResource(TEST_FILE_PATH).getFile().getAbsolutePath();
    }

    /**
     * Safely wait for signal
     */
    private void safelyWaitForSignal(long aNumberOfMilliseconds) {

        try {
            this.getTestCompletedSignal().tryAcquire(1,
                                                     aNumberOfMilliseconds,
                                                     TimeUnit.MILLISECONDS);
        }
        catch(InterruptedException e) {

            //Do nothing
        }
    }

    /**
     * Create FluxRepository
     * @return FluxRepository
     */
    private FluxRepository createFluxRepository() {

        return new
                FluxRepositoryImpl(this.createSubscriberCache(),
                                   this.getIngesterService(),
                     null,
                                   this.getTargetConnection());
    }


    /**
     * Answer a mocked out ingester service
     * @return IngesterServiceFacade
     */
    private IngesterServiceFacade getIngesterService() {

        IngesterServiceFacade tempService;

        tempService = mock(IngesterServiceFacade.class);
        Mockito.when(tempService.updateDataRun(ArgumentMatchers.any(DataRun.class)))
                .thenReturn(this.getDataRun());
        Mockito.when(tempService.findDataRunById(ArgumentMatchers.any(String.class)))
                .thenReturn(this.getDataRun());

        return tempService;
    }

    /**
     * Answer a subscriber cache
     */
    private SubscriberCache createSubscriberCache() {

        SubscriberCache  tempCache;

        tempCache = new SubscriberCache();
        this.setSubscriberCache(tempCache);

        return tempCache;
    }

    /**
     * Answer a target connection
     * @return TargetConnection
     */
    private TargetConnection getTargetConnection() {

        return new TargetConnection() {

            @Override
            public void postTargetObject(TargetObject aTargetObject) {

                System.out.println(aTargetObject.toString());
                IngesterTest.this.addTargetObject(aTargetObject);
                IngesterTest.this.setNumberOfIterations(IngesterTest.this.getNumberOfIterations() + 1);

                if (IngesterTest.this.getNumberOfIterations() >= NUMBER_OF_TEST_DATA_FILES* NUMBER_OF_LINES_TO_PARSE) {

                    IngesterTest.this.getTestCompletedSignal().release();
                }

            }

        };

    }

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

        InputStream         tempStream;
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
