package com.cse4322.mockstock;

import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.app.WindowDecorActionBar;
import android.support.v7.widget.Toolbar;
import android.view.Window;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.ArrayList;

import yahoofinance.Stock;


public class StockDetails extends AppCompatActivity implements StockUpdateAsyncResponse{

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
        Stock stock = output.get(0);
        TextView cmpyname = (TextView) findViewById(R.id.companyname);
        TextView tickername = (TextView) findViewById(R.id.stockticker);
        TextView price = (TextView) findViewById(R.id.stock_price);
        TextView amountChange = (TextView) findViewById(R.id.amtchange);
        TextView percentChange = (TextView) findViewById(R.id.amtpctchange);
        // TextView

        cmpyname.setText(stock.getName());
        tickername.setText(stock.getSymbol());
    }
}
