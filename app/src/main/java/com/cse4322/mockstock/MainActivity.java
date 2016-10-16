package com.cse4322.mockstock;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Map;

import yahoofinance.*;
import yahoofinance.Stock;

public class MainActivity extends AppCompatActivity implements StockUpdateAsyncResponse{


    private ArrayList<com.cse4322.mockstock.Stock> stocklist;
    private ListAdapter stockListAdapter;
    ListView stockListView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Window window = getWindow();
        window.setStatusBarColor(ContextCompat.getColor(this, R.color.statusbar));

        String[] testStocks = {"ABAX", "ACST", "BNFT", "CDW"};
        new StockUpdateAsyncTask(this).execute(testStocks);


        com.cse4322.mockstock.Stock stock1 = new com.cse4322.mockstock.Stock("AAL", "American Air", "nasdaq", "$3.0", "34.0%", "$3300", "$43.00");
        com.cse4322.mockstock.Stock stock2 = new com.cse4322.mockstock.Stock("FB", "Facebook", "nasdaq", "$69.50", "7.0%", "$6300", "$120.00");
        stocklist = new ArrayList<>();

        stocklist.add(stock1);
        stocklist.add(stock2);
        stocklist.add(stock1);
        stocklist.add(stock2);
        stocklist.add(stock1);
        stocklist.add(stock2);
        stocklist.add(stock1);
        stocklist.add(stock2);
        stocklist.add(stock1);
        stocklist.add(stock2);
        stocklist.add(stock1);
        stocklist.add(stock2);
        stocklist.add(stock1);

        stockListAdapter = new StockListAdapter(MainActivity.this, stocklist);
        stockListView = (ListView) findViewById(R.id.stockListView);
        stockListView.setAdapter(stockListAdapter);

        stockListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long l) {
                com.cse4322.mockstock.Stock item = (com.cse4322.mockstock.Stock) parent.getItemAtPosition(position);
                Intent i = new Intent(MainActivity.this, StockDetails.class);
                i.putExtra("name", item.getCompany());
                i.putExtra("ticker", item.getTicker());
                startActivity(i);

            }
        });





    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void processFinished(ArrayList<Stock> output) {
        for(Stock stock : output) {
            Log.v("StockUpdate Complete", stock.toString());
        }
    }
}
