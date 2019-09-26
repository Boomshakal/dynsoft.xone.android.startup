package dynsoft.xone.android.core;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import android.content.*;

import dynsoft.xone.android.converter.TypeConverter;
import dynsoft.xone.android.evaluator.*;
import dynsoft.xone.android.helper.ExpressionHelper;

public class Evaluation {
	
	public Evaluation(Element element, String property, String evaluator, String connection, String value)
	{
		this.Element = element;
		this.Property = property;
		this.Evaluator = evaluator;
		this.Connection = connection;
		this.Value = value;
		
	}
	
	public Context Context;

	public Element Element;
	
	public String Property;
	
	public String Evaluator;
	
	public String Value;

	public String Connection;
	
	public String PropertyName;
	
	public Class<?> PropertyClass;
	
	public void Evaluate()
    {
        String[] arrProperty = this.Property.split(",");
        if (arrProperty == null || arrProperty.length != 2) return;
        
        this.PropertyName = arrProperty[0];
        String propertyClass = arrProperty[1];
        
        this.PropertyClass = App.Current.ClassManager.getType(propertyClass);
        Class<?> objClass = this.Element.Object.getClass();
        
        Evaluator evaluator = App.Current.EvaluatorManager.GetEvaluator(this.Evaluator);
        if (evaluator != null) {
        	evaluator.Evaluate(this);
        	
        }
        
//        Method method;
//        try {
//            method = objClass.getMethod(propertyName, propClass);
//            if (method != null) {
//                Object val = null;
//                if (this.Evaluator.equals(EvaluateTypes.Fixed)) {
//                    val = this.EvaluateFixedValue(propClass);
//                } else if (this.Evaluator.equals(EvaluateTypes.StringResource)) {
//                    val = App.Current.ResourceManager.getString(this.Value);
//                } else if (this.Evaluator.equals(EvaluateTypes.ImageResource)) {
//                    val = App.Current.ResourceManager.getImage(this.Value);
//                }
//                
//                method.invoke(this.Element.Object, val);
//            }
//        } catch (NoSuchMethodException e1) {
//        } catch (IllegalArgumentException e) {
//        } catch (IllegalAccessException e) {
//        } catch (InvocationTargetException e) {
//        }
    }
	
	public void SetValue(String propertyName, Object value)
	{
		 Method method;
	        try {
	            method = this.Element.Object.getClass().getMethod("set" + propertyName, this.PropertyClass);
	            if (method != null) {
	                method.invoke(this.Element.Object, value);
	            }
	        } catch (NoSuchMethodException e1) {
	        } catch (IllegalArgumentException e) {
	        } catch (IllegalAccessException e) {
	        } catch (InvocationTargetException e) {
	        }
	}
	
//	private Object EvaluateFixedValue(Class<?> propertyClass)
//	{
//        if (propertyClass == java.lang.CharSequence.class) {
//            return this.Value;
//        } else if (propertyClass == java.lang.String.class) {
//            return this.Value;
//        } else {
//            TypeConverter converter = App.Current.ClassManager.getTypeConverter(propertyClass);
//            if (converter != null) {
//                if (converter.CanConvertFrom(String.class)) {
//                    return converter.ConvertFrom(this.Value);
//                }
//            }
//        }
//        
//        return null;
//	}
}
