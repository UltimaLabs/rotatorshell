package com.ultimalabs.rotatorshell.common.model;

import lombok.Data;
import org.springframework.validation.annotation.Validated;

/**
 * Parameters for batch CSV output
 */
@Data
@Validated
public class BatchOutputParameters {

    /**
     * Azimuth from
     */
    private final int azimuthFrom;

    /**
     * Azimuth to
     */
    private final int azimuthTo;

    /**
     * Elevation from
     */
    private final int elevationFrom;

    /**
     * Elevation to
     */
    private final int elevationTo;

    /**
     * Duration [sec] at each az/el position
     */
    private final int duration;

    /**
     * Mode - azimuth first (AFR) or elevation first (EFR)
     */
    private final String mode;

    /**
     * Rotate azimuth first
     */
    public static final String MODE_AZ_FIRST = "AFR";

    /**
     * Rotate elevation first
     */
    public static final String MODE_EL_FIRST = "EFR";

    public BatchOutputParameters(int azimuthFrom, int azimuthTo, int elevationFrom, int elevationTo, String mode, int duration) {

        if (azimuthTo < azimuthFrom) {
            throw new IllegalArgumentException("'Azimuth to' must be larger or equal to 'Azimuth from'.");
        }

        if (elevationTo < elevationFrom) {
            throw new IllegalArgumentException("'Elevation to' must be larger or equal to 'Elevation from'.");
        }

        if (duration < 1) {
            throw new IllegalArgumentException("'Duration' must be larger or equal to 1 second.");
        }

        String modeUpper = mode.toUpperCase();

        if (!(modeUpper.equals(MODE_AZ_FIRST) || modeUpper.equals(MODE_EL_FIRST))) {
            throw new IllegalArgumentException("'Mode' must be either 'AFR' or 'EFR'.");
        }

        this.azimuthFrom = azimuthFrom;
        this.azimuthTo = azimuthTo;
        this.elevationFrom = elevationFrom;
        this.elevationTo = elevationTo;
        this.mode = modeUpper;
        this.duration = duration;

    }

}