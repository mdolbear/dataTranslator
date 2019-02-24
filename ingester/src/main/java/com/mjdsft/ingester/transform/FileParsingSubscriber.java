package com.mjdsft.ingester.transform;

import com.mjdsft.ingester.model.ConversionRecordFailure;
import com.mjdsft.ingester.model.DataRun;
import com.mjdsft.ingester.modelinterface.TargetObject;
import com.mjdsft.ingester.output.TargetConnection;
import com.mjdsft.ingester.persistence.ingester.IngesterServiceFacade;
import com.mjdsft.mapper.model.DataTranslateDefinition;
import com.mjdsft.mapper.model.NodeTranslateDefinition;
import com.mjdsft.mapper.model.ObjectDescription;
import com.github.dozermapper.core.DozerBeanMapperBuilder;
import com.github.dozermapper.core.Mapper;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.LineIterator;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;
import org.slf4j.Logger;

import java.io.*;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

@Slf4j
public class FileParsingSubscriber implements Subscriber<Path> {

    @Getter(AccessLevel.PRIVATE) @Setter(AccessLevel.PRIVATE)
    private String dataRunId;

    @Getter(AccessLevel.PRIVATE) @Setter(AccessLevel.PRIVATE)
    private FluxRepositoryImpl repositoryService;

    @Getter(AccessLevel.PRIVATE) @Setter(AccessLevel.PRIVATE)
    private Subscription subscription;

    @Getter(AccessLevel.PRIVATE) @Setter(AccessLevel.PRIVATE)
    private DataTranslateDefinition dataTranslateDefinition;

    @Getter(AccessLevel.PRIVATE) @Setter(AccessLevel.PRIVATE)
    private CSVFormat format;

    @Getter(AccessLevel.PRIVATE) @Setter(AccessLevel.PRIVATE)
    private Class<?> targetClass;

    @Getter(AccessLevel.PROTECTED) @Setter(AccessLevel.PRIVATE)
    private Mapper dozerMapper;

    //Constants
    private static final int NUMBER_OF_CONCURRENT_REQUESTS = 1;

    /**
     * Answer my logger
     * @return Logger
     */
    private static Logger getLogger() {

        return log;
    }


    /**
     * Answer an instance on the arguments below
     * @param aDataRunId String
     * @param aService FluxRepositoryImpl
     * @param aDefinition DataTranslateDefinition
     */
    public FileParsingSubscriber(String aDataRunId,
                                 FluxRepositoryImpl aService,
                                 DataTranslateDefinition aDefinition) {

        super();
        this.setDataRunId(aDataRunId);
        this.setRepositoryService(aService);
        this.setFormat(this.createCSVFormatFrom(aDefinition.getSourceObjectDescription()));
        this.setTargetClass(this.getTargetClass(aDefinition));
        this.setDataTranslateDefinition(aDefinition);

    }

    /**
     * Answer the target class from aDefinition
     * @param aDefinition DataTranslateDefinition
     * @return Class
     */
    private Class<?> getTargetClass(DataTranslateDefinition aDefinition)  {

        String tempMsg = "Failed to load target class";
        try {

            return Class.forName(aDefinition.getTargetClassName());

        }
        catch (ClassNotFoundException e) {

            getLogger().error(tempMsg, e);
            throw new IllegalStateException(tempMsg, e);
        }
    }


    /**
     * Handle on subscribe event
     * @param s Subscription
     */
    @Override
    public void onSubscribe(Subscription s) {

        this.setSubscription(s);
        s.request(NUMBER_OF_CONCURRENT_REQUESTS);
        this.initializeDozerForDataTranslateDefinition(this.getDataTranslateDefinition());

    }

