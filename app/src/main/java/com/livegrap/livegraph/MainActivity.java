package com.livegrap.livegraph;

import android.databinding.DataBindingUtil;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.livegrap.livegraph.databinding.ActivityMainBinding;
import com.livegrap.livegraph.modal.PulseMeasurement;
import com.livegrap.livegraph.modal.PulseMeasurements;
import com.livegrap.livegraph.view.LiveGraphView;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    double random;

    DateTime fakeNow = DateTime.parse("01/01/2019 10:00:20",
            DateTimeFormat.forPattern("dd/MM/yyyy HH:mm:ss"));

    int fakeDateCounter = 2;
//
    private Runnable updateRunnable = new Runnable() {
        @Override
        public void run() {

            PulseMeasurement pulseMeasurement  = new PulseMeasurement(fakeNow.plusSeconds(fakeDateCounter),70);
            binding.graph.appendMeasurementValue(pulseMeasurement);
            fakeDateCounter += 2;

//            random = Math.random();
//            Date date = Calendar.getInstance().getTime();
//
////            binding.graph.appendValue(new LiveGraphView.GraphValuePulseMeasurement();
            binding.graph.postDelayed(updateRunnable, (long) (1500 + 1000 * Math.random()));
//            binding.currentValue.setText(String.format("Current value: %s", random * 2000));
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        
    }

    @Override
    protected void onStart() {
        super.onStart();

        final List<PulseMeasurement> pulseMeasurements = PulseMeasurements.createPulseMeasurements();

        binding.graph.appendMeasurementValues((ArrayList<PulseMeasurement>) pulseMeasurements);


//        for (int i = 0; i < pulseMeasurements.size(); i++) {
//            binding.graph.appendMeasurementValue(pulseMeasurements.get(i));
//        }

    }

    @Override
    public void onResume() {
        super.onResume();
        binding.graph.postDelayed(updateRunnable, 2000);
    }

    @Override
    protected void onPause() {
        super.onPause();
        binding.graph.removeCallbacks(updateRunnable);
    }


}
