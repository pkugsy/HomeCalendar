package pku.gsy.app.plugins;

import pku.gsy.app.R;
import pku.gsy.app.util.FestInOneDay;
import pku.gsy.app.util.content.StarIndexer;
import pku.gsy.app.util.date.DateInfo;
import android.view.View;
import android.widget.TextView;

public class Hint3Plugin {
    
    private static final CharSequence FEST_SEPARATOR = " | ";
	
	private View mImage = null;
	private TextView mHint = null;
	
	private int mLastStarIndex = -1;
	
	public Hint3Plugin(View base) {
		mImage = base.findViewById(R.id.img_star);
		mHint = (TextView) base.findViewById(R.id.txt_hint3);
	}
	
	public void setHint(DateInfo date, FestInOneDay fest, String solarTerm, int starIndex) {
		
		if (mLastStarIndex != starIndex) {
			mImage.setBackgroundResource(StarIndexer.starResId[starIndex]);
			mLastStarIndex = starIndex;
		}
		
		StringBuffer hint = new StringBuffer();
		
		hint.append(StarIndexer.starName[starIndex]);
		
		if (null != fest.other) {
		    hint.append(FEST_SEPARATOR);
		    hint.append(fest.other);
		}
		if (null != fest.cFest) {
		    hint.append(FEST_SEPARATOR);
		    hint.append(fest.cFest);
		}
		if (null != fest.gFest) {
		    hint.append(FEST_SEPARATOR);
		    hint.append(fest.gFest);
		}
		if (null != solarTerm) {
		    hint.append(FEST_SEPARATOR);
		    hint.append(solarTerm);
		}
		if (null != fest.birth) {
		    hint.append(FEST_SEPARATOR);
		    hint.append(fest.birth);
		}
		
		mHint.setText(hint.toString());
	}
}
