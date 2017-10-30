package yanzhikai.ruler;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.support.annotation.ColorInt;
import android.util.AttributeSet;
import android.util.Log;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;

/**
 * 用于包着尺子的外壳，用于画选取光标、外壳
 */

public class BooheeRuler extends ViewGroup {
    private final String TAG = "ruler";
    private Context mContext;
    //内部的尺子
    private InnerRuler mInnerRuler;
    //最小最大刻度值(以0.1kg为单位)
    private int mMinScale = 464, mMaxScale = 2000;
    //中间光标画笔
    private Paint mCPaint, mOutLinePaint;
    //光标宽度、高度
    private int mCursorWidth = 8, mCursorHeight = 70;
    //大小刻度的长度
    private int mSmallScaleLength = 30, mBigScaleLength = 60;
    //大小刻度的粗细
    private int mSmallScaleWidth = 3, mBigScaleWidth = 5;
    //数字字体大小
    private int mTextSize = 28;
    //数字Text距离顶部高度
    private int mTextMarginTop = 120;
    //刻度间隔
    private int mInterval = 18;
    //数字Text颜色
    private
    @ColorInt
    int mTextColor = getResources().getColor(R.color.colorLightBlack);
    //刻度颜色
    private
    @ColorInt
    int mScaleColor = getResources().getColor(R.color.colorGray);
    //初始的当前刻度
    private float mCurrentScale = 0;
    //一格大刻度多少格小刻度
    private int mCount = 10;
    //光标drawable
    private Drawable mCursorDrawable;
    //尺子两端的padding
    private int mPaddingStartAndEnd = 0;

    public BooheeRuler(Context context) {
        super(context);
        initRuler(context);

    }

    public BooheeRuler(Context context, AttributeSet attrs) {
        super(context, attrs);
        initAttrs(context, attrs);
        initRuler(context);

    }

    public BooheeRuler(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initAttrs(context, attrs);
        initRuler(context);

    }

    private void initAttrs(Context context, AttributeSet attrs) {
        TypedArray typedArray = context.getTheme().obtainStyledAttributes(attrs, R.styleable.BooheeRuler, 0, 0);
        mMinScale = typedArray.getInteger(R.styleable.BooheeRuler_minScale, mMinScale);
        mMaxScale = typedArray.getInteger(R.styleable.BooheeRuler_maxScale, mMaxScale);
        mCursorWidth = typedArray.getDimensionPixelSize(R.styleable.BooheeRuler_cursorWidth, mCursorWidth);
        mCursorHeight = typedArray.getDimensionPixelSize(R.styleable.BooheeRuler_cursorHeight, mCursorHeight);
        mSmallScaleWidth = typedArray.getDimensionPixelSize(R.styleable.BooheeRuler_smallScaleWidth, mSmallScaleWidth);
        mSmallScaleLength = typedArray.getDimensionPixelSize(R.styleable.BooheeRuler_smallScaleLength, mSmallScaleLength);
        mBigScaleWidth = typedArray.getDimensionPixelSize(R.styleable.BooheeRuler_bigScaleWidth, mBigScaleWidth);
        mBigScaleLength = typedArray.getDimensionPixelSize(R.styleable.BooheeRuler_bigScaleLength, mBigScaleLength);
        mTextSize = typedArray.getDimensionPixelSize(R.styleable.BooheeRuler_numberTextSize, mTextSize);
        mTextMarginTop = typedArray.getDimensionPixelSize(R.styleable.BooheeRuler_textMarginTop, mTextMarginTop);
        mInterval = typedArray.getDimensionPixelSize(R.styleable.BooheeRuler_scaleInterval, mInterval);
        mTextColor = typedArray.getColor(R.styleable.BooheeRuler_numberTextColor, mTextColor);
        mScaleColor = typedArray.getColor(R.styleable.BooheeRuler_scaleColor, mScaleColor);
        mCurrentScale = typedArray.getFloat(R.styleable.BooheeRuler_currentScale, (mMaxScale + mMinScale) / 2);
        mCount = typedArray.getInt(R.styleable.BooheeRuler_count, mCount);
        mCursorDrawable = typedArray.getDrawable(R.styleable.BooheeRuler_cursorDrawable);
        if (mCursorDrawable == null) {
            mCursorDrawable = getResources().getDrawable(R.drawable.cursor_shape);
        }
        mPaddingStartAndEnd = typedArray.getDimensionPixelSize(R.styleable.BooheeRuler_paddingStartAndEnd, mPaddingStartAndEnd);
        typedArray.recycle();
    }

