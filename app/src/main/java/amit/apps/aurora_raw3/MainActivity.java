package amit.apps.aurora_raw3;

import android.app.Activity;
import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.ShareActionProvider;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.PopupMenu;

import com.google.firebase.messaging.FirebaseMessaging;

import amit.apps.aurora_raw3.datainitializers.DatabaseHolder;
import amit.apps.aurora_raw3.datainitializers.PlaylistsHolder;
import amit.apps.aurora_raw3.datainitializers.SettingsHolder;
import amit.apps.aurora_raw3.notifications.MyFirebaseMessagingService;
import io.paperdb.Paper;

public class MainActivity extends AppCompatActivity implements Application.ActivityLifecycleCallbacks{
    public static int fragmenttoload=1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Paper.init(getApplicationContext());
        PlaylistsHolder.initiaizeSharedPreferences(getApplicationContext());
        SettingsHolder.initiaizeSharedPreferences(getApplicationContext());
        DatabaseHolder.initializesongsarray();
        LocalBroadcastManager.getInstance(this).registerReceiver(notificationreceived,
                new IntentFilter("notification received"));

        Intent caller=getIntent();
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(getApplicationContext());
        notificationManager.cancel(MyFirebaseMessagingService.facebook_group, MyFirebaseMessagingService.fbId);
        notificationManager.cancel(MyFirebaseMessagingService.instagram_group, MyFirebaseMessagingService.inId);
        notificationManager.cancel(MyFirebaseMessagingService.twitter_group, MyFirebaseMessagingService.twId);
        notificationManager.cancel(MyFirebaseMessagingService.videos_group, MyFirebaseMessagingService.viId);
        notificationManager.cancel(MyFirebaseMessagingService.tickets_group, MyFirebaseMessagingService.tiId);
        if(caller!=null && caller.getExtras()!=null && caller.getExtras().getString("message")!=null) {
            Bundle extras = caller.getExtras();
            //String message= extras.getString("message");
            String notificationtitle=extras.getString("title");
            Log.e("in main :", "notificationtitle = "+notificationtitle);
            if(notificationtitle.compareTo("Facebook")==0) {
                ActivityFragment.tabtoload=0;
                android.support.v4.app.FragmentTransaction fragmentTransaction=getSupportFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.fragment_activity_container, new ActivityFragment()).commit();
            } else if(notificationtitle.compareTo("Instagram")==0) {
                ActivityFragment.tabtoload=1;
                android.support.v4.app.FragmentTransaction fragmentTransaction=getSupportFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.fragment_activity_container, new ActivityFragment()).commit();
            } else if(notificationtitle.compareTo("Twitter")==0) {
                ActivityFragment.tabtoload=2;
                android.support.v4.app.FragmentTransaction fragmentTransaction=getSupportFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.fragment_activity_container, new ActivityFragment()).commit();
            } else if(notificationtitle.compareTo("Youtube")==0) {
                videosIconClicked(null);
            } else if(notificationtitle.compareTo("Tickets")==0) {
                ticketIconClicked(null);
            }

        } else {
            activityIconClicked(null);
        }
        if(caller!=null && caller.getExtras()!=null && caller.getSerializableExtra("caller")!=null) {
            SplashActivity splashActivity=(SplashActivity)caller.getSerializableExtra("caller");
            splashActivity.finish();
        }
        new TopicSubscriber().execute();
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.e("main_start :", "fragment to load = "+fragmenttoload);
        switch (fragmenttoload) {
            case 3: videosIconClicked(null);
                break;
            case 4: ticketIconClicked(null);
                break;
        }
    }

    public void activityIconClicked(View v) {
        android.support.v4.app.FragmentTransaction fragmentTransaction=getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.fragment_activity_container, new ActivityFragment()).commit();
    }

    public void videosIconClicked(View v) {
        android.support.v4.app.FragmentTransaction fragmentTransaction=getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.fragment_activity_container, new VideosFragment()).commit();
    }

    public void playlistIconClicked(View v) {
        android.support.v4.app.FragmentTransaction fragmentTransaction=getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.fragment_activity_container, new PlaylistFragment()).commit();
    }

    public void ticketIconClicked(View v) {
        android.support.v4.app.FragmentTransaction fragmentTransaction=getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.fragment_activity_container, new TicketsFragment()).commit();
    }

    public void settingIconClicked(View v) {
        android.support.v4.app.FragmentTransaction fragmentTransaction=getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.fragment_activity_container, new SettingsFragment()).commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.appbarmenu, menu);

        MenuItem notificationbell=menu.findItem(R.id.notificationbell);
        if(MyFirebaseMessagingService.unseennotifications.size()>0)
            notificationbell.setIcon(R.drawable.round_notifications_active_white_48dp);
        else
            notificationbell.setIcon(R.drawable.round_notifications_white_48dp);

        MenuItem shareaction = menu.findItem(R.id.shareaction);
        ShareActionProvider mShareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(shareaction);
        mShareActionProvider.setShareIntent(doshare());
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()==R.id.notificationbell) {
            item.setIcon(R.drawable.round_notifications_white_48dp);
            MyFirebaseMessagingService.unseennotifications.clear();
            View menuItemView = findViewById(R.id.notificationbell);
            PopupMenu recentnotifications = new PopupMenu(this, menuItemView);
            for(String x: MyFirebaseMessagingService.notifications) {
                if(x.startsWith("Facebook"))
                    recentnotifications.getMenu().add(MyFirebaseMessagingService.fbId, Menu.NONE, Menu.FIRST, x);
                if(x.startsWith("Instagram"))
                    recentnotifications.getMenu().add(MyFirebaseMessagingService.inId, Menu.NONE, Menu.FIRST, x);
                if(x.startsWith("Twitter"))
                    recentnotifications.getMenu().add(MyFirebaseMessagingService.twId, Menu.NONE, Menu.FIRST, x);
                if(x.startsWith("Tickets"))
                    recentnotifications.getMenu().add(MyFirebaseMessagingService.tiId, Menu.NONE, Menu.FIRST, x);
                if(x.startsWith("Youtube"))
                    recentnotifications.getMenu().add(MyFirebaseMessagingService.viId, Menu.NONE, Menu.FIRST, x);
            }
            recentnotifications.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    switch (item.getGroupId()) {
                        case MyFirebaseMessagingService.fbId:
                            ActivityFragment.tabtoload=0;
                            activityIconClicked(null);
                            break;
                        case MyFirebaseMessagingService.inId:
                            ActivityFragment.tabtoload=1;
                            activityIconClicked(null);
                            break;
                        case MyFirebaseMessagingService.twId:
                            ActivityFragment.tabtoload=2;
                            activityIconClicked(null);
                            break;
                        case MyFirebaseMessagingService.tiId:
                            ticketIconClicked(null);
                            break;
                        case MyFirebaseMessagingService.viId:
                            videosIconClicked(null);
                            break;
                    }

                    return true;
                }
            });
            recentnotifications.show();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(notificationreceived);
    }

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
        MyFirebaseMessagingService.appinforeground=true;
    }

    @Override
    public void onActivityStarted(Activity activity) {
        MyFirebaseMessagingService.appinforeground=true;
    }

    @Override
    public void onActivityResumed(Activity activity) {
        MyFirebaseMessagingService.appinforeground=true;
    }

    @Override
    public void onActivityPaused(Activity activity) {

    }

    @Override
    public void onActivityStopped(Activity activity) {
        MyFirebaseMessagingService.appinforeground=false;
    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

    }

    @Override
    public void onActivityDestroyed(Activity activity) {
        MyFirebaseMessagingService.appinforeground=false;
    }

    private BroadcastReceiver notificationreceived=new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            switch (fragmenttoload) {
                case 0: activityIconClicked(null);
                break;
                case 1: videosIconClicked(null);
                break;
                case 2: playlistIconClicked(null);
                break;
                case 3: ticketIconClicked(null);
            }
        }
    };

    private Intent doshare() {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        String msg="Moths of Aurora\n";
        intent.putExtra(Intent.EXTRA_TEXT, msg+"http://play.google.com/store/apps/details?id="+getPackageName());
        return intent;
    }

    private static class TopicSubscriber extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... voids) {
            FirebaseMessaging.getInstance().subscribeToTopic("alldevices");
            return null;
        }
    }
}
