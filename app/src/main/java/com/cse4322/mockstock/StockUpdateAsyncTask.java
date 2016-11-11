package com.cse4322.mockstock;

import android.os.AsyncTask;
import android.util.Log;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;

import yahoofinance.*;
import yahoofinance.Stock;

/**
 * AsyncTask that gets updated stock data using YahooFinance API.
 *
 * TODO Citation
 * http://stackoverflow.com/questions/12575068/how-to-get-the-result-of-onpostexecute-to-main-activity-because-asynctask-is-a
 */
public class StockUpdateAsyncTask extends AsyncTask<String[], Void, ArrayList<Stock>> {
    private StockUpdateAsyncResponse mDelegate;

    private ArrayList<Stock> mStocks = new ArrayList<Stock>(100);

    public StockUpdateAsyncTask(StockUpdateAsyncResponse delegate) {
        mDelegate = delegate;
    }

    @Override
    protected ArrayList<Stock> doInBackground(String[]... params) {
        Map<String, Stock> stocks = null;
        try {
            stocks = YahooFinance.get(params[0]);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

        for(String p : params[0]) {
            mStocks.add(stocks.get(p));
        }

        return mStocks;
    }

    @Override
    protected void onPostExecute(ArrayList<Stock> result) {
        mDelegate.stockUpdateProcessFinished(result);
    }

}
