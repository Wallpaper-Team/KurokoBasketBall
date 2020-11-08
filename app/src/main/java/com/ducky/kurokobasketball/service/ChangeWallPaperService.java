package com.ducky.kurokobasketball.service;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PixelFormat;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ducky.kurokobasketball.R;
import com.ducky.kurokobasketball.database.AlbumsDatabase;
import com.ducky.kurokobasketball.model.Album;
import com.ducky.kurokobasketball.model.Image;
import com.ducky.kurokobasketball.model.State;
import com.ducky.kurokobasketball.ui.MainActivity;
import com.ducky.kurokobasketball.utils.callback.OnSwipeTouchListener;
import com.ducky.kurokobasketball.utils.support.Constants;
import com.ducky.kurokobasketball.utils.support.NumberUtils;
import com.ducky.kurokobasketball.utils.support.TimeUtil;
import com.ducky.kurokobasketball.utils.support.WindowUtils;

import java.util.ArrayList;


public class ChangeWallPaperService extends Service implements View.OnClickListener {

    private static final String TAG = "ChangeWallPaperService";
    private static ArrayList<Image> images;
    private static int currentPosition;
    private AlarmManager alarmManager;
    private Intent intent;
    private PendingIntent pIntent;
    private NotificationCompat.Builder builder;
    private AlbumsDatabase database;
    private static Album album;

    // The following are used for the shake detection
    private SensorManager mSensorManager;
    private ShakeDetector mShakeDetector;
    private WindowManager windowManager;
    private Button btnHintOverlay;
    private LinearLayout contentOverlay;

    private OverlayAdapter.ItemClickListener listener = new OverlayAdapter.ItemClickListener() {
        @Override
        public void onClick(int currentItem) {
            currentPosition = currentItem;
            setUpWallpaper();
        }
    };
    private FrameLayout viewGroup;
    private RecyclerView rvOverlay;

    public ChangeWallPaperService() {
        super();
    }

    public static Album getAlbum() {
        return album;
    }

    @Override
    public void onCreate() {
        Log.d(TAG, "onCreate: ");
        super.onCreate();
        // ShakeDetector initialization
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        assert mSensorManager != null;
        Sensor mAccelerometer = mSensorManager
                .getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mShakeDetector = new ShakeDetector();
        mShakeDetector.setOnShakeListener(count -> handleShakeEvent());
        mSensorManager.registerListener(mShakeDetector, mAccelerometer, SensorManager.SENSOR_DELAY_UI);
        createOverlayLayout();
    }


    private void handleShakeEvent() {
        Log.d(TAG, "onShake: ");
        if (images == null) {
            return;
        }
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        boolean enableAutochange = prefs.getBoolean(Constants.PREF_AUTO_CHANGE_SWITCH_KEY, true);
        boolean enableShakeChange = prefs.getBoolean(Constants.PREF_ALLOW_SHAKE_SWITCH_KEY, true);
        if (!enableAutochange || !enableShakeChange) {
            return;
        }
        getNext();
        setUpWallpaper();
    }


    private void getNext() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        int mState = Integer.parseInt(prefs.getString(Constants.PREF_AUTO_CHANGE_MODE_KEY, "1"));
        switch (mState) {
            case State.NEXT:
                next();
                break;
            case State.PREVIOUS:
                previous();
                break;
            default:
                if (images != null) {
                    int rInt = NumberUtils.random(images.size());
                    while (rInt == currentPosition) {
                        rInt = NumberUtils.random(images.size());
                    }
                    currentPosition = rInt;
                }
                break;
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand: ");
        String action = intent.getAction();
        assert action != null;
        switch (action) {
            case Constants.ACTION_START_FOREGROUND_SERVICE:
                createNotificationChannel();
                currentPosition = intent.getIntExtra(Constants.CURRENTITEM, 0);
                images = intent.getParcelableArrayListExtra(Constants.IMAGE);
                setUpWallpaper();
                break;
            case Constants.ACTION_START_IN_LOOP_FOREGROUND_SERVICE:
                getNext();
                setUpWallpaper();
                break;
            case Constants.ACTION_STOP_FOREGROUND_SERVICE:
                clearAll();
                sendBroadcastUpdateWidget();
                stopForeground(true);
                stopSelf();
                break;
            case Constants.ACTION_NEXT:
                next();
                setUpWallpaper();
                break;
            case Constants.ACTION_PREVIOUS:
                previous();
                setUpWallpaper();
                break;
            default:
                break;
        }
        setupListview();
        return START_STICKY;
    }

