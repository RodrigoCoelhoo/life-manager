package com.rodrigocoelhoo.lifemanager.finances.model;

import lombok.Getter;

import java.util.EnumSet;

@Getter
public enum ExpenseCategory {
    SALARY(ExpenseType.INCOME),
    FREELANCE(ExpenseType.INCOME),
    SELL_INVESTMENT(ExpenseType.INCOME),
    PASSIVE_INCOME(ExpenseType.INCOME),

    HOUSING(ExpenseType.EXPENSE),
    FOOD(ExpenseType.EXPENSE),
    HEALTH(ExpenseType.EXPENSE),
    ENTERTAINMENT(ExpenseType.EXPENSE),
    TRANSPORTATION(ExpenseType.EXPENSE),
    EDUCATION(ExpenseType.EXPENSE),
    BUY_INVESTMENT(ExpenseType.EXPENSE),
    OTHER(ExpenseType.EXPENSE);

    private final ExpenseType type;

    ExpenseCategory(ExpenseType type) {
        this.type = type;
    }

    public static EnumSet<ExpenseCategory> all() {
        return EnumSet.allOf(ExpenseCategory.class);
    }
}