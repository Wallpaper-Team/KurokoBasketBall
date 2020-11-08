package com.ducky.kurokobasketball.common.widget;

import android.content.Context;
import android.util.AttributeSet;

import androidx.appcompat.widget.AppCompatImageView;

public class SquareAlbumView extends AppCompatImageView {

    public SquareAlbumView(Context context) {
        super(context);
    }

    public SquareAlbumView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SquareAlbumView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int width = getMeasuredWidth();
        setMeasuredDimension(width, width/16*9);
    }
}