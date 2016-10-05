/**
 * Class Author:    Joseph Tompkins
 * Date:
 * Description:
 */
package com.cse4322.mockstock;

import android.os.AsyncTask;
import android.support.v4.util.Pair;
import android.util.Log;

import org.apache.commons.net.ftp.FTPClient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.StringTokenizer;

import yahoofinance.*;
import yahoofinance.quotes.stock.StockQuote;
import yahoofinance.quotes.stock.StockQuotesData;

public class YahooFinanceService {
    public static final String nasdaqFTP = "ftp.nasdaqtrader.com";
    public static final String nasdaqDir = "/SymbolDirectory";
    public static final String nasdaqFile = "nasdaqlisted.txt";

    private static int maxSize = 4500;
    /**
     * symbols stores the ticker symbols & company name as Pair<String, String>
     * for the particular market of interest. Since the stocks are pulled in alphabetically, this could
     * be used to implement search by name.
     */
    private static ArrayList<StockPair> stocks = new ArrayList<StockPair>(maxSize);
    private static Map<String, Stock> stocksByTicker; // TODO implement map for searching stocks.
    private static Map<String, Stock> stocksByName = new HashMap<>(); // TODO implement map for searching stocks.
    private static AsyncTask updateTask;
    private static String fileString; // a temporary variable for storing a file as a string. Only way to fit the file in memory.

    /**
     * Compiles a list of ticker symbols and company names of stocks on the NASDAQ exchange,
     * and returns in the form of <code>ArrayList<>StockPair></code>
     *
     * @return an <code>ArrayList</code> that contains <code>StockPair</code> elements. Each
     * <code>StockPair</code> contains the ticker symbol and company name of a stock on the NASDAQ exchange.
     */
    public static ArrayList<StockPair> getNASDAQList() {
        fileString = new String();
        /*
        TODO Citation
        Code retrieved from:
        http://stackoverflow.com/questions/7053513/howto-do-a-simple-ftp-get-file-on-android
        [Access Date]Sept 30, 2016
        Retrieving tickers from NASDAQ FTP.
        */
        updateTask = new AsyncTask<String, Void, Void>() {

            @Override
            protected Void doInBackground(String... params) {
                try {
                    // Make FTP connection. Using org.apache.commons.net imported library.
                    FTPClient ftpClient = new FTPClient();
                    ftpClient.connect(nasdaqFTP, 21);
                    ftpClient.login("anonymous", "password");
                    ftpClient.enterLocalPassiveMode();
                    ftpClient.changeWorkingDirectory(nasdaqDir);
                    // Get input stream
                    InputStream inStream = ftpClient.retrieveFileStream(nasdaqFile);
                    InputStreamReader reader = new InputStreamReader(inStream, "UTF-8");
                    // Get buffered reader of input stream
                    BufferedReader in = new BufferedReader(reader);

                    in.readLine(); // skip first line. First line on NASDAQ txt file is not stock data.
                    String temp = new String();
                    // read entire file and concat to str
                    while( (temp = in.readLine()) != null) {
                        fileString += temp;
                        fileString += "\n"; // put new line at end of each stock in file
                    }

                    StringTokenizer strtok;
                    // initialize tokenizer for the file
                    strtok = new StringTokenizer(fileString, "|");
                    // reads the entire file of stocks and stores the
                    int i = 0;
                    while(i < maxSize && strtok.hasMoreTokens()) {
                        // Gets the symbol and company name from the file.
                        // NOTE: this is only really working based on the file from the NASDAQ ftp.
                        //          May not work the same once other stock exchanges are implemented.
                        stocks.add(new StockPair(strtok.nextToken("|"), strtok.nextToken("-|")) );
                        if(strtok.hasMoreTokens()) strtok.nextToken("\n");
                        else break;

                        Log.d("YahooFinanceService", stocks.get(i).getTickerSymbol() + " :: " + stocks.get(i).getCompanyName());
                        i++;
                    }

                    in.close();
                    ftpClient.disconnect();
                } catch (MalformedURLException e) {
                    Log.e("YahooFinanceService", e.getLocalizedMessage());
                } catch (IOException e) {
                    Log.e("YahooFinanceService", e.getLocalizedMessage());
                }
                return null;
            }
        }.execute();

        fileString = null; // insures fileString is trashed when getNASDAQList is complete
        return stocks;
    }
}