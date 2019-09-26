package dynsoft.xone.android.helper;

import java.math.BigDecimal;

public class ConvertHelper {

    public static BigDecimal StringToBigDecimal(String val, BigDecimal defaultValue)
    {
        if (val == null) return defaultValue;
        if (val.length() == 0) return defaultValue;
        
        try {
            return new BigDecimal(val);
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }
}
