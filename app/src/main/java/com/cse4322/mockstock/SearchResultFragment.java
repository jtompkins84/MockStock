package com.cse4322.mockstock;

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

        if(getArguments() != null) mSearchResultAdapter = new SearchResultAdapter(getContext(), getArguments().getCharSequenceArrayList("tickers"));
        else mSearchResultAdapter = new SearchResultAdapter(getContext(), new ArrayList<CharSequence>());
        mSearchResultListView.setAdapter(mSearchResultAdapter);
        mSearchResultListView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                StockDetailsFragment stockDetailsFragment = new StockDetailsFragment();
                Bundle args = new Bundle();
                args.putCharSequence("ticker", mSearchResultAdapter.getItem(position).getSymbol());
                stockDetailsFragment.setArguments(args);
                FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.stockListView, stockDetailsFragment);
                transaction.addToBackStack(null);

                transaction.commit();
            }
        });

        return view;
    }
}
