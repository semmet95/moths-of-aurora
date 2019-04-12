package amit.apps.aurora_raw3;

import android.content.Intent;
import android.net.Uri;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.ImageView;
import android.support.v7.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashSet;

import amit.apps.aurora_raw3.adapters.ItemsAdapter;
import amit.apps.aurora_raw3.adapters.PlaylistsAdapter;
import amit.apps.aurora_raw3.adapters.VideosAdapter;
import amit.apps.aurora_raw3.datainitializers.DatabaseHolder;
import amit.apps.aurora_raw3.datainitializers.PlaylistsHolder;
import amit.apps.aurora_raw3.datainitializers.SettingsHolder;
import io.paperdb.Paper;

public class PlaylistItemsActivity extends AppCompatActivity {
    CollapsingToolbarLayout collapsingToolbarLayout;
    ImageView toolbarbackground;
    Toolbar toolbar;
    RecyclerView playlistitemsrecycler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playlist_items);

        playlistitemsrecycler = findViewById(R.id.playlistitemsrecycler);
        collapsingToolbarLayout=findViewById(R.id.toolbar_layout);
        toolbarbackground=findViewById(R.id.toolbarbackground);
        toolbar=findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //Log.e("in activity :", "in oncreate");

        Intent intent=getIntent();
        if(intent!=null && intent.getExtras()!=null) {
            Bundle extras=intent.getExtras();

            Glide.with(this).load(extras.getString("playlistthumbnailurl")).into(toolbarbackground);
            collapsingToolbarLayout.setTitle(extras.getString("playlisttitle"));

            playlistitemsrecycler.setAdapter(new ItemsAdapter(extras.getString("playlisttitle")));
            playlistitemsrecycler.setLayoutManager(new LinearLayoutManager(this));
        }
    }

    private void copyFileToInternal(Uri filepath) {
        try {
            //Log.e("trying to copy", "in copy");
            InputStream is = getContentResolver().openInputStream(filepath);

            String intStorageDirectory = getFilesDir().toString();
            File filesDir = new File(intStorageDirectory, "io.paperdb");
            File outFile = new File(filesDir, "playlistobject.pt");

            OutputStream os = new FileOutputStream(outFile.getAbsolutePath());

            byte[] buff = new byte[1024];
            int len;
            while ((len = is.read(buff)) > 0) {
                os.write(buff, 0, len);
            }
            //Log.e("new file :", "file size = "+outFile.length()+" file path = "+outFile.getPath());
            os.flush();
            os.close();
            is.close();

        } catch (IOException e) {
            //Log.e("trying to copy", "caught exception = "+e.toString());
            e.printStackTrace();
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    protected void onStart() {
        super.onStart();
        //Log.e("in activity :", "in onstart");
        Intent intent=getIntent();
        if(intent!=null && intent.getData()!=null) {
            //Log.e("in activity :", "going the right way");
            Paper.init(getApplicationContext());
            PlaylistsHolder.initiaizeSharedPreferences(getApplicationContext());
            SettingsHolder.initiaizeSharedPreferences(getApplicationContext());
            DatabaseHolder.initializesongsarray();

            Uri incomingFile=intent.getData();
            intent.setData(null);

            String intStorageDirectory = getFilesDir().toString();
            //Log.e("URI :", "for Files dir = "+intStorageDirectory);
            Paper.book().write("temp", "temp");
            Paper.book().getPath("temp");
            //Log.e("URI :", "for incoming file = "+incomingFile.toString());
            copyFileToInternal(incomingFile);
            String newpath = Paper.book().getPath("playlistobject");
            //Log.e("new book :", "path = "+newpath);
            PlaylistsAdapter.PlaylistObject playlistObject=Paper.book().read("playlistobject");
            PlaylistsHolder.addPlaylistTitle(playlistObject.title);
            HashSet<String> addtoplaylist=new HashSet<>();
            addtoplaylist.add(playlistObject.title);
            Gson gson=new Gson();
            for(String x: playlistObject.items) {
                VideosAdapter.VideosPojo videosPojo=gson.fromJson(x, VideosAdapter.VideosPojo.class);
                PlaylistsHolder.addtoplaylist(addtoplaylist, videosPojo);
            }
            Glide.with(this).load(PlaylistsHolder.getPlaylistThumbnail(playlistObject.title)).into(toolbarbackground);
            collapsingToolbarLayout.setTitle(playlistObject.title);

            playlistitemsrecycler.setAdapter(new ItemsAdapter(playlistObject.title));
            playlistitemsrecycler.setLayoutManager(new LinearLayoutManager(this));

        }
    }

    @Override
    public void onBackPressed() {
        if(isTaskRoot()) {
            Intent intent=new Intent(this, MainActivity.class);
            startActivity(intent);
        }
        super.onBackPressed();
    }
}
