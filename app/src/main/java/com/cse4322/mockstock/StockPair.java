package com.cse4322.mockstock;

import android.util.Pair;

import yahoofinance.Stock;

/**
 * Wrapper class for a pair of <code>String</code> objects.
 */

public class StockPair implements Comparable{
    private Pair<String, String> stock;
    /**
     * Constructor for a Pair.
     *
     * @param symbol   <code>String</code> representing the company's ticker symbol
     * @param name <code>String</code> representing the company's name.
     */
    public StockPair(String symbol, String name) {
        stock = new Pair<String, String>(symbol, name);
    }

    /**
     * Gets company name.
     * @return <code>String</code>
     */
    public String getCompanyName() {
        return this.stock.second;
    }

    /**
     * Gets company ticker symbol.
     * @return <code>String</code>
     */
    public String getTickerSymbol() {
        return this.stock.first;
    }

    @Override
    /**
     * Compares using the stock ticker symbols and returns same output as a
     * <code>String.compareTo</code>.
     * <div>Note: this class has a natural ordering that is inconsistent with equals.</div>
     * @param o <code>StockPair</code> to compare to.
     * @return <code>Integer.MIN_VALUE</code> if <code>o</code> is not an instance of <code>StockPair</code>.
     * <p>Otherwise, same as  <code>String.compareTo</code> method.</p>
     */
    public int compareTo(Object o) {
        if(o instanceof StockPair) {
            StockPair sp = (StockPair) o;
            return getTickerSymbol().compareToIgnoreCase(sp.getTickerSymbol());
        }

        return Integer.MIN_VALUE;
    }
}
