package io.ejf.roastagram.feed;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import io.ejf.roastagram.R;
import io.ejf.roastagram.data.Post;

/**
 * Created by ejf3 on 4/29/16.
 */
public class FeedAdapter extends RecyclerView.Adapter<PostView> {
    private final Context context;
    private List<Post> posts = new ArrayList<>();

    public FeedAdapter(Context context) {
        this.context = context;
        this.posts.clear();
        this.posts.addAll(Post.getPosts());
    }

    public void swap(){
        this.posts.clear();
        this.posts.addAll(Post.getPosts());
        notifyDataSetChanged();
    }

    public void swap(Post post){
        int i = posts.indexOf(post);
        notifyItemChanged(i);
    }

    @Override
    public PostView onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.feed_item, parent, false);

        PostView vh = new PostView(context, v);

        return vh;
    }

    @Override
    public void onBindViewHolder(PostView holder, int position) {
        holder.update(posts.get(position));
    }

    @Override
    public int getItemCount() {
        return posts.size();
    }
}
