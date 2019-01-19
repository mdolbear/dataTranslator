package com.mjdsft.datagenerator;

import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.text.MessageFormat;

import static junit.framework.TestCase.assertTrue;

public class DataCSVFileCreatorTest {

    //Constants
    private static final String TEMPORARY_DIRECTORY = "/tmp/generatedData/";
    private static final String TEMPORARY_DATA_FILE = TEMPORARY_DIRECTORY + "file{0}.csv";
    private static final int    NUMBER_OF_FILES = 10;


    /**
     * Test data creation.
     */
    @Test
    public void csvFileDataCreationTest() throws IOException {

        File                tempFile;
        final               int tempCount = 1000;
        String              tempFilename;
        DataCSVFileCreator  tempCreator = new DataCSVFileCreator();

        this.createDirectoryIfNotExist(TEMPORARY_DIRECTORY);
        for (int i = 0; i < NUMBER_OF_FILES; i++) {

            tempFilename = MessageFormat.format(TEMPORARY_DATA_FILE, i);
            tempFile =
                this.createNewTemporaryFile(tempFilename);

            tempCreator.createRandomizedDataCSVFile(tempFile.toPath(), tempCount);

            assertTrue("File does not exist", tempFile.exists() &&
                                (this.getCountOfLinesInFile(tempFile)
                                         == tempCount));


        }


    }

    /**
     * Create temporary directory if it does not exist
     * @param aDirectoryPath String
     */
    protected void createDirectoryIfNotExist(String aDirectoryPath) {

        File    tempDir;

        tempDir = new File(aDirectoryPath);
        if (!tempDir.exists()) {

            tempDir.mkdir();
        }

    }

    /**
     * Create new temporary file
     * @return File
     */
    private File createNewTemporaryFile(String aFilename) throws IOException {

        File    tempFile;

        tempFile = new File(aFilename);
        if (tempFile.exists()) {

            tempFile.delete();
        }

        tempFile.createNewFile();

        return tempFile;

    }

    /**
     * Answer a coun t of lines in aFile
     * @param aFile File
     * @return int
     */
    private long getCountOfLinesInFile(File aFile) throws IOException {

        return Files.lines(aFile.toPath()).count();
    }

}
