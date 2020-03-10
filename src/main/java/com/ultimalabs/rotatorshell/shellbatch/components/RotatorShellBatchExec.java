package com.ultimalabs.rotatorshell.shellbatch.components;

import com.ultimalabs.rotatorshell.common.config.ShellHelper;
import com.ultimalabs.rotatorshell.common.service.RotctldClientService;
import com.ultimalabs.rotatorshell.shellbatch.model.BatchInputDataPoint;
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

        List<BatchInputDataPoint> records = CsvUtil.readAzElData(inputCsvFile);

        if (records.isEmpty()) {
            return shellHelper.getErrorMessage("Error reading input file.");
        }

        shellHelper.printInfo("Input data points: " + records.size());

        return shellHelper.getSuccessMessage("Done.");

    }


}
