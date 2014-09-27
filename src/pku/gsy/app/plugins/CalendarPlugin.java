package pku.gsy.app.plugins;

import java.util.List;

import pku.gsy.app.R;
import pku.gsy.app.db.DbHelper;
import pku.gsy.app.util.FestInOneDay;
import pku.gsy.app.util.content.StarIndexer;
import pku.gsy.app.util.date.DateHelper;
import pku.gsy.app.util.date.DateInfo;
import pku.gsy.app.view.adapter.DateAdapter;
import pku.gsy.app.view.wrapper.DateWrapper;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;

public class CalendarPlugin implements OnTouchListener, OnItemClickListener {
	
	private static final String chuxi = "除夕";
	private static final String muqinjie = "母亲节";
	private static final String fuqinjie = "父亲节";
	
	private GridView mGrid = null;
	private List<DateWrapper> mWrappers = null;
	private DateInfo mDate = new DateInfo();
	private DateHelper mDateHelper = null;
	private DateWrapper mLastFocusCell = null;
	private int mLastFocusIndex = 0;
	private boolean isPreFocus = false;
	private OnDateClickListener dateClickListener = null;
	
	// 保持calendar状态的数据集合
	private int mWeekday = 1;
	private int mMonthLength = 30;
	private DateInfo mParamDate = new DateInfo();
	private FestInOneDay mParamFest = new FestInOneDay();
	private DbHelper dbHelper = null;
	
	private int mChuxiIndex = -1;
	private int mMuqinjieIndex = -1;
	private int mFuqinjieIndex = -1;
	private int mSectionIndex = -1;
	private int mPrincipleIndex = -1;
	
	
	public CalendarPlugin(View base) {
		mGrid = (GridView) base.findViewById(R.id.grid_calendar);
		DateAdapter mAdapter = new DateAdapter(mGrid);
		mGrid.setAdapter(mAdapter);
		mWrappers = mAdapter.getWrappers();
		mDateHelper = DateHelper.getInstance();
		isPreFocus = false; // 确保如果setFocus首先被调用，可以将focus焦点至于相应cell
		mLastFocusCell = mWrappers.get(mLastFocusIndex);
		dbHelper = DbHelper.getInstance(mGrid.getContext());
		
		// 代理GridView的onClicklistener
		mGrid.setOnItemClickListener(this);
		// touch返回true，使workspace正常运行
		// mGrid.setOnTouchListener(this); // 因为有了onItemClickListener，这个就不需要了
		base.findViewById(R.id.table_weektitle).setOnTouchListener(this);
	}
	
	@Override
	public boolean onTouch(View view, MotionEvent event) {
		return true;
	}
	
	/**
	 * 此函数要求calendar必须从1号开始，并且对此不做检查。
	 */
	public void setCalendar(DateInfo referDate) {
		// 创建临时日期数据mDate
		mDateHelper.setDate(referDate, mDate);
		// 创建本月日历，为逻辑上简洁，对齐到本月1日
		if (mDate.gDay != 1) {
			mDateHelper.addDays(mDate, 1 - mDate.gDay);
		}
		mWeekday = mDateHelper.dayOfWeek(mDate);
		
		mMonthLength = mDateHelper.daysInMonth(mDate.gYear, mDate.gMonth);
		int lastOffset = mWeekday - 1;
		int nextOffset = lastOffset + mMonthLength;
		
		// 除夕，母亲节，父亲节这三个节日没有具体日期，
		// 在想到更好实现办法前，暂时先特殊处理
		mChuxiIndex = mMuqinjieIndex = mFuqinjieIndex = -1;
		if (5 == mDate.gMonth) {
			mMuqinjieIndex = (mWeekday == 1 ? 7 : 14);
		} else if (6 == mDate.gMonth) {
			mFuqinjieIndex = (mWeekday == 1 ? 14 : 21);
		}
		mSectionIndex = mWeekday + mDateHelper.sectionalTerm(mDate.gYear, mDate.gMonth) - 2;
		mPrincipleIndex = mWeekday + mDateHelper.principleTerm(mDate.gYear, mDate.gMonth) - 2;
		int tpMonth = mDate.gMonth + 1;
		if (tpMonth > 12) tpMonth = 1;
		int tpYear = (tpMonth == 1 ? (mDate.gYear + 1) : mDate.gYear);
		int tpSectionIndex = nextOffset + mDateHelper.sectionalTerm(tpYear, tpMonth) - 1;
		mDateHelper.addDays(mDate, -lastOffset);
		
		DateWrapper date = null;
		String gFest = null, cFest = null;
		for (int i = 0; i < 42; ++i) {
			date = mWrappers.get(i);
			date.setEnable(i < lastOffset ? false : (i >= nextOffset ? false : true));
			date.setSolar(mDateHelper.gregorianDateNames[mDate.gDay]);
			date.setLunar(mDateHelper.chineseDateNames[mDate.cDay]);
			
			cFest = dbHelper.getAcFest(mDate.cMonth, mDate.cDay);
			
			if (i == mSectionIndex) gFest = mDateHelper.GetSectionalTermName(mDate.gMonth);
			else if (i == mPrincipleIndex) gFest = mDateHelper.GetPrincipleTermName(mDate.gMonth);
			else if (i == tpSectionIndex) gFest = mDateHelper.GetSectionalTermName(tpMonth);
			else gFest = dbHelper.getAgFest(mDate.gMonth, mDate.gDay);
			
			if (null != gFest && null == cFest) {
				if (5 == mDate.gMonth) {
					if (mMuqinjieIndex == i) {cFest = gFest; gFest = muqinjie;}
				} else if (6 == mDate.gMonth) {
					if (mFuqinjieIndex == i) {cFest = gFest; gFest = fuqinjie;}
				}
			}
			else {
				if (5 == mDate.gMonth) {
					if (mMuqinjieIndex == i) gFest = muqinjie;
				} else if (6 == mDate.gMonth) {
					if (mFuqinjieIndex == i) gFest = fuqinjie;
				}
			}
			
			if (null == gFest) {
				if (cFest != null) date.setHint(cFest);
				else date.setHint(null);
			}
			else {
				date.setHint(gFest);
				if (cFest != null) date.setLunar(cFest);
			}
			
			mDateHelper.rollUpOneDay(mDate);
			if (1 == mDate.cDay && 1 == mDate.cMonth) {
				if (null == gFest) date.setHint(chuxi);
				else date.setLunar(chuxi);
				mChuxiIndex = i;
			}
		}
		
		mDateHelper.setDate(referDate, mDate);
	}
	
