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

import android.widget.ProgressBar;
import android.view.View;

// helpers:
import com.mcmhav.btcwidget.helpers.LogH;
import com.mcmhav.btcwidget.GetUrlContentTask;


public class UpdateService extends Service {
  @Nullable
  @Override
  public IBinder onBind(Intent intent) {
      return null;
  }

  private ProgressBar spinner;

  List<String> values = new ArrayList<String>();
  boolean isRunning = false;

  public void onBackgroundTaskCompleted(String result) {
    RemoteViews view = new RemoteViews(
      getPackageName(),
      R.layout.updating_widget
    );

    if (result != "") {
      values.add(result);
      if (values.size() > 10) {
        values.remove(0);
      }
      LogH.d(values.toString());
      view.setTextViewText(R.id.tvWidget, result);
    }

    view.setViewVisibility(R.id.progressBar1, View.GONE);

    ComponentName theWidget = new ComponentName(this, UpdatingWidget.class);
    AppWidgetManager manager = AppWidgetManager.getInstance(this);
    manager.updateAppWidget(theWidget, view);
    isRunning = false;
  }

  @Override
  public int onStartCommand(Intent intent, int flags, int startId) {
    LogH.breakerSmall();
    LogH.d("onStartCommand");

    if (!isRunning) {
      isRunning = true;
      RemoteViews view = new RemoteViews(
        getPackageName(),
        R.layout.updating_widget
      );
      view.setViewVisibility(R.id.progressBar1, View.VISIBLE);
      ComponentName theWidget = new ComponentName(this, UpdatingWidget.class);
      AppWidgetManager manager = AppWidgetManager.getInstance(this);
      manager.updateAppWidget(theWidget, view);

      String url = "https://api.bitfinex.com/v1/pubticker/btcusd";

      new GetUrlContentTask(this).execute(url);
    }

    return super.onStartCommand(intent, flags, startId);
  }
}
