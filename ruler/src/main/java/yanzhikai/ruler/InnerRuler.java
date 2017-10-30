package yanzhikai.ruler;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.annotation.Px;
import android.util.AttributeSet;
import android.util.Log;
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
    //画笔
    private Paint mSmallScalePaint, mBigScalePaint, mTextPaint;
    //当前刻度值
    private float mCurrentScale = 0;
    //最大刻度数
    private int mMaxLength = 0;
    //长度、最小可滑动值、最大可滑动值
    private int mLength, mMinPositionX = 0, mMaxPositionX = 0;
    //控制滑动
    private OverScroller mOverScroller;
    //记录落点
    private float mLastX = 0;
    //拖动阈值,这里没有使用它，用了感觉体验不好
    private int mTouchSlop;
    //惯性最大最小速度
    private int mMaximumVelocity, mMinimumVelocity;
    //速度获取
    private VelocityTracker mVelocityTracker;
    //一半宽度
    private int mHalfWidth = 0;
    //回调接口
    private RulerCallback mRulerCallback;
    //一格大刻度多少格小刻度
    private int mCount = 10;
    //提前刻画量
    private int mDrawOffset = 0;

    private BooheeRuler mParent;


    public InnerRuler(Context context, BooheeRuler booheeRuler) {
        super(context);
        mParent = booheeRuler;
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

        mMaxLength = mParent.getMaxScale() - mParent.getMinScale();
        mCurrentScale = mParent.getCurrentScale();
        mCount = mParent.getCount();
        mDrawOffset = mCount * mParent.getInterval() / 2;

        initPaints();

        mOverScroller = new OverScroller(mContext);

//        mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
        //配置速度
        mVelocityTracker = VelocityTracker.obtain();
        mMaximumVelocity = ViewConfiguration.get(context)
                .getScaledMaximumFlingVelocity();
        mMinimumVelocity = ViewConfiguration.get(context)
                .getScaledMinimumFlingVelocity();

        //第一次进入，跳转到设定刻度
        getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                getViewTreeObserver().removeOnGlobalLayoutListener(this);
                goToScale(mCurrentScale);
            }
        });
        checkAPILevel();
    }

    //初始化画笔
    private void initPaints() {
        mSmallScalePaint = new Paint();
        mSmallScalePaint.setStrokeWidth(mParent.getSmallScaleWidth());
        mSmallScalePaint.setColor(mParent.getScaleColor());
        mSmallScalePaint.setStrokeCap(Paint.Cap.ROUND);;

        mBigScalePaint = new Paint();
        mBigScalePaint.setColor(mParent.getScaleColor());
        mBigScalePaint.setStrokeWidth(mParent.getBigScaleWidth());
        mBigScalePaint.setStrokeCap(Paint.Cap.ROUND);;

        mTextPaint = new Paint();
        mTextPaint.setAntiAlias(true);
        mTextPaint.setColor(mParent.getTextColor());
        mTextPaint.setTextSize(mParent.getTextSize());
        mTextPaint.setTextAlign(Paint.Align.CENTER);
//        mTextPaint.setStrokeJoin(Paint.Join.ROUND);

    }

    //API小于18则关闭硬件加速，否则setAntiAlias()方法不生效
    private void checkAPILevel(){
        if (Build.VERSION.SDK_INT < 18){
            setLayerType(LAYER_TYPE_NONE,null);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawScale(canvas);
    }

    //画刻度和字
    private void drawScale(Canvas canvas) {
        for (float i = mParent.getMinScale(); i <= mParent.getMaxScale(); i++){
            float locationX = (i - mParent.getMinScale()) * mParent.getInterval();

            if (locationX > getScrollX() - mDrawOffset && locationX < (getScrollX() + canvas.getWidth() + mDrawOffset)) {
                if (i % mCount == 0) {
                    canvas.drawLine(locationX, 0, locationX, mParent.getBigScaleLength(), mBigScalePaint);
                    canvas.drawText(String.valueOf(i / 10), locationX, mParent.getTextMarginTop(), mTextPaint);
                } else {
                    canvas.drawLine(locationX, 0, locationX, mParent.getSmallScaleLength(), mSmallScalePaint);
                }
            }
        }
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
        mOverScroller.fling(getScrollX(), 0, vX, 0, mMinPositionX, mMaxPositionX, 0, 0);
        invalidate();
    }

    //重写滑动方法，设置到边界的时候不滑。滑动完输出刻度
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
        return ((float) (scrollX - mMinPositionX) / mLength) *  mMaxLength + mParent.getMinScale();
    }

    //把Scale转化为ScrollX
    private int scaleToScrollX(float scale){
        return (int) ((scale - mParent.getMinScale()) / mMaxLength * mLength + mMinPositionX);
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
        mMinPositionX = -mHalfWidth;
        mMaxPositionX = mLength - mHalfWidth;
    }

    //设置尺子当前刻度
    public void setCurrentScale(float currentScale) {
        this.mCurrentScale = currentScale;
        goToScale(mCurrentScale);
    }

    public void setRulerCallback(RulerCallback RulerCallback) {
        this.mRulerCallback = RulerCallback;
    }

    public float getCurrentScale() {
        return mCurrentScale;
    }

}
