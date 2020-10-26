package com.tiendollar.edgechangewallpaper.utils.support;

import java.util.Random;

public class NumberUtils {
    public static int random(int n) {
        Random r = new Random();
        return r.nextInt(n);
    }
}
