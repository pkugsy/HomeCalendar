package pku.gsy.app.plugins;

import java.util.Calendar;
import java.util.List;

import pku.gsy.app.R;
import pku.gsy.app.view.adapter.DateMensesAdapter;
import pku.gsy.app.view.wrapper.MensesDateWrapper;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;

public class MensesCalendarPlugin implements OnTouchListener, OnItemClickListener {
	
	private GridView mGrid = null;
	private List<MensesDateWrapper> mWrappers = null;
	private Calendar mDate = null;
	private int mFocusIndex = -1;
	
	public MensesCalendarPlugin(View base) {
		mGrid = (GridView) base.findViewById(R.id.grid_calendar);
		DateMensesAdapter mAdapter = new DateMensesAdapter(mGrid);
		mGrid.setAdapter(mAdapter);
		mWrappers = mAdapter.getWrappers();
		mDate = Calendar.getInstance();
		int date = mDate.get(Calendar.DAY_OF_MONTH);
		mDate.set(Calendar.DAY_OF_MONTH, 1);
		int week = mDate.get(Calendar.DAY_OF_WEEK);
		mFocusIndex = date + week - 2;
		
		mGrid.setOnItemClickListener(this);
		base.findViewById(R.id.table_weektitle).setOnTouchListener(this);
	}
	
	public long setCalendar(Calendar referDate) {
		long timeStamp = 0L;
		mDate.setTimeInMillis(referDate.getTimeInMillis());
		mDate.set(Calendar.DAY_OF_MONTH, 1);
		int mWeekday = mDate.get(Calendar.DAY_OF_WEEK);
		int lastOffset = mWeekday - 1;
		
		mDate.add(Calendar.MONTH, 1);
		mDate.add(Calendar.DAY_OF_MONTH, -1);
		int mMonthLength = mDate.get(Calendar.DAY_OF_MONTH);
		int nextOffset = lastOffset + mMonthLength;
		
		mDate.set(Calendar.DAY_OF_MONTH, 1);
		mDate.add(Calendar.DAY_OF_MONTH, -lastOffset);
		
		timeStamp = mDate.getTimeInMillis();
		
		MensesDateWrapper date = null;
		int i = 0;
		for (i = 0; i < 42; ++i) {
			date = mWrappers.get(i);
			date.setEnable(i < lastOffset ? false : (i >= nextOffset ? false : true));
			date.setSolar(gregorianDateNames[mDate.get(Calendar.DAY_OF_MONTH)]);
			date.setHint(MensesDateWrapper.State_Default);
			
			mDate.add(Calendar.DAY_OF_MONTH, 1);
		}
		
		mWrappers.get(mFocusIndex).setFocusForce(true);
		return timeStamp;
	}

	public void setHint(int index, int state) {
		mWrappers.get(index).setHint(state);
	}
	
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int pos, long id) {
	}

	@Override
	public boolean onTouch(View view, MotionEvent event) {
		return true;
	}
	
	public final String[] gregorianDateNames = { null, // 占位符
		"1", "2", "3", "4", "5", "6", "7", "8", "9", "10", 
		"11", "12", "13", "14", "15", "16", "17", "18", "19", "20",
		"21", "22", "23", "24", "25", "26", "27", "28", "29", "30",
		"31"
	};
}
