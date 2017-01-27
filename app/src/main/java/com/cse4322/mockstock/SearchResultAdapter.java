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

    public SearchResultAdapter(Context context, ArrayList<CharSequence> tickerList) {
        super(context, R.layout.search_result_card, new ArrayList<Stock>());

        String[] tickers = new String[tickerList.size()];
        for (int i = 0; i < tickers.length; i++) tickers[i] = tickerList.get(i).toString();

        if(tickerList.size() > 0) new StockUpdateAsyncTask(this).execute(tickers);
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

    @Override
    public void stockUpdateProcessFinished(ArrayList<Stock> output) {
        for(Stock s : output) add(s);
    }

    @Override
    public void stockSearchProcessFinished(ArrayList<String> output) {

    }
}
