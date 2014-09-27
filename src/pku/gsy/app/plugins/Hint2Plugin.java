package pku.gsy.app.plugins;

import java.util.Calendar;

import pku.gsy.app.R;
import pku.gsy.app.db.DbHelper;
import pku.gsy.app.util.SettingsParam;
import pku.gsy.app.util.SinaWeather;
import pku.gsy.app.util.WeatherForecast;
import pku.gsy.app.util.content.WeatherIndexer;
import pku.gsy.app.util.date.GDateInfo;
import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

public class Hint2Plugin implements Callback, OnClickListener {
	
	private View mImage = null;
	private View mLoading = null;
	private TextView mHint = null;
	private ImageButton mBtnMore = null;
	private Context mContext = null;
	private DbHelper mDbHelper = null;
	private Handler mHandler = null;
	private AnimationDrawable mAnimDrawable = null;
	private GDateInfo mCurDate = null;
	private SinaWeather mWeather = null;
	
	public Hint2Plugin(View base) {
		mContext = base.getContext();
		mImage = base.findViewById(R.id.img_weather);
		mLoading = base.findViewById(R.id.img_loading);
		mHint = (TextView) base.findViewById(R.id.txt_hint2);
		mBtnMore = (ImageButton) base.findViewById(R.id.btn_more);
		
		mHandler = new Handler(this);
		mDbHelper = DbHelper.getInstance(mContext);
		
		mImage.setOnClickListener(this);
		mBtnMore.setOnClickListener(this);
		mLoading.setBackgroundResource(R.drawable.xml_anim_loading);
		
		SettingsParam.read(mContext);
	}
	
	@Override
	public boolean handleMessage(Message msg) {
		switch (msg.what) {
		// Update/Set message by network.
		case 0:
			if (mWeather == null) {
				// mHint.setText(R.string.default_weather);
				Toast.makeText(mContext, "未读取到天气信息。请检查网络。", Toast.LENGTH_SHORT).show();
			}
			else {
				setWeatherHint(mWeather);
				mDbHelper.writeWeather(mWeather);
			}
			if (mAnimDrawable != null) mAnimDrawable.stop();
			mLoading.setVisibility(View.GONE);
			break;
			
		}
		
		return true;
	}
	
	public void setHint(int year, int month, int day) {
		mCurDate = new GDateInfo(year, month, day);
		
		String city = SettingsParam.getCurCity();
		mWeather = mDbHelper.readWeather(mCurDate.gYear, mCurDate.gMonth, mCurDate.gDay, city);
		
		if (null == mWeather) {
			mHint.setText(R.string.default_weather);
		}
		else {
			setWeatherHint(mWeather);
		}
		
	}
	
	private void updateHint() {
		if (null == mCurDate) {
			Calendar cal = Calendar.getInstance();
			mCurDate.gYear = cal.get(Calendar.YEAR);
			mCurDate.gMonth = cal.get(Calendar.MONTH) + 1;
			mCurDate.gDay = cal.get(Calendar.DATE);
		}
		
		// Start loading animation before reading network weather data.
		mLoading.setVisibility(View.VISIBLE);
		mAnimDrawable = (AnimationDrawable) mLoading.getBackground();
		mAnimDrawable.start();
		new Thread(new WeatherTask()).start();
	}
	
	public class WeatherTask implements Runnable {
		
		@Override
		public void run() {
			mWeather = WeatherForecast.getWeather(SettingsParam.getCurCity(), WeatherForecast.TODAY);
			// Debug Message.
			// System.err.println(mWeather.toString());
			mHandler.sendEmptyMessage(0);
		}
		
	}
	
	private void setWeatherHint(SinaWeather w) {
		// mImage
		int hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
		
		int resId = 0;
		if (hour < 6 || hour > 18) {
			// 夜间
			resId = WeatherIndexer.getWeatherRes(w.figure2, WeatherIndexer.NIGHT);
		}
		else {
			resId = WeatherIndexer.getWeatherRes(w.figure1, WeatherIndexer.DAY);
		}
		
		mImage.setBackgroundResource(resId);
		mHint.setText(w.toSimpleString());
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.img_weather:
			updateHint();
			break;
		case R.id.btn_more:
			if (null == mWeather) {
				Toast.makeText(mContext, "没有可用的天气信息，请更新。", Toast.LENGTH_SHORT).show();
			}
			else {
				
			}
			break;
		}
	}

}







