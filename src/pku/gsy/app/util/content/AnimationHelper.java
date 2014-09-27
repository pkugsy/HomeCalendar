package pku.gsy.app.util.content;

import pku.gsy.app.CalendarActivity;
import pku.gsy.app.MensesActivity;
import pku.gsy.app.R;
import pku.gsy.app.view.adapter.SettingsAdapter;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Point;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationSet;
import android.view.animation.AnticipateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.view.animation.OvershootInterpolator;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;

public class AnimationHelper {
	
	private static int alphaInTime = 600;
	private static int defaultTime = 400;
	private static float defaultVelocity = 50.0f;
	
	private static AnimationHelper mInstance = null;
	private static AppDimenInfo mDimen = null;
	
	public static AnimationHelper getInstance(Context context) {
		if (null == mDimen) doMeasure(context);
		AnimationHelper ins = null;
		if (CalendarActivity.class == context.getClass() ||
				MensesActivity.class == context.getClass()) {
			if (null == mInstance) {
				mInstance = new AnimationHelper();
			}
			ins = mInstance;
		}
		return ins;
	}
	
	private AnimationHelper() {
	}
	
	private Animation outsideInAnim = null;
	private Animation outsideOutAnim = null;
	private AnimationSet pickerInAnim = null;
	private Animation pickerOutAnim = null;
	private Animation settingsInAnim = null;
	private Animation settingsOutAnim = null;
	private Interpolator inInterpolator = new DecelerateInterpolator();
	private Interpolator outInterpolator = new AnticipateInterpolator();
	private OnAnimationFinishListener mAnimFinishListener = null;
	
	public final void setOnAnimationFinishListener(OnAnimationFinishListener listener) {
		mAnimFinishListener = listener;
	}
	
	public Animation getOutsideInAnim() {
		if (null == outsideInAnim) {
			outsideInAnim = new AlphaAnimation(0.1f, 1.0f);
			outsideInAnim.setDuration(alphaInTime);
			outsideInAnim.setAnimationListener(mAnimListener);
		}
		return outsideInAnim;
	}
	
	public Animation getOutsideOutAnim() {
		if (null == outsideOutAnim) {
			outsideOutAnim = new AlphaAnimation(1.0f, 0.1f);
			outsideOutAnim.setDuration(defaultTime);
			outsideOutAnim.setAnimationListener(mAnimListener);
		}
		return outsideOutAnim;
	}
	
	public Animation getPickerInAnim() {
		if (null == pickerInAnim) {
			// Relative position of DatePicker and NavTitle
			float relPosY = mDimen.navCenterY - (mDimen.windowHeight >> 1);
			// Scale rate
			float widthScale = mDimen.navWidth / mDimen.datePickerWidth;
			float heightScale = mDimen.navHeight / mDimen.datePickerHeight;
			pickerInAnim = new AnimationSet(true);
			pickerInAnim.addAnimation(
					new ScaleAnimation(widthScale, 1.0f, heightScale, 1.0f, 
					Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f));
			pickerInAnim.addAnimation(
					new TranslateAnimation(0.0f, 0.0f, relPosY, 0.0f));
			pickerInAnim.setDuration(mDimen.navTime);
			pickerInAnim.setInterpolator(inInterpolator);
		}
		return pickerInAnim;
	}
	
	public Animation getPickerOutAnim() {
		if (null == pickerOutAnim) {
			// Scale rate
			pickerOutAnim = new ScaleAnimation(1.0f, 0.9f, 1.0f, 0.1f, 
	    			Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
			pickerOutAnim.setDuration(mDimen.navTime);
			pickerOutAnim.setInterpolator(outInterpolator);
		}
		return pickerOutAnim;
	}
	
	public Animation getSettingsInAnim() {
		if (null == settingsInAnim) {
			float relPosY = - (mDimen.settingsHeight / 2) - (mDimen.windowHeight >> 1);
			
			settingsInAnim = new TranslateAnimation(0.0f, 0.0f, relPosY, 0.0f);

			settingsInAnim.setDuration(mDimen.settingsTime);
			settingsInAnim.setInterpolator(new OvershootInterpolator());
		}
		return settingsInAnim;
	}
	
	public Animation getSettingsOutAnim() {
		if (null == settingsOutAnim) {
			float relPosY = - (mDimen.settingsHeight / 2) - (mDimen.windowHeight >> 1);
			
			settingsOutAnim = new TranslateAnimation(0.0f, 0.0f, 0.0f, relPosY);

			settingsOutAnim.setDuration(mDimen.settingsTime);
			settingsOutAnim.setInterpolator(inInterpolator);
		}
		return settingsOutAnim;
	}
	
	private static void doMeasure(Context context) {
		mDimen = new AppDimenInfo();
		Point outSize = new Point();
		final Resources res = context.getResources();
		((WindowManager)context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getSize(outSize);
		mDimen.windowWidth = outSize.x;
		mDimen.windowHeight = outSize.y;
		
		float btnWidth = res.getDimension(R.dimen.w_button);
		float btnHeight = res.getDimension(R.dimen.h_button);
		float nestPadding = res.getDimension(R.dimen.p_nest);
		float titleHeight = res.getDimension(R.dimen.h_title);
		
		// mDimen.navCenterX = (mDimen.windowWidth >> 1);
		mDimen.navCenterY = titleHeight + nestPadding + btnHeight / 2;
		mDimen.navWidth = mDimen.windowWidth - btnWidth * 2 - nestPadding * 4;
		mDimen.navHeight = btnHeight;
		
		mDimen.datePickerWidth = res.getDimension(R.dimen.w_dialog);
		mDimen.datePickerHeight = res.getDimension(R.dimen.h_picker);
		
		// mDimen.settingsBtnCenterX = mDimen.windowWidth - nestPadding - btnWidth / 2;
		// mDimen.settingsBtnCenterY = nestPadding + btnHeight / 2;
		// mDimen.settingsBtnWidth = btnWidth;
		// mDimen.settingsBtnHeight = btnHeight;
		
		// mDimen.settingsWidth = res.getDimension(R.dimen.w_dialog);
		mDimen.settingsHeight = titleHeight * SettingsAdapter.SettingsCount;
		
		float settingsRelPosY = (mDimen.settingsHeight / 2) + (mDimen.windowHeight >> 1);
		float navRelPosY = (mDimen.windowHeight >> 1) - mDimen.navCenterY;
		defaultVelocity = settingsRelPosY / mDimen.settingsTime;
		mDimen.navTime = (int) (navRelPosY / defaultVelocity);
	}
	
	private AnimationListener mAnimListener = new AnimationListener() {
		@Override
		public void onAnimationStart(Animation animation) {
		}
		@Override
		public void onAnimationRepeat(Animation animation) {
		}
		@Override
		public void onAnimationEnd(Animation animation) {
			if (mAnimFinishListener != null) mAnimFinishListener.onAnimationFinish();
		}
	};
	
	private static class AppDimenInfo {
		public int windowWidth = 0;
		public int windowHeight = 0;
		
		public float navCenterY = 0f;
		public float navWidth = 0f;
		public float navHeight = 0f;
		public float datePickerWidth = 0f;
		public float datePickerHeight = 0f;
		public int navTime = defaultTime;
		
		// public float settingsBtnCenterX = 0f;
		// public float settingsBtnCenterY = 0f;
		// public float settingsBtnWidth = 0f;
		// public float settingsBtnHeight = 0f;
		// public float settingsWidth = 0f;
		public float settingsHeight = 0f;
		public int settingsTime = defaultTime;
	}
	
	public interface OnAnimationFinishListener {
		public void onAnimationFinish();
	}
}
