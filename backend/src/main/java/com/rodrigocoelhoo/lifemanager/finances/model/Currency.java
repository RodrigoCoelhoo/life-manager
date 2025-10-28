package com.rodrigocoelhoo.lifemanager.finances.model;

import lombok.Getter;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.EnumSet;

@Getter
public enum Currency {
    EUR(BigDecimal.valueOf(1.0), "€"),
    USD(BigDecimal.valueOf(0.92), "$"),
    GBP(BigDecimal.valueOf(1.14), "£"),
    BRL(BigDecimal.valueOf(0.18), "R$"),
    JPY(BigDecimal.valueOf(0.0066), "¥"),
    AUD(BigDecimal.valueOf(0.62), "A$"),
    CAD(BigDecimal.valueOf(0.68), "C$"),
    CHF(BigDecimal.valueOf(1.02), "CHF"),
    CNY(BigDecimal.valueOf(0.13), "¥"),
    SEK(BigDecimal.valueOf(0.087), "kr"),
    NZD(BigDecimal.valueOf(0.56), "NZ$"),
    MXN(BigDecimal.valueOf(0.052), "$"),
    SGD(BigDecimal.valueOf(0.67), "S$"),
    HKD(BigDecimal.valueOf(0.12), "HK$"),
    NOK(BigDecimal.valueOf(0.088), "kr"),
    KRW(BigDecimal.valueOf(0.00076), "₩"),
    TRY(BigDecimal.valueOf(0.034), "₺"),
    INR(BigDecimal.valueOf(0.011), "₹"),
    RUB(BigDecimal.valueOf(0.012), "₽"),
    ZAR(BigDecimal.valueOf(0.055), "R");

    private final BigDecimal rateToEUR;
    private final String symbol;

    Currency(BigDecimal rateToEUR, String symbol) {
        this.rateToEUR = rateToEUR;
        this.symbol = symbol;
    }

    public BigDecimal convertTo(BigDecimal amount, Currency target) {
        BigDecimal amountInEUR = amount.multiply(this.rateToEUR);
        return amountInEUR.divide(target.rateToEUR, 10, RoundingMode.HALF_UP);
    }

    public String format(BigDecimal amount) {
        return symbol + amount.setScale(2, RoundingMode.HALF_UP).toPlainString();
    }

    public static EnumSet<Currency> all() {
        return EnumSet.allOf(Currency.class);
    }
}
