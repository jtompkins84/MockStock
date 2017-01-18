package com.cse4322.mockstock;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.TextView;

import org.w3c.dom.Text;

import yahoofinance.Stock;

/**
 * Created by Joseph on 1/5/2017.
 */

public class SearchResultCardView extends CardView {
    private Stock stock;
    private TextView ticker;
    private TextView companyName;
    private TextView market;
    private TextView gainLoss;
    private TextView gainLossPer;
    private TextView price;
    private int positiveColor;
    private int negativeColor;

    public SearchResultCardView(Context context) {
        super(context);
    }

    public SearchResultCardView(Context context, AttributeSet attrSet) {
        super(context, attrSet);
    }

    public SearchResultCardView(Context context, AttributeSet attrSet, int defStyle) {
        super(context, attrSet, defStyle);
    }

    public void init(Stock stock) {
        this.stock = stock;

        positiveColor = getContext().getResources().getColor(R.color.positive);
        negativeColor = getContext().getResources().getColor(R.color.negative);

        ticker = (TextView) findViewById(R.id.ticker);
        companyName = (TextView) findViewById(R.id.nameofcompany);
        market = (TextView) findViewById(R.id.markettype);
        gainLoss = (TextView) findViewById(R.id.gain);
        gainLossPer = (TextView) findViewById(R.id.percent);
        price = (TextView) findViewById(R.id.pricestock);

        if(stock != null){
            ticker.setText(stock.getSymbol());
            companyName.setText(stock.getName());
            market.setText(stock.getStockExchange());
            update(stock);
        }
    }

    public void update(Stock stock) {
        if(this.stock != stock) this.stock = stock;
        int GLColor = 0;

        if(ticker.getText().toString().compareToIgnoreCase(stock.getSymbol()) != 0) {
            ticker.setText(stock.getSymbol());
            companyName.setText(stock.getName());
            market.setText(stock.getStockExchange());
        }

        try {
            if (stock.getQuote().getChange().floatValue() < 0.0f) GLColor = negativeColor;
            else GLColor = positiveColor;
            gainLoss.setText(String.format("%.2f", Math.abs(stock.getQuote().getChange().floatValue())));
            gainLoss.setTextColor(GLColor);
            gainLoss.refreshDrawableState();
        } catch (NullPointerException e) {
            Log.d(this.getClass().toString(), "Stock \'" + stock.getSymbol() + "\' change is unavailable." );
        }

        try {
            gainLossPer.setText(String.format("%.2f%%", stock.getQuote().getChangeInPercent().floatValue()));
            gainLossPer.setTextColor(GLColor);
            gainLossPer.refreshDrawableState();
        } catch (NullPointerException e) {
            Log.d(this.getClass().toString(), "Stock \'" + stock.getSymbol() + "\' change percent is unavailable." );
        }

        try {
            price.setText(String.format("%.2f", stock.getQuote().getPrice().floatValue()));
            price.refreshDrawableState();
        } catch (NullPointerException e) {
            Log.d(this.getClass().toString(), "Stock \'" + stock.getSymbol() + "\' price is unavailable.");
        }
    }

    public String getTicker() {
        return ticker.getText().toString();
    }

    public String getCompanyName() {
        return companyName.getText().toString();
    }

    public String getMarket() {
        return market.getText().toString();
    }
}
