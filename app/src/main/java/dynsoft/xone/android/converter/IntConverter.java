package dynsoft.xone.android.converter;

public class IntConverter extends TypeConverter {

	@Override
	public boolean CanConvertFrom(Class<?> clazz) {
		return clazz == String.class;
	}

	@Override
	public Object ConvertFrom(Object value) {
	    if (value == null) return null;
	    String str = value.toString();
	    if (str.startsWith("0x") == false){
	        return Integer.parseInt(str);
	    } else  {
	    	str = str.substring(2, str.length());
	        return Integer.parseInt(str, 16);
	    }
	}
}
