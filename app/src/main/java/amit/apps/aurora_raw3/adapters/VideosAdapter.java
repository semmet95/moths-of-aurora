package amit.apps.aurora_raw3.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.HashSet;

import amit.apps.aurora_raw3.FullscreenYoutubeActivity;
import amit.apps.aurora_raw3.R;
import amit.apps.aurora_raw3.datainitializers.PlaylistsHolder;

public class VideosAdapter {

    public static class VideosViewHolder extends RecyclerView.ViewHolder {
        ImageView videothumbnail;
        ImageButton playlistaddbutton;
        TextView videoduration, videotitle, videouploader, videoviews, videodate;

        public VideosViewHolder(View itemView) {
            super(itemView);

            videothumbnail=itemView.findViewById(R.id.videothumbnail);
            playlistaddbutton=itemView.findViewById(R.id.playlistaddbutton);
            videoduration=itemView.findViewById(R.id.videoduration);
            videotitle=itemView.findViewById(R.id.videotitle);
            videouploader=itemView.findViewById(R.id.videouploader);
            videodate=itemView.findViewById(R.id.videodate);
            videoviews=itemView.findViewById(R.id.videoviews);
        }

        public void setVideothumbnail(String url, Context context) {
            Glide.with(context).load(url).into(videothumbnail);
        }

        public void setVideoduration(String d) {
            videoduration.setText(d);
        }

        public void setVideotitle(String t) {
            videotitle.setText(t);
        }

        public void setVideouploader(String u) {
            videouploader.setText(u);
        }

        public void setVideoviews(String v) {
            videoviews.setText(v);
        }

        public void setUrl(final String u) {
            videothumbnail.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent=new Intent(videothumbnail.getContext(), FullscreenYoutubeActivity.class);
                    intent.putExtra("videourl", u);
                    videothumbnail.getContext().startActivity(intent);
                }
            });
        }

        public void setUploadDate(String u) {
            videodate.setText(u);
        }

        public void addToPlaylist(final VideosPojo videosPojo) {
            final CharSequence[] items=PlaylistsHolder.getPlaylistTitles().toArray(new CharSequence[0]);
            final ArrayList<Integer> seletedItems=new ArrayList<>();

            playlistaddbutton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    boolean[] playlistvideorelation=PlaylistsHolder.getPlaylistvideorelation(videosPojo);

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
                                    PlaylistsHolder.addtoplaylist(selectedplaylists, videosPojo);
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
    }

    public static class VideosPojo {
        public String duration, title, upload_date, uploader, url, views;

        public VideosPojo(){}

        public VideosPojo(String duration, String title, String upload_date, String uploader, String url, String views) {
            this.duration = duration;
            this.title = title;
            this.upload_date = upload_date;
            this.uploader = uploader;
            this.url = url;
            this.views = views;
        }

        public String getDuration() {
            return duration;
        }

        public void setDuration(String duration) {
            this.duration = duration;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getUpload_date() {
            return upload_date;
        }

        public void setUpload_date(String upload_date) {
            this.upload_date = upload_date;
        }

        public String getUploader() {
            return uploader;
        }

        public void setUploader(String uploader) {
            this.uploader = uploader;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public String getViews() {
            return views;
        }

        public void setViews(String views) {
            this.views = views;
        }

        public String getThumbnailUrl() {
            return "https://img.youtube.com/vi/"+url.substring(url.indexOf('=')+1)+"/mqdefault.jpg";
        }

        public String getVideoId() {
            return url.substring(url.indexOf('=')+1);
        }

        public String getThumbnailUrlForPLaylist() {
            return "https://img.youtube.com/vi/"+url.substring(url.indexOf('=')+1)+"/hqdefault.jpg";
        }
    }
}