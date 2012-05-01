/**************************************************************
  Source	: DateUtil.java
  Date		: 2009/03/29 17:23:58
**************************************************************/
package info.one.ideal.milm.search.common.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.apache.commons.lang.time.DateUtils;

/**
 * 日付に関するユーティリティクラスです。
 * 
 * @author 林 瑞起
 */
public class DateUtil {

	/** 日付フォーマットのデフォルト */
	public static final String DEFAULT_DATE_FORMAT = "yyyy年MM月dd日 (EEE) HH:mm:ss z";
	
	/** 日付フォーマットの電子メール標準 */
	public static final String RFC2822_DATE_FORMAT = "EEE, dd MMM yyyy HH:mm:ss Z";

	/**
	 * 月の英語表記のリストを取得します。
	 * JanuaryからDecemberまで順番に格納されています。
	 * 
	 * @return 月の英語表記リスト
	 */
	public static List<String> monthStrList() {
		List<String> monthStrList = new ArrayList<String>();
		monthStrList.add("January");
		monthStrList.add("February");
		monthStrList.add("March");
		monthStrList.add("April");
		monthStrList.add("May");
		monthStrList.add("June");
		monthStrList.add("July");
		monthStrList.add("August");
		monthStrList.add("September");
		monthStrList.add("October");
		monthStrList.add("November");
		monthStrList.add("December");
		return monthStrList;
	}

	/**
	 * 指定した月から始まる月の英語表記のリストを取得します。
	 * 
	 * @param firstMonth 最初に格納される月（英語表記）
	 * @return 月の英語表記リスト
	 */
	public static List<String> monthStrList(String firstMonth) {
		List<String> monthStrList = DateUtil.monthStrList();
		int firstMonthIndex = -1;
		for (int i = 0; i < monthStrList.size(); i++) {
			if (firstMonth.equals(monthStrList.get(i))) {
				firstMonthIndex = i;
				break;
			}
		}
		if (firstMonthIndex == -1) {
			throw new IllegalArgumentException("[" + firstMonth + "]という月は存在しません。");
		}
		List<String> resultList = new ArrayList<String>();
		for (int i = firstMonthIndex; i < monthStrList.size(); i++) {
			resultList.add(monthStrList.get(i));
		}
		for (int i = 0; i < firstMonthIndex; i++) {
			resultList.add(monthStrList.get(i));
		}
		return resultList;
	}

	/**
	 * RFC2822の日付形式（EEE, dd MMM yyyy HH:mm:ss Z）の文字列を解析し、
	 * "yyyy年MM月dd日 (EEE) HH:mm:ss z" の形式に変換します。
	 * 
	 * @param rfc2822DateStr RFC2822形式の日付文字列 
	 * @return デフォルト形式の日付文字列
	 * @throws ParseException 
	 */
	public static String reformRFC2822(String rfc2822DateStr) throws ParseException {
		Date date = new SimpleDateFormat(RFC2822_DATE_FORMAT, Locale.US).parse(rfc2822DateStr);
		return new SimpleDateFormat(DEFAULT_DATE_FORMAT, Locale.JAPAN).format(date);
	}
	
	/**
	 * RFC2822の日付形式（EEE, dd MMM yyyy HH:mm:ss Z）の文字列を解析し、
	 * 日付型に変換します。
	 * 
	 * @param rfc2822DateStr RFC2822形式の日付文字列 
	 * @return 日付
	 * @throws ParseException 
	 */
	public static Date convertRFC2822ToDate(String rfc2822DateStr) throws ParseException {
		return new SimpleDateFormat(RFC2822_DATE_FORMAT, Locale.US).parse(rfc2822DateStr);
	}
	
    /**
     * デフォルト形式の文字列を解析し、
     * 日付型に変換します。
     * 
     * @param rfc2822DateStr RFC2822形式の日付文字列 
     * @return 日付
     * @throws ParseException 
     */
    public static Date convertDefaultToDate(String defaultDateStr) throws ParseException {
        return new SimpleDateFormat(DEFAULT_DATE_FORMAT, Locale.JAPAN).parse(defaultDateStr);
    }	

	/**
	 * 日付を
	 * "yyyy年MM月dd日 (EEE) HH:mm:ss z" 形式の文字列に変換します。
	 * 
	 * @param 日付 
	 * @return デフォルト形式の日付文字列
	 * @throws ParseException 
	 */
	public static String convertDate2Str(Date date) {
		return new SimpleDateFormat(DEFAULT_DATE_FORMAT, Locale.JAPAN).format(date);
	}
	
	/**
	 * 2つの日付が同じ年月のかを判断します。
	 * 
	 * @param date1 日付
	 * @param date2 日付
	 * @return 同じなら true
	 */
    public static boolean isSameYearMonth(Date date1, Date date2) {
        Date date = DateUtils.truncate(date1, Calendar.MONTH);
        Date otherDate = DateUtils.truncate(date2, Calendar.MONTH);
        return DateUtils.isSameDay(date, otherDate);
    }
    
    /**
     * 2つの日付が同じ年月のかを判断します。
     * 
     * @param date1 日付
     * @param date2 日付
     * @return 同じなら true
     */
    public static boolean isSameYearMonth(long date1, long date2) {
        return DateUtil.isSameYearMonth(new Date(date1), new Date(date2));
    }    
}
