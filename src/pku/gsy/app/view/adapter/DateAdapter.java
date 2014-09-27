package pku.gsy.app.view.adapter;

import pku.gsy.app.R;
import pku.gsy.app.view.wrapper.DateWrapper;
import android.view.View;
import android.view.ViewGroup;

public class DateAdapter extends CalBaseAdapter<DateWrapper> {
	
	public DateAdapter(ViewGroup parent) {
		super(parent, R.layout.layout_date, 42);
		
		DateWrapper wrapper = null;
		for (int i = 0; i < 42; ++i) {
			View view = newOneView(false);
			wrapper = new DateWrapper(view);
			view.setTag(wrapper);
			
			addToWrappers(wrapper);
		}
	}
	
}
