package com.cse4322.mockstock;

import android.os.AsyncTask;
import android.util.Log;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;

import yahoofinance.*;
import yahoofinance.Stock;

/**
 * Created by Joseph on 11/5/2016.
 */

public class UserStockUpdateAsyncTask extends AsyncTask<UserAccount, Void, ArrayList<UserStock>> {
    private UserStockUpdateAsyncResponse mDelegate;
    private ArrayList<Stock> mStocks = new ArrayList<Stock>();
    private ArrayList<UserStock> userStocks;

    public UserStockUpdateAsyncTask(UserStockUpdateAsyncResponse delegate) {
        mDelegate = delegate;
    }

    @Override
    protected ArrayList<UserStock> doInBackground(UserAccount ... params) {
        Map<String, Stock> stocks = null;
        UserAccount userAccount = params[0];
        // Attempts to get all the user stocks that belong to the user denoted by the first parameter.
        // The default user name is used if param[0] is null.
        userStocks = (ArrayList<UserStock>) userAccount.getUserStocks(true);

        String[] tickers = new String[userStocks.size()];

        // retrieving tickers from the UserStocks
        for(int i = 0; i < tickers.length; i++) {
            tickers[i] = userStocks.get((i)).getSymbol();
        }

        try {
            stocks = YahooFinance.get(tickers);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } catch (IndexOutOfBoundsException e) {
            Log.d("UserStockUpdate", "User \'" + UserAccount.getCurrUserAccount().getUserName() + "\' has no purchased stocks.");
        }

        // update each user stock & add yahoofinance.Stock object to mStocks ArrayList<Stock>.
        for(UserStock uStock : userStocks) {
            uStock.updateUserStock(stocks.get(uStock.getSymbol()));
//            mStocks.add(stocks.get(uStock.getSymbol())); // TODO Not sure mStocks member is still necessary...
        }

        return userStocks; // return resulting list of yahoofinance.Stock objects
    }

    @Override
    protected void onPostExecute(ArrayList<UserStock> result) {
        mDelegate.userStockUpdateProcessFinished(result);
    }

    public ArrayList<UserStock> getUserStocks() {
        return userStocks;
    }
}
