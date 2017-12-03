package com.mcmhav.btcwidget.helpers;

import android.util.Log;

public class LogH {

  public static final String TAG = "BCTWidget";

  public static void breakerTop() {
    Log.i(TAG, "╔═════════════════════════════════════════════");
  }

  public static void breaker() {
    Log.i(TAG, "╠═════════════════════════════════════════════");
  }

  public static void breakerSmall() {
    Log.i(TAG, "╟─────────────────────────────────────────────");
  }

  public static void breakerBottom() {
    Log.i(TAG, "╚═════════════════════════════════════════════");
  }

  public static void empty() {
    Log.i(TAG, "");
  }

  public static void i(String message) {
    Log.i(TAG, "║ " + message);
  }

  public static void iList(String message) {
    Log.i(TAG, "╟─ " + message);
  }

  public static void d(String message) {
    Log.d(TAG, "║ " + message);
  }

  public static void e(String message) {
    Log.e(TAG, "║ " + message);
  }

  public static void eStack(String message, Exception e) {
    Log.e(TAG, "║ " + message);
    LogH.e(Log.getStackTraceString(e));
  }
}