    /**
     * Initialize dozer for aUserProfileId and aVersion
     * @param aDefinition DataTranslateDefinition
     */
    private void initializeDozerForDataTranslateDefinition(@NonNull DataTranslateDefinition aDefinition) {

        DozerBeanMapperBuilder tempBuilder;

        tempBuilder = DozerBeanMapperBuilder.create();

        for (NodeTranslateDefinition aNode: aDefinition.getTranslatorNodes()) {

            tempBuilder.withXmlMapping(this.createSupplierFrom(aNode.getContents()));
        }

        this.setDozerMapper(tempBuilder.build());

    }

    /**
     * Create Input Stream supplier from byte[]
     * @param aBytes byte[]
     * @return Supplier
     */
    private Supplier<InputStream> createSupplierFrom(byte[] aBytes) {

        return new Supplier<InputStream>() {

            public InputStream get() {

                return new ByteArrayInputStream(aBytes);
            }
        };

    }

    /**
     * Handle on next event
     * @param aPath Path
     */
    @Override
    public void onNext(Path aPath) {

        if (!this.cancelSubscriptionIfNecessary()) {

            this.parseFileForPath(aPath);
            this.getSubscription().request(NUMBER_OF_CONCURRENT_REQUESTS);
        }

    }

    /**
     * Cancel subscription if a cancel was requested on my run. Answer true
     * if I had to cancel
     * @return boolean
     */
    private boolean cancelSubscriptionIfNecessary() {

        DataRun   tempRun;
        boolean   tempResult;

        tempRun = this.findDataRunById();
        tempResult = tempRun.isCancelled();
        if (tempResult) {

            this.getSubscription().cancel();
            this.getCache().removeSubscriberForId(this.getDataRunId());
        }

        return tempResult;
    }


    /**
     * Handle on error event
     * @param t
     */
    @Override
    public void onError(Throwable t) {

        DataRun                 tempRun;

        tempRun = this.findDataRunById();
        tempRun.markAsFailed();
        this.recordDataRunError((Exception)t, tempRun);

        this.getCache().removeSubscriberForId(this.getDataRunId());

    }

    /**
     * Handle on complete event
     */
    @Override
    public void onComplete() {

        DataRun                 tempRun;

        tempRun = this.findDataRunById();
        if (!tempRun.getConversionFailures().isEmpty()) {

            tempRun.markAsFailed();
        }
        else {

            tempRun.markAsCompleted();
        }

        this.getIngesterService().updateDataRun(tempRun);
        this.getCache().removeSubscriberForId(this.getDataRunId());

    }

    /**
     * Parse file for aPath
     * @param aPath Path
     */
    private void parseFileForPath(Path aPath) {

        LineIterator    tempItr = null;
        File            tempFile;

        try {

            tempFile = aPath.toFile();
            tempItr = FileUtils.lineIterator(tempFile, "UTF-8");
            while (tempItr.hasNext()) {

                this.parseAndOutputLine(tempItr.nextLine());
            }

        }
        catch (Exception e) {

            this.throwFileParseError(e);
        }
        finally {

            this.closeLineIterator(tempItr);
        }

    }

    /**
     * Quietly close non null line iterator
     * @param anIterator
     */
    private void closeLineIterator(LineIterator anIterator) {

        if (anIterator != null) {

            LineIterator.closeQuietly(anIterator);

        }

    }

    /**
     * Throw file parse error
     * @param e Exception
     */
    private void throwFileParseError(Exception e) {

        String  tempMsg = "Failure on File Parse";

        getLogger().error(tempMsg, e);
        throw new IllegalStateException(tempMsg, e);
    }

    /**
     * Parse and output a line
     */
    private void parseAndOutputLine(String aLine) {

        Map<String, String> tempMap;
        TargetObject        tempObject;

        try {

            tempMap = this.createMapForParsedLine(aLine);
            tempObject =
                    (TargetObject)this.getDozerMapper().map(tempMap,this.getTargetClass());
            this.getTargetConnection().postTargetObject(tempObject);
            this.updateNumberOfSuccessfulRecordConversions();

        }
        catch (Exception e) {

            this.throwExceptionOrRecordFailureAndContinue(e);
        }

    }

