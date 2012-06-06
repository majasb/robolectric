package com.xtremelabs.robolectric.shadows;

import com.xtremelabs.robolectric.internal.Implementation;
import com.xtremelabs.robolectric.internal.Implements;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

@Implements(RelativeLayout.class)
public class ShadowRelativeLayout extends ShadowViewGroup {

    public void __constructor__(Context context, AttributeSet attributeSet, int defStyle) {
        setLayoutParams(new ViewGroup.MarginLayoutParams(0, 0));
        super.__constructor__(context, attributeSet, defStyle);
    }

    @Implementation
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
    	int width = View.MeasureSpec.getSize(widthMeasureSpec);
    	int height = View.MeasureSpec.getSize(heightMeasureSpec);

    	layout(right, top, right + width, top + height);
    }

}
