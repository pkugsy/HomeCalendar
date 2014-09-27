package pku.gsy.app.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import pku.gsy.app.util.content.Key;
import pku.gsy.app.util.date.DateInterpreter;
import android.content.ContentValues;
import android.database.Cursor;


public class SinaWeather {
	
	private static final String mLineLinkSymbol = new String("\n");
	
	private static final String[] name = {
		"id", "date", "city", 
		"status1", "status2", "figure1", "figure2", 
		"temperature1", "temperature2", 
		"power1", "power2", "direction1", "direction2", 
		"apparentTemperature1", "apparentTemperature2", 
		"somatosensoryIndex", "somatosensory", "somatosensorySpec", 
		"ultravioletIndex", "ultraviolet", "ultravioletSpec", 
		"airIndex", "air", "airSpec", 
		"pollutionIndex", "pollution", "pollutionSpec", 
		"carIndex", "car", "carSpec", 
		"dressIndex", "dress", "dressSpec", 
		"coldIndex", "cold", "coldSpec", 
		"sportIndex", "sport", "sportSpec"};
	
	public static SinaWeather fromXmlString(String xml) {
		String[] lines = xml.split(mLineLinkSymbol);
		SinaWeather w = new SinaWeather();
		
		Pattern ptn = Pattern.compile("[\\s]*<[\\s]*(\\w+)[\\s]*>[\\s]*(\\S*)[\\s]*<[\\s]*/[\\s]*(\\w+)[\\s]*>[\\s]*");
		for (String line : lines) {
			Matcher m = ptn.matcher(line);
			if (m.matches()) {
				String key = m.group(1).trim();
				String value = m.group(2).trim();
				// if (value.length() == 0) continue;
				System.err.println("Key: " + key + ", Value: " + value);
				switch (key.hashCode()) {
				case 3053931: // "city"
					w.city = value;
					break;
				case -1897124225: // "status1"
					w.status1 = value;
					break;
				case -1897124224: // "status2"
					w.status2 = value;
					break;
				case -859123251: // "figure1"
					w.figure1 = value;
					break;
				case -859123250: // "figure2"
					w.figure2 = value;
					break;
				case 1382803773: // "temperature1"
					w.temperature1 = EnhancedParser.parseInt(value);
					break;
				case 1382803774: // "temperature2"
					w.temperature2 = EnhancedParser.parseInt(value);
					break;
				case -982345780: // "power1"
					w.power1 = value;
					break;
				case -982345779: // "power2"
					w.power2 = value;
					break;
				case 224454802: // "direction1"
					w.direction1 = value;
					break;
				case 224454803: // "direction2"
					w.direction2 = value;
					break;
				case 3557888: // "tgd1"
					w.apparentTemperature1 = EnhancedParser.parseInt(value);
					break;
				case 3557889: // "tgd2"
					w.apparentTemperature2 = EnhancedParser.parseInt(value);
					break;
				case 114180: // "ssd"
					w.somatosensoryIndex = EnhancedParser.parseInt(value);
					break;
				case 121051: // "zwx"
					w.ultravioletIndex = EnhancedParser.parseInt(value);
					break;
				case 106530: // "ktk"
					w.airIndex = EnhancedParser.parseInt(value);
					break;
				case -123307862: // "pollution"
					w.pollutionIndex = EnhancedParser.parseInt(value);
					break;
				case 118511: // "xcz"
					w.carIndex = EnhancedParser.parseInt(value);
					break;
				case 98484: // "chy"
					w.dressIndex = EnhancedParser.parseInt(value);
					break;
				case -396571729: // "chy_shuoming"
					w.dressSpec = value;
					break;
				case 1760231959: // "pollution_l"
					w.pollution = value;
					break;
				case 116333064: // "zwx_l"
					w.ultraviolet = value;
					break;
				case 109730033: // "ssd_l"
					w.somatosensory = value;
					break;
				case 94646177: // "chy_l"
					w.dress = value;
					break;
				case 102378383: // "ktk_l"
					w.air = value;
					break;
				case 113892124: // "xcz_l"
					w.car = value;
					break;
				case 1760231966: // "pollution_s"
					w.pollutionSpec = value;
					break;
				case 116333071: // "zwx_s"
					w.ultravioletSpec = value;
					break;
				case 109730040: // "ssd_s"
					w.somatosensorySpec = value;
					break;
				case 102378390: // "ktk_s"
					w.airSpec = value;
					break;
				case 113892131: // "xcz_s"
					w.carSpec = value;
					break;
				case 3302: // "gm"
					w.coldIndex = EnhancedParser.parseInt(value);
					break;
				case 3176275: // "gm_l"
					w.cold = value;
					break;
				case 3176282: // "gm_s"
					w.coldSpec = value;
					break;
				case 3851: // "yd"
					w.sportIndex = EnhancedParser.parseInt(value);
					break;
				case 3703864: // "yd_l"
					w.sport = value;
					break;
				case 3703871: // "yd_s"
					w.sportSpec = value;
					break;
				case 140881504: // "savedate_weather"
					w.date = DateInterpreter.stringToInteger(value);
					break;
				}
			}
		}
		
		w.id = generateId(w.date, w.city);
		
		return w;
	}
	
