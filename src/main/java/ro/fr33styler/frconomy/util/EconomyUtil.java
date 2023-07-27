package ro.fr33styler.frconomy.util;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;

public class EconomyUtil {

    private static final DecimalFormat WHOLE_FORMAT = new DecimalFormat("#,###");
    private static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("#,##0.00");

    static {
        DecimalFormatSymbols wholeSymbols = new DecimalFormatSymbols();
        wholeSymbols.setGroupingSeparator(',');
        WHOLE_FORMAT.setDecimalFormatSymbols(wholeSymbols);

        DecimalFormatSymbols decimalSymbols = new DecimalFormatSymbols();
        decimalSymbols.setDecimalSeparator('.');
        decimalSymbols.setGroupingSeparator(',');
        DECIMAL_FORMAT.setDecimalFormatSymbols(decimalSymbols);
    }

    private EconomyUtil() {}

    public static DecimalFormat getWholeFormat() {
        return WHOLE_FORMAT;
    }

    public static DecimalFormat getDecimalFormat() {
        return DECIMAL_FORMAT;
    }

}
