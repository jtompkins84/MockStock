package com.cse4322.mockstock;

import java.util.ArrayList;


/**
 * Created by Joseph on 11/5/2016.
 */

public interface UserStockUpdateAsyncResponse {
    void userStockUpdateProcessFinished(ArrayList<UserStock> output);
}
