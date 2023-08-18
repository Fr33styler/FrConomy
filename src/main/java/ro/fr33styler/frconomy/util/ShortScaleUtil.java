package ro.fr33styler.frconomy.util;

import java.text.DecimalFormat;

public class ShortScaleUtil {

    private static final long[] MULTIPLIERS = { 1_000_000_000_000L, 1_000_000_000, 1_000_000, 1_000 };
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

    public static double fromShortScaleNotation(String balance) {
        for (int i = 0; i < MULTIPLIERS.length; i++) {
            if (Character.toLowerCase(balance.charAt(balance.length() - 1)) == SUFFIXES[i]) {
                return Double.parseDouble(balance.substring(0, balance.length() - 1)) * MULTIPLIERS[i];
            }
        }
        return Double.parseDouble(balance);
    }

}