	public static SinaWeather fromCursor(Cursor cursor) {
		SinaWeather w = new SinaWeather();
		w.id = cursor.getInt(cursor.getColumnIndex(name[0]));
		w.date = cursor.getInt(cursor.getColumnIndex(name[1]));
		w.city = cursor.getString(cursor.getColumnIndex(name[2]));
		
		w.status1 = cursor.getString(cursor.getColumnIndex(name[3]));
		w.status2 = cursor.getString(cursor.getColumnIndex(name[4]));
		w.figure1 = cursor.getString(cursor.getColumnIndex(name[5]));
		w.figure2 = cursor.getString(cursor.getColumnIndex(name[6]));
		w.temperature1 = cursor.getInt(cursor.getColumnIndex(name[7]));
		w.temperature2 = cursor.getInt(cursor.getColumnIndex(name[8]));
		
		w.power1 = cursor.getString(cursor.getColumnIndex(name[9]));
		w.power2 = cursor.getString(cursor.getColumnIndex(name[10]));
		w.direction1 = cursor.getString(cursor.getColumnIndex(name[11]));
		w.direction2 = cursor.getString(cursor.getColumnIndex(name[12]));
		
		w.apparentTemperature1 = cursor.getInt(cursor.getColumnIndex(name[13]));
		w.apparentTemperature2 = cursor.getInt(cursor.getColumnIndex(name[14]));
		
		w.somatosensoryIndex = cursor.getInt(cursor.getColumnIndex(name[15]));
		w.somatosensory = cursor.getString(cursor.getColumnIndex(name[16]));
		w.somatosensorySpec = cursor.getString(cursor.getColumnIndex(name[17]));
		w.ultravioletIndex = cursor.getInt(cursor.getColumnIndex(name[18]));
		w.ultraviolet = cursor.getString(cursor.getColumnIndex(name[19]));
		w.ultravioletSpec = cursor.getString(cursor.getColumnIndex(name[20]));
		w.airIndex = cursor.getInt(cursor.getColumnIndex(name[21]));
		w.air = cursor.getString(cursor.getColumnIndex(name[22]));
		w.airSpec = cursor.getString(cursor.getColumnIndex(name[23]));
		w.pollutionIndex = cursor.getInt(cursor.getColumnIndex(name[24]));
		w.pollution = cursor.getString(cursor.getColumnIndex(name[25]));
		w.pollutionSpec = cursor.getString(cursor.getColumnIndex(name[26]));
		w.carIndex = cursor.getInt(cursor.getColumnIndex(name[27]));
		w.car = cursor.getString(cursor.getColumnIndex(name[28]));
		w.carSpec = cursor.getString(cursor.getColumnIndex(name[29]));
		w.dressIndex = cursor.getInt(cursor.getColumnIndex(name[30]));
		w.dress = cursor.getString(cursor.getColumnIndex(name[31]));
		w.dressSpec = cursor.getString(cursor.getColumnIndex(name[32]));
		w.coldIndex = cursor.getInt(cursor.getColumnIndex(name[33]));
		w.cold = cursor.getString(cursor.getColumnIndex(name[34]));
		w.coldSpec = cursor.getString(cursor.getColumnIndex(name[35]));
		w.sportIndex = cursor.getInt(cursor.getColumnIndex(name[36]));
		w.sport = cursor.getString(cursor.getColumnIndex(name[37]));
		w.sportSpec = cursor.getString(cursor.getColumnIndex(name[38]));
		return w;
	}
	
	public static ContentValues toContentValues(SinaWeather w) {
		ContentValues values = new ContentValues();
		
		values.put(name[0], w.id);
		values.put(name[1], w.date);
		values.put(name[2], w.city);
		
		values.put(name[3], w.status1);
		values.put(name[4], w.status2);
		values.put(name[5], w.figure1);
		values.put(name[6], w.figure2);
		values.put(name[7], w.temperature1);
		values.put(name[8], w.temperature2);
		
		values.put(name[9], w.power1);
		values.put(name[10], w.power2);
		values.put(name[11], w.direction1);
		values.put(name[12], w.direction2);
		
		values.put(name[13], w.apparentTemperature1);
		values.put(name[14], w.apparentTemperature2);
		
		values.put(name[15], w.somatosensoryIndex);
		values.put(name[16], w.somatosensory);
		values.put(name[17], w.somatosensorySpec);
		values.put(name[18], w.ultravioletIndex);
		values.put(name[19], w.ultraviolet);
		values.put(name[20], w.ultravioletSpec);
		values.put(name[21], w.airIndex);
		values.put(name[22], w.air);
		values.put(name[23], w.airSpec);
		values.put(name[24], w.pollutionIndex);
		values.put(name[25], w.pollution);
		values.put(name[26], w.pollutionSpec);
		values.put(name[27], w.carIndex);
		values.put(name[28], w.car);
		values.put(name[29], w.carSpec);
		values.put(name[30], w.dressIndex);
		values.put(name[31], w.dress);
		values.put(name[32], w.dressSpec);
		values.put(name[33], w.coldIndex);
		values.put(name[34], w.cold);
		values.put(name[35], w.coldSpec);
		values.put(name[36], w.sportIndex);
		values.put(name[37], w.sport);
		values.put(name[38], w.sportSpec);
		
		return values;
	}
	
