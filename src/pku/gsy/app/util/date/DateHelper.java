package pku.gsy.app.util.date;


public final class DateHelper {
	
	public static DateHelper mInstance = null;
	
	public static DateHelper getInstance() {
		if (null == mInstance) {
			mInstance = new DateHelper();
		}
		return mInstance;
	}
	
	public final boolean setDate(DateInfo date, int year, int month, int day) {
		if (year < 1900 | year > 2099) return false;
		date.gYear = year;
		date.gMonth = month;
		date.gDay = day;
		alignChineseFieldsToGregorianFields(date);
		return true;
	}
	
	/**
	 * @param from from date
	 * @param to to date
	 */
	public void setDate(DateInfo from, DateInfo to) {
		to.gYear = from.gYear;
		to.gMonth = from.gMonth;
		to.gDay = from.gDay;
		to.cYear = from.cYear;
		to.cMonth = from.cMonth;
		to.cDay = from.cDay;
	}
	
	/**
	 * 计算 month/date/year是星期几。
	 * Zeller 公式: 
	 *   w = [c / 4] - 2 * c + y + [y / 4] + [26 * (m + 1) / 10] + d - 1
	 *   相当于：
	 *   w = (c >> 2) - (c << 1) + y + (y >> 2) + [26 * (m + 1) / 10] + d - 1
	 * c : 世纪 - 1，即年份的高两位。
	 * y : 年份的后两位。
	 * m : 月份。1，2，3月表示为上一年的13，14，15月。 
	 * d : 日期。
	 * 
	 * 注释:
	 *   "[]" 表示取整数部分。
	 *   从1900年至2100年，w的最小值为-27，所以w+28>0。
	 * 
	 * 返回:
	 *     星期几. Sunday 1, Monday 2, ... , Saturday 7
	 */
	public final int dayOfWeek(DateInfo date) {
		int year = date.gYear;
		int month = date.gMonth;
		int day = date.gDay;
		
		if (month < 3) { --year; month += 12; }
		int century = year / 100;
		year = year % 100;
		
		int w = (century >> 2) - (century << 1) + year + (year >> 2) + 26 * (month + 1) / 10 + day + 27;
		
		return (w % 7 + 1);
	}
	
	public final int dayOfWeek(int year, int month, int day) {
		if (month < 3) { --year; month += 12; }
		int century = year / 100;
		year = year % 100;
		
		int w = (century >> 2) - (century << 1) + year + (year >> 2) + 26 * (month + 1) / 10 + day + 27;
		
		return (w % 7 + 1);
	}
	
	public final int daysInMonth(int gYear, int gMonth) {
		if (gMonth != 2) {
			return daysInGregorianMonth[gMonth];
		} else {
			if (!isLeapYear(gYear))
				return 28;
			else
				return 29;
		}
	}
	
	public final boolean rollUpOneDay(DateInfo date) {
		if (date.gYear >= 2099 && date.gMonth == 12 && date.gDay == 31) return false;
		addDaysToGregorianFields(date, 1);
		addDaysToChineseFields(date, 1);
		return true;
	}
	
	public final boolean rollBackOneDay(DateInfo date) {
		if (date.gYear <= 1900 && date.gMonth == 1 && date.gDay == 1) return false;
		minusDaysToGregorianFields(date, 1);
		minusDaysToChineseFields(date, 1);
		return true;
	}
	
	public final boolean addDays(DateInfo date, int offset) {
		if (offset != 0) {
			int year = date.gYear;
			int month = date.gMonth;
			int day = date.gDay;
			if (offset > 0) {
				addDaysToGregorianFields(date, offset);
				if (date.gYear > 2099) {
					date.gYear = year;
					date.gMonth = month;
					date.gDay = day;
					return false;
				} else {
					addDaysToChineseFields(date, offset);
				}

			} else { // offset < 0
				minusDaysToGregorianFields(date, -offset);
				if (date.gYear < 1900) {
					date.gYear = year;
					date.gMonth = month;
					date.gDay = day;
					return false;
				} else {
					minusDaysToChineseFields(date, -offset);
				}
			}
		}
		return true;
	}
	
