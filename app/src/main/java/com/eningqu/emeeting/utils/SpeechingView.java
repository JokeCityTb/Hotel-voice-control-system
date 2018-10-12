package com.eningqu.emeeting.utils;

import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.SweepGradient;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.graphics.ColorUtils;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.Animation;

import com.eningqu.emeeting.R;


/**
 * Created by yuhaiyang on 2018/7/3.
 * 正在说话的View
 */
public class SpeechingView extends View implements ValueAnimator.AnimatorUpdateListener {
    private static final String TAG = "SpeechingView";
    /**
     * 语音圈的最小透明度
     */
    private static final int MIN_CIRCLE_ALPHA = 0X88;
    /**
     * 语音圈的最大透明度
     */
    private static final int MAX_CIRCLE_ALPHA = 0XFF;

    /**
     * 0 点开始的透明度
     */
    private static final int START_CIRCLE_ALPHA = MIN_CIRCLE_ALPHA + (MAX_CIRCLE_ALPHA - MIN_CIRCLE_ALPHA) / 3;

    private Paint mPaint;
    private Drawable mMikeDrawable;
    private int mCircleColor;
    private int mAlpha;
    private ValueAnimator mValueAnimator;

    private Rect mMikeRectF;
    private RectF mFirstRectF;
    private RectF mSecondRectF;
    private RectF mThirdRectF;


    public SpeechingView(Context context) {
        super(context);
        init(context, null);
    }

    public SpeechingView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public SpeechingView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    @SuppressLint("NewApi")
    public SpeechingView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context, attrs);
    }

    private void init(Context context, @Nullable AttributeSet attrs) {
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.SpeechingView);
        mCircleColor = a.getColor(R.styleable.SpeechingView_color, context.getResources().getColor(R.color.speeching_color));
        a.recycle();
        mValueAnimator = ValueAnimator.ofInt(80, 255);
        mValueAnimator.setRepeatCount(Animation.INFINITE);
        mValueAnimator.setRepeatMode(ValueAnimator.REVERSE);
        mValueAnimator.setDuration(500);
        mValueAnimator.addUpdateListener(this);

        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(5);
        mPaint.setStrokeCap(Paint.Cap.ROUND);

        mMikeRectF = new Rect();
        mFirstRectF = new RectF();
        mSecondRectF = new RectF();
        mThirdRectF = new RectF();

        mMikeDrawable = context.getResources().getDrawable(R.drawable.ic_mike_svg);
        mMikeDrawable = DrawableCompat.wrap(mMikeDrawable);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        // 0点的Color
        final int zeroColor = ColorUtils.setAlphaComponent(mCircleColor, START_CIRCLE_ALPHA);
        final int minColor = ColorUtils.setAlphaComponent(mCircleColor, MIN_CIRCLE_ALPHA);
        final int maxColor = ColorUtils.setAlphaComponent(mCircleColor, MAX_CIRCLE_ALPHA);

        int[] colorls = new int[]{zeroColor, minColor, minColor, maxColor, zeroColor};
        float[] positions = new float[]{0, 0.125F, 0.375F, 0.75F, 1F};

        Shader shader = new SweepGradient(w / 2, h / 2, colorls, positions);
        mPaint.setShader(shader);


        int size = (int) (w * 0.35F);
        mMikeRectF.left = (w - size) / 2;
        mMikeRectF.right = mMikeRectF.left + size;
        mMikeRectF.top = (h - size) * 6 / 7;
        mMikeRectF.bottom = mMikeRectF.top + size;
        mMikeDrawable.setBounds(mMikeRectF);

        final int centerY = mMikeRectF.top + mMikeRectF.height() / 6;
        float scale = 0.4F;

        size = (int) (w * scale);
        mFirstRectF.left = (w - size) / 2;
        mFirstRectF.right = mFirstRectF.left + size;
        mFirstRectF.top = centerY - size / 2;
        mFirstRectF.bottom = mFirstRectF.top + size;

        scale += 0.25F;
        size = (int) (w * scale);
        mSecondRectF.left = (w - size) / 2;
        mSecondRectF.right = mSecondRectF.left + size;
        mSecondRectF.top = centerY - size / 2;
        mSecondRectF.bottom = mSecondRectF.top + size;

        scale += 0.28F;
        size = (int) (w * scale);
        mThirdRectF.left = (w - size) / 2;
        mThirdRectF.right = mThirdRectF.left + size;
        mThirdRectF.top = centerY - size / 2;
        mThirdRectF.bottom = mThirdRectF.top + size;

    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        mPaint.setAlpha(mAlpha);
        canvas.drawArc(mFirstRectF, -225, 270, false, mPaint);
        mPaint.setAlpha(mAlpha - 20);
        canvas.drawArc(mSecondRectF, -225, 270, false, mPaint);
        mPaint.setAlpha(mAlpha - 50);
        canvas.drawArc(mThirdRectF, -225, 270, false, mPaint);


        mMikeDrawable.draw(canvas);
    }

    @Override
    protected void onVisibilityChanged(@NonNull View changedView, int visibility) {
        super.onVisibilityChanged(changedView, visibility);
        if (visibility == View.VISIBLE) {
            mValueAnimator.start();
        } else {
            mValueAnimator.end();
        }
    }

    @Override
    public void onAnimationUpdate(ValueAnimator animation) {
        mAlpha = (int) animation.getAnimatedValue();
        postInvalidate();
    }
}
