package com.cse4322.mockstock;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Looper;
import android.support.v4.content.ContextCompat;
import android.support.v4.util.DebugUtils;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.orm.SugarContext;
import com.orm.SugarDb;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Handler;
import java.util.logging.LogRecord;

import yahoofinance.*;
import yahoofinance.Stock;

public class MainActivity extends AppCompatActivity implements StockUpdateAsyncResponse, SearchView.OnQueryTextListener {
    private ArrayList<UserStock> stocklist;
    private StockListAdapter stockListAdapter;
    private ListView stockListView;
    private TextView portfolioBalance;
    private boolean doListInitial = true;
    private Timer refreshTimer;

    UserAccount current_user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.mockstock_toolbar);
        setSupportActionBar(toolbar);
        Window window = getWindow();
        window.setStatusBarColor(ContextCompat.getColor(this, R.color.statusbar));

        portfolioBalance = (TextView) findViewById(R.id.accountbalance);

        String[] testStocks = {"GRVY", "EBIO", "OSTK","TSLA", "CRIS", "MOMO"};
        new StockUpdateAsyncTask(this).execute(testStocks);

        UserAccount.getCurrUserAccount().resetAccount(); // TODO temp. remove when Buy is implemented!

        // custom stock list adapter view. Search functionality will implement the "filterable" interface
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
    public boolean onQueryTextSubmit(String query) {

        // this portion can be used to choose the closest match (which would be position 0 in the list)

        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {

        // retrieve the current filter from the current stockListAdapter
        stockListAdapter.getFilter().filter(newText);
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);

        // Associate searchable configuration with the SearchView
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.app_bar_search).getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(this.getComponentName()));

        // enabled query text listener to allow variant adapter results
        searchView.setSubmitButtonEnabled(true);
        searchView.setOnQueryTextListener(this);

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
    public void onStart() {
        super.onStart();
        updatePortfolio();
    }

    @Override
    public void onResume() {
        super.onResume();
        refreshTimer = new Timer();
        refreshTimer.schedule(new RefreshStockTask(), 5000);
        updatePortfolio();
    }

    @Override
    public void onPause() {
        super.onPause();
        refreshTimer.cancel();
        refreshTimer.purge();
    }

    @Override
    public void stockUpdateProcessFinished(ArrayList<Stock> output) {
        for(Stock stock : output) {
           // Log.v("StockUpdate Complete", stock.toString());
            UserAccount.getCurrUserAccount().buyStock(stock.getSymbol(), 10, stock.getQuote().getPrice().floatValue());
//            stockListAdapter.updateCurrUserStockList();
        }
    }

    public void updatePortfolio() {
        float bal = UserAccount.getCurrUserAccount().getBalance();
        String accBal = "$" + String.format("%.2f", bal);
        if(portfolioBalance != null) portfolioBalance.setText(accBal);
        Log.v("Portfolio Balance", "balance = " + accBal);
    }


    private class RefreshStockTask extends TimerTask {

        @Override
        public void run() {
            if(stockListAdapter != null) stockListAdapter.updateCurrUserStockList();
            refreshTimer.schedule(new RefreshStockTask(), 5000);
        }
    }
}
