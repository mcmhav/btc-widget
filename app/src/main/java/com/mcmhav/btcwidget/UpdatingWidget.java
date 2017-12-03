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

import com.mcmhav.btcwidget.helpers.LogH;

/**
 * Implementation of App Widget functionality.
 */
public class UpdatingWidget extends AppWidgetProvider {
  private PendingIntent service;

  @Override
  public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
    final AlarmManager manager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
    final Intent i = new Intent(context, UpdateService.class);

    if (service == null) {
        service = PendingIntent.getService(context, 0, i, PendingIntent.FLAG_CANCEL_CURRENT);
    }
    manager.setRepeating(AlarmManager.ELAPSED_REALTIME, SystemClock.elapsedRealtime(), 60000, service);
    //if you need to call your service less than 60 sec
    //answer is here:
    //http://stackoverflow.com/questions/29998313/how-to-run-background-service-after-every-5-sec-not-working-in-android-5-1
    LogH.d("onUpdate");
  }

  @Override
  public void onReceive(Context context, Intent intent) {
    super.onReceive(context, intent);
    LogH.d("onReceive");
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
