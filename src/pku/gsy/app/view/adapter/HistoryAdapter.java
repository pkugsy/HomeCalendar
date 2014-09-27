package pku.gsy.app.view.adapter;

import java.util.List;

import pku.gsy.app.R;
import pku.gsy.app.util.MensesDate;
import pku.gsy.app.view.wrapper.HistoryItemWrapper;
import android.view.View;
import android.view.ViewGroup;

public class HistoryAdapter extends CalBaseAdapter<HistoryItemWrapper> {
	
	public HistoryAdapter(ViewGroup parent, List<MensesDate> referMDate) {
		super(parent, R.layout.layout_history_item, referMDate.size() - 3);
		
		HistoryItemWrapper wrapper = null;
		final int size = referMDate.size();
		for (int i = size - 1; i > 2; --i) {
			View view = newOneView(true);
			wrapper = new HistoryItemWrapper(view);
			MensesDate tp = referMDate.get(i);
			wrapper.setText(tp.year, tp.month, tp.day);
			view.setTag(wrapper);
			
			addToWrappers(wrapper);
		}
	}
	
	public void updateAll(List<MensesDate> referMDate) {
		final int size = referMDate.size();
		for (int i = size - 1; i > 2; --i) {
			MensesDate tp = referMDate.get(i);
			getWrapper(size - i - 1).setText(tp.year, tp.month, tp.day);
		}
		
		notifyDataSetChanged();
	}
	
	public void updateOne(int pos, MensesDate date) {
		getWrapper(pos).setText(date.year, date.month, date.day);
	}

}
