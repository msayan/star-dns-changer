package com.hololo.app.dnschanger.utils.ui;

import android.content.Context;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.design.widget.CoordinatorLayout;
import android.util.AttributeSet;
import android.widget.ScrollView;

import com.hololo.app.dnschanger.utils.MoveUpBehavior;

@CoordinatorLayout.DefaultBehavior(MoveUpBehavior.class)
public class BehaviorScrollView extends ScrollView {
    public BehaviorScrollView(Context context) {
        super(context);
    }

    public BehaviorScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public BehaviorScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public BehaviorScrollView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }
}
