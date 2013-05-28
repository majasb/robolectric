package org.robolectric.shadows;

import android.view.animation.BounceInterpolator;
import android.widget.Scroller;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.TestRunners;

import static org.fest.assertions.api.Assertions.assertThat;

@RunWith(TestRunners.WithDefaults.class)
public class ScrollerTest {
  private Scroller scroller;

  @Before
  public void setup() throws Exception {
    scroller = new Scroller(Robolectric.application, new BounceInterpolator());
  }

  @Test
  public void shouldScrollOverTime() throws Exception {
    scroller.startScroll(0, 0, 12, 36, 1000);

    assertThat(scroller.getFinalX()).isEqualTo(12);
    assertThat(scroller.getCurrX()).isEqualTo(0);
    assertThat(scroller.getCurrY()).isEqualTo(0);

    Robolectric.idleMainLooper(334);
    assertThat(scroller.getCurrX()).isEqualTo(4);
    assertThat(scroller.getCurrY()).isEqualTo(12);

    Robolectric.idleMainLooper(166);
    assertThat(scroller.getCurrX()).isEqualTo(6);
    assertThat(scroller.getCurrY()).isEqualTo(18);

    Robolectric.idleMainLooper(500);
    assertThat(scroller.getCurrX()).isEqualTo(12);
    assertThat(scroller.getCurrY()).isEqualTo(36);
  }

  @Test
  public void computeScrollOffsetShouldCalculateWhetherScrollIsFinished() throws Exception {
    assertThat(scroller.computeScrollOffset()).isFalse();

    scroller.startScroll(0, 0, 12, 36, 1000);
    assertThat(scroller.computeScrollOffset()).isTrue();

    Robolectric.idleMainLooper(500);
    assertThat(scroller.computeScrollOffset()).isTrue();

    Robolectric.idleMainLooper(500);
    assertThat(scroller.computeScrollOffset()).isTrue();
    assertThat(scroller.computeScrollOffset()).isFalse();
  }
}