    /**
     * Depending on what is specified in my data run, either bail out or continue
     * with processing
     * @param e Exception
     */
    private void throwExceptionOrRecordFailureAndContinue(Exception e) {

        ByteArrayOutputStream   tempStream;
        DataRun                 tempRun;
        ConversionRecordFailure tempFailure;


        tempRun = this.findDataRunById();

        if (tempRun.isStopOnFailure()) {

            throw new IllegalStateException("Failed to parse line");
        }
        else {

            //Record error
            this.recordDataRunError(e, tempRun);
        }

    }

    /**
     * Record the error e that occurred for my data run and persist the eror
     * @param e Exception
     * @param aRun DataRun
     */
    private void recordDataRunError(Exception e,
                                    DataRun aRun) {

        ByteArrayOutputStream   tempStream;
        ConversionRecordFailure tempFailure;

        //Get stack trace from e
        tempStream = new ByteArrayOutputStream();
        e.printStackTrace(new PrintWriter(tempStream));

        //Create ConversionRecordFailure
        tempFailure = new ConversionRecordFailure(tempStream.toByteArray());
        aRun.addFailure(tempFailure);

        //Persist it
        this.getIngesterService().updateDataRun(aRun);

    }


    /**
     * Answer a map of keys and values for aLine
     * @param aLine String
     * @return Map
     */
    private Map<String, String> createMapForParsedLine(String aLine) throws IOException {

        CSVParser            tempParser;
        List<CSVRecord>      tempRecords;

        tempParser = this.getParserForLine(aLine);
        tempRecords = tempParser.getRecords();
        this.validateRecordsNotEmpty(tempRecords);

        //There should be only one record for aLine
        return tempRecords.get(0).toMap();

    }

    /**
     * Validate records not empty
     * @param aRecords List
     */
    private void validateRecordsNotEmpty(List<CSVRecord> aRecords) {

        String  tempMsg = "Parse a line and received empty records";

        if (aRecords.isEmpty()) {

            getLogger().error(tempMsg);
            throw new IllegalStateException(tempMsg);
        }
    }


    /**
     * Create parser for aLine
     * @param aLine String
     * @return CSVParser
     */
    private CSVParser getParserForLine(String aLine) throws IOException {

        return CSVParser.parse(aLine, this.getFormat());
    }

    /**
     * Create a CSVFormat from aSourceObjectDescription
     * @param aSourceObjectDescription ObjectDescription
     * @return CSVFormat
     */
    private CSVFormat createCSVFormatFrom(@NonNull ObjectDescription aSourceObjectDescription) {

        return CSVFormat.DEFAULT
                        .withHeader(aSourceObjectDescription.getFieldsSortedByIndexAsStrings());
    }

    /**
     * Update number of successful record conversions
     */
    private void updateNumberOfSuccessfulRecordConversions() {
        
        DataRun tempRun;
        
        tempRun = this.findDataRunById();
        tempRun.incrementNumberOfSuccessfulRecordConversions();
        this.getIngesterService().updateDataRun(tempRun);
        
    }
    
    
    /**
     * Find a DataRun by id
     * @return DataRun
     */
    private DataRun findDataRunById() {

        return this.getIngesterService().findDataRunById(this.getDataRunId());
    }

    /**
     * Answer the ingester service
     * @return IngesterServiceFacade
     */
    private IngesterServiceFacade getIngesterService() {

        return this.getRepositoryService().getIngesterService();
    }

    /**
     * Answer my target connection
     * @return TargetConnection
     */
    private TargetConnection getTargetConnection() {
        return this.getRepositoryService().getTargetConnection();
    }

    /**
     * Answer my cache
     * @return Subscriber
     */
    private SubscriberCache getCache() {
        return this.getRepositoryService().getCache();
    }
}
