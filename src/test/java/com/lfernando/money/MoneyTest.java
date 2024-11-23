package com.lfernando.money;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static java.lang.Boolean.TRUE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class MoneyTest {

    @Test
    void verify_float_to_cents_conversion() {
        Money money = Money.ofDecimal(30f);
        assertEquals(3000, money.getCents());

        money = Money.ofDecimal(3f);
        assertEquals(300, money.getCents());

        money = Money.ofDecimal(0.3f);
        assertEquals(30, money.getCents());

        money = Money.ofDecimal(0.03f);
        assertEquals(3, money.getCents());

        money = Money.ofDecimal(0.003f);
        assertEquals(0, money.getCents());

        money = Money.ofDecimal(0.006f);
        assertEquals(1, money.getCents());

        money = Money.ofDecimal(0.005f);
        assertEquals(0, money.getCents());
    }

    @Test
    void verify_double_to_cents_conversion() {
        Money money = Money.ofDecimal(30);
        assertEquals(3000, money.getCents());

        money = Money.ofDecimal(3);
        assertEquals(300, money.getCents());

        money = Money.ofDecimal(0.3);
        assertEquals(30, money.getCents());

        money = Money.ofDecimal(0.03);
        assertEquals(3, money.getCents());

        money = Money.ofDecimal(0.003);
        assertEquals(0, money.getCents());

        money = Money.ofDecimal(0.005);
        assertEquals(1, money.getCents());
    }

    @Test
    void verify_cents_to_float_conversion() {
        Money money = Money.ofCents(300000);
        assertEquals(3000f, money.toFloat());

        money = Money.ofCents(3000);
        assertEquals(30f, money.toFloat());

        money = Money.ofCents(300);
        assertEquals(3f, money.toFloat());

        money = Money.ofCents(30);
        assertEquals(0.3f, money.toFloat());

        money = Money.ofCents(3);
        assertEquals(0.03f, money.toFloat());

        money = Money.ofCents(0);
        assertEquals(0f, money.toFloat());
    }

    @Test
    void verify_value_string_conversion() {
        Money money = Money.ofCents(300000);
        assertEquals("3,000", money.toString());
        assertEquals("$3,000.00", money.toCurrencyString());

        Money moneyDecimals = Money.ofCents(300006);
        assertEquals("3,000.06", moneyDecimals.toString());
        assertEquals("$3,000.06", moneyDecimals.toCurrencyString());

        Money m2 = Money.ofString(money.toString());
        assertEquals(money.getCents(), m2.getCents());

        Money m3 = Money.ofCurrency(money.toCurrencyString());
        assertEquals(money.getCents(), m3.getCents());
    }

    @Test
    void verify_sum() {
        Money money1 = Money.ofDecimal(30.325f); // 30.33
        Money money2 = Money.ofDecimal(25.413f); // 25.41
        Money money3 = money1.plus(money2);
        assertEquals(5574, money3.getCents());

        money1 = Money.ofDecimal(30.32589);
        money2 = Money.ofDecimal(25.41356);
        money3 = money1.plus(money2);
        assertEquals(5574, money3.getCents());

        money1 = Money.ofDecimal(BigDecimal.valueOf(30.32589));
        money2 = Money.ofDecimal(BigDecimal.valueOf(25.41356));
        money3 = money1.plus(money2);
        assertEquals(5574, money3.getCents());

        money1 = Money.ofCents(1);
        money2 = Money.ofCents(2);
        money3 = money1.plus(money2);
        assertEquals(3, money3.getCents());
    }

    @Test
    void verify_minus() {
        Money money1 = Money.ofDecimal(30.325f); // 30.33
        Money money2 = Money.ofDecimal(25.413f); // 25.41
        Money money3 = money1.minus(money2);
        assertEquals(492, money3.getCents());

        money1 = Money.ofDecimal(30.32589);
        money2 = Money.ofDecimal(25.41356);
        money3 = money1.minus(money2);
        assertEquals(492, money3.getCents());

        money1 = Money.ofDecimal(BigDecimal.valueOf(30.32589));
        money2 = Money.ofDecimal(BigDecimal.valueOf(25.41356));
        money3 = money1.minus(money2);
        assertEquals(492, money3.getCents());

        money1 = Money.ofCents(3);
        money2 = Money.ofCents(2);
        money3 = money1.minus(money2);
        assertEquals(1, money3.getCents());

        money1 = Money.ofCents(Long.MIN_VALUE);
        assertEquals(0, money1.minus(Long.MIN_VALUE).getCents());

        var moneyError = Money.ofCents(Long.MAX_VALUE);
        assertThrows(Exception.class, () -> moneyError.minus(Long.MIN_VALUE));
    }

    @Test
    void verify_multiply() {
        Money money1 = Money.ofDecimal(30.325f); // 30.33
        Money money2 = Money.ofDecimal(25.413f); // 25.41
        Money money3 = money1.multipliedBy(money2);
        assertEquals(77069, money3.getCents());

        money1 = Money.ofDecimal(30.32589);
        money2 = Money.ofDecimal(25.41356);
        money3 = money1.multipliedBy(money2);
        assertEquals(77069, money3.getCents());

        money1 = Money.ofDecimal(BigDecimal.valueOf(30.32589));
        money2 = Money.ofDecimal(BigDecimal.valueOf(25.41356));
        money3 = money1.multipliedBy(money2);
        assertEquals(77069, money3.getCents());

        money1 = Money.ofCents(3);
        money3 = money1.multipliedBy(2);
        assertEquals(6, money3.getCents());
    }

    @Test
    void verify_divide() {
        Money money1 = Money.ofDecimal(30.325f); // 30.33
        Money money2 = Money.ofDecimal(25.413f); // 25.41
        Money money3 = money1.dividedBy(money2);
        assertEquals(119, money3.getCents());

        money1 = Money.ofDecimal(30.32589);
        money2 = Money.ofDecimal(25.41356);
        money3 = money1.dividedBy(money2);
        assertEquals(119, money3.getCents());

        money1 = Money.ofDecimal(BigDecimal.valueOf(30.32589));
        money2 = Money.ofDecimal(BigDecimal.valueOf(25.41356));
        money3 = money1.dividedBy(money2);
        assertEquals(119, money3.getCents());

        money1 = Money.ofCents(300);
        money3 = money1.dividedBy(2);
        assertEquals(150, money3.getCents());
    }

    @Test
    void verify_sum_list() {
        Money money = Money.total(Money.ofDecimal(3.1416));
        assertEquals(314, money.getCents());

        money = Money.total(Money.ofDecimal(3.1416), Money.ofDecimal(2.5), Money.ofCents(90));
        assertEquals(654, money.getCents());

        money = Money.total(3.1416, 2.5, 90);
        assertEquals(9564, money.getCents());

        money = Money.total(List.of(Money.ofDecimal(3.1416)));
        assertEquals(314, money.getCents());

        money = Money.total(List.of(Money.ofDecimal(3.1416), Money.ofDecimal(2.5), Money.ofCents(90)));
        assertEquals(654, money.getCents());
    }

    @Test
    void verify_utils_methods() {
        Money money = Money.ofDecimal(0f);
        assertEquals(TRUE, money.isZero());
        assertEquals(TRUE, money.isPositiveOrZero());
        assertEquals(TRUE, money.isNegativeOrZero());

        money = Money.ofDecimal(1f);
        assertEquals(TRUE, money.isPositive());
        assertEquals(TRUE, money.isPositiveOrZero());

        money = Money.ofDecimal(-1f);
        assertEquals(TRUE, money.isNegative());
        assertEquals(TRUE, money.isNegativeOrZero());

        money = Money.ofDecimal(-1f);
        assertEquals(100, money.negated().getCents());

        money = money.abs();
        assertEquals(100, money.getCents());
    }

    @Test
    void verify_compareTo_methods() {
        Money money = Money.ofDecimal(100.445);
        assertEquals(TRUE, money.isEqual(Money.ofCents(10045)));

        assertEquals(TRUE, money.isGreaterThan(Money.ofCents(10044)));
        assertEquals(TRUE, money.isGreaterThanOrEqual(Money.ofCents(10044)));
        assertEquals(TRUE, money.isGreaterThanOrEqual(Money.ofCents(10045)));

        assertEquals(TRUE, money.isLessThan(Money.ofCents(10046)));
        assertEquals(TRUE, money.isLessThanOrEqual(Money.ofCents(10046)));
        assertEquals(TRUE, money.isLessThanOrEqual(Money.ofCents(10045)));
    }
}
