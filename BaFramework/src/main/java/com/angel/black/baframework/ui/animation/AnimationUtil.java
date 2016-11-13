package com.angel.black.baframework.ui.animation;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.TargetApi;
import android.app.Activity;
import android.os.Build;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.RotateAnimation;

import com.angel.black.baframework.R;
import com.angel.black.baframework.core.base.BaseActivity;
import com.angel.black.baframework.logger.BaLog;
import com.angel.black.baframework.util.BuildUtil;

/**
 * Created by KimJeongHun on 2016-07-13.
 */
public class AnimationUtil {

    public static void startSlideRightInAnim(Activity activity, final View view) {
        startSlideRightInAnim(activity, view, new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                view.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animation animation) {

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }

    public static void startSlideRightInAnim(Activity activity, final View view, Animation.AnimationListener animationListener) {
        BaLog.i();
        final Animation btnAnim = AnimationUtils.loadAnimation(activity, R.anim.slide_right_in);
        btnAnim.setInterpolator(new DecelerateInterpolator());
        btnAnim.setAnimationListener(animationListener);
        view.post(new Runnable() {
            @Override
            public void run() {
                view.startAnimation(btnAnim);
            }
        });
    }

    public static void startSlideRightOutAnim(Activity activity, final View view) {
        startSlideRightOutAnim(activity, view, new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                view.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }

    public static void startSlideRightOutAnim(Activity activity, final View view, Animation.AnimationListener animationListener) {
        BaLog.i();
        final Animation btnAnim = AnimationUtils.loadAnimation(activity, R.anim.slide_right_out);
        btnAnim.setAnimationListener(animationListener);
        view.post(new Runnable() {
            @Override
            public void run() {
                view.startAnimation(btnAnim);
            }
        });
    }

    public static void startSlideTopInAnim(Activity activity, final View view) {
        startSlideTopInAnim(activity, view, null);
    }

    public static void startSlideTopInAnim(Activity activity, final View view, final Animation.AnimationListener externalAnimationListener) {
        BaLog.i();
        final Animation anim = AnimationUtils.loadAnimation(activity, R.anim.slide_top_in);
        anim.setInterpolator(new AccelerateInterpolator());
        anim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                view.setVisibility(View.VISIBLE);
                if(externalAnimationListener != null) {
                    externalAnimationListener.onAnimationStart(animation);
                }
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                if(externalAnimationListener != null) {
                    externalAnimationListener.onAnimationEnd(animation);
                }
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
                if(externalAnimationListener != null) {
                    externalAnimationListener.onAnimationRepeat(animation);
                }
            }
        });

