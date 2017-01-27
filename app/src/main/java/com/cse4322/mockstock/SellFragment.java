package com.cse4322.mockstock;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.renderscript.ScriptGroup;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import yahoofinance.Stock;

/**
 * Created by Joseph on 1/16/2017.
 *
 * The <cde>Fragment</cde> class that is used to
 */
public class SellFragment extends Fragment implements StockUpdateAsyncResponse, TextWatcher, View.OnClickListener {
    private Stock mStock;

    private int mColorPositive;
    private int mColorNegative;
    private int mBackgroundID;

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

    /**
     * <p>Initializes the <code>View</code> member objects of this <code>Fragment</code> and sets
     * the values of the text fields. <code>Button</code>s and <code>EditText</code> are
     * assigned listeners.</p>
     * <p>This is called only once in the first execution of the
     * <code>stockUpdateProcessFinished</code> method.</p>
     */
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

        Button sellButton = (Button)getView().findViewById(R.id.sell_button);
        Button cancelButton = (Button)getView().findViewById(R.id.cancel_button);
        sellButton.setOnClickListener(this);
        cancelButton.setOnClickListener(this);
        View background = getView().findViewById(R.id.background);
        background.setOnClickListener(this);
        mBackgroundID = background.getId();

        mSellQuantity.addTextChangedListener(this);

        mIsInitialized = true;
    }

    @Override
    public void stockUpdateProcessFinished(ArrayList<Stock> output) {
        if(output.size() > 0) {
            mStock = output.get(0);
            if(!mIsInitialized) initializeTextViews();
            UserStock userStock = UserAccount.getCurrUserAccount().getUserStock(mStock.getQuote().getSymbol());
            if (userStock != null) {
                int quantity = userStock.getQuantityOwned();
                mQuantity.setText(Integer.toString(quantity));
            }
        }
    }

    @Override
    public void onClick(View view) {
        if(view.getId() == mBackgroundID) {
            InputMethodManager inputMethodManager = (InputMethodManager) getActivity().getSystemService(Activity.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), 0);
            getActivity().onBackPressed();
        }
        else if(view instanceof Button) {
            Button button = (Button)view;
            if(button.getText().toString().compareToIgnoreCase("sell") == 0) {
                int sellQty;
                String sellQtyStr = mSellQuantity.getText().toString();
                if(sellQtyStr.length() == 0) sellQty = 0; // handle case where user trys to sell with nothing entered into the
                else sellQty = Integer.valueOf(mSellQuantity.getText().toString());

                UserStock userStock = UserAccount.getCurrUserAccount().getUserStock(mStock.getSymbol());
                if(userStock != null) {
                    if(userStock.getQuantityOwned() == 0) {
                        Toast.makeText(getContext(), "No shares owned of this stock.", Toast.LENGTH_SHORT).show();
                        getActivity().onBackPressed();
                    }
                    else if(userStock.getQuantityOwned() < sellQty) {
                        Toast.makeText(getContext(), "Cannot sell more shares than are owned.", Toast.LENGTH_SHORT).show();
                    }
                    else if(sellQty == 0) getActivity().onBackPressed();
                    else {
                        UserAccount.getCurrUserAccount().sellStock(mStock.getSymbol(), sellQty, mStock.getQuote().getPrice().floatValue());
                        MainActivity activity = (MainActivity) getActivity();
                        activity.updateAccountBalance();
                        getActivity().onBackPressed();
                    }
                }
                else {
                    Toast.makeText(getContext(), "No shares owned of this stock.", Toast.LENGTH_SHORT).show();
                }
            }
            else if(button.getText().toString().compareToIgnoreCase("cancel") == 0) getActivity().onBackPressed();
        }
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        int sellQty;
        if(s.toString().length() == 0) sellQty = 0;
        else sellQty = Integer.valueOf(s.toString());
        float value = sellQty * mStock.getQuote().getPrice().floatValue();
        mValue.setText(String.format("$%.2f", value));
    }

    @Override
    public void afterTextChanged(Editable s) {}
}
