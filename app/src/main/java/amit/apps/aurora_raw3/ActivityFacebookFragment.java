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

import amit.apps.aurora_raw3.adapters.FacebookAdapter;
import amit.apps.aurora_raw3.datainitializers.DatabaseHolder;
import amit.apps.aurora_raw3.notifications.MyFirebaseMessagingService;

public class ActivityFacebookFragment extends Fragment {
    RecyclerView facebookrecycler;
    FirebaseRecyclerAdapter firebaseRecyclerAdapter;

    public ActivityFacebookFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View layout=inflater.inflate(R.layout.fragment_activity_facebook, container, false);
        setHasOptionsMenu(true);

        MyFirebaseMessagingService.unseennotifications.remove("Facebook");

        facebookrecycler=layout.findViewById(R.id.facebookrecycler);

        populateRecycler();
        return layout;
    }

    private void populateRecycler() {
        FirebaseRecyclerOptions<FacebookAdapter.FacebookPojo> options=new FirebaseRecyclerOptions.
                Builder<FacebookAdapter.FacebookPojo>()
                .setQuery(DatabaseHolder.facebookref, FacebookAdapter.FacebookPojo.class)
                .build();

        firebaseRecyclerAdapter=new FirebaseRecyclerAdapter<FacebookAdapter.FacebookPojo,
                FacebookAdapter.FacebookViewHolder>(options) {
            @Override
            public FacebookAdapter.FacebookViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                View view=LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.facebookcard, parent, false);
                return new FacebookAdapter.FacebookViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(FacebookAdapter.FacebookViewHolder holder, int position,
                                            FacebookAdapter.FacebookPojo model) {

                holder.setTimeposted(model.getCreated_time());
                holder.setPostmessage(model.getMessage());
                holder.setProfilethumbnail(DatabaseHolder.getFacebookprofilepicurl(), getContext());
                holder.setPostthumbnail(model.getFull_picture(), getContext());
                holder.setUrl(model.getLink());
            }

            @Override
            public void onDataChanged() {
                super.onDataChanged();
            }
        };

        facebookrecycler.setAdapter(firebaseRecyclerAdapter);
        facebookrecycler.setLayoutManager(new LinearLayoutManager(getContext()));
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
