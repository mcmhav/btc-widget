package com.mcmhav.btcwidget;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.os.StrictMode;

// helpers:
import com.mcmhav.btcwidget.helpers.LogH;

public class MainActivity extends AppCompatActivity {

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    LogH.breakerTop();

    StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
    StrictMode.setThreadPolicy(policy);

    setContentView(R.layout.activity_main);
  }
}
