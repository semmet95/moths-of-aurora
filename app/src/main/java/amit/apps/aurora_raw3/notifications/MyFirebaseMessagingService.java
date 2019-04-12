package amit.apps.aurora_raw3.notifications;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.content.LocalBroadcastManager;

import com.google.firebase.messaging.RemoteMessage;

import java.util.ArrayList;
import java.util.HashSet;

import amit.apps.aurora_raw3.ActivityFragment;
import amit.apps.aurora_raw3.MainActivity;
import amit.apps.aurora_raw3.R;
import amit.apps.aurora_raw3.datainitializers.SettingsHolder;

public class MyFirebaseMessagingService extends com.google.firebase.messaging.FirebaseMessagingService {
    public static boolean appinforeground=false;
    public static final String facebook_group="FA_GROUP", instagram_group="IN_GROUP", twitter_group="TW_GROUP",
            videos_group="VI_GROUP", tickets_group="TI_GROUP";
    private boolean notifyFA=true, notifyIN=true, notifyTW=true, notifyVI=true, notifyTI=true;
    public static final int fbId=11, inId=22, twId=33, viId=44, tiId=55;
    public static ArrayList<String> notifications=new ArrayList<>();
    public static HashSet<String> unseennotifications=new HashSet<>();

    @Override
    public void onCreate() {
        super.onCreate();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("notificationchannel",
                    getResources().getString(R.string.app_name), NotificationManager.IMPORTANCE_DEFAULT);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    @Override
    public void onMessageReceived(final RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        SettingsHolder.initiaizeSharedPreferences(getApplicationContext());

        notifyFA= SettingsHolder.getfanotif();
        notifyIN= SettingsHolder.getinnotif();
        notifyTW= SettingsHolder.gettwnotif();
        notifyVI= SettingsHolder.getvinotif();
        notifyTI= SettingsHolder.gettinotif();

        notifications.add(remoteMessage.getData().get("title")+"\n"+remoteMessage.getData().get("message"));
        if(notifications.size()>15)
            notifications.remove(0);
        unseennotifications.add(remoteMessage.getData().get("title"));
        if(appinforeground) {
            LocalBroadcastManager.getInstance(this).sendBroadcast(new Intent("notification received"));
        } else {
            Intent faIntent=new Intent(getApplicationContext(), MainActivity.class),
                    inIntent=new Intent(getApplicationContext(), MainActivity.class),
                    twIntent=new Intent(getApplicationContext(), MainActivity.class),
                    viIntent=new Intent(getApplicationContext(), MainActivity.class),
                    tiIntent=new Intent(getApplicationContext(), MainActivity.class);
            faIntent.putExtra("message", "lol");
            faIntent.putExtra("title", "Facebook");

            inIntent.putExtra("message", "lol");
            inIntent.putExtra("title", "Instagram");

            twIntent.putExtra("message", "lol");
            twIntent.putExtra("title", "Twitter");

            viIntent.putExtra("message", "lol");
            viIntent.putExtra("title", "Youtube");

            tiIntent.putExtra("message", "lol");
            tiIntent.putExtra("title", "Tickets");

            PendingIntent pfaIntent=PendingIntent.getActivity(getApplicationContext(), 0, faIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT),
                    pinIntent=PendingIntent.getActivity(getApplicationContext(), (int) System.currentTimeMillis(), inIntent,
                            PendingIntent.FLAG_UPDATE_CURRENT),
                    ptwIntent=PendingIntent.getActivity(getApplicationContext(), (int) System.currentTimeMillis(), twIntent,
                            PendingIntent.FLAG_UPDATE_CURRENT),
                    pviIntent=PendingIntent.getActivity(getApplicationContext(), (int) System.currentTimeMillis(), viIntent,
                            PendingIntent.FLAG_UPDATE_CURRENT),
                    ptiIntent=PendingIntent.getActivity(getApplicationContext(), (int) System.currentTimeMillis(), tiIntent,
                            PendingIntent.FLAG_UPDATE_CURRENT);

            Notification faNotification=new NotificationCompat.Builder(getApplicationContext(),
                    "notificationchannel").setSmallIcon(R.drawable.ic_stat_name).setLargeIcon(BitmapFactory
                    .decodeResource(getApplicationContext().getResources(), R.mipmap.ic_launcher))
                    .setContentTitle("Moths of Aurora").setContentText(remoteMessage.getData().get("message"))
                    .setDefaults(Notification.DEFAULT_SOUND)
                    .setContentIntent(pfaIntent).setGroup(facebook_group).setAutoCancel(true).build();

            Notification inNotification=new NotificationCompat.Builder(getApplicationContext(),
                    "notificationchannel").setSmallIcon(R.drawable.ic_stat_name).setLargeIcon(BitmapFactory
                    .decodeResource(getApplicationContext().getResources(), R.mipmap.ic_launcher))
                    .setContentTitle("Moths of Aurora").setContentText(remoteMessage.getData().get("message"))
                    .setDefaults(Notification.DEFAULT_SOUND)
                    .setContentIntent(pinIntent).setGroup(instagram_group).setAutoCancel(true).build();

            Notification twNotification=new NotificationCompat.Builder(getApplicationContext(),
                    "notificationchannel").setSmallIcon(R.drawable.ic_stat_name).setLargeIcon(BitmapFactory
                    .decodeResource(getApplicationContext().getResources(), R.mipmap.ic_launcher))
                    .setContentTitle("Moths of Aurora").setContentText(remoteMessage.getData().get("message"))
                    .setDefaults(Notification.DEFAULT_SOUND)
                    .setContentIntent(ptwIntent).setGroup(twitter_group).setAutoCancel(true).build();

            Notification viNotification=new NotificationCompat.Builder(getApplicationContext(),
                    "notificationchannel").setSmallIcon(R.drawable.ic_stat_name).setLargeIcon(BitmapFactory
                    .decodeResource(getApplicationContext().getResources(), R.mipmap.ic_launcher))
                    .setContentTitle("Moths of Aurora").setContentText(remoteMessage.getData().get("message"))
                    .setDefaults(Notification.DEFAULT_SOUND)
                    .setContentIntent(pviIntent).setGroup(videos_group).setAutoCancel(true).build();

            Notification tiNotification=new NotificationCompat.Builder(getApplicationContext(),
                    "notificationchannel").setSmallIcon(R.drawable.ic_stat_name).setLargeIcon(BitmapFactory
                    .decodeResource(getApplicationContext().getResources(), R.mipmap.ic_launcher))
                    .setContentTitle("Moths of Aurora").setContentText(remoteMessage.getData().get("message"))
                    .setDefaults(Notification.DEFAULT_SOUND)
                    .setContentIntent(ptiIntent).setGroup(tickets_group).setAutoCancel(true).build();

            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(getApplicationContext());


            String notificationtitle=remoteMessage.getData().get("title");
            //Log.e("notification received :", "with app in background with title = "+notificationtitle);
            if(notificationtitle.compareTo("Facebook")==0 && notifyFA) {
                //Log.e("notifying :", "for fb");
                ActivityFragment.tabtoload=0;
                notificationManager.notify(facebook_group,fbId, faNotification);
            } else if(notificationtitle.compareTo("Instagram")==0 && notifyIN) {
                //Log.e("notifying :", "for insta");
                ActivityFragment.tabtoload=1;
                notificationManager.notify(instagram_group,inId, inNotification);
            } else if(notificationtitle.compareTo("Twitter")==0 && notifyTW) {
                //Log.e("notifying :", "for tweet");
                ActivityFragment.tabtoload=2;
                notificationManager.notify(twitter_group,twId, twNotification);
            } else if(notificationtitle.compareTo("Tickets")==0 && notifyTI) {
                //Log.e("notifying :", "for ticket");
                MainActivity.fragmenttoload=4;
                notificationManager.notify(tickets_group,tiId, tiNotification);
            } else if(notificationtitle.compareTo("Youtube")==0 && notifyVI) {
                //Log.e("notifying :", "for video");
                MainActivity.fragmenttoload=3;
                notificationManager.notify(videos_group,viId, viNotification);
            }

        }
    }
}