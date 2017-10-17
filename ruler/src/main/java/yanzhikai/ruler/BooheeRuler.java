package yanzhikai.ruler;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.annotation.ColorInt;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

/**
 * 用于包着尺子的外壳，用于画选取光标、外壳
 */

public class BooheeRuler extends RelativeLayout {
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
    private int mSmallScaleWidth = 3,mBigScaleWidth = 5;
    //数字字体大小
    private int mTextSize = 28;
    //数字Text距离顶部高度
    private int mTextMarginTop = 120;
    //刻度间隔
    private int mInterval = 18;
    //数字Text颜色
    private @ColorInt int mTextColor = getResources().getColor(R.color.colorLightBlack);
    //刻度颜色
    private @ColorInt int mScaleColor = getResources().getColor(R.color.colorGray);
    //光标颜色
    private @ColorInt int mCursorColor = getResources().getColor(R.color.colorForgiven);

    public BooheeRuler(Context context) {
        super(context);
        initRuler(context);

    }

    public BooheeRuler(Context context, AttributeSet attrs) {
        super(context, attrs);
        initAttrs(context,attrs);
        initRuler(context);

    }

    public BooheeRuler(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initAttrs(context,attrs);
        initRuler(context);

    }

    private void initAttrs(Context context, AttributeSet attrs){
        TypedArray typedArray = context.getTheme().obtainStyledAttributes(attrs,R.styleable.BooheeRuler,0,0);
        mMinScale = typedArray.getInteger(R.styleable.BooheeRuler_minScale,mMinScale);
        mMaxScale = typedArray.getInteger(R.styleable.BooheeRuler_maxScale,mMaxScale);
        mCursorWidth = typedArray.getDimensionPixelSize(R.styleable.BooheeRuler_cursorWidth,mCursorWidth);
        mCursorHeight = typedArray.getDimensionPixelSize(R.styleable.BooheeRuler_cursorHeight,mCursorHeight);
        mSmallScaleWidth = typedArray.getDimensionPixelSize(R.styleable.BooheeRuler_smallScaleWidth,mSmallScaleWidth);
        mSmallScaleLength = typedArray.getDimensionPixelSize(R.styleable.BooheeRuler_smallScaleLength,mSmallScaleLength);
        mBigScaleWidth = typedArray.getDimensionPixelSize(R.styleable.BooheeRuler_bigScaleWidth,mBigScaleWidth);
        mBigScaleLength = typedArray.getDimensionPixelSize(R.styleable.BooheeRuler_bigScaleLength,mBigScaleLength);
        mTextSize = typedArray.getDimensionPixelSize(R.styleable.BooheeRuler_numberTextSize,mTextSize);
        mTextMarginTop = typedArray.getDimensionPixelSize(R.styleable.BooheeRuler_textMarginTop,mTextMarginTop);
        mInterval = typedArray.getDimensionPixelSize(R.styleable.BooheeRuler_scaleInterval,mInterval);
        mCursorColor = typedArray.getColor(R.styleable.BooheeRuler_cursorColor,mCursorColor);
        mTextColor = typedArray.getColor(R.styleable.BooheeRuler_numberTextColor,mTextColor);
        mScaleColor = typedArray.getColor(R.styleable.BooheeRuler_scaleColor,mScaleColor);
        typedArray.recycle();
    }

    private void initRuler(Context context) {
        mContext = context;
        mInnerRuler = new InnerRuler(context,this);
        //设置全屏
        LayoutParams layoutParams = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        mInnerRuler.setLayoutParams(layoutParams);
        addView(mInnerRuler);
        setWillNotDraw(false);

        initPaint();

    }

    private void initPaint() {
        mCPaint = new Paint();
        mCPaint.setColor(mCursorColor);
        mCPaint.setStrokeWidth(mCursorWidth);
        mCPaint.setStrokeCap(Paint.Cap.ROUND);

        mOutLinePaint = new Paint();
        mOutLinePaint.setStrokeWidth(0);
        mOutLinePaint.setColor(mScaleColor);

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
        //画中间的选定光标
        canvas.drawLine(canvas.getWidth() / 2, 0, canvas.getWidth() / 2, mCursorHeight, mCPaint);
    }

    public void setCallback(RulerCallback rulerCallback) {
        mInnerRuler.setRulerCallback(rulerCallback);

    }

    public void setCurrentScale(float currentScale) {
        mInnerRuler.setCurrentScale(currentScale);
    }

    public float getCurrentScale() {
        return mInnerRuler.getCurrentScale();
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

    //    public void setBigScaleLength(float bigScaleLength) {
//        mInnerRuler.setBigScaleLength(bigScaleLength);
//    }
//
//    public float getBigScaleLength() {
//        return mInnerRuler.getBigScaleLength();
//    }
//
//    public void setSmallScaleLength(float smallScaleLength) {
//        mInnerRuler.setSmallScaleLength(smallScaleLength);
//    }
//
//    public float getSmallScaleLength() {
//        return mInnerRuler.getSmallScaleLength();
//    }
//
//    public void setTextMarginTop(int textMarginTop) {
//        mInnerRuler.setTextMarginTop(textMarginTop);
//    }
//
//    public int getTextMarginTop() {
//        return mInnerRuler.getTextMarginTop();
//    }
//
//    public void setTextSize(float textSize) {
//        mInnerRuler.setTextSize(textSize);
//    }
//
//    public float getTextSize() {
//        return mInnerRuler.getTextSize();
//    }
//
//    public void setInterval(float interval) {
//        mInnerRuler.setInterval(interval);
//    }
//
//    public float getInterval() {
//        return mInnerRuler.getInterval();
//    }
//
//    public void setBigScaleWidth(float bigScaleWidth) {
//        mInnerRuler.setBigScaleWidth(bigScaleWidth);
//    }
//
//    public float getBigScaleWidth() {
//        return mInnerRuler.getBigScaleWidth();
//    }
//
//    public void setSmallScaleWidth(float smallScaleWidth) {
//        mInnerRuler.setSmallScaleWidth(smallScaleWidth);
//    }
//
//    public float getSmallScaleWidth() {
//        return mInnerRuler.getSmallScaleWidth();
//    }
}
