package com.ultimalabs.rotatorshell.common.model;

import lombok.Data;

/**
 * Batch output data point - azimuth, elevation, begin, end
 */
@Data
public class BatchOutputDataPoint {

    /**
     * Azimuth/elevation pair
     */
    private final AzimuthElevation azimuthElevation;

    /**
     * Begin epoch, in milliseconds
     */
    private final long epochBeginMillis;

    /**
     * End epoch, in milliseconds
     */
    private final long epochEndMillis;

}
