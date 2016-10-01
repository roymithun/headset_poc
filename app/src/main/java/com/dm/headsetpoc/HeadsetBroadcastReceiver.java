package com.dm.headsetpoc;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;

/**
 * Created by mithun.roy on 7/27/16.
 */
public class HeadsetBroadcastReceiver extends BroadcastReceiver {

    private static final String TAG = HeadsetBroadcastReceiver.class.getSimpleName();

    @Override
    public void onReceive(Context context, Intent intent) {

        Log.i(TAG, "I have received call");
//        Bundle bundle = intent.getExtras();
//        KeyEvent keyEvent = (KeyEvent) bundle.get(Intent.EXTRA_KEY_EVENT);
//        if (keyEvent.getAction() == KeyEvent.ACTION_DOWN) {
//            Log.i(TAG, "keyCode = "+keyEvent.getKeyCode());
//        }
    }
}
