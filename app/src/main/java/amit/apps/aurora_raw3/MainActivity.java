package amit.apps.aurora_raw3;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.internal.BottomNavigationItemView;
import android.support.design.internal.BottomNavigationMenuView;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.ShareActionProvider;
import android.text.InputType;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.PopupMenu;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.google.firebase.messaging.FirebaseMessaging;

import java.lang.reflect.Field;

import amit.apps.aurora_raw3.datainitializers.DatabaseHolder;
import amit.apps.aurora_raw3.datainitializers.PlaylistsHolder;
import amit.apps.aurora_raw3.datainitializers.SettingsHolder;
import amit.apps.aurora_raw3.notifications.MyFirebaseMessagingService;
import io.paperdb.Paper;

public class MainActivity extends AppCompatActivity implements Application.ActivityLifecycleCallbacks{
    public static int fragmenttoload=1;

    private TextView toolbarTitleView;
    private RadioGroup triToggleGroup;
    private FloatingActionButton playlistFAB;

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

        // Setup toolbarTitleView
        toolbarTitleView = findViewById(R.id.toolbar_title);
        // Setup FAB aka FloatingActionButton
        playlistFAB = findViewById(R.id.playlistFab);

        // Setup BottomNavigationView
        final BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_nav);
        removeShiftMode(bottomNavigationView);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                android.support.v4.app.FragmentTransaction fragmentTransaction=getSupportFragmentManager().beginTransaction();
                switch (item.getItemId()) {
                    case R.id.action_feed:
                        activityIconClicked(0);
                        break;
                    case R.id.action_video:
                        videosIconClicked();
                        break;
                    case R.id.action_info:
                        playlistIconClicked();
                        break;
                    case R.id.action_ticket:
                        ticketIconClicked();
                        break;
                    case R.id.action_settings:
                        switchToolbarView(6);
                        playlistFAB.setImageResource(R.drawable.ic_play);
                        PlaylistFragment.isWindowActive = false;
                        fragmentTransaction.replace(R.id.fragment_activity_container, new SettingsFragment()).commit();
                        break;
                }
                return true;
            }
        });

        playlistFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bottomNavigationView.setSelectedItemId(R.id.action_info);

                if(PlaylistFragment.isWindowActive){
                    AlertDialog.Builder builder=new AlertDialog.Builder(MainActivity.this);
                    builder.setTitle("Name your playlist");
                    final EditText input = new EditText(MainActivity.this);
                    input.setInputType(InputType.TYPE_TEXT_VARIATION_SHORT_MESSAGE);
                    builder.setView(input);

                    builder.setPositiveButton("Create", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            String playlistTitle=input.getText().toString();
                            if(playlistTitle.length()>0) {
                                PlaylistsHolder.addPlaylistTitle(playlistTitle);
                                playlistIconClicked();
                            }
                        }
                    });

                    builder.setNegativeButton("Nah", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });

                    builder.show();
                }
            }
        });

        // Setup Tri-Toggle for tab-switching
        triToggleGroup = findViewById(R.id.triToggleGroup);
        triToggleGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                int checkedRadioButtonId = radioGroup.getCheckedRadioButtonId();

                RadioButton fbButton = findViewById(R.id.facebookbtn);
                RadioButton instaButton = findViewById(R.id.instagrambtn);
                RadioButton twitterButton = findViewById(R.id.twitterbtn);

                if(checkedRadioButtonId == fbButton.getId()){
                    activityIconClicked(0);
                    fbButton.setTextColor(getResources().getColor(R.color.black));
                    instaButton.setTextColor(getResources().getColor(R.color.white));
                    twitterButton.setTextColor(getResources().getColor(R.color.white));
                } else if(checkedRadioButtonId == instaButton.getId()){
                    activityIconClicked(1);
                    fbButton.setTextColor(getResources().getColor(R.color.white));
                    instaButton.setTextColor(getResources().getColor(R.color.black));
                    twitterButton.setTextColor(getResources().getColor(R.color.white));
                } else if(checkedRadioButtonId == twitterButton.getId()){
                    activityIconClicked(2);
                    fbButton.setTextColor(getResources().getColor(R.color.white));
                    instaButton.setTextColor(getResources().getColor(R.color.white));
                    twitterButton.setTextColor(getResources().getColor(R.color.black));
                }
            }
        });

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
                activityIconClicked(0);
            } else if(notificationtitle.compareTo("Instagram")==0) {
                activityIconClicked(1);
            } else if(notificationtitle.compareTo("Twitter")==0) {
                activityIconClicked(2);
            } else if(notificationtitle.compareTo("Youtube")==0) {
                videosIconClicked();
            } else if(notificationtitle.compareTo("Tickets")==0) {
                ticketIconClicked();
            }

        } else {
            activityIconClicked(0);
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
            case 3: videosIconClicked();
                break;
            case 4: ticketIconClicked();
                break;
        }
    }

    private void switchToolbarView(int activeWindow){
        if(activeWindow == 0){
            toolbarTitleView.setText("Feed");
            triToggleGroup.check(R.id.facebookbtn);
            triToggleGroup.setVisibility(View.VISIBLE);
        } else if(activeWindow == 1){
            toolbarTitleView.setText("Feed");
            triToggleGroup.check(R.id.instagrambtn);
            triToggleGroup.setVisibility(View.VISIBLE);
        } else if(activeWindow == 2){
            toolbarTitleView.setText("Feed");
            triToggleGroup.check(R.id.twitterbtn);
            triToggleGroup.setVisibility(View.VISIBLE);
        } else if(activeWindow == 3){
            toolbarTitleView.setText("Videos");
            triToggleGroup.setVisibility(View.GONE);
        } else if(activeWindow == 4){
            toolbarTitleView.setText("Playlists");
            triToggleGroup.setVisibility(View.GONE);
        } else if(activeWindow == 5){
            toolbarTitleView.setText("Tickets");
            triToggleGroup.setVisibility(View.GONE);
        } else {
            toolbarTitleView.setText("Settings");
            triToggleGroup.setVisibility(View.GONE);
        }
    }

    public void activityIconClicked(int tabToLoad) {
        playlistFAB.setImageResource(R.drawable.ic_play);
        PlaylistFragment.isWindowActive = false;
        android.support.v4.app.FragmentTransaction fragmentTransaction=getSupportFragmentManager().beginTransaction();
        if(tabToLoad == 0){
            switchToolbarView(0);
            fragmentTransaction.replace(R.id.fragment_activity_container, new ActivityFacebookFragment()).commit();
        } else if(tabToLoad == 1){
            switchToolbarView(1);
            fragmentTransaction.replace(R.id.fragment_activity_container, new ActivityInstagramFragment()).commit();
        } else if(tabToLoad == 2){
            switchToolbarView(2);
            fragmentTransaction.replace(R.id.fragment_activity_container, new ActivityTwitterFragment()).commit();
        }
    }

    public void videosIconClicked() {
        switchToolbarView(3);
        playlistFAB.setImageResource(R.drawable.ic_play);
        PlaylistFragment.isWindowActive = false;
        android.support.v4.app.FragmentTransaction fragmentTransaction=getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.fragment_activity_container, new VideosFragment()).commit();
    }

    public void playlistIconClicked() {
        switchToolbarView(4);
        playlistFAB.setImageResource(R.drawable.round_add_white_36);
        android.support.v4.app.FragmentTransaction fragmentTransaction=getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.fragment_activity_container, new PlaylistFragment()).commit();
    }

    public void ticketIconClicked() {
        switchToolbarView(5);
        playlistFAB.setImageResource(R.drawable.ic_play);
        PlaylistFragment.isWindowActive = false;
        android.support.v4.app.FragmentTransaction fragmentTransaction=getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.fragment_activity_container, new TicketsFragment()).commit();
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
                            activityIconClicked(0);
                            break;
                        case MyFirebaseMessagingService.inId:
                            activityIconClicked(1);
                            break;
                        case MyFirebaseMessagingService.twId:
                            activityIconClicked(2);
                            break;
                        case MyFirebaseMessagingService.tiId:
                            ticketIconClicked();
                            break;
                        case MyFirebaseMessagingService.viId:
                            videosIconClicked();
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
                case 0: activityIconClicked(0);
                break;
                case 1: videosIconClicked();
                break;
                case 2: playlistIconClicked();
                break;
                case 3: ticketIconClicked();
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

    @SuppressLint("RestrictedApi")
    public static void removeShiftMode(BottomNavigationView view) {
        BottomNavigationMenuView menuView = (BottomNavigationMenuView) view.getChildAt(0);
        try {
            Field shiftingMode = menuView.getClass().getDeclaredField("mShiftingMode");
            shiftingMode.setAccessible(true);
            shiftingMode.setBoolean(menuView, false);
            shiftingMode.setAccessible(false);
            for (int i = 0; i < menuView.getChildCount(); i++) {
                BottomNavigationItemView item = (BottomNavigationItemView) menuView.getChildAt(i);
                //noinspection RestrictedApi
                item.setShiftingMode(false);
                // set once again checked value, so view will be updated
                //noinspection RestrictedApi
                item.setChecked(item.getItemData().isChecked());
            }
        } catch (NoSuchFieldException e) {
            Log.e("BottomNav", "Unable to get shift mode field", e);
        } catch (IllegalAccessException e) {
            Log.e("BottomNav", "Unable to change value of shift mode", e);
        }
    }
}