	public final boolean RollUpOneMonth(DateInfo date) {
		if (date.gYear >= 2099 && date.gMonth == 12) return false;
		// 如果下个月天数少于此月，且当前为此月最后一天，则移至下个月最后一天。
		int days0 = daysInMonth(date.gYear, date.gMonth);
		date.gMonth = (date.gMonth == 12 ? 1 : date.gMonth + 1);
		if (1 == date.gMonth) ++date.gYear;
		int days1 = daysInMonth(date.gYear, date.gMonth);
		
		if (date.gDay == days0 && days1 < days0) {
			date.gDay = days1;
			addDaysToChineseFields(date, days1);
		} else {
			addDaysToChineseFields(date, days0);
		}
		return true;
	}
	
	public final boolean RollBackOneMonth(DateInfo date) {
		if (date.gYear <= 1900 && date.gMonth == 1) return false;
		int days0 = daysInMonth(date.gYear, date.gMonth);
		date.gMonth = (date.gMonth == 1 ? 12 : date.gMonth - 1);
		if (date.gMonth == 12) --date.gYear;
		int days1 = daysInMonth(date.gYear, date.gMonth);
		
		if (date.gDay == days0 && days1 < days0) {
			date.gDay = days1;
			minusDaysToChineseFields(date, days0);
		} else {
			minusDaysToChineseFields(date, days1);
		}
		return true;
	}
	
	public final boolean RollUpOneYear(DateInfo date) {
		if (date.gYear >= 2099) return false;
		int offset = 365;
		if (1 == date.gMonth) {
			if (isLeapYear(date.gYear)) offset = 366;
		} else if (2 == date.gMonth) {
			if (isLeapYear(date.gYear)) {
				if (date.gDay < 29) {
					offset = 366;
				} else {
					date.gDay = 28;
				}
			}
		} else {
			if (isLeapYear(date.gYear + 1)) offset = 366;
		}
		
		date.gYear += 1;
		
		addDaysToChineseFields(date, offset);
		
		return true;
	}
	
	public final boolean RollBackOneYear(DateInfo date) {
		if (date.gYear <= 1900) return false;
		int offset = 365;
		if (date.gMonth == 1) {
			if (isLeapYear(date.gYear - 1)) offset = 366;
		} else if (date.gMonth == 2) {
			if (isLeapYear(date.gYear)) {
				if (date.gDay == 29) {
					offset = 366;
					date.gDay = 28;
				}
			} else {
				if (isLeapYear(date.gYear - 1)) {
					offset = 366;
				}
			}
		} else {
			if (isLeapYear(date.gYear)) offset = 366;
		}
		
		date.gYear -= 1;
		
		minusDaysToChineseFields(date, offset);
		
		return true;
	}
	
	/**
	 * 通过公历日期推算农历日期。
	 * 
	 * 算法描述:
	 *   以公历日期为参考，计算对其点日期到“计算日期”的天数。然后一个月一个月的减去中国农历月的天数，计算出中国农历日期。
	 *   仅为一个简单描述，如何计算请分析代码。
	 */
	private final void alignChineseFieldsToGregorianFields(DateInfo date) {
		// Determine nearest reference point. And set Chinese fields to it.
		int startYear = date.gYear / 10 * 10;
		int index = (date.gYear - baseGregorianYear) / 10;
		date.cYear = referencePoint[index][0];
		date.cMonth = referencePoint[index][1];
		date.cDay = referencePoint[index][2];
		
		// 计算公历日期至参照点的天数。
		int cumulativeDays = 0;
		for (int i = startYear; i < date.gYear; i++) {
			if (isLeapYear(i)) cumulativeDays += 366;
			else cumulativeDays += 365;
		}
		cumulativeDays += daysBeforeGMonth(date.gYear, date.gMonth);
		cumulativeDays += date.gDay - 1;
		
		addDaysToChineseFields(date, cumulativeDays);
	}
	
