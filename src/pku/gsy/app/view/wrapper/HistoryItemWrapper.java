package pku.gsy.app.view.wrapper;

import android.view.View;
import android.widget.TextView;

public class HistoryItemWrapper extends BaseWrapper {
	
	private static final String link = " / ";
	
	private TextView mText = null;

	public HistoryItemWrapper(View base) {
		super(base);
		
		mText = (TextView) mBase;
	}
	
	public void setText(CharSequence text) {
		mText.setText(text);
	}
	
	public void setText(int year, int month, int day) {
		StringBuilder builder = new StringBuilder();
		builder.append(year);
		builder.append(link);
		builder.append(month);
		builder.append(link);
		builder.append(day);
		mText.setText(builder);
	}

}
