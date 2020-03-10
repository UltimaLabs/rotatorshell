package com.ultimalabs.rotatorshell.shellbatch.model;

import com.ultimalabs.rotatorshell.common.model.AzimuthElevation;
import lombok.Data;

/**
 * Batch input data point - azimuth, elevation and duration
 */
@Data
public class BatchInputDataPoint {

    private final AzimuthElevation azimuthElevation;
    private final int duration;

}
