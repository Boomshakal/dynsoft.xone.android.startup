package dynsoft.xone.android.converter;

public abstract class TypeConverter {

	public boolean CanConvertFrom(Class<?> clazz)
	{
		return false;
	}
	
	public abstract Object ConvertFrom(Object value);
}
