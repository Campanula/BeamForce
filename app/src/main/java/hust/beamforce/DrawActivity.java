package hust.beamforce;

import android.app.Activity;
import android.os.Bundle;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.widget.LinearLayout;

public class DrawActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.draw_activity);

        final LinearLayout draw_layout = (LinearLayout) findViewById(R.id.draw_layout);
        ViewTreeObserver observer = draw_layout.getViewTreeObserver();

        final LinearLayout draw_beam = (LinearLayout) findViewById(R.id.draw_beam);
        observer.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                int layout_width = draw_beam.getWidth();
                DrawBeam drawBeam = new DrawBeam(DrawActivity.this, layout_width, -2);
                draw_beam.removeAllViews();
                draw_beam.addView(drawBeam);
                draw_beam.setTag(drawBeam);
                draw_beam.getViewTreeObserver().removeOnGlobalLayoutListener(this);
            }
        });

        final LinearLayout draw_shear = (LinearLayout) findViewById(R.id.draw_shear);
        observer.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                int layout_width = draw_shear.getWidth();
                DrawShear drawShear = new DrawShear(DrawActivity.this, layout_width, 17);
                draw_shear.removeAllViews();
                draw_shear.addView(drawShear);
                draw_shear.setTag(drawShear);
                draw_shear.getViewTreeObserver().removeOnGlobalLayoutListener(this);
            }
        });

        final LinearLayout draw_moment = (LinearLayout) findViewById(R.id.draw_moment);
        observer.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                int layout_width = draw_moment.getWidth();
                DrawMoment drawMoment = new DrawMoment(DrawActivity.this, layout_width, 17);
                draw_moment.removeAllViews();
                draw_moment.addView(drawMoment);
                draw_moment.setTag(drawMoment);
                draw_moment.getViewTreeObserver().removeOnGlobalLayoutListener(this);
            }
        });
    }
}
