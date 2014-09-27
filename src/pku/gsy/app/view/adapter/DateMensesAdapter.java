package pku.gsy.app.view.adapter;

import pku.gsy.app.R;
import pku.gsy.app.view.wrapper.MensesDateWrapper;
import android.view.View;
import android.view.ViewGroup;

public class DateMensesAdapter extends CalBaseAdapter<MensesDateWrapper> {
	
	public DateMensesAdapter(ViewGroup parent) {
		super(parent, R.layout.layout_date_menses, 42);
		
		MensesDateWrapper wrapper = null;
		for (int i = 0; i < 42; ++i) {
			View view = newOneView(false);
			wrapper = new MensesDateWrapper(view);
			view.setTag(wrapper);
			
			addToWrappers(wrapper);
		}
	}

}
