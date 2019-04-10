package com.livegrap.livegraph;

import android.databinding.DataBindingUtil;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.livegrap.livegraph.databinding.ActivityMainBinding;
import com.livegrap.livegraph.modal.PulseMeasurement;
import com.livegrap.livegraph.modal.PulseMeasurements;
import com.livegrap.livegraph.view.LiveGraphView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    double random;

//
//    private Runnable updateRunnable = new Runnable() {
//        @Override
//        public void run() {
//            random = Math.random();
//            Date date = Calendar.getInstance().getTime();
//
////            binding.graph.appendValue(new LiveGraphView.GraphValuePulseMeasurement();
//            binding.graph.postDelayed(updateRunnable, (long) (1500 + 1000 * Math.random()));
//            binding.currentValue.setText(String.format("Current value: %s", random * 2000));
//        }
//    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);



        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);


        Date date = Calendar.getInstance().getTime();
        binding.graph.setDataWindowWidth(40000);
        binding.graph.setData(Arrays.asList(
                new LiveGraphView.GraphValue(date.getTime() - 5 * 2000, 150),
                new LiveGraphView.GraphValue(date.getTime() - 4 * 2000, 140),
                new LiveGraphView.GraphValue(date.getTime() - 3 * 2000, 200),
                new LiveGraphView.GraphValue(date.getTime() - 2 * 2000, 220),
                new LiveGraphView.GraphValue(date.getTime() - 2000, 150),
                new LiveGraphView.GraphValue(date.getTime(), 200)
        ));
        for (int i = 0; i < 1000; i++) {
            new LiveGraphView.GraphValue(date.getTime() - i * 2000, 150);
        }
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
//        binding.graph.postDelayed(updateRunnable, 2000);
    }

    @Override
    protected void onPause() {
        super.onPause();
//        binding.graph.removeCallbacks(updateRunnable);
    }


}
