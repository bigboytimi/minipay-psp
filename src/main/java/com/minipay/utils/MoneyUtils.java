package com.minipay.utils;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class MoneyUtils {
    private static final int SCALE = 2;
    private static final RoundingMode ROUNDING_MODE = RoundingMode.HALF_UP;

    public static BigDecimal scale(BigDecimal value) {
        return value != null ? value.setScale(SCALE, ROUNDING_MODE) : null;
    }
}
