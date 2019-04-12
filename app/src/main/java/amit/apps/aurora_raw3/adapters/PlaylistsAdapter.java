package amit.apps.aurora_raw3.adapters;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Color;
import android.net.Uri;
import android.support.v4.content.FileProvider;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.amulyakhare.textdrawable.TextDrawable;
import com.bumptech.glide.Glide;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

import amit.apps.aurora_raw3.PlaylistFragment;
import amit.apps.aurora_raw3.PlaylistItemsActivity;
import amit.apps.aurora_raw3.R;
import amit.apps.aurora_raw3.CachedFileProvider;
import amit.apps.aurora_raw3.datainitializers.PlaylistsHolder;
import io.paperdb.Paper;

public class PlaylistsAdapter extends RecyclerView.Adapter<PlaylistsAdapter.PlaylistsViewHolder> {
    private String[] playlistTitles;
    private PlaylistFragment playlistFragment;

    public PlaylistsAdapter(PlaylistFragment pf) {
        playlistFragment=pf;
        playlistTitles= PlaylistsHolder.getPlaylistTitles().toArray(new String[0]);
    }

    @Override
    public PlaylistsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.playlistscard, parent, false);
        return new PlaylistsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final PlaylistsViewHolder holder, int position) {
        final String url=PlaylistsHolder.getPlaylistThumbnail(playlistTitles[position]);
        holder.setPlaylistthumbnail(holder.playlistthumbnail.getContext(), url, playlistTitles[position]);
        holder.setPlaylisttitle(playlistTitles[position]);

        holder.playlistremovebutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PlaylistsHolder.deletePlaylist(playlistTitles[holder.getAdapterPosition()]);
                playlistFragment.playlistrecycler.swapAdapter(new PlaylistsAdapter(playlistFragment)
                        , false);
            }
        });

        holder.playlistthumbnail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(holder.playlistthumbnail.getContext(), PlaylistItemsActivity.class);
                intent.putExtra("playlisttitle", playlistTitles[holder.getAdapterPosition()]);
                intent.putExtra("playlistthumbnailurl", url);
                intent.putExtra("playlistitemslist",
                        PlaylistsHolder.getItemsfromplaylist(playlistTitles[holder.getAdapterPosition()]));
                playlistFragment.startActivity(intent);
            }
        });

        holder.sharebutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(playlistFragment.getContext(), "We recommend using gmail to share the playlist",
                        Toast.LENGTH_SHORT).show();
                PlaylistObject playlistObject=new PlaylistObject(playlistTitles[holder.getAdapterPosition()],
                        PlaylistsHolder.getItemsfromplaylist(playlistTitles[holder.getAdapterPosition()]));
                Paper.book().write("playlistobject", playlistObject);
                String filePath=Paper.book().getPath("playlistobject");

                Log.e("paper file path :", filePath);

                String modifiedfilePath="/"+filePath.substring(filePath.indexOf("files"));
                Intent playlistintent=new Intent(android.content.Intent.ACTION_SEND);
                File root = playlistFragment.getContext().getFilesDir();
                //Uri uri=Uri.parse("content://"
                       // + CachedFileProvider.AUTHORITY + filePath);
                Uri uri=Uri.parse(filePath);
                Log.e("checking uri :",
                        new File(uri.getPath()).exists()+"");
                playlistintent.setType("plain/text");
                //playlistintent.setData(Uri.parse("mailto:"));


                File file = new File(uri.getPath());
                File outfile=copyFileToInternal(file);

                if(outfile!=null) {
                    Uri contentUri = FileProvider.getUriForFile(playlistFragment.getContext(),
                            CachedFileProvider.AUTHORITY, outfile);
                    playlistintent.putExtra(Intent.EXTRA_STREAM, contentUri);
                }
                if (!file.exists() || !file.canRead()) {
                    Log.e("accessing file :", "file exists = "+file.exists()+" file can read = "+file.canRead()
                            +" and file size = "+file.length());
                } else
                    Log.e("accessing file :", "here in else file exists = "+file.exists()

                            +" file can read = "+file.canRead()+" and file size = "+file.length());

                //playlistintent.putExtra(android.content.Intent.EXTRA_CC, "");
                //playlistintent.putExtra(android.content.Intent.EXTRA_BCC, "");
                //playlistintent.putExtra(Intent.EXTRA_STREAM, uri);
                playlistintent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Moths of Aurora playlist");
                playlistintent.putExtra(Intent.EXTRA_TEXT, "Download Moths of Aurora to enjoy this playlist I created" +
                        "\nhttp://play.google.com/store/apps/details?id="+playlistFragment.getContext().getPackageName());
                Intent backupIntent=new Intent(playlistintent);
                //playlistintent.setClassName("com.google.android.gm",
                       // "com.google.android.gm.ComposeActivityGmail");
                //playlistFragment.startActivity(playlistintent);
                List<ResolveInfo> resInfoList = playlistFragment.getContext().getPackageManager()
                        .queryIntentActivities(playlistintent, PackageManager.MATCH_DEFAULT_ONLY);
                for (ResolveInfo resolveInfo : resInfoList) {
                    String packageName = resolveInfo.activityInfo.packageName;
                    playlistFragment.getContext().grantUriPermission(packageName, uri,
                            Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
                }
                try {
                    playlistFragment.startActivity(playlistintent);
                    //playlistFragment.startActivity(Intent.createChooser(playlistintent, "Choose an email client"));
                } catch(ActivityNotFoundException ex) {
                    //Log.e("oh :", "shit");
                    playlistFragment.startActivity(Intent.createChooser(backupIntent, "Choose an email client"));
                }
            }
        });
    }

    private File copyFileToInternal(File filepath) {
        try {
            Log.e("trying to copy", "in copy");
            InputStream is = new FileInputStream(filepath);

            File cacheDir = playlistFragment.getContext().getCacheDir();
            File outFile = new File(cacheDir, "playlistobject.pt");

            OutputStream os = new FileOutputStream(outFile.getAbsolutePath());

            byte[] buff = new byte[1024];
            int len;
            while ((len = is.read(buff)) > 0) {
                os.write(buff, 0, len);
            }
            Log.e("new file :", "file size = "+outFile.length()+" file path = "+outFile.getPath());
            os.flush();
            os.close();
            is.close();
            return outFile;

        } catch (IOException e) {
            Log.e("trying to copy", "caught exception = "+e.toString());
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public int getItemCount() {
        return playlistTitles.length;
    }

    class PlaylistsViewHolder extends RecyclerView.ViewHolder {
        ImageView playlistthumbnail;
        ImageButton playlistremovebutton, sharebutton;
        TextView playlisttitle;

        PlaylistsViewHolder(View itemView) {
            super(itemView);

            playlistthumbnail=itemView.findViewById(R.id.playlistthumbnail);
            playlistremovebutton=itemView.findViewById(R.id.playlistremovebutton);
            playlisttitle=itemView.findViewById(R.id.playlisttitle);
            sharebutton=itemView.findViewById(R.id.playlistsharebutton);
        }

        void setPlaylistthumbnail(Context c, String url, String title) {
            if(url==null) {
                TextDrawable textDrawable=TextDrawable.builder().beginConfig()
                        .width((int) playlistFragment.getResources().getDimension(R.dimen.playlistcardsizepxwidth))
                        .height((int) playlistFragment.getResources().getDimension(R.dimen.playlistcardsizepxheight))
                        .endConfig().buildRect(title.charAt(0)+"", Color.RED);
                playlistthumbnail.setImageDrawable(textDrawable);
            } else {
                Glide.with(c).load(url).into(playlistthumbnail);
            }
        }

        void setPlaylisttitle(String title) {
            playlisttitle.setText(title);
        }
    }

    public static class PlaylistObject {
        public String title;
        public String[] items;

        PlaylistObject(String t, String[] it) {
            title=t;
            items=it;
        }
    }
}
