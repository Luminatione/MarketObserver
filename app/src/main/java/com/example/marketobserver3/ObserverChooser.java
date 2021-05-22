package com.example.marketobserver3;

import android.content.Intent;

public class ObserverChooser
{
    public static IObserver getProperObserver(Intent intent)
    {
        return new LTDInaraObserver();
    }

}
