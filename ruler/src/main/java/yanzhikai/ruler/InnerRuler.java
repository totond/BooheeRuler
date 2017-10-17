package yanzhikai.ruler;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.support.annotation.Px;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewTreeObserver;
import android.widget.OverScroller;

/**
 * 实现尺子的绘画，滑动处理，计算刻度
 */

public class InnerRuler extends View {
    private final String TAG = "ruler";
    private Context mContext;

    private Paint mSmallScalePaint, mBigScalePaint, mTextPaint;
    //最小最大刻度值(以0.1kg为单位)
    private int mMinScale = 464, mMaxScale = 2000;
    //当前刻度值
    private float mCurrentScale = 0;
    //最大刻度数
    private int mMaxLength = 0;
    //大小刻度的长度
    private float mSmallScaleLength = 30, mBigScaleLength = 60;
    //长度
    private int mLength, mMinPositionX = 0, mMaxPositionX = 0;
    //刻度间隔
    private float mInterval = 18;
    //手势监听
    private GestureDetector mGestureDetector;
    //控制滑动
    private OverScroller mOverScroller;
    //记录落点
    private float mLastX = 0, mLastMoveX = 0;
    //拖动阈值
    private int mTouchSlop;
    //惯性最大最小速度
    private int mMaximumVelocity, mMinimumVelocity;
    //速度获取
    private VelocityTracker mVelocityTracker;
    //一半宽度
    private int mHalfWidth = 0;
    //回调接口
    private RulerCallback mRulerCallback;


    public InnerRuler(Context context) {
        super(context);
        init(context);
    }

