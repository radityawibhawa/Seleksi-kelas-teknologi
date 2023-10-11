package com.tujuhsembilan.logic;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.Locale;
public class CurrencyUtil {
    public static String formatRupiah(BigDecimal amount) {
        Locale localeID = new Locale("in", "ID");
        NumberFormat formatRupiah = NumberFormat.getCurrencyInstance(localeID);
        return formatRupiah.format(amount.doubleValue());
    }
}
