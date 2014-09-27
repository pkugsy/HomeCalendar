package pku.gsy.app.view.wrapper;

import pku.gsy.app.R;
import android.view.View;
import android.widget.TextView;

public class SettingsOptionWrapper extends BaseWrapper {
	
	private View mIcon = null;
	private TextView mContent = null;

	public SettingsOptionWrapper(View base) {
		super(base);

		mIcon = base.findViewById(R.id.settings_item_icon);
		mContent = (TextView) base.findViewById(R.id.settings_item_content);
	}
	
	public void setContent(String content) {
		mContent.setText(content);
	}
	
	public void setIcon(int iconId) {
		mIcon.setBackgroundResource(iconId);
	}
}
