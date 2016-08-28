package hust.beamforce;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.view.View;

public class DrawBeam extends View {
    Paint paint;
    Paint paint2;
    Paint paint3;
    Paint paint4;
    Paint paint5;
    Path path = new Path();
    Context context;
    int screen_width = Resources.getSystem().getDisplayMetrics().widthPixels;
    int screen_height = Resources.getSystem().getDisplayMetrics().heightPixels;

    int offset;
    int scope;

    public DrawBeam(Context context, int scope, int offset) {
        super(context);
        this.context = context;
        this.scope = scope;
        this.offset = offset;

        paint = new Paint();
        paint.setColor(0xffc7c9cd);
        paint.setStrokeJoin(Paint.Join.ROUND);
        paint.setStrokeCap(Paint.Cap.SQUARE);//笔触末端为矩形
        paint.setStrokeWidth(10);
        paint.setAntiAlias(true);

        paint2 = new Paint();
        paint2.setColor(0xff4fba21);
        paint2.setStrokeJoin(Paint.Join.ROUND);
        paint2.setStrokeCap(Paint.Cap.ROUND);
        paint2.setStrokeWidth(5);
        paint2.setStyle(Paint.Style.STROKE);//空心
        paint2.setAntiAlias(true);

        paint3 = new Paint();
        paint3.setColor(0xfff26418);
        paint3.setStrokeJoin(Paint.Join.ROUND);
        paint3.setStrokeCap(Paint.Cap.ROUND);
        paint3.setStrokeWidth(5);
        paint3.setAntiAlias(true);

        paint4 = new Paint();
        paint4.setColor(0xfff26418);
        paint4.setStrokeJoin(Paint.Join.ROUND);
        paint4.setStrokeCap(Paint.Cap.SQUARE);
        paint4.setStrokeWidth(5);
        paint4.setStyle(Paint.Style.STROKE);
        paint4.setAntiAlias(true);

        paint5 = new Paint();
        paint5.setColor(0xff1c9bff);
        paint5.setStrokeJoin(Paint.Join.ROUND);
        paint5.setStrokeCap(Paint.Cap.SQUARE);//笔触末端为矩形
        paint5.setStrokeWidth(8);
        paint5.setStyle(Paint.Style.STROKE);//空心
        paint5.setAntiAlias(true);
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

        //绘制梁
        if(MainActivity.beam.getState(0)) {
            canvas.drawLine(0.1f*scope+offsetX, 0.2f*scope+offsetY, 0.9f*scope+offsetX, 0.2f*scope+offsetY, paint);
        }

        //绘制约束
        if(MainActivity.beam.getState(2)) {
            int[] type = MainActivity.beam.getConstraintType();
            switch (type[0]){
                case 0:{//固定端
                    canvas.drawLine(0.098f*scope+offsetX, 0.16f*scope+offsetY, 0.098f*scope+offsetX, 0.24f*scope+offsetY, paint2);

                    canvas.drawLine(0.098f*scope+offsetX, 0.16f*scope+offsetY, 0.078f*scope+offsetX, 0.18f*scope+offsetY,paint2);
                    canvas.drawLine(0.098f*scope+offsetX, 0.18f*scope+offsetY, 0.078f*scope+offsetX, 0.20f*scope+offsetY,paint2);
                    canvas.drawLine(0.098f*scope+offsetX, 0.20f*scope+offsetY, 0.078f*scope+offsetX, 0.22f*scope+offsetY,paint2);
                    canvas.drawLine(0.098f*scope+offsetX, 0.22f*scope+offsetY, 0.078f*scope+offsetX, 0.24f*scope+offsetY,paint2);
                    break;
                }
                case 1:{//固定铰支座
                    canvas.drawCircle(0.09f*scope+offsetX, 0.2f*scope+offsetY, 0.01f*scope, paint2);

                    canvas.drawLine(0.1f*scope+offsetX,0.21f*scope+offsetY,0.12f*scope+offsetX,0.25f*scope+offsetY,paint2);
                    canvas.drawLine(0.08f*scope+offsetX,0.21f*scope+offsetY,0.06f*scope+offsetX,0.25f*scope+offsetY,paint2);
                    canvas.drawLine(0.05f*scope+offsetX,0.25f*scope+offsetY,0.13f*scope+offsetX,0.25f*scope+offsetY,paint2);

                    canvas.drawLine(0.125f*scope+offsetX,0.25f*scope+offsetY,0.105f*scope+offsetX,0.27f*scope+offsetY,paint2);
                    canvas.drawLine(0.105f*scope+offsetX,0.25f*scope+offsetY,0.085f*scope+offsetX,0.27f*scope+offsetY,paint2);
                    canvas.drawLine(0.085f*scope+offsetX,0.25f*scope+offsetY,0.065f*scope+offsetX,0.27f*scope+offsetY,paint2);
                    canvas.drawLine(0.065f*scope+offsetX,0.25f*scope+offsetY,0.045f*scope+offsetX,0.27f*scope+offsetY,paint2);

                    break;
                }
                case 2:{//滑移支座
                    canvas.drawCircle(0.09f*scope+offsetX, 0.2f*scope+offsetY, 0.01f*scope, paint2);
                    canvas.drawLine(0.09f*scope+offsetX, 0.21f*scope+offsetY,0.09f*scope+offsetX,0.23f*scope+offsetY,paint2);
                    canvas.drawCircle(0.09f*scope+offsetX, 0.24f*scope+offsetY, 0.01f*scope, paint2);
                    canvas.drawLine(0.05f*scope+offsetX,0.25f*scope+offsetY,0.13f*scope+offsetX,0.25f*scope+offsetY,paint2);

                    canvas.drawLine(0.13f*scope+offsetX,0.25f*scope+offsetY,0.11f*scope+offsetX,0.27f*scope+offsetY,paint2);
                    canvas.drawLine(0.11f*scope+offsetX,0.25f*scope+offsetY,0.09f*scope+offsetX,0.27f*scope+offsetY,paint2);
                    canvas.drawLine(0.09f*scope+offsetX,0.25f*scope+offsetY,0.07f*scope+offsetX,0.27f*scope+offsetY,paint2);
                    canvas.drawLine(0.07f*scope+offsetX,0.25f*scope+offsetY,0.05f*scope+offsetX,0.27f*scope+offsetY,paint2);
                    break;
                }
                case 3:{//自由端
                    break;
                }
            }
            switch (type[1]){
                case 0:{
                    canvas.drawLine(0.902f*scope+offsetX, 0.16f*scope+offsetY, 0.902f*scope+offsetX, 0.24f*scope+offsetY, paint2);

                    canvas.drawLine(0.922f*scope+offsetX, 0.16f*scope+offsetY, 0.902f*scope+offsetX, 0.18f*scope+offsetY, paint2);
                    canvas.drawLine(0.922f*scope+offsetX, 0.18f*scope+offsetY, 0.902f*scope+offsetX, 0.20f*scope+offsetY, paint2);
                    canvas.drawLine(0.922f*scope+offsetX, 0.20f*scope+offsetY, 0.902f*scope+offsetX, 0.22f*scope+offsetY, paint2);
                    canvas.drawLine(0.922f*scope+offsetX, 0.22f*scope+offsetY, 0.902f*scope+offsetX, 0.24f*scope+offsetY, paint2);
                    break;
                }
                case 1:{
                    canvas.drawCircle(0.91f*scope+offsetX, 0.2f*scope+offsetY, 0.01f*scope, paint2);

                    canvas.drawLine(0.9f*scope+offsetX,0.21f*scope+offsetY,0.88f*scope+offsetX,0.25f*scope+offsetY,paint2);
                    canvas.drawLine(0.92f*scope+offsetX,0.21f*scope+offsetY,0.94f*scope+offsetX,0.25f*scope+offsetY,paint2);
                    canvas.drawLine(0.87f*scope+offsetX,0.25f*scope+offsetY,0.95f*scope+offsetX,0.25f*scope+offsetY,paint2);

                    canvas.drawLine(0.945f*scope+offsetX,0.25f*scope+offsetY,0.925f*scope+offsetX,0.27f*scope+offsetY,paint2);
                    canvas.drawLine(0.925f*scope+offsetX,0.25f*scope+offsetY,0.905f*scope+offsetX,0.27f*scope+offsetY,paint2);
                    canvas.drawLine(0.905f*scope+offsetX,0.25f*scope+offsetY,0.885f*scope+offsetX,0.27f*scope+offsetY,paint2);
                    canvas.drawLine(0.885f*scope+offsetX,0.25f*scope+offsetY,0.865f*scope+offsetX,0.27f*scope+offsetY,paint2);
                    break;
                }
                case 2:{
                    canvas.drawCircle(0.91f*scope+offsetX, 0.2f*scope+offsetY, 0.01f*scope, paint2);
                    canvas.drawLine(0.91f*scope+offsetX, 0.21f*scope+offsetY,0.91f*scope+offsetX,0.23f*scope+offsetY,paint2);
                    canvas.drawCircle(0.91f*scope+offsetX, 0.24f*scope+offsetY, 0.01f*scope, paint2);
                    canvas.drawLine(0.87f*scope+offsetX,0.25f*scope+offsetY,0.95f*scope+offsetX,0.25f*scope+offsetY,paint2);

                    canvas.drawLine(0.95f*scope+offsetX,0.25f*scope+offsetY,0.93f*scope+offsetX,0.27f*scope+offsetY,paint2);
                    canvas.drawLine(0.93f*scope+offsetX,0.25f*scope+offsetY,0.91f*scope+offsetX,0.27f*scope+offsetY,paint2);
                    canvas.drawLine(0.91f*scope+offsetX,0.25f*scope+offsetY,0.89f*scope+offsetX,0.27f*scope+offsetY,paint2);
                    canvas.drawLine(0.89f*scope+offsetX,0.25f*scope+offsetY,0.87f*scope+offsetX,0.27f*scope+offsetY,paint2);
                    break;
                }
                case 3:{
                    break;
                }
            }
        }

        //绘制载荷
        if(MainActivity.beam.getState(3)) {
            for(Load beam_load : MainActivity.beam.getLoads()) {
                switch (beam_load.getType()) {
                    case "均布力":{
                        double l = beam_load.getLeftPosition()/MainActivity.beam.getLength();
                        double r = beam_load.getRightPosition()/MainActivity.beam.getLength();
                        float left = (float) l;
                        float right = (float) r;
                        float lx = 0.1f*scope+left*0.8f*scope+offsetX;
                        float rx = 0.1f*scope+right*0.8f*scope+offsetX;
                        int count = (int) ((rx - lx)/(0.02*scope));
                        if(count <= 0){
                            count = 1;
                        }
                        float delta = (rx - lx)/count;

                        if(beam_load.getValue() > 0){
                            canvas.drawLine(lx,0.14f*scope+offsetY,rx,0.14f*scope+offsetY,paint3);
                            for(int i = 0; i <= count; i++){
                                canvas.drawLine(lx+i*delta,0.14f*scope+offsetY,lx+i*delta,0.18f*scope+offsetY,paint3);

                                path.moveTo(lx+i*delta-0.005f*scope,0.18f*scope+offsetY);
                                path.lineTo(lx+i*delta,0.195f*scope+offsetY);
                                path.lineTo(lx+i*delta+0.005f*scope,0.18f*scope+offsetY);
                                path.close();
                                canvas.drawPath(path,paint3);
                                path.reset();
                            }
                        } else {
                            canvas.drawLine(lx,0.26f*scope+offsetY,rx,0.26f*scope+offsetY,paint3);
                            for(int i = 0; i <= count; i++){
                                canvas.drawLine(lx+i*delta,0.22f*scope+offsetY,lx+i*delta,0.26f*scope+offsetY,paint3);

                                path.moveTo(lx+i*delta-0.005f*scope,0.22f*scope+offsetY);
                                path.lineTo(lx+i*delta,0.205f*scope+offsetY);
                                path.lineTo(lx+i*delta+0.005f*scope,0.22f*scope+offsetY);
                                path.close();
                                canvas.drawPath(path,paint3);
                                path.reset();
                            }
                        }
                        break;
                    }
                    case "集中力":{
                        double p = beam_load.getLeftPosition()/MainActivity.beam.getLength();
                        float position = (float) p;
                        float x = 0.1f*scope+position*0.8f*scope+offsetX;

                        if(beam_load.getValue() > 0){
                            canvas.drawLine(x,0.16f*scope+offsetY,x,0.1f*scope+offsetY,paint3);
                            path.moveTo(x-0.01f*scope,0.16f*scope+offsetY);
                            path.lineTo(x+0.01f*scope,0.16f*scope+offsetY);
                            path.lineTo(x,0.19f*scope+offsetY);
                            path.close();
                            canvas.drawPath(path,paint3);
                            path.reset();
                        } else {
                            canvas.drawLine(x,0.24f*scope+offsetY,x,0.3f*scope+offsetY,paint3);
                            path.moveTo(x-0.01f*scope,0.24f*scope+offsetY);
                            path.lineTo(x+0.01f*scope,0.24f*scope+offsetY);
                            path.lineTo(x,0.21f*scope+offsetY);
                            path.close();
                            canvas.drawPath(path,paint3);
                            path.reset();
                        }

                        break;
                    }
                    case "弯矩":{
                        double p = beam_load.getLeftPosition()/MainActivity.beam.getLength();
                        float position = (float) p;
                        float x = 0.1f*scope+position*0.8f*scope+offsetX;

                        if(beam_load.getValue() < 0){
                            path.moveTo(x-0.02f*scope,0.16f*scope+offsetY);
                            path.lineTo(x,0.16f*scope+offsetY);
                            path.lineTo(x,0.24f*scope+offsetY);
                            path.lineTo(x+0.02f*scope,0.24f*scope+offsetY);
                            canvas.drawPath(path,paint4);
                            path.reset();

                            path.moveTo(x-0.02f*scope,0.15f*scope+offsetY);
                            path.lineTo(x-0.05f*scope,0.16f*scope+offsetY);
                            path.lineTo(x-0.02f*scope,0.17f*scope+offsetY);
                            path.close();
                            canvas.drawPath(path,paint3);
                            path.reset();

                            path.moveTo(x+0.02f*scope,0.23f*scope+offsetY);
                            path.lineTo(x+0.05f*scope,0.24f*scope+offsetY);
                            path.lineTo(x+0.02f*scope,0.25f*scope+offsetY);
                            path.close();
                            canvas.drawPath(path,paint3);
                            path.reset();
                        } else {
                            path.moveTo(x-0.02f*scope,0.24f*scope+offsetY);
                            path.lineTo(x,0.24f*scope+offsetY);
                            path.lineTo(x,0.16f*scope+offsetY);
                            path.lineTo(x+0.02f*scope,0.16f*scope+offsetY);
                            canvas.drawPath(path,paint4);
                            path.reset();

                            path.moveTo(x+0.02f*scope,0.15f*scope+offsetY);
                            path.lineTo(x+0.05f*scope,0.16f*scope+offsetY);
                            path.lineTo(x+0.02f*scope,0.17f*scope+offsetY);
                            path.close();
                            canvas.drawPath(path,paint3);
                            path.reset();

                            path.moveTo(x-0.02f*scope,0.23f*scope+offsetY);
                            path.lineTo(x-0.05f*scope,0.24f*scope+offsetY);
                            path.lineTo(x-0.02f*scope,0.25f*scope+offsetY);
                            path.close();
                            canvas.drawPath(path,paint3);
                            path.reset();
                        }

                        break;
                    }
                }
            }
        }
        if(MainActivity.beam.checkAll() && offset==-2) {
            //绘制曲线
            float points[][] = MainActivity.beam.pointForDraw();

            float max=Math.abs(points[2][0]);
            for(float point : points[2]){
                if(Math.abs(point) > max){
                    max = Math.abs(point);
                }
            }

            path.moveTo(5*step,0.2f*scope+offsetY - (points[2][0]/max)*(0.2f*scope+offsetY - 4*step));
            for( int i=0;i<=1000;i++){
                float x = 5*step + i*(40*step)/1000;
                float y = 0.2f*scope+offsetY - (points[2][i]/max)*(0.2f*scope+offsetY - 4*step);
                path.lineTo(x,y);
            }
            //path.lineTo(45*step, 0.2f*scope+offsetY);
            canvas.drawPath(path,paint5);
            path.reset();
        }
    }
}
