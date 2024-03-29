package com.ay.exchange.common.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;

public class DateUtil {

    public static String getCurrentDate() {
        return LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
    }

    public static String getAvailableDate() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, -3);
        Date date = new Date(calendar.getTimeInMillis());
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");

        return simpleDateFormat.format(date);
    }

    public static boolean isSuspensionPeriodExpired(String date) throws ParseException {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

        Date suspendedDate = dateFormat.parse(date);
        Date nowDate = dateFormat.parse(DateUtil.getCurrentDate());

        if (suspendedDate.before(nowDate)) {
            return true;
        }
        return false;
    }
}