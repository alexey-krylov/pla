package org.nthdimenzion.presentation;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.joda.money.CurrencyUnit;
import org.joda.money.Money;
import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.math.BigDecimal;
import java.util.Locale;

import static org.nthdimenzion.common.AppConstants.DEFAULT_CURRENCY;

/**
 * Author: Nthdimenzion
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class AppUtils {

    
    public static String StripCurrencyUnit(String money){
        return money.substring(3);
    }

    public static String prependCurrencyUnit(String money){
        return CurrencyUnit.getInstance(Locale.getDefault()).getCurrencyCode() + money;
    }

    public static DateTimeFormatter getDateTimeFormat(){
        String patternEnglish = DateTimeFormat.patternForStyle("S-", Locale.getDefault());
        patternEnglish = patternEnglish.replace("yy", "yyyy");
        return DateTimeFormat.forPattern(patternEnglish);
    }

    public static LocalDate toLocalDate(String date){
        return getDateTimeFormat().parseLocalDate(date);
    }

    public static Money toMoney(String money){
        return Money.of(DEFAULT_CURRENCY, new BigDecimal(money));
    }

}

