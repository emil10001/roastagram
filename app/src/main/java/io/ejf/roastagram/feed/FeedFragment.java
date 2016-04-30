package io.ejf.roastagram.feed;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import io.ejf.roastagram.R;
import io.ejf.roastagram.data.Post;

/**
 * A placeholder fragment containing a simple view.
 */
public class FeedFragment extends Fragment {
    private RecyclerView mRecyclerView;
    private FeedAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    public FeedFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_main, container, false);
        mRecyclerView = (RecyclerView) v.findViewById(R.id.feed_view);
        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);

        // specify an adapter (see also next example)
        mAdapter = new FeedAdapter(getActivity());
        mRecyclerView.setAdapter(mAdapter);

        Post.tell(mAdapter);

        return v;
    }


    @Override
    public void onDestroy(){
        Post.forget(mAdapter);

        super.onDestroy();
    }
}
