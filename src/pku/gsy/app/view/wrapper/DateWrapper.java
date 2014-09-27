package pku.gsy.app.view.wrapper;

import pku.gsy.app.R;
import pku.gsy.app.util.content.Key;
import android.view.View;
import android.widget.TextView;

public class DateWrapper extends BaseWrapper {
	
	private final int black = mBase.getResources().getColor(R.color.black);
	private final int red = mBase.getResources().getColor(R.color.red);
	private final int gray = mBase.getResources().getColor(R.color.gray);
	private final int lightGray = mBase.getResources().getColor(R.color.light_gray);
	
	private TextView mSolar = null;
	private TextView mLunar = null;
	private TextView mHint = null;
	private boolean mEnable = true;
	
	public DateWrapper(View base) {
		super(base);
		
		mSolar = (TextView) base.findViewById(R.id.txt_solar);
		mLunar = (TextView) base.findViewById(R.id.txt_lunar);
		mHint = (TextView) base.findViewById(R.id.txt_hint);
		mEnable = true;
	}
	
	public final View getBase() {
		return mBase;
	}
	
	public final boolean isEnable() {
		return mEnable;
	}
	
	public final void setFocus(boolean focus) {
		if (mEnable)
			mBase.setEnabled(!focus);
	}
	
	public final void setFocusForce(boolean focus) {
		mBase.setEnabled(!focus);
	}
	
	public final void setEnable(boolean enable) {
		if (mEnable == enable) return;
		mEnable = enable;
		if (enable) {
			mSolar.setTextColor(black);
			mLunar.setTextColor(gray);
			mHint.setTextColor(red);
		}
		else {
			mSolar.setTextColor(lightGray);
			mLunar.setTextColor(lightGray);
			mHint.setTextColor(lightGray);
		}
	}
	
	public final void setSolar(String solar) {
		mSolar.setText(solar);
	}
	
	public final void setLunar(String lunar) {
		mLunar.setText(lunar);
	}
	
	public final void setHint(String hint) {
		if (null == hint) {
			mHint.setText(Key.CBLANK);
		}
		else {
			StringBuilder builder = new StringBuilder();
			int len = hint.length() - 1;
			for (int i = 0; i < len; ++i) {
				builder.append(hint.charAt(i));
				builder.append(Key.cENTER);
			}
			builder.append(hint.charAt(len));
			mHint.setText(builder.toString());
		}
	}
}
