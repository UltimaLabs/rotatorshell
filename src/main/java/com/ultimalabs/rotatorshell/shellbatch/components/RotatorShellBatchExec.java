package com.ultimalabs.rotatorshell.shellbatch.components;

import com.ultimalabs.rotatorshell.common.config.ShellHelper;
import com.ultimalabs.rotatorshell.common.model.BatchOutputDataPoint;
import com.ultimalabs.rotatorshell.common.service.RotctldClientService;
import com.ultimalabs.rotatorshell.common.model.BatchInputDataPoint;
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

    @ShellMethod(value = "Input {az,el,duration} data points from a CSV file (RFC 4180), set the rotator position accordingly and output a timestamped CSV.", key = {"csv-in"})
    public String batchFromCsv(
            @ShellOption(value = {"-in"}, help = "Input CSV file name") String inputCsvFile,
            @ShellOption(value = {"-out"}, help = "Output CSV file name") String outputCsvFile
    ) {

        List<BatchInputDataPoint> inputDataPoints = CsvUtil.readAzElData(inputCsvFile);

        if (inputDataPoints.isEmpty()) {
            return shellHelper.getErrorMessage("Error reading input file.");
        }

        shellHelper.printInfo("Input data points: " + inputDataPoints.size() +"\nWorking, please wait...");
        List<BatchOutputDataPoint> outputDataPoints = rotctldClientService.batchSetAzEl(inputDataPoints);

        if (outputDataPoints.isEmpty()) {
            return shellHelper.getErrorMessage("No output data was returned.");
        }

        boolean success = CsvUtil.writeOutputAzElData(outputDataPoints, outputCsvFile);

        if (success) {
            shellHelper.printInfo("Written output file: " + outputCsvFile);
        } else {
            return shellHelper.getErrorMessage("Error writing output file.");
        }

        return shellHelper.getSuccessMessage("Done.");

    }

}
