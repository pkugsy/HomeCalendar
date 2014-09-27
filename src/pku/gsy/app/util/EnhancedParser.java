package pku.gsy.app.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class EnhancedParser {

	private static final Pattern mIntegerPattern = Pattern.compile("[-]?\\d+");
	public static int ExptNum = Integer.MAX_VALUE;
	
	public static final int parseInt(String strNum) {
		int retNum = ExptNum;
		
		if (strNum.length() > 0) {
			try {
				retNum = Integer.parseInt(strNum);
			}
			catch(NumberFormatException e) {
				Matcher m = mIntegerPattern.matcher(strNum);
				if (m.find()) {
					// 如果找到数字字符串，一定可以parse integer
					retNum = Integer.parseInt(strNum);
				}
				else {
					System.err.println("Error occured when parse integer from string \"" + strNum + "\".");
				}
			}
		}
		else {
			System.err.println("Error occured when parse integer from blank string.");
		}
		
		return retNum;
	}
	
}
