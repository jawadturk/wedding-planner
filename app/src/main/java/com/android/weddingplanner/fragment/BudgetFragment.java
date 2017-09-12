package com.android.weddingplanner.fragment;

import android.app.Dialog;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.weddingplanner.R;
import com.android.weddingplanner.helper.BudgetManager;
import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.DefaultValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.github.mikephil.charting.utils.MPPointF;

import java.util.ArrayList;

public class BudgetFragment extends Fragment implements OnChartValueSelectedListener {
    protected String[] mParties = new String[]{
            "Party A", "Party B", "Party C", "Party D", "Party E", "Party F", "Party G", "Party H",
            "Party I", "Party J", "Party K", "Party L", "Party M", "Party N", "Party O", "Party P",
            "Party Q", "Party R", "Party S", "Party T", "Party U", "Party V", "Party W", "Party X",
            "Party Y", "Party Z"
    };

    private RecyclerView mRecycler;
    private LinearLayoutManager mManager;
    private View rootview;
    private PieChart mChart;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootview = inflater.inflate(R.layout.fragment_budget, container, false);
        return rootview;

    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        mRecycler = (RecyclerView) rootview.findViewById(R.id.recyclerView_budgetItems);
        mRecycler.setHasFixedSize(true);

        setupRecyclerView();
        setUpChartView();

        rootview.findViewById(R.id.imageButton_editBudget).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showAdjustBudgetValue();
            }
        });
    }

    private void setUpChartView() {
        mChart = (PieChart) rootview.findViewById(R.id.chart1);
        mChart.setUsePercentValues(false);
        mChart.getDescription().setEnabled(false);
        mChart.setExtraOffsets(5, 10, 5, 5);

        mChart.setDragDecelerationFrictionCoef(0.95f);


        mChart.setCenterText(generateCenterSpannableText());

        mChart.setDrawHoleEnabled(true);
        mChart.setHoleColor(Color.WHITE);

        mChart.setTransparentCircleColor(Color.WHITE);
        mChart.setTransparentCircleAlpha(110);

        mChart.setHoleRadius(58f);
        mChart.setTransparentCircleRadius(61f);

        mChart.setDrawCenterText(true);

        mChart.setRotationAngle(0);
        // enable rotation of the chart by touch
        mChart.setRotationEnabled(true);
        mChart.setHighlightPerTapEnabled(true);

        // mChart.setUnit(" €");
        // mChart.setDrawUnitsInChart(true);

        // add a selection listener
        mChart.setOnChartValueSelectedListener(this);

        setData(4, 100);

        mChart.animateY(1400, Easing.EasingOption.EaseInOutQuad);
        // mChart.spin(2000, 0, 360);


        Legend l = mChart.getLegend();
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.CENTER);
        l.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        l.setDrawInside(false);
        l.setXEntrySpace(7f);
        l.setYEntrySpace(0f);
        l.setYOffset(0f);

        // entry label styling
        mChart.setEntryLabelColor(Color.WHITE);

        mChart.setEntryLabelTextSize(12f);
    }


    private void setupRecyclerView() {
        mManager = new LinearLayoutManager(getContext());
        mManager.setReverseLayout(true);
        mManager.setStackFromEnd(true);
        mRecycler.setLayoutManager(mManager);


    }

    private SpannableString generateCenterSpannableText() {

        SpannableString s = new SpannableString("Budget\n" + BudgetManager.getBudgetValue() + " $");
        return s;
    }

    private void setData(int count, float range) {

        float mult = range;

        ArrayList<PieEntry> entries = new ArrayList<PieEntry>();


        entries.add(new PieEntry((float) 25, "avb"));
        entries.add(new PieEntry((float) 25, "avb"));
        entries.add(new PieEntry((float) 25, "avb"));
        entries.add(new PieEntry((float) 25, "avb"));
        PieDataSet dataSet = new PieDataSet(entries, "Election Results");

        dataSet.setDrawIcons(false);

        dataSet.setSliceSpace(3f);
        dataSet.setIconsOffset(new MPPointF(0, 40));
        dataSet.setSelectionShift(5f);

        // add a lot of colors

        ArrayList<Integer> colors = new ArrayList<Integer>();

        for (int c : ColorTemplate.VORDIPLOM_COLORS)
            colors.add(c);

        for (int c : ColorTemplate.JOYFUL_COLORS)
            colors.add(c);

        for (int c : ColorTemplate.COLORFUL_COLORS)
            colors.add(c);

        for (int c : ColorTemplate.LIBERTY_COLORS)
            colors.add(c);

        for (int c : ColorTemplate.PASTEL_COLORS)
            colors.add(c);

        colors.add(ColorTemplate.getHoloBlue());

        dataSet.setColors(colors);
        //dataSet.setSelectionShift(0f);

        PieData data = new PieData(dataSet);
        data.setValueFormatter(new DefaultValueFormatter(2));
        data.setValueTextSize(11f);
        data.setValueTextColor(Color.WHITE);
        mChart.setData(data);

        // undo all highlights
        mChart.highlightValues(null);

        mChart.invalidate();
    }

    @Override
    public void onValueSelected(Entry e, Highlight h) {

    }

    @Override
    public void onNothingSelected() {

    }

    private void showAdjustBudgetValue() {
        final Dialog ReviewDialog = new Dialog(getContext());
        // Set GUI of login screen
        ReviewDialog.requestWindowFeature(Window.FEATURE_NO_TITLE); //before
        ReviewDialog.setCancelable(true);
        ReviewDialog.setContentView(R.layout.layout_budget_price);


        // Init button of login GUI

        final EditText editText_budgetAmount = (EditText) ReviewDialog.findViewById(R.id.editText_budgetAmount);
        editText_budgetAmount.setText(Float.toString(BudgetManager.getBudgetValue()));
        Button buttonSave = (Button) ReviewDialog.findViewById(R.id.button_save);

        buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (Float.parseFloat(editText_budgetAmount.getText().toString()) > 100.00) {
                    BudgetManager.setBudgetValue(Float.parseFloat(editText_budgetAmount.getText().toString()));
                    mChart.setCenterText(generateCenterSpannableText());
                    ReviewDialog.dismiss();
                } else {
                    Toast.makeText(getContext(), "Value must be greater than 100$", Toast.LENGTH_SHORT).show();
                }

            }
        });


        // Make dialog box visible.
        ReviewDialog.show();
    }

    @Override
    public void onResume() {
        super.onResume();
        mChart.setCenterText(generateCenterSpannableText());

    }
}
