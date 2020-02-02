package com.gmail.maystruks08.nfcruntracker.ui.stepview;

import android.content.Context;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.gmail.maystruks08.nfcruntracker.R;

import java.util.List;

/**
 * Maystruk Alexandr：17/01/2020 22:47
 * <p/>
 * ：StepView
 */
public class HorizontalStepView extends LinearLayout implements StepsViewIndicator.OnDrawIndicatorListener {
    private FrameLayout mTextContainer;
    private StepsViewIndicator mStepsViewIndicator;
    private List<StepBean> mStepBeanList;
    private int mCompletingPosition;
    private int mUnComplectedTextColor = ContextCompat.getColor(getContext(), R.color.colorTextActive);
    private int mComplectedTextColor = ContextCompat.getColor(getContext(), R.color.colorTextInactive);
    private int mTextSize = 14;
    private TextView mTextView;

    public HorizontalStepView(Context context) {
        this(context, null);
    }

    public HorizontalStepView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public HorizontalStepView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        View rootView = LayoutInflater.from(getContext()).inflate(R.layout.widget_horizontal_step_view, this);
        mStepsViewIndicator = rootView.findViewById(R.id.steps_indicator);
        mStepsViewIndicator.setOnDrawListener(this);
        mTextContainer = rootView.findViewById(R.id.rl_text_container);
        mStepsViewIndicator.setOrientation(StepsViewIndicator.Orientation.HORIZONTAL);
    }

    /**
     * @param stepsBeanList
     * @return
     */
    public HorizontalStepView setStepViewTexts(List<StepBean> stepsBeanList) {
        mStepBeanList = stepsBeanList;
        mStepsViewIndicator.setStepNum(mStepBeanList);
        return this;
    }


    /**
     * @param unComplectedTextColor
     * @return
     */
    public HorizontalStepView setStepViewUnComplectedTextColor(int unComplectedTextColor) {
        mUnComplectedTextColor = unComplectedTextColor;
        return this;
    }

    /**
     * @param complectedTextColor
     * @return
     */
    public HorizontalStepView setStepViewComplectedTextColor(int complectedTextColor) {
        this.mComplectedTextColor = complectedTextColor;
        return this;
    }

    /**
     * @param unCompletedLineColor
     * @return
     */
    public HorizontalStepView setStepsViewIndicatorUnCompletedLineColor(int unCompletedLineColor) {
        mStepsViewIndicator.setUnCompletedLineColor(unCompletedLineColor);
        return this;
    }

    /**
     * @param completedLineColor
     * @return
     */
    public HorizontalStepView setStepsViewIndicatorCompletedLineColor(int completedLineColor) {
        mStepsViewIndicator.setCompletedLineColor(completedLineColor);
        return this;
    }

    /**
     * @param defaultIcon
     */
    public HorizontalStepView setStepsViewIndicatorDefaultIcon(Drawable defaultIcon) {
        mStepsViewIndicator.setDefaultIcon(defaultIcon);
        return this;
    }

    /**
     * @param completeIcon
     */
    public HorizontalStepView setStepsViewIndicatorCompleteIcon(Drawable completeIcon) {
        mStepsViewIndicator.setCompleteIcon(completeIcon);
        return this;
    }

    /**
     * @param attentionIcon
     */
    public HorizontalStepView setStepsViewIndicatorAttentionIcon(Drawable attentionIcon) {
        mStepsViewIndicator.setAttentionIcon(attentionIcon);
        return this;
    }

    /**
     * set textSize
     *
     * @param textSize
     * @return
     */
    public HorizontalStepView setTextSize(int textSize) {
        if (textSize > 0) {
            mTextSize = textSize;
        }
        return this;
    }

    @Override
    public void onDrawIndicator() {
        if (mTextContainer != null) {
            mTextContainer.removeAllViews();
            List<Float> complectedXPosition = mStepsViewIndicator.getCircleCenterPointPositionList();
            if (mStepBeanList != null && complectedXPosition != null && complectedXPosition.size() > 0) {
                for (int i = 0; i < mStepBeanList.size(); i++) {
                    mTextView = new TextView(getContext());
                    mTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, mTextSize);
                    mTextView.setText(mStepBeanList.get(i).getName());
                    int spec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
                    mTextView.measure(spec, spec);
                    int measuredWidth = mTextView.getMeasuredWidth();
                    mTextView.setX(complectedXPosition.get(i) - measuredWidth / 2);
                    mTextView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                    if (i <= mCompletingPosition) {
                        mTextView.setTypeface(null, Typeface.BOLD);
                        mTextView.setTextColor(mComplectedTextColor);
                    } else {
                        mTextView.setTextColor(mUnComplectedTextColor);
                    }
                    mTextContainer.addView(mTextView);
                }
            }
        }
    }
}