package dynsoft.xone.android.converter;

import android.graphics.Color;

public class ColorConverter extends TypeConverter {

	@Override
	public boolean CanConvertFrom(Class<?> clazz) {
		return clazz == String.class;
	}

	@Override
	public Object ConvertFrom(Object value) {
	    if (value == null) return null;
	    String str = value.toString();
	    return Color.parseColor(str);
	}
}
