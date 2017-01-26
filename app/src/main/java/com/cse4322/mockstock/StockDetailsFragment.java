package com.cse4322.mockstock;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import yahoofinance.Stock;

public class StockDetailsFragment extends Fragment implements StockUpdateAsyncResponse, View.OnClickListener {
    private Stock mStock;
    private Timer mRefreshTimer;

    private TextView mTicker;
    private TextView mCompanyName;
    private TextView mPrice;
    private TextView mAmtChg;
    private TextView mAmtPctChg;
    private TextView mAskPrice;
    private TextView mHighPrice;
    private TextView mOpenPrice;
    private TextView mBidPrice;
    private TextView m52WeekHigh;
    private TextView m52WeekLow;
    private TextView mAnnuaYield;
    private TextView mYearTarget;
    private TextView mVolume;
    private TextView mMarketCap;
    private TextView mDividend;
    private TextView mPERatio;
    private int mColorPositive;
    private int mColorNegative;

    private boolean mIsInitialized = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_stock_details, container, false);

        Bundle args = getArguments();
        if(args != null)
            new StockUpdateAsyncTask(this).execute(new String[] {args.getCharSequence("ticker").toString()});

        mRefreshTimer = new Timer();

        Button sellButton = (Button) view.findViewById(R.id.buy);
        Button buyButton = (Button) view.findViewById(R.id.Sell);
        Button cancelButton = (Button) view.findViewById(R.id.Watch);

        sellButton.setOnClickListener(this);
        buyButton.setOnClickListener(this);
        cancelButton.setOnClickListener(this);

        return view;
    }

    private void initializeTextViews() {
        /******************************************************
         ** INITIALIZE  TextView OBJECTS
         ******************************************************/
        mTicker = (TextView) getView().findViewById(R.id.stockticker);
        mCompanyName = (TextView) getView().findViewById(R.id.companyname);
        mPrice = (TextView) getView().findViewById(R.id.stock_price);
        mAmtChg = (TextView) getView().findViewById(R.id.amtchange);
        mAmtPctChg = (TextView) getView().findViewById(R.id.amtpctchange);
        mAskPrice = (TextView) getView().findViewById(R.id.askprice2amt);
        mHighPrice = (TextView) getView().findViewById(R.id.highpriceamt);
        mOpenPrice = (TextView) getView().findViewById(R.id.openpriceamt);
        mBidPrice = (TextView) getView().findViewById(R.id.bidpriceamt);
        m52WeekHigh = (TextView) getView().findViewById(R.id.high52weekamt);
        m52WeekLow = (TextView) getView().findViewById(R.id.low52weekamt);
        mAnnuaYield = (TextView) getView().findViewById(R.id.yearreturnamt);
        mYearTarget = (TextView) getView().findViewById(R.id.year1targetamt);
        mVolume = (TextView) getView().findViewById(R.id.volumeamt);
        mMarketCap = (TextView) getView().findViewById(R.id.marketcapamt);
        mDividend = (TextView) getView().findViewById(R.id.dividendamt);
        mPERatio = (TextView) getView().findViewById(R.id.peratioamt);
        mColorPositive = getResources().getColor(R.color.positive);
        mColorNegative = getResources().getColor(R.color.negative);

        /******************************************************
         ** SET TEXT FIELDS AND TEXT COLOR
         ******************************************************/
        mTicker.setText(mStock.getSymbol());
        mCompanyName.setText(mStock.getName());

        mIsInitialized = true;
    }

    private void populateStockDetails() {
        int color;
        long tempLong = 0;
        float tempFloat = 0;

        try {
            tempFloat = mStock.getQuote().getPrice().floatValue();
            mPrice.setText(String.format("%.2f", tempFloat));
        } catch (NullPointerException e) { e.printStackTrace(); }
        try {
            tempFloat = mStock.getQuote().getChange().floatValue();
            mAmtChg.setText(String.format("%.2f", Math.abs(tempFloat)));
        } catch (NullPointerException e) { e.printStackTrace(); }
        try {
            tempFloat = mStock.getQuote().getChangeInPercent().floatValue();
            mAmtPctChg.setText(String.format("%.2f", tempFloat) + "%");
        } catch (NullPointerException e) { e.printStackTrace(); }

        // Set color of text affected by positive/negative market movement.
        if(tempFloat >= 0.0f) color = mColorPositive;
        else color = mColorNegative;
        mAmtChg.setTextColor(color);
        mAmtPctChg.setTextColor(color);

        try {
            tempFloat = mStock.getQuote().getAsk().floatValue();
            mAskPrice.setText(String.format("%.2f", tempFloat));
            tempFloat = mStock.getQuote().getBid().floatValue();
            mBidPrice.setText(String.format("%.2f", tempFloat));
            tempFloat = mStock.getQuote().getDayHigh().floatValue();
            mHighPrice.setText(String.format("%.2f", tempFloat));
        } catch (NullPointerException e) { e.printStackTrace(); }

        try {
            tempFloat = mStock.getQuote().getOpen().floatValue();
            mOpenPrice.setText(String.format("%.2f", tempFloat));
        } catch (NullPointerException e) { e.printStackTrace(); }

        try {
            tempFloat = mStock.getQuote().getYearHigh().floatValue();
            m52WeekHigh.setText(String.format("%.2f", tempFloat));
        } catch (NullPointerException e) { e.printStackTrace(); }

        try {
            tempFloat = mStock.getQuote().getYearLow().floatValue();
            m52WeekLow.setText(String.format("%.2f", tempFloat));
        } catch (NullPointerException e) { e.printStackTrace(); }

        try {
            tempFloat = mStock.getDividend().getAnnualYield().floatValue();
            mAnnuaYield.setText(String.format("%.2f", tempFloat));
        } catch (NullPointerException e) { e.printStackTrace(); }

        try {
            tempFloat = mStock.getStats().getOneYearTargetPrice().floatValue();
            mYearTarget.setText(String.format("%.2f", tempFloat));
        } catch (NullPointerException e) { e.printStackTrace(); }

        try {
            tempLong = mStock.getQuote().getVolume().longValue();
            String magnitude = "";
            if (tempLong >= 1000000L && tempLong <= 999999999L) {
                magnitude = "M";
                tempLong /= 1000000L;
            } else if (tempLong >= 1000000000L && tempLong <= 999999999999L) {
                magnitude = "B";
                tempLong /= 1000000000L;
            }
            mVolume.setText(String.format("%2d", tempLong) + magnitude);
        } catch (NullPointerException e) { e.printStackTrace(); }

        try { // catches cases where market cap is unavailable
            tempFloat = mStock.getStats().getMarketCap().scaleByPowerOfTen(-6).floatValue();
            String magnitude = "";
            if (tempFloat < 1000.0f) magnitude = "M";
            else {
                tempFloat = mStock.getStats().getMarketCap().scaleByPowerOfTen(-9).floatValue();
                magnitude = "B";
            }
            mMarketCap.setText(String.format("%.2f", tempFloat) + magnitude);
        } catch (NullPointerException e) { e.printStackTrace(); }

        try { // catches cases where annual yeild is unavailable
            tempFloat = mStock.getDividend().getAnnualYieldPercent().floatValue();
            mDividend.setText(String.format("%.2f", tempFloat));
        } catch (NullPointerException e) { e.printStackTrace(); }

        try { // catches cases where PE ratio is unavailable
            tempFloat = mStock.getStats().getPe().floatValue();
            mPERatio.setText(String.format("%.2f", tempFloat));
        } catch (NullPointerException e) { e.printStackTrace(); }
    }

    /**
     * Populates the text views with data retrieved from the <code>Stock</code> object.
     */
    public void updateStockDetails() {
        new StockUpdateAsyncTask(this).execute(new String[] {mStock.getSymbol()});
    }

    @Override
    public void stockUpdateProcessFinished(ArrayList<Stock> output) {
        if(output.size() > 0) {
            mStock = output.get(0);
            if(!mIsInitialized) initializeTextViews();
            populateStockDetails();
            mRefreshTimer.schedule(new StockDetailsFragment.RefreshStockTask(), 5000);
        }
    }

    @Override
    public void onClick(View v) {
        Button button = null;
        if(v instanceof Button) button = (Button) v;
        if(button != null) {
            if(button.getText().toString().compareToIgnoreCase("sell") == 0) {
                SellFragment sellFragment = new SellFragment();
                Bundle args = new Bundle();
                args.putCharSequence("ticker", mStock.getSymbol());
                sellFragment.setArguments(args);

                FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                transaction.add(R.id.stockListView, sellFragment);
                transaction.addToBackStack(null);
                transaction.commit();
            }
            else if(button.getText().toString().compareToIgnoreCase("buy") == 0) {
                BuyFragment buyFragment = new BuyFragment();
                Bundle args = new Bundle();
                args.putCharSequence("ticker", mStock.getSymbol());
                buyFragment.setArguments(args);

                FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                transaction.add(R.id.stockListView, buyFragment);
                transaction.addToBackStack(null);
                transaction.commit();
            }
            else if(button.getText().toString().compareToIgnoreCase("watch") == 0) {
                Toast.makeText(getContext(), "Watch Button.", Toast.LENGTH_LONG).show(); // TODO DELETE
                // TODO display 'watched' status
            }
        }
    }

    /**
     * TimerTask class that will launch the AsyncTask which updates the stock data.
     */
    private class RefreshStockTask extends TimerTask {
        @Override
        public void run() {
            if(mStock != null) {
                if(getView() != null && getView().isShown()) updateStockDetails();
            }
        }
    }
}
