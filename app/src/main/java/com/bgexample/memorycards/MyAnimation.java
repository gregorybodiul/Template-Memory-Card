package com.bgexample.memorycards;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.view.View;

public class MyAnimation extends ValueAnimator {
    private ObjectAnimator objectAnimator;

    public MyAnimation(View view, String property, int ValStart, int ValEnd, int duration) {
        ObjectAnimator animation = ObjectAnimator.ofFloat(view, property, ValStart, ValEnd);
        animation.setDuration(duration);
        animation.start();
        objectAnimator = animation;
    }

    public ObjectAnimator getObjectAnimator(){
        return objectAnimator;
    }
}
