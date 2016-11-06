package com.cse4322.mockstock;

import java.util.ArrayList;
import java.util.Map;

import yahoofinance.*;
import yahoofinance.Stock;

public interface StockUpdateAsyncResponse {

    void stockUpdateProcessFinished(ArrayList<Stock> output);
}
