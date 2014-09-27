package pku.gsy.app.db;

import pku.gsy.app.util.Festival;
import pku.gsy.app.util.MensesDate;


public class DbDescriber {

	public static String dbName = "db_homecalendar";
	
	
	public static final String ColId = "id";
	public static final String ColDate = "date";
	public static final String ColCity = "city";
	public static final String ColTag = "tag";
	public static final String[] ColNameAndTag = {"name", "tag"};
	
	public static final String tbMenses = "tb_menses";
	public static final String tbGFestival = "tb_gfest";
	public static final String tbCFestival = "tb_cfest";
	public static final String tbWeather = "tb_weather";
	
	public static String sTbGFestival = "create table tb_gfest (name varchar(16), month integer, day integer, tag integer)";
	public static String sTbCFestival = "create table tb_cfest (name varchar(16), month integer, day integer, tag integer)";
	public static String sTbParam = "create table tb_param (key varchar(16), value integer)";
	public static String sTbMenses = "create table tb_menses (id INTEGER PRIMARY KEY AUTOINCREMENT, date integer)";
	public static String sTbWeather = 
			"create table tb_weather (" +
					"id INTEGER, date INTEGER, city TEXT, " +
					"status1 TEXT, status2 TEXT, figure1 TEXT, figure2 TEXT, " +
					"temperature1 INTEGER, temperature2 INTEGER, " +
					"power1 TEXT, power2 TEXT, direction1 TEXT, direction2 TEXT, " +
					"apparentTemperature1 INTEGER, apparentTemperature2 INTEGER, " +
					"somatosensoryIndex INTEGER, somatosensory TEXT, somatosensorySpec TEXT, " +
					"ultravioletIndex INTEGER, ultraviolet TEXT, ultravioletSpec TEXT, " +
					"airIndex INTEGER, air TEXT, airSpec TEXT, " +
					"pollutionIndex INTEGER, pollution TEXT, pollutionSpec TEXT, " +
					"carIndex INTEGER, car TEXT, carSpec TEXT, " +
					"dressIndex INTEGER, dress TEXT, dressSpec TEXT, " +
					"coldIndex INTEGER, cold TEXT, coldSpec TEXT, " +
					"sportIndex INTEGER, sport TEXT, sportSpec TEXT)";
	
	public static Festival[] lGFestival = {
		new Festival("元旦", 1, 1),
		new Festival("情人节", 2, 14),
		new Festival("妇女节", 3, 8),
		new Festival("植树节", 3, 12),
		new Festival("愚人节", 4, 1),
		new Festival("地球日", 4, 22),
		new Festival("劳动节", 5, 1),
		new Festival("青年节", 5, 4),
		new Festival("护士节", 5, 12),
		new Festival("助残日", 5, 16),
		new Festival("儿童节", 6, 1),
		new Festival("环境日", 6, 5),
		new Festival("教师节", 9, 10),
		new Festival("孔子祭", 9, 28),
		new Festival("国庆节", 10, 1),
		new Festival("平安夜", 12, 24),
		new Festival("圣诞节", 12, 25)
		};
	
	public static Festival[] lCFestival = {
		new Festival("春节", 1, 1),
		new Festival("元宵节", 1, 15),
		new Festival("龙抬头", 2, 2),
		new Festival("端午", 5, 5),
		new Festival("七夕", 7, 7),
		new Festival("中元节", 7, 15),
		new Festival("中秋节", 8, 15),
		new Festival("重阳", 9, 9),
		new Festival("腊八", 12, 8),
		new Festival("小年", 12, 23)
	};
	
	public static MensesDate[] lMensesDate = {
		new MensesDate(2013, 9, 18),
		new MensesDate(2013, 10, 16),
		new MensesDate(2013, 11, 15),
		new MensesDate(2013, 12, 11),
		new MensesDate(2014, 1, 7),
		new MensesDate(2014, 2, 1)
	};
	
}
