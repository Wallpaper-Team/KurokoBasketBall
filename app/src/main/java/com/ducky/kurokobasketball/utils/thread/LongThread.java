package com.ducky.kurokobasketball.utils.thread;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.ducky.kurokobasketball.utils.support.Constants;
import com.ducky.kurokobasketball.utils.support.FileUtils;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class LongThread implements Runnable {
    public static final String TAG = "LongThread";
    private Handler handler;
    private String imageUrl, albumName;

    public LongThread() {
    }

    public LongThread(String imageUrl, String albumName, Handler handler) {
        this.handler = handler;
        this.imageUrl = imageUrl;
        this.albumName = albumName;
    }

    @Override
    public void run() {
        String outputPath = downloadWPP();
        sendMessage(1,outputPath);
    }


    private void sendMessage(int what, String msg) {
        Message message = handler.obtainMessage(what, msg);
        message.sendToTarget();
    }

    private String downloadWPP() {
        String fileOutputPath = null;
        HttpURLConnection connection = null;
        try {
            URL url = new URL(imageUrl);
            connection = (HttpURLConnection) url.openConnection();
            connection.connect();
            InputStream inputStream = connection.getInputStream();
            BufferedInputStream bufferedInputStream = new BufferedInputStream(inputStream);
            Bitmap bmp = BitmapFactory.decodeStream(bufferedInputStream);
            // Return the downloaded bitmap
            if (bmp != null) {
                fileOutputPath = saveImageToInternalStorage(albumName, albumName + "_" + System.currentTimeMillis() + ".jpg", bmp);
            }
            if (fileOutputPath != null) {
                return fileOutputPath;
            }
        } catch (Exception e) {
            Log.d(TAG, "doInBackground: " + e.getMessage());
        } finally {
            // Disconnect the http url connection
            assert connection != null;
            connection.disconnect();
        }
        return null;
    }

    private String saveImageToInternalStorage(String albumName, String fileName, Bitmap bitmap) {
        File file = createFileImageInternalStorage(albumName, fileName);
        try {
            OutputStream stream = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
            stream.flush();
            stream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        // Parse the gallery image url to uri
        Log.d(TAG, "saveImageToInternalStorage: " + file.getAbsolutePath());
        // Return the saved image Uri
        return file.getAbsolutePath();
    }

    private File createFileImageInternalStorage(String albumName, String fileName) {
        File albums = new File(FileUtils.getAppOwnDirectory() + File.separator + Constants.ALBUMS);
        File album = new File(albums, albumName);
        if (!album.exists()) {
            if (album.mkdirs()) {
                Log.d(TAG, "create folder " + albumName + " successful");
            }
        }
        return new File(album, fileName);
    }

}