package com.ultimalabs.rotatorshell.common.model;

import lombok.Data;

/**
 * Batch output data point - azimuth, elevation, begin, end
 */
@Data
public class BatchOutputDataPoint {

    private final AzimuthElevation azimuthElevation;
    private final long epochBeginMillis;
    private final long epochEndMillis;

}