	private final void addDaysToGregorianFields(DateInfo date, int offset) {
		date.gDay += offset;
		int days = daysInMonth(date.gYear, date.gMonth);
		while (date.gDay > days) {
			date.gMonth++;
			if (date.gMonth > 12) {
				date.gMonth = 1;
				date.gYear++;
			}
			
			date.gDay -= days;
			days = daysInMonth(date.gYear, date.gMonth);
		}
	}
	
	private final void minusDaysToGregorianFields(DateInfo date, int offset) {
		int days = daysInMonth(date.gYear, date.gMonth);
		int remainder = offset + (days - date.gDay);
		while (remainder >= days) {
			date.gMonth--;
			if (date.gMonth < 1) {
				date.gMonth = 12;
				date.gYear--;
			}
			
			remainder -= days;
			days = daysInMonth(date.gYear, date.gMonth);
		}
		date.gDay = days - remainder;
	}
	
	private final void addDaysToChineseFields(DateInfo date, int offset) {
		date.cDay += offset;
		int days = daysInChineseMonth(date.cYear, date.cMonth);
		int nextMonth = 0;
		
		while (date.cDay > days) {
			nextMonth = nextChineseMonth(date.cYear, date.cMonth);
			if (Math.abs(nextMonth) < Math.abs(date.cMonth)) ++date.cYear;
			date.cMonth = nextMonth;
			date.cDay -= days;
			days = daysInChineseMonth(date.cYear, date.cMonth);
		}
	}
	
	private final void minusDaysToChineseFields(DateInfo date, int offset) {
		int days = daysInChineseMonth(date.cYear, date.cMonth);
		int remainder = offset + (days - date.cDay);
		int previousMonth = 0;
		while (remainder >= days) {
			previousMonth = previousChineseMonth(date.cYear, date.cMonth);
			if (Math.abs(previousMonth) > Math.abs(date.cMonth)) --date.cYear;
			date.cMonth = previousMonth;
			
			remainder -= days;
			days = daysInChineseMonth(date.cYear, date.cMonth);
		}
		date.cDay = days - remainder;
	}
	
	/**
	 * @return 天干+地支的年名字
	 */
//	public final String GetChineseYearName(DateInfo date) {
//		return GetChineseYearName(date.cYear);
//	}
	public final String GetChineseYearName(int cYear) {
		cYear -= 2;
		return stemNames[cYear%10] + branchNames[cYear%12];
	}
	
	/**
	 * @return 0 到 11
	 */
//	public final int GetAnimalIndex(DateInfo date) {
//		return GetAnimalIndex(date.cYear);
//	}
	public final int GetAnimalIndex(int cYear) {
		return (cYear - 2) % 12;
	}
	
	/**
	 * @return 属相的名称
	 */
//	public final String GetAnimalName(DateInfo date) {
//		return GetAnimalName(date.cYear);
//	}
	public final String GetAnimalName(int cYear) {
		return animalNames[(cYear - 2) % 12];
	}
	
	/**
	 * @return 返回上半个月的节气名称
	 */
	public final String GetSectionalTermName(int gMonth) {
		return termNames[gMonth - 1];
	}
	
	/**
	 * @return 返回下半个月的节气名称
	 */
	public final String GetPrincipleTermName(int gMonth) {
		return termNames[11 + gMonth];
	}
	
	public final boolean isLeapYear(int gYear) {
		return ((gYear % 4 == 0 && gYear % 100 != 0) || (gYear % 400 == 0));
	}
	
