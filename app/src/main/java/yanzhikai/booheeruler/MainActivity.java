package yanzhikai.booheeruler;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import yanzhikai.ruler.BooheeRuler;
import yanzhikai.ruler.KgNumberLayout;

public class MainActivity extends AppCompatActivity {
    private BooheeRuler mBooheeRuler;
    private KgNumberLayout mKgNumberLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mBooheeRuler = (BooheeRuler) findViewById(R.id.br_top_head);
        mKgNumberLayout = (KgNumberLayout) findViewById(R.id.knl);
        mKgNumberLayout.bindRuler(mBooheeRuler);


    }
}
