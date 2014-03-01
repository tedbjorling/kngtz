package com.holidaystudios.kngtz;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import com.holidaystudios.kngt.server.ServerAnnouncer;
import com.holidaystudios.kngt.server.ServerFinder;

import java.util.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

public class GameServerService extends Service {
    static boolean loggedIn = false;
    static String dateString = "notSet";

    // These are the Intent actions that we are prepared to handle. Notice that the fact these
	// constants exist in our class is a mere convenience: what really defines the actions our
	// service can handle are the <action> tags in the <intent-filters> tag for our service in
	// AndroidManifest.xml.
    public static final String ACTION_LAUNCH = "com.holidaystudios.kngtz.action.LAUNCH";
    public static final String ACTION_LOGIN_TO_SERVER = "com.holidaystudios.kngtz.action.LOGIN_TO_SERVER";
	public static final String ACTION_CREATE_SERVER = "com.holidaystudios.kngtz.action.CREATE_SERVER";

    private NotificationManager mNM;

    // Unique Identification Number for the Notification.
    // We use it on Notification start, and to cancel it.
    private int NOTIFICATION = R.string.gameserver_started;

    /**
     * Class for clients to access.  Because we know this service always
     * runs in the same process as its clients, we don't need to deal with
     * IPC.
     */
    public class LocalBinder extends Binder {
        GameServerService getService() {
            return GameServerService.this;
        }
    }

    @Override
    public void onCreate() {
        mNM = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
        if(!loggedIn) {
            ServerFinder.onCreate(new WifiServerNetworkInterface(this));

            DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
            Date date = new Date();
            dateString = dateFormat.format(date);

            Log.i("Kngtz", "Launching StartupActivity intent..." + dateString);

            loggedIn = true;
            Intent loginIntent = new Intent(this, com.holidaystudios.kngtz.StartupActivity.class);
            loginIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            this.startActivity(loginIntent);
        }
        Log.i("Kngtz", "onCreate called for service with dateString(" + dateString + ")");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i("Kngtz", "onStartCommand called for service with dateString (" + dateString + ")");
        if(intent != null) {
            String action = intent.getAction();

            Log.i("LocalService", "Received start id " + startId + ": " + intent);

            if (action.equals(ACTION_LOGIN_TO_SERVER)) processLoginToServerRequest(intent);
            else if (action.equals(ACTION_CREATE_SERVER)) processCreateServerRequest(intent);
        }

        // We want this service to continue running until it is explicitly
        // stopped, so return sticky.
        return START_NOT_STICKY;
    }

    void bringDownServer() {
        mNM.cancel(NOTIFICATION);
        ServerAnnouncer.onDestroy();
    }

    void bringUpServer() {
        ServerAnnouncer.onCreate(new WifiServerNetworkInterface(this));
        showNotification();
    }

    void processLoginToServerRequest(Intent intent) {
        bringDownServer();
    }

    void processCreateServerRequest(Intent intent) {
        bringUpServer();
    }

    @Override
    public void onDestroy() {
        // Cancel the persistent notification.
        Log.i("Kngtz", "onDestroy called for service with dateString (" + dateString + ")");
        mNM.cancel(NOTIFICATION);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    // This is the object that receives interactions from clients.  See
    // RemoteService for a more complete example.
    private final IBinder mBinder = new LocalBinder();

    /**
     * Show a notification while this service is running.
     */
    private void showNotification() {
        // In this sample, we'll use the same text for the ticker and the expanded notification
        CharSequence text = getText(R.string.gameserver_started);

/*
        // The PendingIntent to launch our activity if the user selects this notification
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
                new Intent(this, LocalServiceActivities.Controller.class), 0);
*/
        // Set the icon, scrolling text and timestamp
        Notification notification = new Notification.Builder(this)
                .setContentTitle(text)
                .setOngoing(true)
                .setSmallIcon(R.drawable.server_notification_icon)
                //.setContentIntent(contentIntent)
                .build();

        // Send the notification.
        mNM.notify(NOTIFICATION, notification);
    }
}