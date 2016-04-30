package io.ejf.roastagram.data;

import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import com.firebase.client.DataSnapshot;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import io.ejf.roastagram.feed.FeedAdapter;
import io.ejf.roastagram.utils.FileHelper;

/**
 * Created by ejf3 on 4/30/16.
 */
public class Post {
    private static final String TAG = Post.class.getSimpleName();
    private static final List<Post> posts = new ArrayList<>();
    private static final Set<String> postImages = new HashSet<>();
    private static final Set<FeedAdapter> adapters = new HashSet<>();

    private String user;
    private String src;
    private int likes;

    public static List<Post> getPosts() {
        List<Post> tmpPosts = new ArrayList<>();
        for (Post post : posts)
            tmpPosts.add(post);
        return tmpPosts;
    }

    public Post() {}

    public Post(String user, String src, int likes) {
        this.user = user;
        this.src = src;
        this.likes = likes;
    }

    public String getUser() {
        return user;
    }

    public String getSrc() {
        return src;
    }

    public int getLikes() {
        return likes;
    }

    private void downloadImage(Context c) {
        try {
            FileHelper.download(c, getSrc(), new Downloaded(this));
        } catch (Exception e) {
            Log.w(TAG, "couldn't download the file", e);
        }
    }

    public Uri getUri(Context c) {
        try {
            return FileHelper.getUri(c, getSrc());
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Uri getProfileUri(Context c) {
        return Roaster.getRoasters().get(getUser()).getUri(c);
    }

    @Override
    public String toString() {
        return "Post{" +
                "user='" + user + '\'' +
                ", src='" + src + '\'' +
                ", likes=" + likes +
                '}';
    }

    public static void tell(FeedAdapter adapter) {
        adapters.add(adapter);
    }

    public static void forget(FeedAdapter adapter) {
        adapters.remove(adapter);
    }

    public static class Downloaded implements Runnable{
        private final Post post;

        public Downloaded(Post post) {
            this.post = post;
        }

        @Override
        public void run() {
            postImages.add(post.getSrc());
            post.updateAdaptersSingle();
        }

    }

    private void updateAdaptersSingle() {
        for (FeedAdapter adapter : adapters)
            adapter.swap(this);
    }

    public static class FireBaseEvent implements ValueEventListener {
        private final Context context;

        public FireBaseEvent(Context context) {
            this.context = context;
        }

        @Override
        public void onDataChange(DataSnapshot snapshot) {
            Set<String> images = new HashSet<>();
            posts.clear();
            for (DataSnapshot postSnapshot: snapshot.getChildren()) {
                Post post = postSnapshot.getValue(Post.class);
                if (!postImages.contains(post.getSrc()))
                    post.downloadImage(context);

                posts.add(post);
                Log.d(TAG, post.toString());
            }
            updateAdapters();
        }
        @Override public void onCancelled(FirebaseError error) { }

    }

    static void updateAdapters() {
        for (FeedAdapter adapter : adapters)
            adapter.swap();
    }

    public static class Liked implements View.OnClickListener {
        private final Post post;

        public Liked(Post post) {
            this.post = post;
        }

        @Override
        public void onClick(View v) {
            post.likes++;
            post.updateAdaptersSingle();
        }
    }
}
