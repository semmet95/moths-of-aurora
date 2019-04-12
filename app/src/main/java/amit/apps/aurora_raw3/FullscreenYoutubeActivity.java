package amit.apps.aurora_raw3;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerView;

public class FullscreenYoutubeActivity extends YouTubeBaseActivity {
private YouTubePlayerView youTubePlayerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fullscreen_youtube);

        youTubePlayerView=findViewById(R.id.youtubeplayerview);

        Intent caller=getIntent();
        if(caller!=null && caller.getExtras()!=null) {
            Bundle extras = caller.getExtras();
            loadVideo(extras.getString("videourl"));
        }
    }

    private void loadVideo(final String url) {
        youTubePlayerView.initialize(getResources().getString(R.string.api_key),
                new YouTubePlayer.OnInitializedListener() {
            @Override
            public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer youTubePlayer, boolean b) {
                youTubePlayer.setShowFullscreenButton(false);
                youTubePlayer.setFullscreen(true);
                youTubePlayer.cueVideo(url.substring(url.indexOf('=')+1));
            }

            @Override
            public void onInitializationFailure(YouTubePlayer.Provider provider,
                                                YouTubeInitializationResult youTubeInitializationResult) {
                Toast.makeText(FullscreenYoutubeActivity.this,
                        "Sorry, unable the play the video, player initialization failed", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
