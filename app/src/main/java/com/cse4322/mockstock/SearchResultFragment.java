package com.cse4322.mockstock;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import yahoofinance.Stock;

/**
 * Created by Joseph on 12/5/2016.
 */

public class SearchResultFragment extends Fragment implements StockUpdateAsyncResponse {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_search_results, container, false);
    }

    @Override
    public void stockUpdateProcessFinished(ArrayList<Stock> output) {

    }
}
