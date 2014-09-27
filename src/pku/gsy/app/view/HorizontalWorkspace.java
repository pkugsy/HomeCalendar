package pku.gsy.app.view;
import pku.gsy.app.view.listener.SubviewSlideInListener;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.Scroller;

public class HorizontalWorkspace extends ViewGroup {
    private final static int INVALID_SCREEN = -1;
    private final static int TOUCH_STATE_REST = 0;
    private final static int TOUCH_STATE_SCROLLING = 1;
    private static final int SNAP_VELOCITY = 600;
    private static final float BASELINE_FLING_VELOCITY = 2500.f;
    
    private int mTouchState = TOUCH_STATE_REST;
    private int mCurrentScreen = 0;
    private int mNextScreen = INVALID_SCREEN;
    
    private int mWidth = 0;
    private int mHeight = 0;
    private boolean mFirstLayout = true;
    
    private float mDownX;
    private float mLastMotionX;
    private int mDirection;
    private int mTouchSlop;
    private int mMaximumVelocity;
    private int mSnapSlop = 100;
    private boolean mNotInterceptFlag = false;
    private boolean mAllowLongPress = true;
    
    private Scroller mScroller;
    private VelocityTracker mVelocityTracker = null;
    private ScreenHelper mScreenHelper;
    
    private SubviewSlideInListener beforeSlideListener = null;
    private SubviewSlideInListener afterSlideInListener = null;

    public HorizontalWorkspace(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }
    
    public HorizontalWorkspace(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        setHapticFeedbackEnabled(false);
        initWorkspace();
    }
    
    private void initWorkspace() {
        final Context context = getContext();
        mScroller = new Scroller(context);
        final ViewConfiguration configuration = ViewConfiguration.get(context);
        mTouchSlop = configuration.getScaledTouchSlop();
        mMaximumVelocity = configuration.getScaledMaximumFlingVelocity();
    }

    public void setBeforeSlideInListener(SubviewSlideInListener listener) {
        this.beforeSlideListener = listener;
    }
    
    public void setAfterSlideInListener(SubviewSlideInListener listener) {
        this.afterSlideInListener = listener;
    }
    
