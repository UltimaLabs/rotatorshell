package com.ultimalabs.rotatorshell.common.service;

import com.ultimalabs.rotatorshell.common.config.RotatorShellConfig;
import com.ultimalabs.rotatorshell.common.model.AzimuthElevation;
import com.ultimalabs.rotatorshell.common.model.BatchInputDataPoint;
import com.ultimalabs.rotatorshell.common.model.BatchOutputDataPoint;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

/**
 * Client service for rotctld
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class RotctldClientService {

    private final RotatorShellConfig config;

    private Socket clientSocket;
    private PrintWriter out;
    private BufferedReader in;

    /**
     * Set rotator azimuth & elevation
     *
     * @param azEl newly set azimuth/elevation
     * @param handleConnection should the method open/close the TCP connection
     * @return new az/el, as reported by the rotator
     */
    public AzimuthElevation setAzEl(AzimuthElevation azEl, boolean handleConnection) {

        if (handleConnection) {
            startConnection();
        }

        String returnMessage = sendMessage(",\\set_pos " + azEl.getAzimuth() + " " + azEl.getElevation());

        if (isInvalidResponse(returnMessage)) {
            log.error("Rotctld failed executing setAzEl() command. Response: {}", returnMessage);
            stopConnection();
            return null;
        }

        AzimuthElevation currentAzEl = waitForRotator(handleConnection);

        if (handleConnection) {
            stopConnection();
        }

        return currentAzEl;

    }

    /**
     * Get rotator azimuth & elevation
     *
     * @param handleConnection should the method open/close the TCP connection
     * @return azimuth & elevation
     */
    public AzimuthElevation getAzEl(boolean handleConnection) {

        if (handleConnection) {
            startConnection();
        }
        String returnMessage = sendMessage(",\\get_pos");
        if (handleConnection) {
            stopConnection();
        }

        if (isInvalidResponse(returnMessage)) {
            log.error("Rotctld failed executing getAzEl() command. Response: {}", returnMessage);
            return null;
        }

        String[] parts = returnMessage.split(",");

        return new AzimuthElevation(Double.parseDouble(parts[1].replace("Azimuth: ", "")),
                Double.parseDouble(parts[2].replace("Elevation: ", "")));

    }

    /**
     * Batch set rotator azimuth/elevation
     *
     * @param inputDataPoints list of input data points - az/el, duration at that az/el
     * @return list of output data points - az/el, begin timestamp, end timestamp
     */
    public List<BatchOutputDataPoint> batchSetAzEl(List<BatchInputDataPoint> inputDataPoints) {

        List<BatchOutputDataPoint> outputDataPoints = new ArrayList<>();

        startConnection();

        for (BatchInputDataPoint dataPoint : inputDataPoints) {

            AzimuthElevation azEl = dataPoint.getAzimuthElevation();
            int duration = dataPoint.getDuration();

            if (duration < 1) {
                duration = 1;
            }

            AzimuthElevation currentPosition = setAzEl(azEl, false);
            if (currentPosition == null) {
                continue;
            }

            try {

                long beginTimestamp = Instant.now().toEpochMilli();
                Thread.sleep((long) duration * 1000);
                long endTimestamp = Instant.now().toEpochMilli();
                outputDataPoints.add(new BatchOutputDataPoint(currentPosition, beginTimestamp, endTimestamp));

            } catch (InterruptedException e) {
                log.error("Interrupted exception: {}", e.getMessage());
            }

        }

        stopConnection();

        return outputDataPoints;

    }

    /**
     * Wait for rotator to complete the setAzEl() command
     *
     * @param handleConnection should the method open/close the TCP connection
     * @return newly set azimuth/elevation
     */
    private AzimuthElevation waitForRotator(boolean handleConnection) {

        int setPosWaitStep = config.getSetPosWaitStep();
        int setPosWaitNumSteps = config.getSetPosWaitNumSteps();
        long setPosDeadline = Instant.now().toEpochMilli() / 1000 + config.getSetPosWaitTimeout();

        AzimuthElevation prevPosition = null;
        AzimuthElevation currentPosition = null;
        int numStopSteps = 0;
        long now = Instant.now().toEpochMilli() / 1000;

        while (now < setPosDeadline && numStopSteps < setPosWaitNumSteps) {
            try {

                Thread.sleep(setPosWaitStep);

                currentPosition = getAzEl(handleConnection);

                if (prevPosition != null && prevPosition.equals(currentPosition)) {
                    numStopSteps++;
                } else {
                    numStopSteps = 0;
                }

                prevPosition = currentPosition;
                now = Instant.now().toEpochMilli() / 1000;

            } catch (InterruptedException e) {
                log.error("Interrupted exception: {}", e.getMessage());
            }
        }

        if (numStopSteps < setPosWaitNumSteps) {
            log.error("Timeout reached while waiting for setAzEl() to complete.");
            return null;
        }

        return currentPosition;
    }

    /**
     * Check whether rotctld returned a successful response
     *
     * @param message string returned from rotctld
     * @return whether response indicates a success
     */
    private boolean isInvalidResponse(String message) {
        if (message == null) {
            return true;
        }
        return !(message.contains(",") && message.contains("RPRT 0"));
    }

    /**
     * Open a connection to rotctld
     */
    private void startConnection() {
        try {
            clientSocket = new Socket(config.getRotctldHost(), config.getRotctldPort());
        } catch (IOException e) {
            log.error("Error connecting to rotctld host: {}", e.getMessage());
            return;
        }
        try {
            out = new PrintWriter(clientSocket.getOutputStream(), true);
        } catch (IOException e) {
            log.error("Error getting output stream for rotctld host: {}", e.getMessage());
            return;
        }
        try {
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        } catch (IOException e) {
            log.error("Error getting input stream for rotctld host: {}", e.getMessage());
        }
    }

    /**
     * Send a message over the socket
     *
     * @param msg message to be sent
     * @return return message from rotctld
     */
    private String sendMessage(String msg) {

        if (out == null || in == null) {
            return null;
        }

        out.println(msg);
        String resp = null;
        try {
            resp = in.readLine();
        } catch (IOException e) {
            log.error("Error reading response from rotctld host: {}", e.getMessage());
        }
        return resp;
    }

    /**
     * Close the connection
     */
    private void stopConnection() {

        if (in != null) {
            try {
                in.close();
            } catch (IOException e) {
                log.error("Error closing socket reader: {}", e.getMessage());
            }
        }

        if (out != null) {
            out.close();
        }

        if (clientSocket != null) {
            try {
                clientSocket.close();
            } catch (IOException e) {
                log.error("Error closing socket: {}", e.getMessage());
            }
        }
    }

}