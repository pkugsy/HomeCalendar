package pku.gsy.app.view.wrapper;

import pku.gsy.app.R;
import android.annotation.SuppressLint;
import android.view.View;
import android.widget.TextView;

public class MensesDateWrapper extends BaseWrapper {
	
	public static final int State_FirstDay = 1;
	public static final int State_Period = 2;
	public static final int State_FirstFertile = 3;
	public static final int State_Fertile = 4;
	public static final int State_Default = 0;
	
	private final int black = mBase.getResources().getColor(R.color.black);
	private final int lightGray = mBase.getResources().getColor(R.color.light_gray);
	
	private TextView mSolar = null;
	private View mHint = null;
	private boolean mEnable = true;
	private int mState = State_Default;

	public MensesDateWrapper(View base) {
		super(base);
		
		mSolar = (TextView) base.findViewById(R.id.txt_solar);
		mHint = base.findViewById(R.id.hint);
		mEnable = true;
	}
	
	public final boolean isEnable() {
		return mEnable;
	}
	
	public final void setEnable(boolean enable) {
		if (mEnable == enable) return;
		mEnable = enable;
		if (enable) {
			mSolar.setTextColor(black);
		}
		else {
			mSolar.setTextColor(lightGray);
		}
	}
	
	public final void setFocusForce(boolean focus) {
		mBase.setEnabled(!focus);
	}
	
	public final void setSolar(String solar) {
		mSolar.setText(solar);
	}
	
	@SuppressLint("NewApi")
	public final void setHint(int state) {
		if (state == mState) return;
		mState = state;
		switch(state) {
		case State_Default:
			mHint.setBackgroundResource(0);
			break;
		case State_FirstDay:
			mHint.setBackgroundResource(R.drawable.red);
			break;
		case State_Period:
			mHint.setBackgroundResource(R.drawable.yellow);
			break;
		case State_FirstFertile:
			mHint.setBackgroundResource(R.drawable.violet);
			break;
		case State_Fertile:
			mHint.setBackgroundResource(R.drawable.purple);
			break;
		}
	}

}
