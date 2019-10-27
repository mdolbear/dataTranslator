package com.mjdsft.datagenerator;

import com.mjdsft.dozerexample.FinancialObjectBuilder;
import com.mjdsft.dozerexample.InstrumentType;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Random;
import java.util.UUID;

public class DataCSVFileCreator {

    //Constants
    private static final String[] FILE_HEADERS =
            {FinancialObjectBuilder.INSTRUMENT_TYPE_KEY,
             FinancialObjectBuilder.EXTERNAL_ID_KEY,
             FinancialObjectBuilder.ID_KEY,
             FinancialObjectBuilder.SYMBOL_KEY,
             FinancialObjectBuilder.UNDERLYING_ID_KEY,
             FinancialObjectBuilder.EXPIRATION_DATE_KEY,
             FinancialObjectBuilder.EFFECTIVE_DATE_KEY,
             FinancialObjectBuilder.CONTRACT_SIZE_KEY,
             FinancialObjectBuilder.FUTURE_TYPE_KEY,
             FinancialObjectBuilder.FIRST_NOTICE_DATE_KEY,
             FinancialObjectBuilder.LAST_TRADING_DATE_KEY};

    private static final String[] STOCK_SYMBOLS = {
            "TXN",
            "AAPL",
            "IBM",
            "MMM",
            "AXP",
            "BA",
            "CAT",
            "CVX",
            "CSCO",
            "KO",
            "DIS",
            "DWDP",
            "XOM",
            "GS",
            "HD",
            "INTC",
            "JNJ",
            "JPM",
            "MCD",
            "MRK",
            "MSFT",
            "NKE",
            "PFE",
            "PG",
            "TRV",
            "UTX",
            "UMH",
            "VZ",
            "V",
            "WMT",
            "WBA"};

    private static final String[] FUTURE_TYPE = {
            "CURRENCY",
            "AGRICULTURAL",
            "LIVESTOCK",
            "SOFT",
            "METAL",
            "INDEX",
            "INTEREST_RATE",
            "ENERGY"
    };

    private static final int NUMBER_OF_DATA_COLUMNS = 11;
    private int MAX_DATE_BOUND = 5*24*3600*1000;

    /**
     * Answer my default instance
     */
    public DataCSVFileCreator() {

        super();

    }

    /**
     * Create CSV file for aPath. This file will contain randomized data for aNumberOfLines
     * @param aPath Path
     * @param aNumberOfLines int
     */
    public void createRandomizedDataCSVFile(Path aPath,
                                            int aNumberOfLines) {

        try (FileWriter tempOutWriter = new FileWriter(aPath.toFile())) {

            try (CSVPrinter tempPrinter = new CSVPrinter(tempOutWriter,
                                                         CSVFormat.DEFAULT.withHeader(FILE_HEADERS))) {

                this.printRandomizedData(tempPrinter,
                                         aNumberOfLines);
            }

        }
        catch (IOException e) {

            throw new RuntimeException("Failed to open FileWriter for " + aPath.toString(),
                                       e);
        }

    }

    /**
     * Print bids and asks
     */
    private void printRandomizedData(CSVPrinter aPrinter,
                                     int    aNumberOfLines) throws IOException {

        Object[]              tempDataLine;
        int                   i = 0;


        while (i < aNumberOfLines) {

            tempDataLine = this.createRandomizedDataLine();
            this.printRecord(aPrinter,tempDataLine);
            i++;
        }


    }


    /**
     * Print record
     */
    private void printRecord(CSVPrinter aPrinter,
                             Object[] aDataLine) throws IOException {

        aPrinter.printRecord(aDataLine);

    }

    /**
     * Create randomized data line
     * @return Object[]
     */
    private Object[] createRandomizedDataLine() {

        Object[] tempLine;

        tempLine = new Object[NUMBER_OF_DATA_COLUMNS];

        tempLine[0] = InstrumentType.FUTURE.name();
        tempLine[1] = String.valueOf((Math.abs(new Random().nextInt())));
        tempLine[2] =  UUID.randomUUID().toString();
        tempLine[3] = this.getRandomStockSymbol();

        tempLine[4] = ""; //UUID.randomUUID().toString();
        tempLine[5] = this.createRandomDateFromNow().toString();
        tempLine[6] = this.createRandomDateFromNow().toString();
        tempLine[7] = String.valueOf((Math.abs(new Random().nextInt())));

        tempLine[8] = this.getRandomFutureType();
        tempLine[9] = this.createRandomDateFromNow().toString();
        tempLine[10] = this.createRandomDateFromNow().toString();

        return tempLine;
    }


    /**
     * Answer a random stock symbol
     * @returh String
     */
    private String getRandomFutureType() {

        int tempRandomIdx;

        tempRandomIdx = this.getRandomKeyBoundedBy(FUTURE_TYPE.length);
        return FUTURE_TYPE[tempRandomIdx];

    }


    /**
     * Answer a random stock symbol
     * @returh String
     */
    private String getRandomStockSymbol() {

        int tempRandomIdx;

        tempRandomIdx = this.getRandomKeyBoundedBy(STOCK_SYMBOLS.length);
        return STOCK_SYMBOLS[tempRandomIdx];

    }


    /**
     * Answer a random index bounded by aSize
     * @param aSize int
     */
    private int getRandomKeyBoundedBy(int aSize) {

        return (new Random()).nextInt(aSize);
    }

    /**
     * Create random data from now
     * @return LocalDate
     */
    private LocalDate createRandomDateFromNow() {

        LocalDate   tempDate = LocalDate.now();
        Random      tempRandom = new Random();
        long        tempDelta;


        tempDelta = (long)tempRandom.nextInt(MAX_DATE_BOUND);
        return this.createDataStartingFrom(tempDate,tempDelta);

    }

    /**
     * Create data starting from aLocalDate with aDelta
     * @apram aDate LocalDate
     * @param aDelta long
     * @return LocalDate
     */
    private LocalDate createDataStartingFrom(LocalDate aDate,
                                             long aDelta) {

        long    tempBaseValue;


        tempBaseValue = this.toEpochInMilliseconds(aDate);
        return this.toLocalDateFrom(tempBaseValue + aDelta);

    }


    /**
     * Answer the epoch for aLocalDate
     * @param aLocalDate LocalDate
     * @return long
     */
    private long toEpochInMilliseconds(LocalDate aLocalDate)
    {
        return  aLocalDate.atStartOfDay(ZoneId.systemDefault()).toEpochSecond();

    }

    /**
     * Answer a local date given an epoch
     * @param anEpoch long
     * @return LocalDate
     */
    private LocalDate toLocalDateFrom(long anEpoch) {

        return Instant.ofEpochMilli(anEpoch).
                    atZone(ZoneId.systemDefault()).toLocalDate();

    }


}
