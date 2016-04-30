package io.ejf.roastagram;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.firebase.client.Firebase;

import io.ejf.roastagram.data.Post;
import io.ejf.roastagram.data.Roaster;

/**
 * Created by ejf3 on 4/30/16.
 */
public class RoastService extends Service {
    private static final String TAG = RoastService.class.getSimpleName();
    private static Firebase myFirebaseRef;

    public static void init(Context context) {
        if (null != myFirebaseRef)
            return;

        Log.i(TAG, "roastservice starting");
        Firebase.setAndroidContext(context);
        myFirebaseRef = new Firebase("https://roastagram.firebaseio.com/all/-KGd8rDkV44TlM0HDR6x");
        Log.d(TAG, "myFirebaseRef: " + myFirebaseRef.child("roasters").toString());
    }

    @Override
    public void onCreate(){
        super.onCreate();
        init(getApplicationContext());
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId){
        myFirebaseRef.child("roasters").addValueEventListener(new Roaster.FireBaseEvent(getApplicationContext()));
        myFirebaseRef.child("posts").addValueEventListener(new Post.FireBaseEvent(getApplicationContext()));
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        myFirebaseRef = null;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

}
