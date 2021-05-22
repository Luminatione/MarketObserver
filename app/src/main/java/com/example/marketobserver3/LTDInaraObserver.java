package com.example.marketobserver3;

import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import androidx.annotation.NonNull;

import org.jsoup.nodes.Element;

import java.io.IOException;
import java.util.ArrayList;

public class LTDInaraObserver implements IObserver
{
    private static String server = "https://inara.cz/ajaxaction.php?act=goodsdata&refname=sellmax&refid={0}&refid2={1}";
    private final static String commodityID = "144";
    private static String systemID;

    private String referenceSystem;
    private float maximalDistance;
    private int minimalDemand;
    private Boolean allowMPads;
    private ObservingService observingService;
    private Handler uiHandle;
    final String[] updateResult = new String[2];
    private boolean stopped = false;
    private boolean paused = false;
    Thread updateThread;
    @Override
    public void init(Intent intent, ObservingService observingService)
    {
        referenceSystem = intent.getStringExtra(FieldPassNames.REFERENCE_SYSTEM_PASS_NAME);
        maximalDistance = new Float(intent.getStringExtra(FieldPassNames.MAXIMAL_DISTANCE_PASS_NAME));
        minimalDemand = new Integer(intent.getStringExtra(FieldPassNames.MINIMAL_DEMAND_PASS_NAME));
        allowMPads = intent.getBooleanExtra(FieldPassNames.ALLOW_MSIZED_PADS_PASS_NAME, false);
        uiHandle = new Handler(Looper.getMainLooper())
        {
            @Override
            public void handleMessage(@NonNull Message msg) {
                updateResult();
            }
        };
        this.observingService = observingService;
    }

    @Override
    public void run() {
        final WebsiteConnectionManager connectionManager = new WebsiteConnectionManager();
        updateThread = new Thread()
        {
            @Override
            public void run() {
                try {
                    systemID = SystemNameToInaraIdTranslator.translate(referenceSystem);
                    server = server.replace("{0}", commodityID).replace("{1}", systemID);
                    while (!stopped && !paused)
                    {
                        update(connectionManager);
                        uiHandle.sendEmptyMessage(1);
                        waitForTenMinutes();
                    }
                }
                catch(Throwable e)
                {
                    e.printStackTrace();
                    if(!stopped && !paused)
                    {
                        updateResult[0] = "Unexpected error occured.\nCheck your internet connection and reset observation";
                        updateResult[1] = "Application couldn't get data from Inara";
                        updateResult();
                    }
                }
                finally
                {
                    if(stopped)
                    {
                        observingService.stop();
                    }
                }
            }
        };
        updateThread.start();
    }
    @Override
    public void pauseUpdateThread() throws InterruptedException
    {
        paused = true;
        updateThread.interrupt();
        observingService.pause();
    }
    @Override
    public void stopUpdateThread()
    {
        stopped = true;
        updateThread.interrupt();
    }
    private void updateResult()
    {
        observingService.updateNotification(updateResult[0], updateResult[1]);
    }

    private void update(WebsiteConnectionManager connectionManager) throws IOException
    {
        connectionManager.requestAndStore(server);
        ArrayList<ResultRecord> results = getResultRecords(connectionManager);
        results.removeIf(e -> (e.distance > maximalDistance || e.demand < minimalDemand || (!allowMPads && e.pad == 'M')));
        results.sort((a, b) -> a.price > b.price ? -1  : a.price < b.price ? 1 : 0);
        updateResult[1] = results.get(0).toString();
        updateResult[0] = recordsToExpandedText(results);
    }
    private ArrayList<ResultRecord> getResultRecords(WebsiteConnectionManager connectionManager)
    {
        ArrayList<ResultRecord> results = new ArrayList<ResultRecord>();
        boolean checkedFirst = false;
        for (Element row : connectionManager.getInaraTable())
        {
            if(!checkedFirst)//first element is header of table
            {
                checkedFirst = true;
                continue;
            }
            results.add(new ResultRecord(row));
        }
        return results;
    }
    private String recordsToExpandedText(ArrayList<ResultRecord> results)
    {
        String expandedText = "";
        for(int i = 0; i < 5 && i < results.size(); ++i)
        {
            expandedText += results.get(i).toString() + (i < 4 && i < results.size() - 1 ? '\n' : "");
        }
        return expandedText.equals("") ? "No matching results" : expandedText;
    }
    private void waitForTenMinutes()
    {
        try
        {
            Thread.sleep(600000);
        }
        catch (InterruptedException e)
        {
            Thread.currentThread().interrupt();
        }
    }
}
