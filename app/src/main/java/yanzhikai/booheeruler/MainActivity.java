package yanzhikai.booheeruler;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import yanzhikai.ruler.BooheeRuler;
import yanzhikai.ruler.KgNumberLayout;

public class MainActivity extends AppCompatActivity {
    private BooheeRuler br_top_head,br_bottom_head,br_left_head,br_right_head;
    private KgNumberLayout knl_top_head,knl_bottom_head,knl_left_head,knl_right_head;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        br_top_head = (BooheeRuler) findViewById(R.id.br_top_head);
        knl_top_head = (KgNumberLayout) findViewById(R.id.knl_top_head);
        knl_top_head.bindRuler(br_top_head);

        br_bottom_head = (BooheeRuler) findViewById(R.id.br_bottom_head);
        knl_bottom_head = (KgNumberLayout) findViewById(R.id.knl_bottom_head);
        knl_bottom_head.bindRuler(br_bottom_head);

        br_left_head = (BooheeRuler) findViewById(R.id.br_left_head);
        knl_left_head = (KgNumberLayout) findViewById(R.id.knl_left_head);
        knl_left_head.bindRuler(br_left_head);

        br_right_head = (BooheeRuler) findViewById(R.id.br_right_head);
        knl_right_head = (KgNumberLayout) findViewById(R.id.knl_right_head);
        knl_right_head.bindRuler(br_right_head);

    }
}
