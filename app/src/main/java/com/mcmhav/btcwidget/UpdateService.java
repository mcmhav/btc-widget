package com.mcmhav.btcwidget;

import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.widget.RemoteViews;
import android.util.Log;

import java.util.Random;

import java.util.List;
import java.util.ArrayList;

// helpers:
import com.mcmhav.btcwidget.helpers.LogH;
import com.mcmhav.btcwidget.GetUrlContentTask;


public class UpdateService extends Service {
  @Nullable
  @Override
  public IBinder onBind(Intent intent) {
      return null;
  }

  List<String> values = new ArrayList<String>();


  public void onBackgroundTaskCompleted(String result) {
    values.add(result);
    LogH.d(values.toString());
    RemoteViews view = new RemoteViews(getPackageName(), R.layout.updating_widget);
    view.setTextViewText(R.id.tvWidget, result);
    ComponentName theWidget = new ComponentName(this, UpdatingWidget.class);
    AppWidgetManager manager = AppWidgetManager.getInstance(this);
    manager.updateAppWidget(theWidget, view);
  }

  @Override
  public int onStartCommand(Intent intent, int flags, int startId) {
    LogH.d("onStartCommand");

    String url = "https://api.bitfinex.com/v1/pubticker/btcusd";

    new GetUrlContentTask(this).execute(url);

    return super.onStartCommand(intent, flags, startId);
  }
}