	public static int generateId(int date, String city) {
		return date ^ city.hashCode();
	}
	
	public int id = 0;
	public int date = 0;

	public String city = null;
	public String status1 = null;
	public String status2 = null;
	public String figure1 = null;
	public String figure2 = null;
	public String direction1 = null;
	public String direction2 = null;
	public String power1 = null;
	public String power2 = null;
	public int temperature1 = 0;
	public int temperature2 = 0;
	public int somatosensoryIndex = 0;
	public String somatosensory = null;
	public String somatosensorySpec = null;
	public int apparentTemperature1 = 0;
	public int apparentTemperature2 = 0;
	public int ultravioletIndex = 0;
	public String ultraviolet = null;
	public String ultravioletSpec = null;
	public int airIndex = 0;
	public String air = null;
	public String airSpec = null;
	public int pollutionIndex = 0;
	public String pollution = null;
	public String pollutionSpec = null;
	public int carIndex = 0;
	public String car = null;
	public String carSpec = null;
	public int dressIndex = 0;
	public String dress = null;
	public String dressSpec = null;
	public int coldIndex = 0;
	public String cold = null;
	public String coldSpec = null;
	public int sportIndex = 0;
	public String sport = null;
	public String sportSpec = null;
	
	public String toSimpleString() {
		StringBuilder b = new StringBuilder();
		// b.append("昼: ");
		b.append(status1);
		b.append(Key.SPACE);
		b.append(temperature1);
		b.append(Key.CELSIUS);
		// b.append("; 夜: ");
		b.append(Key.cSPACE);
		b.append(Key.cSLASH);
		b.append(Key.cSPACE);
		b.append(status2);
		b.append(Key.SPACE);
		b.append(temperature2);
		b.append(Key.CELSIUS);
		
		return b.toString();
	}

	@Override
	public String toString() {
		StringBuilder b = new StringBuilder();
		b.append("天气状况{");
		b.append("\n\t标识符: " + id);
		b.append("\n\t城市: " + city);
		b.append("\n\t日间天气: " + status1);
		b.append("\n\t夜间天气: " + status2);
		b.append("\n\t日间天气拼音: " + figure1);
		b.append("\n\t夜间天气拼音: " + figure2);
		b.append("\n\t日间风向: " + direction1);
		b.append("\n\t夜间风向: " + direction2);
		b.append("\n\t日间风力: " + power1);
		b.append("\n\t夜间风力: " + power2);
		b.append("\n\t日间气温: " + temperature1);
		b.append("\n\t夜间气温: " + temperature2);
		b.append("\n\t体感指数数值: " + somatosensoryIndex);
		b.append("\n\t体感指数: " + somatosensory);
		b.append("\n\t体感指数说明: " + somatosensorySpec);
		b.append("\n\t日间体感温度: " + apparentTemperature1);
		b.append("\n\t夜间体感温度: " + apparentTemperature2);
		b.append("\n\t紫外线指数数值: " + ultravioletIndex);
		b.append("\n\t紫外线指数: " + ultraviolet);
		b.append("\n\t紫外线指数说明: " + ultravioletSpec);
		b.append("\n\t空调指数数值: " + airIndex);
		b.append("\n\t空调指数: " + air);
		b.append("\n\t空调指数说明: " + airSpec);
		b.append("\n\t污染指数数值: " + pollutionIndex);
		b.append("\n\t污染指数: " + pollution);
		b.append("\n\t污染指数说明: " + pollutionSpec);
		b.append("\n\t洗车指数数值: " + carIndex);
		b.append("\n\t洗车指数: " + car);
		b.append("\n\t洗车指数说明: " + carSpec);
		b.append("\n\t穿衣指数数值: " + dressIndex);
		b.append("\n\t穿衣指数: " + dress);
		b.append("\n\t穿衣指数说明: " + dressSpec);
		b.append("\n\t感冒指数数值: " + coldIndex);
		b.append("\n\t感冒指数: " + cold);
		b.append("\n\t感冒指数说明: " + coldSpec);
		b.append("\n\t运动指数数值: " + sportIndex);
		b.append("\n\t运动指数: " + sport);
		b.append("\n\t运动指数说明: " + sportSpec);
		b.append("\n\t预报日期: " + DateInterpreter.integerToString(date));
		b.append("\n}");
		
		return b.toString();
	}
}












