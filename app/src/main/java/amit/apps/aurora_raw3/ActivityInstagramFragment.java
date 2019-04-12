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

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.Locale;
import java.util.TimeZone;

import amit.apps.aurora_raw3.adapters.InstagramAdapter;
import amit.apps.aurora_raw3.datainitializers.DatabaseHolder;
import amit.apps.aurora_raw3.notifications.MyFirebaseMessagingService;

public class ActivityInstagramFragment extends Fragment {
    RecyclerView instagramrecycler;
    FirebaseRecyclerAdapter firebaseRecyclerAdapter;

    public ActivityInstagramFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View layout=inflater.inflate(R.layout.fragment_activity_instagram, container, false);
        setHasOptionsMenu(true);

        MyFirebaseMessagingService.unseennotifications.remove("Instagram");

        ActivityFragment.tabtoload=1;

        instagramrecycler=layout.findViewById(R.id.instagramrecycler);

        populateRecycler();
        return layout;
    }

    private void populateRecycler() {
        FirebaseRecyclerOptions<InstagramAdapter.InstagramPojo> options=new FirebaseRecyclerOptions.
                Builder<InstagramAdapter.InstagramPojo>()
                .setQuery(DatabaseHolder.instagramref,
                        new SnapshotParser<InstagramAdapter.InstagramPojo>(){

                            @Override
                            public InstagramAdapter.InstagramPojo parseSnapshot(DataSnapshot snapshot) {
                                InstagramAdapter.InstagramPojo instagramPojo = new InstagramAdapter.InstagramPojo();
                                try {
                                    Iterator<DataSnapshot> iterator;
                                    DataSnapshot temp;
                                    iterator = snapshot.child("thumbnail_resources").getChildren().iterator();
                                    iterator.next();
                                    iterator.next();
                                    temp = iterator.next();
                                    instagramPojo.setFull_picture(temp.child("src").getValue().toString());

                                    String message;
                                    try {
                                        message = snapshot.child("edge_media_to_caption").child("edges")
                                                .child("0").child("node").child("text").getValue().toString();
                                    } catch (NullPointerException e) {
                                        message = null;
                                    }
                                    if (message != null)
                                        instagramPojo.setMessage(message);

                                    instagramPojo.setCreated_time(getDateAndTime(Long.parseLong(snapshot
                                            .child("taken_at_timestamp").getValue().toString())));

                                    instagramPojo.setLink(snapshot.child("shortcode").getValue().toString());
                                } catch (NullPointerException ignored){}
                                return instagramPojo;
                            }
                        })
                .build();

        firebaseRecyclerAdapter=new FirebaseRecyclerAdapter<InstagramAdapter.InstagramPojo,
                InstagramAdapter.InstagramViewHolder>(options) {
            @Override
            public InstagramAdapter.InstagramViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                View view=LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.instagramcard, parent, false);
                return new InstagramAdapter.InstagramViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(InstagramAdapter.InstagramViewHolder holder, int position,
                                            InstagramAdapter.InstagramPojo model) {

                holder.setTimeposted(model.getCreated_time());
                holder.setPostmessage(model.getMessage());
                holder.setProfilethumbnail(DatabaseHolder.getInstagramprofilepicurl(), getParentFragment());
                holder.setPostthumbnail(model.getFull_picture(), getParentFragment());
                holder.setUrl(model.getLink());
            }

            @Override
            public void onDataChanged() {
                super.onDataChanged();
            }
        };

        instagramrecycler.setAdapter(firebaseRecyclerAdapter);
        instagramrecycler.setLayoutManager(new LinearLayoutManager(getContext()));
    }

    private String getDateAndTime(long unix_s) {
        //convert seconds to milliseconds
        Date date = new Date(unix_s *1000L);
        // format of the date
        SimpleDateFormat jdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
        jdf.setTimeZone(TimeZone.getDefault());
        return jdf.format(date);
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
