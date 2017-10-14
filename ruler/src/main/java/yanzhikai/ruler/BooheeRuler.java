package yanzhikai.ruler;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by yany on 2017/10/13.
 */

public class BooheeRuler extends View {
    private final String TAG = "ruler";
    private Context mContext;

    private Paint mSmallScalePaint, mBigScalePaint, mCursorPaint, mTextPaint;
    //最小最大刻度
    private float mMinScale = 46.4f, mMaxScale = 200.0f;
    //最大长度
    private int mMaxLength;
    //大小刻度的长度
    private float mSmallScaleLength = 30, mBigScaleLength = 60;
    //刻度间隔
    private float mInterval = 12;
    //手势监听
    private GestureDetector mGestureDetector;
    //test
    private int testTransition = 0;


    public BooheeRuler(Context context) {
        super(context);
        init(context);
    }

    public BooheeRuler(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public BooheeRuler(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context){
        mContext = context;

        mMaxLength = (int) ((mMaxScale - mMinScale) * 10);

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

        mGestureDetector = new GestureDetector(context,new RulerGestureListener());
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
        for (int i = 0; i < mMaxLength; i++){
            float locationX = i * mInterval;

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
        Log.d(TAG, "onTouchEvent: ");
        return mGestureDetector.onTouchEvent(event);
    }

    private class RulerGestureListener extends GestureDetector.SimpleOnGestureListener{
        @Override
        public boolean onDown(MotionEvent e) {
            return true;
        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            Log.d(TAG, "onScroll: ");
            scrollBy((int) distanceX,0);
            return super.onScroll(e1, e2, distanceX, distanceY);
        }
    }
}
