package com.example.jiashuaishuai.myapplicationrefreshscrollview;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Point;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;


/**
 * Created by jiashuaishuai on 2016/3/22.
 */
public class DropDownScrollView extends ScrollView {
    private static final String TAG = "DropDownScrollView";


    private Point point;
    /**
     * 下拉监听
     */
    private DropDownScrollViewPullDownListener dropDownScrollViewPullDownListener;
    // 记录首次按下位置
    private float mFirstPosition = 0;
    // 是否正在放大
    private Boolean mScaling = false;
    /**
     * 指标器
     */
    private DisplayMetrics metric;
    /**
     * 头部布局
     */
    private RelativeLayout register_rl;
    /**
     * 头部布局背景图片，
     */
    private ImageView head_image_view;
    private int distance;


    public DropDownScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
        WindowManager wm = (WindowManager) getContext()
                .getSystemService(Context.WINDOW_SERVICE);
// 获取屏幕宽高
        metric = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(metric);
        point = new Point();
    }


    /**
     * 设置需要放大的图片和布局，
     *
     * @param img
     * @param layout
     */
    public void setBackgroundImgAndStretchLayout(ImageView img, View layout) {
        head_image_view = img;
        register_rl = (RelativeLayout) layout;
        //设置布局和图片初始大小  这里我设为满屏的16:9，也可以测量实现，
        ViewGroup.LayoutParams lp = (ViewGroup.LayoutParams) register_rl.getLayoutParams();
        LinearLayout.LayoutParams llp = (LinearLayout.LayoutParams) head_image_view.getLayoutParams();
        /**
         * 图片
         */
        llp.width = metric.widthPixels;
        llp.height = metric.widthPixels * 12 / 16;
        head_image_view.setLayoutParams(llp);
        /**
         * 布局
         */
        lp.width = metric.widthPixels;
        lp.height = metric.widthPixels * 12 / 16;
        register_rl.setLayoutParams(lp);
    }


    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        ViewGroup.LayoutParams lp = (ViewGroup.LayoutParams) register_rl.getLayoutParams();
        LinearLayout.LayoutParams llp = (LinearLayout.LayoutParams) head_image_view.getLayoutParams();
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:

                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
/**
 * 当手指抬起时，当前坐标-顶端记录的坐=下拉图片的长度；
 */
                int endY = (int) (ev.getY() - point.y);
                /**
                 *如果这个长度大于三百，并且，图片拉伸的长度大于100，则触发监听，
                 */
                if (endY > 300 && distance >= 100) {
                    if (dropDownScrollViewPullDownListener != null)
                        dropDownScrollViewPullDownListener.ScrollViewPullDown();
                }
                //手指离开后恢复图片
                mScaling = false;
                replyImage();
                break;
            case MotionEvent.ACTION_MOVE:
                if (!mScaling) {
                    if (getScrollY() == 0) {
                        mFirstPosition = ev.getY();// 滚动到顶部时记录位置，否则正常返回
                        /**
                         * 当滑动到顶部时记录一个坐标
                         */
                        point.y = (int) ev.getY();
                    } else {
                        break;
                    }
                }

                distance = (int) ((ev.getY() - mFirstPosition) * 0.3); // 滚动距离乘以一个系数,越小阻力越大
                if (distance < 0) { // 当前位置比记录位置要小，正常返回
                    break;
                }
                // 处理放大
                mScaling = true;
                /**
                 * 布局
                 */
//                        lp.width = metric.widthPixels + distance;
                lp.height = (metric.widthPixels + distance) * 12 / 16;
                register_rl.setLayoutParams(lp);
                /**
                 * 图片
                 */
                llp.width = metric.widthPixels + distance;
                llp.height = (metric.widthPixels + distance) * 12 / 16;
                head_image_view.setLayoutParams(llp);
                return true; // 返回true表示已经完成触摸事件，不再处理
        }
        return super.onTouchEvent(ev);
    }

    // 回弹动画 (使用了属性动画)
    @SuppressLint("NewApi")
    public void replyImage() {
        final ViewGroup.LayoutParams lp = (ViewGroup.LayoutParams) register_rl.getLayoutParams();
        final LinearLayout.LayoutParams llp = (LinearLayout.LayoutParams) head_image_view.getLayoutParams();
        final float w = head_image_view.getLayoutParams().width;// 图片当前宽度
        final float h = head_image_view.getLayoutParams().height;// 图片当前高度
        final float newW = metric.widthPixels;// 图片原宽度
        final float newH = metric.widthPixels * 12 / 16;// 图片原高度

        // 设置动画
        ValueAnimator anim = ObjectAnimator.ofFloat(0.0F, 1.0F).setDuration(200);

        anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float cVal = (Float) animation.getAnimatedValue();
                /**
                 * 布局
                 */
//                lp.width = (int) (w - (w - newW) * cVal);
                lp.height = (int) (h - (h - newH) * cVal);
                register_rl.setLayoutParams(lp);
                /**
                 * 图片
                 */
                llp.width = (int) (w - (w - newW) * cVal);
                llp.height = (int) (h - (h - newH) * cVal);
                head_image_view.setLayoutParams(llp);
            }
        });
        anim.start();
    }

    /**
     * 设置下拉监听
     *
     * @param dropDownScrollViewPullDownListener
     * @return
     */
    public void setDropDownScrollViewPullDownListener(DropDownScrollViewPullDownListener dropDownScrollViewPullDownListener) {
        this.dropDownScrollViewPullDownListener = dropDownScrollViewPullDownListener;
    }

    /**
     * 下拉监听借口
     */
    public interface DropDownScrollViewPullDownListener {
        void ScrollViewPullDown();
    }
}
