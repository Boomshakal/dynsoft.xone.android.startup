package dynsoft.xone.android.helper;

import java.util.Set;

import android.os.Bundle;
import dynsoft.xone.android.data.Parameters;

public class ParametersHelper {

    public Bundle ToBundle(Parameters parameters)
    {
        if (parameters != null) {
            Bundle bundle = new Bundle();
            Set<Object> keys = parameters.keySet();
            for (Object key : keys) {
                Object val = parameters.get(key);
                if (val != null) {
                    Class<?> clss = val.getClass();
                    if (clss == Boolean.class) {
                        bundle.putBoolean(key.toString(), (Boolean)val);
                    } else if (clss == boolean[].class) {
                        bundle.putBooleanArray(key.toString(), (boolean[])val);
                    } else if (clss == Byte.class) {
                        bundle.putByte(key.toString(), (Byte)val);
                    } else if (clss == byte[].class) {
                        bundle.putByteArray(key.toString(), (byte[])val);
                    } else if (clss == Character.class) {
                        bundle.putChar(key.toString(), (Character)val);
                    } else if (clss == char[].class) {
                        bundle.putCharArray(key.toString(), (char[])val);
                    }
                }
            }
            return bundle;
        }
        return null;
    }
}
