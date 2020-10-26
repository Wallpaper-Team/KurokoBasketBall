package com.tiendollar.edgechangewallpaper.utils.callback;

public interface ItemTouchListenner {
    void onMove(int oldPosition, int newPosition);

    void swipe(int position, int direction);
}