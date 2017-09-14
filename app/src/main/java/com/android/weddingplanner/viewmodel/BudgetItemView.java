package com.android.weddingplanner.viewmodel;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.android.weddingplanner.R;
import com.android.weddingplanner.models.BudgetByCategoryItem;
import com.android.weddingplanner.models.BudgetItem;
import com.android.weddingplanner.stickyrecyclerheaders.StatelessSection;


/**
 * Created by jawad.turk on 24-Dec-16.
 */

public class BudgetItemView extends StatelessSection {

    public BudgetByCategoryItem budgetItem;
    public DietPlanClickListener myClickListener;
    public int sectionPosition;

    public BudgetItemView(BudgetByCategoryItem budgetItem, int position) {
        super(R.layout.list_item_budget_category_header, R.layout.list_item_budget);

        this.budgetItem = budgetItem;
        this.sectionPosition = position;
    }

    public void setMyClickListener(DietPlanClickListener myClickListener) {
        this.myClickListener = myClickListener;
    }

    @Override
    public int getContentItemsTotal() {
        return budgetItem.budgetItems.size();
    }

    @Override
    public RecyclerView.ViewHolder getItemViewHolder(View view) {
        return new ItemViewHolder(view);
    }

    @Override
    public void onBindItemViewHolder(RecyclerView.ViewHolder holder, final int position) {
        final ItemViewHolder itemHolder = (ItemViewHolder) holder;


        final BudgetItem budjet = budgetItem.budgetItems.get(position);

        String budgetItemTitle = budjet.itemName;
        float itemBudjetValue = budjet.itemBudjet;


        itemHolder.textView_itemName.setText(budgetItemTitle);
        itemHolder.textView_itemBudget.setText("$" + itemBudjetValue);
        itemHolder.cardView_budgetItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myClickListener.onItemClick(sectionPosition, position, budjet);
            }
        });
    }

    @Override
    public RecyclerView.ViewHolder getHeaderViewHolder(View view) {
        return new HeaderViewHolder(view);
    }

    @Override
    public void onBindHeaderViewHolder(RecyclerView.ViewHolder holder, int headerPosition) {
        HeaderViewHolder headerHolder = (HeaderViewHolder) holder;
        headerHolder.textView.setText(budgetItem.categoryName);
    }


    public interface DietPlanClickListener {
        public void onItemClick(int sectionPosition, int position, BudgetItem item);
    }

    class HeaderViewHolder extends RecyclerView.ViewHolder {

        TextView textView;

        public HeaderViewHolder(View view) {
            super(view);

            textView = (TextView) itemView.findViewById(R.id.textView_header);
        }
    }

    class ItemViewHolder extends RecyclerView.ViewHolder {

        private final View rootView;

        private final TextView textView_itemName;
        private final TextView textView_itemBudget;
        private final CardView cardView_budgetItem;

        public ItemViewHolder(View view) {
            super(view);

            rootView = view;

            textView_itemName = (TextView) view.findViewById(R.id.textView_itemName);
            textView_itemBudget = (TextView) view.findViewById(R.id.textView_itemPrice);
            cardView_budgetItem = (CardView) view.findViewById(R.id.cardView_budgetItem);

        }
    }

}