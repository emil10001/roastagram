package io.ejf.roastagram.utils;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by ejf3 on 4/30/16.
 */
public class FileHelper {
    private static final String TAG = FileHelper.class.getSimpleName();
    private static final int BUFFER_SIZE = 4096;

    public static void download(Context c, String src, Runnable onFinish) throws NoSuchAlgorithmException, UnsupportedEncodingException {
        Uri filename = getUri(c, src);
        Log.d(TAG, "downloading to: " + filename);
        new Downloader(onFinish).execute(src, filename.toString());
    }

    public static File getCacheDir(Context c){
        File cacheDir = c.getExternalCacheDir();

        if (null == cacheDir)
            cacheDir = c.getCacheDir();

        if (!cacheDir.exists())
            cacheDir.mkdirs();

        return cacheDir;
    }


    public static String write(String filename, byte[] data) {
        File pictureFile = new File(filename);

        try {
            FileOutputStream fos = new FileOutputStream(pictureFile);
            fos.write(data);
            fos.close();
            Log.d(TAG, " New Image saved:" + filename);
        } catch (Exception error) {
            Log.d(TAG, " File" + filename + "not saved: ", error);
        }
        return filename;
    }

    public static void copy(String src, String dst) throws IOException {
        InputStream in = new FileInputStream(src);
        OutputStream out = new FileOutputStream(dst);

        // Transfer bytes from in to out
        byte[] buf = new byte[1024];
        int len;
        while ((len = in.read(buf)) > 0) {
            out.write(buf, 0, len);
        }
        in.close();
        out.close();
    }

    public static String hash(String src) throws NoSuchAlgorithmException, UnsupportedEncodingException {
        // credit - https://dzone.com/articles/get-md5-hash-few-lines-java
        MessageDigest md = MessageDigest.getInstance("MD5");
        md.update(src.getBytes("UTF-8"),0,src.length());
        return new BigInteger(1,md.digest()).toString(16);
    }

    public static Uri getUri(Context c, String src) throws UnsupportedEncodingException, NoSuchAlgorithmException {
        String hash = FileHelper.hash(src);
        File filePath = getCacheDir(c);
        return Uri.parse(filePath.getPath() + File.separator + hash);
    }

    private static class Downloader extends AsyncTask<String, Void, Void> {
        private final Runnable onFinish;

        private Downloader(Runnable onFinish) {
            this.onFinish = onFinish;
        }

        @Override
        protected Void doInBackground(String... params) {
            String src = params[0];
            File destFile = new File(params[1]);
            Log.d(TAG, "downloading src: " + src + "\ndest: " + params[1]);

            if (destFile.exists())
                return null;

            URL url = null;
            try {
                url = new URL(src);
            } catch (MalformedURLException e) {
                Log.w(TAG, "couldn't get url", e);
            }
            HttpURLConnection urlConnection = null;
            try {
                urlConnection = (HttpURLConnection) url.openConnection();
                InputStream is = new BufferedInputStream(urlConnection.getInputStream());
                FileOutputStream fos = new FileOutputStream(destFile);

                int bytesRead = -1;
                byte[] buffer = new byte[BUFFER_SIZE];
                while ((bytesRead = is.read(buffer)) != -1)
                    fos.write(buffer, 0, bytesRead);

                fos.close();
                is.close();
            } catch (Exception e) {
                Log.w(TAG, "failed to download", e);
            } finally {
                urlConnection.disconnect();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void param) {
            onFinish.run();
        }
    }
}
