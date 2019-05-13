package amit.apps.aurora_raw3.adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import amit.apps.aurora_raw3.R;

public class FacebookAdapter {

    public static class FacebookViewHolder extends RecyclerView.ViewHolder {

        ImageView profilethumbnail, postthumbnail;
        TextView timeposted, postmessage;

        public FacebookViewHolder(View itemView) {
            super(itemView);

            profilethumbnail=itemView.findViewById(R.id.profilethumbnail);
            postthumbnail=itemView.findViewById(R.id.postthumbnail);
            timeposted=itemView.findViewById(R.id.timeposted);
            postmessage=itemView.findViewById(R.id.postmessage);
        }

        public void setTimeposted(String t) {
            timeposted.setText(t);
        }

        public void setPostmessage(String p) {
            postmessage.setText(p);
        }

        public void setProfilethumbnail(String url, Context context) {
            Glide.with(context).load(url).apply(RequestOptions.circleCropTransform().placeholder(context.getResources().getDrawable(R.drawable.user))).into(profilethumbnail);
        }

        public void setPostthumbnail(String url, Context context) {
            if(url.compareTo("NA")!=0)
                Glide.with(context).load(url).into(postthumbnail);
        }

        public void setUrl(final String u) {
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Uri uri = Uri.parse(u);
                    Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                    itemView.getContext().startActivity(intent);
                }
            });
        }
    }

    public static class FacebookPojo {
        public String created_time, full_picture, link, message;

        public FacebookPojo(){}

        public FacebookPojo(String c, String f, String l, String m) {
            created_time=c;
            full_picture=f;
            link=l;
            message=m;
        }

        public String getCreated_time() {
            return created_time;
        }

        public void setCreated_time(String created_time) {
            this.created_time = created_time;
        }

        public String getFull_picture() {
            return full_picture;
        }

        public void setFull_picture(String full_picture) {
            this.full_picture = full_picture;
        }

        public String getLink() {
            return link;
        }

        public void setLink(String link) {
            this.link = link;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }
    }
}