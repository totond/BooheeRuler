package yanzhikai.ruler;

import android.content.Context;
import android.graphics.Canvas;
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
import android.widget.OverScroller;

/**
 * Created by yany on 2017/10/13.
 */

public class InnerRuler extends View {
    private final String TAG = "ruler";
    private Context mContext;

    private Paint mSmallScalePaint, mBigScalePaint, mCursorPaint, mTextPaint;
    //最小最大刻度值(以0.1kg为单位)
    private int mMinScale = 464, mMaxScale = 2000;
    //最大刻度数
    private int mMaxLength;
    //大小刻度的长度
    private float mSmallScaleLength = 30, mBigScaleLength = 60;
    //宽度
    private int mWidth;
    //刻度间隔
    private float mInterval = 12;
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
    //test
    private int testTransition = 0;


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
        mGestureDetector = new GestureDetector(context,new RulerGestureListener());
        mOverScroller = new OverScroller(mContext);

        mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
        mMaximumVelocity = ViewConfiguration.get(context)
                .getScaledMaximumFlingVelocity();
        mMinimumVelocity = ViewConfiguration.get(context)
                .getScaledMinimumFlingVelocity();
    }

    private void initPaints() {
        mSmallScalePaint = new Paint();
        mBigScalePaint = new Paint();
        mCursorPaint = new Paint();
        mTextPaint = new Paint();

        mSmallScalePaint.setStrokeWidth(3);
        mBigScalePaint.setStrokeWidth(5);
        mCursorPaint.setStrokeWidth(3);
        mTextPaint.setStrokeWidth(3);

        mTextPaint.setTextSize(28);

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Log.d(TAG, "onDraw: ");
        drawScale(canvas);
    }

    private void drawScale(Canvas canvas) {
        for (int i = mMinScale; i <= mMaxScale; i++){
            float locationX = (i - mMinScale) * mInterval;
            if (i % 10 == 0) {
                canvas.drawLine(locationX, 0, locationX, mBigScaleLength, mBigScalePaint);
                canvas.drawText(String.valueOf(i/10), locationX, mBigScaleLength + 30, mTextPaint);
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
        mOverScroller.fling(getScrollX(), 0, vX, 0, 0, mWidth, 0, 0);
        invalidate();
    }

    @Override
    public void scrollTo(@Px int x, @Px int y) {
        if (x < 0)
        {
            x = 0;
        }
        if (x > mWidth)
        {
            x = mWidth;
        }
        if (x != getScrollX())
        {
            super.scrollTo(x, y);
        }

    }

    @Override
    public void computeScroll() {
        if (mOverScroller.computeScrollOffset()) {
            scrollTo(mOverScroller.getCurrX(), mOverScroller.getCurrY());
//            Log.d(TAG, "computeScroll: 执行");
            invalidate();
        }else {
//            Log.d(TAG, "computeScroll: 不执行");
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mWidth = (int) (mMaxLength * mInterval);
    }

    private class RulerGestureListener extends GestureDetector.SimpleOnGestureListener{
        @Override
        public boolean onDown(MotionEvent e) {
            return true;
        }

        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {
            Log.d(TAG, "onSingleTapConfirmed: ");
            mOverScroller.fling(0,0,4,0,100,400,0,0);
            return super.onSingleTapConfirmed(e);
        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            Log.d(TAG, "onScroll: ");
            scrollBy((int) distanceX,0);
            return super.onScroll(e1, e2, distanceX, distanceY);
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            Log.d(TAG, "onFling: ");
            return super.onFling(e1, e2, velocityX, velocityY);
        }
    }
}
