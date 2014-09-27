package pku.gsy.app.util.content;

import java.util.HashMap;
import java.util.Map;

import pku.gsy.app.R;

public class WeatherIndexer {
	public static final boolean DAY = false;
	public static final boolean NIGHT = true;
	
	private static Map<String, Integer> dayWeatherMap = new HashMap<String, Integer>(){

		private static final long serialVersionUID = 3930628536033883371L;

	{
		put("qing", R.drawable.weather_00_0);
		put("yin", R.drawable.weather_047);
		put("duoyun", R.drawable.weather_01_0);
		put("zhenyu", R.drawable.weather_031_0);
		put("zhenxue", R.drawable.weather_021_0);
		
		put("xiaoxue", R.drawable.weather_022);
		put("zhongxue", R.drawable.weather_024);
		put("daxue", R.drawable.weather_026);
		put("baoxue", R.drawable.weather_028);
		
		put("xiaoyu", R.drawable.weather_032);
		put("zhongyu", R.drawable.weather_034);
		put("dayu", R.drawable.weather_036);
		put("baoyu", R.drawable.weather_038);
		put("dongyu", R.drawable.weather_043);
		put("leizhenyu", R.drawable.weather_044);
		
		put("yujiaxue", R.drawable.weather_046);
		put("bingbao", R.drawable.weather_046);
		// put("dongyu", R.drawable.weather_046);
		
		put("fuchen", R.drawable.weather_050);
		put("yangsha", R.drawable.weather_051);
		put("shachenbao", R.drawable.weather_052);
		put("yan", R.drawable.weather_054_0);
		put("wu", R.drawable.weather_054_0);
		put("mai", R.drawable.weather_055_0);
	}};
	
	private static Map<String, Integer> nightWeatherMap = new HashMap<String, Integer>(){

		private static final long serialVersionUID = -8862696374365063601L;

	{
		put("qing", R.drawable.weather_00_1);
		put("duoyun", R.drawable.weather_01_1);
		put("zhenyu", R.drawable.weather_031_1);
		put("zhenxue", R.drawable.weather_021_1);
		put("yan", R.drawable.weather_054_1);
		put("wu", R.drawable.weather_054_1);
		put("mai", R.drawable.weather_055_1);
	}};
	
	public static final String[] citys = {
		"北京", "张家口", "杭州", "朔州"};
	
	public static int getWeatherRes(String figure, boolean status) {
		
		Integer resId = null;
		
		if (status) {
			resId = nightWeatherMap.get(figure);
			if (null == resId || 0 == resId) {
				resId = dayWeatherMap.get(figure);
			}
		}
		else {
			resId = dayWeatherMap.get(figure);
		}
		
		// 没有获取到图标，赋值一个最接近的描述
		if (null == resId || 0 == resId) {
			
			if (figure.contains("xue")) {
				resId = R.drawable.weather_022;
			}
			else if (figure.contains("yu") && !figure.contains("yun")) {
				resId = R.drawable.weather_032;
			}
			else if (figure.contains("chen")) {
				resId = R.drawable.weather_050;
			}
			else if (figure.contains("wu")) {
				resId = R.drawable.weather_054_0;
			}
			else if (figure.contains("mai")) {
				resId = R.drawable.weather_054_0;
			}
			else {
				if (status) {
					resId = R.drawable.weather_00_1;
				}
				else {
					resId = R.drawable.weather_00_0;
				}
			}
			
		}
		
		return resId;
	}
}








