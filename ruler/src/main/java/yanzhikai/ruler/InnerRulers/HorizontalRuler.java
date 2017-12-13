package yanzhikai.ruler.InnerRulers;

import android.content.Context;
import android.support.annotation.Px;
import android.util.Log;
import android.view.MotionEvent;
import android.view.VelocityTracker;

import yanzhikai.ruler.BooheeRuler;

/**
 * 水平尺子抽象类
 */

public abstract class HorizontalRuler extends InnerRuler {
    private final String TAG = "ruler";
    private float mLastX = 0;
    //拖动阈值,这里没有使用它，用了感觉体验不好
    private int mTouchSlop;
    //一半宽度
    protected int mHalfWidth = 0;


    public HorizontalRuler(Context context, BooheeRuler booheeRuler) {
        super(context, booheeRuler);
    }


    //处理滑动，主要是触摸的时候通过计算现在的event坐标和上一个的位移量来决定scrollBy()的多少
    //滑动完之后计算速度是否满足Fling，满足则使用OverScroller来计算Fling滑动
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float currentX = event.getX();
        //开始速度检测
        if (mVelocityTracker == null) {
            mVelocityTracker = VelocityTracker.obtain();
        }
        mVelocityTracker.addMovement(event);

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (!mOverScroller.isFinished()) {
                    mOverScroller.abortAnimation();
                }

                mLastX = currentX;
                break;
            case MotionEvent.ACTION_MOVE:
                float moveX = mLastX - currentX;
                mLastX = currentX;
                scrollBy((int) (moveX), 0);
                break;
            case MotionEvent.ACTION_UP:
                //处理松手后的Fling
                mVelocityTracker.computeCurrentVelocity(1000, mMaximumVelocity);
                int velocityX = (int) mVelocityTracker.getXVelocity();
                if (Math.abs(velocityX) > mMinimumVelocity) {
                    fling(-velocityX);
                } else {
                    scrollBackToCurrentScale();
                }
                //VelocityTracker回收
                if (mVelocityTracker != null) {
                    mVelocityTracker.recycle();
                    mVelocityTracker = null;
                }
                releaseEdgeEffects();
                break;
            case MotionEvent.ACTION_CANCEL:
                if (!mOverScroller.isFinished()) {
                    mOverScroller.abortAnimation();
                }
                //回滚到整点刻度
                scrollBackToCurrentScale();
                //VelocityTracker回收
                if (mVelocityTracker != null) {
                    mVelocityTracker.recycle();
                    mVelocityTracker = null;
                }
                releaseEdgeEffects();
                break;
        }
        return true;
    }

    private void fling(int vX) {
        mOverScroller.fling(getScrollX(), 0, vX, 0, mMinPosition - mEdgeLength, mMaxPosition + mEdgeLength, 0, 0);
        invalidate();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    //重写滑动方法，设置到边界的时候不滑,并显示边缘效果。滑动完输出刻度。
    @Override
    public void scrollTo(@Px int x, @Px int y) {
        if (x < mMinPosition) {
            goStartEdgeEffect(x);
            x = mMinPosition;
        }
        if (x > mMaxPosition) {
            goEndEdgeEffect(x);
            x = mMaxPosition;
        }
        if (x != getScrollX()) {
            super.scrollTo(x, y);
        }

        mCurrentScale = scrollXtoScale(x);
        if (mRulerCallback != null) {
            mRulerCallback.onScaleChanging(Math.round(mCurrentScale));
        }

    }

    //头部边缘效果处理
    private void goStartEdgeEffect(int x){
        if (mParent.canEdgeEffect()) {
            if (!mOverScroller.isFinished()) {
                mStartEdgeEffect.onAbsorb((int) mOverScroller.getCurrVelocity());
                mOverScroller.abortAnimation();
            } else {
                mStartEdgeEffect.onPull((float) (mMinPosition - x) / (mEdgeLength) * 3 + 0.3f);
                mStartEdgeEffect.setSize(mParent.getCursorHeight(), getWidth());
            }
            postInvalidateOnAnimation();
        }
    }

    //尾部边缘效果处理
    private void goEndEdgeEffect(int x){
        if(mParent.canEdgeEffect()) {
            if (!mOverScroller.isFinished()) {
                mEndEdgeEffect.onAbsorb((int) mOverScroller.getCurrVelocity());
                mOverScroller.abortAnimation();
            } else {
                mEndEdgeEffect.onPull((float) (x - mMaxPosition) / (mEdgeLength) * 3 + 0.3f);
                mEndEdgeEffect.setSize(mParent.getCursorHeight(), getWidth());
            }
            postInvalidateOnAnimation();
        }
    }

    //取消边缘效果动画
    private void releaseEdgeEffects(){
        if (mParent.canEdgeEffect()) {
            mStartEdgeEffect.onRelease();
            mEndEdgeEffect.onRelease();
        }
    }

    //直接跳转到当前刻度
    public void goToScale(float scale) {
        mCurrentScale = Math.round(scale);
        scrollTo(scaleToScrollX(mCurrentScale), 0);
//        if (mRulerCallback != null) {
//            mRulerCallback.onScaleChanging(mCurrentScale);
//        }
    }

    //把滑动偏移量scrollX转化为刻度Scale
    //TODO 转化大刻度（1000k以上）的时候会很卡，后面有时间再尝试缓存或者分级处理
    private float scrollXtoScale(int scrollX) {
//        Log.d(TAG, "scrollXtoScale: " + scrollX);
        return ((float) (scrollX - mMinPosition) / mLength) * mMaxLength + mParent.getMinScale();
    }

    //把Scale转化为ScrollX
    private int scaleToScrollX(float scale) {
//        Log.d(TAG, "scaleToScrollX: ");
        return (int) ((scale - mParent.getMinScale()) / mMaxLength * mLength + mMinPosition);
    }


    //把移动后光标对准距离最近的刻度，就是回弹到最近刻度
    @Override
    protected void scrollBackToCurrentScale() {
        //渐变回弹
//        Log.d(TAG, "scrollBackToCurrentScale: ");
        mOverScroller.startScroll(getScrollX(), 0, scaleToScrollX(Math.round(mCurrentScale)) - getScrollX(), 0, 500);
        invalidate();

        //立刻回弹
//        scrollTo(scaleToScrollX(mCurrentScale),0);
    }


    //获取控件宽高，设置相应信息
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mLength = (mParent.getMaxScale() - mParent.getMinScale()) * mParent.getInterval();
        mHalfWidth = w / 2;
        mMinPosition = -mHalfWidth;
        mMaxPosition = mLength - mHalfWidth;
    }


}
