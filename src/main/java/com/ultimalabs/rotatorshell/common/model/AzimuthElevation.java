package com.ultimalabs.rotatorshell.common.model;

import com.ultimalabs.rotatorshell.common.util.AzimuthElevationUtil;
import lombok.Data;

/**
 * Stores azimuth/elevation data pairs
 * <p>
 * The angles are normalized in 0 to 360 range,
 * with 360 not included.
 */
@Data
public class AzimuthElevation {

    /**
     * Azimuth
     */
    private final int azimuth;

    /**
     * Elevation
     */
    private final int elevation;

    /**
     * Constructor
     *
     * @param az azimuth
     * @param el elevation
     */
    public AzimuthElevation(int az, int el) {
        this.azimuth = AzimuthElevationUtil.normalizeAngle(az);
        this.elevation = AzimuthElevationUtil.normalizeAngle(el);
    }

    /**
     * Constructor for arguments of the type double
     *
     * @param az azimuth
     * @param el elevation
     */
    public AzimuthElevation(double az, double el) {
        this.azimuth = AzimuthElevationUtil.normalizeAngle((int) Math.round(az));
        this.elevation = AzimuthElevationUtil.normalizeAngle((int) Math.round(el));
    }

    @Override
    public String toString()
    {
        return "Az: " + getAzimuth() + ", El: " + getElevation();
    }
}
