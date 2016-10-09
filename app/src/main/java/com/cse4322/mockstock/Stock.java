package com.cse4322.mockstock;

import java.io.Serializable;

/**
 * Created by Winston on 10/9/2016.
 */

public class Stock {
    private Stock stock;
    private String ticker;
    private String company;
    private String market;
    private String gain;
    private String pct;
    private String value;
    private String price;

    public Stock(String ticker, String company, String market, String gain, String pct, String value, String price) {
        this.ticker = ticker;
        this.company = company;
        this.market = market;
        this.gain = gain;
        this.pct = pct;
        this.value = value;
        this.price = price;
    }

    public Stock getStock() {
        return stock;
    }

    public void setStock(Stock stock) {
        this.stock = stock;
    }

    public String getTicker() {
        return ticker;
    }

    public void setTicker(String ticker) {
        this.ticker = ticker;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getMarket() {
        return market;
    }

    public void setMarket(String market) {
        this.market = market;
    }

    public String getGain() {
        return gain;
    }

    public void setGain(String gain) {
        this.gain = gain;
    }

    public String getPct() {
        return pct;
    }

    public void setPct(String pct) {
        this.pct = pct;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }
}
