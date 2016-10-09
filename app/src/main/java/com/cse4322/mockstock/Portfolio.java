package com.cse4322.mockstock;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class Portfolio extends AppCompatActivity {
    private ArrayList<Stock> stocklist;
    private ListAdapter stockListAdapter;
    ListView stockListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_portfolio);
        Stock stock1 = new Stock("AAL", "American Air", "nasdaq", "$3.0", "34.0%", "$3300", "$43.00");
        Stock stock2 = new Stock("FB", "Facebook", "nasdaq", "$69.50", "7.0%", "$6300", "$120.00");
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

        stockListAdapter = new StockListAdapter(Portfolio.this, stocklist);
        stockListView = (ListView) findViewById(R.id.stockListView);
        stockListView.setAdapter(stockListAdapter);

        stockListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long l) {
                Stock item = (Stock) parent.getItemAtPosition(position);
                Intent i = new Intent(Portfolio.this, StockDetails.class);
                i.putExtra("name", item.getCompany());
                i.putExtra("ticker", item.getTicker());
                startActivity(i);

            }
        });
    }
}
