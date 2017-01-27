package com.cse4322.mockstock;

import java.util.ArrayList;

/**
 * Created by Joseph on 1/26/2017.
 */

public interface StockSearchAsyncResponse {
    /**
     * <p>Called during the <code>onPostExecute</code> method of a <code>StockSearchAsyncTask</code>.</p>
     * <p>Implement the delegate's response to the output of the task here.</p>
     * @param output contains the stock symbols that resulted from the user's search
     */
    void stockSearchProcessFinished(ArrayList<String> output);
}
