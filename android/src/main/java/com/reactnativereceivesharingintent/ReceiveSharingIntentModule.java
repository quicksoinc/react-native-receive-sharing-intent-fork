package com.reactnativereceivesharingintent;

import android.app.Activity;
import android.app.Application;
import android.content.Intent;

import java.util.*;
import android.os.Build;
import androidx.annotation.RequiresApi;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;


public class ReceiveSharingIntentModule extends ReactContextBaseJavaModule {
  public final String Log_Tag = "ReceiveSharingIntent";

  private final ReactApplicationContext reactContext;
  private ReceiveSharingIntentHelper receiveSharingIntentHelper;
  private Set<String> handledIntentIds = new HashSet<String> ();

  public ReceiveSharingIntentModule(ReactApplicationContext reactContext) {
    super(reactContext);
    this.reactContext = reactContext;
    Application applicationContext = (Application) reactContext.getApplicationContext();
    receiveSharingIntentHelper = new ReceiveSharingIntentHelper(applicationContext);
  }


  protected void onNewIntent(Intent intent) {
    Activity mActivity = getCurrentActivity();
    if(mActivity == null) { return; }
    mActivity.setIntent(intent);
  }

  @RequiresApi(api = Build.VERSION_CODES.KITKAT)
  @ReactMethod
  public void getFileNames(Promise promise){
    Activity mActivity = getCurrentActivity();
    if (mActivity == null) {
      return;
    }
    Intent intent = mActivity.getIntent();

    try {
      String intentId = intent.getIdentifier();

      if (!handledIntentIds.contains(intentId)){
        receiveSharingIntentHelper.sendFileNames(reactContext, intent, promise);

        String newIdentifier = UUID.randomUUID().toString();
        intent.setIdentifier(newIdentifier);
        handledIntentIds.add(newIdentifier);
      }
    } catch (Throwable t) {
      promise.reject("getFileNames error", t.toString());
    }
  }

  @ReactMethod
  public void clearFileNames(){
    Activity mActivity = getCurrentActivity();
    if(mActivity == null) { return; }
    Intent intent = mActivity.getIntent();
    receiveSharingIntentHelper.clearFileNames(intent);
  }


  @Override
  public String getName() {
    return "ReceiveSharingIntent";
  }
}
