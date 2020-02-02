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

public class VerticalStepView extends LinearLayout implements StepsViewIndicator.OnDrawIndicatorListener {
    private FrameLayout container;
    private StepsViewIndicator mStepsViewIndicator;
    private List<StepBean> mStepBeanList;
    private int mUnComplectedTextColor = ContextCompat.getColor(getContext(), R.color.colorTextActive);
    private int mComplectedTextColor = ContextCompat.getColor(getContext(), R.color.colorTextInactive);
    private int mCompletingPosition;
    private int mTextSize = 14;
    private TextView mTextView;

    public VerticalStepView(Context context) {
        this(context, null);
    }

    public VerticalStepView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public VerticalStepView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        View rootView = LayoutInflater.from(getContext()).inflate(R.layout.widget_vertical_step_view, this);
        mStepsViewIndicator = rootView.findViewById(R.id.steps_indicator);
        mStepsViewIndicator.setOnDrawListener(this);
        container = rootView.findViewById(R.id.ll_container);
        mStepsViewIndicator.setOrientation(StepsViewIndicator.Orientation.VERTICAL);
    }

    /**
     * @param stepsBeanList
     * @return
     */
    public VerticalStepView setStepViewTexts(List<StepBean> stepsBeanList) {
        mStepBeanList = stepsBeanList;
        mStepsViewIndicator.setStepNum(mStepBeanList);
        return this;
    }

    /**
     * @param unComplectedTextColor
     * @return
     */
    public VerticalStepView setStepViewUnComplectedTextColor(int unComplectedTextColor) {
        mUnComplectedTextColor = unComplectedTextColor;
        return this;
    }

    /**
     * @param complectedTextColor
     * @return
     */
    public VerticalStepView setStepViewComplectedTextColor(int complectedTextColor) {
        this.mComplectedTextColor = complectedTextColor;
        return this;
    }

    /**
     * @param unCompletedLineColor
     * @return
     */
    public VerticalStepView setStepsViewIndicatorUnCompletedLineColor(int unCompletedLineColor) {
        mStepsViewIndicator.setUnCompletedLineColor(unCompletedLineColor);
        return this;
    }

    /**
     * @param completedLineColor
     * @return
     */
    public VerticalStepView setStepsViewIndicatorCompletedLineColor(int completedLineColor) {
        mStepsViewIndicator.setCompletedLineColor(completedLineColor);
        return this;
    }

    /**
     * @param defaultIcon
     */
    public VerticalStepView setStepsViewIndicatorDefaultIcon(Drawable defaultIcon) {
        mStepsViewIndicator.setDefaultIcon(defaultIcon);
        return this;
    }

    /**
     * @param completeIcon
     */
    public VerticalStepView setStepsViewIndicatorCompleteIcon(Drawable completeIcon) {
        mStepsViewIndicator.setCompleteIcon(completeIcon);
        return this;
    }

    /**
     * @param attentionIcon
     */
    public VerticalStepView setStepsViewIndicatorAttentionIcon(Drawable attentionIcon) {
        mStepsViewIndicator.setAttentionIcon(attentionIcon);
        return this;
    }

    /**
     * set textSize
     *
     * @param textSize
     * @return
     */
    public VerticalStepView setTextSize(int textSize) {
        if (textSize > 0) {
            mTextSize = textSize;
        }
        return this;
    }

    @Override
    public void onDrawIndicator() {
        if (container != null) {
            container.removeAllViews();
            List<Float> complectedXPosition = mStepsViewIndicator.getCircleCenterPointPositionList();
            if (mStepBeanList != null && complectedXPosition.size() > 0) {
                for (int i = 0; i < mStepBeanList.size(); i++) {
                    mTextView = new TextView(getContext());
                    mTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, mTextSize);
                    mTextView.setText(mStepBeanList.get(i).getName());
                    int spec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
                    mTextView.measure(spec, spec);
                    int measuredHeight = mTextView.getMeasuredHeight();
                    mTextView.setY(complectedXPosition.get(i) - measuredHeight / 2);
                    mTextView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));

                    if (i <= mCompletingPosition) {
                        mTextView.setTypeface(null, Typeface.BOLD);
                        mTextView.setTextColor(mComplectedTextColor);
                    } else {
                        mTextView.setTextColor(mUnComplectedTextColor);
                    }
                    container.addView(mTextView);
                }
            }
        }
    }
}