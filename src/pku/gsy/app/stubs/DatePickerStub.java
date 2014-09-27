package pku.gsy.app.stubs;

import pku.gsy.app.R;
import pku.gsy.app.util.content.AnimationHelper;
import pku.gsy.app.util.content.AnimationHelper.OnAnimationFinishListener;
import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewStub;
import android.view.animation.Animation;
import android.widget.NumberPicker;

public class DatePickerStub implements OnClickListener {
	
	public interface OnDatePickerListener {
		public void onDatePickerCancel();
		public void onDatePickerOK(int gYear, int gMonth);
	}
	
	private Context mContext = null;
	private ViewStub mStub = null;
	
	private View mPickerOutside = null;
    private View mPicker = null;
    private NumberPicker mCenturyPicker = null;
	private NumberPicker mDecadePicker = null;
	private NumberPicker mYearPicker = null;
	private NumberPicker mMonthPicker = null;
    
    private boolean isFirstTime = true;
    private boolean isShow = false;
    private boolean isOutsideClickable = true;
    
    private AnimationHelper mAnimHelper = null;
    private Animation alphaInAnim = null;
    private Animation zoomInAnim = null;
    private Animation alphaOutAnim = null;
    private Animation zoomOutAnim = null;
    
    private OnDatePickerListener mListener = null;
    
    private OnAnimationFinishListener mResetDisableOutsideClick = new OnAnimationFinishListener() {
		@Override
		public void onAnimationFinish() {
			isOutsideClickable = true;
		}
	};

	public DatePickerStub(Context ctx, ViewStub baseStub) {
		mContext = ctx;
		mStub = baseStub;
		mAnimHelper = AnimationHelper.getInstance(mContext);
	}
	
	public final boolean isShow() {
		return (this.isShow);
	}
	
	public void setOnDatePickerListener(OnDatePickerListener listener) {
		mListener = listener;
	}
	
	public void show(int gYear, int gMonth) {
		if (isShow) return;
		isShow = true;
		
		if (isFirstTime) {
			isFirstTime = false;
			inflateViewStub();
		}
        
		mYearPicker.setValue(gYear % 10);
		gYear /= 10;
		mDecadePicker.setValue(gYear % 10);
		gYear /= 10;
		mCenturyPicker.setValue(gYear);
		mMonthPicker.setValue(gMonth);
		
		isOutsideClickable = false;
		mAnimHelper.setOnAnimationFinishListener(mResetDisableOutsideClick);
		mPickerOutside.clearAnimation();
    	mPicker.clearAnimation();
    	mPickerOutside.startAnimation(alphaInAnim);
    	mPicker.startAnimation(zoomInAnim);
    	mPickerOutside.setVisibility(View.VISIBLE);
    	mPicker.setVisibility(View.VISIBLE);
    	
    	// 使picker们重绘
    	mCenturyPicker.invalidate();
    	mDecadePicker.invalidate();
    	mYearPicker.invalidate();
    	mMonthPicker.invalidate();
	}
    
    public void hide() {
    	if (isShow) {
    		isShow = false;
    		isOutsideClickable = false;
    		mAnimHelper.setOnAnimationFinishListener(mResetDisableOutsideClick);
	    	mPickerOutside.clearAnimation();
	    	mPicker.clearAnimation();
	    	mPickerOutside.startAnimation(alphaOutAnim);
	    	mPicker.startAnimation(zoomOutAnim);
	    	mPickerOutside.setVisibility(View.GONE);
	    	mPicker.setVisibility(View.GONE);
    	}
    }
    
    private final void inflateViewStub() {
    	View mRootView = mStub.inflate();
    	mPickerOutside = mRootView.findViewById(R.id.datepicker_outside);
    	mPicker = mRootView.findViewById(R.id.datepicker);
    	mPickerOutside.setOnClickListener(this);
    	mPicker.setOnClickListener(this); // 阻止点击picker，导致触发picker outside的点击事件
    	
    	mCenturyPicker = (NumberPicker) mPicker.findViewById(R.id.centuryPicker);
        mDecadePicker = (NumberPicker) mPicker.findViewById(R.id.decadePicker);
        mYearPicker = (NumberPicker) mPicker.findViewById(R.id.yearPicker);
        mMonthPicker = (NumberPicker) mPicker.findViewById(R.id.monthPicker);
        
        mCenturyPicker.setMinValue(19);
        mCenturyPicker.setMaxValue(20);
        mCenturyPicker.setValue(20);
        
        mDecadePicker.setMinValue(0);
        mDecadePicker.setMaxValue(9);
        mDecadePicker.setValue(1);
        
		mYearPicker.setMinValue(0);
		mYearPicker.setMaxValue(9);
		mYearPicker.setValue(3);
		
		mMonthPicker.setMinValue(1);
		mMonthPicker.setMaxValue(12);
		mMonthPicker.setValue(12);
		
		mPicker.findViewById(R.id.btn_picker_cancel).setOnClickListener(this);
		mPicker.findViewById(R.id.btn_picker_ok).setOnClickListener(this);
    	
    	initAnimation();
    }
	
	private final void initAnimation() {
		alphaInAnim = mAnimHelper.getOutsideInAnim();
		alphaOutAnim = mAnimHelper.getOutsideOutAnim();
	    zoomInAnim = mAnimHelper.getPickerInAnim();
	    zoomOutAnim = mAnimHelper.getPickerOutAnim();
	}

	@Override
	public void onClick(View view) {
		if (!isOutsideClickable) return;
		hide();
		switch(view.getId()) {
		case R.id.datepicker_outside:
		case R.id.btn_picker_cancel:
			if (mListener != null) mListener.onDatePickerCancel();
			break;
		case R.id.btn_picker_ok:
			if (mListener != null) {
				int gYear = mCenturyPicker.getValue() * 100 + mDecadePicker.getValue() * 10 + mYearPicker.getValue();
				int gMonth = mMonthPicker.getValue();
				mListener.onDatePickerOK(gYear, gMonth);
			}
			break;
		}
	}
}
