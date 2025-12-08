package com.rodrigocoelhoo.lifemanager.finances.model;

import java.math.BigDecimal;
import java.util.EnumSet;

public enum ExpenseType {
    EXPENSE,
    INCOME;

    public static EnumSet<ExpenseType> all() {
        return EnumSet.allOf(ExpenseType.class);
    }

    public static BigDecimal normalize(ExpenseType type, BigDecimal amount) {
        return type == EXPENSE ? amount.negate() : amount;
    }
}
