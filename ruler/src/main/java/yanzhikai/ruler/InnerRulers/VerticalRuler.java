package yanzhikai.ruler.InnerRulers;

import android.content.Context;
import android.support.annotation.Px;
import android.util.Log;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.ViewGroup;

import yanzhikai.ruler.BooheeRuler;

/**
 * 垂直尺子抽象类
 */

public abstract class VerticalRuler extends InnerRuler {
    private final String TAG = "ruler";
    //记录落点
    private float mLastY = 0;
    //一半高度
    protected int mHalfHeight = 0;

    public VerticalRuler(Context context, BooheeRuler booheeRuler) {
        super(context, booheeRuler);
    }

    //处理滑动，主要是触摸的时候通过计算现在的event坐标和上一个的位移量来决定scrollBy()的多少
    //滑动完之后计算速度是否满足Fling，满足则使用OverScroller来计算Fling滑动
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float currentY = event.getY();
        //开始速度检测
        if (mVelocityTracker == null) {
            mVelocityTracker = VelocityTracker.obtain();
        }
        mVelocityTracker.addMovement(event);
        ViewGroup parent = (ViewGroup) getParent();//为了解决刻度尺在scrollview这种布局里面滑动冲突问题
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (!mOverScroller.isFinished()) {
                    mOverScroller.abortAnimation();
                }

                mLastY = currentY;
                parent.requestDisallowInterceptTouchEvent(true);//按下时开始让父控件不要处理任何touch事件
                break;
            case MotionEvent.ACTION_MOVE:
                float moveY = mLastY - currentY;
                mLastY = currentY;
                scrollBy(0, (int) (moveY));
                break;
            case MotionEvent.ACTION_UP:
                //处理松手后的Fling
                mVelocityTracker.computeCurrentVelocity(1000, mMaximumVelocity);
                int velocityY = (int) mVelocityTracker.getYVelocity();
                if (Math.abs(velocityY) > mMinimumVelocity) {
                    fling(-velocityY);
                } else {
                    scrollBackToCurrentScale();
                }
                //VelocityTracker回收
                if (mVelocityTracker != null) {
                    mVelocityTracker.recycle();
                    mVelocityTracker = null;
                }
                releaseEdgeEffects();
                parent.requestDisallowInterceptTouchEvent(false);//按下时开始让父控件不要处理任何touch事件
                break;
            case MotionEvent.ACTION_CANCEL:
                if (!mOverScroller.isFinished()) {
                    mOverScroller.abortAnimation();
                }
                scrollBackToCurrentScale();
                //VelocityTracker回收
                if (mVelocityTracker != null) {
                    mVelocityTracker.recycle();
                    mVelocityTracker = null;
                }
                releaseEdgeEffects();
                parent.requestDisallowInterceptTouchEvent(false);//按下时开始让父控件不要处理任何touch事件
                break;
        }
        return true;
    }

    private void fling(int vY) {
        mOverScroller.fling(0, getScrollY(), 0, vY, 0, 0, mMinPosition - mEdgeLength, mMaxPosition + mEdgeLength);
        invalidate();
    }

    //重写滑动方法，设置到边界的时候不滑。滑动完输出刻度
    @Override
    public void scrollTo(@Px int x, @Px int y) {
        if (y < mMinPosition) {
            goStartEdgeEffect(y);
            y = mMinPosition;
        }
        if (y > mMaxPosition) {
            goEndEdgeEffect(y);
            y = mMaxPosition;
        }
        if (y != getScrollY()) {
            super.scrollTo(x, y);
        }

        mCurrentScale = scrollYtoScale(y);
        if (mRulerCallback != null) {
            mRulerCallback.onScaleChanging(Math.round(mCurrentScale));
        }

    }

    //头部边缘效果处理
    private void goStartEdgeEffect(int y) {
        if (mParent.canEdgeEffect()) {
            if (!mOverScroller.isFinished()) {
                mStartEdgeEffect.onAbsorb((int) mOverScroller.getCurrVelocity());
                mOverScroller.abortAnimation();
            } else {
                mStartEdgeEffect.onPull((float) (mMinPosition - y) / (mEdgeLength) * 3 + 0.3f);
                mStartEdgeEffect.setSize(mParent.getCursorWidth(), getHeight());
            }
            postInvalidateOnAnimation();
        }
    }

    //尾部边缘效果处理
    private void goEndEdgeEffect(int y) {
        if (mParent.canEdgeEffect()) {
            if (!mOverScroller.isFinished()) {
                mEndEdgeEffect.onAbsorb((int) mOverScroller.getCurrVelocity());
                mOverScroller.abortAnimation();
            } else {
                mEndEdgeEffect.onPull((float) (y - mMaxPosition) / (mEdgeLength) * 3 + 0.3f);
                mEndEdgeEffect.setSize(mParent.getCursorWidth(), getHeight());
            }
            postInvalidateOnAnimation();
        }
    }

    //取消边缘效果动画
    private void releaseEdgeEffects() {
        if (mParent.canEdgeEffect()) {
            mStartEdgeEffect.onRelease();
            mEndEdgeEffect.onRelease();
        }
    }

    //直接跳转到当前刻度
    public void goToScale(float scale) {
        mCurrentScale = Math.round(scale);
        scrollTo(0, scaleToScrollY(mCurrentScale));
//        if (mRulerCallback != null) {
//            mRulerCallback.onScaleChanging(mCurrentScale);
//        }
    }

    //把滑动偏移量scrollY转化为刻度Scale
    private float scrollYtoScale(int scrollY) {
        return ((float) (scrollY - mMinPosition) / mLength) * mMaxLength + mParent.getMinScale();
    }

    //把Scale转化为ScrollY
    private int scaleToScrollY(float scale) {
        return Math.round((scale - mParent.getMinScale()) / mMaxLength * mLength + mMinPosition);
    }

    //把Scale转化为ScrollY,放大SCALE_TO_PX_FACTOR倍，以免精度丢失问题
    private float scaleToScrollFloatY(float scale) {
        return ((scale - mParent.getMinScale()) / mMaxLength * mLength * SCALE_TO_PX_FACTOR + mMinPosition * SCALE_TO_PX_FACTOR);
    }

    //把移动后光标对准距离最近的刻度，就是回弹到最近刻度
    @Override
    protected void scrollBackToCurrentScale() {
        scrollBackToCurrentScale(Math.round(mCurrentScale));
    }

    @Override
    protected void scrollBackToCurrentScale(int currentIntScale) {
        int dy = Math.round((scaleToScrollFloatY(currentIntScale) - SCALE_TO_PX_FACTOR * getScrollY()) / SCALE_TO_PX_FACTOR);
        if (dy > minScrollerPx) {
            //渐变回弹
            mOverScroller.startScroll(getScrollX(), getScrollY(), 0, dy, 500);
            invalidate();
        } else {
            //立刻回弹
            scrollBy(0, dy);
        }

    }

    @Override
    public void refreshSize() {
        mLength = (mParent.getMaxScale() - mParent.getMinScale()) * mParent.getInterval();
        mHalfHeight = getHeight() / 2;
        mMinPosition = -mHalfHeight;
        mMaxPosition = mLength - mHalfHeight;
    }

    //获取控件宽高，设置相应信息
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        refreshSize();
    }


}
