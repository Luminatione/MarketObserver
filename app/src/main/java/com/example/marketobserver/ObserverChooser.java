package com.example.marketobserver;

import android.content.Intent;

public class ObserverChooser
{
    public static IObserver getProperObserver(Intent intent)
    {
        return new LTDInaraObserver();
    }

}
