package com.cse4322.mockstock;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.lang.reflect.Array;
import java.util.ArrayList;

/**
 * Created by Winston on 10/9/2016.
 */

public class StockListAdapter extends ArrayAdapter<Stock>{
    public StockListAdapter(Context context, ArrayList<Stock> itemName) {
        super(context, R.layout.portfolio_stock, itemName);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        LayoutInflater myInflater = LayoutInflater.from(getContext());
        View customView = myInflater.inflate(R.layout.portfolio_stock, parent, false);

        Stock stock = getItem(position);
        TextView tickerName = (TextView) customView.findViewById(R.id.ticker);
        TextView companyName = (TextView) customView.findViewById(R.id.nameofcompany);
        TextView market = (TextView) customView.findViewById(R.id.markettype);
        TextView gain = (TextView) customView.findViewById(R.id.gain);
        TextView percent = (TextView) customView.findViewById(R.id.percent);
        TextView value = (TextView) customView.findViewById(R.id.valueamt);
        TextView price = (TextView) customView.findViewById(R.id.pricestock);



        tickerName.setText(stock.getTicker());
        companyName.setText(stock.getCompany());
        market.setText(stock.getMarket());
        gain.setText(stock.getGain());
        percent.setText(stock.getPct());
        value.setText(stock.getValue());
        price.setText(stock.getPrice());

        return customView;
    }

}
