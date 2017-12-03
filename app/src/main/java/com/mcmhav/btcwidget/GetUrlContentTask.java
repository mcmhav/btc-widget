package com.mcmhav.btcwidget;

import android.os.AsyncTask;
import android.app.Service;

import java.net.URL;
import java.net.HttpURLConnection;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.InputStream;

import java.io.IOException;
import java.net.MalformedURLException;

import org.json.JSONObject;

// widget:
import android.widget.RemoteViews;
import android.content.ComponentName;
import android.appwidget.AppWidgetManager;

// helpers:
import com.mcmhav.btcwidget.helpers.LogH;

public class GetUrlContentTask extends AsyncTask<String, Integer, String> {
  protected String doInBackground(String... urls) {

    HttpURLConnection connection = null;
    BufferedReader reader = null;

    try {
      URL url = new URL(urls[0]);
      connection = (HttpURLConnection) url.openConnection();
      connection.connect();


      InputStream stream = connection.getInputStream();

      reader = new BufferedReader(new InputStreamReader(stream));

      StringBuffer buffer = new StringBuffer();
      String line = "";

      while ((line = reader.readLine()) != null) {
        buffer.append(line+"\n");
      }

      String response = buffer.toString();

      return response;
    } catch (MalformedURLException e) {
        e.printStackTrace();
    } catch (IOException e) {
        e.printStackTrace();
    } finally {
      if (connection != null) {
        connection.disconnect();
      }
      try {
        if (reader != null) {
          reader.close();
        }
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
    return null;
  }

  protected void onProgressUpdate(Integer... progress) {}

  UpdateService caller;

  GetUrlContentTask(UpdateService caller) {
      this.caller = caller;
  }

  protected void onPostExecute(String result) {
    LogH.d("onPostExecute");
    LogH.d(result);

    String mid = "";

    try {
      JSONObject jsonReader = new JSONObject(result);
      mid = jsonReader.getString("mid");
      String bid = jsonReader.getString("bid");
      String ask = jsonReader.getString("ask");
      String last_price = jsonReader.getString("last_price");
      String low = jsonReader.getString("low");
      String high = jsonReader.getString("high");

      // LogH.d(mid);
      // LogH.d(bid);
      // LogH.d(ask);
      // LogH.d(last_price);
      // LogH.d(low);
      // LogH.d(high);
    } catch(Exception e) {
      e.printStackTrace();
    }

    caller.onBackgroundTaskCompleted(mid);
  }
}
