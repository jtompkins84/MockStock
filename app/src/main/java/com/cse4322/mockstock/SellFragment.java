package com.cse4322.mockstock;

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.orm.SugarRecord;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

import yahoofinance.Stock;

/**
 * Created by Joseph on 1/16/2017.
 */

public class SellFragment extends Fragment implements StockUpdateAsyncResponse{
    private Stock mStock;
    private UserStock mUserStock;

    private int mColorPositive;
    private int mColorNegative;

    private TextView mTicker;
    private TextView mCmpyName;
    private TextView mPrice;
    private TextView mQuantity;
    private TextView mValue;
    private EditText mSellQuantity;

    private boolean mIsInitialized = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_sell, container, false);

        Bundle args = getArguments();
        if(args != null)
            new StockUpdateAsyncTask(this).execute(new String[] {args.getCharSequence("ticker").toString()});

        return view;
    }

    private void initializeTextViews() {
        mTicker = (TextView) getView().findViewById(R.id.symbol);
        mCmpyName = (TextView) getView().findViewById(R.id.company_name);
        mPrice = (TextView) getView().findViewById(R.id.price);
        mQuantity = (TextView) getView().findViewById(R.id.quantity);
        mValue = (TextView) getView().findViewById(R.id.value);
        mSellQuantity = (EditText) getView().findViewById(R.id.sell_quantity);

        mColorPositive = getResources().getColor(R.color.positive);
        mColorNegative = getResources().getColor(R.color.negative);

        mTicker.setText(mStock.getSymbol());
        mCmpyName.setText(mStock.getName());
        mCmpyName.setSelected(true);
        try {
            float price = mStock.getQuote().getPrice().floatValue();
            int color  = mColorPositive;
            if(price < 0) {
                color = mColorNegative;
                price = Math.abs(price);
            }
            mPrice.setText(String.format("$%.2f", price));
            mPrice.setTextColor(color);
        } catch (NullPointerException e) {
            e.printStackTrace();
        }

        mIsInitialized = true;
    }

    @Override
    public void stockUpdateProcessFinished(ArrayList<Stock> output) {
        if(output.size() > 0) {
            mStock = output.get(0);
            if(!mIsInitialized) initializeTextViews();
            UserAccount curUser = UserAccount.getCurrUserAccount();
            List<UserStock> userStocks = SugarRecord.find(UserStock.class, "user_name = ? and ticker = ?", curUser.getUserName(), output.get(0).getSymbol());
            if (userStocks.size() > 0) {
//                mQuantity.setText(userStocks.get(0).getNumberOwned());
            }
        }
    }
}
