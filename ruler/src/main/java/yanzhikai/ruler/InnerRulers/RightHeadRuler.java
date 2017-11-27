package yanzhikai.ruler.InnerRulers;

import android.content.Context;
import android.graphics.Canvas;
import android.util.Log;

import yanzhikai.ruler.BooheeRuler;

/**
 * 头向→的尺子
 */
public class RightHeadRuler extends VerticalRuler {

    public RightHeadRuler(Context context, BooheeRuler booheeRuler) {
        super(context, booheeRuler);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawScale(canvas);
        drawEdgeEffect(canvas);
        canvas.drawLine(0,0,mParent.getCursorWidth(),0,mTextPaint);
    }

    //画刻度和字
    private void drawScale(Canvas canvas) {
        int width = canvas.getWidth();
        int height = canvas.getHeight();
        for (float i = mParent.getMinScale(); i <= mParent.getMaxScale(); i++){
            float locationY = (i - mParent.getMinScale()) * mParent.getInterval();

            if (locationY > getScrollY() - mDrawOffset && locationY < (getScrollY() + height + mDrawOffset)) {
                if (i % mCount == 0) {
                    canvas.drawLine(width - mParent.getBigScaleLength(), locationY, width, locationY, mBigScalePaint);
                    canvas.drawText(String.valueOf(i / 10),width - mParent.getTextMarginHead(), locationY + mParent.getTextSize() / 2, mTextPaint);
                } else {
                    canvas.drawLine(width - mParent.getSmallScaleLength(), locationY, width, locationY, mSmallScalePaint);
                }
                //画轮廓线
                canvas.drawLine(canvas.getWidth(), getScrollY(), canvas.getWidth(), getScrollY() + height, mOutLinePaint);
            }
        }
    }

    private void drawEdgeEffect(Canvas canvas) {
        if (!mStartEdgeEffect.isFinished()) {
            int count = canvas.save();
//            Log.d("ruler", "drawEdgeEffect: mStartEdgeEffect");
            canvas.translate((getWidth() - mParent.getCursorWidth()), 0);

            mStartEdgeEffect.setSize(mParent.getCursorWidth(), getWidth());
            if (mStartEdgeEffect.draw(canvas)) {
                postInvalidateOnAnimation();
            }
            canvas.restoreToCount(count);
        } else {
            mStartEdgeEffect.finish();
        }
        if (!mEndEdgeEffect.isFinished()) {
//            Log.d("ruler", "drawEdgeEffect: mStartEdgeEffect");
            int count = canvas.save();
            canvas.rotate(180);
            canvas.translate(-getWidth(), -mLength);
            mEndEdgeEffect.setSize(mParent.getCursorWidth(),getWidth());
            if (mEndEdgeEffect.draw(canvas)) {
                postInvalidateOnAnimation();
            }
            canvas.restoreToCount(count);
        } else {
            mEndEdgeEffect.finish();
        }
    }
}
