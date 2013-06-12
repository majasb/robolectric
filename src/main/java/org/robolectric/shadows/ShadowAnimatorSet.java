package org.robolectric.shadows;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.TimeInterpolator;
import org.robolectric.annotation.Implementation;
import org.robolectric.annotation.Implements;
import org.robolectric.annotation.RealObject;

@Implements(value = AnimatorSet.class, inheritImplementationMethods = true)
public class ShadowAnimatorSet extends ShadowAnimator {
  @RealObject
  private AnimatorSet realObject;
  private Animator[] childAnimators;

  @Implementation
  public void playTogether(Animator... items) {
    childAnimators = items;
  }

  @Implementation
  public void start() {
    for (Animator childAnimator : childAnimators) {
      childAnimator.setDuration(duration);
        childAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationCancel(Animator animation) {
                ShadowAnimatorSet.this.notifyCancel();
            }
            @Override
            public void onAnimationEnd(Animator animation) {
                ShadowAnimatorSet.this.notifyEnd();
            }
            @Override
            public void onAnimationRepeat(Animator animation) {
                ShadowAnimatorSet.this.notifyRepeat();
            }
            @Override
            public void onAnimationStart(Animator animation) {
                ShadowAnimatorSet.this.notifyStart();
            }
        });
      childAnimator.start();
    }
  }

  @Implementation
  public AnimatorSet setDuration(long duration) {
    this.duration = duration;
    return realObject;
  }

  @Implementation
  public void setInterpolator(TimeInterpolator interpolator) {
    for (Animator childAnimator : childAnimators) {
      childAnimator.setInterpolator(interpolator);
    }
  }

}
