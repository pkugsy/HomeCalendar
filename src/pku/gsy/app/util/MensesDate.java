package pku.gsy.app.util;

public class MensesDate {
	public int id = 0;
	public int year = 0;
	public int month = 0;
	public int day = 0;
	public int cipher = 0;
	
	public MensesDate(int cipher) {
		this(0, cipher);
	}
	
	public MensesDate(int year, int month, int day) {
		this(0, year, month, day);
	}
	
	public MensesDate(int id, int year, int month, int day) {
		this.id = id;
		this.year = year;
		this.month = month;
		this.day = day;
		cipher = ((year << 16) | (month << 8) | (day));
	}
	
	public MensesDate(int id, int cipher) {
		this.id = id;
		this.cipher = cipher;
		this.year = (cipher >> 16);
		this.month = ((cipher >> 8) & 0xFF);
		this.day = (cipher & 0xFF);
	}
	
	public MensesDate copy(final MensesDate date) {
		this.year = date.year;
		this.month = date.month;
		this.day = date.day;
		this.cipher = ((this.year << 16) | (this.month << 8) | (this.day));
		return this;
	}
	
//	public void set(int year, int month, int day) {
//		this.year = year;
//		this.month = month;
//		this.day = day;
//		cipher = ((year << 16) | (month << 8) | (day));
//	}
	
	public final CharSequence getString() {
		StringBuilder builder = new StringBuilder();
		builder.append(this.year);
		builder.append("/");
		builder.append(this.month);
		builder.append("/");
		builder.append(this.day);
		return builder;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("{class MensesDate ( id=");
		builder.append(this.id);
		builder.append(" ");
		builder.append(this.year);
		builder.append("/");
		builder.append(this.month);
		builder.append("/");
		builder.append(this.day);
		builder.append("-");
		builder.append(this.cipher);
		builder.append("}");
		return builder.toString();
	}
}