	/**
	 * 计算公历'year'年/'month'月之前已经经历了多少天。
	 */
	private final int daysBeforeGMonth(int gYear, int gMonth)
	{
		if (gMonth > 2)
		{
			if (!isLeapYear(gYear))
				return cumulativeDaysBeforeGregorianMonth[gMonth];
			else
				return cumulativeDaysBeforeGregorianMonth[gMonth] + 1;
		} else {
			return cumulativeDaysBeforeGregorianMonth[gMonth];
		}
	}
	
	/**
	 * 查表获取中国农历'cYear'年/'cMonth'月的天数。
	 */
	private final int daysInChineseMonth(int cYear, int cMonth) {
		int index = cYear - baseChineseYear;
		char info = chineseLunarMonthInfo[index];
		char mask = 0x8000;
		if (cMonth > 0) {
			mask >>= (cMonth - 1);
			if ((info & mask) == 0x00) return 29;
			else return 30;
		} else {
			if ((info & 0x000F) == -cMonth) {
				for (int i = 0; i < bigLeapMonthYears.length; i++) {
					if (bigLeapMonthYears[i] == index) return 30;
				}
				return 29;
			}
			return 0;
		}
	}
	
	/**
	 * 计算中国农历'cYear'年/'cMonth'月的下一个月。
	 */
	private final int nextChineseMonth(int cYear, int cMonth) {
		if (cMonth > 0) {
			// Check whether the next month is a leap month.
			int index = cYear - baseChineseYear;
			char info = chineseLunarMonthInfo[index];
			
			if ((info & 0x000F) == cMonth) cMonth = -cMonth;
			else cMonth++;
			
		} else {
			cMonth = -cMonth + 1;
		}
		
		if (cMonth == 13) cMonth = 1;
		
		return cMonth;
	}
	
	/**
	 * 计算中国农历'cYear'年/'cMonth'月的前一个月。
	 */
	private final int previousChineseMonth(int cYear, int cMonth) {
		if (cMonth > 1) {
			int index = cYear - baseChineseYear;
			char info = chineseLunarMonthInfo[index];
			cMonth -= 1;
			if ((info & 0x000F) == cMonth) cMonth = -cMonth;
			
		} else if (cMonth == 1) {
			cMonth = 12;
		} else {
			cMonth = -cMonth;
		}
		
		return cMonth;
	}
	
	/**
	 * 获取公历月中第一个节气的日期。
	 */
	public final int sectionalTerm(int gYear, int gMonth) {
		int index = 0;
		int ry = gYear - baseGregorianYear;
		while (ry >= sectionalTermYear[gMonth - 1][index]) index++;
		int term = sectionalTermMap[gMonth - 1][4 * index + ry % 4];
		if ((ry == 121) && (gMonth == 4))
			term = 5;
		if ((ry == 132) && (gMonth == 4))
			term = 5;
		if ((ry == 194) && (gMonth == 6))
			term = 6;
		return term;
	}
	
	/**
	 * 获取公历月中第二个节气的日期。
	 */
	public final int principleTerm(int gYear, int gMonth) {
		int index = 0;
		int ry = gYear - baseGregorianYear + 1;
		while (ry >= principleTermYear[gMonth - 1][index])
			index++;
		int term = principleTermMap[gMonth - 1][4 * index + ry % 4];
		if ((ry == 171) && (gMonth == 3))
			term = 21;
		if ((ry == 181) && (gMonth == 5))
			term = 21;
		return term;
	}
	
	private final int[] daysInGregorianMonth = { 0, // 占位符
		31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31
	};
	
	private final int[] cumulativeDaysBeforeGregorianMonth = { 0, // 占位符
		0, 31, 59, 90, 120, 151, 181, 212, 243, 273, 304, 334 /*, 365*/
	};
	
