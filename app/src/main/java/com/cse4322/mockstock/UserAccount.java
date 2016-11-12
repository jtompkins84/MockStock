package com.cse4322.mockstock;

import android.database.sqlite.SQLiteException;
import android.widget.TextView;

import com.orm.SugarRecord;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by joseph on 10/2/16.
 */

public class UserAccount extends SugarRecord {
    public final static String DEFAULT_USER_NAME = "default";
    private static UserAccount currUserAccount;

    /** The name associated with the user's account. Must be unique. */
    private String userName;
    /** account balance */
    private float balance = 20000.0f;

    /**
     * Attempts to retrieve the user account specified by <code>userName</code>.
     * @param userName the unique user name of the desired account
     * @return the specified account
     * @throws UserDoesNotExistException
     */
    public static UserAccount getUserAccount(String userName) throws UserDoesNotExistException {
        List<UserAccount> userAccounts = UserAccount.find(UserAccount.class, "user_name = ?", userName);
        UserAccount account = null;

        try {
            account = userAccounts.get(0);
        }
        catch(Exception e) {
            throw new UserDoesNotExistException("User name \'" + userName + "\' does not exist.");
        }

        return account;
    }

    /**
     * Get the current user account.
     * @return the <code>UserAccount</code> object associated with the current user account
     */
    public static UserAccount getCurrUserAccount(){
        if(currUserAccount == null) {
            try {
                currUserAccount = getUserAccount(DEFAULT_USER_NAME);
            } catch (UserDoesNotExistException e) {
                try {
                    currUserAccount = createUserAccount(DEFAULT_USER_NAME);
                    return currUserAccount;
                } catch (UserAlreadyExistsException e1) {
                    e1.printStackTrace();
                }
            }
        }

        return currUserAccount;
    }

    /**
     * Attempts to create a user account associated with a unique user name. If successful, the
     * current user account is switched to the created account.
     * @param userName Must be a unique name. If <code>null</code>, <code>UserAccount.DEFAULT_USER_NAME</code> is used.
     * @return <code>null</code> if the <code>UserAccount</code> object with user name equal to <code>userName</code> already exists.
     * Otherwise the new account is returned.
     * @throws UserAlreadyExistsException
     */
    public static UserAccount createUserAccount(String userName) throws UserAlreadyExistsException {
        if(userName == null) userName = UserAccount.DEFAULT_USER_NAME;
        UserAccount result = null;

        try {
            result = new UserAccount(userName);
            currUserAccount = result;
        }
        catch (UserAlreadyExistsException e) {
            result = null;
            throw e;
        }
        finally {
            return result;
        }
    }


    /**
     * DO NOT USE! This constructor exists for SugarRecord compatibility. Use <code>UserAccount.createAccount</code> instead.
     */
    @Deprecated
    public UserAccount() {}

    /**
     * DO NOT USE! This constructor exists for SugarRecord compatibility. Use <code>UserAccount.createAccount</code> instead.
     */
    @Deprecated
    public UserAccount(String userName, float balance) {
        this.userName = userName;
        this.balance = balance;
    }

    private UserAccount(String userName) throws UserAlreadyExistsException {
        try {
            try {
                getUserAccount(userName);
            }
            catch(SQLiteException e) {
                this.userName = userName;
                save();
                return;
            }

            // if getUserAccount does not throw UserDoesNotExistException, then the user account
            // already exists and cannot be instantiated.
            throw new UserAlreadyExistsException("User name \'" + userName + "\' already exists.");
        }
        catch (UserDoesNotExistException e) {
            this.userName = userName;
            save();
        }
    }

    public void buyStock(String ticker, int amount, float price) {
        balance -= price * amount;
        UserStock.buyStock(userName, ticker, amount, price);
    }

    // TODO implement sellStock

    public ArrayList<UserStock> getUserStocks(boolean doSort) {
        if(doSort) return (ArrayList<UserStock>) UserStock.getSortedUserStocks(userName);
        else return (ArrayList<UserStock>) UserStock.getUserStocks(userName);
    }

    public void resetAccount() {
        balance = 20000.0f;
        SugarRecord.deleteAll(UserStock.class, "user_name = ?", this.userName);
    }

    public String getUserName() {
        return userName;
    }

    public float getBalance() {
        return balance;
    }

    public static class UserAlreadyExistsException extends Exception {
        public UserAlreadyExistsException(String message) {
            super(message);
        }
    }
    public static class UserDoesNotExistException extends Exception {
        public UserDoesNotExistException(String message) {
            super(message);
        }
    }

    private static class CurrUserAccount extends SugarRecord {
        protected String userName;


    }
}
