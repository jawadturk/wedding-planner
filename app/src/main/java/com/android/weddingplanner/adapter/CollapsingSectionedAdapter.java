package com.android.weddingplanner.adapter;


import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import com.android.weddingplanner.R;
import com.android.weddingplanner.models.BudgetByCategoryItem;
import com.android.weddingplanner.models.BudgetItem;

import org.zakariya.stickyheaders.SectioningAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * SimpleDemoAdapter, just shows demo data
 */
public class CollapsingSectionedAdapter extends SectioningAdapter {

    static final String TAG = CollapsingSectionedAdapter.class.getSimpleName();

    public ItemClickListener myClickListener;
    List<BudgetByCategoryItem> budjetItemsCategoriesList = new ArrayList<>();
    ArrayList<Section> sections = new ArrayList<>();


    public CollapsingSectionedAdapter() {


    }

    public void setMyClickListener(ItemClickListener myClickListener) {
        this.myClickListener = myClickListener;
    }

    public void setBudjetItemsCategoriesList(List<BudgetByCategoryItem> categoriesList) {
        this.budjetItemsCategoriesList = categoriesList;

        sections.clear();

        for (int i = 0; i < budjetItemsCategoriesList.size(); i++) {
            appendSection(i);
        }


    }

    public void updateSectionItem(BudgetItem item, int sectionId, int itemId) {
        sections.get(sectionId).items.set(itemId, item);
        notifySectionItemChanged(sectionId, itemId);

    }

    void appendSection(int index) {
        Section section = new Section();
        section.index = index;
        section.header = budjetItemsCategoriesList.get(index).categoryName;
        section.items = budjetItemsCategoriesList.get(index).budgetItems;

        sections.add(section);
    }


    @Override
    public int getNumberOfSections() {
        Log.d(TAG, "getNumberOfSections: section size" + sections.size());
        return sections.size();
    }

    @Override
    public int getNumberOfItemsInSection(int sectionIndex) {
        Log.d(TAG, "section index" + sectionIndex + " itemsize: " + sections.get(sectionIndex).items.size());
        return sections.get(sectionIndex).items.size();
    }

    @Override
    public boolean doesSectionHaveHeader(int sectionIndex) {
        return !TextUtils.isEmpty(sections.get(sectionIndex).header);
    }

    @Override
    public ItemViewHolder onCreateItemViewHolder(ViewGroup parent, int itemType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View v = inflater.inflate(R.layout.list_item_budget, parent, false);
        Log.d(TAG, "onCreateItemViewHolder: ");
        return new ItemViewHolder(v);

    }

    @Override
    public HeaderViewHolder onCreateHeaderViewHolder(ViewGroup parent, int headerType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View v = inflater.inflate(R.layout.list_item_budget_category_header, parent, false);
        Log.d(TAG, "onCreateHeaderViewHolder: ");
        return new HeaderViewHolder(v);
    }

    @Override
    public void onBindItemViewHolder(SectioningAdapter.ItemViewHolder viewHolder, final int sectionIndex, final int itemIndex, int itemType) {
        Section s = sections.get(sectionIndex);

        final BudgetItem budjet = s.items.get(itemIndex);

        final ItemViewHolder itemHolder = (ItemViewHolder) viewHolder;

        String budgetItemTitle = budjet.itemName;
        float itemBudjetValue = budjet.itemBudjet;


        itemHolder.textView_itemName.setText(budgetItemTitle);
        itemHolder.textView_itemBudget.setText("$" + itemBudjetValue);

        Log.d(TAG, "onBindItemViewHolder: " + budgetItemTitle + "value" + itemBudjetValue);

    }

    @Override
    public void onBindHeaderViewHolder(SectioningAdapter.HeaderViewHolder viewHolder, int sectionIndex, int headerType) {
        Section s = sections.get(sectionIndex);
        HeaderViewHolder hvh = (HeaderViewHolder) viewHolder;
        hvh.textView.setText(s.header);
        Log.d(TAG, "onBindHeaderViewHolder: " + s.header);
    }

    public interface ItemClickListener {
        public void onItemClicked(int sectionIndex, int sectionItemIndex, int dietDayId, boolean checked);

    }

    private class Section {
        int index;
        String header;
        List<BudgetItem> items;
    }

    public class ItemViewHolder extends SectioningAdapter.ItemViewHolder {
        private final View rootView;

        private final TextView textView_itemName;
        private final TextView textView_itemBudget;


        public ItemViewHolder(View view) {
            super(view);
            rootView = view;

            textView_itemName = (TextView) view.findViewById(R.id.textView_itemName);
            textView_itemBudget = (TextView) view.findViewById(R.id.textView_itemPrice);


        }


    }

    public class HeaderViewHolder extends SectioningAdapter.HeaderViewHolder {
        TextView textView;


        public HeaderViewHolder(View itemView) {
            super(itemView);

            textView = (TextView) itemView.findViewById(R.id.textView_header);
            Log.d(TAG, "HeaderViewHolder: ");

        }


    }

}
