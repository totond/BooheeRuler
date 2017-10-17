package yanzhikai.ruler;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.ColorInt;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * Created by yany on 2017/10/17.
 */

public class KgNumberLayout extends RelativeLayout implements RulerCallback {
    private TextView tv_scale,tv_kg;
    //字体大小
    private float mScaleTextSize = 80, mKgTextSize = 40;
    //字体颜色
    private @ColorInt int mScaleTextColor = getResources().getColor(R.color.colorForgiven);
    private @ColorInt int mKgTextColor = getResources().getColor(R.color.colorForgiven);


    public KgNumberLayout(Context context) {
        super(context);
        init(context);
    }

    public KgNumberLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        initAttrs(context, attrs);
        init(context);
    }

    public KgNumberLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void initAttrs(Context context, AttributeSet attrs){
        TypedArray typedArray = context.getTheme().obtainStyledAttributes(attrs,R.styleable.KgNumberLayout,0,0);
        mScaleTextSize = typedArray.getDimension(R.styleable.KgNumberLayout_scaleTextSize,mScaleTextSize);
        mKgTextSize = typedArray.getDimension(R.styleable.KgNumberLayout_kgTextSize,mKgTextSize);
        mScaleTextColor = typedArray.getColor(R.styleable.KgNumberLayout_scaleTextColor,mScaleTextColor);
        mKgTextColor = typedArray.getColor(R.styleable.KgNumberLayout_kgTextColor,mKgTextColor);

    }

    private void init(Context context){
        LayoutInflater.from(context).inflate(R.layout.layout_kg_number,this);
        tv_scale = (TextView) findViewById(R.id.tv_scale);
        tv_kg = (TextView) findViewById(R.id.tv_kg);

        tv_scale.setTextSize(TypedValue.COMPLEX_UNIT_PX,mScaleTextSize);
        tv_scale.setTextColor(mScaleTextColor);

        tv_kg.setTextSize(TypedValue.COMPLEX_UNIT_PX,mKgTextSize);
        tv_kg.setTextColor(mKgTextColor);
    }

    public void bindRuler(BooheeRuler booheeRuler){
        booheeRuler.setCallback(this);
        tv_scale.setText(String.valueOf(booheeRuler.getCurrentScale()));
    }

    @Override
    public void onScaleChanging(float scale) {
        tv_scale.setText(String.valueOf(scale/10));
    }
}