	public void setOnDateClickListener(OnDateClickListener listener) {
		this.dateClickListener = listener;
	}
	
	public int getFocusIndex() {
		return mLastFocusIndex;
	}
	
	public void setFocusOnDay(DateInfo date, int day) {
		mLastFocusIndex = mDateHelper.dayOfWeek(date) + day - 2;
		mLastFocusCell.setFocusForce(false);
		mLastFocusCell = mWrappers.get(mLastFocusIndex);
		mLastFocusCell.setFocusForce(true);
		if (null != dateClickListener) onDateClick(mLastFocusIndex);
		isPreFocus = false;
	}
	
	/**
	 * 仅将focus的焦点对准指定的index，而不触发ItemClick事件。
	 * 比如，日历滑动到下一个月的动作前，将焦点对准指定位置，
	 * 但是由于未必滑动到一个月，所以，可以仅将焦点先对准index位置。
	 */
	public void setPreFocus(int index) {
		if (index < 0 || index > 41) return;
		mLastFocusIndex = index;
		mLastFocusCell.setFocusForce(false);
		mLastFocusCell = mWrappers.get(index);
		mLastFocusCell.setFocusForce(true);
		isPreFocus = true;
	}
	
	public void setFocus(int index) {
		if (index < 0 || index > 41) return;
		if (!isPreFocus || mLastFocusIndex != index) {
			mLastFocusIndex = index;
			mLastFocusCell.setFocusForce(false);
			mLastFocusCell = mWrappers.get(index);
			mLastFocusCell.setFocusForce(true);
		}
		if (null != dateClickListener) onDateClick(mLastFocusIndex);
		isPreFocus = false;
	}
	
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int pos, long id) {
		DateWrapper tp = mWrappers.get(pos);
		if (tp.isEnable()) {
			mLastFocusIndex = pos;
			mLastFocusCell.setFocusForce(false);
			mLastFocusCell = tp;
			mLastFocusCell.setFocus(true);
			if (null != dateClickListener) onDateClick(pos);
			isPreFocus = false;
		}
		else if ((pos == 38 || pos == 40) && null != dateClickListener) {
			dateClickListener.dateClick(pos, null, null, null, 0);
		}

	}
	
	private final void onDateClick(int pos) {
		mDateHelper.setDate(mDate, mParamDate);
		mDateHelper.addDays(mParamDate, pos - mWeekday + 1);
		mParamFest.gFest = dbHelper.getAgFest(mParamDate.gMonth, mParamDate.gDay);
		mParamFest.cFest = dbHelper.getAcFest(mParamDate.cMonth, mParamDate.cDay);
		if (pos == mMuqinjieIndex) {
			if (5 == mParamDate.gMonth) mParamFest.other = muqinjie;
		} else if (pos == mFuqinjieIndex) {
			if (6 == mParamDate.gMonth) mParamFest.other = fuqinjie;
		} else if (pos == mChuxiIndex) {
			mParamFest.cFest = chuxi;
		}
		// 设置生日，在这里
		// TODO:
		String solarTerm = null;
		if (pos == mSectionIndex) {
			solarTerm = mDateHelper.GetSectionalTermName(mParamDate.gMonth);
		}
		else if (pos == mPrincipleIndex) {
			solarTerm = mDateHelper.GetPrincipleTermName(mParamDate.gMonth);
		}
		dateClickListener.dateClick(pos, mParamDate, mParamFest, solarTerm, StarIndexer.star(mParamDate.gMonth, mParamDate.gDay));
	}
	
	public interface OnDateClickListener {
		public void dateClick(int pos, DateInfo date, FestInOneDay fests, String solarTerm, int starIndex);
	}
}