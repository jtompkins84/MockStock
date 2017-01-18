package com.cse4322.mockstock;

import android.app.SearchManager;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import yahoofinance.Stock;


public class MainActivity extends AppCompatActivity implements StockUpdateAsyncResponse, SearchView.OnQueryTextListener {
    private PortfolioFragment mPortfolioFragment;
    private Menu mMainMenu;
    private SearchView searchView;
    private TextView portfolioBalance;
    private boolean doListInitial = true;
    private boolean doSearchUpdate = true;
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

        try {
            UserAccount.createUserAccount(null);
        } catch (UserAccount.UserAlreadyExistsException e) {
            e.printStackTrace();
        }

        portfolioBalance = (TextView)findViewById(R.id.accountbalance);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        // this portion can be used to choose the closest match (which would be position 0 in the list)
        query = query.toUpperCase();
        String[] testStocks = {query};
        if(doSearchUpdate) {
                new StockUpdateAsyncTask(this).execute(testStocks);
                doSearchUpdate = false;
        }

        getSupportFragmentManager().popBackStackImmediate("search_results", FragmentManager.POP_BACK_STACK_INCLUSIVE);
        searchView.onActionViewCollapsed();
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        return false;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        mMainMenu = menu;

        // Associate searchable configuration with the SearchView
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchView = (SearchView) menu.findItem(R.id.app_bar_search).getActionView();

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
        if (id == R.id.reset_account) {
            UserAccount.getCurrUserAccount().resetAccount();
            updatePortfolio();
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
        if(mPortfolioFragment != null && mPortfolioFragment.isVisible()) mPortfolioFragment.notifyDataSetChanged();
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
            try {
                SearchResultFragment searchResultFragment = new SearchResultFragment();
                Bundle args = new Bundle();
                ArrayList<CharSequence> tickers = new ArrayList<>();
                for(Stock s : output) {
                    tickers.add(s.getSymbol());
                }
                args.putCharSequenceArrayList("tickers", tickers);
                searchResultFragment.setArguments(args);

                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.stockListView, searchResultFragment);
                transaction.addToBackStack("search_results");
                transaction.commit();

            } catch (NullPointerException e) {
                e.printStackTrace();
                Toast.makeText(getApplicationContext(), "No ticker \'" + stock.getSymbol() + "\' was found.", Toast.LENGTH_LONG).show();
                return;
            } finally {
                doSearchUpdate = true;
            }
        }
    }

    public void updatePortfolio() {
        float bal = UserAccount.getCurrUserAccount().getBalance();
        String accBal = String.format("$%.2f", bal);
        if(portfolioBalance != null) portfolioBalance.setText(accBal);
        Log.v("Portfolio Balance", "balance = " + accBal);
    }

    /**
     * Runs the stock data refresh
     */
    private class RefreshStockTask extends TimerTask {
        @Override
        public void run() {
            if(mPortfolioFragment != null && mPortfolioFragment.isVisible()) mPortfolioFragment.updatePortfolioStockList();
            refreshTimer.schedule(new RefreshStockTask(), 8000);
        }
    }
}
