package io.ejf.roastagram.feed;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import org.w3c.dom.Text;

import io.ejf.roastagram.R;
import io.ejf.roastagram.data.Post;

/**
 * Created by ejf3 on 4/30/16.
 */
public class PostView extends RecyclerView.ViewHolder {
    private final Context context;
    private ImageView postImage;
    private ImageButton beanLikeButton;
    private ImageButton profilePicture;
    private TextView likes;
    private TextView username;

    public PostView(Context context, View itemView) {
        super(itemView);
        this.context = context;
        postImage = (ImageView) itemView.findViewById(R.id.post_image);
        beanLikeButton = (ImageButton) itemView.findViewById(R.id.post_beanlike_button);
        likes = (TextView) itemView.findViewById(R.id.post_likes);
        profilePicture = (ImageButton) itemView.findViewById(R.id.post_profile_picture);
        username = (TextView) itemView.findViewById(R.id.post_username);
    }

    public void update(Post post) {
        postImage.setImageURI(post.getUri(context));
        profilePicture.setImageURI(post.getProfileUri(context));
        username.setText(post.getUser());
        likes.setText(String.valueOf(post.getLikes()));

        beanLikeButton.setOnClickListener(new Post.Liked(post));

    }

}
