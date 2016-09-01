package com.example.qqrecord;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import java.util.ArrayList;
import java.util.LinkedList;

public class RecordVoiceView extends View {


	private Paint paint;
	private int color;
	private float lineHeight = 10;
	private float maxLineheight;
	private float lineWidth;
	private float textSize;
	private String text = "00";
	private int textColor;
	private Thread mThread;
	private int milliSeconds;
	private boolean isStart = false;
	private Runnable mRunable;
	
//	LinkedList<Integer> list = new LinkedList<>();

	private ArrayList<RectF> leftRectF=new ArrayList<>();
	private ArrayList<RectF> rightRectF = new ArrayList<>();
	private LinkedList<Integer> leftLineHeight = new LinkedList<>();
	private LinkedList<Integer> rightLineHeight = new LinkedList<>();
	
	public RecordVoiceView(Context context) {
		super(context);
	}
	
	public RecordVoiceView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}
	
	public RecordVoiceView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		for(int i = 0 ; i < 10 ; i++ ){
//			list.add(1);
			leftLineHeight.add(3);
			rightLineHeight.add(3);
		}
		paint = new Paint();
		TypedArray mTypedArray = context.obtainStyledAttributes(attrs,R.styleable.RecordVoiceView);
		color = mTypedArray.getColor(R.styleable.RecordVoiceView_voiceLineColor, Color.BLACK);
		lineWidth = mTypedArray.getDimension(R.styleable.RecordVoiceView_voiceLineWidth, 35);
		lineHeight = mTypedArray.getDimension(R.styleable.RecordVoiceView_voiceLineHeight, 5);
		maxLineheight = mTypedArray.getDimension(R.styleable.RecordVoiceView_voiceLineHeight, 32);
		textSize = mTypedArray.getDimension(R.styleable.RecordVoiceView_voiceTextSize, 45);
		textColor = mTypedArray.getColor(R.styleable.RecordVoiceView_voiceTextColor, Color.BLACK);
		mTypedArray.recycle();
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		int widthcentre = getWidth() / 2;
		int heightcentre = getHeight() / 2;
		
		paint.setStrokeWidth(0);   
        paint.setColor(textColor);  
        paint.setTextSize(textSize);  
        paint.setTypeface(Typeface.DEFAULT_BOLD);
        float textWidth = paint.measureText(text);  
        canvas.drawText(text, widthcentre - textWidth / 2, heightcentre - (paint.ascent() + paint.descent())/2, paint);
		
		paint.setColor(color);
		paint.setStyle(Paint.Style.FILL);
		paint.setStrokeWidth(lineWidth);
		paint.setAntiAlias(true);
		for(int i = 0 ; i < 10 ; i++){
			//右侧波纹
			RectF rect = new RectF(widthcentre + 2 * i * lineHeight + textWidth / 2 + lineHeight, heightcentre - rightLineHeight.get(i) * lineHeight / 2,
					widthcentre + 2 * i * lineHeight + 2 * lineHeight + textWidth / 2, heightcentre + rightLineHeight.get(i) * lineHeight / 2);
			rightRectF.add(rect);
			//左侧波纹
			RectF rect2 = new RectF(widthcentre - (2 * i * lineHeight +2* lineHeight +  textWidth / 2),heightcentre - leftLineHeight.get(i) * lineHeight / 2,
					widthcentre  -( 2 * i * lineHeight + textWidth / 2 + lineHeight), heightcentre + leftLineHeight.get(i) * lineHeight / 2);
			leftRectF.add(rect2);
			canvas.drawRect(rect, paint); 
			canvas.drawRect(rect2, paint);
		}
	}
	
	public synchronized void addElement(Integer height){
		for (int i = 0; i <= height / 30 ; i ++) {
			leftLineHeight.remove(9 - i);
			leftLineHeight.add(i, (height / 20  - i) < 1 ? 1 : height / 20 - i);
			rightLineHeight.remove(9 - i);
			rightLineHeight.add(i, (height / 20  - i) < 1 ? 1 : height / 20 - i);
		}
		Log.e("波峰", "height" + height);
		postInvalidate();
	}

	public synchronized void setHeight(Integer height) {
		for (int i=9;i>0;i--) {
			leftLineHeight.set(i, leftLineHeight.get(i - 1));
			rightLineHeight.set(i, rightLineHeight.get(i - 1));
		}
		leftLineHeight.set(0, height);
		rightLineHeight.set(0, height);
		postInvalidate();
	}
	
	public synchronized void setText(String text) {
		this.text = text;
		postInvalidate();
	}
	public synchronized void startRecording(){
		milliSeconds = 0;
		isStart = true;
		new Thread(mRunable).start();
	}
//	public synchronized void stopRecord(){
//		isStart = false;
//		list.clear();
//		for(int i = 0 ; i < 10 ; i++ ){
//			list.add(1);
//		}
//		text = "00";
//		postInvalidate();
//	}
}



































