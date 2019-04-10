package com.livegrap.livegraph.modal;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;

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

        /*
        for (int i = 1; i < 200; i++) {
            measurements.add(new PulseMeasurement(DateTime.now().minusSeconds(i), Math.random() * i));
        }
        */


        DateTime now = DateTime.parse("01/01/2019 10:00:20",
                DateTimeFormat.forPattern("dd/MM/yyyy HH:mm:ss"));

        measurements.add(new PulseMeasurement(now.minusSeconds(0), 20));
        measurements.add(new PulseMeasurement(now.minusSeconds(2), 55));
        measurements.add(new PulseMeasurement(now.minusSeconds(4), 75));
        measurements.add(new PulseMeasurement(now.minusSeconds(6), 73));
        measurements.add(new PulseMeasurement(now.minusSeconds(8), 70));
        measurements.add(new PulseMeasurement(now.minusSeconds(10), 100));
        measurements.add(new PulseMeasurement(now.minusSeconds(12), 20));
        measurements.add(new PulseMeasurement(now.minusSeconds(14), 30));
        measurements.add(new PulseMeasurement(now.minusSeconds(16), 40));
        measurements.add(new PulseMeasurement(now.minusSeconds(18), 60));
        measurements.add(new PulseMeasurement(now.minusSeconds(20), 50));


        Collections.reverse(measurements);

        return measurements;
    }


}
