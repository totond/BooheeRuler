package yanzhikai.ruler.InnerRulers;

import android.content.Context;
import android.graphics.Paint;
import android.os.Build;
import android.util.Log;
import android.util.TypedValue;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewTreeObserver;
import android.widget.EdgeEffect;
import android.widget.OverScroller;

import yanzhikai.ruler.BooheeRuler;
import yanzhikai.ruler.RulerCallback;

/**
 * 内部尺子抽象类
 */

public abstract class InnerRuler extends View {
    public static final String TAG = "ruler";
    protected Context mContext;
    protected BooheeRuler mParent;

    //加入放大倍数来防止精度丢失而导致无限绘制
    protected static final int SCALE_TO_PX_FACTOR = 100;
    //惯性回滚最小偏移值，小于这个值就应该直接滑动到目的点
    protected static final int MIN_SCROLLER_DP = 1;
    protected float minScrollerPx = MIN_SCROLLER_DP;

    protected Paint mSmallScalePaint, mBigScalePaint, mTextPaint, mOutLinePaint;
    //当前刻度值
    protected float mCurrentScale = 0;
    //最大刻度数
    protected int mMaxLength = 0;
    //长度、最小可滑动值、最大可滑动值
    protected int mLength, mMinPosition = 0, mMaxPosition = 0;
    //控制滑动
    protected OverScroller mOverScroller;
    //一格大刻度多少格小刻度
    protected int mCount = 10;
    //提前刻画量
    protected int mDrawOffset;
    //速度获取
    protected VelocityTracker mVelocityTracker;
    //惯性最大最小速度
    protected int mMaximumVelocity, mMinimumVelocity;
    //回调接口
    protected RulerCallback mRulerCallback;
    //边界效果
    protected EdgeEffect mStartEdgeEffect,mEndEdgeEffect;
    //边缘效应长度
    protected int mEdgeLength;

    public InnerRuler(Context context, BooheeRuler booheeRuler) {
        super(context);
        mParent = booheeRuler;
        init(context);
    }

    public void init(Context context){
        mContext = context;

        mMaxLength = mParent.getMaxScale() - mParent.getMinScale();
        mCurrentScale = mParent.getCurrentScale();
        mCount = mParent.getCount();
        mDrawOffset = mCount * mParent.getInterval() / 2;

        minScrollerPx = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, MIN_SCROLLER_DP, context.getResources().getDisplayMetrics());

        initPaints();

        mOverScroller = new OverScroller(mContext);

//        mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
        //配置速度
        mVelocityTracker = VelocityTracker.obtain();
        mMaximumVelocity = ViewConfiguration.get(context)
                .getScaledMaximumFlingVelocity();
        mMinimumVelocity = ViewConfiguration.get(context)
                .getScaledMinimumFlingVelocity();

        initEdgeEffects();

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
        mSmallScalePaint.setStrokeCap(Paint.Cap.ROUND);

        mBigScalePaint = new Paint();
        mBigScalePaint.setColor(mParent.getScaleColor());
        mBigScalePaint.setStrokeWidth(mParent.getBigScaleWidth());
        mBigScalePaint.setStrokeCap(Paint.Cap.ROUND);

        mTextPaint = new Paint();
        mTextPaint.setAntiAlias(true);
        mTextPaint.setColor(mParent.getTextColor());
        mTextPaint.setTextSize(mParent.getTextSize());
        mTextPaint.setTextAlign(Paint.Align.CENTER);
//        mTextPaint.setStrokeJoin(Paint.Join.ROUND);
        mOutLinePaint = new Paint();
        mOutLinePaint.setStrokeWidth(mParent.getOutLineWidth());
        mOutLinePaint.setAntiAlias(true);
        mOutLinePaint.setColor(mParent.getScaleColor());
    }

    //初始化边缘效果
    public void initEdgeEffects(){
        if (mParent.canEdgeEffect()) {
            if (mStartEdgeEffect == null || mEndEdgeEffect == null) {
                mStartEdgeEffect = new EdgeEffect(mContext);
                mEndEdgeEffect = new EdgeEffect(mContext);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    mStartEdgeEffect.setColor(mParent.getEdgeColor());
                    mEndEdgeEffect.setColor(mParent.getEdgeColor());
                }
                mEdgeLength = mParent.getCursorHeight() + mParent.getInterval() * mParent.getCount();
            }
        }
    }


    //API小于18则关闭硬件加速，否则setAntiAlias()方法不生效
    private void checkAPILevel(){
        if (Build.VERSION.SDK_INT < 18){
            setLayerType(LAYER_TYPE_NONE,null);
        }
    }

    @Override
    public void computeScroll() {
        if (mOverScroller.computeScrollOffset()) {
            scrollTo(mOverScroller.getCurrX(), mOverScroller.getCurrY());
            //这是最后OverScroller的最后一次滑动，如果这次滑动完了mCurrentScale不是整数，则把尺子移动到最近的整数位置

            if (!mOverScroller.computeScrollOffset()){
                int currentIntScale = Math.round(mCurrentScale);
                if ((Math.abs(mCurrentScale - currentIntScale) > 0.001f)) {
                    //Fling完进行一次检测回滚
                    scrollBackToCurrentScale(currentIntScale);
                }
            }
            postInvalidate();
        }
    }

    protected abstract void scrollBackToCurrentScale();
    protected abstract void scrollBackToCurrentScale(int currentIntScale);
    protected abstract void goToScale(float scale);
    public abstract void refreshSize();

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
