/*
 * Copyright (C) 2014 Giuseppe Buzzanca
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.gb.android.widget;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;

import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.animation.AnimatorListenerAdapter;
import com.nineoldandroids.animation.ValueAnimator;
import com.nineoldandroids.animation.ValueAnimator.AnimatorUpdateListener;
import com.nineoldandroids.view.ViewHelper;

/**
 * A {@link LinearLayout} version with sliding animation implementation
 */
public class SlidingLinearLayout extends LinearLayout {

    public interface SlideListener {

	/**
	 * Notifies the start of the slide animation.
	 * 
	 * @param slidingLinearLayout
	 *            An instance of {@link SlidingLinearLayout}
	 */
	public void onSlideStart(SlidingLinearLayout slidingLinearLayout);

	/**
	 * Notifies the end of the slide animation.
	 * 
	 * @param slidingLinearLayout
	 *            An instance of {@link SlidingLinearLayout}
	 */
	public void onSlideEnd(SlidingLinearLayout slidingLinearLayout);

    }

    private int mExpandedValue;
    private int mSlideOrientation;
    private int mDuration;
    private boolean mExpanded;
    private boolean isSliding;
    private SlideListener mOnSlideListener = null;
    private android.view.ViewGroup.LayoutParams mLayoutParams;
    private android.view.ViewGroup.LayoutParams mOrigParams;

    /**
     * The slide is animated in horizontal left direction
     * <p/>
     * Constant Value: 0 (0x00000000)
     */
    public static final int HORIZONTAL_LEFT = 0;
    /**
     * The slide is animated in horizontal right direction
     * <p/>
     * Constant Value: 1 (0x00000001)
     */
    public static final int HORIZONTAL_RIGHT = 1;
    /**
     * The slide is animated in vertical direction
     * <p/>
     * Constant Value: 2 (0x00000002)
     */
    public static final int VERTICAL = 2;
    private static final String INSTANCESTATE_KEY = SlidingLinearLayout.class
	    .getName() + ".INSTANCESTATE_KEY";
    private static final String SLIDEORIENTATION_KEY = SlidingLinearLayout.class
	    .getName() + ".SLIDEORIENTATION_KEY";
    private static final String DURATION_KEY = SlidingLinearLayout.class
	    .getName() + ".DURATION_KEY";
    private static final String EXPANDED_KEY = SlidingLinearLayout.class
	    .getName() + ".EXPANDED_KEY";
    private ValueAnimator mAnimator;

    private AnimatorUpdateListener mAnimatorUpdateListener = new AnimatorUpdateListener() {

	@Override
	public void onAnimationUpdate(ValueAnimator animation) {
	    int mValue = (Integer) animation.getAnimatedValue();
	    if (mSlideOrientation == VERTICAL) {
		mLayoutParams.height = mValue;
	    } else if (mSlideOrientation == HORIZONTAL_LEFT) {
		mLayoutParams.width = mValue;

	    } else if (mSlideOrientation == HORIZONTAL_RIGHT) {
		mLayoutParams.width = mValue;
		ViewHelper.setX(SlidingLinearLayout.this,
			Float.valueOf(mExpandedValue - mValue));
	    }

	    setLayoutParams(mLayoutParams);
	}
    };

    public SlidingLinearLayout(Context context) {
	super(context);
	mAnimator = new ValueAnimator();
	isSliding = false;
    }