    private void startForegroundService() {
        //Handle tap action go back to

        Intent intentBackToScreen = new Intent(this, MainActivity.class);
        intentBackToScreen.putParcelableArrayListExtra(Constants.IMAGE, images);
        intentBackToScreen.putExtra(Constants.CURRENTITEM, currentPosition);
        if (database == null)
            database = AlbumsDatabase.getInMemoryDatabase(this);
        try {
            album = database.albumDao().findById(images.get(currentPosition).getAlbumId());
            String albumName = album.getTitle();
            String posPerTotal = getString(R.string.positionPerTotal, currentPosition + 1, images.size());
            //Stop
            intent = new Intent(this, this.getClass());
            intent.setAction(Constants.ACTION_STOP_FOREGROUND_SERVICE);
            pIntent = PendingIntent.getService(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

            //Next
            Intent intentNext = new Intent(this, this.getClass());
            intentNext.setAction(Constants.ACTION_NEXT);
            PendingIntent pendingIntentNext = PendingIntent.getService(this, 0, intentNext, PendingIntent.FLAG_UPDATE_CURRENT);

            //Previous
            Intent intentPre = new Intent(this, this.getClass());
            intentPre.setAction(Constants.ACTION_PREVIOUS);
            PendingIntent pendingIntentPre = PendingIntent.getService(this, 0, intentPre, PendingIntent.FLAG_UPDATE_CURRENT);

//            Bitmap bitmap = BitmapFactory.decodeFile(images.get(currentPosition).getPath());
            builder = new NotificationCompat.Builder(this, Constants.CHANEL_ID)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setContentTitle(getString(R.string.noti_text_title))
                    .setContentText(albumName + " (" + posPerTotal + ")")
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                    .addAction(android.R.drawable.ic_media_previous, getString(R.string.previous_auto_change), pendingIntentPre)
                    .addAction(android.R.drawable.ic_media_pause, getString(R.string.stop_auto_change), pIntent)
                    .addAction(android.R.drawable.ic_media_next, getString(R.string.next_auto_change), pendingIntentNext)
                    .setCategory(NotificationCompat.CATEGORY_MESSAGE)
//                    .setLargeIcon(bitmap)
                    .setNumber(1)
                    .setBadgeIconType(NotificationCompat.BADGE_ICON_SMALL);
//                    .setStyle(new NotificationCompat.BigPictureStyle().bigPicture(bitmap).bigLargeIcon(null));
            startForeground(Constants.FOREGROUND_ID, builder.build());
        } catch (Exception e) {
            Log.e(TAG, "startForegroundService: " + e.toString());
        }

    }


    private void sendBroadcastUpdateWidget() {
        Intent intent = new Intent();
        intent.setAction(Constants.ACTION_APPWIDGET_UPDATE);
        intent.setPackage(Constants.CHANEL_ID);
        intent.putParcelableArrayListExtra(Constants.IMAGE, images);
        intent.putExtra(Constants.CURRENTITEM, currentPosition);
        intent.putExtra(Constants.ALBUM, album);
        sendBroadcast(intent);
    }


    private void setUpWallpaper() {
        if (images == null) {
            return;
        }
        startForegroundService();
        sendBroadcastUpdateWidget();
        if (currentPosition >= images.size()) {
            currentPosition = 0;
        }
        WindowUtils.setWallPaperFitScreen(this, images.get(currentPosition).getPath());
        scheduleNextChange();
    }


    private void scheduleNextChange() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        boolean enableAutochange = prefs.getBoolean(Constants.PREF_AUTO_CHANGE_SWITCH_KEY, true);
        int time_interval = Integer.parseInt(prefs.getString(Constants.PREF_AUTO_CHANGE_FREQUENCY_KEY, "15"));
        intent = new Intent(this, this.getClass());
        intent.setAction(Constants.ACTION_START_IN_LOOP_FOREGROUND_SERVICE);
        if (time_interval == -1) {
            String interval = prefs.getString(Constants.PREF_CUSTOM_FREQUENCY_KEY, "");
            time_interval = TimeUtil.parseTime(interval);
            if (!enableAutochange || time_interval == 0) {
                if (time_interval == 0)
                    Toast.makeText(this, getString(R.string.not_complete_setting_time_interval), Toast.LENGTH_LONG).show();
                intent.setAction(Constants.ACTION_STOP_FOREGROUND_SERVICE);
                time_interval = 0;
            }
        }
        Log.d(TAG, "enableAutochange = " + enableAutochange + " scheduleNextChange: Time interval = " + time_interval);
        alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        pIntent = PendingIntent.getService(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime() + time_interval * 60 * 1000, pIntent);
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.channel_name);
            String description = getString(R.string.channel_description);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(Constants.CHANEL_ID, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            channel.setShowBadge(true);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            assert notificationManager != null;
            notificationManager.createNotificationChannel(channel);
        }
    }

    private void next() {
        if (images == null) return;
        currentPosition = ++currentPosition % images.size();
    }

    private void previous() {
        if (images == null) return;
        int size = images.size();
        currentPosition = (--currentPosition + size) % size;
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onDestroy() {
        clearAll();
        super.onDestroy();
        listener = null;
        windowManager.removeView(viewGroup);
    }

    private void clearAll() {
        if (alarmManager != null)
            alarmManager.cancel(pIntent);
        images = null;
        album = null;
        alarmManager = null;
        builder = null;
        if (mSensorManager != null)
            mSensorManager.unregisterListener(mShakeDetector);
    }

    @SuppressLint("ClickableViewAccessibility")
    private void createOverlayLayout() {
        Log.d(TAG, "createOverlayLayout: ");
        windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        LayoutInflater inflater = LayoutInflater.from(this);
        viewGroup = new FrameLayout(this);
        FrameLayout layoutOverlay = (FrameLayout) inflater.inflate(R.layout.layout_overlay, viewGroup);

        WindowManager.LayoutParams mParams = new WindowManager.LayoutParams();
        mParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
        mParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
        mParams.gravity = Gravity.CENTER_VERTICAL | Gravity.START;
        mParams.format = PixelFormat.TRANSLUCENT;
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            mParams.type = WindowManager.LayoutParams.TYPE_PHONE;
        } else {
            mParams.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        }
        mParams.flags = WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        windowManager.addView(viewGroup, mParams);

        btnHintOverlay = layoutOverlay.findViewById(R.id.hint_overlay);
        ImageView backBtn = layoutOverlay.findViewById(R.id.back);
        ImageView appBtn = layoutOverlay.findViewById(R.id.app);
        rvOverlay = layoutOverlay.findViewById(R.id.rv_overlay);
        contentOverlay = layoutOverlay.findViewById(R.id.content_overlay);

        contentOverlay.setVisibility(View.GONE);
        btnHintOverlay.setOnTouchListener(new OnSwipeTouchListener(this){
            @Override
            public void onSwipeRight() {
                showOverlayView();
            }

            @Override
            public void onClick() {
                showOverlayView();
            }
        });
        contentOverlay.setOnTouchListener(new OnSwipeTouchListener(this){
            @Override
            public void onSwipeLeft() {
                hideOverlayView();
            }
        });
        backBtn.setOnClickListener(this);
        appBtn.setOnClickListener(this);
    }

    private void setupListview() {
        GridLayoutManager mLayoutManager = new GridLayoutManager(this, 1);
        rvOverlay.setLayoutManager(mLayoutManager);
        rvOverlay.setHasFixedSize(true);

        OverlayAdapter adapter = new OverlayAdapter(images);
        adapter.setItemClickListener(listener);
        rvOverlay.setAdapter(adapter);
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.back:
                hideOverlayView();
                break;
            case R.id.app:
                Intent intent = new Intent(this, MainActivity.class);
                intent.putExtra(Constants.ALBUM, album);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                break;
        }
    }

    private void showOverlayView() {
        Animation showAnim = AnimationUtils.loadAnimation(this, R.anim.show_anim);
        Animation hideAnim = AnimationUtils.loadAnimation(this, R.anim.hide_anim);
        btnHintOverlay.startAnimation(hideAnim);
        new Handler().postDelayed(() -> {
            btnHintOverlay.setVisibility(View.GONE);
            contentOverlay.setVisibility(View.VISIBLE);
            contentOverlay.startAnimation(showAnim);
        }, 490);
    }

    private void hideOverlayView() {
        Animation showAnim = AnimationUtils.loadAnimation(this, R.anim.show_anim);
        Animation hideAnim = AnimationUtils.loadAnimation(this, R.anim.hide_anim);
        contentOverlay.startAnimation(hideAnim);
        new Handler().postDelayed(() -> {
            contentOverlay.setVisibility(View.GONE);
            btnHintOverlay.setVisibility(View.VISIBLE);
            btnHintOverlay.startAnimation(showAnim);
        }, 490);

    }

}
