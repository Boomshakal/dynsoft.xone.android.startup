package dynsoft.xone.android.converter;

public class LongConverter extends TypeConverter {
	
	@Override
	public boolean CanConvertFrom(Class<?> clazz) {
		return clazz == String.class;
	}

	@Override
	public Object ConvertFrom(Object value) {
	    if (value == null) return null;
        if (value.toString().length() == 0) return 0l;
        return Long.valueOf(value.toString());
	}
}
