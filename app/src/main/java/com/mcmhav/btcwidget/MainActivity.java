package com.mcmhav.btcwidget;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.os.StrictMode;
import android.os.PowerManager;
import android.os.Build;

// helpers:
import com.mcmhav.btcwidget.helpers.LogH;

public class MainActivity extends AppCompatActivity {

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
    StrictMode.setThreadPolicy(policy);

    setContentView(R.layout.activity_main);
  }

  @Override
  protected void onPause() {
    super.onPause();
    LogH.d("onPause");

    // If the screen is off then the device has been locked
    PowerManager powerManager = (PowerManager) getSystemService(POWER_SERVICE);
    boolean isScreenOn;
    if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT_WATCH) {
      isScreenOn = powerManager.isInteractive();
    } else {
      isScreenOn = powerManager.isScreenOn();
    }

    if (!isScreenOn) {
      LogH.d("screen is not on!!");

      // The screen has been locked
      // do stuff...
    }
  }

  @Override
  protected void onStop() {
    super.onStop();
    LogH.d("onStop");
  }
}
