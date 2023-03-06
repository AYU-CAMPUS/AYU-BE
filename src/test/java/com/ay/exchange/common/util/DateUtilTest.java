package com.ay.exchange.common.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

class DateUtilTest {

    @Test
    @DisplayName("정지 회원일 경우")
    void isSuspensionPeriodExpiredTest() throws ParseException {
        //given
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, 3);
        Date date = new Date(calendar.getTimeInMillis());
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");

        //when
        boolean isSuspended = DateUtil.isSuspensionPeriodExpired(simpleDateFormat.format(date));

        //then
        assertFalse(isSuspended);
    }
}