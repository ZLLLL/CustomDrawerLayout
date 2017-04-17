package com.zz.customdrawerlayout;

import android.content.Context;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.ViewDragHelper;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by user on 2017/4/17.
 */

public class CustomDrawerLayout extends ViewGroup {

    private static final String TAG = "CustomDrawerLayout";

    private static final int MIN_DRAWER_MARGIN = 64; //dp
    /**
     * minimum speed that will be detected as a fling
     */
    private static final int MIN_FLANG_VELOCITY = 400;  //dips per seconde

    /**
     * drawer离父容器右边的最小边距
     */
    private int mMinDrawerMargin;

    private View mDrawerView;
    private View mContetView;
    private View mRootView;

    private ViewDragHelper mHelper;
    /**
     * drawer显示处理的占自身百分比
     */
    private float mDrawerOnScreen;

    /**
     * 由于当前的侧滑栏划出的状态有两种，一种是完全显示，另一种是部分显示
     */
    private int mCurrentWidth = 0;

    private static float PROPORTION = 0.25f;

    /**
     * 只显示图标的drawer的宽度
     */
    private int mOnlyIconVisibleWidth;

    /**
     * drawer的高度
     */
    private int mDrawerTopMargin;

    public CustomDrawerLayout(Context context, AttributeSet attrs) {
        super(context, attrs);

        mRootView = this;
        final float debsity = getResources().getDisplayMetrics().density; //获取屏幕密度
        final float minVel = MIN_FLANG_VELOCITY * debsity;
        mMinDrawerMargin = (int) (MIN_DRAWER_MARGIN * debsity + 0.5f);

        mHelper = ViewDragHelper.create(this, 1.0f, new ViewDragHelper.Callback() {
            /**
             * 该方法的返回值决定哪个子view可以拖动
             * @param child 子view
             * @param pointerId
             * @return true: 代表该child可以被拖动, false: 代表child不可以被拖动
             */
            @Override
            public boolean tryCaptureView(View child, int pointerId) {
                return child == mDrawerView;
            }

            /**
             * 返回一个适当的数值就能实现横向拖动效果，
             * @param child
             * @param left left参数指当前拖动子view应该到达的x坐标 按照常理直接返回该参数就可以了，但是为了让被拖动的view遇到边界就不能再拖动
             *             了，所以就可以对该值进行处理，返回更合理的数值
             * @param dx
             * @return
             */
            @Override
            public int clampViewPositionHorizontal(View child, int left, int dx) {
                int newLeft = Math.max(-child.getWidth(), Math.min(left, 0));
                return newLeft;
            }

            /**
             * 在边缘滑动的时候根据滑动距离移动一个子view
             * @param edgeFlags
             * @param pointerId
             */
            @Override
            public void onEdgeDragStarted(int edgeFlags, int pointerId) {
                mHelper.captureChildView(mDrawerView, pointerId);
            }

            /**
             * 手指释放的时候回调该方法
             * @param releasedChild
             * @param xvel
             * @param yvel
             */
            @Override
            public void onViewReleased(View releasedChild, float xvel, float yvel) {
                final int childWidth = releasedChild.getWidth();
                float offset = (childWidth + releasedChild.getLeft()) * 1.0f / childWidth;
                int width;
                Log.d(TAG, "onViewReleased: xvel : " + xvel + ", offset: " + offset);
                if (offset < PROPORTION * 0.5) {
                    Log.d(TAG, "invisible");
                    width = -childWidth;
                } else if (offset >= PROPORTION * 0.5 && offset <= (1 + PROPORTION) * 0.5) {
                    width = (int) (-childWidth * (1 - PROPORTION));
                    mCurrentWidth = width;
                    Log.d(TAG, "part visible");
                } else {
                    width = 0;
                    mCurrentWidth = width;
                    Log.d(TAG, "visible");
                }
                mHelper.settleCapturedViewAt(
                        width,
                        mDrawerTopMargin);
                invalidate();
            }

            /**
             * changeView在拖动过程中，坐标发生变化时回调该方法，包括手动拖动和view自动滚动
             * @param changedView
             * @param left
             * @param top
             * @param dx
             * @param dy
             */
            @Override
            public void onViewPositionChanged(View changedView, int left, int top, int dx, int dy) {
//                super.onViewPositionChanged(changedView, left, top, dx, dy);
                final int childWidth = changedView.getWidth();
                float offset = (float) (childWidth + left) / childWidth;
                mDrawerOnScreen = offset;
                //offset can callback here
                changedView.setVisibility(offset == 0 ? INVISIBLE : VISIBLE);
                invalidate();
            }

            /**
             * 返回横向拖动的最大距离
             * @param child
             * @return
             */
            @Override
            public int getViewHorizontalDragRange(View child) {
//                return super.getViewHorizontalDragRange(child);
                return child == mDrawerView ? mDrawerView.getWidth() : 0;
            }
        });

        mHelper.setEdgeTrackingEnabled(ViewDragHelper.EDGE_LEFT);   //设置左边缘滑动
        mHelper.setMinVelocity(minVel); //设置最小滑动速度
    }

