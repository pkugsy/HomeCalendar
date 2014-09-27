package pku.gsy.app.stubs;

import pku.gsy.app.R;
import pku.gsy.app.util.content.AnimationHelper;
import pku.gsy.app.util.content.AnimationHelper.OnAnimationFinishListener;
import pku.gsy.app.view.adapter.SettingsAdapter;
import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewStub;
import android.view.animation.Animation;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

public class MainSettingsStub {
	
	private Context mContext = null;
	private ViewStub mStub = null;
	
	private View mSettingsOutside = null;
	private ListView mSettingsList = null;
	private SettingsAdapter mAdapter = null;
	private boolean isFirstTime = true;
	private boolean isShow = false;
    private boolean isOutsideClickable = true;
    
    private OnSettingsItemClickListener mListener = null;
	
    private AnimationHelper mAnimHelper = null;
	private Animation alphaInAnim = null;
    private Animation zoomInAnim = null;
    private Animation alphaOutAnim = null;
    private Animation zoomOutAnim = null;
    
    private OnAnimationFinishListener mResetDisableOutsideClick = new OnAnimationFinishListener() {
		@Override
		public void onAnimationFinish() {
			isOutsideClickable = true;
		}
	};
	
	public MainSettingsStub(Context ctx, ViewStub baseStub) {
		mContext = ctx;
		mStub = baseStub;
		mAnimHelper = AnimationHelper.getInstance(mContext);
	}
	
	public final boolean isShow() {
		return (this.isShow);
	}
	
	public void toggle() {
		if (isShow) {
			hide();
		} else {
			show();
		}
	}
	
	public void show() {
		if (isShow) return;
		isShow = true;
		
		if (isFirstTime) {
			isFirstTime = false;
			inflateViewStub();
		}
		
		isOutsideClickable = false;
		mAnimHelper.setOnAnimationFinishListener(mResetDisableOutsideClick);
		mSettingsOutside.clearAnimation();
		mSettingsList.clearAnimation();
    	mSettingsOutside.startAnimation(alphaInAnim);
    	mSettingsList.startAnimation(zoomInAnim);
    	mSettingsOutside.setVisibility(View.VISIBLE);
    	mSettingsList.setVisibility(View.VISIBLE);
	}
	
	public void hide() {
		if (isShow) {
    		isShow = false;
    		isOutsideClickable = false;
    		mAnimHelper.setOnAnimationFinishListener(mResetDisableOutsideClick);
    		mSettingsOutside.clearAnimation();
    		mSettingsList.clearAnimation();
	    	mSettingsOutside.startAnimation(alphaOutAnim);
	    	mSettingsList.startAnimation(zoomOutAnim);
	    	mSettingsOutside.setVisibility(View.GONE);
	    	mSettingsList.setVisibility(View.GONE);
		}
	}
	
	private final void inflateViewStub() {
		View mRootView = mStub.inflate();
		mSettingsOutside = mRootView.findViewById(R.id.settings_outside);
    	mSettingsList = (ListView) mRootView.findViewById(pku.gsy.app.R.id.settings_list);
    	mAdapter = new SettingsAdapter(mSettingsList);
    	mSettingsList.setAdapter(mAdapter);
    	
    	mSettingsOutside.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) { if (isOutsideClickable) hide();}
		});
    	
    	mSettingsList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int pos, long id) {
			    
			    if (mListener != null) {
			        mListener.OnSettingsItemClick(pos);
			    }
			    
				hide();
			}
		});
    	
    	initAnimation();
    }
	
	private final void initAnimation() {
		AnimationHelper animHelper = AnimationHelper.getInstance(mContext);
		alphaInAnim = animHelper.getOutsideInAnim();
		alphaOutAnim = animHelper.getOutsideOutAnim();
	    zoomInAnim = animHelper.getSettingsInAnim();
	    zoomOutAnim = animHelper.getSettingsOutAnim();
	}
	
	public void setOnSettingsItemClickListener(OnSettingsItemClickListener listener) {
	    this.mListener = listener;
	}
	
	public interface OnSettingsItemClickListener {
	    public void OnSettingsItemClick(int id);
	}
}
