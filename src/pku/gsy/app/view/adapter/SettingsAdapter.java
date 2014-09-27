package pku.gsy.app.view.adapter;

import pku.gsy.app.R;
import pku.gsy.app.view.wrapper.SettingsOptionWrapper;
import android.view.View;
import android.view.ViewGroup;

public class SettingsAdapter extends CalBaseAdapter<SettingsOptionWrapper> {
	
	private static final int[] mIcons = {
		R.drawable.option, R.drawable.option, R.drawable.option, 
		R.drawable.option, R.drawable.option};
	
	private static final String[] mContents = {
		"节日管理", "生日管理", "日程管理", "天气预报", "退出应用"};
	
	public static final int SettingsCount = mContents.length;
	
	public SettingsAdapter(ViewGroup parent) {
		super(parent, R.layout.layout_settings_item, SettingsCount);
		
		SettingsOptionWrapper wrapper = null;
		for (int i = 0; i < SettingsCount; ++i) {
			View view = newOneView(true);
			wrapper = new SettingsOptionWrapper(view);
			wrapper.setIcon(mIcons[i]);
			wrapper.setContent(mContents[i]);
			view.setTag(wrapper);
			
			addToWrappers(wrapper);
		}
	}

}
