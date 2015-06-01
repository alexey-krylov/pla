package org.nthdimenzion.presentation;

import org.joda.money.CurrencyUnit;
import org.joda.money.Money;
import org.joda.time.Days;
import org.joda.time.LocalDate;
import org.joda.time.Years;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.nthdimenzion.common.AppConstants;
import org.springframework.security.core.userdetails.UserDetails;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.Locale;

import static org.nthdimenzion.common.AppConstants.DEFAULT_CURRENCY;

/**
 * Author: Nthdimenzion
 */
public class AppUtils {

    private AppUtils() {
    }

    public static String stripCurrencyUnit(String money) {
        return money.substring(3);
    }

    public static String prependCurrencyUnit(String money) {
        return CurrencyUnit.getInstance(Locale.getDefault()).getCurrencyCode() + money;
    }

    public static DateTimeFormatter getDateTimeFormat() {
        String patternEnglish = AppConstants.DD_MM_YYY_FORMAT;
        return DateTimeFormat.forPattern(patternEnglish);
    }

    public static LocalDate toLocalDate(String date) {
        return getDateTimeFormat().parseLocalDate(date);
    }

    public static String toString(LocalDate date) {
        if (date == null) {
            return "";
        }
        return date.toString(AppConstants.DD_MM_YYY_FORMAT);
    }

    public static Money toMoney(String money) {
        return Money.of(DEFAULT_CURRENCY, new BigDecimal(money));
    }


    public static UserDetails getLoggedInUserDetail(HttpServletRequest request) {
        UserDetails userDetails = (UserDetails) request.getSession().getAttribute(AppConstants.LOGGED_IN_USER);
        return userDetails;
    }

    public static Integer getAge(LocalDate dateOfBirth) {
        Years age = Years.yearsBetween(dateOfBirth, LocalDate.now());
        return age.getYears();
    }

    public static Integer getIntervalInDays(LocalDate date) {
        if (date == null) {
            return null;
        }
        Days interval = Days.daysBetween(date, LocalDate.now());
        return interval.getDays();
    }

    public static void main(String[] args) {
        System.out.println(getAge(new LocalDate(1985, 5, 25)));
    }
}

