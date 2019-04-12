package amit.apps.aurora_raw3;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import amit.apps.aurora_raw3.adapters.PlaylistsAdapter;
import amit.apps.aurora_raw3.datainitializers.PlaylistsHolder;

public class PlaylistFragment extends Fragment {
    public RecyclerView playlistrecycler;
    private FloatingActionButton addbutton;

    public PlaylistFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.fragment_playlist, container, false);

        MainActivity.fragmenttoload=2;
        playlistrecycler=layout.findViewById(R.id.playlistrecycler);
        addbutton=layout.findViewById(R.id.addbutton);
        addbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addbutton.hide();
                AlertDialog.Builder builder=new AlertDialog.Builder(getContext());
                builder.setTitle("Name your playlist");
                final EditText input = new EditText(getContext());
                input.setInputType(InputType.TYPE_TEXT_VARIATION_SHORT_MESSAGE);
                builder.setView(input);

                builder.setPositiveButton("Create", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String playlistTitle=input.getText().toString();
                        if(playlistTitle.length()>0) {
                            PlaylistsHolder.addPlaylistTitle(playlistTitle);
                            resetRecycler();
                        }
                    }
                });

                builder.setNegativeButton("Nah", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        addbutton.show();
                    }
                });

                builder.show();
            }
        });

        android.support.v7.app.ActionBar actionBar = ((MainActivity)getActivity()).getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle("Playlists");
        }

        resetRecycler();
        playlistrecycler.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
        return layout;
    }

    public void resetRecycler() {
        playlistrecycler.setAdapter(new PlaylistsAdapter(this));
    }
}
