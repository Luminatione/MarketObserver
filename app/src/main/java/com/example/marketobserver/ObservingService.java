package com.example.marketobserver;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

public class ObservingService extends Service
{

    private final String channelID = "observing_channel";
    NotificationManager notificationManager = null;
    NotificationCompat.Builder currentNotificationBuilder = null;
    IObserver observer;
    Intent mainIntent;
    private observingNotificationMessageReceiver observingNotificationMessageReceiver;
    public ObservingService() { }
    @Override
    public void onCreate()
    {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {
        mainIntent = intent;
        notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        currentNotificationBuilder = getDefaultNotificationBuilder();
        createNotificationChannel();
        startForeground(1, getObservingNotification());
        observingNotificationMessageReceiver = new observingNotificationMessageReceiver(() -> {
            try {
                observer.pauseUpdateThread();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }, this::resume, () -> observer.stopUpdateThread());
        registerReceiver(observingNotificationMessageReceiver, getObservingNotificationIntentFilter());
        addRunningServiceNotificationControlButtons();
        startObserving(intent);
        return START_STICKY;
    }
    private void startObserving(Intent intent)
    {
        observer = ObserverChooser.getProperObserver(intent);
        observer.init(intent, this);
        observer.run();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent)
    {
       return null;
    }
    private void createNotificationChannel()
    {
        NotificationChannel channel = new NotificationChannel(channelID, "Observing Channel",
                NotificationManager.IMPORTANCE_DEFAULT);
        notificationManager.createNotificationChannel(channel);
    }
    private Notification getObservingNotification()
    {
        return currentNotificationBuilder.build();
    }
    private Intent getDefaultNotificationIntent()
    {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra(FieldPassNames.REFERENCE_SYSTEM_PASS_NAME,
                mainIntent.getStringExtra(FieldPassNames.REFERENCE_SYSTEM_PASS_NAME));
        intent.putExtra(FieldPassNames.MAXIMAL_DISTANCE_PASS_NAME,
                mainIntent.getStringExtra(FieldPassNames.MAXIMAL_DISTANCE_PASS_NAME));
        intent.putExtra(FieldPassNames.MINIMAL_DEMAND_PASS_NAME,
                mainIntent.getStringExtra(FieldPassNames.MINIMAL_DEMAND_PASS_NAME));
        intent.putExtra(FieldPassNames.ALLOW_MSIZED_PADS_PASS_NAME,
                mainIntent.getBooleanExtra(FieldPassNames.ALLOW_MSIZED_PADS_PASS_NAME, false));
        return intent;
    }
    private PendingIntent getDefaultNotificationPendingIntent()
    {
        return PendingIntent.getActivity(this, 0, getDefaultNotificationIntent(),
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
    }
    private NotificationCompat.Builder getDefaultNotificationBuilder()
    {
        return new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.ic_observation)
                .setContentTitle("Observing...")
                .setContentText("Waiting for update")
                .setContentIntent(getDefaultNotificationPendingIntent())
                .setStyle(new NotificationCompat.BigTextStyle())
                .setChannelId(channelID);
    }

    public void updateNotification(String bigText, String content)
    {
        getSystemNotificationService().notify(1,
                currentNotificationBuilder.setStyle(
                        new NotificationCompat.BigTextStyle().bigText(bigText)).setContentText(content).build());
    }
    public void addRunningServiceNotificationControlButtons()
    {
        addPauseButton();
        addStopButton();
        getSystemNotificationService().notify(1, currentNotificationBuilder.build());
    }
    public void addPausedServiceNotificationControlButtons()
    {
        addResumeButton();
        addStopButton();
        getSystemNotificationService().notify(1, currentNotificationBuilder.build());
    }
    private IntentFilter getObservingNotificationIntentFilter()
    {
        IntentFilter filter = new IntentFilter();
        filter.addAction(FieldPassNames.PAUSE_CODE);
        filter.addAction(FieldPassNames.RESUME_CODE);
        filter.addAction(FieldPassNames.STOP_CODE);
        return filter;
    }

    private void addPauseButton()
    {
        PendingIntent pauseIntent = PendingIntent.getBroadcast(this, 0,
                new Intent().setAction(FieldPassNames.PAUSE_CODE),
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        currentNotificationBuilder.addAction(R.drawable.ic_pause_black_24dp, "Pause", pauseIntent);
    }
    private void addStopButton()
    {
        PendingIntent stopIntent = PendingIntent.getBroadcast(this, 2,
                new Intent().setAction(FieldPassNames.STOP_CODE),
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        currentNotificationBuilder.addAction(R.drawable.ic_stop_observing_service, "Stop", stopIntent);
    }
    private void addResumeButton()
    {
        PendingIntent resumeIntent = PendingIntent.getBroadcast(this, 1,
                new Intent().setAction(FieldPassNames.RESUME_CODE),
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        currentNotificationBuilder.addAction(R.drawable.ic_resume_observing_service, "Resume", resumeIntent);
    }
    private NotificationManager getSystemNotificationService()
    {
        return ((NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE));
    }
    public void stop()
    {
        unregisterReceiver(observingNotificationMessageReceiver);
        stopForeground(true);
        stopSelf();
    }
    @SuppressLint("RestrictedApi")
    public void pause()
    {
        currentNotificationBuilder.mActions.clear();
        addPausedServiceNotificationControlButtons();
    }
    @SuppressLint("RestrictedApi")
    public void resume()
    {
        currentNotificationBuilder.mActions.clear();
        addRunningServiceNotificationControlButtons();
        startObserving(mainIntent);
    }

}
