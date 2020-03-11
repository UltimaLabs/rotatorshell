package com.ultimalabs.rotatorshell.shellbatch.util;

import com.ultimalabs.rotatorshell.common.model.AzimuthElevation;
import com.ultimalabs.rotatorshell.common.model.BatchInputDataPoint;
import com.ultimalabs.rotatorshell.common.model.BatchOutputDataPoint;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;

import java.io.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * CSV input/output utility class
 */
@Slf4j
public class CsvUtil {

    private static final String AZIMUTH = "azimuth";
    private static final String ELEVATION = "elevation";
    private static final String DURATION = "duration";

    private CsvUtil() {
        throw new IllegalStateException("Utility class");
    }

    /**
     * Parse input batch CSV file
     *
     * @param inputCsvFileName input CSV file name
     * @return list of input data points
     */
    public static List<BatchInputDataPoint> readAzElData(String inputCsvFileName) {

        List<BatchInputDataPoint> recordsFiltered = new ArrayList<>();

        try {

            Reader in = new FileReader(inputCsvFileName);
            Iterable<CSVRecord> recordsRaw = CSVFormat.RFC4180.withFirstRecordAsHeader().parse(in);

            for (CSVRecord record : recordsRaw) {

                if (record.isSet(AZIMUTH) && record.isSet(ELEVATION) && record.isSet(DURATION)) {
                    AzimuthElevation azEl = new AzimuthElevation(Integer.parseInt(record.get(AZIMUTH)), Integer.parseInt(record.get(ELEVATION)));
                    int duration = Integer.parseInt(record.get(DURATION));
                    recordsFiltered.add(new BatchInputDataPoint(azEl, duration));
                }

            }

        } catch (FileNotFoundException e) {
            log.error("Input file not found: {} ", inputCsvFileName);
        } catch (IOException e) {
            log.error("There was an error reading input CSV data.");
        }

        return recordsFiltered;

    }

    /**
     * Writes results of a rotator batch set to a CSV file
     *
     * @param outputDataPoints output data points - az/el, begin timestamp, end timestamp
     * @param outputFileName output file name
     * @return true on success
     */
    public static boolean writeOutputAzElData(List<BatchOutputDataPoint> outputDataPoints, String outputFileName) {

        FileWriter writer;

        try {
            writer = new FileWriter(outputFileName);
        } catch (IOException e) {
            log.error("There was an error writing output data to file: {}", outputFileName);
            return false;
        }

        try (CSVPrinter csvPrinter = new CSVPrinter(writer, CSVFormat.RFC4180.withHeader(AZIMUTH, ELEVATION, "begin_epoch_millis", "end_epoch_millis", "begin_timestamp", "end_timestamp")))
        {
            for (BatchOutputDataPoint dataPoint : outputDataPoints) {

                int azimuth = dataPoint.getAzimuthElevation().getAzimuth();
                int elevation = dataPoint.getAzimuthElevation().getElevation();
                long beginEpochMillis = dataPoint.getEpochBeginMillis();
                long endEpochMillis = dataPoint.getEpochEndMillis();

                Date beginTime= new Date(beginEpochMillis);
                Date endTime= new Date(endEpochMillis);
                DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss.SSS");
                String beginTimestamp = dateFormat.format(beginTime);
                String endTimestamp = dateFormat.format(endTime);

                csvPrinter.printRecord(azimuth, elevation, beginEpochMillis, endEpochMillis, beginTimestamp, endTimestamp);

            }

            csvPrinter.flush();
            return true;
        } catch (IOException e) {
            log.error("There was an error writing CSV data.");
            return false;
        }
    }

}
