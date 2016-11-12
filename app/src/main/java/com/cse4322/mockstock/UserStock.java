package com.cse4322.mockstock;

import com.orm.SugarRecord;

import java.util.Collections;
import java.util.List;

import yahoofinance.Stock;

/**
 * Created by Joseph on 11/4/2016.
 *
 * This class is designed to store data relevant to stocks owned by the user, and are stored on the
 * local device using Sugar ORM. When retrieving UserStock objects, a user name is always required.
 */
public class UserStock extends SugarRecord implements Comparable<UserStock> {
    /** the name that this UserStock belongs to */


    private String userName;

    /** company name associated with this stock */
    private String companyName;
    /** company ticker associated with this stock */
    private String ticker;
    /** the exchange or market that this stock's ticker is listed on */
    private String exchange;
    /** number of stocks owned */
    private int numberOwned = 0;
    /** the amount in USD the user has invested in this stock */
    private float totalInvestment = 0.0f;
    /** the amount in fees, included as a part of the total investment */
    private float totalFees = 0.0f; // TODO Ignoring fees for now. May implement eventually.
    /** total value of stocks */
    private float totalValue = 0.0f;
    /** current price of stock */
    private float currPrice = 0.0f;

    /**
     * Gets a unsorted list of <code>UserStock</code>s belonging to <code>userName</code>
     * that have been stored using Sugar ORM.
     * @param userName the name of the user the stocks are owned by
     * @return the <code>List</code> of unsorted stocks
     */
    public static List<UserStock> getUserStocks(String userName) {
        List<UserStock> userStocks = SugarRecord.find(UserStock.class, "user_name = ?", userName);

        return userStocks;
    }

    /**
     * Gets a sorted list of <code>UserStock</code>s belonging to <code>userName</code>
     * that have been stored using Sugar ORM.
     * @param userName the name of the user the stocks are owned by
     * @return the <code>List</code> of sorted stocks
     */
    public static List<UserStock> getSortedUserStocks(String userName) {
        List<UserStock> userStocks = SugarRecord.find(UserStock.class, "user_name = ?", userName);
        Collections.sort(userStocks);

        return userStocks;
    }

    /**
     * Updates the stock associated with the user name.
     * @param userName the name of the user that owns the stock
     * @param yStock the <code>yahoofinance.Stock</code> with the updated stock data
     */
    public static void updateUserStock(String userName, Stock yStock) {
        List<UserStock> userStocks = SugarRecord.find(UserStock.class, "ticker = ? and user_name = ?", yStock.getSymbol(), userName);

        if(userStocks != null) userStocks.get(0).updateUserStock(yStock);
    }

    /**
     * If the user has not previously bought stock associated with the ticker, a new UserStock
     * will be stored using Sugar ORM of the amount determined by the <code>amount</code> parameter.
     * <div></div>
     * Otherwise, if the user has already purchased stock under the ticker, the UserStock object
     * will be loaded and updated by adding <code>amount</code> to the currently owned stock.
     * <div></div>
     * @param userName the name of the user this stock is being purchased by
     * @param ticker the ticker associated with the stock being purchased
     * @param amount the amount of stock to buy
     * @param price the price of the stock
     */
    public static void buyStock(String userName, String ticker, int amount, float price) {
        // attempt to find the stock owned by userName
        try {
            UserStock uStock = SugarRecord.find(UserStock.class, "ticker = ? and user_name = ?", ticker, userName).get(0);

            uStock.addStocks(amount);
            uStock.addTotalInvestment(price * amount);
            uStock.save();
        }
        // catch the index-out-of-bounds exception that is thrown when the user does not own the referenced stock
        catch (IndexOutOfBoundsException e) {
            new UserStock(userName, ticker, amount, price); // UserStock is created and saved
        }
    }

    // TODO implement sellStock

    /*
            INSTANCE METHODS
     */


    /**
     * DO NOT USE! This constructor exists for SugarRecord compatibility. Use <code>buyStock</code> instead.
     */
    @Deprecated
    public UserStock() {}

    /**
     * DO NOT USE! This constructor exists for SugarRecord compatibility. Use <code>buyStock</code> instead.
     */
    @Deprecated
    public UserStock(String userName, String companyName, String ticker, String exchange, int numberOwned, float totalInvestment, float totalFees, float totalValue, float currPrice) {
        this.userName = userName;
        this.companyName = companyName;
        this.ticker = ticker;
        this.exchange = exchange;
        this.numberOwned = numberOwned;
        this.totalInvestment = totalInvestment;
        this.totalFees = totalFees;
        this.totalValue = totalValue;
        this.currPrice = currPrice;
    }

    private UserStock(String userName, String ticker, int amount, float price) {
        this.userName = userName;
        this.ticker = ticker;
        this.numberOwned = amount;
        this.totalInvestment = price * amount;
        this.save();
    }

    public void updateUserStock(Stock yStock) {
        companyName = yStock.getName();
        exchange = yStock.getStockExchange();
        float price = yStock.getQuote().getPrice().floatValue();
        totalValue = numberOwned * price;
        currPrice = price;
        this.save();
    }

    /**
     * Adds <code>amount</code> to the number of stocks owned.
     *
     * @param amount must be greater than zero
     */
    public void addStocks(int amount) {
        if(amount > 0) this.numberOwned += amount;
    }

    public void addTotalInvestment(float amount) {
        if(amount > 0) totalInvestment += amount;
    }

    /*
            GETTERS & SETTERS
     */

    public float getGainLoss() {
        return totalValue - totalInvestment;
    }

    public float getGainLossPercent() {
        return (totalValue - totalInvestment) / totalValue;
    }

    public String getCompanyName() {
        return companyName;
    }

    public String getTicker() {
        return ticker;
    }

    public String getExchange() {
        return exchange;
    }

    public int getNumberOwned() {
        return numberOwned;
    }

    public float getTotalInvestment() {
        return totalInvestment;
    }

    public float getTotalFees() {
        return totalFees;
    }

    public float getTotalValue() {
        return totalValue;
    }

    public float getCurrPrice() {
        return currPrice;
    }

    @Override
    public int compareTo(UserStock anotherUserStock) {
        if(companyName == null && anotherUserStock.getCompanyName() == null) return 0;
        else if(companyName == null) return -1;
        else if(anotherUserStock.getCompanyName() == null) return 1;
        return companyName.compareToIgnoreCase(anotherUserStock.getCompanyName());
    }
}
