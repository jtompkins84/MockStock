package com.cse4322.mockstock;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import java.util.ArrayList;

import yahoofinance.Stock;

/**
 * Created by Joseph on 12/5/2016.
 */

public class SearchResultAdapter extends ArrayAdapter<Stock> implements StockSearchAsyncResponse, StockUpdateAsyncResponse{
    private boolean mDoAdapterRefresh = false;

    public SearchResultAdapter(Context context) {
        super(context, R.layout.search_result_card, new ArrayList<Stock>());
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Stock stock = getItem(position);

        SearchResultCardView searchResultCard = (SearchResultCardView) convertView;
        if(searchResultCard == null) {
            LayoutInflater inflater = LayoutInflater.from(getContext());
            searchResultCard = (SearchResultCardView)inflater.inflate(R.layout.search_result_card, parent, false);
            searchResultCard.init(stock);
        }
        else if(stock != null && searchResultCard.getTicker().compareToIgnoreCase(stock.getSymbol()) != 0) {
            LayoutInflater inflater = LayoutInflater.from(getContext());
            searchResultCard = (SearchResultCardView)inflater.inflate(R.layout.search_result_card, parent, false);
            searchResultCard.init(stock);
        }
        else
            searchResultCard.update(stock);

        return searchResultCard;
    }

    /**
     * Initiates a <code>StockSearchAsyncTask</code> that will then update this adapter with the
     * results based on the query.
     * @param query the query to associate with the search request
     */
    public void submitQuery(String query) {
        new StockSearchAsyncTask(this).execute(query);
    }

    @Override
    public void stockUpdateProcessFinished(ArrayList<Stock> output) {
        clear();
        for(Stock s : output) add(s);
        notifyDataSetChanged();
    }

    @Override
    public void stockSearchProcessFinished(ArrayList<String> output) {
        String[] symbols = new String[output.size()];
        for(int i = 0; i < symbols.length; i++) {
            symbols[i] = output.get(i);
        }
        mDoAdapterRefresh = true;
        if(symbols.length > 0) new StockUpdateAsyncTask(this).execute(symbols);
    }

}
