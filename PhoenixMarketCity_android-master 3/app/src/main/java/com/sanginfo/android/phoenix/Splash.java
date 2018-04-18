package com.sanginfo.android.phoenix;

import android.Manifest;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.sanginfo.android.phoenix.R;
import com.sanginfo.intripper.libraryproject.ui.MapActivity;
import com.sanginfo.intripper.libraryproject.ui.VideoPreviewActivity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

//import io.fabric.sdk.android.Fabric;

public class Splash extends AppCompatActivity {

    private View mDecorView;
    private DevicePolicyManager mDpm;
    private boolean mIsKioskEnabled = false;

    private String[] permissions = new String[]{
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.ACCESS_WIFI_STATE,
            Manifest.permission.ACCESS_NETWORK_STATE,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.WAKE_LOCK,
            Manifest.permission.RECEIVE_BOOT_COMPLETED
    };
    private Handler handler = new Handler();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       // Fabric.with(this, new Crashlytics());
        try{
            setContentView(R.layout.activity_splash);

            mDecorView = getWindow().getDecorView();


            ComponentName deviceAdmin = new ComponentName(this, AdminReceiver.class);
            mDpm = (DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE);
            if (!mDpm.isAdminActive(deviceAdmin)) {
              //  Toast.makeText(this, getString(R.string.not_device_admin), Toast.LENGTH_SHORT).show();
            }

            if (mDpm.isDeviceOwnerApp(getPackageName())) {
                mDpm.setLockTaskPackages(deviceAdmin, new String[]{getPackageName()});
            } else {
             //   Toast.makeText(this, getString(R.string.not_device_owner), Toast.LENGTH_SHORT).show();
            }

            enableKioskMode(!mIsKioskEnabled);


            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (!hasPermissions()){
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            requestPermissions(permissions,101);
                        }
                    }
                    else{
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                openMapScreen();
                            }
                        },3000);
                    }
                }
            },1000);
        }
        catch (Exception ex){
            ex.printStackTrace();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        boolean permissionGranted = true;
        for (int grantResult : grantResults) {
            if (grantResult == PackageManager.PERMISSION_DENIED) {
                permissionGranted = false;
                break;
            }
        }
        if (!permissionGranted) {
           // Toast.makeText(getApplicationContext(), "You have not granted one or more permissions. App may not behave as expected.", Toast.LENGTH_LONG).show();
        }
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                openMapScreen();
            }
        },250);
    }

    private void openMapScreen(){
        try{
            Intent intent;
            intent = new Intent( getApplicationContext(), MapActivity.class );
            startActivity( intent);
            overridePendingTransition(R.anim.open_next,R.anim.close_main);
            finish();
        }
        catch (Exception ex){
            ex.printStackTrace();
        }
    }

    public boolean hasPermissions(){
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        }
        for (String p : permissions) {
            if (PackageManager.PERMISSION_DENIED == checkSelfPermission(p)) {
                return false;
            }
        }
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        hideSystemUI();
    }

    // This snippet hides the system bars.
    private void hideSystemUI() {
        // Set the IMMERSIVE flag.
        // Set the content to appear under the system bars so that the content
        // doesn't resize when the system bars hide and show.
        mDecorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
                        | View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        );
    }

    private void enableKioskMode(boolean enabled) {
        try {
            if (enabled) {
                if (mDpm.isLockTaskPermitted(this.getPackageName())) {
                    startLockTask();
                    mIsKioskEnabled = true;
                   // mButton.setText(getString(R.string.exit_kiosk_mode));
                } else {
                  //  Toast.makeText(this, getString(R.string.kiosk_not_permitted), Toast.LENGTH_SHORT).show();
                }
            } else {
                stopLockTask();
                mIsKioskEnabled = false;
               // mButton.setText(getString(R.string.enter_kiosk_mode));
            }
        } catch (Exception e) {
            // TODO: Log and handle appropriately
        }
    }

    private final List blockedKeys = new ArrayList(Arrays.asList(KeyEvent.KEYCODE_VOLUME_DOWN, KeyEvent.KEYCODE_VOLUME_UP));

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (blockedKeys.contains(event.getKeyCode())) {
            return true;
        } else {
            return super.dispatchKeyEvent(event);
        }
    }
}
