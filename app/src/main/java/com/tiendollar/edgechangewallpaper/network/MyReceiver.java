package com.tiendollar.edgechangewallpaper.network;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.tiendollar.edgechangewallpaper.ui.SplashActivity;
import com.tiendollar.edgechangewallpaper.database.DataLoaderFromFireBase;

import java.lang.ref.WeakReference;

public class MyReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        String status = NetworkUtils.getConnectivityStatusString(context);
        if (status.isEmpty() || status.equals("No internet is available")) {
            status = "No Internet Connection";
            if (context instanceof SplashActivity) {
                ((SplashActivity) context).hideLoadingLayout();
            }
            Toast.makeText(context, status + " ... Please connect internet during loading process", Toast.LENGTH_SHORT).show();
        } else {
            Log.d("ahuhu", "onReceive: ");
            DataLoaderFromFireBase loader = new DataLoaderFromFireBase(new WeakReference<>(context));
            loader.loadDefaultAlbumsIfNecessary();
        }
    }
}