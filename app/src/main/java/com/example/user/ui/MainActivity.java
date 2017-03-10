package com.example.user.ui;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {
    private ImageView disappear_image;
    Bitmap bitmap;
    Bitmap bitmap_origin;
    private Button test_button;
    private Boolean thread_control = true; //Thread中斷控制
    private EditText et;
    private RelativeLayout rl;
    private TextView tt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findView();
        setView();
        setAdapter();
        setListener();
    }

    private void setListener() {
        test_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                thread_control = false;
            }
        });
    }


    private void setView() {
        disappear_image.setOnTouchListener(new View.OnTouchListener() {
            private int longClickDuration = 2000; //長按觸發動畫所需時間 ((可更改
            private boolean isLongPress = false;  //長按判斷
            private boolean isDossolveEnd = false;  //動畫是否完全結束
            private boolean isDossolveIng= false;  //動畫是否進行中;
            private boolean canTouchable=true;  //避免重複觸發動畫
            private   Handler hd=new Handler();
            private int touch_X;
            private int touch_Y;

            private Runnable main_dissolve_runnable=new Runnable() {
                @Override
                public void run() {
                    if (isLongPress) {

                        ///////////////////溶解(動畫)//////////////////////
                        isDossolveIng=true;
                        bitmap_origin = ((BitmapDrawable) disappear_image.getDrawable()).getBitmap();
//                            bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.aaa740820);
                        bitmap_origin = bitmap_origin.copy(Bitmap.Config.ARGB_8888, true);
                        bitmap_origin.setHasAlpha(true);
                        bitmap=bitmap_origin.copy(Bitmap.Config.ARGB_8888,true);
                        disappear_image.setImageBitmap(bitmap);
                        disappear_image.postInvalidate();
                        int threadPool_limit=3;  //ExecutorService內thread最大數量 ((可更改，影響效能
                        int width = bitmap.getWidth();
                        int height = bitmap.getHeight();
                        final int width_parting_length = (int) convertDpToPixel(50, getApplicationContext());  //將BITMAP分割之後每塊的寬
                        final int height_parting_length = (int) convertDpToPixel(50, getApplicationContext());  //將BITMAP分割之後每塊的長
                        final int width_parting_count = width / width_parting_length+1; //將BITMAP的寬分割成X塊，怕有餘數所以+1
                        final int height_parting_count = height / height_parting_length+1; //將BITMAP的高分割成X塊，怕有餘數所以+1

                        Log.e("testcount",(width_parting_count * height_parting_count)+"");
                        final int[][] coordinate_after_parting = new int[width_parting_count * height_parting_count][2];//將BITMAP分割之後每塊的左上初始座標
                        int x = 0;
                        for (int i = 0; i < width_parting_count; i++) {
                            for (int z = 0; z < height_parting_count; z++) {
                                coordinate_after_parting[x][0] = i;  //0都放x座標
                                coordinate_after_parting[x][1] = z;  //1都放y座標
                                x++;
                            }
                        }
                        final Canvas canvas = new Canvas(bitmap);
                        final ExecutorService executorService = Executors.newFixedThreadPool(threadPool_limit);

                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                Shuffle_test.shuffleArray2(coordinate_after_parting); //打亂座標排序，能夠隨機溶解
                                final int one_thread_run = 3; //一個thread內跑幾格動畫 ((可更改，可加快動畫結束時間
                                final int length_remaining=coordinate_after_parting.length % one_thread_run;
                                int loop_count = coordinate_after_parting.length / one_thread_run; //一次跑兩格溶解動畫，減少Thread數量

                                for (int i = 0; i <coordinate_after_parting.length;i+=one_thread_run) {

                                    if (thread_control) {

                                        final int finalI = i;
                                        boolean isFinal = false;  //判斷是否為最後一格動畫

                                        if (i ==  coordinate_after_parting.length - length_remaining) {
                                            isFinal = true;
                                        }

                                        executorService.execute(new dissolve_runnable(isFinal) {
                                            int basic_time=1; //動畫間隔基本時間 ((可更改
                                            int random=10;  //隨機時間 ((可更改
                                            int wait_time= (int)(Math.random()* random)+basic_time;  //每次更新透明度的時間
                                            @Override
                                            public void run() {
                                                if(this._isFinal){
                                                    wait_time=random+basic_time;
                                                }
//                                                wait_time=50;
                                                for (int z = 0; z < 49; z++) {  //迴圈次數，可控制透明程度間隔，影響動畫長度 ((可更改，需與透明度乘數一起改
                                                    isDossolveIng=true;
                                                    if (thread_control) {

                                                        this.draw_paint.setAlpha(z * 1);  //透明度乘數，可控制透明程度間隔，影響動畫長度 ((可更改，需與迴圈次數一起改
                                                        if(this._isFinal){
                                                            for(int i=0;i<length_remaining;i++){
                                                                canvas.drawRect(width_parting_length * coordinate_after_parting[finalI+i][0],
                                                                        height_parting_length * coordinate_after_parting[finalI+i][1],
                                                                        width_parting_length * (coordinate_after_parting[finalI+i][0] + 1),
                                                                        height_parting_length * (coordinate_after_parting[finalI+i][1] + 1),
                                                                        this.draw_paint);
                                                            }
                                                        }
                                                        else{
                                                            for(int i=0;i<one_thread_run;i++){
                                                                canvas.drawRect(width_parting_length * coordinate_after_parting[finalI+i][0],
                                                                        height_parting_length * coordinate_after_parting[finalI+i][1],
                                                                        width_parting_length * (coordinate_after_parting[finalI+i][0] + 1),
                                                                        height_parting_length * (coordinate_after_parting[finalI+i][1] + 1),
                                                                        this.draw_paint);
                                                            }
                                                        }

                                                    } else {
                                                        isDossolveEnd=false;
                                                        break;
                                                    }
                                                    try {
                                                        synchronized (this) {
                                                            this.wait(wait_time);  //控制透明度刷新時間速度，影響動畫長度((數字太小會卡頓OR當掉
                                                        }
                                                    } catch (InterruptedException e) {
                                                        e.printStackTrace();
                                                    }
                                                }
                                                if (this._isFinal) {
                                                    isDossolveEnd=true;
                                                    thread_control = false; //當此Thread是最後一個時，控制外層Thread停止postInvalidate;
                                                }
                                            }
                                        });

                                    } else {
                                        break;
                                    }

                                    disappear_image.postInvalidate();

                                }


                                while (thread_control) {
                                    disappear_image.postInvalidate();
                                    try {
                                        synchronized (this) {
                                            this.wait(3);
                                        }
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    }
                                }
                                disappear_image.postInvalidate();
                                if(isDossolveEnd){  /////////動畫順利結束run這///////
                                    Log.e("testover","動畫順利結束");
                                    isDossolveIng=false;
                                }
                                else{   /////////動畫被中斷run這///////
                                    isDossolveIng=false;
//                                    thread_control=true;
                                    bitmap_origin = BitmapFactory.decodeResource(getResources(), R.drawable.aaa740820);

                                    canvas.drawBitmap(bitmap_origin,0,0,null);
                                    disappear_image.postInvalidate();
                                    Log.e("testover","動畫被中斷");

                                }

                            }
                        }).start();
/////////////////溶解(動畫)//////////////////////
                    }
                    else{
                        canTouchable=true;
                    }
                }
            };


            @Override
            public boolean onTouch(final View view, MotionEvent motionEvent) {

                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {

                    touch_X= (int) motionEvent.getX();
                    touch_Y= (int) motionEvent.getY();

                    if(canTouchable){
                        thread_control=true;
                        isLongPress=true;
                        isDossolveEnd=false;
                        canTouchable=false;
                        hd.postDelayed(main_dissolve_runnable, longClickDuration);
                    }
                    if(isDossolveIng){
                        thread_control=false;
                        canTouchable=true;

                    }



                }
                if(motionEvent.getAction()==MotionEvent.ACTION_MOVE){
                    if((int)motionEvent.getX()-touch_X>15||
                            (int)motionEvent.getX()-touch_X<-15||
                            (int)motionEvent.getY()-touch_Y>15||
                            (int)motionEvent.getY()-touch_Y<-15){

                        isLongPress=false;

                    }
                }
                if(motionEvent.getAction()==MotionEvent.ACTION_UP){
                    isLongPress=false;
                }
                return true;
            }
        });

    }

    private void setAdapter() {

    }

    private void findView() {

        disappear_image = (ImageView) findViewById(R.id.disappear_image);
        test_button = (Button) findViewById(R.id.test_button);


    }

    public static float convertPixelToDp(float px, Context context) {
        float dp = px / getDensity(context);
        return dp;
    }

    public static float convertDpToPixel(float dp, Context context) {
        float px = dp * getDensity(context);
        return px;
    }


    public static float getDensity(Context context) {
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        return metrics.density;
    }

    abstract class dissolve_runnable implements Runnable {
        boolean _isFinal;
        Paint draw_paint = new Paint(Paint.ANTI_ALIAS_FLAG);

        public dissolve_runnable(boolean iF) {
            _isFinal = iF;
            draw_paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_OUT));  //設置畫筆效果
            draw_paint.setColor(Color.rgb(0, 0, 0));
        }
        @Override
        abstract public void run();
    }

}
