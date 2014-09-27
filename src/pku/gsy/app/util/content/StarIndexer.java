package pku.gsy.app.util.content;

import pku.gsy.app.R;

public class StarIndexer {
	public static int[] starResId = {
		R.drawable.star_capricorn, R.drawable.star_aquarius, R.drawable.star_pisces, R.drawable.star_aries,
		R.drawable.star_taurus, R.drawable.star_gemini, R.drawable.star_cancer, R.drawable.star_leo,
		R.drawable.star_virgo, R.drawable.star_libra, R.drawable.star_scorpio, R.drawable.star_sagittarious
	};
	
	public static String[] starName = {
	    "摩羯座", "水瓶座", "双鱼座", "白羊座", "金牛座", "双子座", 
	    "天蝎座", "狮子座", "处女座", "天平座", "天蝎座", "射手座"
	};
	
	public static final int star(int gMonth, int gDay) {
		int tag = (gMonth << 5) + gDay;
		
		if (tag < 52) {
			return 0; 
		}
		else if (tag < 83) {
			return 1;
		}
		else if (tag < 117) {
			return 2;
		}
		else if (tag < 148) {
			return 3;
		}
		else if (tag < 181) {
			return 4;
		}
		else if (tag < 214) {
			return 5;
		}
		else if (tag < 247) {
			return 6;
		}
		else if (tag < 279) {
			return 7;
		}
		else if (tag < 311) {
			return 8;
		}
		else if (tag < 344) {
			return 9;
		}
		else if (tag < 375) {
			return 10;
		}
		else if (tag < 406) {
			return 11;
		}
		else {
			return 0;
		}
	}
}
