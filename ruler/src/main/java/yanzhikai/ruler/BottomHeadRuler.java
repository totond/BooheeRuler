package yanzhikai.ruler;

import android.content.Context;
import android.graphics.Canvas;

/**
 * Created by yany on 2017/11/14.
 */

public class BottomHeadRuler extends HorizontalRuler {
    public BottomHeadRuler(Context context, BooheeRuler booheeRuler) {
        super(context, booheeRuler);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawScale(canvas);
    }

    //画刻度和字
    private void drawScale(Canvas canvas) {
        int height = canvas.getHeight();
        for (float i = mParent.getMinScale(); i <= mParent.getMaxScale(); i++){
            float locationX = (i - mParent.getMinScale()) * mParent.getInterval();

            if (locationX > getScrollX() - mDrawOffset && locationX < (getScrollX() + canvas.getWidth() + mDrawOffset)) {
                if (i % mCount == 0) {
                    canvas.drawLine(locationX, height - mParent.getBigScaleLength(), locationX, height, mBigScalePaint);
                    canvas.drawText(String.valueOf(i / 10), locationX, height - mParent.getTextMarginHead(), mTextPaint);
                } else {
                    canvas.drawLine(locationX, height - mParent.getSmallScaleLength(), locationX, height, mSmallScalePaint);
                }
            }
        }
    }
}
