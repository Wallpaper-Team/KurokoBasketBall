package com.ducky.kurokobasketball.common;

import android.app.DownloadManager;
import android.content.Context;
import android.content.IntentFilter;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Environment;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.ducky.kurokobasketball.utils.support.Constants;

import java.util.Locale;

public class Utils {

    public static String[] IMAGE_EXTENSIONS = {".gif", ".png", ".jpg"};

    public static long downloadThroughManager(String albumName, String imageUrl, Context context, DownloadCallback callback) {
        final DownloadManager downloadManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
        Uri uri = Uri.parse(imageUrl);
        DownloadManager.Request request = new DownloadManager.Request(uri);
        request.setTitle("Download image");
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE);
        request.allowScanningByMediaScanner();
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS,
                Constants.APP_OWNER_FOLDER_NAME + "/" + albumName + "/" + getImageName(imageUrl));
        IntentFilter filter = new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE);
        long id = downloadManager.enqueue(request);
        context.registerReceiver(new DownloadFileReceiver(downloadManager, callback, id), filter);
        return id;
    }

    private static String getImageName(String imageUrl) {
        imageUrl = imageUrl.substring(0, imageUrl.indexOf(getImageExtension(imageUrl)) + 4);
        return imageUrl.substring(imageUrl.lastIndexOf("/") + 1);
    }

    private static String getImageExtension(String imageUrl) {
        for (String ex : IMAGE_EXTENSIONS) {
            if (imageUrl.contains(ex) || imageUrl.contains(ex = ex.toUpperCase(Locale.ROOT))) {
                return ex;
            }
        }
        return null;
    }
}
