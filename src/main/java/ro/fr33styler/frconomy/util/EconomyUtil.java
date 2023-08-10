package ro.fr33styler.frconomy.util;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Arrays;

public class EconomyUtil {

    private static final DecimalFormat WHOLE_FORMAT = new DecimalFormat("#,###");
    private static final DecimalFormat WHOLE_FORMAT_COMMAS = new DecimalFormat("#,###");
    private static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("#,##0.00");
    private static final DecimalFormat DECIMAL_FORMAT_COMMAS = new DecimalFormat("#,##0.00");

    private static final long[] MULTIPLIERS = { 1_000_000_000_000L, 1_000_000_000, 1_000_000, 1_000 };
    private static final char[] SUFFIXES = { 't', 'b', 'm', 'k' };

    static {
        DecimalFormatSymbols symbols = new DecimalFormatSymbols();
        symbols.setDecimalSeparator('.');
        symbols.setGroupingSeparator(',');
        WHOLE_FORMAT.setDecimalFormatSymbols(symbols);
        DECIMAL_FORMAT.setDecimalFormatSymbols(symbols);
        DECIMAL_FORMAT.setMinimumFractionDigits(0);

        symbols.setDecimalSeparator(',');
        symbols.setGroupingSeparator('.');
        WHOLE_FORMAT_COMMAS.setDecimalFormatSymbols(symbols);
        DECIMAL_FORMAT_COMMAS.setDecimalFormatSymbols(symbols);
        DECIMAL_FORMAT_COMMAS.setMinimumFractionDigits(0);
    }

    private EconomyUtil() {}

    public static DecimalFormat getWholeFormat() {
        return WHOLE_FORMAT;
    }

    public static DecimalFormat getWholeFormatCommas() {
        return WHOLE_FORMAT_COMMAS;
    }

    public static DecimalFormat getDecimalFormat() {
        return DECIMAL_FORMAT;
    }

    public static DecimalFormat getDecimalFormatCommas() {
        return DECIMAL_FORMAT_COMMAS;
    }

    public static String toShortScaleNotation(double balance, boolean commas) {
        for (int i = 0; i < MULTIPLIERS.length; i++) {
            long multiplier = MULTIPLIERS[i];
            if (balance >= multiplier) {
                if (commas) {
                    return DECIMAL_FORMAT_COMMAS.format(balance / multiplier) + SUFFIXES[i];
                }
                return DECIMAL_FORMAT.format(balance / multiplier) + SUFFIXES[i];
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
