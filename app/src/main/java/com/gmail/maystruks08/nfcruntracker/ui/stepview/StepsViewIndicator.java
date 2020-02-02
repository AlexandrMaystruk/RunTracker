package com.gmail.maystruks08.nfcruntracker.ui.stepview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathEffect;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;

import androidx.core.content.ContextCompat;

import com.gmail.maystruks08.nfcruntracker.R;

import java.util.ArrayList;
import java.util.List;

import static com.gmail.maystruks08.nfcruntracker.ui.stepview.StepBeanKt.STEP_COMPLETED;
import static com.gmail.maystruks08.nfcruntracker.ui.stepview.StepBeanKt.STEP_CURRENT;
import static com.gmail.maystruks08.nfcruntracker.ui.stepview.StepBeanKt.STEP_UNDO;

public class StepsViewIndicator extends View {

    enum Orientation {HORIZONTAL, VERTICAL}

    private int defaultStepIndicatorNum = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 40, getResources().getDisplayMetrics());

    private float mCompletedLineHeight;// definition completed line height
    private float mCircleRadius;// definition circle radius

    private Drawable mCompleteIcon;// definition default completed icon
    private Drawable mAttentionIcon;//definition default underway icon
    private Drawable mDefaultIcon;//definition default unCompleted icon

    private float mCenterX;
    private float mCenterY;//definition view centerY position
    private float mLeftY;//  definition rectangle LeftY position
    private float mRightY;//  definition rectangle RightY position

    private List<StepBean> mStepBeanList;// there are currently few step
    private int mStepNum = 0;
    private float mLinePadding;// definition the spacing between the two circles

    private List<Float> mCircleCenterPointPositionList;// definition all of circles center point list
    private Paint mUnCompletedPaint;//Paint definition mUnCompletedPaint
    private Paint mCompletedPaint;//Paint definition mCompletedPaint

    private int mUnCompletedLineColor = ContextCompat.getColor(getContext(), R.color.colorTextActive);
    private int mCompletedLineColor = ContextCompat.getColor(getContext(), R.color.colorTextInactive);
    private int mCompletingPosition;//Position   underway position

    private Path mPath;

    private OnDrawIndicatorListener mOnDrawListener;

    private int screenWidth;//this screen width
    private int screenHeight;//this screen height

    private Orientation orientation = Orientation.HORIZONTAL;


    private Rect rect;

    /**
     * @param onDrawListener
     */
    public void setOnDrawListener(OnDrawIndicatorListener onDrawListener) {
        mOnDrawListener = onDrawListener;
    }

    /**
     * @return mCircleRadius
     */
    public float getCircleRadius() {
        return mCircleRadius;
    }


    public StepsViewIndicator(Context context) {
        this(context, null);
    }

    public StepsViewIndicator(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public StepsViewIndicator(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    /**
     * init
     */
    private void init() {
        mStepBeanList = new ArrayList<>();
        mPath = new Path();
        PathEffect mEffects = new DashPathEffect(new float[]{8, 8, 8, 8}, 1);

        mCircleCenterPointPositionList = new ArrayList<>();

        mUnCompletedPaint = new Paint();
        mCompletedPaint = new Paint();
        mUnCompletedPaint.setAntiAlias(true);
        mUnCompletedPaint.setColor(mUnCompletedLineColor);
        mUnCompletedPaint.setStyle(Paint.Style.STROKE);
        mUnCompletedPaint.setStrokeWidth(2);

        mCompletedPaint.setAntiAlias(true);
        mCompletedPaint.setColor(mCompletedLineColor);
        mCompletedPaint.setStyle(Paint.Style.STROKE);
        mCompletedPaint.setStrokeWidth(2);

        mUnCompletedPaint.setPathEffect(mEffects);
        mCompletedPaint.setStyle(Paint.Style.FILL);

        //set mCompletedLineHeight
        mCompletedLineHeight = 0.05f * defaultStepIndicatorNum;
        //set mCircleRadius
        mCircleRadius = 0.26f * defaultStepIndicatorNum;
        //set mLinePadding
        mLinePadding = 0.65f * defaultStepIndicatorNum;

        mCompleteIcon = ContextCompat.getDrawable(getContext(), R.drawable.ic_check_circle);
        mAttentionIcon = ContextCompat.getDrawable(getContext(), R.drawable.ic_checked);
        mDefaultIcon = ContextCompat.getDrawable(getContext(), R.drawable.ic_unchecked);
        rect = new Rect();
    }

    @Override
    protected synchronized void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if (orientation == Orientation.HORIZONTAL) {
            if (MeasureSpec.UNSPECIFIED != MeasureSpec.getMode(widthMeasureSpec)) {
                screenWidth = MeasureSpec.getSize(widthMeasureSpec);
            }
            int height = defaultStepIndicatorNum;
            if (MeasureSpec.UNSPECIFIED != MeasureSpec.getMode(heightMeasureSpec)) {
                height = Math.min(height, MeasureSpec.getSize(heightMeasureSpec));
            }
            int width = (int) (mStepNum * mCircleRadius * 2 - (mStepNum - 1) * mLinePadding);
            setMeasuredDimension(width, height);

        } else {
            if (MeasureSpec.UNSPECIFIED != MeasureSpec.getMode(heightMeasureSpec)) {
                screenHeight = MeasureSpec.getSize(heightMeasureSpec);
            }
            int width = defaultStepIndicatorNum;
            // this view dynamic height
            int mHeight = 0;
            if (mStepNum > 0) {
                //dynamic measure VerticalStepViewIndicator height
                mHeight = (int) (getPaddingTop() + getPaddingBottom() + mCircleRadius * 2 * mStepNum + (mStepNum - 1) * mLinePadding);
            }
            if (MeasureSpec.UNSPECIFIED != MeasureSpec.getMode(widthMeasureSpec)) {
                width = Math.min(width, MeasureSpec.getSize(widthMeasureSpec));
            }
            setMeasuredDimension(width, mHeight);
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if (orientation == Orientation.HORIZONTAL) {
            //get view centerY，keep current step view center vertical
            mCenterY = 0.5f * getHeight();
            mLeftY = mCenterY - (mCompletedLineHeight / 2);
            mRightY = mCenterY + mCompletedLineHeight / 2;
            mCircleCenterPointPositionList.clear();
            for (int i = 0; i < mStepNum; i++) {
                float paddingLeft = (screenWidth - mStepNum * mCircleRadius * 2 - (mStepNum - 1) * mLinePadding) / 2;
                mCircleCenterPointPositionList.add(paddingLeft + mCircleRadius + i * mCircleRadius * 2 + i * mLinePadding);
            }
        } else {
            mCenterX = getWidth() / 2;
            mLeftY = mCenterX - (mCompletedLineHeight / 2);
            mRightY = mCenterX + (mCompletedLineHeight / 2);
            for (int i = 0; i < mStepNum; i++) {
                float paddingLeft = (screenHeight - mStepNum * mCircleRadius * 2 - (mStepNum - 1) * mLinePadding) / 2;
                mCircleCenterPointPositionList.add(paddingLeft + mCircleRadius + i * mCircleRadius * 2 + i * mLinePadding);
            }
        }
        if (mOnDrawListener != null) {
            mOnDrawListener.onDrawIndicator();
        }
    }

    @Override
    protected synchronized void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (mOnDrawListener != null) {
            mOnDrawListener.onDrawIndicator();
        }
        mUnCompletedPaint.setColor(mUnCompletedLineColor);
        mCompletedPaint.setColor(mCompletedLineColor);

        //-----------------------------draw line-----------------------------------------------
        for (int i = 0; i < mCircleCenterPointPositionList.size() - 1; i++) {
            //ComplectedXPosition
            final float preComplectedXPosition = mCircleCenterPointPositionList.get(i);
            //ComplectedXPosition
            final float afterComplectedXPosition = mCircleCenterPointPositionList.get(i + 1);

            if (i <= mCompletingPosition && mStepBeanList.get(0).getState() != STEP_UNDO) {
                if (orientation == Orientation.HORIZONTAL) {
                    canvas.drawRect(preComplectedXPosition + mCircleRadius - 10, mLeftY, afterComplectedXPosition - mCircleRadius + 10, mRightY, mCompletedPaint);
                } else {
                    canvas.drawRect(mLeftY, preComplectedXPosition + mCircleRadius - 10, mRightY, afterComplectedXPosition - mCircleRadius + 10, mCompletedPaint);
                }
            } else {
                if (orientation == Orientation.HORIZONTAL) {
                    mPath.moveTo(preComplectedXPosition + mCircleRadius, mCenterY);
                    mPath.lineTo(afterComplectedXPosition - mCircleRadius, mCenterY);
                    canvas.drawPath(mPath, mUnCompletedPaint);
                } else {
                    mPath.moveTo(mCenterX, preComplectedXPosition + mCircleRadius);
                    mPath.lineTo(mCenterX, afterComplectedXPosition - mCircleRadius);
                    canvas.drawPath(mPath, mUnCompletedPaint);
                }
            }
        }
        //---------------------------draw icon-----------------------------------------------
        for (int i = 0; i < mCircleCenterPointPositionList.size(); i++) {
            final float currentComplectedXPosition = mCircleCenterPointPositionList.get(i);
            if (orientation == Orientation.HORIZONTAL) {
                rect.left = (int) (currentComplectedXPosition - mCircleRadius);
                rect.top = (int) (mCenterY - mCircleRadius);
                rect.right = (int) (currentComplectedXPosition + mCircleRadius);
                rect.bottom = (int) (mCenterY + mCircleRadius);
            } else {
                rect.left = (int) (mCenterX - mCircleRadius);
                rect.top = (int) (currentComplectedXPosition - mCircleRadius);
                rect.right = (int) (mCenterX + mCircleRadius);
                rect.bottom = (int) (currentComplectedXPosition + mCircleRadius);
            }
            StepBean stepsBean = mStepBeanList.get(i);
            if (stepsBean.getState() == STEP_UNDO) {
                mDefaultIcon.setBounds(rect);
                mDefaultIcon.draw(canvas);
            } else if (stepsBean.getState() == STEP_CURRENT) {
                mCompletedPaint.setColor(Color.WHITE);
                if (orientation == Orientation.HORIZONTAL) {
                    canvas.drawCircle(currentComplectedXPosition, mCenterY, mCircleRadius * 1.1f, mCompletedPaint);
                } else {
                    canvas.drawCircle(mCenterX, currentComplectedXPosition, mCircleRadius * 1.1f, mCompletedPaint);
                }
                mAttentionIcon.setBounds(rect);
                mAttentionIcon.draw(canvas);
            } else if (stepsBean.getState() == STEP_COMPLETED) {
                mCompleteIcon.setBounds(rect);
                mCompleteIcon.draw(canvas);
            }
        }
    }

    /**
     * @return mCircleCenterPointPositionList
     */
    public List<Float> getCircleCenterPointPositionList() {
        return mCircleCenterPointPositionList;
    }

    /**
     * @param stepsBeanList
     */
    public void setStepNum(List<StepBean> stepsBeanList) {
        this.mStepBeanList = stepsBeanList;
        mStepNum = mStepBeanList.size();

        if (mStepBeanList.size() > 0) {
            for (int i = 0; i < mStepNum; i++) {
                StepBean stepsBean = mStepBeanList.get(i);
                if (stepsBean.getState() == STEP_COMPLETED) {
                    mCompletingPosition = i;
                }
            }
        }

        requestLayout();
    }

    /**
     * @param unCompletedLineColor
     */
    public void setUnCompletedLineColor(int unCompletedLineColor) {
        this.mUnCompletedLineColor = unCompletedLineColor;
    }

    /**
     * @param completedLineColor
     */
    public void setCompletedLineColor(int completedLineColor) {
        this.mCompletedLineColor = completedLineColor;
    }

    /**
     * @param defaultIcon
     */
    public void setDefaultIcon(Drawable defaultIcon) {
        this.mDefaultIcon = defaultIcon;
    }

    /**
     * @param completeIcon
     */
    public void setCompleteIcon(Drawable completeIcon) {
        this.mCompleteIcon = completeIcon;
    }

    /**
     * @param attentionIcon
     */
    public void setAttentionIcon(Drawable attentionIcon) {
        this.mAttentionIcon = attentionIcon;
    }

    public void setOrientation(Orientation orientation) {
        this.orientation = orientation;
        requestLayout();
    }

    public interface OnDrawIndicatorListener {
        void onDrawIndicator();
    }
}
