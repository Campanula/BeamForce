package hust.beamforce;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.view.View;

public class DrawShear extends View {

    Paint paint;
    Paint paint2;
    Paint paint3;
    Paint paint4;
    Path path = new Path();
    Context context;
    int screen_width = Resources.getSystem().getDisplayMetrics().widthPixels;
    int screen_height = Resources.getSystem().getDisplayMetrics().heightPixels;

    int offset;
    int scope;

    public DrawShear(Context context, int scope, int offset) {
        super(context);
        this.context = context;
        this.scope = scope;
        this.offset = offset;

        paint = new Paint();
        paint.setColor(Color.DKGRAY);
        paint.setStrokeJoin(Paint.Join.ROUND);
        paint.setStrokeCap(Paint.Cap.SQUARE);//笔触末端为矩形
        paint.setStrokeWidth(6);
        paint.setAntiAlias(true);
        paint.setTextSize(55);
        paint.setTextSkewX(-0.2f);

        paint2 = new Paint();
        paint2.setColor(0xff4fba21);
        paint2.setStrokeJoin(Paint.Join.ROUND);
        paint2.setStrokeCap(Paint.Cap.ROUND);
        paint2.setStrokeWidth(5);
        paint2.setAntiAlias(true);

        paint3 = new Paint();
        paint3.setColor(0xff1c9bff);
        paint3.setStrokeJoin(Paint.Join.ROUND);
        paint3.setStrokeCap(Paint.Cap.ROUND);
        paint3.setStrokeWidth(5);
        paint3.setStyle(Paint.Style.STROKE);//空心
        paint3.setAntiAlias(true);

        paint4 = new Paint();
        paint4.setColor(0xfff26418);
        paint4.setStrokeJoin(Paint.Join.ROUND);
        paint4.setStrokeCap(Paint.Cap.SQUARE);
        paint4.setStrokeWidth(5);
        paint4.setStyle(Paint.Style.STROKE);
        paint4.setAntiAlias(true);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        //绘制格点
        float step = scope/50.0f;
        for(float x = step; x <=49*step; x+= step){
            for (float y = step; y <= 49*step; y+= step){
                canvas.drawPoint(x,y,paint2);
            }
        }

        float offsetX = 0;//+offsetX
        float offsetY = offset*step;//+offsetY

        //绘制坐标
        canvas.drawLine(5*step, offsetY, 45*step, offsetY, paint);//X轴
        canvas.drawLine(5*step,4*step,5*step,32*step,paint);//Y轴
        path.moveTo(4.5f*step,4*step);
        path.lineTo(5.5f*step,4*step);
        path.lineTo(5*step,2*step);
        path.close();
        canvas.drawPath(path,paint);
        path.reset();
        canvas.drawText("Fs",1*step,4*step,paint);

        //绘制曲线
        float points[][] = MainActivity.beam.pointForDraw();

        float max=Math.abs(points[0][0]);
        for(float point : points[0]){
            if(Math.abs(point) > max){
                max = Math.abs(point);
            }
        }

        path.moveTo(5*step,offsetY - (points[0][0]/max)*(offsetY - 4*step));
        for( int i=0;i<=1000;i++){
            float x = 5*step + i*(40*step)/1000;
            float y = offsetY - (points[0][i]/max)*(offsetY - 4*step);
            path.lineTo(x,y);
        }
        path.lineTo(45*step, offsetY);
        canvas.drawPath(path,paint3);
        path.reset();
    }
}
