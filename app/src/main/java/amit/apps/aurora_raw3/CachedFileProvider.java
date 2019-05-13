package amit.apps.aurora_raw3;
import java.io.File;
import java.io.FileNotFoundException;
import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;
import android.os.ParcelFileDescriptor;
import android.support.annotation.NonNull;
import android.util.Log;

public class CachedFileProvider extends ContentProvider {

    // The authority is the symbolic name for the provider class
    public static final String AUTHORITY = "amit.apps.aurora_raw3";

    // UriMatcher used to match against incoming requests<br />
    private UriMatcher uriMatcher;
    @Override
    public boolean onCreate() {
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

        // Add a URI to the matcher which will match against the form
        // 'content://com.stephendnicholas.gmailattach.provider/*'
        // and return 1 in the case that the incoming Uri matches this pattern
        uriMatcher.addURI(AUTHORITY, "*", 1);

        return true;
    }


    @Override
    public ParcelFileDescriptor openFile(@NonNull Uri uri, @NonNull String mode) throws FileNotFoundException {

        Log.e("in content provider", "Called with uri: '" + uri + "'." + uri.getLastPathSegment());

        File cacheDir = getContext().getCacheDir();
        File privateFile = new File(cacheDir, "playlistobject.pt");

        return ParcelFileDescriptor.open(privateFile, ParcelFileDescriptor.MODE_READ_ONLY);
    }

    @Override
    public int update(@NonNull Uri uri, ContentValues contentvalues, String s, String[] as) { return 0; }

    @Override
    public int delete(@NonNull Uri uri, String s, String[] as) { return 0; }

    @Override
    public Uri insert(@NonNull Uri uri, ContentValues contentvalues) { return null; }

    @Override
    public String getType(@NonNull Uri uri) { return null; }

    @Override
    public Cursor query(@NonNull Uri uri, String[] projection, String s, String[] as1, String s1) { return null; }
}