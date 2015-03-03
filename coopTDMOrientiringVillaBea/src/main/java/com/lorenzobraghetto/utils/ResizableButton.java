package com.lorenzobraghetto.utils;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.Button;

public class ResizableButton extends Button {

	private static final String LOG = ResizableButton.class.getSimpleName();
	private float mTextSize;

	public ResizableButton(Context context) {
		super(context, null);
		Log.d(LOG, "Constructor: ResizableButton(Context context)");
		Log.d(LOG, "getTextSize(): " + getTextSize());

	}

	public ResizableButton(Context context, AttributeSet attrs) {
		super(context, attrs, android.R.attr.buttonStyle);
		Log.d(LOG, "Constructor: ResizableButton(Context context, AttributeSet attrs)");

		float dens = context.getResources().getDisplayMetrics().density;
		mTextSize = getTextSize() / dens;
	}

	public ResizableButton(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		Log.d(LOG, "Constructor: ResizableButton(Context context, AttributeSet attrs, int defStyle)");
		Log.d(LOG, "getTextSize(): " + getTextSize());
	}

	@Override
	protected void onLayout(boolean changed, int left, int top, int right,
			int bottom) {
		super.onLayout(changed, left, top, right, bottom);
		final int oldWidth = getMeasuredWidth();
		final int oldHeight = getMeasuredHeight();
		int newWidth;
		float size = mTextSize;

		do {

			setTextSize(size--);
			measure(MeasureSpec.UNSPECIFIED, MeasureSpec.UNSPECIFIED);
			newWidth = getMeasuredWidth();

		} while (newWidth > oldWidth);

		setMeasuredDimension(oldWidth, oldHeight);
	}

}