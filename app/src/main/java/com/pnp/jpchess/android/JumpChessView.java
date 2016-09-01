package com.pnp.jpchess.android;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Point;
import android.util.AttributeSet;
import android.view.View;

import com.pnp.jpchess.R;
import com.pnp.jpchess.common.Config;
import com.pnp.jpchess.common.Step;
import com.pnp.jpchess.mode.Game;
import com.pnp.jpchess.mode.UserHuman;

/*
 * This file is part of "PnP Chinese Jump Chess".
 *
 * Author 2016 Payne Pan
 *
 * "PnP Chinese Jump Chess" is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * "PnP Chinese Jump Chess" is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 * see <http://www.gnu.org/licenses/lgpl.html>
 */

/**
 * This is the View of chess board,
 * Many of the methods are for drawing 2D array to surface and transfer (x,y) to 2D array (row,col)
 * The board is rotated for user to use easily, so transfer is necessary.
 */
public class JumpChessView  extends View {
	private static String TAG = "JumpChessView";

	public static int DOT_WIDTH = 40;
	final static double PI = 3.14159265;
	//static int offset = (int)(DOT_WIDTH  + 1.4 /2 * DOT_WIDTH * Config.BOX_RANG);
	static int offsetX;
	static int offsetY;

	Bitmap dotSrcBitmap1 = BitmapFactory.decodeResource(getResources(), R.drawable.redball);
	Bitmap dotSrcBitmap2 = BitmapFactory.decodeResource(getResources(), R.drawable.blueball);
	Bitmap dotBitmap1;
	Bitmap dotBitmap2;
	Paint alphaPaint = new Paint();
	Paint redPaint = new Paint();
	Paint bluePaint = new Paint();

	public JumpChessView(Context context) {
		super(context);
	}

	public JumpChessView(Context context, AttributeSet attrs){
		super(context, attrs);
	}

	static void r(Point src, Point dest, int angle) {
		double x = src.x; 
		double y = src.y;
		double n = angle*PI/180;
		double xx = x*Math.cos(n)+y*Math.sin(n);
		double yy = -x*Math.sin(n)+y*Math.cos(n);
		dest.x = (int) xx;
		dest.y = (int) yy;
	}
	
	static void rotate(Point src, Point dest) {
		int angle =  -45;
		r( src, dest, angle);
		dest.x += offsetX;
		dest.y += offsetY;
	}

	static void reverseRotate(Point src, Point dest) {
		src.x -= offsetX;
		src.y -= offsetY;
		int angle =  45;
		r( src, dest, angle);
	}

	static int i2y(int i){
		return - DOT_WIDTH * Config.BOX_RANG /2 + DOT_WIDTH * i + DOT_WIDTH /2; 
	}

	static int j2x(int j){
		return  - DOT_WIDTH * Config.BOX_RANG /2 + DOT_WIDTH * j + DOT_WIDTH /2; 
	}

	static int x2j(int x){
		for(int j = 0; j < Config.BOX_RANG;  j++)
			if  (  j2x(j) - DOT_WIDTH /2  < x && x < j2x(j) + DOT_WIDTH /2  )
				return j;
		return -1;
	}

	static int y2i(int y){
		for (int i = 0; i < Config.BOX_RANG; i++)
			if  (  i2y(i) - DOT_WIDTH /2 < y && y < i2y(i) + DOT_WIDTH /2 )
				return i;
		return -1;
	}

	void drawDot(int i, int j, Paint paint ,Canvas canvas  )
	{
		Point p = new Point( j2x(j), i2y(i));
		Point pp = new Point();
		rotate(p, pp);
		canvas.drawCircle(pp.x, pp.y, DOT_WIDTH / 2, paint);
	}

	void drawDot(int i, int j, Paint paint, Bitmap bmp ,Canvas canvas  )
	{
		Point p = new Point( j2x(j), i2y(i));
		Point pp = new Point();
		rotate(p, pp);
		canvas.drawBitmap(bmp,pp.x-DOT_WIDTH/2,pp.y-DOT_WIDTH/2,paint);
	}

	void drawDot(int i, int j, int color, Bitmap bmp ,Canvas canvas  )
	{
		Point p = new Point( j2x(j), i2y(i));
		Point pp = new Point();
		rotate(p, pp);
		Paint paint = new Paint();
		paint.setStyle(Style.FILL);
		paint.setColor(color);
		if ( bmp ==  null) {
			canvas.drawCircle(pp.x, pp.y, DOT_WIDTH / 2, paint);
		} else {
			canvas.drawBitmap(bmp,pp.x-DOT_WIDTH/2,pp.y-DOT_WIDTH/2,alphaPaint);
		}
	}

	void drawSelected(int i, int j, int color,Canvas canvas  )
	{
		Point p = new Point(  j2x(j), i2y(i));
		Point pp = new Point();
		rotate(p, pp);
		Paint paint = new Paint();
		paint.setStyle(Style.STROKE);
		paint.setColor(color);
		paint.setStrokeWidth(4);
		canvas.drawCircle(pp.x, pp.y, DOT_WIDTH/2, paint);
	}       

	Point oldPoint1 = new Point();
	Point oldPoint2 = new Point();
	Point point1 = new Point();
	Point point2 = new Point();

	Paint whitePaint = new Paint();


