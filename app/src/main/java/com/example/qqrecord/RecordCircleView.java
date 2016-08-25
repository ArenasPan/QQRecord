package com.example.qqrecord;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by pz on 2016/8/24.
 * 仿QQ圆形播放按钮
 */
public class RecordCircleView extends View {

    private Context mContext;

    private Paint mPaint;
    private int circleColor;
    private int mWidth;
    private int mHeight;

    private float radius;

    // 画笔的粗细（默认为40f）
    private float mStrokeWidth = 40f;


    public RecordCircleView(Context context) {
        this(context, null);
    }

    public RecordCircleView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RecordCircleView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mPaint = new Paint();
        TypedArray mTypedArray = context.obtainStyledAttributes(attrs,
                R.styleable.RecordCircleView);
        circleColor = mTypedArray.getColor(R.styleable.RecordCircleView_circleColor, Color.RED);
        radius = mTypedArray.getDimension(R.styleable.RecordCircleView_radius, 40);
        mTypedArray.recycle();
        this.mContext = context;
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        mWidth = getMeasuredWidth();
        mHeight = getMeasuredHeight();
        // 画笔的粗细为总宽度的 1 / 15
        mStrokeWidth = mWidth / 15f;

        // 创建圆环画笔
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setColor(circleColor);
        mPaint.setStyle(Paint.Style.FILL); // 边框风格
        mPaint.setStrokeWidth(mStrokeWidth);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        float cx = mWidth / 2.0f;
        float cy = mHeight / 2.0f;
        mPaint.setColor(circleColor);
        canvas.drawCircle(cx, cy, radius, mPaint);

        //花圆弧
        mPaint.setColor(Color.GRAY);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(1f);
        RectF oval = new RectF(0 + mStrokeWidth / 2, 0 + mStrokeWidth / 2,
                mWidth - mStrokeWidth / 2, mHeight - mStrokeWidth / 2);
        canvas.drawArc(oval, 0, 360, false, mPaint);


    }
}
