package yanzhikai.ruler;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * Created by yany on 2017/10/17.
 */

public class KgNumberLayout extends RelativeLayout implements RulerCallback {
    private TextView tv_scale,tv_kg;
    public KgNumberLayout(Context context) {
        super(context);
        init(context);
    }

    public KgNumberLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public KgNumberLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context){
        LayoutInflater.from(context).inflate(R.layout.layout_kg_number,this);
        tv_scale = (TextView) findViewById(R.id.tv_scale);
        tv_kg = (TextView) findViewById(R.id.tv_kg);
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
