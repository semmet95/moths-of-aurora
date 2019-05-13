package amit.apps.aurora_raw3;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import amit.apps.aurora_raw3.adapters.PlaylistsAdapter;

public class PlaylistFragment extends Fragment {
    public RecyclerView playlistrecycler;

    public static Boolean isWindowActive = false;

    public PlaylistFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.fragment_playlist, container, false);

        MainActivity.fragmenttoload=2;
        playlistrecycler=layout.findViewById(R.id.playlistrecycler);

        isWindowActive = true;

        playlistrecycler.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
        playlistrecycler.setAdapter(new PlaylistsAdapter(this));

        return layout;
    }

}
