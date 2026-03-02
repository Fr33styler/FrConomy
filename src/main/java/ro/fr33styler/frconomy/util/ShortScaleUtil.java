package ro.fr33styler.frconomy.util;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;

public class ShortScaleUtil {

    private static final long[] MULTIPLIERS = { 1_000_000_000_000L, 1_000_000_000, 1_000_000, 1_000 };
    private static final BigDecimal[] MULTIPLIERS_BIG_DECIMAL = {
            BigDecimal.valueOf(1_000_000_000_000L),
            BigDecimal.valueOf(1_000_000_000),
            BigDecimal.valueOf(1_000_000),
            BigDecimal.valueOf(1_000)
    };

    private static final char[] SUFFIXES = { 't', 'b', 'm', 'k' };

    private ShortScaleUtil() {}

    public static String toShortScaleNotation(DecimalFormat decimalFormat, double balance) {
        for (int i = 0; i < MULTIPLIERS.length; i++) {
            long multiplier = MULTIPLIERS[i];
            if (balance >= multiplier) {
                return decimalFormat.format(balance / multiplier) + SUFFIXES[i];
            }
        }
        return String.valueOf(balance);
    }

    public static String toShortScaleNotation(DecimalFormat decimalFormat, BigDecimal balance) {
        for (int i = 0; i < MULTIPLIERS_BIG_DECIMAL.length; i++) {
            BigDecimal multiplier = MULTIPLIERS_BIG_DECIMAL[i];
            if (balance.compareTo(multiplier) >= 0) {
                return decimalFormat.format(balance.divide(multiplier, decimalFormat.getMaximumFractionDigits(), RoundingMode.HALF_UP)) + SUFFIXES[i];
            }
        }
        return String.valueOf(balance);
    }

    public static double fromShortScaleNotation(String balance) {
        for (int i = 0; i < MULTIPLIERS.length; i++) {
            if (Character.toLowerCase(balance.charAt(balance.length() - 1)) == SUFFIXES[i]) {
                return Double.parseDouble(balance.substring(0, balance.length() - 1)) * MULTIPLIERS[i];
            }
        }
        return Double.parseDouble(balance);
    }

}
