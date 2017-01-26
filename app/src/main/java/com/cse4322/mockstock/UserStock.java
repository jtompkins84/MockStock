package com.cse4322.mockstock;

import android.util.Log;

import com.orm.SugarRecord;

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
    /** company symbol associated with this stock */
    private String symbol;
    /** the exchange or market that this stock's symbol is listed on */
    private String exchange;
    /** number of stocks owned */
    private int quantityOwned = 0;
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
//        List<UserStock> userStocks = SugarRecord.find(UserStock.class, "user_name = ?", userName);
//        Collections.sort(userStocks);
        List<UserStock> userStocks = UserStock.find(UserStock.class, "user_name = ?", new String[] {userName}, null, "symbol asc", null);

        return userStocks;
    }

    /**
     * Updates the stock associated with the user name.
     * @param userName the name of the user that owns the stock
     * @param yStock the <code>yahoofinance.Stock</code> with the updated stock data
     */
    public static void updateUserStock(String userName, Stock yStock) {
        List<UserStock> userStocks = SugarRecord.find(UserStock.class, "symbol = ? and user_name = ?", yStock.getSymbol(), userName);

        if(userStocks != null) userStocks.get(0).updateUserStock(yStock);
    }

    /**
     * If the user has not previously bought shares associated with the symbol symbol, a new UserStock
     * will be stored using Sugar ORM of the amount determined by the <code>amount</code> parameter.
     * <div></div>
     * Otherwise, if the user has already purchased shares, the UserStock object
     * will be loaded and updated by adding <code>amount</code> to the currently owned stock.
     * <div></div>
     * @param userName the name of the user this stock is being purchased by
     * @param ticker the symbol associated with the stock being purchased
     * @param amount the amount of stock to buy
     * @param price the price of the stock
     */
    public static UserStock buyStock(String userName, String ticker, int amount, float price) {
        // attempt to find the stock owned by userName
        UserStock userStock = null;
        try {
            userStock = SugarRecord.find(UserStock.class, "symbol = ? and user_name = ?", ticker, userName).get(0);

            userStock.addStocks(amount);
            userStock.addTotalInvestment(price * amount);
            userStock.save();
        }
        // catch the index-out-of-bounds exception that is thrown when the user does not own the referenced stock
        catch (IndexOutOfBoundsException e) {
            new UserStock(userName, ticker, amount, price); // UserStock is created and saved
        }

        return userStock;
    }

    /**
     * Attempts to search for the requested user-owned stock. When found, the quantity
     * of shares will be reduced by <code>amount</code> and the <code>totalInvestment</code>
     * will be reduced by <code>amount * price</code>. The <code>UserStock</code> is then
     * saved to the database and returned.
     * <p>If the <code>UserStock</code> is not found to exist in the database,
     * then <code>null</code> will be returned.</p>
     * @param userName the name of the user this stock is being purchased by
     * @param ticker the symbol associated with the stock being purchased
     * @param amount the amount of stock to buy
     * @param price the price of the stock
     * @return
     */
    public static UserStock sellStock(String userName, String ticker, int amount, float price) {
        // attempt to find the stock owned by userName
        UserStock userStock = null;
        try {
            userStock = SugarRecord.find(UserStock.class, "symbol = ? and user_name = ?", ticker, userName).get(0);
            userStock.addStocks((-1)*amount);
            userStock.addTotalInvestment((-1)*(amount * price));
            if(userStock.getQuantityOwned() > 0) userStock.save();
            else SugarRecord.delete(userStock);
        } catch (IndexOutOfBoundsException e) {
            e.printStackTrace();
            return null;
        }

        return userStock;
    }

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
    public UserStock(String userName, String companyName, String symbol, String exchange, int quantityOwned, float totalInvestment, float totalFees, float totalValue, float currPrice) {
        this.userName = userName;
        this.companyName = companyName;
        this.symbol = symbol;
        this.exchange = exchange;
        this.quantityOwned = quantityOwned;
        this.totalInvestment = totalInvestment;
        this.totalFees = totalFees;
        this.totalValue = totalValue;
        this.currPrice = currPrice;
    }

    private UserStock(String userName, String symbol, int amount, float price) {
        this.userName = userName;
        this.symbol = symbol;
        this.quantityOwned = amount;
        this.totalInvestment = price * amount;
        this.save();
    }

    public void updateUserStock(Stock yStock) {
        companyName = yStock.getName();
        exchange = yStock.getStockExchange();
        float price = yStock.getQuote().getPrice().floatValue();
        totalValue = quantityOwned * price;
        currPrice = price;
        this.save();
    }

    /**
     * Attempts to refresh this <code>UserStock</code>'s data to match the database.
     * @return <code>false</code> if no matching <code>UserStock</code> was found in the database, or if
     * a null value is encountered while attempting to refresh.
     * Returns <code>true</code> otherwise.
     */
    public boolean refresh() {
        List<UserStock> userStocks = UserStock.find(UserStock.class, "user_name = ? and symbol = ?", this.userName, this.symbol);

        try {
            // userStocks should only have one element.
            quantityOwned = userStocks.get(0).getQuantityOwned();
            totalInvestment = userStocks.get(0).getTotalInvestment();
            totalFees = userStocks.get(0).getTotalFees();
            totalValue = userStocks.get(0).getTotalValue();
            currPrice = userStocks.get(0).getCurrPrice();
        } catch (IndexOutOfBoundsException e) {
            return false;
        } catch (NullPointerException e) {
            Log.e(getClass().getSimpleName(), "A null value was encountered while attempting to refresh.");
            return false;
        }

        return true;
    }

    /**
     * Adds <code>amount</code> to the number of stocks owned.
     *
     * @param amount must be greater than zero
     */
    public void addStocks(int amount) {
        this.quantityOwned += amount;
    }

    public void addTotalInvestment(float amount) {
        totalInvestment += amount;
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

    public String getSymbol() {
        return symbol;
    }

    public String getExchange() {
        return exchange;
    }

    public int getQuantityOwned() {
        return quantityOwned;
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
