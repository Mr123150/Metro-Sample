package ru.me.surfaceviewsample;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.Window;


public class MainActivity extends Activity {
	
	float pos = 5;
	int speed = 0;
	float acc=0;
	String spd;
	float textPos;
	
	private Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);

	private float initX, initY;
	private float targetX, targetY;
	private boolean drawing = true;
	
	float height;
	float width;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setRequestedOrientation (ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(new MySurfaceView(this));
		DisplayMetrics metrics = new DisplayMetrics();
	    getWindowManager().getDefaultDisplay().getMetrics(metrics);
	    height=metrics.heightPixels;
	    width=metrics.widthPixels;
	    textPos=width;
	    //Toast.makeText(this, width +"x"+ height, Toast.LENGTH_LONG).show();
	}

	public class MySurfaceThread extends Thread {

		private SurfaceHolder myThreadSurfaceHolder;
		private MySurfaceView myThreadSurfaceView;
		private boolean myThreadRun = false;

		public MySurfaceThread(SurfaceHolder surfaceHolder,
				MySurfaceView surfaceView) {
			myThreadSurfaceHolder = surfaceHolder;
			myThreadSurfaceView = surfaceView;
		}

		public void setRunning(boolean b) {
			myThreadRun = b;
		}

		@Override
		public void run() {
			// TODO Auto-generated method stub
			// super.run();
			while (myThreadRun) {
				Canvas c = null;
				try {
					c = myThreadSurfaceHolder.lockCanvas(null);
					synchronized (myThreadSurfaceHolder) {
						myThreadSurfaceView.onDraw(c);
					}

					sleep(40);

				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} finally {
					// do this in a finally so that if an exception is thrown
					// during the above, we don't leave the Surface in an
					// inconsistent state
					if (c != null) {
						myThreadSurfaceHolder.unlockCanvasAndPost(c);
					}
				}
			}
		}
	}

	public class MySurfaceView extends SurfaceView implements
			SurfaceHolder.Callback {

		private MySurfaceThread thread;

		@Override
		protected void onDraw(Canvas canvas) {
			// TODO Auto-generated method stub
			// super.onDraw(canvas);
			//canvas.drawCircle(width/2, height/2, 10, paint);
			canvas.drawRGB(0, 0, 0);
			
			for(int x=(int)pos%((int)height/8);x<height;x+=(height/8)){
				drawTie(canvas, x);
			}
			drawRail(canvas);
			drawTrain(canvas);
			drawControls(canvas, speed);
			//drawStationName(canvas, textPos);
			if(speed>0){
				if(acc<speed*height/60){
					acc+=speed*height/10000;
				}
				else{
					if(acc>speed*height/60){
						acc-=speed*height/10000;
					}
				}
			}
			else{
				if(acc>0){
					acc-=(height/10000-speed*height/4000);
				}
				else{
					acc=0;
				}
			}
			pos+=acc;
			/*textPos-=8;
			if(textPos==-width*5){
				textPos=width;
			}*/
			//pos+=height/30;
			/*if (drawing) {
				canvas.drawRGB(0, 0, 0);
				canvas.drawCircle(initX, initY, 3, paint);
				if ((initX == targetX) && (initY == targetY)) {
					drawing = false;
				} else {
					initX = (initX + targetX) / 2;
					initY = (initY + targetY) / 2;
				}
			}*/
		}
		
		public void drawRail(Canvas canvas){
			paint.setColor(Color.GRAY);
			paint.setStrokeWidth(5f);
			canvas.drawLine(width/3,0,width/3,height, paint);
			canvas.drawLine(width*2/3,0,width*2/3,height, paint);
		}
		
		public void drawTie(Canvas canvas, int x){
			paint.setColor(Color.DKGRAY);
			canvas.drawRect(width/4, x-width/18, width*3/4, x, paint);
		}
		public void drawTrain(Canvas canvas){
			paint.setColor(0xffeee1a9);
			RectF frontRightLight = new RectF(width*2/3-width/32, height*5/8-height/100, width*2/3+width/32, height*5/8+height/100);
			RectF frontLeftLight = new RectF(width/3-width/32, height*5/8-height/100, width/3+width/32, height*5/8+height/100);
			canvas.drawArc(frontLeftLight, 0, 360, true, paint);
			canvas.drawArc(frontRightLight, 0, 360, true, paint);
			paint.setColor(0xff1e90ff);
			RectF trainRect = new RectF(width/4, height*5/8,width*3/4, height);
			canvas.drawRect(width/4, height*7/8, width*3/4, height, paint);
			canvas.drawRoundRect(trainRect, 15, 15, paint);
			paint.setColor(0xff365ca6);
			paint.setStrokeWidth(2f);
			canvas.drawLine(width/3+width/25, height*5/8+15, width/3+width/25, height, paint);
			canvas.drawLine(width*5/12+width/50, height*5/8+15, width*5/12+width/50, height, paint);
			canvas.drawLine(width/2, height*5/8+15, width/2, height, paint);
			canvas.drawLine(width*7/12-width/50, height*5/8+15, width*7/12-width/50, height, paint);
			canvas.drawLine(width*2/3-width/25, height*5/8+15, width*2/3-width/25, height, paint);
			
		}
		public void drawControls(Canvas canvas, int speed){
			paint.setColor(Color.WHITE);
			paint.setTextSize(height/30);
			canvas.drawRect(0, height/2-height/30, width/6+width/30, height*7/8+height/30, paint);
			paint.setColor(Color.BLACK);
			canvas.drawText("3", width/12+width/15, height/2+height/60, paint);
			canvas.drawText("2", width/12+width/15, height/2+(height*7/8-height/2)/6+height/60, paint);
			canvas.drawText("1", width/12+width/15, height/2+(height*7/8-height/2)*2/6+height/60, paint);
			canvas.drawText("0", width/12+width/15, height/2+(height*7/8-height/2)*3/6+height/60, paint);
			canvas.drawText("-1", width/12+width/20, height/2+(height*7/8-height/2)*4/6+height/60, paint);
			canvas.drawText("-2", width/12+width/20, height/2+(height*7/8-height/2)*5/6+height/60, paint);
			canvas.drawText("-3", width/12+width/20, height*7/8+height/60, paint);
			paint.setColor(Color.DKGRAY);
			paint.setStrokeWidth(5f);
			canvas.drawLine(width/12, height/2, width/12, height*7/8, paint);
			paint.setColor(0xff844021);
			canvas.drawRect(width/12-width/20, height/2+(3-speed)*(height*7/8-height/2)/6-height/40, width/12+width/20, height/2+(3-speed)*(height*7/8-height/2)/6+height/40, paint);
			paint.setColor(Color.WHITE);
		}
		public void drawStationName(Canvas canvas,float x){
			paint.setColor(Color.GRAY);
			canvas.drawRect(0, 0, width, height/18, paint);
			paint.setColor(0xff6ac850);
			paint.setTextSize(height/25);
			canvas.drawText("Следующая станция Шаболовская", x, height/25, paint);
		}
	
		@Override
		public boolean onTouchEvent(MotionEvent event) {
			// TODO Auto-generated method stub
			// return super.onTouchEvent(event);
			int action = event.getAction();

			if (action == MotionEvent.ACTION_DOWN) {
				targetX = event.getX();
				targetY = event.getY();
				drawing = true;
				if(event.getX()<=width/6&&event.getY()>=height/2-height/40){
					if(event.getY()<height/2+(height*7/8-height/2)/12){
						speed=3;
					}
					if(event.getY()>=height/2+(height*7/8-height/2)/12&&event.getY()<height/2+(height*7/8-height/2)*3/12){
						speed=2;
					}
					if(event.getY()>=height/2+(height*7/8-height/2)*3/12&&event.getY()<height/2+(height*7/8-height/2)*5/12){
						speed=1;
					}
					if(event.getY()>=height/2+(height*7/8-height/2)*5/12&&event.getY()<height/2+(height*7/8-height/2)*7/12){
						speed=0;
					}
					if(event.getY()>=height/2+(height*7/8-height/2)*7/12&&event.getY()<height/2+(height*7/8-height/2)*9/12){
						speed=-1;
					}
					if(event.getY()>=height/2+(height*7/8-height/2)*9/12&&event.getY()<height/2+(height*7/8-height/2)*11/12){
						speed=-2;
					}
					if(event.getY()>=height/2+(height*7/8-height/2)*11/12){
						speed=-3;
					}
				}
			}

			return true;
		}

		public MySurfaceView(Context context) {
			super(context);
			// TODO Auto-generated constructor stub
			init();
		}

		public MySurfaceView(Context context, AttributeSet attrs) {
			super(context, attrs);
			// TODO Auto-generated constructor stub
			init();
		}

		public MySurfaceView(Context context, AttributeSet attrs, int defStyle) {
			super(context, attrs, defStyle);
			// TODO Auto-generated constructor stub
			init();
		}

		private void init() {
			getHolder().addCallback(this);
			thread = new MySurfaceThread(getHolder(), this);

			setFocusable(true); // make sure we get key events

			paint.setStyle(Paint.Style.FILL);
			paint.setStrokeWidth(3);
			paint.setColor(Color.WHITE);

			initX = targetX = 0;
			initY = targetY = 0;

		}

		@Override
		public void surfaceChanged(SurfaceHolder arg0, int arg1, int arg2,
				int arg3) {
			// TODO Auto-generated method stub
		}

		@Override
		public void surfaceCreated(SurfaceHolder holder) {
			// TODO Auto-generated method stub
			thread.setRunning(true);
			thread.start();
		}

		@Override
		public void surfaceDestroyed(SurfaceHolder holder) {
			// TODO Auto-generated method stub
			boolean retry = true;
			thread.setRunning(false);
			while (retry) {
				try {
					thread.join();
					retry = false;
				} catch (InterruptedException e) {
				}
			}
		}
	}
}
	
	

