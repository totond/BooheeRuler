package yanzhikai.ruler;

import android.content.Context;
import android.graphics.Canvas;

/**
 * 头向上的尺子
 */

public class TopHeadRuler extends HorizontalRuler {

    public TopHeadRuler(Context context, BooheeRuler booheeRuler) {
        super(context, booheeRuler);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawScale(canvas);
    }

    //画刻度和字
    private void drawScale(Canvas canvas) {
        for (float i = mParent.getMinScale(); i <= mParent.getMaxScale(); i++) {
            float locationX = (i - mParent.getMinScale()) * mParent.getInterval();

            if (locationX > getScrollX() - mDrawOffset && locationX < (getScrollX() + canvas.getWidth() + mDrawOffset)) {
                if (i % mCount == 0) {
                    canvas.drawLine(locationX, 0, locationX, mParent.getBigScaleLength(), mBigScalePaint);
                    canvas.drawText(String.valueOf(i / 10), locationX, mParent.getTextMarginHead(), mTextPaint);
                } else {
                    canvas.drawLine(locationX, 0, locationX, mParent.getSmallScaleLength(), mSmallScalePaint);
                }
                //画轮廓线
                canvas.drawLine(getScrollX(), 0, getScrollX() + canvas.getWidth(), 0, mOutLinePaint);
            }
        }
    }
}
