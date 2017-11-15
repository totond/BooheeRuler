package yanzhikai.ruler.InnerRulers;

import android.content.Context;
import android.graphics.Canvas;

import yanzhikai.ruler.BooheeRuler;

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
        int height = canvas.getHeight();
        for (float i = mParent.getMinScale(); i <= mParent.getMaxScale(); i++){
            float locationY = (i - mParent.getMinScale()) * mParent.getInterval();

            if (locationY > getScrollY() - mDrawOffset && locationY < (getScrollY() + height + mDrawOffset)) {
                if (i % mCount == 0) {
                    canvas.drawLine(0, locationY, mParent.getBigScaleLength(), locationY, mBigScalePaint);
                    canvas.drawText(String.valueOf(i / 10), mParent.getTextMarginHead(), locationY + mParent.getTextSize() / 2, mTextPaint);
                } else {
                    canvas.drawLine(0, locationY, mParent.getSmallScaleLength(), locationY, mSmallScalePaint);
                }
                //画轮廓线
                canvas.drawLine(0, getScrollY() , 0, getScrollY() + height, mOutLinePaint);
            }
        }
    }
}
