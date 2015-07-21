package com.example.weather.utils;

import java.util.Calendar;
import java.util.TimeZone;

public class TimeUtil {

	public static String getCurrentDate(){
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeZone(TimeZone.getTimeZone("GMT+8:00"));
		
		int weekDay=calendar.get(Calendar.DAY_OF_WEEK);
		String week;
		switch(weekDay){
			case 1:
				week="日";
				break;
			case 2:
				week="一";
				break;
			case 3:
				week="二";
				break;
			case 4:
				week="三";
				break;
			case 5:
				week="四";
				break;
			case 6:
				week="五";
				break;
			case 7:
				week="六";
				break;
			default:
				week="日";
				break;
		}
		
		return String.valueOf(calendar.get(Calendar.MONTH) + 1)+"月"+String.valueOf(calendar.get(Calendar.DAY_OF_MONTH))+"日"
				+" 星期"+week;
	}
}
