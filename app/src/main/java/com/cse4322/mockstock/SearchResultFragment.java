package com.cse4322.mockstock;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;

/**
 * Created by Joseph on 12/5/2016.
 */

public class SearchResultFragment extends Fragment{
    SearchResultAdapter mSearchResultAdapter;
    ListView mSearchResultListView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search_results, container, false);
        mSearchResultListView = (ListView) view.findViewById(R.id.search_results_list);

        mSearchResultListView.setAdapter(mSearchResultAdapter);
        mSearchResultListView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                MainActivity mainActivity = (MainActivity) getActivity();

                StockDetailsFragment stockDetailsFragment = new StockDetailsFragment();
                Bundle args = new Bundle();
                args.putCharSequence("ticker", mSearchResultAdapter.getItem(position).getSymbol());
                stockDetailsFragment.setArguments(args);
                FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.stockListView, stockDetailsFragment);
                transaction.addToBackStack("stock_detail");
                transaction.commit();

                mainActivity.collapseSearchView("stock_detail");
            }
        });

        return view;
    }

    public SearchResultAdapter getSearchResultAdapter() {
        return mSearchResultAdapter;
    }

    public void setSearchResultAdapter(SearchResultAdapter searchResultAdapter) {
        mSearchResultAdapter = searchResultAdapter;
        if(mSearchResultListView != null && mSearchResultListView.getAdapter() == null) {
            mSearchResultListView.setAdapter(mSearchResultAdapter);
        }
    }
}
