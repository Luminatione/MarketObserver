package com.example.marketobserver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.security.InvalidParameterException;
import java.util.concurrent.Callable;

public class observingNotificationMessageReceiver extends BroadcastReceiver
{

    Runnable onPause;
    Runnable onResume;
    Runnable onStop;
    public observingNotificationMessageReceiver()
    {
        super();
    }
    public observingNotificationMessageReceiver(Runnable onPause, Runnable onResume, Runnable onStop)
    {
        super();
        this.onStop = onStop;
        this.onResume = onResume;
        this.onPause = onPause;
    }

    @Override
    public void onReceive(Context context, Intent intent)
    {
        try {
            switch (intent.getAction()) {
                case FieldPassNames.PAUSE_CODE: {
                    onPause.run();
                }
                break;
                case FieldPassNames.RESUME_CODE: {
                    onResume.run();
                }
                break;
                case FieldPassNames.STOP_CODE: {
                    onStop.run();
                }
                break;
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}
