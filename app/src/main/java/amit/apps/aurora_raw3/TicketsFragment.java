package amit.apps.aurora_raw3;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;

import amit.apps.aurora_raw3.adapters.TicketAdapter;
import amit.apps.aurora_raw3.datainitializers.DatabaseHolder;
import amit.apps.aurora_raw3.notifications.MyFirebaseMessagingService;

public class TicketsFragment extends Fragment {
    RecyclerView ticketrecycler;
    FirebaseRecyclerAdapter firebaseRecyclerAdapter;

    public TicketsFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.fragment_ticket, container, false);
        setHasOptionsMenu(true);

        MainActivity.fragmenttoload=3;
        MyFirebaseMessagingService.unseennotifications.remove("Tickets");
        getActivity().invalidateOptionsMenu();

        ticketrecycler=layout.findViewById(R.id.ticketrecycler);

        populateRecycler();
        return layout;
    }

    private void populateRecycler() {
        FirebaseRecyclerOptions<TicketAdapter.TicketPojo> options=new FirebaseRecyclerOptions.
                Builder<TicketAdapter.TicketPojo>()
                .setQuery(DatabaseHolder.ticketsref, TicketAdapter.TicketPojo.class)
                .build();

        firebaseRecyclerAdapter=new FirebaseRecyclerAdapter<TicketAdapter.TicketPojo,
                TicketAdapter.TicketViewHolder>(options) {
            @Override
            public TicketAdapter.TicketViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                View view=LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.ticketcard, parent, false);
                return new TicketAdapter.TicketViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(TicketAdapter.TicketViewHolder holder, int position,
                                            TicketAdapter.TicketPojo model) {
                holder.setDate(model.getDate());
                holder.setFest(model.getFest());
                holder.setLocation(model.getLocation());
                holder.setRsvp(model.getRsvp());
                holder.setUrl(model.getUrl());
            }

            @Override
            public void onDataChanged() {
                super.onDataChanged();
            }
        };

        ticketrecycler.setAdapter(firebaseRecyclerAdapter);
        ticketrecycler.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
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
