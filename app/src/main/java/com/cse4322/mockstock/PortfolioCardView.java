package com.cse4322.mockstock;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Joseph on 12/19/2016.
 */

public class PortfolioCardView extends CardView {
    private UserStock userStock;
    private TextView ticker;
    private TextView companyName;
    private TextView market;
    private TextView gainLoss;
    private TextView quantity;
    private TextView totalValue;
    private TextView curPrice;
    private int positiveColor;
    private int negativeColor;

    public PortfolioCardView(Context context) {
        super(context);
    }

    public PortfolioCardView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public PortfolioCardView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void init(UserStock stock) {
        this.userStock = stock;
        ticker = (TextView) this.findViewById(R.id.ticker);
        companyName = (TextView) this.findViewById(R.id.nameofcompany);
        companyName.setSelected(true);

        market = (TextView) this.findViewById(R.id.markettype);
        gainLoss = (TextView) this.findViewById(R.id.gain);
        quantity = (TextView) this.findViewById(R.id.quantity);
        totalValue = (TextView) this.findViewById(R.id.valueamt);
        curPrice = (TextView) this.findViewById(R.id.pricestock);
        positiveColor = getContext().getResources().getColor(R.color.positive);
        negativeColor = getContext().getResources().getColor(R.color.negative);

        if(userStock != null) {
            this.ticker.setText(userStock.getTicker());
            this.companyName.setText(userStock.getCompanyName());
            this.market.setText(userStock.getExchange());
            updateText(userStock);
        }
    }

    public void updateText(UserStock stock) {
        int GLColor, valueColor;

        if(stock.getGainLoss() < 0.0f) GLColor = negativeColor;
        else GLColor = positiveColor;
        gainLoss.setText("$" + String.format("%.2f", Math.abs(stock.getGainLoss())) );
        gainLoss.setTextColor(GLColor);
        Log.d(PortfolioCardView.class.getSimpleName(), "updateText: " + stock.getTicker());

        quantity.setText(String.format("%d", stock.getNumberOwned()));

        curPrice.setText("$" + String.format("%.2f", stock.getCurrPrice()) );
        curPrice.refreshDrawableState();

        if(stock.getTotalValue() < stock.getTotalInvestment()) valueColor = negativeColor;
        else valueColor = positiveColor;
        totalValue.setText("$" + String.format("%.2f", stock.getTotalValue()) );
        totalValue.setTextColor(valueColor);
    }

    public void updateText() {
        List<UserStock> userStocks = UserStock.find(UserStock.class, "ticker = ?", userStock.getTicker());

        try {
            userStock = userStocks.get(0);
        }
        catch (IndexOutOfBoundsException e) {
            Log.e(PortfolioCardView.class.getSimpleName(),
                    "UserStock for ticker \'" + userStock.getTicker() + "\' not found in database.");
            return;
        }

        updateText(userStock);
    }

    public String getTicker() {
        return userStock.getTicker();
    }
}