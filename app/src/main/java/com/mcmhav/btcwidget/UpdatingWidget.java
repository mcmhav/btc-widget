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

  // graph vars:
  private float mPixelDensity;
  private TreeMap<Integer, RemoteViews> mRemoteViews;
  private Settings mSettings;

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

    int[] appWidgetIds = null;
    Bundle extras = intent.getExtras();
    if (extras != null) {
      appWidgetIds = extras.getIntArray(AppWidgetManager.EXTRA_APPWIDGET_IDS);
    }
    if (appWidgetIds == null) {
      appWidgetIds = AppWidgetManager.getInstance(context).getAppWidgetIds(
          new ComponentName(context, BatteryGraphWidgetProvider.class)
      );
    }

    mSettings = Settings.get(context);
    if (mRemoteViews == null) {
      mPixelDensity = context.getResources().getDisplayMetrics().density;
      mRemoteViews = new TreeMap<>();
      for (int appWidgetId : appWidgetIds) {
        mRemoteViews.put(
          appWidgetId,
          new RemoteViews(context.getPackageName(), R.layout.widget)
        );
      }
    }

    if (appWidgetIds != null) {
      for (int appWidgetId : appWidgetIds) {
        AppWidgetManager.getInstance(context).updateAppWidget(
            appWidgetId, mRemoteViews.get(appWidgetId)
        );
      }
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

  // graph?:
  private void refreshGraph(Context context, int[] appWidgetIds) {
    for (int appWidgetId : appWidgetIds) {
      Intent intent = new Intent(context, SettingsActivity.class);
      intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
      intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
      PendingIntent pendingIntent = PendingIntent.getActivity(
        context,
        appWidgetId,
        intent,
        PendingIntent.FLAG_CANCEL_CURRENT
      );

      mRemoteViews
        .get(appWidgetId)
        .setOnClickPendingIntent(R.id.image, pendingIntent);

      Settings.GraphSettings graphSettings = mSettings.getGraphSettings(appWidgetId);
      int numMinutes = graphSettings.getNumHours() * 60;
      Bitmap bmp = renderGraph(context, graphSettings, numMinutes);
      mRemoteViews.get(appWidgetId).setImageViewBitmap(R.id.image, bmp);
    }
  }

  private Bitmap renderGraph(Context context, Settings.GraphSettings graphSettings, int numMinutes) {
    final int width = (int) (graphSettings.getGraphWidth() * mPixelDensity);
    final int height = (int) (graphSettings.getGraphHeight() * mPixelDensity);
    if (width == 0 || height == 0) {
      return null;
    }

    Bitmap bmp = Bitmap.createBitmap(width, height, Config.ARGB_8888);
    Canvas canvas = new Canvas(bmp);

    int graphHeight = height;
    if (graphSettings.showTimeScale()) {
      graphHeight -= 10 * mPixelDensity;
    }

    List<BatteryStatus> batteryHistory = BatteryStatus.getHistory(
      context,
      0,
      graphSettings.getNumHours()
    );

    int numGraphsShowing = 0;
    List<GraphPoint> batteryChargePoints = null;

    if (graphSettings.showBatteryGraph()) {
      batteryChargePoints = renderChargeGraph(batteryHistory, numMinutes, width,
          graphHeight, Color.GREEN);
      numGraphsShowing++;
    }

    if (batteryChargePoints != null) {
      drawGraphBackground(batteryChargePoints, canvas, width, graphHeight);
    }

    if (batteryChargePoints != null) {
      drawGraphLine(batteryChargePoints, canvas);
    }

    String text = "";
    if (graphSettings.showBatteryGraph() && batteryHistory.size() > 0) {
      text = String.format(Locale.US, "%d%%", (int) (batteryHistory.get(0).getChargeFraction() * 100.0f));
    }

    Paint paint = new Paint();
    paint.setAntiAlias(true);
    paint.setTextSize(20.0f * mPixelDensity);
    paint.setColor(Color.WHITE);
    paint.setStyle(Style.FILL);
    paint.setStrokeWidth(mPixelDensity);
    float textWidth = paint.measureText(text);
    canvas.drawText(text, width - textWidth - 4, graphHeight - 4, paint);

    return bmp;
  }

  private void drawGraphBackground(List<GraphPoint> points, Canvas canvas, int width, int zeroValue) {
    Path path = new Path();
    path.moveTo(width, zeroValue);
    for (GraphPoint pt : points) {
      path.lineTo(pt.x, pt.y);
    }
    path.lineTo(0, zeroValue);
    path.lineTo(width, zeroValue);
    Paint paint = new Paint();
    paint.setAntiAlias(true);
    paint.setARGB(128, 255, 0, 0);
    paint.setStyle(Style.FILL);
    canvas.drawPath(path, paint);
  }

  private void drawGraphLine(List<GraphPoint> points, Canvas canvas) {
    Paint paint = new Paint();
    paint.setAntiAlias(true);
    paint.setStyle(Style.STROKE);
    paint.setAlpha(255);
    paint.setStrokeWidth(4.0f);
    Path path = null;
    int colour = Color.BLACK;
    for (GraphPoint pt : points) {
      if (pt.colour != colour || path == null) {
        if (path != null) {
          path.lineTo(pt.x, pt.y);
          paint.setColor(colour);
          canvas.drawPath(path, paint);
          colour = pt.colour;
        }
        path = new Path();
        path.moveTo(pt.x, pt.y);
      } else {
        path.lineTo(pt.x, pt.y);
      }
    }
    if (path != null) {
      paint.setColor(colour);
      canvas.drawPath(path, paint);
    }
  }

  private static class GraphPoint {
    public float x;
    public float y;
    public int colour;

    public GraphPoint(float x, float y, int colour) {
      this.x = x;
      this.y = y;
      this.colour = colour;
    }
  }
}
