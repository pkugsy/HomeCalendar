package pku.gsy.app.stubs;

import java.util.Calendar;

import pku.gsy.app.R;
import pku.gsy.app.util.MensesDate;
import pku.gsy.app.util.content.AnimationHelper;
import pku.gsy.app.util.content.AnimationHelper.OnAnimationFinishListener;
import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewStub;
import android.view.animation.Animation;
import android.widget.TextView;

public class DateAdjustStub implements OnClickListener {
	
	private Context mContext = null;
	private ViewStub mStub = null;

	private View mPickerOutside = null;
	private View mPicker = null;
	
	private TextView mYearPicker = null;
	private TextView mMonthPicker = null;
	private TextView mDayPicker = null;

	private boolean isFirstTime = true;
	private boolean isShow = false;
	private boolean isOutsideClickable = true;

	private AnimationHelper mAnimHelper = null;
	private Animation alphaInAnim = null;
	private Animation zoomInAnim = null;
	private Animation alphaOutAnim = null;
	private Animation zoomOutAnim = null;
	
	private Calendar mCalendar = null;
	private OnAdjustFinishListener mListener = null;
	
	public int mId = 0;
	public long mTimeStamp = 0L;

	private OnAnimationFinishListener mResetDisableOutsideClick = new OnAnimationFinishListener() {
		@Override
		public void onAnimationFinish() {
			isOutsideClickable = true;
		}
	};
	
	public DateAdjustStub(Context ctx, ViewStub baseStub) {
		mContext = ctx;
		mStub = baseStub;
		mAnimHelper = AnimationHelper.getInstance(mContext);
		mCalendar = Calendar.getInstance();
	}

	public final boolean isShow() {
		return (this.isShow);
	}

	public void show(int id, final MensesDate date) {
		if (isShow) return;
		isShow = true;

		if (isFirstTime) {
			isFirstTime = false;
			inflateViewStub();
		}
		
		mId = id;
		mCalendar.set(date.year, date.month - 1, date.day);
		mTimeStamp = mCalendar.getTimeInMillis();
		
		mYearPicker.setText(String.valueOf(date.year));
		mMonthPicker.setText(String.valueOf(date.month));
		mDayPicker.setText(String.valueOf(date.day));
		
		isOutsideClickable = false;
		mAnimHelper.setOnAnimationFinishListener(mResetDisableOutsideClick);
		mPickerOutside.clearAnimation();
		mPicker.clearAnimation();
		mPickerOutside.startAnimation(alphaInAnim);
		mPicker.startAnimation(zoomInAnim);
		mPickerOutside.setVisibility(View.VISIBLE);
		mPicker.setVisibility(View.VISIBLE);
	}

	public void setOnAdjustFinishListener(OnAdjustFinishListener listener) {
		mListener = listener;
	}
	
	private void hide() {
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
		
		mYearPicker = (TextView) mPicker.findViewById(R.id.yearPicker);
		mMonthPicker = (TextView) mPicker.findViewById(R.id.monthPicker);
		mDayPicker = (TextView) mPicker.findViewById(R.id.picker_value);
		mPicker.findViewById(R.id.picker_up).setOnClickListener(this);
		mPicker.findViewById(R.id.picker_down).setOnClickListener(this);

		mPicker.findViewById(R.id.btn_picker_cancel).setOnClickListener(this);
		mPicker.findViewById(R.id.btn_picker_ok).setOnClickListener(this);

		alphaInAnim = mAnimHelper.getOutsideInAnim();
		alphaOutAnim = mAnimHelper.getOutsideOutAnim();
		zoomInAnim = mAnimHelper.getPickerInAnim();
		zoomOutAnim = mAnimHelper.getPickerOutAnim();
	}

	private final void onPickerScroll(int step) {
		mCalendar.add(Calendar.DAY_OF_MONTH, step);
		mYearPicker.setText(String.valueOf(mCalendar.get(Calendar.YEAR)));
		mMonthPicker.setText(String.valueOf(mCalendar.get(Calendar.MONTH) + 1));
		mDayPicker.setText(String.valueOf(mCalendar.get(Calendar.DAY_OF_MONTH)));
	}
	
	@Override
	public void onClick(View view) {
		if (!isOutsideClickable) return;
		
		switch(view.getId()) {
		case R.id.picker_up:
			onPickerScroll(1);
			break;
		case R.id.picker_down:
			onPickerScroll(-1);
			break;
		case R.id.stub_history_adjuster:
			break;
		case R.id.datepicker_outside:
		case R.id.btn_picker_cancel:
			hide();
			break;
		case R.id.btn_picker_ok:
			if (mListener != null && mCalendar.getTimeInMillis() != mTimeStamp) {
				mListener.onAdjusted(mId, 
						mCalendar.get(Calendar.YEAR), 
						mCalendar.get(Calendar.MONTH) + 1, 
						mCalendar.get(Calendar.DAY_OF_MONTH));
			}
			hide();
			break;
		}
	}
	
	public interface OnAdjustFinishListener {
		public void onAdjusted(int id, int year, int month, int day);
	}
}
