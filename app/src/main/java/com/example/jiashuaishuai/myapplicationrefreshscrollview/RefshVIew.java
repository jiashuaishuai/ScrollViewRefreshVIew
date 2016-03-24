package com.example.jiashuaishuai.myapplicationrefreshscrollview;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Point;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

/**
 * Created by jiashuaishuai on 2016/3/23.
 */
public class RefshVIew extends RelativeLayout {

    private static final String TAG = "RefreshView";
    private View atView;
    private int imgheight, imgwidth;//img高度
    private int imgy;//当前img的y轴坐标
    private int endimgy;//结束img的y轴坐标
    private ImageView img;
    private Point point;

    private RefreshListener refreshListener;


    public RefshVIew(Context context, AttributeSet attrs) {
        super(context, attrs);
        img = new ImageView(context);
        img.setImageResource(R.mipmap.ic_launcher);
        addView(img);
        point = new Point();

/**
 * 获取img的高度
 */
        int w = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        int h = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        img.measure(w, h);
        imgheight = img.getMeasuredHeight();
        imgwidth = img.getMeasuredWidth();

        img.setY(-imgheight);//设置img的y轴坐标
        imgy = -imgheight;//当前img的y坐标
        animator = ValueAnimator.ofFloat(0, 360);
        animator.setRepeatCount(Integer.MAX_VALUE);
        animator.setDuration(300);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
//                        img.setY((Float) animation.getAnimatedValue());
                img.setRotation((Float) animation.getAnimatedValue());
            }
        });
        if (atView == null) {
            Log.i(TAG, "getChildAt==null");
            atView = getChildAt(1);
        }


    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        if (atView == null) {
            Log.i(TAG, "getChildAt==null");
            atView = getChildAt(1);
        }

    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                point.x = (int) event.getRawX();
                point.y = (int) event.getRawY();
                if (animator.isStarted()) {
                    animator.end();
                }

                break;
            case MotionEvent.ACTION_MOVE:
                /**
                 * 如果子view滑动到顶端则下拉动画
                 */
                if (atView.getScrollY() != 0) {
                    break;
                }
                int dx = (int) (event.getRawX() - point.x);
                int dy = (int) (event.getRawY() - point.y);
                Log.i("TAG", "dy:" + dy + "atView.getScrollY()" + atView.getScrollY());
                endimgy = imgy + dy / 3;//结束y坐标
                /**
                 * 下拉倒一定程度不会再下拉
                 */
                if (endimgy > 100) {
                    endimgy = 100;
                }
                img.setY(endimgy);//设置y轴坐标

                img.setRotation(dy);//设置旋转角度

                break;
            case MotionEvent.ACTION_UP:
/**
 * 如果没有达到100则属于取消刷新，停止动画
 */
                if (endimgy < 100) {
                    noRefresh();
                } else {

                    if (refreshListener != null) {
                        refreshListener.RefreshViewPullDown();
                    }
                    /**
                     * 如果当前旋转动画在执行，则停止上一个动画在启动，
                     */
                    if (animator.isStarted()) {
                        animator.end();
                        animator.start();
                    } else {
                        animator.start();
                    }
                }

                break;
        }

        return super.dispatchTouchEvent(event);
    }

    private ValueAnimator animator;

    /**
     * 停止刷新
     */
    private void noRefresh() {
        animator.end();
        animator.cancel();
        ValueAnimator animator = ValueAnimator.ofFloat(endimgy, 0 - imgheight);
//        animator.setTarget(this);
        animator.setDuration(300).start();
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                img.setY((Float) animation.getAnimatedValue());
                img.setRotation((Float) animation.getAnimatedValue());
            }
        });


    }

    /**
     * 下拉刷新监听
     *
     * @param refreshListener
     */
    public void setRefreshListener(RefreshListener refreshListener) {
        this.refreshListener = refreshListener;
    }

    public interface RefreshListener {
        void RefreshViewPullDown();
    }
}
