package com.rodrigocoelhoo.lifemanager.finances.model;

import java.util.EnumSet;

public enum TransactionRecurrence {
    DAILY,
    WEEKLY,
    MONTHLY,
    YEARLY;


    public static EnumSet<TransactionRecurrence> all() {
        return EnumSet.allOf(TransactionRecurrence.class);
    }
}