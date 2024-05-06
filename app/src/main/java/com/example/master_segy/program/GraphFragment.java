package com.example.master_segy.program;

import android.graphics.RectF;
import android.os.Bundle;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;


import android.text.Editable;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckedTextView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;


import com.example.master_segy.R;
import com.example.master_segy.data.traceP.Trace;
import com.example.master_segy.databinding.FragmentGraphBinding;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;


import java.util.ArrayList;


public class GraphFragment extends Fragment {
    private LineChart lineChart;
    TextView btnViewFilter, btnViewBorder, btnRotation, tvLength;
    EditText editMarker1, editMarker2, editTextSpeed, editTextAmplitude, editTextGain;
    LinearLayout tableLayoutBorder, layoutRez, linearLayoutFilter;
    ArrayList<Trace> trails;
    private FragmentGraphBinding binding;
    LinearLayout linearLayout;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentGraphBinding.inflate(getLayoutInflater());
        View rootView = binding.getRoot();
        lineChart = binding.lineChart;
        lineChart.getLegend().setEnabled(false);
        lineChart.getAxisLeft().setDrawLabels(false);
        lineChart.getAxisRight().setDrawLabels(false);
        lineChart.getDescription().setEnabled(false);
        lineChart.setDrawGridBackground(false);
        lineChart.setTouchEnabled(true);
        lineChart.setDragEnabled(true);
        lineChart.setScaleEnabled(true);
        lineChart.setDrawGridBackground(false);
        lineChart.setPinchZoom(true);
        lineChart.animateX(3000);
        btnViewFilter = binding.textViewFilter;
        btnRotation = binding.textViewRotation;
        editMarker1 = binding.editTextNumberDecimalMareker1;
        layoutRez = binding.layoutRez;
        editMarker2 = binding.editTextNumberDecimalMarker2;
        editTextSpeed = binding.editTextSpeedRez;
        editTextSpeed.setText(String.valueOf(4000));
        tvLength = binding.textViewLengthRez;
        editTextAmplitude = binding.editTextNumberAmplitude;
        editTextAmplitude.setText(String.valueOf(1));
        editTextGain = binding.editTextNumberGain;
        editTextGain.setText(String.valueOf(1));
        linearLayout = binding.layoutG;
        lineChart.setDragEnabled(true);
        btnViewBorder = binding.textViewBorder;
        linearLayoutFilter = binding.layoutFilter;
        tableLayoutBorder = binding.layoutBorder;
        CheckedTextView textView_dcRemoval = binding.dcRemoval;
        CheckedTextView textView_autoStaticCorrection = binding.autoStaticCorrection;
        CheckedTextView textView_amplitudeCorrection = binding.amplitudeCorrection;
        CheckedTextView textView_Gain = binding.gain;
        textView_dcRemoval.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                textView_dcRemoval.setChecked(!textView_dcRemoval.isChecked());
                drawGraph(textView_dcRemoval.isChecked(), textView_autoStaticCorrection.isChecked(), textView_amplitudeCorrection.isChecked(), textView_Gain.isChecked());
            }
        });

        textView_autoStaticCorrection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                textView_autoStaticCorrection.setChecked(!textView_autoStaticCorrection.isChecked());
                drawGraph(textView_dcRemoval.isChecked(), textView_autoStaticCorrection.isChecked(), textView_amplitudeCorrection.isChecked(), textView_Gain.isChecked());
            }
        });
        textView_amplitudeCorrection.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                textView_amplitudeCorrection.setChecked(!textView_amplitudeCorrection.isChecked());

                if (textView_amplitudeCorrection.isChecked()) {
                    editTextAmplitude.setVisibility(View.VISIBLE);
                } else {
                    editTextAmplitude.setVisibility(View.GONE);
                }
                drawGraph(textView_dcRemoval.isChecked(), textView_autoStaticCorrection.isChecked(), textView_amplitudeCorrection.isChecked(), textView_Gain.isChecked());
            }

        });
        textView_Gain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                textView_Gain.setChecked(!textView_Gain.isChecked());

                if (textView_Gain.isChecked()) {
                    editTextGain.setVisibility(View.VISIBLE);
                } else {
                    editTextGain.setVisibility(View.GONE);
                }
                drawGraph(textView_dcRemoval.isChecked(), textView_autoStaticCorrection.isChecked(), textView_amplitudeCorrection.isChecked(), textView_Gain.isChecked());
            }
        });
        editTextAmplitude.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!s.toString().isEmpty()) {
                    drawGraph(textView_dcRemoval.isChecked(), textView_autoStaticCorrection.isChecked(), textView_amplitudeCorrection.isChecked(), textView_Gain.isChecked());
                }
                else {
                    editTextAmplitude.setText("1");
                }
            }
        });
        editTextGain.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!s.toString().isEmpty()) {
                    drawGraph(textView_dcRemoval.isChecked(), textView_autoStaticCorrection.isChecked(), textView_amplitudeCorrection.isChecked(), textView_Gain.isChecked());
                }
                else {
                    editTextGain.setText("1");
                }
            }
        });
        btnViewFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isShown = linearLayoutFilter.getVisibility() == View.VISIBLE;
                linearLayoutFilter.setVisibility(isShown ? View.GONE : View.VISIBLE);
            }
        });
        btnViewBorder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isShown = tableLayoutBorder.getVisibility() == View.VISIBLE;
                tableLayoutBorder.setVisibility(isShown ? View.GONE : View.VISIBLE);
            }
        });

        if (lineChart.getData() == null) {
            btnViewFilter.setVisibility(View.GONE);
            btnViewBorder.setVisibility(View.GONE);
            layoutRez.setVisibility(View.GONE);
        }
        editTextSpeed.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!s.toString().isEmpty()) {
                    tvLength.setText(String.format("%.2f", searchLength()));
                }
                else {
                    editTextSpeed.setText("0");
                }
            }
        });
        TextWatcher textWatcherMarker = new TextWatcher() {

            float oldPosition;

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                if (!s.toString().isEmpty()) {

                    oldPosition = Float.parseFloat(s.toString());
                    for (LimitLine limitLine : lineChart.getXAxis().getLimitLines()) {
                        if (limitLine.getLimit() == oldPosition) {
                            lineChart.getXAxis().removeLimitLine(limitLine);
                            lineChart.invalidate();
                            break;
                        }
                    }
                }
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!s.toString().isEmpty()) {

                    float newPos = Float.parseFloat(s.toString());
                    LimitLine limitLine = new LimitLine(newPos);
                    limitLine.setLineWidth(1f);
                    lineChart.getXAxis().addLimitLine(limitLine);
                    lineChart.invalidate();
                    tvLength.setText(String.format("%.2f", searchLength()));
                }
            }
        };
        editMarker1.addTextChangedListener(textWatcherMarker);
        editMarker2.addTextChangedListener(textWatcherMarker);
        btnRotation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isShown = lineChart.getRotation() == 90;
                int angle;
                if (isShown) {
                    angle=0;
                }
                else
                    angle=90;
                lineChart.setRotation(angle);
                lineChart.requestLayout();
            }
        });
        tvLength.setText("0");

        return rootView;
    }

    double searchLength() {
        double marker1, marker2, speed;
        if(!editMarker1.getText().toString().isEmpty() && !editMarker2.getText().toString().isEmpty() ) {
            marker1 = Double.parseDouble(editMarker1.getText().toString());
            marker2 = Double.parseDouble(editMarker2.getText().toString());
            speed = Double.parseDouble(editTextSpeed.getText().toString());
            if (marker1 > marker2)
                return ((marker1 * speed) - (marker2 * speed)) / 2;
            return ((marker2 * speed) - (marker1 * speed)) / 2;
        }
        return 0;
    }

    public void setTrails(ArrayList<Trace> X) {
        trails = X;
        drawGraph(false, false, false, false);
    }

    private void drawGraph(boolean dcRemoval, boolean autoStaticCorrection, boolean amplitudeCorr, boolean gainCorr) {
        lineChart.clear();
        if(trails.size()!=0) {
            ArrayList<ILineDataSet> dataSets = new ArrayList<>();
            double minT = 0, maxT = 0;
            double max_0 = searchMax(trails.get(0).get_signal());
            double id_max_0 = indexOf(trails.get(0).get_signal(), max_0);
            for (Trace trace : trails) {

                minT += 0 / trace.get_FS();
                double dcRem = 0, gain;
                ArrayList<Entry> entries = new ArrayList<>();
                double[] x_mass = trace.get_signal();
                if (dcRemoval)
                    dcRem = searchAvg(x_mass);
                if (gainCorr)
                    gain = Double.parseDouble(editTextGain.getText().toString());
                else {
                    gain = 1;
                }
                double max = searchMax(x_mass);
                int id = indexOf(x_mass, max);
                double razn = id_max_0 - id;
                double x = 0;
                for (int i = 0; i < x_mass.length; i++) {
                    double y = (x_mass[i] - dcRem) * gain;
                    x = i / trace.get_FS();
                    if (autoStaticCorrection)
                        x = (i + razn) / trace.get_FS();
                    if (amplitudeCorr)
                        y = y * Math.exp(x * Double.parseDouble(editTextAmplitude.getText().toString()));
                    y = (y / max) + trails.indexOf(trace);
                    entries.add(new Entry((float) x, (float) y));
                }
                maxT += x;
                LineDataSet ld = new LineDataSet(entries, trace.getTitle_trace());
                ld.setDrawCircles(false);
                ld.setColor(R.color.black);
                ld.setDrawFilled(false);
                dataSets.add(ld);
            }
            InputFilter[] filters = new InputFilter[1];
            filters[0] = new InputFilterMinMax(minT / trails.size(), maxT / trails.size());
            editMarker2.setFilters(filters);
            editMarker1.setFilters(filters);

            LineData data = new LineData(dataSets);
            lineChart.setData(data);
            btnViewFilter.setVisibility(View.VISIBLE);
            btnViewBorder.setVisibility(View.VISIBLE);
            layoutRez.setVisibility(View.VISIBLE);
            lineChart.invalidate();
            editMarker1.setText(editMarker1.getText());
            editMarker2.setText(editMarker2.getText());
        }
    }
    public static int indexOf(double [] mass, double value) {
        for (int i = 0; i < mass.length; i++) {
            if (mass[i]==value) {
                return i;
            }
        }
        return -1; // элемент не найден
    }

    double searchMax(double[] mass) {
        double max = mass[0];
        for (int i = 1; i < mass.length; i++) {
            if (mass[i] > max) {
                max = mass[i];
            }
        }
        return max;
    }

    double searchAvg(double[] mass) {
        double sum = 0;
        for (int i = 1; i < mass.length; i++) {
            sum += mass[i];
        }
        return sum / mass.length;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }


    public class InputFilterMinMax implements InputFilter {

        private double minValue;
        private double maxValue;

        public InputFilterMinMax(double minValue, double maxValue) {
            this.minValue = minValue;
            this.maxValue = maxValue;
        }

        @Override
        public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
            try {
                double input = Double.parseDouble(dest.toString() + source.toString());
                if (isInRange(minValue, maxValue, input)) {
                    return null;
                }
            } catch (NumberFormatException e) {
            }
            return "";
        }

        private boolean isInRange(double a, double b, double c) {
            return b > a ? c >= a && c <= b : c >= b && c <= a;
        }
    }

    public double getMarkerOne() {
        return Double.parseDouble(editMarker1.getText().toString());
    }

    public double getMarkerTwo() {
        return Double.parseDouble(editMarker2.getText().toString());
    }

    public double getSpeed() {
        return Double.parseDouble(editTextSpeed.getText().toString());
    }

    public double getLength() {
        return Double.parseDouble(tvLength.getText().toString().replace(',', '.'));
    }

    public void setMarkerOne(double markerOne) {
        editMarker1.setText(String.valueOf(markerOne));
    }

    public void setMarkerTwo(double markerTwo) {
        editMarker2.setText(String.valueOf(markerTwo));
    }

    public void setSpeed(double speed) {
        editTextSpeed.setText(String.valueOf(speed));
    }

}