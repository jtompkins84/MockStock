package com.cse4322.mockstock;

import android.os.AsyncTask;
import android.util.JsonReader;
import android.util.JsonToken;
import android.util.Log;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

/**
 * Created by Joseph on 1/26/2017.
 */

public class StockSearchAsyncTask extends AsyncTask<String, Void, ArrayList<String>> {
    private static final int TIMEOUT = 1000;
    private static final String REQUEST_BASE = "http://d.yimg.com/aq/autoc?";
    private static final String REGION_US = "&region=US";
    private static final String LANG_EN_US = "&lang=en-US";
    private static final String CALLBACK = "&callback=YAHOO.Finance.SymbolSuggest.ssCallback";

    private StockSearchAsyncResponse mDelegate;

    public StockSearchAsyncTask(StockSearchAsyncResponse delegate) {
        mDelegate = delegate;
    }

    @Override
    protected ArrayList<String> doInBackground(String... params) {
        if(params.length > 0) {
            ArrayList<String> searchResult = new ArrayList<>();
            String query = "query=" + params[0];
            String url = REQUEST_BASE + query + REGION_US + LANG_EN_US + CALLBACK;

            try {
                URL request = new URL(url);
                URLConnection connection = request.openConnection();
                connection.setConnectTimeout(TIMEOUT);
                connection.setReadTimeout(TIMEOUT);
                InputStreamReader isr = new InputStreamReader(connection.getInputStream());

                // Parse JSON
                JsonReader jsonReader = new JsonReader(isr);
                jsonReader.setLenient(true);
                // Finds the "Result" JSON Array and tells the JSON Reader to begin reading the array.
                while(jsonReader.hasNext()) {
                    Log.d(getClass().getSimpleName(), "jsonReader.peek = " + jsonReader.peek().toString());
                    if(jsonReader.peek() == JsonToken.NAME) {
                        if(jsonReader.nextName().compareTo("Result") == 0) {
                            jsonReader.beginArray(); // Begin "Result" array
                            break;
                        }
                    }
                    else if(jsonReader.peek() == JsonToken.BEGIN_OBJECT) jsonReader.beginObject();
                    else jsonReader.skipValue();
                }
                // Iterates through the array, adding all stock symbols to mSearchResult
                while(jsonReader.hasNext()) {
                    jsonReader.beginObject(); // Begin next object in "Result" array
                    jsonReader.nextName(); // "symbol"
                    String symbol = jsonReader.nextString();
                    Log.d(getClass().getSimpleName(), "search result: symbol = " + symbol);
                    if(symbol.matches("[a-zA-Z]++")) searchResult.add(symbol);
                    while(jsonReader.hasNext()) jsonReader.skipValue();
                    jsonReader.endObject();
                }

                return searchResult;
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }

        return null;
    }

    @Override
    protected void onPostExecute(ArrayList<String> output) {
        mDelegate.stockSearchProcessFinished(output);
    }
}
