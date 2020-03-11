package com.ultimalabs.rotatorshell.common.model;

import lombok.Data;

/**
 * Batch input data point - azimuth, elevation and duration
 */
@Data
public class BatchInputDataPoint {

    private final AzimuthElevation azimuthElevation;
    private final int duration;

}