	/**
	 * 使用char类型（16位）表示中国农历年一年的大月/小月信息。
	 * 定义字节序: 15 ~ 12, 11 ~ 8, 7 ~ 4 | 3 ~ 0
	 * 15至4位依次表示一月至12月，3至0位表示闰月月份。
	 * “1”表示大月，“0”表示小月。
	 */
	private final char[] chineseLunarMonthInfo = {
		0xab50, // 4597; 1899
		0x4bd8,0x4ae0,0xa570,0x54d5,0xd260,0xd950,0x6554,0x56a0,0x9ad0,0x55d2, // 4598 - 4607; 1900 - 1909
		0x4ae0,0xa5b6,0xa4d0,0xd250,0xd255,0xb540,0xd6a0,0xada2,0x95b0,0x4977, // 4608 - 4617; 1910 - 1919
		0x4970,0xa4b0,0xb4b5,0x6a50,0x6d40,0xab54,0x2b60,0x9570,0x52f2,0x4970, // 4618 - 4627; 1920 - 1929
		0x6566,0xd4a0,0xea50,0x6a95,0x5ad0,0x2b60,0x86e3,0x92e0,0xc8d7,0xc950, // 4628 - 4637; 1930 - 1939
		0xd4a0,0xd8a6,0xb550,0x56a0,0xa5b4,0x25d0,0x92d0,0xd2b2,0xa950,0xb557, // 4638 - 4647; 1940 - 1949
		0x6ca0,0xb550,0x5355,0x4da0,0xa5b0,0x4573,0x52b0,0xa9a8,0xe950,0x6aa0, // 4648 - 4657; 1950 - 1959
		0xaea6,0xab50,0x4b60,0xaae4,0xa570,0x5260,0xf263,0xd950,0x5b57,0x56a0, // 4658 - 4667; 1960 - 1969
		0x96d0,0x4dd5,0x4ad0,0xa4d0,0xd4d4,0xd250,0xd558,0xb540,0xb6a0,0x95a6, // 4668 - 4677; 1970 - 1979
		0x95b0,0x49b0,0xa974,0xa4b0,0xb27a,0x6a50,0x6d40,0xaf46,0xab60,0x9570, // 4678 - 4687; 1980 - 1989
		0x4af5,0x4970,0x64b0,0x74a3,0xea50,0x6b58,0x5ac0,0xab60,0x96d5,0x92e0, // 4688 - 4697; 1990 - 1999
		0xc960,0xd954,0xd4a0,0xda50,0x7552,0x56a0,0xabb7,0x25d0,0x92d0,0xcab5, // 4698 - 4707; 2000 - 2009
		0xa950,0xb4a0,0xbaa4,0xad50,0x55d9,0x4ba0,0xa5b0,0x5176,0x52b0,0xa930, // 4708 - 4717; 2010 - 2019
		0x7954,0x6aa0,0xad50,0x5b52,0x4b60,0xa6e6,0xa4e0,0xd260,0xea65,0xd530, // 4718 - 4727; 2020 - 2029
		0x5aa0,0x76a3,0x96d0,0x4afb,0x4ad0,0xa4d0,0xd0b6,0xd250,0xd520,0xdd45, // 4728 - 4737; 2030 - 2039
		0xb5a0,0x56d0,0x55b2,0x49b0,0xa577,0xa4b0,0xaa50,0xb255,0x6d20,0xada0, // 4738 - 4747; 2040 - 2049
		0x4b63,0x9370,0x49f8,0x4970,0x64b0,0x68a6,0xea50,0x6b20,0xa6c4,0xaae0, // 4748 - 4757; 2050 - 2059
		0x92e0,0xd2e3,0xc960,0xd557,0xd4a0,0xda50,0x5d55,0x56a0,0xa6d0,0x55d4, // 4758 - 4767; 2060 - 2069
		0x52d0,0xa9b8,0xa950,0xb4a0,0xb6a6,0xad50,0x55a0,0xaba4,0xa5b0,0x52b0, // 4768 - 4777; 2070 - 2079
		0xb273,0x6930,0x7337,0x6aa0,0xad50,0x4b55,0x4b60,0xa570,0x54e4,0xd260, // 4778 - 4787; 2080 - 2089
		0xe968,0xd520,0xdaa0,0x5aa6,0x56d0,0x4ae0,0xa9d4,0xa4d0,0xd150,0xf252, // 4788 - 4797; 2090 - 2099
		0xd530 // 4798; 2100
	};
	
