package amit.apps.aurora_raw3.adapters;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import amit.apps.aurora_raw3.R;

public class TicketAdapter {

    public static class TicketViewHolder extends RecyclerView.ViewHolder {

        TextView date, fest, location;
        Button url;
        ImageButton rsvp;

        public TicketViewHolder(View itemView) {
            super(itemView);

            date=itemView.findViewById(R.id.ticketdate);
            fest=itemView.findViewById(R.id.ticketfest);
            location=itemView.findViewById(R.id.ticketlocation);
            url=itemView.findViewById(R.id.ticketurl);
            rsvp=itemView.findViewById(R.id.ticketrsvp);
        }

        public void setDate(String d) {
            date.setText(d);
        }

        public void setFest(String f) {
            fest.setText(f);
        }

        public void setLocation(String l) {
            location.setText(l);
        }

        public void setRsvp(final String r) {

            if(r.compareTo("NA")==0) {
                //Log.e("rsvp :", "setting button invisible");
                rsvp.setVisibility(View.INVISIBLE);
            } else {
                //Log.e("rsvp :", "setting button visible");
                rsvp.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Uri uri = Uri.parse(r);
                        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                        rsvp.getContext().startActivity(intent);
                    }
                });
                rsvp.setVisibility(View.VISIBLE);
            }
        }

        public void setUrl(final String u) {
            if(u.compareTo("NA")==0) {
                //Log.e("rsvp :", "setting button invisible");
                url.setVisibility(View.INVISIBLE);
            } else {
                url.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Uri uri = Uri.parse(u);
                        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                        url.getContext().startActivity(intent);
                    }
                });
                url.setVisibility(View.VISIBLE);
            }
        }
    }

    public static class TicketPojo {
        public String date, fest, location, rsvp, url;

        public TicketPojo(){}

        public TicketPojo(String d, String f, String l, String r, String u) {
            date=d;
            fest=f;
            location=l;
            rsvp=r;
            url=u;
        }

        public String getDate() {
            return date;
        }

        public void setDate(String d) {
            date=d;
        }

        public String getFest() {
            return fest;
        }

        public void setFest(String f) {
            fest=f;
        }

        public String getLocation() {
            return location;
        }

        public void setLocation(String location) {
            this.location = location;
        }

        public String getRsvp() {
            return rsvp;
        }

        public void setRsvp(String rsvp) {
            this.rsvp = rsvp;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }
    }
}