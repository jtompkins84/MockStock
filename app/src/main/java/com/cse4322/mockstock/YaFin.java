package com.cse4322.mockstock;

import android.os.AsyncTask;
import android.util.Log;

import java.io.IOException;
import java.math.BigDecimal;

import yahoofinance.*;

/**
 * Created by Joseph on 9/28/2016.
 */

public class YaFin extends AsyncTask<String, Void, Stock> {

    @Override
    protected Stock doInBackground(String... params) {
        Stock stock = null;

        // TODO needs better error handling
        try {
            stock = YahooFinance.get("INTC");
        } catch (IOException e) {
            e.printStackTrace();
        }

        Log.d("YahooFinance: ", stock.toString());

        return stock;
    }


}