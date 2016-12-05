package com.cse4322.mockstock;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.ColorRes;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.lang.reflect.Array;
import java.util.ArrayList;

/**
 * Created by Winston on 10/9/2016.
 */

public class StockListAdapter extends ArrayAdapter<UserStock> implements UserStockUpdateAsyncResponse, Filterable{
    private static TextView portfolioBalance;

    public int number_of_stocks;

    ArrayList<UserStock> stock_list;
    ArrayList<UserStock> filteredList;
    StockFilter stockFilter;

    public StockListAdapter(Context context, ArrayList<UserStock> itemName) {
        super(context, R.layout.portfolio_stock, itemName);

        this.stock_list = itemName;
        this.filteredList = itemName;

        getFilter();        // implement filter for search processing
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        LayoutInflater myInflater = LayoutInflater.from(getContext());
        View customView = myInflater.inflate(R.layout.portfolio_stock, parent, false);

        UserStock stock = getItem(position);
        /******************************************************
        ** INITIALIZE TextView OBJECTS
        ******************************************************/
        TextView tickerName = (TextView) customView.findViewById(R.id.ticker);
        TextView companyName = (TextView) customView.findViewById(R.id.nameofcompany);
        companyName.setSelected(true);
        TextView market = (TextView) customView.findViewById(R.id.markettype);
        TextView gain = (TextView) customView.findViewById(R.id.gain);
        TextView quantity = (TextView) customView.findViewById(R.id.quantity);
        TextView value = (TextView) customView.findViewById(R.id.valueamt);
        TextView price = (TextView) customView.findViewById(R.id.pricestock);
        int positiveColor = getContext().getResources().getColor(R.color.positive);
        int negativeColor = getContext().getResources().getColor(R.color.negative);

        /******************************************************
        ** SET TEXT FIELDS & TEXT COLOR
        ******************************************************/
        int GLColor, valueColor;
        tickerName.setText(stock.getTicker());
        companyName.setText(stock.getCompanyName());
        market.setText(stock.getExchange());

        if(stock.getGainLoss() < 0.0f) GLColor = negativeColor;
        else GLColor = positiveColor;
        gain.setText("$" + String.format("%.2f", Math.abs(stock.getGainLoss())) );
        gain.setTextColor(GLColor);

        quantity.setText(String.format("%d", stock.getNumberOwned()));

        if(stock.getTotalValue() < stock.getTotalInvestment()) valueColor = negativeColor;
        else valueColor = positiveColor;
        value.setText("$" + String.format("%.2f", stock.getTotalValue()) );
        value.setTextColor(valueColor);

        price.setText("$" + String.format("%.2f", stock.getCurrPrice()) );

        return customView;
    }

    /**
     * Launches AsyncTask that retrieves data from the Yahoo Finance API
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

        int i;
        int count = getCount();

        for(i = 0; i < count; i++) {
            remove(getItem(0));
        }
        for(i = 0; i < output.size(); i++) {
            add(output.get(i));
        }

        notifyDataSetChanged();
    }

    /*
        The following functionality is gathered from https://coderwall.com/p/zpwrsg/add-search-function-to-list-view-in-android
        The search works as follows...

        1. user enters in text in search bar
        2. the adapter will be a filtered list which does not contain all stocks
        3. update the view to have the filtered list
     */

    public int getNumber_of_stocks() {
        return number_of_stocks;
    }

    public ArrayList<UserStock> getStock_list() {
        return stock_list;
    }

    // set the stock list to public variable
    public void setStock_list(ArrayList<UserStock> stock_list) {
        this.stock_list = stock_list;
    }

    @Override
    public int getCount() {
        return filteredList.size();
    }

    // Retrieve the items from the filtered list
    @Override
    public UserStock getItem(int i) {
        return filteredList.get(i);
    }

    // gets the unique ID for an item in the list.
    @Override
    public long getItemId(int i) {
        return i;
    }

    // get the current filter, if none exists then create a new filter object
    @Override
    public Filter getFilter() {
        if (stockFilter == null) {
            stockFilter = new StockFilter();
        }

        return stockFilter;
    }

    /*
        Bulk search functionality.
        1. Get input from search bar
        2. Create a new temp list to hold filtered results
        3. if a match occured between what is searched, then place it in the filtered list.
        4. display filtered list.
     */

    private class StockFilter extends Filter {


        @Override
        protected FilterResults performFiltering(CharSequence charSequence) {
            FilterResults filterResults = new FilterResults();
            if (charSequence!=null && charSequence.length()>0) {
                ArrayList<UserStock> tempList = new ArrayList<UserStock>(); // setup the temp list which will be used for the filtered list.

                // search for stock
                // ensure that the text matches by eliminating case sensitivity
                // Filter based purely on the name of the ticker.
                for (int i = 0; i < stock_list.size(); i++) {
                    if (stock_list.get(i).getTicker().toLowerCase().contains(charSequence.toString().toLowerCase())) {
                        tempList.add(stock_list.get(i));
                    }
                }

                // setup the filter list size to avoid out of bounds exceptions
                filterResults.count = tempList.size();

                // set the values to the filtered list.
                filterResults.values = tempList;
            } else {

                // Handles a case where the search is blank and avoid null exceptions from searching nothing.
                filterResults.count = stock_list.size();
                filterResults.values = stock_list;
            }

            return filterResults;
        }

        // once the filtered list is created, "push" the list on the main activity screen
        @Override
        protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
            filteredList = (ArrayList<UserStock>) filterResults.values;
            notifyDataSetChanged();
        }
    }
}
