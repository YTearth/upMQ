package com.uphn.upMQ.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MyDateUtil {
    
	public static String getDateTimeNow(String format) {
		SimpleDateFormat sdf = new SimpleDateFormat(format);
		return sdf.format(new Date());
	}
	public static String getDateTimeBefore(int datarange) throws ParseException {
		Calendar calendar = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		Date date = sdf.parse(getDateTimeNow("yyyyMMdd"));
		calendar.setTime(date);
		calendar.add(calendar.DATE, 0 - datarange);
		return sdf.format(calendar.getTime());
	}
	
    //格式校验
	public static boolean validDateTimeWithLongFormat(String timeStr) {
		String format = "((19|20)[0-9]{2})(0?[1-9]|1[012])(0?[1-9]|[12][0-9]|3[01])"
				+ "(([01]?[0-9]|2[0-3])[0-5][0-9][0-5][0-9])?";
		Pattern pattern = Pattern.compile(format);
		Matcher matcher = pattern.matcher(timeStr);
		if (matcher.matches()) {
			pattern = Pattern.compile("(\\d{4})(\\d+)(\\d+).*");
			matcher = pattern.matcher(timeStr);
			if (matcher.matches()) {
				int y = Integer.valueOf(matcher.group(1));
				int m = Integer.valueOf(matcher.group(2));
				int d = Integer.valueOf(matcher.group(3));
				if (d > 28) {
					Calendar c = Calendar.getInstance();
					c.set(y, m-1, 1);
					int lastDay = c.getActualMaximum(Calendar.DAY_OF_MONTH);
					return (lastDay >= d);
				}
			}
			return true;
		}
		return false;
	}
	public static void main(String[] args){
		System.out.println(MyDateUtil.validDateTimeWithLongFormat("201652080202"));
		System.out.println(MyDateUtil.validDateTimeWithLongFormat("20160229080202"));
		System.out.println(MyDateUtil.validDateTimeWithLongFormat("20150232"));
		System.out.println(MyDateUtil.validDateTimeWithLongFormat("18160202"));
	}
}
