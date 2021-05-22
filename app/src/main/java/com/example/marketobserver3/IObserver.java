package com.example.marketobserver3;

import android.content.Intent;

interface IObserver
{
    void init(Intent intent, ObservingService observingService);
    void run();
    void pauseUpdateThread() throws InterruptedException;
    void stopUpdateThread();
}
