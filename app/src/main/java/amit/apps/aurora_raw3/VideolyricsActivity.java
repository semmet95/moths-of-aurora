package amit.apps.aurora_raw3;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerSupportFragment;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;

import amit.apps.aurora_raw3.adapters.VideosAdapter;
import amit.apps.aurora_raw3.datainitializers.PlaylistsHolder;

public class VideolyricsActivity extends AppCompatActivity {
    public static VideosAdapter.VideosPojo[] itemsvideopojo;
    public static int currentpojo;
    private static ArrayList<String> playlistids;
    private TextView videotitle, lyricstext;
    private Button addlyrics;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_videolyrics);

        videotitle=findViewById(R.id.videotitle);
        lyricstext=findViewById(R.id.lyricstext);
        addlyrics = findViewById(R.id.addlyrics);

        YouTubePlayerSupportFragment ytFragment= (YouTubePlayerSupportFragment)
                getSupportFragmentManager().findFragmentById(R.id.ytFragment);
        ytFragment.initialize(getResources().getString(R.string.api_key),
                new YouTubePlayer.OnInitializedListener() {
                    @Override
                    public void onInitializationSuccess(YouTubePlayer.Provider provider,
                                                        YouTubePlayer youTubePlayer, boolean b) {
                        youTubePlayer.setPlaylistEventListener(new YouTubePlayer.PlaylistEventListener() {
                            @Override
                            public void onPrevious() {
                                if(currentpojo>0) {
                                    --currentpojo;
                                    refreshvideodetails();
                                }
                            }

                            @Override
                            public void onNext() {
                                Log.e("youyube player :", "next is working");
                                if(currentpojo<playlistids.size()-1) {
                                    ++currentpojo;
                                    refreshvideodetails();
                                }
                            }

                            @Override
                            public void onPlaylistEnded() {

                            }
                        });
                        youTubePlayer.loadVideos(playlistids, currentpojo, 0);
                    }

                    @Override
                    public void onInitializationFailure(YouTubePlayer.Provider provider,
                                                        YouTubeInitializationResult youTubeInitializationResult) {
                        youTubeInitializationResult.getErrorDialog(VideolyricsActivity.this, 11).show();
                    }
                });

        refreshvideodetails();
    }

    public void addiconClicked(View v) {
        final ImageButton playlistaddbutton=findViewById(R.id.playlistaddbutton);
        final CharSequence[] items=PlaylistsHolder.getPlaylistTitles().toArray(new CharSequence[0]);
        final ArrayList<Integer> seletedItems=new ArrayList<>();
        playlistaddbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean[] playlistvideorelation= PlaylistsHolder.getPlaylistvideorelation(itemsvideopojo[currentpojo]);

                AlertDialog playlistchooser=new AlertDialog.Builder(playlistaddbutton.getContext())
                        .setTitle("Select Playlists")
                        .setMultiChoiceItems(items, playlistvideorelation, new DialogInterface.OnMultiChoiceClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                                if(isChecked)
                                    seletedItems.add(which);
                                else if(seletedItems.contains(which))
                                    seletedItems.remove(which);
                            }
                        }).setPositiveButton("Add", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                HashSet<String> selectedplaylists=new HashSet<>();
                                for(int x: seletedItems) {
                                    selectedplaylists.add(items[x].toString());
                                }
                                PlaylistsHolder.addtoplaylist(selectedplaylists, itemsvideopojo[currentpojo]);
                            }
                        }).setNegativeButton("Nah", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        }).create();
                playlistchooser.show();
            }
        });
    }

    public void addlyricsclicked(View v) {
        AlertDialog.Builder builder=new AlertDialog.Builder(this);
        builder.setTitle("Name the song");
        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_TEXT_VARIATION_SHORT_MESSAGE);
        builder.setView(input);

        builder.setPositiveButton("Get lyrics", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String title=input.getText().toString();
                new FetchLyrics(title, itemsvideopojo[currentpojo].getTitle(), VideolyricsActivity.this).execute();
            }
        });

        builder.setNegativeButton("Abort!", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }

    private void refreshvideodetails() {
        videotitle.setText(itemsvideopojo[currentpojo].getTitle());
        String title=PlaylistsHolder.gettitlefromvideo(itemsvideopojo[currentpojo].getTitle());
        if(title!=null) {
            String thelyrics=PlaylistsHolder.getlyricsfromtitle(title);

            if(thelyrics!=null) {
                addlyrics.setVisibility(View.GONE);
                lyricstext.setVisibility(View.VISIBLE);
                thelyrics="\n"+thelyrics;
                lyricstext.setText(thelyrics);
            }
        }
    }

    public static void createPlaylist() {
        playlistids=new ArrayList<>();
        for(VideosAdapter.VideosPojo x: itemsvideopojo) {
            playlistids.add(x.getVideoId());
        }
    }

    private static class FetchLyrics extends AsyncTask<Void, Void, Void> {
        String lyrics, inputitle, currvid;
        WeakReference<VideolyricsActivity> weakReference;

        FetchLyrics(String inp, String cv, VideolyricsActivity va) {
            this.inputitle=inp;
            this.currvid=cv;
            weakReference= new WeakReference<>(va);
            inputitle=inputitle.trim();
            inputitle=inputitle.replaceAll(" ", "+");
        }

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                StringBuilder response  = new StringBuilder();

                URL url = new URL("https://lyricsprovideraurora.herokuapp.com/?title="+inputitle);
                HttpURLConnection httpconn = (HttpURLConnection)url.openConnection();
                BufferedReader input = new BufferedReader(new InputStreamReader(httpconn.getInputStream()),8192);
                String strLine;
                while ((strLine = input.readLine()) != null)
                    response.append(strLine).append("\n");
                input.close();

                lyrics=response.toString();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            lyrics=lyrics.trim();
            if(lyrics.compareTo("NA")==0) {
                Toast.makeText(weakReference.get(), "Sorry lyrics not avaiable, try again later",
                        Toast.LENGTH_LONG).show();
            }
            else {
                PlaylistsHolder.addtitletovideo(currvid, inputitle);
                PlaylistsHolder.addlyricstosongs(inputitle, lyrics);
                weakReference.get().refreshvideodetails();
            }
        }
    }
}
