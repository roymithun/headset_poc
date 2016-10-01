package com.dm.headsetpoc;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();

    Messenger _mediaButtonService;
    final Messenger _targetMessenger = new Messenger(new MediaButtonHandler());
    private boolean _isBound;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        doBindMediaButtonService();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
        doUnbindMediaButtonService();
    }


    private ServiceConnection _serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.i(TAG, "service connected");
            _mediaButtonService = new Messenger(service);

            try {
                Message msg = Message.obtain(null, MediaButtonService.MSG_REGISTER_CLIENT);
                msg.replyTo = _targetMessenger;
                _mediaButtonService.send(msg);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            _mediaButtonService = null;
            Log.i(TAG, "service disconnected");
        }
    };

    private void doBindMediaButtonService() {
        bindService(new Intent(this,
                MediaButtonService.class), _serviceConnection, Context.BIND_AUTO_CREATE);
        _isBound = true;
    }

    private void doUnbindMediaButtonService() {
        if (_isBound) {
            // Detach our existing connection.
            unbindService(_serviceConnection);
            _isBound = false;
        }
    }

    class MediaButtonHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == MediaButtonService.MSG_MEDIA_BUTTON_DOWN) {
                Log.i(TAG, "action down");
            }
        }
    }
}