	// 大多数闰月是29天，部分为30天。此变量记录大闰月年份。
	// 第一个值表示1906年 (即中国农历 4604年)
	private final int[] bigLeapMonthYears = {
		7, 15, 20, 26, 34, 37, 39, 42, 45, 53, 56, 80, 118, 137, 148, 151, 156, 159, 186, 194 
	};
	
	// 起始年份，在计算"char[] chineseLunarMonthInfo" 和 "int[] bigLeapMonthYears"时使用。
	private final int baseGregorianYear = 1900;
	private final int baseChineseYear = 4597; // 12/1/4597 (1/1/1900)
	
	// 多个工农历日期对照点，提升程序执行性能。
	private final int[][] referencePoint = {
		{4597, 12, 1},  // 1/1/1900
		{4607, 11, 20}, // 1/1/1910
		{4617, 11, 11}, // 1/1/1920
		{4627, 12, 2},  // 1/1/1930
		{4637, 11, 22}, // 1/1/1940
		{4647, 11, 13}, // 1/1/1950
		{4657, 12, 3},  // 1/1/1960
		{4667, 11, 24}, // 1/1/1970
		{4677, 11, 14}, // 1/1/1980
		{4687, 12, 5},  // 1/1/1990
		{4697, 11, 25}, // 1/1/2000
		{4707, 11, 17}, // 1/1/2010
		{4717, 12, 7},  // 1/1/2020
		{4727, 11, 28}, // 1/1/2030
		{4737, 11, 17}, // 1/1/2040
		{4747, 12, 8},  // 1/1/2050
		{4757, 11, 28}, // 1/1/2060
		{4767, 11, 19}, // 1/1/2070
		{4777, 12, 10}, // 1/1/2080
		{4787, 12, 1}   // 1/1/2090
	};
	
	/**
	 * 如下表都是用来计算中国农历的节气的。
	 * 中国农历实际上是一种工农历结合的历法。24节气均匀分布在公历的12个月份里，每月两个。一月份是“小寒”和“大寒”……。
	 * 表的含义及算法描述，请查看：http://zhw410.blog.163.com/blog/static/222767520091130102535908/
	 */
	private final short[][] sectionalTermYear = {
		{ 13, 49, 85, 117, 149, 185, 201 },
		{ 13, 45, 81, 117, 149, 185, 201 },
		{ 13, 48, 84, 112, 148, 184, 200, 201 },
		{ 13, 45, 76, 108, 140, 172, 200, 201 },
		{ 13, 44, 72, 104, 132, 168, 200, 201 },
		{ 5,  33, 68, 96,  124, 152, 188, 200, 201 },
		{ 29, 57, 85, 120, 148, 176, 200, 201 },
		{ 13, 48, 76, 104, 132, 168, 196, 200, 201 },
		{ 25, 60, 88, 120, 148, 184, 200, 201 },
		{ 16, 44, 76, 108, 144, 172, 200, 201 },
		{ 28, 60, 92, 124, 160, 192, 200, 201 },
		{ 17, 53, 85, 124, 156, 188, 200, 201 } 
	};
	
