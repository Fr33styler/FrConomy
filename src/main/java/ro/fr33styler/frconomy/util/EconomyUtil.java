package ro.fr33styler.frconomy.util;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;

public class EconomyUtil {

    private static final DecimalFormat WHOLE_FORMAT = new DecimalFormat("#,###");
    private static final DecimalFormat WHOLE_FORMAT_COMMAS = new DecimalFormat("#,###");
    private static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("#,##0.00");
    private static final DecimalFormat DECIMAL_FORMAT_COMMAS = new DecimalFormat("#,##0.00");

    static {
        DecimalFormatSymbols symbols = new DecimalFormatSymbols();
        symbols.setDecimalSeparator('.');
        symbols.setGroupingSeparator(',');
        WHOLE_FORMAT.setDecimalFormatSymbols(symbols);
        DECIMAL_FORMAT.setDecimalFormatSymbols(symbols);

        symbols.setDecimalSeparator(',');
        symbols.setGroupingSeparator('.');
        WHOLE_FORMAT_COMMAS.setDecimalFormatSymbols(symbols);
        DECIMAL_FORMAT_COMMAS.setDecimalFormatSymbols(symbols);
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
}
