package pku.gsy.app.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

public class WeatherForecast {
	
	public static final int TODAY = 0;
	public static final int TOMORROW = 1;
	public static final int THIRDDAY = 2;
	public static final int FORTHDAY = 3;
	public static final int FIFTHDAY = 4;
	
	private static final String GB2312 = "GB2312";
	private static final String UTF8 = "utf-8";

	public static SinaWeather getWeather(String city, int time) {
		// if (time < 0 || time > 4) return null;
		SinaWeather w = null;
		try {
			city = URLEncoder.encode(city, GB2312);
			StringBuilder builder = new StringBuilder(128);
			builder.append("http://php.weather.sina.com.cn/xml.php?city=");
			builder.append(city);
			builder.append("&password=DJOYnieT8234jlsK&day=");
			builder.append(time);
			
			URL url = new URL(builder.toString());
			InputStream is = url.openStream();
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			int len = -1;
			byte[] buffer = new byte[1024];
			while ((len = is.read(buffer)) != -1) {
				bos.write(buffer, 0, len);
			}
			String xml = bos.toString(UTF8);
			w = SinaWeather.fromXmlString(xml);
			
			is.close();
			bos.close();
		} catch (MalformedURLException e) {
			System.err.println(e);
		} catch (IOException e) {
			System.err.println(e);
		}
		
		return w;
	}
	
}
