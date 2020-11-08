package com.ducky.kurokobasketball.utils.support;

import android.app.Activity;
import android.app.ProgressDialog;
import android.app.WallpaperManager;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.annotation.RequiresApi;

import com.ducky.kurokobasketball.R;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.Objects;

import static android.app.WallpaperManager.FLAG_LOCK;
import static android.app.WallpaperManager.FLAG_SYSTEM;
import static android.app.WallpaperManager.getInstance;
import static android.preference.PreferenceManager.getDefaultSharedPreferences;

public class WindowUtils {

    private static final String TAG = "WindowUtils";

    private static boolean mSystemUIShowed = true;
    private static ProgressDialog progressDialog;

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    private static void hideSystemUI(Activity activity) {
        Log.d(TAG, "hideSystemUI: ");
        mSystemUIShowed = false;
        // Enables regular immersive mode.
        // For "lean back" mode, remove SYSTEM_UI_FLAG_IMMERSIVE.
        // Or for "sticky immersive," replace it with SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        View decorView = activity.getWindow().getDecorView();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            decorView.setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_IMMERSIVE
                            // Set the content to appear under the system bars so that the
                            // content doesn't resize when the system bars hide and show.
                            | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            // Hide the nav bar and status bar
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_FULLSCREEN);
        }
    }

    // Shows the system bars by removing all the flags
// except for the ones that make the content appear under the system bars.
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    private static void showSystemUI(Activity activity) {
        mSystemUIShowed = true;
        Log.d(TAG, "showSystemUI: ");
        View decorView = activity.getWindow().getDecorView();
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
    }

    public static void setWallPaperFitScreen(Context context, final String file) {
        SharedPreferences prefs = getDefaultSharedPreferences(context);
        String type = prefs.getString(Constants.SET_WALLPAPER_OPTION, "");
        new SetWallPaper(new WeakReference<>(context)).execute(file, type);
    }

    static class SetWallPaper extends AsyncTask<String, Void, Void> {
        private WeakReference<Context> context;
        private String flagS = "";

        SetWallPaper(WeakReference<Context> context) {
            this.context = context;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }


        @RequiresApi(api = Build.VERSION_CODES.N)
        @Override
        protected Void doInBackground(String... strings) {
            String file = strings[0];
            flagS = strings[1];
            int flag;
            if (flagS.equals(Constants.HOME_SCREEN)) {
                flag = FLAG_SYSTEM;
            } else if (flagS.equals(Constants.LOCK_SCREEN)) {
                flag = FLAG_LOCK;
            } else flag = FLAG_LOCK | FLAG_SYSTEM;
            Log.d(TAG, "Set file: " + file);
            WallpaperManager manager = getInstance(context.get());
            try {
                File file1 = new File(file);
                if (!file1.exists()) {
                    Log.d(TAG, "doInBackground: File not found");
                    Toast.makeText(context.get(), "Image not found", Toast.LENGTH_SHORT).show();
                    return null;
                }
                DisplayMetrics metrics = new DisplayMetrics();
                WindowManager wm = (WindowManager) context.get().getSystemService(Context.WINDOW_SERVICE);
                assert wm != null;
                wm.getDefaultDisplay().getMetrics(metrics);
                int height = metrics.heightPixels;
                int width = metrics.widthPixels;
                if (width > height) {
                    int temp = width;
                    width = height;
                    height = temp;
                }
//                Bitmap bitmap = ImageUtils.scaleCenterCrop(file, height, width);
                Bitmap bitmap = ImageUtils.decodeBitmapFromFile(file, width, height);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    manager.setBitmap(bitmap, null, false, flag);
                } else {
                    manager.setBitmap(bitmap);
                }
            } catch (Exception e) {
                Log.e(TAG, "setWallPaperFitScreen: " + e.toString());
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            String message = context.get().getString(R.string.set_wallpaper_done, flagS);
            Toast.makeText(context.get(), message, Toast.LENGTH_LONG).show();
            if(progressDialog!=null){
                progressDialog.dismiss();
            }
            if (context.get() instanceof Activity) {
                ((Activity) context.get()).finish();
            }
            super.onPostExecute(aVoid);
        }
    }

    public static void shareApp(Context context) {
        final String appPackageName = context.getPackageName();
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, "Check out the App at: https://play.google.com/store/apps/details?id=" + appPackageName);
        sendIntent.setType("text/plain");
        context.startActivity(sendIntent);
    }

    public static void rateApp(Context context) {
        Uri uri = Uri.parse("market://details?id=" + Objects.requireNonNull(context).getPackageName());
        Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            goToMarket.addFlags(
                    Intent.FLAG_ACTIVITY_NO_HISTORY |
                            Intent.FLAG_ACTIVITY_NEW_DOCUMENT |
                            Intent.FLAG_ACTIVITY_MULTIPLE_TASK
            );
        }
        try {
            context.startActivity(goToMarket);
        } catch (ActivityNotFoundException e) {
            context.startActivity(
                    new Intent(
                            Intent.ACTION_VIEW,
                            Uri.parse("http://play.google.com/store/apps/details?id=" + context.getPackageName())
                    )
            );
        }
    }


    public static void setProgressDialog(Context context) {
        progressDialog = new ProgressDialog(context, R.style.CircleProgressStyle);
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Objects.requireNonNull(progressDialog.getWindow()).setGravity(Gravity.CENTER);
        }
        progressDialog.setIndeterminate(true);
        progressDialog.show();
    }

}
