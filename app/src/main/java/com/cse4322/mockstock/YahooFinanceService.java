/**
 * Class Author:    Joseph Tompkins
 * Date:
 * Description:
 */
package com.cse4322.mockstock;

import android.net.wifi.WifiConfiguration;
import android.os.AsyncTask;
import android.util.Log;

import org.apache.commons.net.ftp.FTPClient;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.lang.reflect.Array;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.StringTokenizer;

import yahoofinance.*;

public class YahooFinanceService {
    /**
     * maximum number of stocks to be loaded into ram at once.
     */
    private static int maxStocks = 80;
    /**
     * maximum number of stocks on a page.
     */
    private static int maxOnPage = maxStocks / 2;
    private static int curPage = 1;
    public static final String nasdaqURL = "ftp.nasdaqtrader.com";
    public static final String nasdaqDir = "/SymbolDirectory";
    public static final String nasdaqFile = "nasdaqlisted.txt";


    private static Stock[] stocks = new Stock[maxStocks];

    public static void updateStocks() {
        final String[] tickers = new String[maxOnPage];

        /*
        TODO Citation
        Code retrieved from:
        http://stackoverflow.com/questions/7053513/howto-do-a-simple-ftp-get-file-on-android
        [Access Date]Sept 30, 2016

        Retrieving tickers from NASDAQ FTP
        */
        new AsyncTask<String, Void, Void>() {
            String str;

            @Override
            protected Void doInBackground(String... params) {
                StringTokenizer strtok;
                int pageStart = maxOnPage * (curPage - 1);

                try {
                    // Create a URL for the desired page
                    FTPClient ftpClient = new FTPClient();
                    ftpClient.connect(nasdaqURL, 21);
                    ftpClient.login("anonymous", "password");
                    ftpClient.changeWorkingDirectory(nasdaqDir);

                    InputStream inStream = ftpClient.retrieveFileStream(nasdaqFile);
                    InputStreamReader reader = new InputStreamReader(inStream, "UTF-8");

                    BufferedReader in = new BufferedReader(reader);

//                    BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));
                    in.readLine(); // skip first line. First line on NASDAQ txt file is not stock data.


                    // loop skips ahead in file to scope of the "current page".
                    for (int i = 0; i < pageStart; i++) {
                        str = in.readLine();
                        if (str == null)
                            return null; // RETURN empty handed if the end of the file is reached.
                    }

                    for (int i = 0; i < maxOnPage; i++) {
                        str = in.readLine();
                        strtok = new StringTokenizer(str, "|", false);

                        tickers[i] = strtok.nextToken();
                        Log.d("YahooFinanceService", " Found ticker \"" + tickers[i] + "\"");
                    }

                    in.close();
                } catch (MalformedURLException e) {
                    Log.e("YahooFinanceService", e.getLocalizedMessage());
                } catch (IOException e) {
                    Log.e("YahooFinanceService", e.getLocalizedMessage());
                }

                int j; // iterative variable set starting index; either first or second half of 'stocks' array.
                int end; // ending index
                if (curPage % 2 != 0) {
                    j = 0;
                    end = maxOnPage;
                } else {
                    j = maxOnPage - 1;
                    end = maxStocks;
                }
                try {
                    for (int i = 0; j < end && i < tickers.length; i++, j++) {

                        stocks[j] = YahooFinance.get(tickers[i]);
                        if (stocks[j].getName() != null) {
                            Log.d("YahooFinanceService", stocks[j].toString() + " : " + stocks[j].getName());
                        }
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }

                return null;
            }
        }.execute(nasdaqURL);
    }
}