	private final byte[][] sectionalTermMap = {
		{ 7, 6, 6, 6, 6, 6, 6, 6, 6, 5, 6, 6, 6, 5, 5, 6, 6, 5, 5, 5, 5, 5, 5, 5, 5, 4, 5, 5 },
		{ 5, 4, 5, 5, 5, 4, 4, 5, 5, 4, 4, 4, 4, 4, 4, 4, 4, 3, 4, 4, 4, 3, 3, 4, 4, 3, 3, 3 },
		{ 6, 6, 6, 7, 6, 6, 6, 6, 5, 6, 6, 6, 5, 5, 6, 6, 5, 5, 5, 6, 5, 5, 5, 5, 4, 5, 5, 5, 5 },
		{ 5, 5, 6, 6, 5, 5, 5, 6, 5, 5, 5, 5, 4, 5, 5, 5, 4, 4, 5, 5, 4, 4, 4, 5, 4, 4, 4, 4, 5 },
		{ 6, 6, 6, 7, 6, 6, 6, 6, 5, 6, 6, 6, 5, 5, 6, 6, 5, 5, 5, 6, 5, 5, 5, 5, 4, 5, 5, 5, 5 },
		{ 6, 6, 7, 7, 6, 6, 6, 7, 6, 6, 6, 6, 5, 6, 6, 6, 5, 5, 6, 6, 5, 5, 5, 6, 5, 5, 5, 5, 4, 5, 5, 5, 5 },
		{ 7, 8, 8, 8, 7, 7, 8, 8, 7, 7, 7, 8, 7, 7, 7, 7, 6, 7, 7, 7, 6, 6, 7, 7, 6, 6, 6, 7, 7 },
		{ 8, 8, 8, 9, 8, 8, 8, 8, 7, 8, 8, 8, 7, 7, 8, 8, 7, 7, 7, 8, 7, 7, 7, 7, 6, 7, 7, 7, 6, 6, 7, 7, 7 },
		{ 8, 8, 8, 9, 8, 8, 8, 8, 7, 8, 8, 8, 7, 7, 8, 8, 7, 7, 7, 8, 7, 7, 7, 7, 6, 7, 7, 7, 7 },
		{ 9, 9, 9, 9, 8, 9, 9, 9, 8, 8, 9, 9, 8, 8, 8, 9, 8, 8, 8, 8, 7, 8, 8, 8, 7, 7, 8, 8, 8 },
		{ 8, 8, 8, 8, 7, 8, 8, 8, 7, 7, 8, 8, 7, 7, 7, 8, 7, 7, 7, 7, 6, 7, 7, 7, 6, 6, 7, 7, 7 },
		{ 7, 8, 8, 8, 7, 7, 8, 8, 7, 7, 7, 8, 7, 7, 7, 7, 6, 7, 7, 7, 6, 6, 7, 7, 6, 6, 6, 7, 7 } 
	};
		
	private final short[][] principleTermYear = {
		{ 13, 45, 81, 113, 149, 185, 201 },
		{ 21, 57, 93, 125, 161, 193, 201 },
		{ 21, 56, 88, 120, 152, 188, 200, 201 },
		{ 21, 49, 81, 116, 144, 176, 200, 201 },
		{ 17, 49, 77, 112, 140, 168, 200, 201 },
		{ 28, 60, 88, 116, 148, 180, 200, 201 },
		{ 25, 53, 84, 112, 144, 172, 200, 201 },
		{ 29, 57, 89, 120, 148, 180, 200, 201 },
		{ 17, 45, 73, 108, 140, 168, 200, 201 },
		{ 28, 60, 92, 124, 160, 192, 200, 201 },
		{ 16, 44, 80, 112, 148, 180, 200, 201 },
		{ 17, 53, 88, 120, 156, 188, 200, 201 } 
	};
	
