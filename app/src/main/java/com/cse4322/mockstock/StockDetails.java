package com.cse4322.mockstock;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class StockDetails extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stock_content);

        String companyname = getIntent().getExtras().getString("name");
        String ticker = getIntent().getExtras().getString("ticker");

        TextView cmpyname = (TextView) findViewById(R.id.companyname);
        TextView tickername = (TextView) findViewById(R.id.stockticker);
        cmpyname.setText(companyname);
        tickername.setText(ticker);
    }
}
