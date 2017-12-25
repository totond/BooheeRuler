package yanzhikai.ruler.InnerRulers;

import android.content.Context;
import android.graphics.Canvas;

import yanzhikai.ruler.BooheeRuler;
import yanzhikai.ruler.Utils.RulerStringUtil;

/**
 * 头向左的尺子
 */

public class LeftHeadRuler extends VerticalRuler{

    public LeftHeadRuler(Context context, BooheeRuler booheeRuler) {
        super(context, booheeRuler);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawScale(canvas);
        drawEdgeEffect(canvas);
    }

    //画刻度和字
    private void drawScale(Canvas canvas) {
        int height = canvas.getHeight();
        float start = (getScrollY() - mDrawOffset) / mParent.getInterval() + mParent.getMinScale();
        float end = (getScrollY() + height + mDrawOffset) / mParent.getInterval() + mParent.getMinScale();
        for (float i = start; i <= end; i++){
            float locationY = (i - mParent.getMinScale()) * mParent.getInterval();

            if (i >= mParent.getMinScale() && i <= mParent.getMaxScale()) {
                if (i % mCount == 0) {
                    canvas.drawLine(0, locationY, mParent.getBigScaleLength(), locationY, mBigScalePaint);
                    canvas.drawText(RulerStringUtil.resultValueOf(i,mParent.getFactor()), mParent.getTextMarginHead(), locationY + mParent.getTextSize() / 2, mTextPaint);
                } else {
                    canvas.drawLine(0, locationY, mParent.getSmallScaleLength(), locationY, mSmallScalePaint);
                }
            }
        }
        //画轮廓线
        canvas.drawLine(0, getScrollY() , 0, getScrollY() + height, mOutLinePaint);

    }

    //画边缘效果
    private void drawEdgeEffect(Canvas canvas) {
        if (mParent.canEdgeEffect()) {
            if (!mStartEdgeEffect.isFinished()) {
                int count = canvas.save();
                if (mStartEdgeEffect.draw(canvas)) {
                    postInvalidateOnAnimation();
                }
                canvas.restoreToCount(count);
            } else {
                mStartEdgeEffect.finish();
            }
            if (!mEndEdgeEffect.isFinished()) {
                int count = canvas.save();
                canvas.rotate(180);
                canvas.translate(-mParent.getCursorWidth(), -mLength);
                if (mEndEdgeEffect.draw(canvas)) {
                    postInvalidateOnAnimation();
                }
                canvas.restoreToCount(count);
            } else {
                mEndEdgeEffect.finish();
            }
        }
    }
}