    private void initRuler(Context context) {
        mContext = context;
        mInnerRuler = new InnerRuler(context, this);
        //设置全屏，加入InnerRuler
        LayoutParams layoutParams = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        mInnerRuler.setLayoutParams(layoutParams);
        addView(mInnerRuler);
        //设置ViewGroup可画
        setWillNotDraw(false);

        initPaint();
        initDrawable();
    }

    //在宽高初始化之后定义光标Drawable的边界
    private void initDrawable() {
        getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                getViewTreeObserver().removeOnPreDrawListener(this);
                mCursorDrawable.setBounds((getWidth() - mCursorWidth) / 2, 0
                        , (getWidth() + mCursorWidth) / 2, mCursorHeight);
                return false;
            }
        });

    }

    private void initPaint() {
        mCPaint = new Paint();

        mOutLinePaint = new Paint();
        mOutLinePaint.setStrokeWidth(0);
        mOutLinePaint.setColor(mScaleColor);

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        int newWidthSize = widthSize - mPaddingStartAndEnd * 2;
        if (newWidthSize <= 0) {
            Log.d(TAG, "mPaddingStartAndEnd设置过大，设置无效！");
            newWidthSize = widthSize;
        }
        super.onMeasure(MeasureSpec.makeMeasureSpec(newWidthSize, widthMode), MeasureSpec.makeMeasureSpec(heightSize, heightMode));
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //画上面的轮廓线
        canvas.drawLine(0, 0, canvas.getWidth(), 0, mOutLinePaint);
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
        //画中间的选定光标，要在这里画，因为dispatchDraw()执行在onDraw()后面，这样子光标才能不被尺子的刻度遮蔽
        mCursorDrawable.draw(canvas);

    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        mInnerRuler.layout(0, 0, r - l, b - t);
    }

    //设置回调
    public void setCallback(RulerCallback rulerCallback) {
        mInnerRuler.setRulerCallback(rulerCallback);

    }

    //设置当前进度
    public void setCurrentScale(float currentScale) {
        mCurrentScale = currentScale;
        mInnerRuler.setCurrentScale(currentScale);
    }

    //如果控件尺寸变化，中间光标的位置也要重新定义
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        initDrawable();
    }

    public float getCurrentScale() {
        return mCurrentScale;
    }

    public void setMinScale(int minScale) {
        this.mMinScale = minScale;
    }

    public int getMinScale() {
        return mMinScale;
    }

    public void setMaxScale(int maxScale) {
        this.mMaxScale = maxScale;
    }

    public int getMaxScale() {
        return mMaxScale;
    }

    public void setCursorWidth(int cursorWidth) {
        this.mCursorWidth = cursorWidth;
    }

    public int getCursorWidth() {
        return mCursorWidth;
    }

    public void setCursorHeight(int cursorHeight) {
        this.mCursorHeight = cursorHeight;
    }

    public int getCursorHeight() {
        return mCursorHeight;
    }


    public void setBigScaleLength(int bigScaleLength) {
        this.mBigScaleLength = bigScaleLength;
    }

    public int getBigScaleLength() {
        return mBigScaleLength;
    }

    public void setBigScaleWidth(int bigScaleWidth) {
        this.mBigScaleWidth = bigScaleWidth;
    }

    public int getBigScaleWidth() {
        return mBigScaleWidth;
    }

    public void setSmallScaleLength(int smallScaleLength) {
        this.mSmallScaleLength = smallScaleLength;
    }

    public int getSmallScaleLength() {
        return mSmallScaleLength;
    }

    public void setSmallScaleWidth(int smallScaleWidth) {
        this.mSmallScaleWidth = smallScaleWidth;
    }

    public int getSmallScaleWidth() {
        return mSmallScaleWidth;
    }

    public void setTextMarginTop(int textMarginTop) {
        this.mTextMarginTop = textMarginTop;
    }

    public int getTextMarginTop() {
        return mTextMarginTop;
    }

    public void setTextSize(int textSize) {
        this.mTextSize = textSize;
    }

    public int getTextSize() {
        return mTextSize;
    }

    public void setInterval(int interval) {
        this.mInterval = interval;
    }

    public int getInterval() {
        return mInterval;
    }

    public int getTextColor() {
        return mTextColor;
    }

    public int getScaleColor() {
        return mScaleColor;
    }

    public void setCount(int mCount) {
        this.mCount = mCount;
    }

    public int getCount() {
        return mCount;
    }
}