    @Override
    public void computeScroll() {
        if (mScroller.computeScrollOffset()) {
            scrollTo(mScroller.getCurrX(), mScroller.getCurrY());
            postInvalidate();
        } else if (mNextScreen != INVALID_SCREEN) {
            mCurrentScreen = mNextScreen;
            mNextScreen = INVALID_SCREEN;
            
            /*
             * Because mScroller has the max and min values, it should reset mScroller
             * when reach max or min.
             */
        }
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        final boolean fastDraw = (mTouchState != TOUCH_STATE_SCROLLING) && mNextScreen == INVALID_SCREEN;
        final long drawingTime = getDrawingTime();

        if (fastDraw && mCurrentScreen != INVALID_SCREEN) {
            drawChild(canvas, getChildAt(mCurrentScreen), drawingTime);
        } else {
            drawChild(canvas, getChildAt(0), drawingTime);
            drawChild(canvas, getChildAt(1), drawingTime);
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        /*
         * Measure dimension of Workspace and its children. Workspace can only
         * be used in EXACTLY mode. And its children must use the same size with
         * it. We use the first child as the default screen, so there needs no
         * scrollTo() current screen.
         */

        mWidth = MeasureSpec.getSize(widthMeasureSpec);
        final int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        if (widthMode != MeasureSpec.EXACTLY) {
            throw new IllegalStateException("Workspace can only be used in EXACTLY mode.");
        }

        mHeight = MeasureSpec.getSize(heightMeasureSpec);
        final int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        if (heightMode != MeasureSpec.EXACTLY) {
            throw new IllegalStateException("Workspace can only be used in EXACTLY mode.");
        }

        final int count = getChildCount();
        for (int i = 0; i < count; ++i) {
            getChildAt(i).measure(widthMeasureSpec, heightMeasureSpec);
        }
        mSnapSlop = (Math.min(mWidth, mHeight) >> 1);
        
        setMeasuredDimension(mWidth, mHeight);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        if (mFirstLayout) {
            mFirstLayout = false;

            mScreenHelper = new ScreenHelper(new ScreenInfo(0, 0, 0), new ScreenInfo(1, mWidth, 0));
        }

        mScreenHelper.updateLayout();
    }

    public View getCurrentScreen() {
        return getChildAt(mCurrentScreen);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        /**
         * This method JUST determines whether we want to intercept the motion.
         * If we return true, onTouchEvent will be called and we do the actual
         * scrolling there.
         */
        final int action = (ev.getAction() & MotionEvent.ACTION_MASK);
        if ((action == MotionEvent.ACTION_MOVE) && (mTouchState != TOUCH_STATE_REST)) {
            return true;
        }
        
        obtainVelocityTracker().addMovement(ev);
        
        switch (action) {
        case MotionEvent.ACTION_DOWN:
            mDownX = mLastMotionX = ev.getX();
            mDirection = 0;
            mNotInterceptFlag = false;
            mAllowLongPress = true;
            
            mTouchState = (mScroller.isFinished() ? TOUCH_STATE_REST : TOUCH_STATE_SCROLLING);
            break;
        case MotionEvent.ACTION_CANCEL:
        case MotionEvent.ACTION_UP:
            // if there are no move event occurred between down and up event.
            mTouchState = TOUCH_STATE_REST;
            mAllowLongPress = false;
            releaseVelocityTracker();
            break;
        case MotionEvent.ACTION_MOVE:
            final float x = ev.getX();
            final int xDiff = (int) Math.abs(x - mLastMotionX);

            final int touchSlop = mTouchSlop;

            if (xDiff > touchSlop) { // 启动水平方向的滑动
                mTouchState = TOUCH_STATE_SCROLLING;

                if (!mScroller.isFinished()) mScroller.abortAnimation();
            }

            if (mTouchState == TOUCH_STATE_SCROLLING && mAllowLongPress) {
                mAllowLongPress = false;
                getChildAt(mCurrentScreen).cancelLongPress();
            }

            break;
        } // End of switch
        
        return mTouchState != TOUCH_STATE_REST;
    }

    @SuppressLint("ClickableViewAccessibility")
	@Override
    public boolean onTouchEvent(MotionEvent ev) {
        /* 
         * MotionEvent here is for whole screen, not the Workspace.
         * So we should use relative coordinate instead of global here.
         */
        
        obtainVelocityTracker().addMovement(ev);

        final int action = (ev.getAction() & MotionEvent.ACTION_MASK);
        switch (action) {
        case MotionEvent.ACTION_DOWN:
            /*
             * See ACTION_DOWN process:
             *     When mScroller is not finished, it returns true to intercept this touch event.
             *     Then, it reaches here.
             *     Set mNotInterceptFlag true to release flow move events.
             */
            mNotInterceptFlag = true;
            break;
        case MotionEvent.ACTION_MOVE:
            if (mNotInterceptFlag) break;
            
            if (mTouchState == TOUCH_STATE_SCROLLING) {
                handleHorizontalMoveEvent(ev.getX());
            }
            break;
        case MotionEvent.ACTION_CANCEL:
        case MotionEvent.ACTION_UP:
            if (mTouchState == TOUCH_STATE_SCROLLING) {
                if (!mNotInterceptFlag) {
                    if (!mScroller.isFinished()) {
                        mScroller.abortAnimation();
                    }
                    
                    handleHorizontalUpEvent();
                }
                
                releaseVelocityTracker();
            }
            
            mTouchState = TOUCH_STATE_REST;
        }

        return true;
    }
    
    private final VelocityTracker obtainVelocityTracker() {
        if (null == mVelocityTracker) {
            mVelocityTracker = VelocityTracker.obtain();
        }
        
        return mVelocityTracker;
    }
    
    private final void releaseVelocityTracker() {
        if (null == mVelocityTracker) return;

        mVelocityTracker.recycle();
        mVelocityTracker = null;
    }

    private void handleHorizontalMoveEvent(float x) {
        final int deltaX = (int) (mLastMotionX - x);
        if (Math.abs(deltaX) < mTouchSlop) return;
        
        mLastMotionX = x;

        // direction 左为负，右为正 (初始化mDirection的方向为与滑动方向相反)
        if (mDirection == 0) mDirection = deltaX;

        // deltaX > 0, 即lastX在X的右侧，表明此时手指在向左滑动
        // 所以，scroll指针要向右移动
        if (x < mDownX) {
            // 页面向左滑动，要判断右侧是否会有缺页
            checkAndHandleBackPageFault(SubviewSlideInListener.DirectionLeft);
        } else {
            checkAndHandleFrontPageFault(SubviewSlideInListener.DirectionRight);
        }

        // 经过之前对页面是否缺页的维护，此处可以放心的scroll了。
        // scrollY 应该是当前view的纵向对其。注意这里是scollBy，而不是scrollTo
        scrollBy(deltaX, 0);
    }

    private void handleHorizontalUpEvent() {
        final VelocityTracker velocityTracker = mVelocityTracker;
        velocityTracker.computeCurrentVelocity(1000, mMaximumVelocity);
        
        final int velocityX = (int) Math.abs(velocityTracker.getXVelocity());
        final int scrollX = getScrollX();
        int newX = mScreenHelper.getChildById(mCurrentScreen).left;
        final int offsetX = Math.abs(newX - scrollX);
        mNextScreen = mCurrentScreen;
        /*
         * 根据velocityX和offsetX，即滑动速度和距离判断判断是否滑动到下一屏；
         * 根据scrollX和newX的相对位置判断滑动方向；
         */
        if (offsetX > mSnapSlop || velocityX > SNAP_VELOCITY) {
            mNextScreen ^= 0x01;
            
            // mNextScreen将可能在左侧，因为curLeft在对齐点scrollX的右侧
            if (newX >= scrollX) {
                newX -= mWidth;
                if (null != afterSlideInListener) {
                    afterSlideInListener.onSlide(getChildAt(mNextScreen), SubviewSlideInListener.DirectionRight);
                }
            }
            // mNextScreen将可能在右侧，因为curLeft在对齐点scrollX的左侧
            else {
                newX += mWidth;
                if (null != afterSlideInListener) {
                    afterSlideInListener.onSlide(getChildAt(mNextScreen), SubviewSlideInListener.DirectionLeft);
                }
            }
        }
        
        /*
         * duration与距离offsetX和速度velocityX相关，
         * 距离越大duration越长，速度越快duration越短。
         */
        final int delta = newX - scrollX;
        int duration = (Math.abs(delta) << 1);
        if (velocityX > BASELINE_FLING_VELOCITY) {
            duration *= BASELINE_FLING_VELOCITY / velocityX;
        }
        
        mScroller.startScroll(scrollX, getScrollY(), delta, 0, duration);
        invalidate();
    }

    /**
     * @param slideDirection: Left OR Up
     */
    private void checkAndHandleBackPageFault(int slideDirection) {
        if (mDirection > 0) {
            mDirection = -1;

            mScreenHelper.checkAndHandleBackPageFault(mCurrentScreen);

            if (null != beforeSlideListener) {
                int candidate = mCurrentScreen ^ 0x01;
                beforeSlideListener.onSlide(getChildAt(candidate), slideDirection);
            }
        }
    }

    /**
     * @param slideDirection: Right OR Down
     */
    private void checkAndHandleFrontPageFault(int slideDirection) {
        if (mDirection < 0) {
            mDirection = 1;

            mScreenHelper.checkAndHandleFrontPageFault(mCurrentScreen);

            if (null != beforeSlideListener) {
                int candidate = mCurrentScreen ^ 0x01;
                beforeSlideListener.onSlide(getChildAt(candidate), slideDirection);
            }
        }
    }

    public void snapToScreen(int direction) {
        if (!mScroller.isFinished() || direction == 0) {
            return;
        }

        mNextScreen = mCurrentScreen ^ 0x01;

        int snapDirection = direction < 0 ? 
                SubviewSlideInListener.DirectionLeft : SubviewSlideInListener.DirectionRight;

        if (null != beforeSlideListener) {
            beforeSlideListener.onSlide(getChildAt(mNextScreen), snapDirection);
        }

        if (direction < 0) {
            mScreenHelper.checkAndHandleBackPageFault(mCurrentScreen);
        } else {
            mScreenHelper.checkAndHandleFrontPageFault(mCurrentScreen);
        }

        if (null != afterSlideInListener) {
            afterSlideInListener.onSlide(getChildAt(mNextScreen), snapDirection);
        }
        
        final int delta = mScreenHelper.getChildById(mNextScreen).left - getScrollX();
        mScroller.startScroll(getScrollX(), getScrollY(), delta, 0, Math.abs(delta) * 2);
        invalidate();
    }

    public class ScreenInfo {
        public int id;
        public View view;

        public int left;
        public int top;

        public ScreenInfo(int id, int left, int top) {
            this.id = id;
            this.view = getChildAt(id);
            set(left, top);
        }

        public final void set(int left, int top) {
            this.left = left;
            this.top = top;
        }

        public final int getRight() {
            return (left + mWidth);
        }

        public final int getBottom() {
            return (top + mHeight);
        }

        public final void layout() {
            view.layout(left, top, getRight(), getBottom());
        }
    }

    public final class ScreenHelper {
        private ScreenInfo fScreen;
        private ScreenInfo bScreen;

        public ScreenHelper(ScreenInfo s1, ScreenInfo s2) {
            fScreen = s1;
            bScreen = s2;
        }

        public void updateLayout() {
            fScreen.layout();
            bScreen.layout();
        }

        public synchronized void checkAndHandleFrontPageFault(int curScreenId) {
            if (isAtFront(curScreenId))
                moveLastToFront();
        }

        public synchronized void checkAndHandleBackPageFault(int curScreenId) {
            if (isAtBack(curScreenId))
                moveFirstToBack();
        }

        public final ScreenInfo getChildById(int screenId) {
            return (screenId == fScreen.id ? fScreen : bScreen);
        }

        private final void moveLastToFront() {
            bScreen.left = fScreen.left - mWidth;

            bScreen.layout();

            ScreenInfo tp = fScreen;
            fScreen = bScreen;
            bScreen = tp;
        }

        private final void moveFirstToBack() {
            fScreen.left = bScreen.getRight();

            fScreen.layout();

            ScreenInfo tp = fScreen;
            fScreen = bScreen;
            bScreen = tp;
        }

        public final boolean isAtFront(int screenId) {
            return screenId == fScreen.id;
        }

        public final boolean isAtBack(int screenId) {
            return screenId == bScreen.id;
        }
    }
    
}
