package pku.gsy.app;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import pku.gsy.app.db.DbHelper;
import pku.gsy.app.plugins.AppTitlePlugin;
import pku.gsy.app.plugins.DateNaviPlugin;
import pku.gsy.app.plugins.MensesCalendarPlugin;
import pku.gsy.app.stubs.DateAdjustStub;
import pku.gsy.app.util.MensesDate;
import pku.gsy.app.util.SettingsParam;
import pku.gsy.app.util.content.Key;
import pku.gsy.app.view.HorizontalWorkspace;
import pku.gsy.app.view.adapter.ButtonAdapter;
import pku.gsy.app.view.adapter.HistoryAdapter;
import pku.gsy.app.view.listener.SubviewSlideInListener;
import pku.gsy.app.view.wrapper.MensesDateWrapper;
import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewStub;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class MensesActivity extends FullScreenBaseActivity implements 
    OnClickListener {
	
	private static final long OneDayMillisecond = 86400000L;
	
	private DbHelper mDbHelper = null;
	private Calendar mCurCal = null;
	private Calendar mNextCal = null;
	private Calendar mTpCal = null;
	private int mYear = 0;
	private int mMonth = 0;
	private int mDay = 0;
	private long mFrontTime = Long.MAX_VALUE;
	
	// 在SettingsHelper中可以使用
	private List<MensesDate> mMDates = null;
	private int mPeriod = 28;
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
    	if (keyCode==KeyEvent.KEYCODE_BACK) {
    		if (mSettingsHelper.isShow()) {
    			mSettingsHelper.hide();
    		}
    		else {
    			finish();
    		}
    		return true;
    	}
    	
    	return super.onKeyDown(keyCode, event);
	}
	
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mDbHelper = DbHelper.getInstance(this);
        
        setContentViewAndBindView();
	}

	private void setContentViewAndBindView() {
		setContentView(R.layout.activity_memses);
		
		mWorkspace = (HorizontalWorkspace) findViewById(R.id.layout_calendar_container);
		mWorkspace.setBeforeSlideInListener(new BeforeSlideInListener());
		mWorkspace.setAfterSlideInListener(new AfterSlideInListener());
		mAppTitleHelper = new AppTitlePlugin(this, findViewById(R.id.layout_app_title));
		mAppTitleHelper.setOnClickListener(this);
    	mDateNavHelper = new DateNaviPlugin(this, findViewById(R.id.layout_date_navigation_title));
    	mDateNavHelper.setOnClickListener(this);
    	mTxtHint = (TextView) findViewById(R.id.txt_hint_menses);
    	mTxtHint2 = (TextView) findViewById(R.id.txt_hint_menses2);
    	findViewById(R.id.btn_back).setOnClickListener(this);
    	mSettingsHelper = new MensesSettings(this, (ViewStub)findViewById(R.id.stub_settings_menses));
    	
    	View view = findViewById(R.id.calendar1);
        MensesCalendarPlugin chelper = new MensesCalendarPlugin(view);
        view.setTag(chelper);
        
        view = findViewById(R.id.calendar2);
        chelper = new MensesCalendarPlugin(view);
        view.setTag(chelper);
    	
        // 准备数据：当前年月日，周期起始点
    	mTpCal = Calendar.getInstance();
    	mNextCal = Calendar.getInstance();
    	mCurCal = Calendar.getInstance();
        mYear = mCurCal.get(Calendar.YEAR);
        mMonth = mCurCal.get(Calendar.MONTH) + 1;
        mDay = mCurCal.get(Calendar.DAY_OF_MONTH);
        long curTimeInMillis = mCurCal.getTimeInMillis();
        mCurCal.set(Calendar.DAY_OF_MONTH, 1);
        
        SettingsParam.read(this);
        mPeriod = SettingsParam.getPeriod();
        
        // *新打开MensesActivity时，补齐历史和新缺乏的周期日期
        // *其中，几个月前的用默认值28补齐，因为这个距今时间很长，而且是缺失的值
        // *到今天的新值，需要用设置周期来补足
        // *这个就开启Activity时一次被调用即可了。
        mMDates = mDbHelper.readDates();
        if (mMDates != null && mMDates.size() > 0) {
        	MensesDate tpDate = mMDates.get(0);
        	mTpCal.set(tpDate.year, tpDate.month - 1, tpDate.day);
        	for (int i = 0; i < 3; ++i) {
        		// 历史过去周期的补足，用默认值28
        		mTpCal.add(Calendar.DAY_OF_MONTH, -28);
        		mMDates.add(0, new MensesDate(
            		mTpCal.get(Calendar.YEAR), mTpCal.get(Calendar.MONTH) + 1, mTpCal.get(Calendar.DAY_OF_MONTH)));
        	}
        	mTpCal.add(Calendar.DAY_OF_MONTH, -42);
        	mFrontTime = mTpCal.getTimeInMillis();
        	
        	// 补齐历史上到现在缺少的周期起始点们
        	tpDate = mMDates.get(mMDates.size() - 1);
        	mTpCal.set(tpDate.year, tpDate.month - 1, tpDate.day);
        	mTpCal.add(Calendar.DAY_OF_MONTH, mPeriod);
        	MensesDate tpMensesDate = null;
        	while (mTpCal.getTimeInMillis() <= curTimeInMillis) {
        		tpMensesDate = new MensesDate(
                    mTpCal.get(Calendar.YEAR), mTpCal.get(Calendar.MONTH) + 1, mTpCal.get(Calendar.DAY_OF_MONTH));
        		long id = mDbHelper.addDate(tpMensesDate);
        		if (id > 0) {
        			tpMensesDate.id = (int) id;
        			mMDates.add(tpMensesDate);
        		}
        		
        		mTpCal.add(Calendar.DAY_OF_MONTH, mPeriod);
        	}
        	// 计算周期剩余天数有两种算法：
        	// 1，下一个周期减去今天的时间
        	// 2，周期数目减去（今天减去上一个周期的差）
        	// 这里选择了前一个方法。
        	int remain = (int) ((mTpCal.getTimeInMillis() - curTimeInMillis + 10800000L) / OneDayMillisecond);
        	mTxtHint.setText("Remain " + remain + " days to next peroid.");
        }
        else {
        	mTxtHint.setText("No hints!");
        }
        
        setRandomHint();
        
        
        // 准备好数据后，将日历界面刷新到当前月份
        mAppTitleHelper.setTitle(getResources().getString(R.string.app_name_menses));
        mAppTitleHelper.setLogoDate(mMonth, mDay);
        setToToday(false);
	}
	
	public void setToToday(boolean isAnim) {
		mCurCal.set(mYear, mMonth - 1, 1);
		final MensesCalendarPlugin helper = (MensesCalendarPlugin) mWorkspace.getCurrentScreen().getTag();
		long timeStamp = helper.setCalendar(mCurCal);
		setMensesHint(timeStamp, helper);
		mDateNavHelper.setTitle(mYear, mMonth, isAnim);
	}
	
	
	private void setMensesHint(long timeStamp, MensesCalendarPlugin helper) {
		final int size = mMDates.size();
		if (timeStamp <= mFrontTime || null == mMDates || size <= 0) return;
		
		int i = 1; // 设置为1是有用意的
		int firstIndex = 0, secondIndex = 0, _middleIndex = 0, middleIndex = 0;
		mTpCal.setTimeInMillis(mNextCal.getTimeInMillis());
		MensesDate tpd = mMDates.get(0);
		mTpCal.set(tpd.year, tpd.month - 1, tpd.day);
		long diff = timeStamp - mTpCal.getTimeInMillis();
		if (diff >= 0) {
			// 当第0个diff大于0时，说明整个展示月份都进入有周期数据的段
			for (i = 1; i < size; ++i) {
				tpd = mMDates.get(i);
				mTpCal.set(tpd.year, tpd.month - 1, tpd.day);
				long diff2 = timeStamp - mTpCal.getTimeInMillis();
				if (diff2 < 0L) break;
				diff = diff2;
			}
			
			secondIndex = (int) ((10800000L + diff) / OneDayMillisecond);
			if (i == size) secondIndex = secondIndex % mPeriod;
			secondIndex = -secondIndex;
		}
		else {
			// 当第0个diff小于0时，说明此展示月份只有后半段有周期数据，
			// 将secondIndex对其到这个周期起始点，于是使用了mMDates的第0个数据，故之后取数据从i=1开始。
			secondIndex = (int) ((10800000L - diff) / OneDayMillisecond);
		}
		
		// 为了使循环中将secondIndex值赋值给firstIndex，所以之前都是先给secondIndex赋值的。
		int state = MensesDateWrapper.State_Default;
		int c1 = 0, c2 = 0, j = i;
		for (i = secondIndex; i < 42; ++i) {
			if (i == secondIndex) {
				firstIndex = secondIndex;
				// 获取secondIndex
				if (j < size) {
					tpd = mMDates.get(j++);
					mTpCal.set(tpd.year, tpd.month - 1, tpd.day);
					diff = mTpCal.getTimeInMillis() - timeStamp;
					secondIndex = (int) ((diff + 10800000L) / OneDayMillisecond);
				} else {
					secondIndex = firstIndex + mPeriod;
				}
				
				middleIndex = ((firstIndex + secondIndex) >> 1);
				c1 = middleIndex - firstIndex;
				c1 = (c1 > 9 ? 4 : ((c1 >> 1) - 1));
				c2 = secondIndex - middleIndex;
				c2 = (c2 > 9 ? 6 : ((c2 >> 1) - 1));
				_middleIndex = middleIndex - (c2 >> 1);
			}
			
			if (i == firstIndex) {
				state = MensesDateWrapper.State_FirstDay;
			}
			else if (c1-- > 0) {
				state = MensesDateWrapper.State_Period;
			}
			else {
				if (i >= _middleIndex && c2-- > 0) {
					state = MensesDateWrapper.State_Fertile;
					if (i == middleIndex) {
						state = MensesDateWrapper.State_FirstFertile;
					}
				}
				else {
					state = MensesDateWrapper.State_Default;
				}
			}
			
			if (i >= 0) helper.setHint(i, state);
		}
		// End of "for (i = secondIndex; i < 42; ++i) {"
	}
	
	
	public void setRandomHint() {
		int pos = ((int)(Math.random() * 1000)) % RandomHint.count;
		String hint = RandomHint.hint[pos];
		
		mTxtHint2.setText(hint);
		mTxtHint2.setEllipsize(TextUtils.TruncateAt.valueOf("MARQUEE"));
	}
	
	
	@Override
	public void onClick(View view) {
    	switch(view.getId()) {
    	case DateNaviPlugin.leftBtnId:
    		mWorkspace.snapToScreen(1);
    		break;
    	case DateNaviPlugin.rightBtnId:
    		mWorkspace.snapToScreen(-1);
    		break;
    	case AppTitlePlugin.logoId:
    		setToToday(true);
    		break;
    	case AppTitlePlugin.settingsId:
    		mSettingsHelper.show();
    		break;
    	case R.id.btn_back:
    		finish();
    		break;
    	}
	}
	
	public void onSettingsChange() {
		mPeriod = SettingsParam.getPeriod();
		setToToday(true);
	}
	
	public class BeforeSlideInListener implements SubviewSlideInListener {
		@Override
		public void onSlide(View which, int direction) {
			final MensesCalendarPlugin helper = (MensesCalendarPlugin) which.getTag();
			mNextCal.setTimeInMillis(mCurCal.getTimeInMillis());
			switch (direction) {
			case SubviewSlideInListener.DirectionLeft:
				mNextCal.add(Calendar.MONTH, 1);
				break;
			case SubviewSlideInListener.DirectionRight:
				mNextCal.add(Calendar.MONTH, -1);
				break;
			}
			long timeStamp = helper.setCalendar(mNextCal);
			setMensesHint(timeStamp, helper);
		}
    }
	
	public class AfterSlideInListener implements SubviewSlideInListener {
		@Override
		public void onSlide(View which, int direction) {
			if (direction == SubviewSlideInListener.DirectionHere) return;
			
			Calendar tpCalendar = mCurCal;
			mCurCal = mNextCal;
			mNextCal = tpCalendar;
			
			mDateNavHelper.setTitle(mCurCal.get(Calendar.YEAR), mCurCal.get(Calendar.MONTH) + 1, true);
		}
    }
	
	private HorizontalWorkspace mWorkspace = null;
	private AppTitlePlugin mAppTitleHelper = null;
    private DateNaviPlugin mDateNavHelper = null;
    private TextView mTxtHint = null;
    private TextView mTxtHint2 = null;
    private MensesSettings mSettingsHelper = null;
    
    private static class RandomHint {
    	public static final String[] hint = {
    		"The soul cannot live without love.",
    		"There is no remedy for love but to love more.",
    		"We cease loving ourselves if no one loves us.",
    		"When love is not madness, it is not love.",
    		// "Wherever you go, whatever you do, I will be right here waiting for you.",
    		// "Who travels for love finds a thousand miles not longer than one.",
    		// "Within you I lose myself, without you I find myself wanting to be lost again.",
    		"A heart that loves is always young.",
    		"At the touch of love everyone becomes a poet.",
    		"Brief is life, but love is long.",
    		"Distance makes the hearts grow fonder.",
    		// "I miss you so much already and I haven’t even left yet!",
    		"I need him like I need the air to breathe.",
    		// "If equal affection cannot be, let the more loving be me.",
    		"If I know what love is, it is because of you.",
    		"I’ll think of you every step of the way.",
    		// "Look into my eyes - you will see what you mean to me.",
    		"Love is a vine that grows into our hearts.",
    		"Love is blind.",
    		// "Love is like the moon, when it does not increase, it decreases.",
    		"Love is the greatest refreshment in life.",
    		"Love keeps the cold out better than a cloak.",
    		"Love never dies.",
    		"My heart is with you.",
    		"Take away love, and our earth is a tomb.",
    		"The darkness is no darkness with thee."
    	};
    	
    	public static final int count = hint.length;
    }
    
    public class MensesSettings implements OnClickListener, OnItemClickListener, DateAdjustStub.OnAdjustFinishListener {
    	
    	private boolean mIsRender = true;
    	private boolean isShow = false;
    	private boolean isNumpadRender = true;
    	private boolean isNumpadShow = false;
    	
    	private Context mContext = null;
    	private ViewStub mStub = null;
    	private View mRootView = null;
    	
    	private TextView mPeriodValue = null;
    	private TextView mDurationValue = null;
    	private ListView mHistoryList = null;
    	private View mOption = null;
    	private View mLayoutPwd = null;
    	private TextView mTxtPwd = null;
    	private GridView mNumpad = null;
    	public HistoryAdapter mAdapter = null;
    	
    	private Animation mShowAnim = null;
    	private Animation mHideAnim = null;
    	private Animation mNumpadShowAnim = null;
    	private Animation mNumpadHideAnim = null;
    	
    	private DateAdjustStub mHistoryHelper = null;
    	
    	private int usePwd = 0;
    	private int pwdHash = 0;
    	private int period = 0;
    	private int duration = 0;
    	private Map<Integer, MensesDate> mChangedDates = null;
    	
    	public MensesSettings (Context context, ViewStub stub) {
    		mContext = context;
    		mStub = stub;
    	}
    	
    	private void inflateViewStub() {
    		mRootView = mStub.inflate();
    		
    		mRootView.findViewById(R.id.period_minus).setOnClickListener(this);
    		mRootView.findViewById(R.id.period_add).setOnClickListener(this);
    		mRootView.findViewById(R.id.duration_minus).setOnClickListener(this);
    		mRootView.findViewById(R.id.duration_add).setOnClickListener(this);
    		mRootView.findViewById(R.id.btn_ok).setOnClickListener(this);
    		mRootView.findViewById(R.id.btn_cancel).setOnClickListener(this);
    		mRootView.findViewById(R.id.option_password).setOnClickListener(this);
    		mRootView.findViewById(R.id.btn_setpassword).setOnClickListener(this);
    		
    		
    		mPeriodValue = (TextView) mRootView.findViewById(R.id.period_value);
    		mDurationValue = (TextView) mRootView.findViewById(R.id.duration_value);
    		mOption = mRootView.findViewById(R.id.option_view);
    		
			mHistoryHelper = new DateAdjustStub(mContext, (ViewStub) findViewById(R.id.stub_history_adjuster));
			mHistoryHelper.setOnAdjustFinishListener(this);
			
			mShowAnim = new TranslateAnimation(
					Animation.RELATIVE_TO_PARENT, -1.0f, Animation.RELATIVE_TO_PARENT, 0.0f,
					Animation.RELATIVE_TO_PARENT, 0.0f, Animation.RELATIVE_TO_PARENT, 0.0f);
			mShowAnim.setDuration(400);
			mHideAnim = new TranslateAnimation(
					Animation.RELATIVE_TO_PARENT, 0.0f, Animation.RELATIVE_TO_PARENT, -1.0f,
					Animation.RELATIVE_TO_PARENT, 0.0f, Animation.RELATIVE_TO_PARENT, 0.0f);
			mHideAnim.setDuration(400);
			
			mHistoryList = (ListView) findViewById(R.id.history_list);
			mAdapter = new HistoryAdapter(mHistoryList, mMDates);
			mHistoryList.setAdapter(mAdapter);
			mHistoryList.setOnItemClickListener(this);
			
    	}
    	
    	public final boolean isShow() {
    		return isShow;
    	}
    	
    	public void show() {
    		if (isShow) return;
    		isShow = true;
    		
    		if (mIsRender) {
    			mIsRender = false;
    			inflateViewStub();
    		}
    		else {
    			if (mChangedDates != null && mChangedDates.size() > 0) {
    				mAdapter.updateAll(mMDates);
    				mChangedDates.clear();
    			}
    		}
    		
    		SettingsParam.read(mContext);
    		usePwd = SettingsParam.getUsePwd();
    		pwdHash = 0;
    		period = SettingsParam.getPeriod();
    		duration = SettingsParam.getDuration();
    		
    		mPeriodValue.setText(String.valueOf(period));
			mDurationValue.setText(String.valueOf(duration));
			mOption.setBackgroundResource(usePwd != 0 ? R.drawable.active : R.drawable.inactive);
    		
    		mRootView.startAnimation(mShowAnim);
    		mRootView.setVisibility(View.VISIBLE);
    	}
    	
    	private void hide() {
        	if (isShow) {
        		isShow = false;
        		mRootView.startAnimation(mHideAnim);
    			mRootView.setVisibility(View.GONE);
        	}
        }

		@Override
		public void onClick(View view) {
			switch (view.getId()) {
			case R.id.btn_ok:
				onSettingsOkClick();
			case R.id.btn_cancel:
				hide();
				break;
			case R.id.option_password:
				usePwd = (usePwd != 0 ? 0 : 1);
				mOption.setBackgroundResource(usePwd != 0 ? R.drawable.active : R.drawable.inactive);
				break;
			case R.id.btn_setpassword:
				onSetClick();
				break;
			case R.id.period_minus:
				period -= 1;
				if (period < 10) period = 10;
				mPeriodValue.setText(String.valueOf(period));
				break;
			case R.id.period_add:
				period += 1;
				if (period > 40) period = 40;
				mPeriodValue.setText(String.valueOf(period));
				break;
			case R.id.duration_minus:
				duration -= 1;
				if (duration < 3) duration = 3;
				mDurationValue.setText(String.valueOf(duration));
				break;
			case R.id.duration_add:
				duration += 1;
				if (duration > 7) duration = 7;
				mDurationValue.setText(String.valueOf(duration));
				break;
			}
		}
		
		private void onSettingsOkClick() {
			// 保存设置好的参数
			SettingsParam.setUsePwd(usePwd);
			if (pwdHash != 0) SettingsParam.setPwdHash(pwdHash);
			SettingsParam.setPeriod(period);
			SettingsParam.setDuration(duration);
			SettingsParam.store(mContext);
			
			// 更新周期显示
			if (mChangedDates != null && mChangedDates.size() > 0) {
				Iterator<Map.Entry<Integer, MensesDate>> it = mChangedDates.entrySet().iterator();
				while (it.hasNext()) {
					Map.Entry<Integer, MensesDate> entry = it.next();
					mDbHelper.updateDate(mMDates.get(entry.getKey()).copy(entry.getValue()));
				}
			}
			
			onSettingsChange();
		}
		
		@Override
		public void onItemClick(AdapterView<?> parent, View child, int pos, long id) {
			if (parent.getId() == R.id.history_list) {
				int posInList = mMDates.size() - pos - 1;
				mHistoryHelper.show(posInList, mMDates.get(posInList));
			}
			else { // number pad
				if (9 == pos) {
					onBackClick();
				} else if (11 == pos) {
					onOkClick();
				}
				else {
					onKeyPress(ButtonAdapter.KEYS[pos]);
				}
			}
		}
		
		@SuppressLint("UseSparseArrays")
		@Override
		public void onAdjusted(int id, int year, int month, int day) {
			MensesDate tp = mMDates.get(id);
			if (tp.day != day || tp.month != month || tp.year != year) {
				if (null == mChangedDates) {
					mChangedDates = new HashMap<Integer, MensesDate>();
				}
				
				tp = new MensesDate(year, month, day);
				mSettingsHelper.mAdapter.updateOne(mMDates.size() - id - 1, tp);
				mChangedDates.put(id, tp);
			}
			
		}
		
		// For Password settings.
		
		private void onBackClick() {
			CharSequence seq = mTxtPwd.getText();
			int len = seq.length();
			if (len > 0) {
				mTxtPwd.setText(seq.subSequence(0, len - 1));
			}
		}
		
		private void onOkClick() {
			CharSequence seq = mTxtPwd.getText();
			if (seq.length() > 3) {
				pwdHash = (seq).hashCode();
				mLayoutPwd.startAnimation(mNumpadHideAnim);
				mLayoutPwd.setVisibility(View.GONE);
			}
			else {
				Toast.makeText(mContext, "Password too short.", Toast.LENGTH_SHORT).show();
			}
		}
		
		private void onKeyPress(CharSequence c) {
			CharSequence seq = mTxtPwd.getText();
			int len = seq.length();
			if (len < 9) {
				StringBuilder newSeq = new StringBuilder(12);
				newSeq.append(seq);
				newSeq.append(c);
				mTxtPwd.setText(newSeq.toString());
			}
		}
		
		private void onSetClick() {
			if (isNumpadRender) {
				isNumpadRender = false;
				mLayoutPwd = mRootView.findViewById(R.id.layout_password);
				mTxtPwd = (TextView) mRootView.findViewById(R.id.txt_password);
				mNumpad = (GridView) mRootView.findViewById(R.id.numpad);
				
				mTxtPwd.setText(Key.CBLANK);
				mNumpad.setAdapter(new ButtonAdapter(mNumpad, R.layout.layout_button, 12));
				mNumpad.setOnItemClickListener(this);
				
				mNumpadShowAnim = new ScaleAnimation(
						1.00f, 1.00f, 0.00f, 1.00f, 
						Animation.RELATIVE_TO_SELF, 0.00f, Animation.RELATIVE_TO_SELF, 1.00f);
				mNumpadShowAnim.setDuration(400L);
				
				mNumpadHideAnim = new ScaleAnimation(
						1.00f, 1.00f, 1.00f, 0.00f,
						Animation.RELATIVE_TO_SELF, 0.00f, Animation.RELATIVE_TO_SELF, 1.00f);
				mNumpadHideAnim.setDuration(400L);
			}
			
			if (isNumpadShow) {
				mLayoutPwd.startAnimation(mNumpadHideAnim);
				mLayoutPwd.setVisibility(View.GONE);
			}
			else {
				mLayoutPwd.startAnimation(mNumpadShowAnim);
				mLayoutPwd.setVisibility(View.VISIBLE);
				
			}
			isNumpadShow = !isNumpadShow;
		}

		
    }



}

