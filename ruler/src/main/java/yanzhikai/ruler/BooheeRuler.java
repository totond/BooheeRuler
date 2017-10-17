package yanzhikai.ruler;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.util.Log;
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
    //中间光标画笔
    private Paint mCPaint,mOutLinePaint;
    //光标长度
    private int mCursorHeight = 70;

    public BooheeRuler(Context context) {
        super(context);
        init(context);
    }

    public BooheeRuler(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public BooheeRuler(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context){
        mContext = context;
        mInnerRuler = new InnerRuler(context);
        LayoutParams layoutParams = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        mInnerRuler.setLayoutParams(layoutParams);
        addView(mInnerRuler);
        setWillNotDraw(false);

        initPaint();
    }

    private void initPaint() {
        mCPaint = new Paint();
        mCPaint.setColor(getResources().getColor(R.color.colorDeepGreen));
        mCPaint.setStrokeWidth(8);
        mCPaint.setStrokeCap(Paint.Cap.ROUND);

        mOutLinePaint = new Paint();
        mOutLinePaint.setStrokeWidth(1);
        mOutLinePaint.setColor(mContext.getResources().getColor(R.color.colorGray));

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Log.d(TAG, "onDraw: parent");
        canvas.drawLine(0,0,canvas.getWidth(),0,mOutLinePaint);
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
        canvas.drawLine(canvas.getWidth()/2,0,canvas.getWidth()/2,mCursorHeight,mCPaint);
    }

    public void setCallback(RulerCallback rulerCallback){
        mInnerRuler.setRulerCallback(rulerCallback);

    }

    public void setCurrentScale(float currentScale){
        mInnerRuler.setCurrentScale(currentScale);
    }

    public float getCurrentScale() {
        return mInnerRuler.getCurrentScale();
    }
}
