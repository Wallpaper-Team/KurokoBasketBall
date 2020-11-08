package com.ducky.kurokobasketball.utils.support;

import android.util.Log;

public class TimeUtil {
    private static final String TAG = "TimeUtil";

    public static int parseTime(String s) {
        int time = 0;
        try {
            int sPos = s.indexOf(" ");
            int h = Integer.parseInt(s.substring(0, sPos));
            time += h * 60;
            s = s.substring(sPos + 1);
            sPos = s.indexOf(" ");
            s = s.substring(sPos + 1);
            sPos = s.indexOf(" ");
            int m = Integer.parseInt(s.substring(0, sPos));
            time += m;
        } catch (Exception e) {
            Log.e(TAG, "parseTime: " + e.toString());
        }
        return time;
    }
}
