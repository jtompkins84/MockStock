package com.cse4322.mockstock;

import android.widget.TextView;

import java.security.AccessControlContext;
import java.security.AccessController;

import static java.security.AccessController.getContext;

/**
 * Created by Joseph on 12/6/2016.
 */

public class PortfolioStockTextController {
    private TextView gainLoss;
    private TextView quantity;
    private TextView totalValue;
    private TextView curPrice;
    private int positiveColor;
    private int negativeColor;

    public PortfolioStockTextController(TextView gainLoss, TextView quantity,
                                        TextView totalValue, TextView curPrice,
                                        int positiveColor, int negativeColor) {
        this.gainLoss = gainLoss;
        this.quantity = quantity;
        this.totalValue = totalValue;
        this.curPrice = curPrice;
        this.positiveColor = positiveColor;
        this.negativeColor = negativeColor;
    }

    public void updateText(UserStock stock) {

    }
}
