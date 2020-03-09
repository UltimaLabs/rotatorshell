package com.ultimalabs.rotatorshell.simplecommands;

import com.ultimalabs.rotatorshell.common.config.ShellHelper;
import com.ultimalabs.rotatorshell.common.model.AzimuthElevation;
import com.ultimalabs.rotatorshell.common.service.RotctldClientService;
import lombok.RequiredArgsConstructor;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;

@ShellComponent
@RequiredArgsConstructor
public class SimpleCommands {

    private final RotctldClientService rotctldClientService;
    private final ShellHelper shellHelper;

    @ShellMethod(value = "Get rotator azimuth/elevation.", key = {"get", "g"})
    public String getAzEl() {
        AzimuthElevation azimuthElevation = rotctldClientService.getAzEl();

        if (azimuthElevation != null) {
            return shellHelper.getInfoMessage(azimuthElevation.toString());
        }

        return shellHelper.getErrorMessage("There was an error getting azimuth/elevation.");

    }

    @ShellMethod(value = "Set rotator azimuth/elevation.", key = {"set", "s"})
    public String setAzEl(
            @ShellOption(value = {"-az"}, help = "Azimuth") int azimuth,
            @ShellOption(value = {"-el"}, help = "Elevation") int elevation

    ) {
        AzimuthElevation azimuthElevation = new AzimuthElevation(azimuth, elevation);
        AzimuthElevation newAzimuthElevation = rotctldClientService.setAzEl(azimuthElevation);

        if (newAzimuthElevation == null) {
            return shellHelper.getErrorMessage("There was an error setting azimuth/elevation.");
        }

        return shellHelper.getSuccessMessage("Ok. " + newAzimuthElevation);
    }

}

