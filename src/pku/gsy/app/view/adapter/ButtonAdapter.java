package pku.gsy.app.view.adapter;

import pku.gsy.app.view.wrapper.ButtonWrapper;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;

public class ButtonAdapter extends CalBaseAdapter<ButtonWrapper> {
	
	public static final CharSequence[] KEYS = {
		"7", "8", "9",
		"4", "5", "6",
		"1", "2", "3",
		"Back", "0", "OK"
	};
	
	public ButtonAdapter(ViewGroup parent, int layoutId, int count) {
		super(parent, layoutId, count);
		
		ButtonWrapper wrapper = null;
		for (int i = 0; i < count; ++i) {
			View view = newOneView(true);
			wrapper = new ButtonWrapper(view);
			wrapper.setText(KEYS[i]);
			view.setTag(wrapper);
			
			addToWrappers(wrapper);
		}
		
		getWrapper(9).getButton().setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
		getWrapper(11).getButton().setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
	}

}
