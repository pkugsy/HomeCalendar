package pku.gsy.app;

import java.util.Calendar;

import pku.gsy.app.plugins.AppTitlePlugin;
import pku.gsy.app.plugins.CalendarPlugin;
import pku.gsy.app.plugins.DateNaviPlugin;
import pku.gsy.app.plugins.Hint1Plugin;
import pku.gsy.app.plugins.Hint2Plugin;
import pku.gsy.app.plugins.Hint3Plugin;
import pku.gsy.app.stubs.DatePickerStub;
import pku.gsy.app.stubs.MainSettingsStub;
import pku.gsy.app.util.FestInOneDay;
import pku.gsy.app.util.SettingsParam;
import pku.gsy.app.util.content.AnimationHelper;
import pku.gsy.app.util.content.Key;
import pku.gsy.app.util.date.DateHelper;
import pku.gsy.app.util.date.DateInfo;
import pku.gsy.app.view.Workspace;
import pku.gsy.app.view.listener.SubviewSlideInListener;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewStub;
import android.widget.Toast;

public class CalendarActivity extends FullScreenBaseActivity implements 
	OnClickListener, 
	CalendarPlugin.OnDateClickListener, 
	DatePickerStub.OnDatePickerListener,
	MainSettingsStub.OnSettingsItemClickListener {
	
	private DateHelper mDateHelper = null;
	private DateInfo mDateCur = new DateInfo();
	private DateInfo mDateNext = new DateInfo();
	
	private int mYear = 0;
	private int mMonth = 0;
	private int mDay = 0;
	private int mLastCellIndex = 0;
	private long mClickTimeInMillis = 0L;
	
	public void bindViews() {
    	mWorkspace = (Workspace) findViewById(R.id.layout_calendar_container);
    	mWorkspace.setBeforeSlideInListener(new BeforeSlideInListener());
    	mWorkspace.setAfterSlideInListener(new AfterSlideInListener());
    	
    	mAppTitleHelper = new AppTitlePlugin(this, findViewById(R.id.layout_app_title));
    	mAppTitleHelper.setOnClickListener(this);
    	mDateNavHelper = new DateNaviPlugin(this, findViewById(R.id.layout_date_navigation_title));
    	mDateNavHelper.setOnClickListener(this);
    	mHint1Helper = new Hint1Plugin(findViewById(R.id.hint1));
    	mHint2Helper = new Hint2Plugin(findViewById(R.id.hint2));
    	mHint3Helper = new Hint3Plugin(findViewById(R.id.hint3));
    	mDatePickerHelper = new DatePickerStub(this, (ViewStub)findViewById(R.id.stub_datepicker));
    	mDatePickerHelper.setOnDatePickerListener(this);
    	mSettingsHelper = new MainSettingsStub(this, (ViewStub)findViewById(R.id.stub_settings));
    	mSettingsHelper.setOnSettingsItemClickListener(this);
    }
		
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);
        bindViews();
        AnimationHelper.getInstance(this);
        
        Calendar sysCalendar = Calendar.getInstance();
        mYear = sysCalendar.get(Calendar.YEAR);
        mMonth = sysCalendar.get(Calendar.MONTH) + 1;
        mDay = sysCalendar.get(Calendar.DAY_OF_MONTH);
        
        mDateHelper = DateHelper.getInstance();
        mDateHelper.setDate(mDateCur, mYear, mMonth, 1);
        
        // 绑定calendar的view和控制该calendar的helper
        View view = findViewById(R.id.calendar1);
        CalendarPlugin chelper = new CalendarPlugin(view);
        view.setTag(chelper);
        chelper.setOnDateClickListener(this);
        
        view = findViewById(R.id.calendar2);
        chelper = new CalendarPlugin(view);
        view.setTag(chelper);
        chelper.setOnDateClickListener(this);
        
        mAppTitleHelper.setLogoDate(mMonth, mDay);
        // 
        setToToday(false);
        
        // Weather
        mHint2Helper.setHint(mYear, mMonth, mDay);
    }
    
    @Override
    public void finish() {
        super.finish();
        System.exit(0);
    }

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    	if (requestCode == PasswordActivity.ActionEntry.hashCode()) {
    		if (resultCode == Activity.RESULT_OK && 1 == data.getIntExtra(Key.PasswordResult, 0)) {
				this.startActivity(new Intent(this, MensesActivity.class));
				overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    		}
    		else {
    			Toast.makeText(this, "取消输入或口令错误。", Toast.LENGTH_SHORT).show();
    		}
    	}
    	super.onActivityResult(requestCode, resultCode, data);
    }

	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.calendar, menu);
        return true;
    }
    
    @Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
    	// System.err.println("menu " + featureId + ":" + item.getItemId());
    	switch(item.getItemId()) {
    	case R.id.action_settings:
    		mSettingsHelper.toggle();
    		break;
    	case R.id.action_exit:
    		finish();
    		break;
    	}
		return super.onMenuItemSelected(featureId, item);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
    	if (keyCode==KeyEvent.KEYCODE_BACK) {
    		if (mDatePickerHelper.isShow()) {
    			mDatePickerHelper.hide();
    			return false;
    		}
    		else if (mSettingsHelper.isShow()) {
    			mSettingsHelper.hide();
    			return false;
    		}
    		else {
    			finish();
    		}
    	}
    	
    	return super.onKeyDown(keyCode, event);
	}

	public void setToToday(boolean isAnim) {
    	final CalendarPlugin helper = (CalendarPlugin) mWorkspace.getCurrentScreen().getTag();
    	mDateHelper.setDate(mDateCur, mYear, mMonth, 1);
		helper.setCalendar(mDateCur);
		helper.setFocusOnDay(mDateCur, mDay);
		mDateNavHelper.setTitle(mYear, mMonth, isAnim);
		
		mLastCellIndex = helper.getFocusIndex();
    }
	
	@Override
    public void OnSettingsItemClick(int id) {
	    switch (id) {
	    case 0:
	        break;
	    case 1:
	        break;
	    case 2:
	        break;
	    case 3:
	        // TODO: 天气预报
	        break;
	    case 4:
	        finish();
	    }
    }
    
    @Override
	public void onClick(View view) {
    	switch(view.getId()) {
    	case DateNaviPlugin.titleBtnId:
    		mDatePickerHelper.show(mDateCur.gYear, mDateCur.gMonth);
    		break;
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
    		mSettingsHelper.toggle();
    		break;
    	}
	}
    
	@Override
	public void onDatePickerCancel() {
		// do nothing here
	}

	@Override
	public void onDatePickerOK(int gYear, int gMonth) {
		final CalendarPlugin helper = (CalendarPlugin) mWorkspace.getCurrentScreen().getTag();
    	mDateHelper.setDate(mDateCur, gYear, gMonth, 1);
		helper.setCalendar(mDateCur);
		mDateNavHelper.setTitle(gYear, gMonth, true);
		helper.setFocus(mLastCellIndex);
	}
    
    @Override
	public void dateClick(int pos, DateInfo date, FestInOneDay fests, String solarTerm, int starIndex) {
    	if (pos < 38) {
    		mHint1Helper.setHint(date);
        	mLastCellIndex = pos;
        	mHint3Helper.setHint(date, fests, solarTerm, starIndex);
    	}
    	else {
    		if (38 == pos) mClickTimeInMillis = System.currentTimeMillis();
    		else if (40 == pos && ((System.currentTimeMillis() - mClickTimeInMillis) < 2000L)) {
    			// When click [38] and [40] cells in order in two seconds,
    			// start menses activity.
    			Intent intent = new Intent();
    			SettingsParam.read(this);
    			if (0 != SettingsParam.getUsePwd()) {
    				intent.setAction(PasswordActivity.ActionEntry);
    				intent.putExtra(Key.PasswordHash, SettingsParam.getPwdHash());
    				intent.setClass(this, PasswordActivity.class);
    				startActivityForResult(intent, PasswordActivity.ActionEntry.hashCode());
    			}
    			else {
    				intent.setClass(this, MensesActivity.class);
    				this.startActivity(intent);
    				overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    			}
    		}
    	}
    	
	}
    
    public class BeforeSlideInListener implements SubviewSlideInListener {
		@Override
		public void onSlide(View which, int direction) {
			final CalendarPlugin helper = (CalendarPlugin) which.getTag();
			mDateHelper.setDate(mDateCur, mDateNext);
			switch (direction) {
			case SubviewSlideInListener.DirectionLeft:
				mDateHelper.RollUpOneMonth(mDateNext);
				break;
			case SubviewSlideInListener.DirectionRight:
				mDateHelper.RollBackOneMonth(mDateNext);
				break;
			case SubviewSlideInListener.DirectionUp:
				mDateHelper.RollUpOneYear(mDateNext);
				break;
			case SubviewSlideInListener.DirectionDown:
				mDateHelper.RollBackOneYear(mDateNext);
				break;
			}
			helper.setCalendar(mDateNext);
			helper.setPreFocus(mLastCellIndex);
		}
    }
    
    public class AfterSlideInListener implements SubviewSlideInListener {
		@Override
		public void onSlide(View which, int direction) {
			if (direction == SubviewSlideInListener.DirectionHere) return;
			
			DateInfo tpCalendar = mDateCur;
			mDateCur = mDateNext;
			mDateNext = tpCalendar;
			
			mDateNavHelper.setTitle(mDateCur.gYear, mDateCur.gMonth, true);
			final CalendarPlugin helper = (CalendarPlugin) which.getTag();
			helper.setFocus(mLastCellIndex);
		}
    }
    
    private Workspace mWorkspace = null;
    private AppTitlePlugin mAppTitleHelper = null;
    private DateNaviPlugin mDateNavHelper = null;
    private Hint1Plugin mHint1Helper = null;
    private Hint2Plugin mHint2Helper = null;
    private Hint3Plugin mHint3Helper = null;
    private DatePickerStub mDatePickerHelper = null;
    private MainSettingsStub mSettingsHelper = null;

}
