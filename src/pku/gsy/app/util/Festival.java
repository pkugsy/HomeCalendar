package pku.gsy.app.util;

public class Festival {
	public String name;
	public int month;
	public int day;
	public int tag;
	
	public Festival(String name, int month, int day) {
		this.name = name;
		this.month = month;
		this.day = day;
		this.tag = (month << 5) + day;
	}

	public Festival(String name, int month, int day, int tag) {
		super();
		this.name = name;
		this.month = month;
		this.day = day;
		this.tag = tag;
	}

	@Override
	public String toString() {
		return "Festival [name=" + name + ", month=" + month + ", day=" + day
				+ ", tag=" + tag + "]";
	}
	
}
