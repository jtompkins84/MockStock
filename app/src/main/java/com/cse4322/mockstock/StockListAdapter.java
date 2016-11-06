package com.cse4322.mockstock;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.ColorRes;
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

public class StockListAdapter extends ArrayAdapter<UserStock> implements UserStockUpdateAsyncResponse{
    public StockListAdapter(Context context, ArrayList<UserStock> itemName) {
        super(context, R.layout.portfolio_stock, itemName);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        LayoutInflater myInflater = LayoutInflater.from(getContext());
        View customView = myInflater.inflate(R.layout.portfolio_stock, parent, false);

        UserStock stock = getItem(position);
        TextView tickerName = (TextView) customView.findViewById(R.id.ticker);
        TextView companyName = (TextView) customView.findViewById(R.id.nameofcompany);
        companyName.setSelected(true);

        TextView market = (TextView) customView.findViewById(R.id.markettype);
        TextView gain = (TextView) customView.findViewById(R.id.gain);
        TextView percent = (TextView) customView.findViewById(R.id.percent);
        TextView value = (TextView) customView.findViewById(R.id.valueamt);
        TextView price = (TextView) customView.findViewById(R.id.pricestock);
        int positiveColor = getContext().getResources().getColor(R.color.positive);
        int negativeColor = getContext().getResources().getColor(R.color.negative);


        int GLColor, valueColor;
        tickerName.setText(stock.getTicker());
        companyName.setText(stock.getCompanyName());
        market.setText(stock.getExchange());

        if(stock.getGainLoss() < 0.0f) GLColor = negativeColor;
        else GLColor = positiveColor;
        gain.setText("$" + String.format("%.2f",stock.getGainLoss()) );
        gain.setTextColor(GLColor);

        percent.setText(String.format("%.2f", stock.getGainLossPercent()) + "%");
        percent.setTextColor(GLColor);

        if(stock.getTotalValue() < stock.getTotalInvestment()) valueColor = negativeColor;
        else valueColor = positiveColor;
        value.setText("$" + String.format("%.2f", stock.getTotalValue()) );
        value.setTextColor(valueColor);

        price.setText("$" + String.format("%.2f", stock.getCurrPrice()) );

        return customView;
    }

    public void updateCurrUserStockList() {
        new UserStockUpdateAsyncTask(this).execute(UserAccount.getCurrUserAccount());
    }

    @Override
    public void userStockUpdateProcessFinished(ArrayList<UserStock> output) {
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
}
