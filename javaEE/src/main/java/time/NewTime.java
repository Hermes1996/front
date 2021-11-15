package time;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Date;

public class NewTime{
	public static void main(String[] args) throws ParseException
	{
		// 时间戳
		Timestamp now= new Timestamp(System.currentTimeMillis());
		System.out.println(now);

		//LocalDate直接获取当月天数
		LocalDate date = LocalDate.now();	//java8 生成当地时间-年月日
		System.out.println(date);
		int dayOfMonth = date.getDayOfMonth();

		//date表示的一周中的第几天
		Date today = new Date();//生成现在时间-年月日时分秒带星期；new Date()调用System.currentTimeMillis()[毫秒数]
		int day = today.getDay();

		//字符串格式化为date
		SimpleDateFormat sdf  =new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String dateStr = "2021-7-7 10:25:00";
		Date dateStrParse = sdf.parse(dateStr);

		//date需转化Calendar获取当月天数
		Calendar cal=Calendar.getInstance();//生成现在时间-年月日时分秒;cal为对象
		cal.setTime(dateStrParse);//重新赋值年月日时分秒
		int dateDay = cal.get(Calendar.DAY_OF_MONTH);

         //比较年月日时分秒
		boolean flag = today.getTime() >= dateStrParse.getTime();

        //将时分秒清空比较是否跨天(原时间过了一天),Date/Calendar类型需转化localDate类型
		cal.add(Calendar.DAY_OF_MONTH,1);     //利用Calendar 实现 Date日期+1天
		LocalDate localDate=cal.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
		// java.time.LocalDate -> java.sql.Date;localDate没getTime【long型】
		Date newDate=java.sql.Date.valueOf(localDate);
		boolean flag2 = today.getTime() >= newDate.getTime();

		//将时分秒清空比较是否跨天，localDate类型
		LocalDate localDate2=dateStrParse.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
		LocalDate datePlus = localDate2.plusDays(1);
		boolean flag3 = !date.isBefore(datePlus);//当前时间早，返回true；大于等于取反

	    //获取上月时间
		Calendar cal2=Calendar.getInstance();
		cal2.add(Calendar.MONTH, -1);
		int lastMonthMaxDay = cal2.getActualMaximum(Calendar.DAY_OF_MONTH);
		cal2.set(cal2.get(Calendar.YEAR), cal2.get(Calendar.MONTH), lastMonthMaxDay, 23, 59, 59);//重新赋值
		String endTime = sdf.format(cal2.getTime());//上月最后一天

		SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-01  00:00:00");
		String startTime = sdf2.format(cal.getTime()); // 上月第一天

        //时间比较
		Timestamp timestamp = new Timestamp(System.currentTimeMillis());
		long timestampLong = timestamp.getTime();
		long yesterdayLong = new Timestamp(System.currentTimeMillis()).getTime() - 24 * 60 * 60 * 1000L;
		if (yesterdayLong < timestampLong)
		{
			System.out.println("今天比昨天大");
			System.out.println("昨天"+ new Date(yesterdayLong));
			System.out.println("昨天"+ new Timestamp(yesterdayLong));
		}

		System.out.println("finish");

	}

	//相差天数
	public static String calcTwoDate(Date date ,Date date2){
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		Calendar calendar2 = Calendar.getInstance();
		calendar2.setTime(date2);

		long days  =(calendar.getTimeInMillis()-calendar2.getTimeInMillis())/(24*60*60*1000);
		return String.valueOf(Math.abs(days));
	}
}