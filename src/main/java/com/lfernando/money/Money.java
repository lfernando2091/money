package com.lfernando.money;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Arrays;
import java.util.Locale;
import java.util.Objects;
import java.util.stream.StreamSupport;

import static java.math.RoundingMode.HALF_EVEN;

public final class Money implements Comparable<Money>, Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    public static final Money ZERO = new Money(0L);

    private static final int DEFAULT_SCALE = 2;
    private static final int ZERO_SCALE = 0;
    private static final RoundingMode DEFAULT_ROUNDING_MODE = HALF_EVEN;
    private static final NumberFormat DEFAULT_CURRENCY_FORMAT = NumberFormat.getCurrencyInstance(Locale.US);
    private static final NumberFormat DEFAULT_FORMAT = NumberFormat.getInstance(Locale.US);

    private final long cents;

    public static Money ofString(String string) {
        try {
            Number number = DEFAULT_FORMAT.parse(string);
            return ofDecimal(number.doubleValue());
        } catch (ParseException e) {
            return ZERO;
        }
    }

    public static Money ofCurrency(String string) {
        try {
            Number number = DEFAULT_CURRENCY_FORMAT.parse(string);
            return ofDecimal(number.doubleValue());
        } catch (ParseException e) {
            return ZERO;
        }
    }

    public static Money ofDecimal(float decimal) {
        return ofDecimal((double) decimal);
    }

    public static Money ofDecimal(double decimal) {
        return ofDecimal(BigDecimal.valueOf(decimal));
    }

    public static Money ofDecimal(BigDecimal decimal) {
        return create(decimal.setScale(DEFAULT_SCALE, DEFAULT_ROUNDING_MODE)
                .movePointRight(DEFAULT_SCALE).longValueExact());
    }

    public static Money ofCents(int cents) {
        return create(cents);
    }

    public static Money ofCents(long cents) {
        return create(cents);
    }

    private static Money create(long cents) {
        return cents == 0L ? ZERO : new Money(cents);
    }

    private Money(long cents) {
        this.cents = cents;
    }

    private BigDecimal toBigDecimalCents() {
        return BigDecimal.valueOf(this.cents, DEFAULT_SCALE);
    }

    private BigDecimal toBigDecimal() {
        return BigDecimal.valueOf(this.cents);
    }

    public long getCents() {
        return this.cents;
    }

    public Money plus(Money money) {
        return this.plus(money.getCents());
    }

    public Money plus(long centsToAdd) {
        if (centsToAdd == 0L) {
            return this;
        }
        return ofCents(Math.addExact(this.cents, centsToAdd));
    }

    public Money minus(Money money) {
        return this.minus(money.getCents());
    }

    public Money minus(long centsToSubtract) {
        if (centsToSubtract == 0L) {
            return this;
        }
        return centsToSubtract == Long.MIN_VALUE ? this.plus(Long.MAX_VALUE).plus(1L) : this.plus(-centsToSubtract);
    }

    public Money multipliedBy(Money money) {
        if (money.cents == 0L) {
            return ZERO;
        }
        return money.cents == 1L ? this : ofCents(this.toBigDecimal().multiply(money.toBigDecimalCents())
                                            .setScale(ZERO_SCALE, DEFAULT_ROUNDING_MODE).longValueExact());
    }

    public Money multipliedBy(long multiplicand) {
        if (multiplicand == 0L) {
            return ZERO;
        }
        return multiplicand == 1L ? this : ofCents(this.toBigDecimal().multiply(BigDecimal.valueOf(multiplicand))
                                                .setScale(ZERO_SCALE, DEFAULT_ROUNDING_MODE).longValueExact());
    }

    public Money multipliedBy(BigDecimal multiplicand) {
        if (BigDecimal.ZERO.equals(multiplicand)) {
            return ZERO;
        }
        return BigDecimal.ONE.equals(multiplicand) ? this : ofCents(this.toBigDecimal().multiply(multiplicand)
                                                        .setScale(ZERO_SCALE, DEFAULT_ROUNDING_MODE).longValueExact());
    }

    public Money dividedBy(Money money) {
        if (money.cents == 0L) {
            throw new ArithmeticException("Cannot divide by zero");
        }
        return money.cents == 100L ? this: ofCents(this.toBigDecimal().divide(money.toBigDecimalCents(), ZERO_SCALE, DEFAULT_ROUNDING_MODE)
                                                .longValueExact());
    }

    public Money dividedBy(long divisor) {
        if (divisor == 0L) {
            throw new ArithmeticException("Cannot divide by zero");
        }
        return divisor == 1L ? this : ofCents(this.toBigDecimal().divide(BigDecimal.valueOf(divisor), ZERO_SCALE, DEFAULT_ROUNDING_MODE)
                                                .longValueExact());
    }

    public Money dividedBy(BigDecimal divisor) {
        if (BigDecimal.ZERO.equals(divisor)) {
            throw new ArithmeticException("Cannot divide by zero");
        }
        return BigDecimal.ONE.equals(divisor) ? this : ofCents(this.toBigDecimal().divide(divisor, ZERO_SCALE, DEFAULT_ROUNDING_MODE)
                .longValueExact());
    }

    public static Money total(double... monies) {
        if (null == monies) {
            return ZERO;
        }
        return Arrays.stream(monies)
                .filter(Objects::nonNull)
                .mapToObj(Money::ofDecimal)
                .reduce(Money.ZERO, Money::plus);
    }

    public static Money total(Money... monies) {
        if (null == monies) {
            return ZERO;
        }
        return Arrays.stream(monies)
                .filter(Objects::nonNull)
                .reduce(Money.ZERO, Money::plus);
    }

    public static Money total(Iterable<Money> monies) {
        if (null == monies) {
            return ZERO;
        }
        return StreamSupport.stream(monies.spliterator(), false)
                .filter(Objects::nonNull)
                .reduce(Money.ZERO, Money::plus);
    }

    public boolean isZero() {
        return this.cents == 0L;
    }

    public boolean isPositive() {
        return this.cents > 0L;
    }

    public boolean isPositiveOrZero() {
        return this.cents >= 0L;
    }

    public boolean isNegative() {
        return this.cents < 0L;
    }

    public boolean isNegativeOrZero() {
        return this.cents <= 0L;
    }

    public Money negated() {
        if (isZero()) {
            return this;
        }
        return ofDecimal(this.toBigDecimalCents().negate());
    }

    public Money abs() {
        return (isNegative() ? negated() : this);
    }

    public boolean isEqual(Money other) {
        return compareTo(other) == 0;
    }

    public boolean isGreaterThan(Money other) {
        return compareTo(other) > 0;
    }

    public boolean isGreaterThanOrEqual(Money other) {
        return compareTo(other) >= 0;
    }

    public boolean isLessThan(Money other) {
        return compareTo(other) < 0;
    }

    public boolean isLessThanOrEqual(Money other) {
        return compareTo(other) <= 0;
    }

    public Float toFloat() {
        return this.toBigDecimalCents().floatValue();
    }

    public Double toDouble() {
        return this.toBigDecimalCents().doubleValue();
    }

    public String toCurrencyString() {
        return DEFAULT_CURRENCY_FORMAT.format(toDouble());
    }

    @Override
    public String toString() {
        return DEFAULT_FORMAT.format(toDouble());
    }

    @Override
    public int compareTo(Money otherMoney) {
        return Long.compare(this.cents, otherMoney.cents);
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        return other instanceof Money otherMoney && this.cents == otherMoney.cents;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(cents);
    }
}

