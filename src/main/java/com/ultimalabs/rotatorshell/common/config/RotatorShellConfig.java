package com.ultimalabs.rotatorshell.common.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;

/**
 * Configuration loaded from appplication.yml
 */
@Data
@Validated
@ConstructorBinding
@ConfigurationProperties("rotatorshell")
public class RotatorShellConfig {

    /**
     * Rotctld host address
     */
    @NotEmpty
    private final String rotctldHost;

    /**
     * Rotctld port
     */
    @Min(value = 1, message = "Invalid rotctldPort - minimum value is 1")
    @Max(value = 65535, message = "Invalid rotctldPort - maximum value is 65535")
    private final int rotctldPort;

    /**
     * Wait time (millis) between two rotator position checks
     */
    @Min(value = 100, message = "Invalid setPosWaitStep - minimum value is 100 millis")
    private final int setPosWaitStep;

    /**
     * After how many consecutive checks do we declare that the rotator stopped
     */
    @Min(value = 2, message = "Invalid setPosWaitNumSteps - minimum value is 2")
    private final int setPosWaitNumSteps;

    /**
     * Timeout (seconds) for the rotator to set into position
     */
    @Min(value = 1, message = "Invalid setPosWaitTimeout - minimum value is 1 second")
    @Max(value = 300, message = "Invalid setPosWaitTimeout - maximum value is 300 seconds")
    private final int setPosWaitTimeout;

}
