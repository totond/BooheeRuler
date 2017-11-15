package yanzhikai.ruler;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.support.annotation.ColorInt;
import android.support.annotation.IntDef;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import yanzhikai.ruler.InnerRulers.BottomHeadRuler;
import yanzhikai.ruler.InnerRulers.InnerRuler;
import yanzhikai.ruler.InnerRulers.LeftHeadRuler;
import yanzhikai.ruler.InnerRulers.RightHeadRuler;
import yanzhikai.ruler.InnerRulers.TopHeadRuler;

/**
 * 用于包着尺子的外壳，用于画选取光标、外壳
 */

public class BooheeRuler extends ViewGroup {
    private final String TAG = "ruler";
    private Context mContext;
    //尺子Style定义
    public static final int TOP_HEAD = 1 , BOTTOM_HEAD = 2, LEFT_HEAD = 3, RIGHT_HEAD = 4;
    @IntDef({TOP_HEAD, BOTTOM_HEAD, LEFT_HEAD, RIGHT_HEAD})
    @Retention(RetentionPolicy.SOURCE)
    public  @interface RulerStyle {}
    private @BooheeRuler.RulerStyle int mStyle = TOP_HEAD;
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
    private int mTextMarginHead = 120;
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
    private int mPaddingLeft = 0,mPaddingTop = 0,mPaddingRight = 0,mPaddingBottom = 0;
    //尺子背景
    private Drawable mRulerBackGround;
    private int mRulerBackGroundColor = getResources().getColor(R.color.colorDirtyWithe);

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

    @SuppressWarnings("WrongConstant")
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
        mTextMarginHead = typedArray.getDimensionPixelSize(R.styleable.BooheeRuler_textMarginHead, mTextMarginHead);
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
        mStyle = typedArray.getInt(R.styleable.BooheeRuler_rulerStyle,mStyle);
        mRulerBackGround = typedArray.getDrawable(R.styleable.BooheeRuler_rulerBackGround);
        if (mRulerBackGround == null){
            mRulerBackGroundColor = typedArray.getColor(R.styleable.BooheeRuler_rulerBackGround,mRulerBackGroundColor);
        }
        typedArray.recycle();
    }

    private void initRuler(Context context) {
        mContext = context;
        switch (mStyle){
            case TOP_HEAD:
                mInnerRuler = new TopHeadRuler(context, this);
                paddingHorizontal();
                break;
            case BOTTOM_HEAD:
                mInnerRuler = new BottomHeadRuler(context, this);
                paddingHorizontal();
                break;
            case LEFT_HEAD:
                mInnerRuler = new LeftHeadRuler(context, this);
                paddingVertical();
                break;
            case RIGHT_HEAD:
                mInnerRuler = new RightHeadRuler(context, this);
                paddingVertical();
                break;
        }

        //设置全屏，加入InnerRuler
        LayoutParams layoutParams = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        mInnerRuler.setLayoutParams(layoutParams);
        addView(mInnerRuler);
        //设置ViewGroup可画
        setWillNotDraw(false);

        initPaint();
        initDrawable();
        initRulerBackground();
    }

    private void initRulerBackground(){
        if (mRulerBackGround != null){
            mInnerRuler.setBackground(mRulerBackGround);
        }else {
            mInnerRuler.setBackgroundColor(mRulerBackGroundColor);
        }
    }

    //在宽高初始化之后定义光标Drawable的边界
    private void initDrawable() {
        getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                getViewTreeObserver().removeOnPreDrawListener(this);
                switch (mStyle){
                    case TOP_HEAD:
                        mCursorDrawable.setBounds((getWidth() - mCursorWidth) / 2, 0
                                , (getWidth() + mCursorWidth) / 2, mCursorHeight);
                        break;
                    case BOTTOM_HEAD:
                        mCursorDrawable.setBounds((getWidth() - mCursorWidth) / 2, getHeight() - mCursorHeight
                                , (getWidth() + mCursorWidth) / 2, getHeight());
                        break;
                    case LEFT_HEAD:
                        mCursorDrawable.setBounds(0, (getHeight() - mCursorHeight) / 2
                                , mCursorWidth, (getHeight() + mCursorHeight) / 2);
                        break;
                    case RIGHT_HEAD:
                        mCursorDrawable.setBounds(getWidth() - mCursorWidth, (getHeight() - mCursorHeight) / 2
                                , getWidth(),(getHeight() + mCursorHeight) / 2);
                        break;
                }

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
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);


    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
        //画中间的选定光标，要在这里画，因为dispatchDraw()执行在onDraw()后面，这样子光标才能不被尺子的刻度遮蔽
        mCursorDrawable.draw(canvas);

    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        mInnerRuler.layout(mPaddingLeft, mPaddingTop, r - l - mPaddingRight, b - t - mPaddingBottom);
    }

    private void paddingHorizontal(){
        mPaddingLeft = mPaddingStartAndEnd;
        mPaddingRight = mPaddingStartAndEnd;
        mPaddingTop = 0;
        mPaddingBottom = 0;
    }

    private void paddingVertical(){
        mPaddingTop = mPaddingStartAndEnd;
        mPaddingBottom = mPaddingStartAndEnd;
        mPaddingLeft = 0;
        mPaddingRight = 0;
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
        this.mTextMarginHead = textMarginTop;
    }

    public int getTextMarginHead() {
        return mTextMarginHead;
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
