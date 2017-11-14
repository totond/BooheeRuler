package yanzhikai.ruler;

import android.content.Context;
import android.support.annotation.Px;
import android.util.Log;
import android.view.MotionEvent;
import android.view.VelocityTracker;

/**
 * 实现尺子的绘画，滑动处理，计算刻度
 */

public class HorizontalRuler extends InnerRuler {
    private final String TAG = "ruler";
//    private Context mContext;
//    //画笔
//    private Paint mSmallScalePaint, mBigScalePaint, mTextPaint;
//    //当前刻度值
//    private float mCurrentScale = 0;
//    //最大刻度数
//    private int mMaxLength = 0;
//    //长度、最小可滑动值、最大可滑动值
//    private int mLength, mMinPosition = 0, mMaxPosition = 0;
//    //控制滑动
//    private OverScroller mOverScroller;
    //记录落点
    private float mLastX = 0;
    //拖动阈值,这里没有使用它，用了感觉体验不好
    private int mTouchSlop;
    //惯性最大最小速度
//    private int mMaximumVelocity, mMinimumVelocity;
//    //速度获取
//    private VelocityTracker mVelocityTracker;
    //一半宽度
    protected int mHalfWidth = 0;

//    //一格大刻度多少格小刻度
//    private int mCount = 10;
//    //提前刻画量
//    private int mDrawOffset = 0;

//    private BooheeRuler mParent;


    public HorizontalRuler(Context context, BooheeRuler booheeRuler) {
        super(context,booheeRuler);
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

        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                if (!mOverScroller.isFinished()) {
                    mOverScroller.abortAnimation();
                }

                mLastX = currentX;
                break;
            case MotionEvent.ACTION_MOVE:
                float moveX = mLastX - currentX;
                mLastX = currentX;
                scrollBy((int)(moveX),0);
                break;
            case MotionEvent.ACTION_UP:
                //处理松手后的Fling
                mVelocityTracker.computeCurrentVelocity(1000,mMaximumVelocity);
                int velocityX = (int) mVelocityTracker.getXVelocity();
                if (Math.abs(velocityX) > mMinimumVelocity)
                {
                    fling(-velocityX);
                }else {
                    scrollBackToCurrentScale();
                }
                //VelocityTracker回收
                if (mVelocityTracker != null) {
                    mVelocityTracker.recycle();
                    mVelocityTracker = null;
                }
                break;
            case MotionEvent.ACTION_CANCEL:
                if (!mOverScroller.isFinished())
                {
                    mOverScroller.abortAnimation();
                }
                //VelocityTracker回收
                if (mVelocityTracker != null) {
                    mVelocityTracker.recycle();
                    mVelocityTracker = null;
                }
                break;
        }
        return true;
    }

    private void fling(int vX){
        mOverScroller.fling(getScrollX(), 0, vX, 0, mMinPosition, mMaxPosition, 0, 0);
        invalidate();
    }

    //重写滑动方法，设置到边界的时候不滑。滑动完输出刻度
    @Override
    public void scrollTo(@Px int x, @Px int y) {
        if (x < mMinPosition)
        {
            x = mMinPosition;
        }
        if (x > mMaxPosition)
        {
            x = mMaxPosition;
        }
        if (x != getScrollX())
        {
            super.scrollTo(x, y);
        }

        mCurrentScale = scrollXtoScale(x);
        if (mRulerCallback != null){
            mRulerCallback.onScaleChanging(Math.round(mCurrentScale));
        }

    }

    //直接跳转到当前刻度
    public void goToScale(float scale){
        mCurrentScale = Math.round(scale);
        scrollTo(scaleToScrollX(mCurrentScale),0);
        if (mRulerCallback != null){
            mRulerCallback.onScaleChanging(mCurrentScale);
        }
    }

    //把滑动偏移量scrollX转化为刻度Scale
    private float scrollXtoScale(int scrollX){
        return ((float) (scrollX - mMinPosition) / mLength) *  mMaxLength + mParent.getMinScale();
    }

    //把Scale转化为ScrollX
    private int scaleToScrollX(float scale){
        return (int) ((scale - mParent.getMinScale()) / mMaxLength * mLength + mMinPosition);
    }

    //把移动后光标对准距离最近的刻度，就是回弹到最近刻度
    private void scrollBackToCurrentScale(){
        //渐变回弹
        mCurrentScale = Math.round(mCurrentScale);
        mOverScroller.startScroll(getScrollX(),0,scaleToScrollX(mCurrentScale) - getScrollX(),0,1000);
        invalidate();

        //立刻回弹
//        scrollTo(scaleToScrollX(mCurrentScale),0);
    }

    @Override
    public void computeScroll() {
        if (mOverScroller.computeScrollOffset()) {
            scrollTo(mOverScroller.getCurrX(), mOverScroller.getCurrY());
            Log.d(TAG, "getScrollX: " + getScrollX());

            //这是最后OverScroller的最后一次滑动，如果这次滑动完了mCurrentScale不是整数，则把尺子移动到最近的整数位置
            if (!mOverScroller.computeScrollOffset() && mCurrentScale != Math.round(mCurrentScale)){
                //Fling完进行一次检测回滚
                scrollBackToCurrentScale();
            }
            invalidate();
        }
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
