package com.cse4322.mockstock;

import android.content.Context;
import android.widget.ArrayAdapter;

import java.util.ArrayList;

import yahoofinance.Stock;

/**
 * Created by Joseph on 12/5/2016.
 */

public class SearchResultAdapter extends ArrayAdapter<Stock> {

    public SearchResultAdapter(Context context, ArrayList<Stock> itemName) {
        super(context, R.layout.search_result_card, itemName);
    }
}
