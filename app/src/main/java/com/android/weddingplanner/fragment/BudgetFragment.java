package com.android.weddingplanner.fragment;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.weddingplanner.R;
import com.android.weddingplanner.activities.AddBudgetItemActivity;
import com.android.weddingplanner.adapter.CollapsingSectionedAdapter;
import com.android.weddingplanner.helper.BudgetManager;
import com.android.weddingplanner.helper.DatabaseOps;
import com.android.weddingplanner.models.BudgetByCategoryItem;
import com.android.weddingplanner.models.BudgetItem;
import com.android.weddingplanner.stickyrecyclerheaders.SectionedRecyclerViewAdapter;
import com.android.weddingplanner.viewmodel.BudgetItemView;
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

import org.zakariya.stickyheaders.StickyHeaderLayoutManager;

import java.util.ArrayList;
import java.util.List;

public class BudgetFragment extends Fragment implements OnChartValueSelectedListener, CollapsingSectionedAdapter.ItemClickListener, BudgetItemView.DietPlanClickListener {
    private RecyclerView mRecycler;

    private View rootview;
    private PieChart mChart;

    private SectionedRecyclerViewAdapter sectionAdapter;

    private CollapsingSectionedAdapter mAdapter = new CollapsingSectionedAdapter();
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootview = inflater.inflate(R.layout.fragment_budget, container, false);
        return rootview;

    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        setUpViews();
        setupRecyclerView();


        rootview.findViewById(R.id.imageButton_editBudget).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showAdjustBudgetValue();
            }
        });

        rootview.findViewById(R.id.fab_addBudgetItem).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), AddBudgetItemActivity.class);
                startActivity(intent);
            }
        });
    }

    private void setUpViews() {
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


    }

    private void setUpChartView(List<BudgetByCategoryItem> items) {
        setData(items);
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
        mRecycler = (RecyclerView) rootview.findViewById(R.id.recyclerView_budgetItems);
        mRecycler.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecycler.setNestedScrollingEnabled(false);
        sectionAdapter = new SectionedRecyclerViewAdapter();
        mRecycler.setAdapter(sectionAdapter);

    }

    private SpannableString generateCenterSpannableText() {

        SpannableString s = new SpannableString("Budget\n" + BudgetManager.getBudgetValue() + " $");
        return s;
    }

    private void setData(List<BudgetByCategoryItem> items) {

        float paidAmount = 0;

        for (int i = 0; i < items.size(); i++) {
            for (int j = 0; j < items.get(i).budgetItems.size(); j++) {
                paidAmount += items.get(i).budgetItems.get(j).itemBudjet;
            }

        }

        float left = BudgetManager.getBudgetValue() - paidAmount;
        float exceeded = 0;

        if (left < 0) {
            exceeded = Math.abs(left);
            left = 0;
        }


        ArrayList<PieEntry> entries = new ArrayList<PieEntry>();


        entries.add(new PieEntry(paidAmount, "Paid"));

        if (left > 0) {
            entries.add(new PieEntry(left, "Left"));
        }
        if (exceeded > 0) {
            entries.add(new PieEntry(exceeded, "Exceeded"));
        }


        PieDataSet dataSet = new PieDataSet(entries, "Budget");

        dataSet.setDrawIcons(false);

        dataSet.setSliceSpace(3f);
        dataSet.setIconsOffset(new MPPointF(0, 40));
        dataSet.setSelectionShift(5f);

        // add a lot of colors

        ArrayList<Integer> colors = new ArrayList<Integer>();


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

                    setUpChartView(DatabaseOps.getCurrentInstance().getBudgetItems());
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

        List<BudgetByCategoryItem> items = new ArrayList<>();
        items = DatabaseOps.getCurrentInstance().getBudgetItems();
        Log.d("BudgetFragment", "onResume: " + items.size());

        setUpChartView(items);
        sectionAdapter.removeAllSections();

        for (int i = 0; i < items.size(); i++) {
            BudgetItemView weekDietPlanView = new BudgetItemView(items.get(i), i);
            weekDietPlanView.setMyClickListener(this);
            sectionAdapter.addSection(String.valueOf(i), weekDietPlanView);
        }
        sectionAdapter.notifyDataSetChanged();
        mRecycler.setAdapter(sectionAdapter);


    }

    @Override
    public void onItemClicked(int sectionIndex, int sectionItemIndex, int dietDayId, boolean checked) {

    }

    @Override
    public void onItemClick(int sectionPosition, int position, BudgetItem item) {
        Intent intent = new Intent(getActivity(), AddBudgetItemActivity.class);
        intent.putExtra(AddBudgetItemActivity.BUDGET_ITEM_KEY, item);
        startActivity(intent);

    }
}
