package com.ducky.kurokobasketball.common;

import android.annotation.SuppressLint;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.util.Log;

import java.util.Objects;

public class DownloadFileReceiver extends BroadcastReceiver {

    private final DownloadManager mDownloadManager;
    private final DownloadCallback mCallback;
    private final long id;

    public DownloadFileReceiver(DownloadManager dManager, DownloadCallback callback, long id) {
        mDownloadManager = dManager;
        mCallback = callback;
        this.id = id;
    }

    /**
     * Override BroadcastReceiver Methods
     **/
    @SuppressLint("Range")
    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (DownloadManager.ACTION_DOWNLOAD_COMPLETE.equals(action)) {
            DownloadManager.Query q = new DownloadManager.Query();
            q.setFilterById(id);
            Cursor c = mDownloadManager.query(q);
            if (c.moveToFirst()) {
                int status = c.getInt(c.getColumnIndex(DownloadManager.COLUMN_STATUS));
                if (status == DownloadManager.STATUS_SUCCESSFUL) {
                    String fullPath;
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        fullPath = c.getString(c.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI));
                    } else {
                        fullPath = c.getString(c.getColumnIndex(DownloadManager.COLUMN_LOCAL_FILENAME));
                    }
                    mCallback.success(FileUtils.getPath(context, Uri.parse(fullPath)));
                    Objects.requireNonNull(context).unregisterReceiver(this);
                } else if (status == DownloadManager.STATUS_FAILED) {
                    Log.d("duc.dv1", "onReceive: ");
                    mCallback.failure();
                    Objects.requireNonNull(context).unregisterReceiver(this);
                }
            }
            c.close();
        }
    }
}
