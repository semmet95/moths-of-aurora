package amit.apps.aurora_raw3.datainitializers;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONArray;
import org.json.JSONException;

public class DatabaseHolder {
    private static FirebaseDatabase firebaseDatabase;
    public static DatabaseReference ticketsref, facebookref, facebookprofilepic, instagramref, instagramprofilepic,
            twitterref, twitterprofilepic, videosref, songsref;
    private static String fbpicurl, instapicurl, twitterpicurl;
    private static JSONArray songsarray;

    static {
        firebaseDatabase=FirebaseDatabase.getInstance();
        firebaseDatabase.setPersistenceEnabled(true);

        ticketsref= firebaseDatabase.getReference().child("tickets");

        facebookref= firebaseDatabase.getReference().child("facebook/data");
        facebookprofilepic=firebaseDatabase.getReference().child("facebook/url");

        instagramref=firebaseDatabase.getReference().child("instagram/data");
        instagramprofilepic=firebaseDatabase.getReference().child("instagram/url");

        twitterref=firebaseDatabase.getReference().child("twitter");
        twitterprofilepic=firebaseDatabase.getReference().child("twitter/0/user/profile_image_url_https");

        videosref=firebaseDatabase.getReference().child("youtube");

        songsref=firebaseDatabase.getReference().child("songs");
    }

    public static String getFacebookprofilepicurl() {
        facebookprofilepic.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                fbpicurl=(String)dataSnapshot.getValue();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });
        return fbpicurl;
    }

    public static String getInstagramprofilepicurl() {
        instagramprofilepic.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                instapicurl=(String)dataSnapshot.getValue();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });
        return instapicurl;
    }

    public static String getTwitterprofilepicurl() {
        twitterprofilepic.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                twitterpicurl=dataSnapshot.getValue().toString();
                twitterpicurl=twitterpicurl.replace("normal", "reasonably_small");
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

        return twitterpicurl;
    }

    public static void initializesongsarray() {
        songsref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                try {
                    songsarray=new JSONArray(dataSnapshot.getValue().toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });

    }
}