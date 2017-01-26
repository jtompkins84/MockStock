package com.cse4322.mockstock;

import android.content.Context;
import android.support.annotation.NonNull;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.ArrayAdapter;

import java.util.ArrayList;

/**
 * Created by Winston on 10/9/2016.
 */

public class PortfolioListAdapter extends ArrayAdapter<UserStock> implements UserStockUpdateAsyncResponse{

    public PortfolioListAdapter(Context context, ArrayList<UserStock> itemName) {
        super(context, R.layout.portfolio_stock, itemName);
    }

    /**
     *
     * @param position
     * @param convertView
     * @param parent
     * @return
     */
    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent){
        UserStock stock = getItem(position);

        PortfolioCardView portfolioCard = (PortfolioCardView)convertView;
        // If the PortfolioCardView at position has not been inflated, or if it is being recycled...
        if(portfolioCard == null ||
                stock != null && portfolioCard.getTicker().compareToIgnoreCase(stock.getSymbol()) != 0) {
            LayoutInflater inflater = LayoutInflater.from(getContext());
            if(stock.getQuantityOwned() != 0) {
                portfolioCard = (PortfolioCardView) inflater.inflate(R.layout.portfolio_stock, parent, false);
                portfolioCard.init(stock);
            }
            else remove(stock);
        }
        else
            portfolioCard.updateText(stock);

        notifyDataSetChanged();
        return portfolioCard;
    }

    /**
     * Launches AsyncTask that retrieves data from the Yahoo Finance API, updates UserStocks belonging
     * to the current user, and refreshes this <code>PortfolioListAdapter</code>'s list.
     */
    public void updateCurrUserStockList() {
        new UserStockUpdateAsyncTask(this).execute(UserAccount.getCurrUserAccount());
    }

    @Override
    /**
     * Refreshes user stock information with data retrieved by the
     * <code>UserStockUpdateAsyncTask</code> calling this method.
     * @param output the output from the <code>UserStockUpdateAsyncTask</code> calling this method
     */
    public void userStockUpdateProcessFinished(ArrayList<UserStock> output) {
        if(output == null) return;

        for(int i = 0; i < output.size(); i++) {
            String outputTicker = output.get(i).getSymbol();
            if(i < getCount()) {
                String itemTicker = getItem(i).getSymbol();
                if(itemTicker.compareToIgnoreCase(outputTicker) != 0 ) {
                    // UserStock.refresh will return false if the UserStock is no longer in the database.
                    if(getItem(i).refresh()) insert(output.get(i), i++);
                    else remove(getItem(i));
                }
                else getItem(i).refresh();
            }
            // add the UserStock from output to the list adapter, because it has not been added yet.
            else add(output.get(i));
        }

        notifyDataSetChanged();
    }
}
