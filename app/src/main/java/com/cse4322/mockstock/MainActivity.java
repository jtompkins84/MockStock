package com.cse4322.mockstock;

import android.app.SearchManager;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import yahoofinance.Stock;


public class MainActivity extends AppCompatActivity implements SearchView.OnQueryTextListener, View.OnClickListener {
    private PortfolioFragment mPortfolioFragment;
    private SearchView mSearchView;
    private SearchResultAdapter mSearchResultAdapter;
    private TextView portfolioBalance;
    private Timer mRefreshTimer;

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

        mPortfolioFragment = (PortfolioFragment) getSupportFragmentManager().findFragmentById(R.id.stockListView);

        portfolioBalance = (TextView)findViewById(R.id.accountbalance);
    }

    public void updateAccountBalance() {
        float bal = UserAccount.getCurrUserAccount().getBalance();
        String accBal = String.format("$%.2f", bal);
        if(portfolioBalance != null) portfolioBalance.setText(accBal);
        Log.v("Portfolio Balance", "balance = " + accBal);

        int backStackCount = getSupportFragmentManager().getBackStackEntryCount();
        Log.d(this.getClass().getSimpleName(), Integer.toString(backStackCount));
    }

    public void collapseSearchView(String backstack) {
        mSearchView.onActionViewCollapsed();
        getSupportFragmentManager().popBackStackImmediate(backstack, 0);
    }

    public void refreshPortfolioList() {
        mPortfolioFragment.updatePortfolioStockList();
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        // this portion can be used to choose the closest match (which would be position 0 in the list)
        if(mSearchResultAdapter != null) mSearchResultAdapter.submitQuery(query);

        collapseSearchView("search_results");
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        if(mSearchResultAdapter != null) mSearchResultAdapter.submitQuery(newText);

        return false;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);

        // Associate searchable configuration with the SearchView
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        mSearchView = (SearchView) menu.findItem(R.id.app_bar_search).getActionView();

        // enabled query text listener to allow variant adapter results
        mSearchView.setSubmitButtonEnabled(true);
        mSearchView.setOnQueryTextListener(this);
        mSearchView.setOnSearchClickListener(this);

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
            updateAccountBalance();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onStart() {
        super.onStart();
        updateAccountBalance();
    }

    @Override
    public void onResume() {
        super.onResume();
        mRefreshTimer = new Timer();
        mRefreshTimer.schedule(new RefreshStockTask(), 5000);
        if(mPortfolioFragment != null && mPortfolioFragment.isVisible()) mPortfolioFragment.notifyDataSetChanged();
        updateAccountBalance();
    }

    @Override
    public void onPause() {
        super.onPause();
        mRefreshTimer.cancel();
        mRefreshTimer.purge();
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == mSearchView.getId()) {

            SearchResultFragment searchResultFragment = new SearchResultFragment();
            mSearchResultAdapter = new SearchResultAdapter(this);
            searchResultFragment.setSearchResultAdapter(mSearchResultAdapter);
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.stockListView, searchResultFragment);
            transaction.addToBackStack("search_results");
            transaction.commit();
        }
    }

    /**
     * Runs the stock data refresh
     */
    private class RefreshStockTask extends TimerTask {
        @Override
        public void run() {
            int fragCount = mPortfolioFragment.getFragmentManager().getBackStackEntryCount();
            if(mPortfolioFragment != null && fragCount == 0) mPortfolioFragment.updatePortfolioStockList();
            mRefreshTimer.schedule(new RefreshStockTask(), 8000);
        }
    }


}
