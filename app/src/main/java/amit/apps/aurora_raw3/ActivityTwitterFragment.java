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
import com.firebase.ui.database.SnapshotParser;
import com.google.firebase.database.DataSnapshot;

import amit.apps.aurora_raw3.adapters.TwitterAdapter;
import amit.apps.aurora_raw3.datainitializers.DatabaseHolder;
import amit.apps.aurora_raw3.notifications.MyFirebaseMessagingService;

public class ActivityTwitterFragment extends Fragment {
    RecyclerView twitterrecycler;
    FirebaseRecyclerAdapter firebaseRecyclerAdapter;

    public ActivityTwitterFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View layout= inflater.inflate(R.layout.fragment_activity_twitter, container, false);
        setHasOptionsMenu(true);

        MyFirebaseMessagingService.unseennotifications.remove("Twitter");

        ActivityFragment.tabtoload=2;

        twitterrecycler =layout.findViewById(R.id.twitterrecycler);

        populateRecycler();
        return layout;
    }

    private void populateRecycler() {
        FirebaseRecyclerOptions<TwitterAdapter.TwitterPojo> options=new FirebaseRecyclerOptions.
                Builder<TwitterAdapter.TwitterPojo>()
                .setQuery(DatabaseHolder.twitterref,
                        new SnapshotParser<TwitterAdapter.TwitterPojo>(){

                            @Override
                            public TwitterAdapter.TwitterPojo parseSnapshot(DataSnapshot snapshot) {
                                TwitterAdapter.TwitterPojo twitterPojo=new TwitterAdapter.TwitterPojo();
                                try {
                                    String pic_url;
                                    try {
                                        pic_url = snapshot.child("extended_entities")
                                                .child("media").child("0").child("media_url_https")
                                                .getValue().toString();
                                    } catch (NullPointerException e) {
                                        pic_url = null;
                                    }
                                    if (pic_url != null)
                                        twitterPojo.setFull_picture(pic_url);

                                    String message;
                                    try {
                                        message = snapshot.child("text").getValue().toString();
                                    } catch (NullPointerException e) {
                                        message = null;
                                    }
                                    if (message != null)
                                        twitterPojo.setMessage(message);

                                    String time=snapshot.child("created_at").getValue().toString();
                                    time=time.substring(4, time.indexOf('+')-1);
                                    twitterPojo.setCreated_time(time);

                                    twitterPojo.setLink("https://www.twitter.com/auroramusic/status/"
                                            + snapshot.child("id_str").getValue());
                                }catch (NullPointerException ignored){}
                                return twitterPojo;
                            }
                        })
                .build();

        firebaseRecyclerAdapter=new FirebaseRecyclerAdapter<TwitterAdapter.TwitterPojo,
                TwitterAdapter.TwitterViewHolder>(options) {
            @Override
            public TwitterAdapter.TwitterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                View view=LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.twittercard, parent, false);
                return new TwitterAdapter.TwitterViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(TwitterAdapter.TwitterViewHolder holder, int position,
                                            TwitterAdapter.TwitterPojo model) {

                holder.setTimeposted(model.getCreated_time());
                holder.setPostmessage(model.getMessage());
                holder.setProfilethumbnail(DatabaseHolder.getTwitterprofilepicurl(), getParentFragment());
                holder.setPostthumbnail(model.getFull_picture(), getParentFragment());
                holder.setUrl(model.getLink());
            }

            @Override
            public void onDataChanged() {
                super.onDataChanged();
            }
        };

        twitterrecycler.setAdapter(firebaseRecyclerAdapter);
        twitterrecycler.setLayoutManager(new LinearLayoutManager(getContext()));
    }

    @Override
    public void onStart() {
        super.onStart();
        firebaseRecyclerAdapter.startListening();
        getActivity().invalidateOptionsMenu();
    }

    @Override
    public void onStop() {
        super.onStop();
        firebaseRecyclerAdapter.stopListening();
    }
}
