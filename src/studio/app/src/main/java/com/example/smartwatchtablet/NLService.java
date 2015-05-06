package com.example.smartwatchtablet;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;

public class NLService extends NotificationListenerService
{

    private NLServiceReceiver nlservicereciver;
    @Override
    public void onCreate() 
    {
        super.onCreate();
        nlservicereciver = new NLServiceReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction("com.example.getnotification.NOTIFICATION_LISTENER_SERVICE_EXAMPLE");
        registerReceiver(nlservicereciver,filter);
    }

    @Override
    public void onDestroy() 
    {
        super.onDestroy();
        unregisterReceiver(nlservicereciver);
    }

	@Override
    public void onNotificationPosted(StatusBarNotification sbn) 
    {
		Intent i = new  Intent("com.example.getnotification.NOTIFICATION_LISTENER_EXAMPLE");
		i.putExtra("type", "posted");
        i.putExtra("package_name",sbn.getPackageName());
        i.putExtra("ticker_text",sbn.getNotification().tickerText);
        i.putExtra("title",extras.getString("android.title"));
        i.putExtra("text",extras.getCharSequence("android.text").toString());
                sendBroadcast(i);
    }

    
	@Override
    public void onNotificationRemoved(StatusBarNotification sbn) 
	{
        Intent i = new  Intent("com.example.getnotification.NOTIFICATION_LISTENER_EXAMPLE");
        i.putExtra("type", "removed");
        i.putExtra("package_name",sbn.getPackageName());
        i.putExtra("ticker_text",sbn.getNotification().tickerText);
        sendBroadcast(i);
    }

    class NLServiceReceiver extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) 
        {
            if(intent.getStringExtra("command").equals("clearall"))
            {
                    NLService.this.cancelAllNotifications();
            }
            else if(intent.getStringExtra("command").equals("list"))
            {
                Intent i1 = new  Intent("com.example.getnotification.NOTIFICATION_LISTENER_EXAMPLE");
                i1.putExtra("notification_event","=====================");
                sendBroadcast(i1);
                int i=1;
                for (StatusBarNotification sbn : NLService.this.getActiveNotifications()) {
                    Intent i2 = new  Intent("com.example.getnotification.NOTIFICATION_LISTENER_EXAMPLE");
                    i2.putExtra("notification_event",i +" " + sbn.getPackageName() + "n");
                    sendBroadcast(i2);
                    i++;
                }
                Intent i3 = new  Intent("com.example.getnotification.NOTIFICATION_LISTENER_EXAMPLE");
                i3.putExtra("notification_event","===== Notification List ====");
                sendBroadcast(i3);

            }

        }
    }
}
