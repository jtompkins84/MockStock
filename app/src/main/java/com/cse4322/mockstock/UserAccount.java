package com.cse4322.mockstock;

import com.orm.SugarRecord;

import java.util.ArrayList;

/**
 * Created by joseph on 10/2/16.
 */

public class UserAccount extends SugarRecord {
    private float balance; // TODO add accessor methods --> [get:set]
    private ArrayList<StockPair> purchased; // TODO add accessor methods --> [add:get]

    public UserAccount(){
    }

    public UserAccount(float balance){
        this.balance = balance;
    }

    public float getBalance() {
        return balance;
    }

    public void setBalance(float balance) {
        this.balance = balance;
    }
}
