package com.cse4322.mockstock;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

/**
 * Created by Joseph on 1/5/2017.
 */

public class PortfolioFragment extends Fragment {
    private PortfolioListAdapter mPortfolioListAdapter;
    private ListView mStockListView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_portfolio, container, false);
        mStockListView = (ListView) view.findViewById(R.id.portfolio_list);
        mPortfolioListAdapter = new PortfolioListAdapter(getContext(), UserAccount.getCurrUserAccount().getUserStocks(true));
        mStockListView.setAdapter(mPortfolioListAdapter);
        mStockListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long l) {
                StockDetailsFragment stockDetailsFragment = new StockDetailsFragment();
                Bundle args = new Bundle();
                args.putCharSequence("ticker", mPortfolioListAdapter.getItem(position).getSymbol());
                stockDetailsFragment.setArguments(args);
                FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.stockListView, stockDetailsFragment);
                transaction.addToBackStack("stock_detail");
                transaction.commit();
            }
        });
        return view;
    }

    public void notifyDataSetChanged() {
        if(mPortfolioListAdapter != null) mPortfolioListAdapter.notifyDataSetChanged();
    }

    public void updatePortfolioStockList() {
        if(mPortfolioListAdapter != null) mPortfolioListAdapter.updateCurrUserStockList();
    }
}
