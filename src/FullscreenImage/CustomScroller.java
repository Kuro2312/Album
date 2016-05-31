package FullscreenImage;

import android.content.Context;
import android.view.animation.Interpolator;
import android.widget.Scroller;

public class CustomScroller extends Scroller {

    private int mDuration;

    public CustomScroller(Context context, int duration) {
        super(context);
        mDuration = duration;
    }

    public CustomScroller(Context context, Interpolator interpolator, int duration) {
        super(context, interpolator);
        mDuration = duration;
    }

    public CustomScroller(Context context, Interpolator interpolator, boolean flywheel, int duration) {
        super(context, interpolator, flywheel);
        mDuration = duration;
    }


    @Override
    public void startScroll(int startX, int startY, int dx, int dy, int duration) {
        // Ignore received duration, use fixed one instead
        super.startScroll(startX, startY, dx, dy, mDuration);
    }

    @Override
    public void startScroll(int startX, int startY, int dx, int dy) {
        // Ignore received duration, use fixed one instead
        super.startScroll(startX, startY, dx, dy, mDuration);
    }
}

