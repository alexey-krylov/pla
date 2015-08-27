package org.nthdimenzion.presentation;

import com.google.common.collect.Lists;
import org.apache.commons.lang.StringUtils;
import org.joda.money.CurrencyUnit;
import org.joda.money.Money;
import org.joda.time.DateTime;
import org.joda.time.Days;
import org.joda.time.LocalDate;
import org.joda.time.Years;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.nthdimenzion.common.AppConstants;
import org.springframework.security.core.userdetails.UserDetails;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
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

    public static String toString(DateTime date) {
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

    public static String replaceSpecialCharactersIn(String textToBeReplaced) {
        String[] searchStringArray = ":;\"'?/.,{[}]\\-=()_ ".split("(?!^)");
        List<String> listOfEmptyString = Lists.newArrayList();
        Lists.newArrayList(searchStringArray).forEach(replacementCharacters ->
                listOfEmptyString.add(""));
        String[] replacementList = Arrays.copyOf(listOfEmptyString.toArray(), listOfEmptyString.toArray().length, String[].class);
        return StringUtils.replaceEachRepeatedly(textToBeReplaced, searchStringArray, replacementList);
    }

    public static Integer getAgeOnNextBirthDate(LocalDate dateOfBirth) {
        if (dateOfBirth == null) {
            return 0;
        }
        Years age = Years.yearsBetween(dateOfBirth, LocalDate.now());
        int ageOnNextBirthDate = age.getYears() + 1;
        return ageOnNextBirthDate;
    }

    public static Integer getIntervalInDays(LocalDate date) {
        if (date == null) {
            return null;
        }
        Days interval = Days.daysBetween(date, LocalDate.now());
        return interval.getDays();
    }

    public static Integer getIntervalInDays(DateTime date) {
        if (date == null) {
            return null;
        }
        Days interval = Days.daysBetween(date, DateTime.now());
        return interval.getDays();
    }

    public static void main(String[] args) {
        System.out.println(getAgeOnNextBirthDate(new LocalDate(1985, 11, 05)));
    }
}