	void drawGameBox(Canvas canvas)
	{
		if (dotBitmap1 == null) {
			dotBitmap1 = Bitmap.createScaledBitmap(dotSrcBitmap1, DOT_WIDTH, DOT_WIDTH, true);
			dotBitmap2 = Bitmap.createScaledBitmap(dotSrcBitmap2, DOT_WIDTH, DOT_WIDTH, true);
			dotSrcBitmap1.recycle();
			dotSrcBitmap2.recycle();

			redPaint.setStyle(Style.STROKE);
			redPaint.setColor(Color.RED);
			redPaint.setStrokeWidth(4);

			bluePaint.setStyle(Style.STROKE);
			bluePaint.setColor(Color.BLUE);
			bluePaint.setStrokeWidth(4);
			alphaPaint.setAlpha(180);
		}

		//draw horizon lines
		int i,j, ei,ej;

		j = 0;
		ej = Config.BOX_RANG-1;
		for( i= 0; i < Config.BOX_RANG; i++){
			oldPoint1.set( j2x(j), i2y(i));
			oldPoint2.set( j2x(ej),i2y(i));
			rotate( oldPoint1, point1); 
			rotate( oldPoint2, point2 );
			canvas.drawLine( point1.x, point1.y, point2.x, point2.y, whitePaint);
		}
		// draw vertical lines
		i = 0;
		ei = Config.BOX_RANG-1;
		for( j= 0; j < Config.BOX_RANG; j++){
			oldPoint1.set( j2x(j), i2y(i));
			oldPoint2.set( j2x(j),i2y(ei));
			rotate( oldPoint1, point1); 
			rotate( oldPoint2, point2 );
			canvas.drawLine( point1.x, point1.y, point2.x, point2.y, whitePaint);
		}
		//draw slash lines
		i = 0;
		for( j= 1; j < Config.BOX_RANG; j++){
			ei = j;
			ej = i;
			oldPoint1.set( j2x(j), i2y(i));
			oldPoint2.set( j2x(ej),i2y(ei));
			rotate( oldPoint1, point1); 
			rotate( oldPoint2, point2 );
			canvas.drawLine( point1.x, point1.y, point2.x, point2.y, whitePaint);
		} 

		j = Config.BOX_RANG -1 ;
		for( i= 1; i < Config.BOX_RANG-1; i++){
			ei = j;
			ej = i;
			oldPoint1.set( j2x(j), i2y(i));
			oldPoint2.set( j2x(ej),i2y(ei));
			rotate( oldPoint1, point1); 
			rotate( oldPoint2, point2 );
			canvas.drawLine( point1.x, point1.y, point2.x, point2.y, whitePaint);
		}

		// draw the Chesses
		int[][] box= Game.instance().getBox();
		for( int m=0; m < Config.BOX_RANG; m++ ){
			for( int n=0; n < Config.BOX_RANG; n++) {
				if ( box[m][n] == 1) {
					drawDot(m,n, alphaPaint, dotBitmap1, canvas);
				}
				else if ( box[m][n] == 2){
					drawDot(m,n, alphaPaint, dotBitmap2, canvas);
				}
			}
		}
		// draw the selected
		if ( 	select_i != -1 )
			drawSelected(select_i, select_j, Color.YELLOW,canvas );

		redPaint.setTextSize(40.0f);
		canvas.drawText("You can hardly Win", 30, 80, redPaint);
	}

	public void onDraw(Canvas canvas) {
		super.onDraw(canvas);

		if (background == null)
		      createBackgroundBitmap();
	    canvas.drawBitmap(background, 0, 0, null);
		
		whitePaint.setStyle(Style.FILL);
		whitePaint.setColor(Color.WHITE);  
		drawGameBox(canvas);
	}

	private int select_i = -1;
	private int select_j = -1;

	public void resetView(){
		 select_i = -1;
		 select_j = -1;
		 this.invalidate();
	}

	public void onSelect( int x, int y) {
		if (!UserHuman.instance().isPlaying()) // no human user
			return;

		if ( Game.instance().status == Config.PLAY_INIT ) {
			return;
		} else if( !UserHuman.instance().isMyTurn()) {
			return;
		}

		Point p = new Point( x, y);
		Point pp = new Point();
		reverseRotate(p,pp);

		int j = x2j(pp.x);
		int i = y2i(pp.y);
		if ( i != -1 && j != -1) {
			if ( select_i == i && select_j == j) {
				select_i = -1;
			}
			else if ( select_i != -1 ){
				//send move step
				Step step = new Step();
				step.i = select_i; 
				step.j = select_j;
				step.new_i = i; 
				step.new_j = j;
				step.side = UserHuman.instance().getSide();
				if ( Game.instance().canMove(step) ) {
				    Game.instance().acceptStep(step);
				}
				select_i = -1;
			}	
			else {
				// record the step
				int[][] box = Game.instance().getBox();
				if (  box[i][j] == UserHuman.instance().getSide()){
					select_i = i;
					select_j = j;
				}
			}
			this.invalidate();
		}
	}

	Bitmap background;
	void createBackgroundBitmap() {
    Resources res = getResources();  
    Bitmap bmp = BitmapFactory.decodeResource(res, R.drawable.background);
    Matrix matrix = new Matrix();
    matrix.postScale( (float)(viewWidth)/bmp.getWidth(), (float)(viewHeight)/bmp.getHeight());  
    //matrix.postScale( 1.5f, 2.0f);  
    background = Bitmap.createBitmap(bmp, 0, 0, bmp.getWidth(), bmp.getHeight(),  
            matrix, true);
    bmp = null;
	}
	
    private int viewWidth;
    private int viewHeight;
	@Override
	 protected void onSizeChanged(int xNew, int yNew, int xOld, int yOld){
	     super.onSizeChanged(xNew, yNew, xOld, yOld);
	     viewWidth = xNew;
	     viewHeight = yNew;
	     createBackgroundBitmap();
	     offsetX = viewWidth/2;
	     offsetY = viewHeight/2;
	}
}
