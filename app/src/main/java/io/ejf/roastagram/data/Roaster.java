package io.ejf.roastagram.data;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import com.firebase.client.DataSnapshot;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import io.ejf.roastagram.feed.FeedAdapter;
import io.ejf.roastagram.utils.FileHelper;

/**
 * Created by ejf3 on 4/30/16.
 */
public class Roaster {
    private static final String TAG = Roaster.class.getSimpleName();
    private static final Map<String, Roaster> roasters = new ConcurrentHashMap<>();
    private static final Set<String> profiles = new HashSet<>();
    private String name;
    private String photo;

    public Roaster() {}

    public static Map<String, Roaster> getRoasters() {
        return roasters;
    }

    public Roaster(String name, String photo) {
        this.name = name;
        this.photo = photo;
    }

    public String getName() {
        return name;
    }

    public String getPhoto() {
        return photo;
    }


    public Uri getUri(Context c) {
        try {
            return FileHelper.getUri(c, getPhoto());
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void downloadImage(Context c) {
        try {
            FileHelper.download(c, getPhoto(), new Downloaded(this));
        } catch (Exception e) {
            Log.w(TAG, "couldn't download the file", e);
        }
    }

    @Override
    public String toString() {
        return "Roaster{" +
                "name='" + name + '\'' +
                ", photo='" + photo + '\'' +
                '}';
    }

    public static class Downloaded implements Runnable{
        private final Roaster roaster;

        public Downloaded(Roaster roaster) {
            this.roaster = roaster;
        }

        @Override
        public void run() {
            profiles.add(roaster.getPhoto());
            Post.updateAdapters();
        }
    }

    public static class FireBaseEvent implements ValueEventListener {
        private final Context context;

        public FireBaseEvent(Context context) {
            this.context = context;
        }

        @Override
        public void onDataChange(DataSnapshot snapshot) {
            for (DataSnapshot roasterSnapshot: snapshot.getChildren()) {
                Roaster roaster = roasterSnapshot.getValue(Roaster.class);
                if (!profiles.contains(roaster.getPhoto()))
                    roaster.downloadImage(context);

                roasters.put(roaster.getName(), roaster);
                Log.d(TAG, roaster.toString());
            }
        }

        @Override public void onCancelled(FirebaseError error) { }
    }
}
