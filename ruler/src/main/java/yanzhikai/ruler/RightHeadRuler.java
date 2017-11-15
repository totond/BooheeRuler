package yanzhikai.ruler;

import android.content.Context;
import android.graphics.Canvas;

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
    }

    //画刻度和字
    private void drawScale(Canvas canvas) {
        int width = canvas.getWidth();
        for (float i = mParent.getMinScale(); i <= mParent.getMaxScale(); i++){
            float locationY = (i - mParent.getMinScale()) * mParent.getInterval();

            if (locationY > getScrollY() - mDrawOffset && locationY < (getScrollY() + canvas.getHeight() + mDrawOffset)) {
                if (i % mCount == 0) {
                    canvas.drawLine(width - mParent.getBigScaleLength(), locationY, width, locationY, mBigScalePaint);
                    canvas.drawText(String.valueOf(i / 10),width - mParent.getTextMarginHead(), locationY, mTextPaint);
                } else {
                    canvas.drawLine(width - mParent.getSmallScaleLength(), locationY, width, locationY, mSmallScalePaint);
                }
                //画轮廓线
                canvas.drawLine(canvas.getWidth(), getScrollY(), canvas.getWidth(), getScrollY() + canvas.getHeight(), mOutLinePaint);
            }
        }
    }
}
