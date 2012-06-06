package com.xtremelabs.robolectric.shadows;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.junit.Assert.*;

import org.junit.Test;
import org.junit.runner.RunWith;

import com.xtremelabs.robolectric.WithTestDefaultsRunner;

import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

@RunWith(WithTestDefaultsRunner.class)
public class RelativeLayoutTest {

    @Test
    public void getLayoutParamsShouldReturnInstanceOfMarginLayoutParams() {
        RelativeLayout relativeLayout = new RelativeLayout(null);
        ViewGroup.LayoutParams layoutParams = relativeLayout.getLayoutParams();
        assertThat(layoutParams, instanceOf(ViewGroup.MarginLayoutParams.class));
    }

}