    public InnerRuler(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);

    }

    public InnerRuler(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context){
        mContext = context;

        mMaxLength = mMaxScale - mMinScale;
        mCurrentScale = (mMaxScale - mMinScale) / 2;

        initPaints();

        //Test
//        setOnClickListener(new OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                testTransition += 30;
////                setTranslationX(testTransition);
//                scrollTo(testTransition,0);
//                Log.d(TAG, "onClick: ");
//            }
//        });

        mVelocityTracker = VelocityTracker.obtain();
        mOverScroller = new OverScroller(mContext);

        mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
        mMaximumVelocity = ViewConfiguration.get(context)
                .getScaledMaximumFlingVelocity();
        mMinimumVelocity = ViewConfiguration.get(context)
                .getScaledMinimumFlingVelocity();

        //第一次进入，跳转到当前刻度
        getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                getViewTreeObserver().removeOnGlobalLayoutListener(this);
                goToScale(mCurrentScale);
            }
        });
    }

    private void initPaints() {
        mSmallScalePaint = new Paint();
        mSmallScalePaint.setStrokeWidth(3);
        mSmallScalePaint.setColor(getResources().getColor(R.color.colorGray));
        mSmallScalePaint.setStrokeCap(Paint.Cap.ROUND);;

        mBigScalePaint = new Paint();
        mBigScalePaint.setColor(getResources().getColor(R.color.colorGray));
        mBigScalePaint.setStrokeWidth(5);
        mBigScalePaint.setStrokeCap(Paint.Cap.ROUND);;

        mTextPaint = new Paint();
        mTextPaint.setStrokeWidth(3);
        mTextPaint.setTextSize(28);
        mTextPaint.setTextAlign(Paint.Align.CENTER);
//        mTextPaint.setStrokeJoin(Paint.Join.ROUND);


    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Log.d(TAG, "onDraw: ");
        canvas.save();
        drawScale(canvas);
        canvas.restore();

    }



    private void drawScale(Canvas canvas) {
        for (int i = mMinScale; i <= mMaxScale; i++){
            float locationX = (i - mMinScale) * mInterval;
            if (i % 10 == 0) {
                canvas.drawLine(locationX, 0, locationX, mBigScaleLength, mBigScalePaint);
                canvas.drawText(String.valueOf(i/10), locationX, 4 * mSmallScaleLength , mTextPaint);
            }else {
                canvas.drawLine(locationX, 0, locationX, mSmallScaleLength, mSmallScalePaint);
            }
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
//        return mGestureDetector.onTouchEvent(event);
        float currentX = event.getX();
        mVelocityTracker.addMovement(event);

        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                if (!mOverScroller.isFinished()) {
                    mOverScroller.abortAnimation();
                }
                mVelocityTracker.clear();
                mVelocityTracker.addMovement(event);
                mLastX = currentX;
                break;
            case MotionEvent.ACTION_MOVE:
                float moveX = mLastX - currentX;
                mLastX = currentX;
                scrollBy((int)(moveX),0);
                break;
            case MotionEvent.ACTION_UP:
                mVelocityTracker.computeCurrentVelocity(1000,mMaximumVelocity);
                int velocityX = (int) mVelocityTracker.getXVelocity();
                if (Math.abs(velocityX) > mMinimumVelocity)
                {
                    fling(-velocityX);
                }else {
                    scrollBackToScale();
                }
                mVelocityTracker.clear();

                break;
            case MotionEvent.ACTION_CANCEL:
                if (!mOverScroller.isFinished())
                {
                    mOverScroller.abortAnimation();
                }
                break;
        }
        return true;
    }

    private void fling(int vX){
        mOverScroller.fling(getScrollX(), 0, vX, 0, mMinPositionX, mMaxPositionX, 0, 0);
        invalidate();
    }

    @Override
    public void scrollTo(@Px int x, @Px int y) {
        if (x < mMinPositionX)
        {
            x = mMinPositionX;
        }
        if (x > mMaxPositionX)
        {
            x = mMaxPositionX;
        }
        if (x != getScrollX())
        {
            super.scrollTo(x, y);
        }

        mCurrentScale = scrollXtoScale(x);
        Log.d(TAG, "scrollTo: mCurrentScale  " + mCurrentScale);
        if (mRulerCallback != null){
            mRulerCallback.onScaleChanging(Math.round(mCurrentScale));
        }

    }

    public void goToScale(float scale){

        scrollTo(scaleToScrollX(scale),0);
        mCurrentScale = scale;
    }

    private float scrollXtoScale(int scrollX){
        return ((float) (scrollX + mHalfWidth) / mLength) *  mMaxLength + mMinScale;
    }

    private int scaleToScrollX(float scale){
        return (int) ((scale - mMinScale) / mMaxLength * mLength - mHalfWidth);
    }

    private void scrollBackToScale(){
        mCurrentScale = Math.round(mCurrentScale);
        mOverScroller.startScroll(getScrollX(),0,scaleToScrollX(mCurrentScale) - getScrollX(),0,1000);
        invalidate();

//        scrollTo(scaleToScrollX(mCurrentScale),0);
    }

    @Override
    public void computeScroll() {
        if (mOverScroller.computeScrollOffset()) {
            scrollTo(mOverScroller.getCurrX(), mOverScroller.getCurrY());
//            Log.d(TAG, "computeScroll: 执行");
            if (!mOverScroller.computeScrollOffset() && mCurrentScale != Math.round(mCurrentScale)){
                //Fling完进行一次检测回滚
                scrollBackToScale();
            }
            invalidate();
        }else {
//            Log.d(TAG, "computeScroll: 不执行");
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mLength = (int) ((mMaxScale - mMinScale) * mInterval);
        mHalfWidth = getMeasuredWidth()/2;
        mMinPositionX = -mHalfWidth;
        mMaxPositionX = mLength - mHalfWidth;
    }

    public void setCurrentScale(float currentScale) {
        this.mCurrentScale = currentScale;
    }

    public void setRulerCallback(RulerCallback RulerCallback) {
        this.mRulerCallback = RulerCallback;
    }

    public float getCurrentScale() {
        return mCurrentScale;
    }
}
