package com.ultimalabs.rotatorshell.shellbatch.util;

import com.ultimalabs.rotatorshell.common.model.AzimuthElevation;
import com.ultimalabs.rotatorshell.shellbatch.model.BatchInputDataPoint;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

/**
 * CSV input/output utility class
 */
@Slf4j
public class CsvUtil {

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

                if (record.isSet("azimuth") && record.isSet("elevation") && record.isSet("duration")) {
                    AzimuthElevation azEl = new AzimuthElevation(Integer.parseInt(record.get("azimuth")), Integer.parseInt(record.get("elevation")));
                    int duration = Integer.parseInt(record.get("duration"));
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

}
