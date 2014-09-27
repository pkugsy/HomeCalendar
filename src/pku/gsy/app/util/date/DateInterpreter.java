package pku.gsy.app.util.date;

import java.util.regex.Pattern;

public class DateInterpreter {
	
	private static final String mDateLinkSymbol = new String("-");
	private static final Pattern mStrDateP = Pattern.compile("\\d{4}\\-\\d{2}\\-\\d{2}");

	public static final int pack(int year, int month, int date) {
		return (year << 16) | (month << 8) | date;
	}
	
	public static final int[] unpack(int packedDate) {
		int[] time = {0, 0, 0};
		time[0] = packedDate >> 16;
		time[1] = (packedDate >> 8) & 0xFF;
		time[2] = packedDate & 0xFF;
		return time;
	}
	
	public static int stringToInteger(String strDate) {
		if (mStrDateP.matcher(strDate).matches()) {
			String[] sp = strDate.split(mDateLinkSymbol);
			try {
				int year = Integer.parseInt(sp[0]);
				int month = Integer.parseInt(sp[1]);
				int date = Integer.parseInt(sp[2]);
				return DateInterpreter.pack(year, month, date);
			}
			catch(NumberFormatException  e) {
				System.err.print("Error occured when parse time string \"" + strDate +"\".");
				return 0;
			}
		}
		else {
			return 0;
		}
	}
	
	public static String integerToString(int packedDate) {
		int[] time = DateInterpreter.unpack(packedDate);
		return integerToString(time[0], time[1], time[2]);
	}
	
	public static String integerToString(int year, int month, int date) {
		StringBuilder strTime = new StringBuilder(16);
		
		strTime.append(year);
		strTime.append(mDateLinkSymbol);
		if (month < 10) {
			strTime.append(0);
		}
		strTime.append(month);
		strTime.append(mDateLinkSymbol);
		if (date < 10) {
			strTime.append(0);
		}
		strTime.append(date);
		
		return strTime.toString();
	}
	
}
