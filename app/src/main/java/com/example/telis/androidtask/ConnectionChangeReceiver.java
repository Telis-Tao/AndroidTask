package com.example.telis.androidtask;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Created by Telis on 2015/6/3.
 */
public class ConnectionChangeReceiver extends BroadcastReceiver {
    private static final String TAG = ConnectionChangeReceiver.class.getName();
    private OnNetworkChangeListener onNetworkChangeListener;

    public ConnectionChangeReceiver() {
        super();
    }

    public ConnectionChangeReceiver(OnNetworkChangeListener onNetworkChangeListener) {
        super();
        this.onNetworkChangeListener = onNetworkChangeListener;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        boolean success = false;

        ConnectivityManager connectivityManager = (ConnectivityManager) context
                .getSystemService
                        (Context
                                .CONNECTIVITY_SERVICE);

        NetworkInfo.State state = connectivityManager.getNetworkInfo(ConnectivityManager
                .TYPE_WIFI).getState();
        if (NetworkInfo.State.CONNECTED == state) {
            success = true;
        }
        state = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE)
                .getState();
        if (NetworkInfo.State.CONNECTED == state) {
            success = true;
        }
        if (success && onNetworkChangeListener != null) {
            onNetworkChangeListener.onNetworkChange();
        }
    }
}
