package pku.gsy.app.plugins;

import pku.gsy.app.R;
import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.widget.Button;

public class DateNaviPlugin implements OnClickListener {
	
	public static final int leftBtnId = R.id.btn_nav_left;
	public static final int rightBtnId = R.id.btn_nav_right;
	public static final int titleBtnId = R.id.btn_nav_datetitle;
	
	private View mBase = null;
	private Button mDateTitle = null;
	private OnClickListener mClickListener = null;
	
	private AlphaAnimation mFadeInAnim = null;
	private AlphaAnimation mFadeOutAnim = null;
	
	public DateNaviPlugin(Context context, View base) {
		mBase = base;
		mDateTitle = (Button) base.findViewById(R.id.btn_nav_datetitle);
		
		mFadeInAnim = new AlphaAnimation(0.4f, 1.0f);
		mFadeOutAnim = new AlphaAnimation(1.0f, 0.3f);
		mFadeInAnim.setDuration(120);
		mFadeOutAnim.setDuration(250);
		mFadeOutAnim.setAnimationListener(mFadeOutListener);
		
		mDateTitle.setOnClickListener(this);
		base.findViewById(R.id.btn_nav_left).setOnClickListener(this);
		base.findViewById(R.id.btn_nav_right).setOnClickListener(this);
	}
	
	public View getBase() {
		return mBase;
	}
	
	private int tpYear = 0;
	private int tpMonth = 0;
	public void setTitle(int year, int month, boolean isAnim) {
		if (isAnim) {
			tpYear = year;
			tpMonth = month;
			
			mDateTitle.clearAnimation();
			mDateTitle.startAnimation(mFadeOutAnim);
		} else {
			mDateTitle.setText(year + "年" + month + "月");
		}
    }
	
	private AnimationListener mFadeOutListener = new  AnimationListener() {
		@Override
		public void onAnimationEnd(Animation animation) {
			mDateTitle.setText(tpYear + "年" + tpMonth + "月");
			mDateTitle.clearAnimation();
			mDateTitle.startAnimation(mFadeInAnim);
		}
		@Override
		public void onAnimationStart(Animation animation) {
		}
		@Override
		public void onAnimationRepeat(Animation animation) {
		}
	};
	
	public void setOnClickListener(OnClickListener listener) {
		mClickListener = listener;
	}

	@Override
	public void onClick(View view) {
		if (null != mClickListener) {
			mClickListener.onClick(view);
		}
	}
	
}
