package com.rodrigocoelhoo.lifemanager.finances.model;

import java.util.EnumSet;

public enum WalletType {
    BANK,
    CASH;

    public static EnumSet<WalletType> all() {
        return EnumSet.allOf(WalletType.class);
    }
}