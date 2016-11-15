package com.cse4322.mockstock;

import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.app.WindowDecorActionBar;
import android.support.v7.widget.Toolbar;
import android.view.Window;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.math.BigDecimal;
import java.util.ArrayList;

import yahoofinance.Stock;


public class StockDetails extends AppCompatActivity implements StockUpdateAsyncResponse{
    Stock stock;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stock_content);

        WindowDecorActionBar winDecorActBar;

        Toolbar toolbar = null;

        try {
            toolbar = (Toolbar) findViewById(R.id.mockstock_toolbar);
            setSupportActionBar(toolbar);

        } catch (Exception e) {
            e.printStackTrace();
        }

        String ticker = getIntent().getExtras().getString("ticker");
        new StockUpdateAsyncTask(this).execute(new String[]{ticker});
    }

    @Override
    public void stockUpdateProcessFinished(ArrayList<Stock> output) {
        stock = output.get(0);
        updateStockDetails();
    }

    public void updateStockDetails() {
        TextView cmpyname = (TextView) findViewById(R.id.companyname);
        TextView tickername = (TextView) findViewById(R.id.stockticker);
        TextView price = (TextView) findViewById(R.id.stock_price);
        TextView amountChange = (TextView) findViewById(R.id.amtchange);
        TextView percentChange = (TextView) findViewById(R.id.amtpctchange);
        TextView askPrice = (TextView) findViewById(R.id.askprice2amt);
        TextView highPrice = (TextView) findViewById(R.id.highpriceamt);
        TextView openPrice = (TextView) findViewById(R.id.openpriceamt);
        TextView bidPrice = (TextView) findViewById(R.id.bidpriceamt);
        TextView high52Week = (TextView) findViewById(R.id.high52weekamt);
        TextView low52Week = (TextView) findViewById(R.id.low52weekamt);
        TextView annualYeild = (TextView) findViewById(R.id.yearreturnamt);
        TextView yearTarget = (TextView) findViewById(R.id.year1targetamt);
        TextView volume = (TextView) findViewById(R.id.volumeamt);
        TextView marketCap = (TextView) findViewById(R.id.marketcapamt);
        TextView dividend = (TextView) findViewById(R.id.dividendamt);
        TextView peRatio = (TextView) findViewById(R.id.peratioamt);
        int positiveColor = getResources().getColor(R.color.positive);
        int negativeColor = getResources().getColor(R.color.negative);
        int color;

        cmpyname.setText(stock.getName());
        tickername.setText(stock.getSymbol());
        float temp = stock.getQuote().getPrice().floatValue();
        price.setText(String.format("%.2f", temp));
        temp = stock.getQuote().getChange().floatValue();

        amountChange.setText(String.format("%.2f", Math.abs(temp)));
        temp = stock.getQuote().getChangeInPercent().floatValue();
        percentChange.setText(String.format("%.2f", temp) + "%");
        if(temp >= 0.0f) color = positiveColor;
        else color = negativeColor;
        amountChange.setTextColor(color);
        percentChange.setTextColor(color);

        temp = stock.getQuote().getAsk().floatValue();
        askPrice.setText(String.format("%.2f", temp));
        temp = stock.getQuote().getBid().floatValue();
        bidPrice.setText(String.format("%.2f", temp));
        temp = stock.getQuote().getDayHigh().floatValue();
        highPrice.setText(String.format("%.2f", temp));
        try {
            temp = stock.getQuote().getOpen().floatValue();
            openPrice.setText(String.format("%.2f", temp));
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
        temp = stock.getQuote().getYearHigh().floatValue();
        high52Week.setText(String.format("%.2f", temp));
        temp = stock.getQuote().getYearLow().floatValue();
        low52Week.setText(String.format("%.2f", temp));
        if(stock.getDividend().getAnnualYield() != null) {
            temp = stock.getDividend().getAnnualYield().floatValue();
            annualYeild.setText(String.format("%.2f", temp));
        }

        temp = stock.getStats().getOneYearTargetPrice().floatValue();
        yearTarget.setText(String.format("%.2f", temp));
        long tempLong = stock.getQuote().getVolume().longValue();

        String magnitude = "";
        if(tempLong >= 1000000L && tempLong <= 999999999L) {
            magnitude = "M";
            tempLong /= 1000000L;
        }
        else if(tempLong >= 1000000000L && tempLong <= 999999999999L) {
            magnitude = "B";
            tempLong /= 1000000000L;
        }
        volume.setText(String.format("%2d", tempLong) + magnitude );

        try {
            temp = stock.getStats().getMarketCap().scaleByPowerOfTen(-6).floatValue();
            if (temp < 1000.0f) magnitude = "M";
            else {
                temp = stock.getStats().getMarketCap().scaleByPowerOfTen(-9).floatValue();
                magnitude = "B";
            }
            marketCap.setText(String.format("%.2f", temp) + magnitude);
        } catch (NullPointerException e) { e.printStackTrace(); }

        try {
            temp = stock.getDividend().getAnnualYieldPercent().floatValue();
            dividend.setText(String.format("%.2f", temp));
        } catch (NullPointerException e) { e.printStackTrace(); }

        try {
            temp = stock.getStats().getPe().floatValue();
            peRatio.setText(String.format("%.2f", temp));
        }catch (NullPointerException e) { e.printStackTrace(); }
    }
}
