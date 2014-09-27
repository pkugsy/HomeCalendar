package pku.gsy.app.db;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import pku.gsy.app.util.Festival;
import pku.gsy.app.util.MensesDate;
import pku.gsy.app.util.SinaWeather;
import pku.gsy.app.util.date.DateInterpreter;
import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DbHelper extends SQLiteOpenHelper {
	
	private static DbHelper instance = null;
	private static Context appContext = null;
	
	public static DbHelper getInstance(Context context) {
		Context ctx = context.getApplicationContext();
		if (appContext == null || instance == null) {
			appContext = ctx;
			instance = new DbHelper(context);
		}
		else {
			if (ctx != appContext) {
				appContext = ctx;
				instance = new DbHelper(context);
			}
		}
		
		return instance;
	}
	
	private DbHelper(Context context) {
		super(context, "d", null, 1);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(DbDescriber.sTbGFestival);
		db.execSQL(DbDescriber.sTbCFestival);
		db.execSQL(DbDescriber.sTbParam);
		db.execSQL(DbDescriber.sTbMenses);
		db.execSQL(DbDescriber.sTbWeather);
		
		for (int i = 0; i < DbDescriber.lGFestival.length; ++i) {
			Festival f = DbDescriber.lGFestival[i];
			db.execSQL("insert into tb_gfest (name, month, day, tag)values(?, ?, ?, ?)", new Object[]{
				f.name, f.month, f.day, f.tag
			});
		}
		
		for (int i = 0; i < DbDescriber.lCFestival.length; ++i) {
			Festival f = DbDescriber.lCFestival[i];
			db.execSQL("insert into tb_cfest (name, month, day, tag)values(?, ?, ?, ?)", new Object[]{
				f.name, f.month, f.day, f.tag
			});
		}
		
		for (MensesDate m : DbDescriber.lMensesDate) {
			db.execSQL("insert into tb_menses (date)values(?)", new Object[]{
				m.cipher
			});
		}
	}
	
	public long addDate(int year, int month, int day) {
		return addDate(new MensesDate(0, year, month, day));
	}
	
	public long addDate(MensesDate date) {
		long rowsAffected = 0;
		ContentValues values = new ContentValues();
		values.put(DbDescriber.ColDate, date.cipher);
		SQLiteDatabase writer = getWritableDatabase();
		rowsAffected = writer.update(DbDescriber.tbMenses, values, "date=" + date.cipher, null);
		if (rowsAffected == 0) {
			rowsAffected = writer.insert(DbDescriber.tbMenses, null, values);
		}
		writer.close();
		return rowsAffected;
	}
	
	public void updateDate(MensesDate date) {
		SQLiteDatabase db = getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(DbDescriber.ColDate, date.cipher);
		db.update(DbDescriber.tbMenses, values, "id=" + date.id, null);
		db.close();
	}
	
	public List<MensesDate> readDates() {
		List<MensesDate> dates = new ArrayList<MensesDate>();
		SQLiteDatabase db = getReadableDatabase();
		Cursor c = db.query(DbDescriber.tbMenses, null, null, null, null, null, DbDescriber.ColDate);
		while (c.moveToNext()) {
			MensesDate d = new MensesDate(
				c.getInt(c.getColumnIndex(DbDescriber.ColId)),
				c.getInt(c.getColumnIndex(DbDescriber.ColDate))
				);
			dates.add(d);
		}
		c.close();
		db.close();
		return dates;
	}
	
	private Map<Integer, String> mGFests = null;
	private Map<Integer, String> mCFests = null;
	@SuppressLint("UseSparseArrays")
	private final void fetchAllGFestivals() {
		if (null == mGFests) {
			mGFests = new HashMap<Integer, String>();
			SQLiteDatabase reader = getReadableDatabase();
			Cursor c = reader.query(DbDescriber.tbGFestival, DbDescriber.ColNameAndTag, null, null, null, null, null);
			while (c.moveToNext()) {
				mGFests.put(c.getInt(c.getColumnIndex("tag")), c.getString(c.getColumnIndex("name")));
			}
			c.close();
			reader.close();
		}
	}
	
	@SuppressLint("UseSparseArrays")
	private final void fetchAllCFestivals() {
		if (null == mCFests) {
			mCFests = new HashMap<Integer, String>();
			SQLiteDatabase reader = getReadableDatabase();
			Cursor c = reader.query(DbDescriber.tbCFestival, DbDescriber.ColNameAndTag, null, null, null, null, null);
			while (c.moveToNext()) {
				mCFests.put(c.getInt(c.getColumnIndex("tag")), c.getString(c.getColumnIndex("name")));
			}
			c.close();
			reader.close();
		}
	}
	
	public String getAgFest(int gMonth, int gDay) {
		if (null == mGFests) fetchAllGFestivals();
		return mGFests.get((gMonth << 5) + gDay);
	}
	
	public String getAcFest(int cMonth, int cDay) {
		if (null == mCFests) fetchAllCFestivals();
		return mCFests.get((cMonth << 5) + cDay);
	}
	
	public List<Festival> getGFestivals(int fromMonth, int fromDay, int toMonth, int toDay) {
		List<Festival> fests = new ArrayList<Festival>();
		SQLiteDatabase reader = getReadableDatabase();
		Cursor c = null;
		if (fromMonth <= toMonth) {
			c = reader.rawQuery("select * from tb_gfest where tag >= ? and ? >= tag",
		              new String[] { ((fromMonth << 5) + fromDay) + "", ((toMonth << 5) + toDay) + "" });
			while (c.moveToNext()) {
				String name = c.getString(c.getColumnIndex("name"));
				Integer month = c.getInt(c.getColumnIndex("month"));
				Integer day = c.getInt(c.getColumnIndex("day"));
				Integer tag = c.getInt(c.getColumnIndex("tag"));
				fests.add(new Festival(name, month, day, tag));
			}
		}
		else {
			c = reader.rawQuery("select * from tb_gfest where tag >= ? and 415 >= tag",
		              new String[] { ((fromMonth << 5) + fromDay) + ""});
			while (c.moveToNext()) {
				String name = c.getString(c.getColumnIndex("name"));
				Integer month = c.getInt(c.getColumnIndex("month"));
				Integer day = c.getInt(c.getColumnIndex("day"));
				Integer tag = c.getInt(c.getColumnIndex("tag"));
				fests.add(new Festival(name, month, day, tag));
			}
			c.close();
			c = reader.rawQuery("select * from tb_gfest where tag >= 33 and ? >= tag",
		              new String[] { ((toMonth << 5) + toDay) + ""});
			while (c.moveToNext()) {
				String name = c.getString(c.getColumnIndex("name"));
				Integer month = c.getInt(c.getColumnIndex("month"));
				Integer day = c.getInt(c.getColumnIndex("day"));
				Integer tag = c.getInt(c.getColumnIndex("tag"));
				fests.add(new Festival(name, month, day, tag));
			}
		}
		c.close();
		reader.close();
		
		return fests;
	}
	
	public SinaWeather readWeather(int year, int month, int date, String city) {
		int packedDate = DateInterpreter.pack(year, month, date);
		
		return readWeather(packedDate, city);
	}
	
	public SinaWeather readWeather(int packedDate, String city) {
		int id = SinaWeather.generateId(packedDate, city);
		
		// System.err.println("readWeather(date: " + packedDate + ", city: " + city.hashCode() + ", id: " + id + ")");
		
		SinaWeather w = null;
		SQLiteDatabase db = getReadableDatabase();
		
		Cursor c = db.query(
				DbDescriber.tbWeather, null, "id=?", new String[] {String.valueOf(id)}, null, null, null);
		if (c.moveToNext()) {
			w = SinaWeather.fromCursor(c);
		}
		
		c.close();
		db.close();
		return w;
	}
	
	public long writeWeather(SinaWeather w) {
		long rowsAffected = 0;
		ContentValues values = SinaWeather.toContentValues(w);
		
		SQLiteDatabase writer = getWritableDatabase();
		
		// System.err.println("writeWeather(date: " + w.date + ", city: " + w.city.hashCode() + ", id: " + w.id + ")");
		
		rowsAffected = writer.update(DbDescriber.tbWeather, values, "id=?", new String[] {String.valueOf(w.id)});
		if (0 == rowsAffected) {
			rowsAffected = writer.insert(DbDescriber.tbWeather, null, values);
		}
		
		writer.close();
		
		return rowsAffected;
	}
	
	public List<SinaWeather> readWeathers() {
		List<SinaWeather> ws = new ArrayList<SinaWeather>();
		
		SQLiteDatabase db = getReadableDatabase();
		Cursor c = db.query(
				DbDescriber.tbWeather, null, null, null, DbDescriber.ColCity, null, DbDescriber.ColDate);
		
		while (c.moveToNext()) {
			ws.add(SinaWeather.fromCursor(c));
		}
		c.close();
		db.close();
		
		return ws;
	}
	
	public void removeWeathers(int year, int month, int date) {
		int packedDate = DateInterpreter.pack(year, month, date);
		
		removeWeathers(packedDate);
	}
	
	public void removeWeathers(int packedDate) {
		SQLiteDatabase db = getWritableDatabase();
		db.delete(DbDescriber.tbWeather, "date<?", new String[]{String.valueOf(packedDate)});
		db.close();
	}
	
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
	}
	
}
