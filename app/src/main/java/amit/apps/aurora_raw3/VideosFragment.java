package amit.apps.aurora_raw3;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;

import amit.apps.aurora_raw3.adapters.VideosAdapter;
import amit.apps.aurora_raw3.datainitializers.DatabaseHolder;
import amit.apps.aurora_raw3.notifications.MyFirebaseMessagingService;

public class VideosFragment extends Fragment {
    RecyclerView videosrecycler;
    FirebaseRecyclerAdapter firebaseRecyclerAdapter;

    public VideosFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.fragment_videos, container, false);
        setHasOptionsMenu(true);

        MainActivity.fragmenttoload=1;
        MyFirebaseMessagingService.unseennotifications.remove("Youtube");
        getActivity().invalidateOptionsMenu();

        android.support.v7.app.ActionBar actionBar = ((MainActivity)getActivity()).getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle("Videos");
        }

        videosrecycler=layout.findViewById(R.id.videosrecycler);

        populateRecycler();
        return layout;
    }

    private void populateRecycler() {
        FirebaseRecyclerOptions<VideosAdapter.VideosPojo> options=new FirebaseRecyclerOptions.
                Builder<VideosAdapter.VideosPojo>()
                .setQuery(DatabaseHolder.videosref, VideosAdapter.VideosPojo.class)
                .build();

        firebaseRecyclerAdapter=new FirebaseRecyclerAdapter<VideosAdapter.VideosPojo, VideosAdapter.VideosViewHolder>(options) {
            @Override
            public VideosAdapter.VideosViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                View view=LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.videocard, parent, false);
                return new VideosAdapter.VideosViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(VideosAdapter.VideosViewHolder holder, int position,
                                            VideosAdapter.VideosPojo model) {
                String tempurl=model.getUrl();
                String thumbnailurl="https://img.youtube.com/vi/"+tempurl.substring(tempurl.indexOf('=')+1)+"/mqdefault.jpg";
                holder.setVideothumbnail(thumbnailurl, getContext());
                holder.setVideoduration(model.getDuration());
                holder.setVideotitle(model.getTitle());
                holder.setVideouploader(model.getUploader());
                holder.setUploadDate(model.getUpload_date());
                holder.setVideoviews(model.getViews()+" views");
                holder.setUrl(model.getUrl());

                holder.addToPlaylist(model);
            }

            @Override
            public void onDataChanged() {
                super.onDataChanged();
            }
        };

        videosrecycler.setAdapter(firebaseRecyclerAdapter);
        videosrecycler.setLayoutManager(new LinearLayoutManager(getContext()));
    }

    @Override
    public void onStart() {
        super.onStart();
        firebaseRecyclerAdapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        firebaseRecyclerAdapter.stopListening();
    }
}
