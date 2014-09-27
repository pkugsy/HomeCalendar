package pku.gsy.app.plugins;

import pku.gsy.app.R;
import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

public class AppTitlePlugin implements OnClickListener {
	
	public static final int[] monthResId = {
		R.drawable.month_jan, R.drawable.month_feb, R.drawable.month_mar, R.drawable.month_apr,
		R.drawable.month_may, R.drawable.month_jun, R.drawable.month_jul, R.drawable.month_aug,
		R.drawable.month_sep, R.drawable.month_oct, R.drawable.month_nov, R.drawable.month_dec};
	
	public static final int logoId = R.id.logo_main;
	public static final int settingsId = R.id.btn_settings;
	
	private View mBase = null;
	private ImageView mLogoMonth = null;
	private TextView mLogoDay = null;
	private TextView mAppTitle = null;
	private OnClickListener mClickListener = null;

	public AppTitlePlugin(Context context, View base) {
		mBase = base;
		mLogoMonth = (ImageView) base.findViewById(R.id.logo_month);
		mLogoDay = (TextView) base.findViewById(R.id.logo_day);
		mAppTitle = (TextView) base.findViewById(R.id.txt_apptitle);
		
		base.findViewById(R.id.logo_main).setOnClickListener(this);
		base.findViewById(R.id.btn_settings).setOnClickListener(this);
	}
	
	public View getBase() {
		return mBase;
	}
	
	public void setLogoDate(int month, int day) {
		mLogoMonth.setImageResource(monthResId[month - 1]);
		mLogoDay.setText(String.valueOf(day));
    }
	
	public void setTitle(String appTitle) {
		mAppTitle.setText(appTitle);
    }
	
	public void setOnClickListener(OnClickListener listener) {
		mClickListener = listener;
	}

	@Override
	public void onClick(View view) {
		if (null != mClickListener) {
			mClickListener.onClick(view);
		}
	}
	
}