    @Override
    protected void onLayout(boolean b, int i, int i1, int i2, int i3) {
        View drawerView = mDrawerView;
        View contentView = mContetView;

        MarginLayoutParams lp = (MarginLayoutParams) contentView.getLayoutParams();
        contentView.layout(lp.leftMargin,
                lp.topMargin,
                lp.leftMargin + contentView.getMeasuredWidth(),
                lp.topMargin + contentView.getMeasuredHeight());


        lp = (MarginLayoutParams) drawerView.getLayoutParams();
        final int menuWidth = drawerView.getMeasuredWidth();
        int childLeft = -menuWidth + (int) (menuWidth * mDrawerOnScreen);

        drawerView.layout(childLeft,
                lp.topMargin,
                childLeft + menuWidth,
                lp.topMargin + drawerView.getMeasuredHeight());

        //获取到drawer距离上侧的距离,用于之后滑动
        mDrawerTopMargin = lp.topMargin;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        setMeasuredDimension(widthSize, heightSize);

        //侧滑栏View
        View drawerView = getChildAt(1);
        MarginLayoutParams lp = (MarginLayoutParams) drawerView.getLayoutParams();
        /**
         * getChildMeasureSpec可以设置子view的内外边距，并且记录预订大小,
         * spec: 父view的详细测量值
         * padding: 子view内外边距
         * childDimension: 子view预订的大小（layoutParam.width 或 height），最终不一定绘制该大小，
         * 子布局大小需要自己的layoutParam属性和父view的measureSpec共同决定
         * */
        final int drawerWidthSpec = getChildMeasureSpec(widthMeasureSpec,
                mMinDrawerMargin + lp.leftMargin + lp.rightMargin,
                lp.width);
        final int drawerHeightSpec = getChildMeasureSpec(heightMeasureSpec,
                lp.topMargin + lp.bottomMargin,
                lp.height);
        drawerView.measure(drawerWidthSpec, drawerHeightSpec);


        View contentView = getChildAt(0);
        lp = (MarginLayoutParams) contentView.getLayoutParams();
        /**
         * makeMeasureSpec传入布局的测量方式和大小，得到的就是上面所说的spec
         * */
        final int contentWidthSpec = MeasureSpec.makeMeasureSpec(
                widthSize - lp.leftMargin - lp.rightMargin,
                MeasureSpec.EXACTLY);

        final int contentHeightSpec = MeasureSpec.makeMeasureSpec(
                heightSize - lp.topMargin - lp.bottomMargin,
                MeasureSpec.EXACTLY);
        contentView.measure(contentWidthSpec, contentHeightSpec);

        mDrawerView = drawerView;
        mContetView = contentView;

        mOnlyIconVisibleWidth = (int) (-drawerView.getWidth() * (1 - PROPORTION));
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        boolean shouldOmterceptTouchEvent = mHelper.shouldInterceptTouchEvent(ev);
        return shouldOmterceptTouchEvent;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        mHelper.processTouchEvent(event);
        return true;
    }

    @Override
    public void computeScroll() {
        if (mHelper.continueSettling(true)) {
            invalidate();
        }
    }

    /**
     * 关闭drawer
     */
    public void closeDrawer() {
        View drawerView = mDrawerView;
        drawer(-drawerView.getWidth());
    }

    /**
     * 打开drawer，要记录上一次是只显示icon还是显示icon和title，如果上一次打开drawer显示的只有icon，那么第二次打开也是显示icon,
     * 如果上一次打开drawer显示icon 和 title,那么第二次打开也显示icon 和 title
     */
    public void openDrawer() {
        drawer(mCurrentWidth);
    }

    /**
     * 当前drawer是否打开
     *
     * @return
     */
    public boolean isOpened() {
        return mDrawerOnScreen != 0;
    }

    /**
     * 水平移动drawer到指定宽度width
     *
     * @param width
     */
    private void drawer(int width) {
        View drawerView = mDrawerView;
        if (width == 0) {
            mDrawerOnScreen = 1;
        } else if (width == -drawerView.getWidth()) {
            mDrawerOnScreen = 0;
        }
        /**
         * smoothSlideViewTo方法是把父布局中的某一个子view移动到指定位置
         * */
        if (mHelper.smoothSlideViewTo(drawerView, width, mDrawerTopMargin)) {
            ViewCompat.postInvalidateOnAnimation(mRootView);
            postInvalidate();
        }
    }

    /**
     * 设置drawer只显示图标
     */
    public void onlyShowIcon() {
        if (mDrawerOnScreen == 1) {
            mCurrentWidth = mOnlyIconVisibleWidth;
            drawer(mOnlyIconVisibleWidth);
        }
    }

    @Override
    protected LayoutParams generateDefaultLayoutParams() {
//        return super.generateDefaultLayoutParams();
        return new MarginLayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
    }

    public LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new MarginLayoutParams(getContext(), attrs);
    }

    protected LayoutParams generateLayoutParams(LayoutParams lp) {
        return new MarginLayoutParams(lp);
    }
}


