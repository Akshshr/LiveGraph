package com.livegrap.livegraph.modal;

import org.joda.time.DateTime;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Random;

public class PulseMeasurements {

    private ArrayList<PulseMeasurement> pulseMeasurements = new ArrayList<>();

    public ArrayList<PulseMeasurement> getPulseMeasurements(DateTime fromTimestamp , DateTime toTimestamp) {
        return pulseMeasurements;
    }

    public PulseMeasurements(ArrayList<PulseMeasurement> pulseMeasurements) {
        this.pulseMeasurements = pulseMeasurements;
    }

    public static List<PulseMeasurement> createPulseMeasurements() {
        List<PulseMeasurement> measurements = new ArrayList<>();
//        for (int i = 1; i < 200; i++) {
//            measurements.add(new PulseMeasurement(DateTime.now().minusSeconds(i), Math.random() * i));
//        }

        measurements.add(new PulseMeasurement(DateTime.now().minusSeconds(0), 100));
        measurements.add(new PulseMeasurement(DateTime.now().minusSeconds(1), 55));
        measurements.add(new PulseMeasurement(DateTime.now().minusSeconds(2), 75));
        measurements.add(new PulseMeasurement(DateTime.now().minusSeconds(3), 76));
        measurements.add(new PulseMeasurement(DateTime.now().minusSeconds(4), 76));
        measurements.add(new PulseMeasurement(DateTime.now().minusSeconds(5), 100));
        measurements.add(new PulseMeasurement(DateTime.now().minusSeconds(6), 20));
        measurements.add(new PulseMeasurement(DateTime.now().minusSeconds(7), 30));
        measurements.add(new PulseMeasurement(DateTime.now().minusSeconds(8), 40));
        measurements.add(new PulseMeasurement(DateTime.now().minusSeconds(9), 60));
        measurements.add(new PulseMeasurement(DateTime.now().minusSeconds(10), 50));

        return measurements;
    }


}
