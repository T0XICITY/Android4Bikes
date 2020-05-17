package de.thu.tpro.android4bikes.util.ChartsUtil;

import android.graphics.Color;
import android.graphics.Typeface;
import android.view.View;

import androidx.annotation.IdRes;
import androidx.core.content.ContextCompat;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import de.thu.tpro.android4bikes.R;
import de.thu.tpro.android4bikes.util.GlobalContext;
import de.thu.tpro.android4bikes.util.Processor;
import de.thu.tpro.android4bikes.view.MainActivity;

public class ChartsUtil {
    //See: https://medium.com/@leelaprasad4648/creating-linechart-using-mpandroidchart-33632324886d
    LineChart elevationChart;
    private Thread thread;
    private Processor processor = Processor.getInstance();
    private MainActivity mainActivity;
    private float maxY;
    private float minY;

    public void initalizeElevationChart(View view, @IdRes int chartID, MainActivity mainActivity) {
        this.mainActivity = mainActivity;
        elevationChart = view.findViewById(chartID);
        elevationChart.setTouchEnabled(false);
        elevationChart.setPinchZoom(false);

        // enable description text
        elevationChart.getDescription().setEnabled(false);


        // enable scaling and dragging
        elevationChart.setDragEnabled(false);
        elevationChart.setScaleEnabled(false);
        elevationChart.setDrawGridBackground(false);

        // if disabled, scaling can be done on x- and y-axis separately
        elevationChart.setPinchZoom(true);

        // set an alternative background color
        elevationChart.setBackgroundColor(ContextCompat.getColor(GlobalContext.getContext(), R.color.colorPrimaryDark));

        LineData data = new LineData();
        data.setValueTextColor(R.color.TextWhite);

        // add empty data
        elevationChart.setData(data);

        // get the legend (only possible after setting data)
        Legend l = elevationChart.getLegend();

        // modify the legend ...
        l.setForm(Legend.LegendForm.LINE);
        l.setTypeface(Typeface.DEFAULT);
        l.setTextColor(Color.WHITE);

        XAxis xl = elevationChart.getXAxis();
        xl.setTypeface(Typeface.DEFAULT);
        xl.setTextColor(Color.WHITE);
        xl.setDrawGridLines(false);
        xl.setAvoidFirstLastClipping(true);
        xl.setEnabled(false);

        YAxis leftAxis = elevationChart.getAxisLeft();
        leftAxis.setTypeface(Typeface.DEFAULT);
        leftAxis.setTextColor(Color.WHITE);
        minY = 0f;
        maxY = 1000f;
        leftAxis.setAxisMaximum(maxY);
        leftAxis.setAxisMinimum(minY);
        leftAxis.setDrawGridLines(true);

        YAxis rightAxis = elevationChart.getAxisRight();
        rightAxis.setEnabled(false);
    }

    private void addEntry(float value) {

        LineData data = elevationChart.getData();

        if (data != null) {

            ILineDataSet set = data.getDataSetByIndex(0);
            // set.addEntry(...); // can be called as well

            if (set == null) {
                set = createSet();
                data.addDataSet(set);
            }
            if (value > maxY) {
                maxY = value;
                YAxis leftAxis = elevationChart.getAxisLeft();
                leftAxis.setAxisMaximum(maxY + 500);
            }
            if (value < minY) {
                minY = value;
                YAxis leftAxis = elevationChart.getAxisLeft();
                leftAxis.setAxisMinimum(minY);
            }
            data.addEntry(new Entry(set.getEntryCount(), value), 0);
            data.notifyDataChanged();

            // let the chart know it's data has changed
            elevationChart.notifyDataSetChanged();

            // limit the number of visible entries
            elevationChart.setVisibleXRangeMaximum(120);
            // chart.setVisibleYRange(30, AxisDependency.LEFT);

            // move to the latest entry
            elevationChart.moveViewToX(data.getEntryCount());

            // this automatically refreshes the chart (calls invalidate())
            // chart.moveViewTo(data.getXValCount()-7, 55f,
            // AxisDependency.LEFT);
        }
    }

    private LineDataSet createSet() {

        LineDataSet set = new LineDataSet(null, "elevation data");
        set.setAxisDependency(YAxis.AxisDependency.LEFT);
        set.setColor(ContextCompat.getColor(GlobalContext.getContext(), R.color.colorAccent));
        set.setCircleColor(Color.WHITE);
        set.setLineWidth(2f);
        set.setCircleRadius(4f);
        set.setFillAlpha(65);
        set.setFillColor(Color.WHITE);
        set.setHighLightColor(Color.rgb(244, 117, 117));
        set.setValueTextColor(Color.WHITE);
        set.setValueTextSize(9f);
        set.setDrawValues(false);
        set.setDrawCircles(false);
        return set;
    }

    public void feedNewData(float value) {

        if (thread != null)
            thread.interrupt();

        final Runnable runnable = new Runnable() {

            @Override
            public void run() {
                addEntry(value);
            }
        };

        thread = new Thread(new Runnable() {

            @Override
            public void run() {
                // Don't generate garbage runnables inside the loop.
                mainActivity.runOnUiThread(runnable);
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });

        thread.start();
    }
}


