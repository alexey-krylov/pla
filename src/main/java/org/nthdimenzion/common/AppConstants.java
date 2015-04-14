package org.nthdimenzion.common;

import org.joda.money.CurrencyUnit;
import org.joda.money.format.MoneyAmountStyle;
import org.joda.money.format.MoneyFormatter;
import org.joda.money.format.MoneyFormatterBuilder;

import java.util.Locale;

/**
 * Author: Nthdimenzion
 */

public interface AppConstants {

    /**
     * Not to used !!
     */
    CurrencyUnit DEFAULT_CURRENCY = CurrencyUnit.of(Locale.getDefault());

    MoneyFormatter MONEY_FORMATTER = new MoneyFormatterBuilder().appendCurrencyCode().appendAmount(MoneyAmountStyle.ASCII_DECIMAL_POINT_GROUP3_COMMA).toFormatter();

    Boolean SUCCESS = Boolean.TRUE;

    Boolean FAILURE = Boolean.FALSE;

    String LOGGED_IN_USER = "LOGGED_IN_USER";

    String TEAM_LEADER_DESIGNATION = "TEAM_LEADER";

    String BRANCH_MANAGER_DESIGNATION = "BRANCH_MANAGER";

    String REGIONAL_MANAGER_DESIGNATION = "REGIONAL_MANAGER";

    String BRANCH_BDE_DESIGNATION = "BRANCH_BDE";

    String PREMIUM_CELL_HEADER_NAME = "Premium";

    String ERROR_CELL_HEADER_NAME = "Error Message";
}
