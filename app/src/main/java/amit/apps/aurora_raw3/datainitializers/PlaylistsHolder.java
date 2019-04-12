package amit.apps.aurora_raw3.datainitializers;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;

import java.util.HashSet;
import java.util.Set;

import amit.apps.aurora_raw3.adapters.VideosAdapter;

public class PlaylistsHolder {
    public static SharedPreferences sharedPreferences, lyricsandsongs;
    private static Set<String> playlistTitles;
    private static Gson gson;

    public static void initiaizeSharedPreferences(Context c) {
        sharedPreferences=c.getSharedPreferences("playlists", Context.MODE_PRIVATE);
        lyricsandsongs =c.getSharedPreferences("lyricsandsongs", Context.MODE_PRIVATE);
        gson=new Gson();
    }

    public static Set<String> getPlaylistTitles() {
        playlistTitles=sharedPreferences.getStringSet("playlistTitles", new HashSet<String>());
        return playlistTitles;
    }

    public static void addPlaylistTitle(String t) {
        playlistTitles=sharedPreferences.getStringSet("playlistTitles", new HashSet<String>());
        playlistTitles.add(t);

        sharedPreferences.edit().remove("playlistTitles").apply();
        sharedPreferences.edit().putStringSet("playlistTitles", playlistTitles).apply();
    }

    public static String getPlaylistThumbnail(String t) {
        String firstsong;
        try {
            firstsong = sharedPreferences.getStringSet(t, null).iterator().next();
        } catch (Exception ex) {return null;}

        VideosAdapter.VideosPojo videoPojo=gson.fromJson(firstsong, VideosAdapter.VideosPojo.class);
        return videoPojo.getThumbnailUrlForPLaylist();
    }

    public static void deletePlaylist(String t) {
        playlistTitles=sharedPreferences.getStringSet("playlistTitles", new HashSet<String>());
        playlistTitles.remove(t);

        sharedPreferences.edit().remove("playlistTitles").apply();
        sharedPreferences.edit().putStringSet("playlistTitles", playlistTitles).apply();
        sharedPreferences.edit().remove(t).apply();
    }

    public static void addtoplaylist(HashSet<String> selectedplaylists, VideosAdapter.VideosPojo videosPojo) {
        String video_json=gson.toJson(videosPojo);
        playlistTitles=sharedPreferences.getStringSet("playlistTitles", new HashSet<String>());
        for(String playlist: playlistTitles) {
            Set<String> json_set=sharedPreferences.getStringSet(playlist, new HashSet<String>());
            if(selectedplaylists.contains(playlist)) {
                json_set.add(video_json);
            } else {
                json_set.remove(video_json);
            }
            sharedPreferences.edit().remove(playlist).apply();
            sharedPreferences.edit().putStringSet(playlist, json_set).apply();
        }
    }

    public static void removefromplaylist(String title, String video) {
        HashSet<String> items= (HashSet<String>) sharedPreferences.getStringSet(title, null),
                itemscopy=new HashSet<>();
        if(items!=null) {
            for(String x: items) {
                VideosAdapter.VideosPojo itempojo=gson.fromJson(x, VideosAdapter.VideosPojo.class);
                if(itempojo.getTitle().compareTo(video)!=0)
                    itemscopy.add(x);
            }

            sharedPreferences.edit().remove(title).apply();
            sharedPreferences.edit().putStringSet(title, itemscopy).apply();
        }
    }

    public static boolean[] getPlaylistvideorelation(VideosAdapter.VideosPojo videosPojo) {
        playlistTitles=sharedPreferences.getStringSet("playlistTitles", new HashSet<String>());
        boolean[] relation=new boolean[playlistTitles.size()];
        String video_json=gson.toJson(videosPojo);
        int i=0;
        for(String playlist: playlistTitles) {
            Set<String> playlistitems = sharedPreferences.getStringSet(playlist, null);
            relation[i++] = playlistitems != null && playlistitems.contains(video_json);
        }
        return relation;
    }

    public static String[] getItemsfromplaylist(String title) {
        return sharedPreferences.getStringSet(title, new HashSet<String>()).toArray(new String[0]);
    }

    public static VideosAdapter.VideosPojo[] getPojosfromtitle(String playlisttitle) {
        String[] items_json=getItemsfromplaylist(playlisttitle);
        VideosAdapter.VideosPojo itemsvideopojo[]=new VideosAdapter.VideosPojo[items_json.length];
        for(int i=0;i<items_json.length;i++) {
            itemsvideopojo[i]=gson.fromJson(items_json[i], VideosAdapter.VideosPojo.class);
        }
        return itemsvideopojo;
    }

    public static String gettitlefromvideo(String vid) {
        return lyricsandsongs.getString(vid, null);
    }

    public static void addtitletovideo(String vid, String title) {
        lyricsandsongs.edit().putString(vid, title).apply();
    }

    public static void addlyricstosongs(String title, String lyrics) {
        lyricsandsongs.edit().putString(title, lyrics).apply();
    }

    public static String getlyricsfromtitle(String title) {
        return lyricsandsongs.getString(title, null);
    }
}