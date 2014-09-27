package pku.gsy.app.util;

import pku.gsy.app.util.content.WeatherIndexer;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class SettingsParam {
	public static final String UsePwd = "pku.gsy.app.rxdq";
	public static final String PwdHash = "pku.gsy.app.kooh";
	public static final String Period = "pku.gsy.app.lzdd";
	public static final String Duration = "pku.gsy.app.aayx";
	public static final String CurCity = "pku.gsy.app.enkg";
	
	private static int usePwd = 1;
	private static int pwdHash = 1656432072;
	private static int period = 28;
	private static int duration = 5;
	private static String curCity = WeatherIndexer.citys[2];
	
	private static int hasChanged = 0x00;
	private static boolean notRead = true;
	
	@SuppressLint("CommitPrefEdits")
	public static void store(Context context) {
		if (hasChanged != 0x00) {
			SharedPreferences sp = context.getSharedPreferences("share", Context.MODE_PRIVATE);
			Editor editor = sp.edit();
			editor.putInt(UsePwd, usePwd);
			editor.putInt(PwdHash, pwdHash);
			editor.putInt(Period, period);
			editor.putInt(Duration, duration);
			editor.putString(CurCity, curCity);
			editor.commit();
		}
	}
	
	public static void read(Context context) {
		if (notRead) {
			notRead = false;
			SharedPreferences sp = context.getSharedPreferences("share", Context.MODE_PRIVATE);
			usePwd = sp.getInt(UsePwd, 1);
			pwdHash = sp.getInt(PwdHash, 1656432072);
			period = sp.getInt(Period, 28);
			duration = sp.getInt(Duration, 5);
			curCity = sp.getString(CurCity, WeatherIndexer.citys[0]);
		}
	}
	
	public static int getUsePwd() {
		return usePwd;
	}
	
	public static int getPwdHash() {
		// System.err.println("read hash: " + pwdHash + "; " + "1234".hashCode());
		return pwdHash;
	}
	
	public static int getPeriod() {
		return period;
	}
	
	public static int getDuration() {
		return duration;
	}
	
	public static String getCurCity() {
		return curCity;
	}
	
	public static void setUsePwd(int _usePwd) {
		if (usePwd != _usePwd) {
			usePwd = _usePwd;
			hasChanged |= 0x01;
		}
	}
	
	public static void setPwdHash(int _pwdHash) {
		if (pwdHash != _pwdHash) {
			// System.err.println("store hash: " + _pwdHash + "; " + "1234".hashCode());
			pwdHash = _pwdHash;
			hasChanged |= 0x02;
		}
	}
	
	public static void setPeriod(int _period) {
		if (period != _period) {
			period = _period;
			hasChanged |= 0x04;
		}
	}
	
	public static void setDuration(int _duration) {
		if (duration != _duration) {
			duration = _duration;
			hasChanged |= 0x08;
		}
	}
	
	public static void setCurCity(String _city) {
		if (!curCity.equals(_city)) {
			curCity = _city;
			hasChanged |= 0x10;
		}
	}
	
	public static String paramString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Home Calendar Settings {use password: ");
		builder.append(usePwd == 0 ? "false" : "true");
		builder.append("; password hash: ");
		builder.append(pwdHash);
		builder.append("; Period: ");
		builder.append(period);
		builder.append("; Duration: ");
		builder.append(duration);
		builder.append("}");
		return builder.toString();
	}
}
