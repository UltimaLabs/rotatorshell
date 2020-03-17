package com.ultimalabs.rotatorshell.shellbatch.components;

import com.ultimalabs.rotatorshell.common.config.ShellHelper;
import com.ultimalabs.rotatorshell.common.model.BatchInputDataPoint;
import com.ultimalabs.rotatorshell.common.model.BatchOutputDataPoint;
import com.ultimalabs.rotatorshell.common.model.BatchOutputParameters;
import com.ultimalabs.rotatorshell.common.service.RotctldClientService;
import com.ultimalabs.rotatorshell.shellbatch.util.CsvUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;

import java.util.List;

@ShellComponent
@RequiredArgsConstructor
public class RotatorShellBatchExec {

    private final RotctldClientService rotctldClientService;
    private final ShellHelper shellHelper;

    @ShellMethod(value = "Input az/el/duration data points from a CSV file (RFC 4180), set the rotator position accordingly and output a timestamped CSV.", key = {"csv-in"})
    public String batchFromCsv(
            @ShellOption(value = {"-in"}, help = "Input CSV file name") String inputCsvFile,
            @ShellOption(value = {"-out"}, help = "Output CSV file name") String outputCsvFile
    ) {

        List<BatchInputDataPoint> inputDataPoints = CsvUtil.readAzElData(inputCsvFile);

        if (inputDataPoints.isEmpty()) {
            return shellHelper.getErrorMessage("Error reading input file.");
        }

        shellHelper.printInfo("Input data points: " + inputDataPoints.size() + "\nWorking, please wait...");
        List<BatchOutputDataPoint> outputDataPoints = rotctldClientService.batchSetAzEl(inputDataPoints);

        if (outputDataPoints.isEmpty()) {
            return shellHelper.getErrorMessage("No output data was returned.");
        }

        boolean success = CsvUtil.writeOutputAzElData(outputDataPoints, outputCsvFile);

        if (success) {
            return shellHelper.getSuccessMessage("Written output file: " + outputCsvFile);
        } else {
            return shellHelper.getErrorMessage("Error writing output file.");
        }

    }

    @ShellMethod(value = "Generate file with az/el/duration data points, suitable as input for 'csv-in' command.", key = {"csv-out"})
    public String generateInputCsv(
            @ShellOption(value = {"-af", "--azimuth-from"}, help = "Azimuth from") int azFrom,
            @ShellOption(value = {"-at", "--azimuth-to"}, help = "Azimuth to, must be larger than or equal to 'Azimuth from'") int azTo,
            @ShellOption(value = {"-ef", "--elevation-from"}, help = "Elevation from") int elFrom,
            @ShellOption(value = {"-et", "--elevation-to"}, help = "Elevation to, must be larger than or equal to 'Alevation from'") int elTo,
            @ShellOption(value = {"-d", "--duration"}, help = "Duration [sec] at each az/el position") int duration,
            @ShellOption(value = {"-m", "--mode"}, help = "Mode: 'afr' (azimuth first) or 'efr' (elevation first)") String mode,
            @ShellOption(value = {"-f", "--file"}, help = "Output CSV file name") String outputFileName
    ) {

        BatchOutputParameters batchOutputParameters;

        try {
            batchOutputParameters = new BatchOutputParameters(azFrom, azTo, elFrom, elTo, mode, duration);
        } catch (IllegalArgumentException ex) {
            return shellHelper.getErrorMessage("Invalid parameter(s): " + ex.getMessage());
        }

        boolean success = CsvUtil.generateBatchData(batchOutputParameters, outputFileName);

        if (success) {
            return shellHelper.getSuccessMessage("Written output file: " + outputFileName);
        } else {
            return shellHelper.getErrorMessage("Error writing output file.");
        }

    }

}