    public SlidingLinearLayout(Context context, AttributeSet attrs) {
	super(context, attrs);
	mAnimator = new ValueAnimator();
	isSliding = false;
	parseAttrs(context, attrs);
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public SlidingLinearLayout(Context context, AttributeSet attrs, int defStyle) {
	super(context, attrs, defStyle);
	mAnimator = new ValueAnimator();
	isSliding = false;
	parseAttrs(context, attrs);
    }

    /**
     * Returns the slide orientation for this view
     * 
     * @return One of {@link #VERTICAL}, {@link #HORIZONTAL_LEFT},
     *         {@link #HORIZONTAL_RIGHT}.
     */
    public int getSlideOrientation() {
	return mSlideOrientation;
    }

    /**
     * Set the slide orientation for this view
     * 
     * @param orientation
     *            One of {@link #VERTICAL}, {@link #HORIZONTAL_LEFT},
     *            {@link #HORIZONTAL_RIGHT}.
     */
    public void setSlideOrientation(int orientation) {
	mSlideOrientation = orientation;
	invalidate();
	requestLayout();
    }

    /**
     * How long this animation should last
     * 
     * @return the duration in milliseconds of the animation
     */
    public int getDuration() {
	return mDuration;
    }

    /**
     * How long this animation should last. The duration cannot be negative.
     * 
     * @param duration
     *            Duration in milliseconds
     */
    public void setDuration(int duration) {
	mDuration = duration;
	invalidate();
	requestLayout();
    }

    /**
     * Binds an animation listener to this animation. The animation listener is
     * notified of animation events such as the start of the animation or the
     * end of the animation.
     * 
     * @param listener
     *            the animation listener to be notified
     */
    public void setSlideListener(SlideListener listener) {
	mOnSlideListener = listener;
    }

    /**
     * Set the view in expanded mode or not.
     * 
     * @param expanded
     *            a {@link boolean}
     */
    public void setExpanded(boolean expanded) {
	mExpanded = expanded;
	if (!mExpanded) {
	    if (mSlideOrientation == VERTICAL)
		mLayoutParams.height = 0;
	    else
		mLayoutParams.width = 0;
	} else {
	    mLayoutParams.height = mOrigParams.height;
	    mLayoutParams.width = mOrigParams.width;
	}
    }

    /**
     * Return if it's expanded
     * 
     * @return a {@link boolean}
     */
    public boolean isExpanded() {
	return mExpanded;
    }

    @Override
    protected void onAttachedToWindow() {
	super.onAttachedToWindow();
	mOrigParams = getLayoutParams();
	mLayoutParams = getLayoutParams();
    }

    @Override
    protected Parcelable onSaveInstanceState() {
	Bundle b = new Bundle();
	b.putParcelable(INSTANCESTATE_KEY, super.onSaveInstanceState());
	b.putInt(SLIDEORIENTATION_KEY, mSlideOrientation);
	b.putInt(DURATION_KEY, mDuration);
	b.putBoolean(EXPANDED_KEY, mExpanded);
	return b;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
	if (state instanceof Bundle) {
	    Bundle b = (Bundle) state;
	    state = b.getParcelable(INSTANCESTATE_KEY);
	    mSlideOrientation = b.getInt(SLIDEORIENTATION_KEY);
	    mDuration = b.getInt(DURATION_KEY);
	    mExpanded = b.getBoolean(EXPANDED_KEY);
	}
	super.onRestoreInstanceState(state);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
	super.onLayout(changed, l, t, r, b);
	for (int i = 0; i < getChildCount(); i++) {
	    View v = getChildAt(i);
	    if (v.getVisibility() != GONE) {
		android.view.ViewGroup.LayoutParams lp = v.getLayoutParams();
		if (mSlideOrientation == VERTICAL) {
		    lp.height = v.getHeight();
		} else
		    lp.width = v.getWidth();
	    }
	}
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
	super.onMeasure(widthMeasureSpec, heightMeasureSpec);

	int t = 0;
	for (int i = 0; i < getChildCount(); i++) {
	    View v = getChildAt(i);
	    if (v.getVisibility() != GONE) {
		if (mSlideOrientation == VERTICAL) {
		    t = t + v.getHeight()
			    + ((LayoutParams) v.getLayoutParams()).bottomMargin
			    + ((LayoutParams) v.getLayoutParams()).topMargin;
		} else {
		    t = t + v.getWidth()
			    + ((LayoutParams) v.getLayoutParams()).leftMargin
			    + ((LayoutParams) v.getLayoutParams()).rightMargin;
		}
	    }
	}

	if (mSlideOrientation == VERTICAL)
	    mExpandedValue = t + getPaddingBottom() + getPaddingTop();
	else
	    mExpandedValue = t + getPaddingLeft() + getPaddingRight();
	
	if (!mExpanded && !isSliding) {
	    if (mSlideOrientation == VERTICAL)
		setMeasuredDimension(getMeasuredWidth(), 0);
	    else
		setMeasuredDimension(0, getMeasuredHeight());
	}
    }

    /**
     * Display or hide this view with a sliding motion.
     */
    public void slide() {
	// Prepare ValueAnimator
	if (isSliding)
	    return;
	isSliding = true;

	if (!mExpanded)
	    mAnimator.setIntValues(0, mExpandedValue);
	else
	    mAnimator.setIntValues(mExpandedValue, 0);

	mExpanded = (mExpanded == true) ? false : true;

	// Finish to setup animation and start it
	mAnimator.addListener(new AnimatorListenerAdapter() {

	    @Override
	    public void onAnimationStart(Animator animation) {
		if (mOnSlideListener != null)
		    mOnSlideListener.onSlideStart(SlidingLinearLayout.this);
	    }

	    @Override
	    public void onAnimationEnd(Animator animation) {
		isSliding = false;
		setLayoutParams(mOrigParams);
		if (mOnSlideListener != null)
		    mOnSlideListener.onSlideEnd(SlidingLinearLayout.this);
	    }
	});
	mAnimator.addUpdateListener(mAnimatorUpdateListener);
	mAnimator.setDuration(mDuration);
	mAnimator.start();
    }

    private void parseAttrs(Context context, AttributeSet attrs) {
	mExpandedValue = 0;

	TypedArray a = context.getTheme().obtainStyledAttributes(attrs,
		R.styleable.SlidingLinearLayout, 0, 0);
	try {
	    mSlideOrientation = a
		    .getInteger(
			    R.styleable.SlidingLinearLayout_slide_orientation,
			    VERTICAL);
	    mDuration = a.getInteger(R.styleable.SlidingLinearLayout_duration,
		    300);
	    mExpanded = a.getBoolean(R.styleable.SlidingLinearLayout_expanded,
		    false);
	} finally {
	    a.recycle();
	}
    }
}
