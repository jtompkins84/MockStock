package com.cse4322.mockstock;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import yahoofinance.Stock;

/**
 * Created by Joseph on 1/17/2017.
 */

public class BuyFragment extends Fragment implements StockUpdateAsyncResponse, View.OnClickListener, TextWatcher {
    private int mBackgroundID;

    private int mColorPositive;
    private int mColorNegative;
    private int mBuyCapacity;

    private Stock mStock;

    private TextView mTicker;
    private TextView mCmpyName;
    private TextView mPrice;
    private TextView mCapacity;
    private TextView mCost;
    private EditText mBuyQuantity;

    private boolean mIsInitialized = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_buy, container, false);

        Bundle args = getArguments();
        if(args != null)
            new StockUpdateAsyncTask(this).execute(new String[] {args.getCharSequence("ticker").toString()});

        return view;
    }

    private void initializeTextViews() {
        mTicker = (TextView) getView().findViewById(R.id.symbol);
        mCmpyName = (TextView) getView().findViewById(R.id.company_name);
        mPrice = (TextView) getView().findViewById(R.id.price);
        mCapacity = (TextView) getView().findViewById(R.id.capacity);
        mCost = (TextView) getView().findViewById(R.id.value);
        mBuyQuantity = (EditText) getView().findViewById(R.id.buy_quantity);

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

        mBuyQuantity.addTextChangedListener(this);

        Button buyButton = (Button)getView().findViewById(R.id.buy_button);
        Button cancelButton = (Button)getView().findViewById(R.id.cancel_button);
        buyButton.setOnClickListener(this);
        cancelButton.setOnClickListener(this);

        View background = getView().findViewById(R.id.background);
        background.setOnClickListener(this);
        mBackgroundID = background.getId();

        mIsInitialized = true;
    }

    @Override
    public void stockUpdateProcessFinished(ArrayList<Stock> output) {
        if(output.size() > 0) {
            mStock = output.get(0);
            if(!mIsInitialized) initializeTextViews();

            float currPrice;
            try {
                currPrice = mStock.getQuote().getPrice().floatValue();
                float userBalance = UserAccount.getCurrUserAccount().getBalance();
                mBuyCapacity = (int)(userBalance / currPrice);
                mCapacity.setText(Integer.toString(mBuyCapacity));
            } catch (NullPointerException e) {
                Toast.makeText(getContext(), "The price is unavailable for this stock.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onClick(View view) {
        if(view.getId() == mBackgroundID) getActivity().onBackPressed();
        else if(view instanceof Button) {
            Button button = (Button)view;
            if(button.getText().toString().compareToIgnoreCase("buy") == 0) {
                int buyQty;
                if(mBuyQuantity.getText().toString().length() == 0) buyQty = 0;
                else buyQty = Integer.valueOf(mBuyQuantity.getText().toString());

                if(mBuyCapacity < buyQty) {
                    Toast.makeText(getContext(), "You do not have enough funds. Max purchase capacity is " + mBuyCapacity + " shares.", Toast.LENGTH_LONG).show();
                }
                else if(buyQty == 0) {
                    getActivity().onBackPressed();
                }
                else {
                    UserAccount.getCurrUserAccount().buyStock(mStock.getSymbol(), buyQty, mStock.getQuote().getPrice().floatValue());
                    MainActivity activity = (MainActivity) getActivity();
                    activity.updateAccountBalance();
                    getActivity().onBackPressed();
                }
            }
            else if(button.getText().toString().compareToIgnoreCase("cancel") == 0) {
                getActivity().onBackPressed();
            }
        }
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        int buyQty;
        if(s.toString().length() == 0) buyQty = 0;
        else buyQty = Integer.valueOf(s.toString());
        float currCost = mStock.getQuote().getPrice().floatValue() * buyQty;

        mCost.setText(String.format("$%.2f", currCost));
    }

    @Override
    public void afterTextChanged(Editable s) {}
}