        view.post(new Runnable() {
            @Override
            public void run() {
                view.startAnimation(anim);
            }
        });
    }

    public static void startSlideBottomInAnim(Activity activity, final View view) {
        startSlideBottomInAnim(activity, view, null);
    }

    public static void startSlideBottomInAnim(Activity activity, final View view, final Animation.AnimationListener externalAnimationListener) {
        BaLog.i();
        final Animation anim = AnimationUtils.loadAnimation(activity, R.anim.slide_bottom_in);
        anim.setDuration(400);
        anim.setInterpolator(new AccelerateInterpolator());
        anim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                BaLog.d();
                view.setVisibility(View.VISIBLE);
                if(externalAnimationListener != null) {
                    externalAnimationListener.onAnimationStart(animation);
                }
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                BaLog.d();
                if(externalAnimationListener != null) {
                    externalAnimationListener.onAnimationEnd(animation);
                }
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
                BaLog.d();
                if(externalAnimationListener != null) {
                    externalAnimationListener.onAnimationRepeat(animation);
                }
            }
        });

        view.post(new Runnable() {
            @Override
            public void run() {
                BaLog.d();
                view.startAnimation(anim);
            }
        });
    }

    public static void startRotateAnimation(final View view, float fromDegree, final float toDegree, final ViewRotateListener imageRotateListener) {
        BaLog.d("fromDegree=" + fromDegree + ", toDegree=" + toDegree);

        if(BuildUtil.isAboveIcecreamSandwich()) {
            ObjectAnimator anim = ObjectAnimator.ofFloat(view, "rotation", fromDegree, toDegree);
            anim.setTarget(view);
            anim.setDuration(150);
            anim.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {
                    imageRotateListener.onStartRotateImage(view);
                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    imageRotateListener.onEndRotateImage(view, (int) toDegree);
                }

                @Override
                public void onAnimationCancel(Animator animation) {

                }

                @Override
                public void onAnimationRepeat(Animator animation) {

                }
            });

            anim.start();
        }
        else {
            final RotateAnimation rotateAnim = new RotateAnimation(fromDegree, toDegree, RotateAnimation.RELATIVE_TO_SELF, 0.5f, RotateAnimation.RELATIVE_TO_SELF, 0.5f);
            rotateAnim.setDuration(300);
            rotateAnim.setFillBefore(true);
            rotateAnim.setFillAfter(true);

            rotateAnim.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                    if(imageRotateListener != null) {
                        imageRotateListener.onStartRotateImage(view);
                    }
                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    BaLog.d("rotate end!!! >> toDegree=" + toDegree);
                    if(imageRotateListener != null) {
                        imageRotateListener.onEndRotateImage(view, (int) toDegree);
                    }
                }

                @Override
                public void onAnimationRepeat(Animation animation) {
                }
            });

            view.post(new Runnable() {
                @Override
                public void run() {
                    view.startAnimation(rotateAnim);
                }
            });
        }
    }

    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    public static void startFlipXAnimation(View view, boolean flipped, Animator.AnimatorListener animatorListener) {
        AnimatorSet animatorSet1 = new AnimatorSet();
        AnimatorSet animatorSet2 = new AnimatorSet();

        int startDegree = flipped ? 180 : 0;
        int endDegree = flipped ? 90 : 90;
        int offset = flipped ? -90 : 90;

        animatorSet1.playTogether(
                ObjectAnimator.ofFloat(view, "rotationY", startDegree, endDegree),
                ObjectAnimator.ofFloat(view, "scaleX", 1.0f, 0.6f),
                ObjectAnimator.ofFloat(view, "scaleY", 1.0f, 0.6f)
        );

        animatorSet2.playTogether(
                ObjectAnimator.ofFloat(view, "rotationY", startDegree + offset, endDegree + offset),
                ObjectAnimator.ofFloat(view, "scaleX", 0.6f, 1.0f),
                ObjectAnimator.ofFloat(view, "scaleY", 0.6f, 1.0f)
        );

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playSequentially(animatorSet1, animatorSet2);
        animatorSet.setDuration(150);
        animatorSet.addListener(animatorListener);

        animatorSet.start();
    }

    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    public static void startFlipYAnimation(View view, boolean flipped, Animator.AnimatorListener animatorListener) {
        AnimatorSet animatorSet1 = new AnimatorSet();
        AnimatorSet animatorSet2 = new AnimatorSet();

        int startDegree = flipped ? 180 : 0;
        int endDegree = flipped ? 90 : 90;
        int offset = flipped ? -90 : 90;

        animatorSet1.playTogether(
                ObjectAnimator.ofFloat(view, "rotationX", startDegree, endDegree),
                ObjectAnimator.ofFloat(view, "scaleX", 1.0f, 0.6f),
                ObjectAnimator.ofFloat(view, "scaleY", 1.0f, 0.6f)
        );

        animatorSet2.playTogether(
                ObjectAnimator.ofFloat(view, "rotationX", startDegree + offset, endDegree + offset),
                ObjectAnimator.ofFloat(view, "scaleX", 0.6f, 1.0f),
                ObjectAnimator.ofFloat(view, "scaleY", 0.6f, 1.0f)
        );

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playSequentially(animatorSet1, animatorSet2);
        animatorSet.setDuration(150);
        animatorSet.addListener(animatorListener);

        animatorSet.start();
    }

    /**
     * 두개의 상하단 뷰가 나타나는 애니메이션
     * @param activity
     * @param views
     */
    public static void startViewAppearSlideTopBottomAnim(BaseActivity activity, final View... views) {
        BaLog.i();

        final Animation slideDownAnim = AnimationUtils.loadAnimation(activity, R.anim.slide_top_in);
        final Animation slideUpAnim = AnimationUtils.loadAnimation(activity, R.anim.slide_bottom_in);
        slideUpAnim.setDuration(300);
        slideDownAnim.setDuration(300);
        slideDownAnim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                views[0].setVisibility(View.VISIBLE);
                views[1].setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animation animation) {

            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });
        views[0].post(new Runnable() {
            @Override
            public void run() {
                views[0].startAnimation(slideDownAnim);
                views[1].startAnimation(slideUpAnim);
            }
        });
    }

    /**
     * 두개의 상하단 뷰가 사라지는 애니메이션
     * @param activity
     * @param views
     */
    public static void startViewDisAppearSlideTopBottomAnim(BaseActivity activity, final View... views) {
        BaLog.i();

        BaLog.d(views);

        final Animation slideDownAnim = AnimationUtils.loadAnimation(activity, R.anim.slide_bottom_out);
        final Animation slideUpAnim = AnimationUtils.loadAnimation(activity, R.anim.slide_top_out);
        slideUpAnim.setDuration(300);
        slideDownAnim.setDuration(300);
        slideDownAnim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                BaLog.i();
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                BaLog.i();
                views[0].setVisibility(View.GONE);
                views[1].setVisibility(View.GONE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });

        views[0].post(new Runnable() {
            @Override
            public void run() {
                BaLog.i();
                views[0].startAnimation(slideUpAnim);
                views[1].startAnimation(slideDownAnim);
            }
        });
    }

    /**
     * 가운데로 축소되면서 사라지는 애니메이션
     * @param activity
     * @param view
     * @param animationListener
     */
    public static void startViewDisAppearScaleToZeroAnim(BaseActivity activity, final View view, Animation.AnimationListener animationListener) {
        BaLog.i();
        final Animation anim = AnimationUtils.loadAnimation(activity, R.anim.scale_to_zero);
        anim.setFillAfter(true);
        anim.setAnimationListener(animationListener);
        view.post(new Runnable() {
            @Override
            public void run() {
                view.startAnimation(anim);
            }
        });
    }

    public interface ViewRotateListener {
        void onStartRotateImage(View imgView);
        void onEndRotateImage(View imgView, int toDegree);
    }
}