	private final byte[][] principleTermMap = {
		{ 21, 21, 21, 21, 21, 20, 21, 21, 21, 20, 20, 21, 21, 20, 20, 20, 20, 20, 20, 20, 20, 19, 20, 20, 20, 19, 19, 20 },
		{ 20, 19, 19, 20, 20, 19, 19, 19, 19, 19, 19, 19, 19, 18, 19, 19, 19, 18, 18, 19, 19, 18, 18, 18, 18, 18, 18, 18 },
		{ 21, 21, 21, 22, 21, 21, 21, 21, 20, 21, 21, 21, 20, 20, 21, 21, 20, 20, 20, 21, 20, 20, 20, 20, 19, 20, 20, 20, 20 },
		{ 20, 21, 21, 21, 20, 20, 21, 21, 20, 20, 20, 21, 20, 20, 20, 20, 19, 20, 20, 20, 19, 19, 20, 20, 19, 19, 19, 20, 20 },
		{ 21, 22, 22, 22, 21, 21, 22, 22, 21, 21, 21, 22, 21, 21, 21, 21, 20, 21, 21, 21, 20, 20, 21, 21, 20, 20, 20, 21, 21 },
		{ 22, 22, 22, 22, 21, 22, 22, 22, 21, 21, 22, 22, 21, 21, 21, 22, 21, 21, 21, 21, 20, 21, 21, 21, 20, 20, 21, 21, 21 },
		{ 23, 23, 24, 24, 23, 23, 23, 24, 23, 23, 23, 23, 22, 23, 23, 23, 22, 22, 23, 23, 22, 22, 22, 23, 22, 22, 22, 22, 23 },
		{ 23, 24, 24, 24, 23, 23, 24, 24, 23, 23, 23, 24, 23, 23, 23, 23, 22, 23, 23, 23, 22, 22, 23, 23, 22, 22, 22, 23, 23 },
		{ 23, 24, 24, 24, 23, 23, 24, 24, 23, 23, 23, 24, 23, 23, 23, 23, 22, 23, 23, 23, 22, 22, 23, 23, 22, 22, 22, 23, 23 },
		{ 24, 24, 24, 24, 23, 24, 24, 24, 23, 23, 24, 24, 23, 23, 23, 24, 23, 23, 23, 23, 22, 23, 23, 23, 22, 22, 23, 23, 23 },
		{ 23, 23, 23, 23, 22, 23, 23, 23, 22, 22, 23, 23, 22, 22, 22, 23, 22, 22, 22, 22, 21, 22, 22, 22, 21, 21, 22, 22, 22 },
		{ 22, 22, 23, 23, 22, 22, 22, 23, 22, 22, 22, 22, 21, 22, 22, 22, 21, 21, 22, 22, 21, 21, 21, 22, 21, 21, 21, 21, 22 } 
	};
	
	private final String[] stemNames = { 
		"甲", "乙", "丙", "丁", "戊", "己", "庚", "辛", "壬", "癸"
	};
	
	private final String[] branchNames = { 
		"子", "丑", "寅", "卯", "辰", "巳", "午", "未", "申", "酉", "戌", "亥"
	};
	
	private final String[] animalNames = { 
		"鼠", "牛", "虎", "兔", "龙", "蛇", "马", "羊", "猴", "鸡", "狗", "猪"
	};
	
	private final String[] termNames = {
		"小寒", "立春", "惊蛰", "清明", "立夏", "芒种", "小暑", "立秋", "白露", "寒露", "立冬", "大雪",
		"大寒", "雨水", "春分", "谷雨", "夏满", "夏至", "大暑", "处暑", "秋分", "霜降", "小雪", "冬至" 
	};
	
	public final String[] chineseMonthNames = { null, // 占位符
		"正", "二", "三", "四", "五", "六", "七", "八", "九", "十", "冬", "腊" 
	};
	
	public final String[] gregorianDateNames = { null, // 占位符
		"1", "2", "3", "4", "5", "6", "7", "8", "9", "10", 
		"11", "12", "13", "14", "15", "16", "17", "18", "19", "20",
		"21", "22", "23", "24", "25", "26", "27", "28", "29", "30",
		"31"
	};
	
	public final String[] chineseDateNames = { null, // 占位符
		"初一", "初二", "初三", "初四", "初五", "初六", "初七", "初八", "初九", "初十", 
		"十一", "十二", "十三", "十四", "十五", "十六", "十七", "十八", "十九", "二十", 
		"廿一", "廿二", "廿三", "廿四", "廿五", "廿六", "廿七", "廿八", "廿九", "三十",
		"卅一"
	};
}
