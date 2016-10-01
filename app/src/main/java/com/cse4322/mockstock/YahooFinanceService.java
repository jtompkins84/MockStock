/**
 * Class Author:    Joseph Tompkins
 * Date:
 * Description:
 */
package com.cse4322.mockstock;

import android.annotation.TargetApi;
import android.net.wifi.WifiConfiguration;
import android.os.AsyncTask;
import android.os.Build;
import android.security.NetworkSecurityPolicy;
import android.support.annotation.NonNull;
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
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.StringTokenizer;

import yahoofinance.*;

public class YahooFinanceService {
    /**
     * maximum number of stocks to be loaded into ram at once.
     */
    private static int maxStocks = 10;
    /**
     * maximum number of stocks on a page.
     */
    private static int maxOnPage = maxStocks / 2;
    private static int curPage = 1;
    public static final String nasdaqURL = "ftp.nasdaqtrader.com";
    public static final String nasdaqDir = "/SymbolDirectory";
    public static final String nasdaqFile = "nasdaqlisted.txt";


    private static String[] tickers = new String[maxStocks];
    private static Map<String, Stock> stocksByTicker;
    private static AsyncTask task;

    @TargetApi(Build.VERSION_CODES.M)
    public static void updateStocks() {
        /*
        TODO Citation
        Code retrieved from:
        http://stackoverflow.com/questions/7053513/howto-do-a-simple-ftp-get-file-on-android
        [Access Date]Sept 30, 2016

        Retrieving tickers from NASDAQ FTP
        */
        task = new AsyncTask<String, Void, Void>() {

            @Override
            protected Void doInBackground(String... params) {
                StringTokenizer strtok;
                String str;
                int pageStart = maxOnPage * (curPage - 1);

                try {
                    // Make FTP connection
                    FTPClient ftpClient = new FTPClient();
                    ftpClient.connect(nasdaqURL, 21);
                    ftpClient.login("anonymous", "password");
                    ftpClient.enterLocalPassiveMode();
                    ftpClient.changeWorkingDirectory(nasdaqDir);
                    // Get input stream
                    InputStream inStream = ftpClient.retrieveFileStream(nasdaqFile);
                    InputStreamReader reader = new InputStreamReader(inStream, "UTF-8");
                    // Get buffered reader of input stream
                    BufferedReader in = new BufferedReader(reader);

                    in.readLine(); // skip first line. First line on NASDAQ txt file is not stock data.

                    // loop skips ahead in file to scope of the "current page".
                    for (int i = 0; i < pageStart; i++) {
                        str = in.readLine();
                        if (str == null)
                            return null; // RETURN empty handed if the end of the file is reached.
                    }

                    int i; // iterative variable set starting index; either first or second half of 'stocks' array.
                    int end; // ending index
                    if ((curPage - 1) % 2 == 0) {
                        i = 0;
                        end = maxOnPage;
                    } else {
                        i = maxOnPage;
                        end = maxStocks;
                    }

                    for (; i < end; i++) {
                        str = in.readLine();
                        strtok = new StringTokenizer(str, "|", false);

                        tickers[i] = strtok.nextToken();
                        Log.d("YahooFinanceService", " Found ticker \"" + tickers[i] + "\"");
                    }

                    ftpClient.disconnect();
                    in.close();
                } catch (MalformedURLException e) {
                    Log.e("YahooFinanceService", e.getLocalizedMessage());
                } catch (IOException e) {
                    Log.e("YahooFinanceService", e.getLocalizedMessage());
                }

                try {
                    stocksByTicker = YahooFinance.get(tickers);

                    for(String s : tickers) {
                        if(s != null) Log.d("YahooFinanceService", stocksByTicker.get(s).toString());
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (NullPointerException e) {
                    e.printStackTrace();
                }


                return null;
            }
        }.execute();
    }

    public static void getNextPage() {
        if(task.getStatus() == AsyncTask.Status.FINISHED) {
            curPage++;
            updateStocks();
        }
        // If task is not FINISHED, create second thread to wait for completion before executing.
        else {
            new AsyncTask<Void, Void, Void>() {
                @Override
                protected Void doInBackground(Void... params) {
                    while (task.getStatus() != AsyncTask.Status.FINISHED) ;
                    curPage++;
                    updateStocks();
                    return null;
                }
            }.execute();
        }
    }
}