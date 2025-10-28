package com.rodrigocoelhoo.lifemanager.finances.model;

import java.util.EnumSet;

public enum ExpenseType {
    EXPENSE,
    INCOME;

    public static EnumSet<ExpenseType> all() {
        return EnumSet.allOf(ExpenseType.class);
    }
}
