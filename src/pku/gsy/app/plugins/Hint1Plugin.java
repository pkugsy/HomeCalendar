package pku.gsy.app.plugins;

import pku.gsy.app.R;
import pku.gsy.app.util.date.DateHelper;
import pku.gsy.app.util.date.DateInfo;
import android.view.View;
import android.widget.TextView;

public class Hint1Plugin {
	
	public static int[] animalResId = {
		R.drawable.animal_mouse, R.drawable.animal_cow, R.drawable.animal_tiger, R.drawable.animal_rabbit,
		R.drawable.animal_dragon, R.drawable.animal_snake, R.drawable.animal_horse, R.drawable.animal_lamb,
		R.drawable.animal_monkey, R.drawable.animal_chicken, R.drawable.animal_dog, R.drawable.animal_pig
	};
	
	public static String nian = "年";
	public static String yue = "月";
	public static String run = "润";
	
	private View mImage = null;
	private TextView mHint = null;
	private DateHelper mHelper = null;
	
	public Hint1Plugin(View base) {
		mImage = base.findViewById(R.id.img_animal);
		mHint = (TextView) base.findViewById(R.id.txt_hint1);
		mHelper = DateHelper.getInstance();
	}
	
	public void setHint(DateInfo date) {
		int cYear = date.cYear;
		int animal = mHelper.GetAnimalIndex(cYear);
		mImage.setBackgroundResource(animalResId[animal]);
		
		StringBuffer hint = new StringBuffer(mHelper.GetChineseYearName(cYear));
		hint.append(mHelper.GetAnimalName(cYear));
		hint.append(nian);
		if (date.cMonth > 0) {
			hint.append(mHelper.chineseMonthNames[date.cMonth]);
		} else {
			hint.append(run);
			hint.append(mHelper.chineseMonthNames[-date.cMonth]);
		}
		hint.append(yue);
		hint.append(mHelper.chineseDateNames[date.cDay]);
		
		mHint.setText(hint.toString());
	}
}
