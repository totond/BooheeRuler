package yanzhikai.ruler;

import android.content.Context;
import android.graphics.Canvas;

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
    }

    //画刻度和字
    private void drawScale(Canvas canvas) {
        int width = canvas.getWidth();
        for (float i = mParent.getMinScale(); i <= mParent.getMaxScale(); i++){
            float locationY = (i - mParent.getMinScale()) * mParent.getInterval();

            if (locationY > getScrollY() - mDrawOffset && locationY < (getScrollY() + canvas.getHeight() + mDrawOffset)) {
                if (i % mCount == 0) {
                    canvas.drawLine(0, locationY, mParent.getBigScaleLength(), locationY, mBigScalePaint);
                    canvas.drawText(String.valueOf(i / 10), mParent.getTextMarginHead(), locationY, mTextPaint);
                } else {
                    canvas.drawLine(0, locationY, mParent.getSmallScaleLength(), locationY, mSmallScalePaint);
                }
                //画轮廓线
                canvas.drawLine(0, getScrollY() , 0, getScrollY() + canvas.getHeight(), mOutLinePaint);
            }
        }
    }
}
