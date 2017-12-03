package com.mcmhav.btcwidget;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;

import android.widget.RemoteViews;

// helpers:
import com.mcmhav.btcwidget.helpers.LogH;

/**
 * Implementation of App Widget functionality.
 */
public class UpdatingWidget extends AppWidgetProvider {
  private PendingIntent service;
  private final int updateInterval = 60000;
  private static final String ACTION_SIMPLEAPPWIDGET = "ACTION_BROADCASTWIDGETSAMPLE";

  static void updateAppWidget(Context context, AppWidgetManager appWidgetManager, int appWidgetId) {

    // Construct the RemoteViews object
    RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.updating_widget);
    // Construct an Intent which is pointing this class.
    Intent intent = new Intent(context, UpdatingWidget.class);
    intent.setAction(ACTION_SIMPLEAPPWIDGET);
    // And this time we are sending a broadcast with getBroadcast
    PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

    views.setOnClickPendingIntent(R.id.tvWidget2, pendingIntent);
    // Instruct the widget manager to update the widget
    appWidgetManager.updateAppWidget(appWidgetId, views);
  }

  @Override
  public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
    LogH.breakerTop();

    final AlarmManager manager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
    final Intent i = new Intent(context, UpdateService.class);

    if (service == null) {
        service = PendingIntent.getService(context, 0, i, PendingIntent.FLAG_CANCEL_CURRENT);
    }
    manager.setRepeating(
      AlarmManager.ELAPSED_REALTIME,
      SystemClock.elapsedRealtime(),
      updateInterval,
      service
    );

    LogH.d("onUpdate");
    for (int appWidgetId : appWidgetIds) {
      updateAppWidget(context, appWidgetManager, appWidgetId);
    }
  }

  @Override
  public void onReceive(Context context, Intent intent) {
    super.onReceive(context, intent);
    LogH.d("onReceive");
    if (ACTION_SIMPLEAPPWIDGET.equals(intent.getAction())) {
      LogH.d("calling service?!");
      Intent i = new Intent(context, UpdateService.class);
      context.startService(i);
    }
  }

  @Override
  public void onAppWidgetOptionsChanged(Context context, AppWidgetManager appWidgetManager, int appWidgetId, Bundle newOptions) {
    super.onAppWidgetOptionsChanged(context, appWidgetManager, appWidgetId, newOptions);
    LogH.d("onAppWidgetOptionsChanged");
  }

  @Override
  public void onDeleted(Context context, int[] appWidgetIds) {
    super.onDeleted(context, appWidgetIds);
    LogH.d("onDeleted");
  }

  @Override
  public void onEnabled(Context context) {
    super.onEnabled(context);
    LogH.d("onEnabled");
  }

  @Override
  public void onDisabled(Context context) {
    super.onDisabled(context);
    LogH.d("onDisabled");
  }

  @Override
  public void onRestored(Context context, int[] oldWidgetIds, int[] newWidgetIds) {
    super.onRestored(context, oldWidgetIds, newWidgetIds);
    LogH.d("onRestored");
  }
}
