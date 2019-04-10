package com.livegrap.livegraph.modal;

import org.joda.time.DateTime;

import java.util.Date;

public class PulseMeasurement {
    private DateTime timestamp;
    private double power;

    public PulseMeasurement(DateTime timestamp, double power) {
        this.timestamp = timestamp;
        this.power = power;
    }

    public DateTime getTimestamp() {
        return timestamp;
    }

    public double getPower() {
        return power;
    }


}
