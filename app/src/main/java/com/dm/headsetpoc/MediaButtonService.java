package com.dm.headsetpoc;

import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.session.PlaybackState;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.os.SystemClock;
import android.support.v4.media.session.MediaButtonReceiver;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.util.Log;
import android.view.KeyEvent;

public class MediaButtonService extends Service {
    static final int MSG_REGISTER_CLIENT = 1;
    static final int MSG_UNREGISTER_CLIENT = 2;
    static final int MSG_MEDIA_BUTTON_DOWN = 3;

    private MediaSessionCompat mMediaSessionCompat;

    private Messenger _messenger;
    private Messenger _targetMessenger = new Messenger(new IncomingHandler());

    public MediaButtonService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();

        mMediaSessionCompat = new MediaSessionCompat(this, "MediaButtonService", null, null);

        mMediaSessionCompat.setFlags(MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS |
                MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS);

        mMediaSessionCompat.setPlaybackState(new PlaybackStateCompat.Builder()
                .setState(PlaybackStateCompat.STATE_PAUSED, 0, 0)
                .setActions(PlaybackStateCompat.ACTION_PLAY_PAUSE)
                .build());

        mMediaSessionCompat.setCallback(new MediaSessionCompat.Callback() {
            @Override
            public boolean onMediaButtonEvent(Intent mediaButtonEvent) {
                Log.i("TAG", "GOT EVENT");

                String intentAction = mediaButtonEvent.getAction();
                if (Intent.ACTION_MEDIA_BUTTON.equals(intentAction)) {
                    KeyEvent event = mediaButtonEvent.getParcelableExtra(Intent.EXTRA_KEY_EVENT);
                    int action = event.getAction();
                    if (action == KeyEvent.ACTION_DOWN) {
                        Log.i("ok", "media button pressed");
                        sendMediaButtonEvent();
                    }

                }

                return super.onMediaButtonEvent(mediaButtonEvent);
            }
        });

        mMediaSessionCompat.setActive(true);
    }

    public int onStartCommand(Intent intent, int flags, int startId) {
        MediaButtonReceiver.handleIntent(mMediaSessionCompat, intent);
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return _targetMessenger.getBinder();
    }

    class IncomingHandler extends Handler { // Handler of incoming messages from clients.
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_REGISTER_CLIENT:
                    _messenger = msg.replyTo;
                    break;
                case MSG_UNREGISTER_CLIENT:
                    _messenger = null;
                    break;
                default:
                    super.handleMessage(msg);
            }
        }
    }

    private void sendMediaButtonEvent() {
        try {
            _messenger.send(Message.obtain(null, MSG_MEDIA_BUTTON_DOWN));
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }
}
