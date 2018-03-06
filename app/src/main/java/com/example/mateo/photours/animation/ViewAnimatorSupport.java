package com.example.mateo.photours.animation;

import android.animation.ValueAnimator;
import android.app.Activity;
import android.view.View;

public class ViewAnimatorSupport {

    public static void animate(Activity activity, int viewId, long duration, final CoordinateAnimation coordinate, float... floats) {
        final View view = activity.findViewById(viewId);

        ValueAnimator va = ValueAnimator.ofFloat(floats);
        va.setDuration(duration);

        va.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            public void onAnimationUpdate(ValueAnimator animation) {
                if(coordinate.equals(CoordinateAnimation.COORDINATE_X)) {
                    view.setTranslationX((float) animation.getAnimatedValue());
                } else if (coordinate.equals(CoordinateAnimation.COORDINATE_Y)) {
                    view.setTranslationY((float) animation.getAnimatedValue());
                }
            }
        });
        va.start();
    }
}
