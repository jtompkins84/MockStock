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
import android.widget.Toast;

import com.orm.SugarContext;
import com.orm.SugarDb;

import java.util.ArrayList;
import java.util.Map;

import yahoofinance.*;
import yahoofinance.Stock;

public class MainActivity extends AppCompatActivity implements StockUpdateAsyncResponse, UserStockUpdateAsyncResponse{
    private ArrayList<UserStock> stocklist;
    private StockListAdapter stockListAdapter;
    private ListView stockListView;
    private boolean doListInitial = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        Toolbar toolbar = (Toolbar) findViewById(R.id.mockstock_toolbar);
//        setSupportActionBar(toolbar);
        Window window = getWindow();
        window.setStatusBarColor(ContextCompat.getColor(this, R.color.statusbar));

        String[] testStocks = {"GRVY", "EBIO", "OSTK"};
        testStocks = new String[]{"TSLA", "CRIS", "MOMO"};
        new StockUpdateAsyncTask(this).execute(testStocks);

//        UserAccount.getCurrUserAccount().resetAccount();

        stockListAdapter = new StockListAdapter(MainActivity.this, UserAccount.getCurrUserAccount().getUserStocks(true));
        stockListView = (ListView) findViewById(R.id.stockListView);
        stockListView.setAdapter(stockListAdapter);

        stockListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long l) {
                UserStock item = (UserStock) parent.getItemAtPosition(position);
                Intent i = new Intent(MainActivity.this, StockDetails.class);
                i.putExtra("name", item.getCompanyName());
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
    public void stockUpdateProcessFinished(ArrayList<Stock> output) {
        for(Stock stock : output) {
            Log.v("StockUpdate Complete", stock.toString());
//            UserAccount.getCurrUserAccount().buyStock(stock.getSymbol(), 10, stock.getQuote().getPrice().floatValue());
            stockListAdapter.updateCurrUserStockList();
        }
    }

    @Override
    public void userStockUpdateProcessFinished(ArrayList<UserStock> output) {
    }
}
