package dynsoft.xone.android.core;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import android.content.*;

public class Hooklet {

	public Hooklet(Context context, Element element, String event, String handler, String ownerType, String script)
	{
		this.Context = context;
		this.Element = element;
		this.Event = event;
		this.Handler = handler;
		this.OwnerType = ownerType;
		this.Script = script;
	}
	
	public Context Context;
	
	public Element Element;
	
	public String Event;
	
	public String Handler;
	
	public String OwnerType;
	
	public String Script;
	
	public void Attach()
	{
		if (this.Handler != null && this.Handler.length() > 0 && this.OwnerType != null && this.OwnerType.length() > 0) {
			try {
				Class<?> clss = Class.forName(this.OwnerType);
				Object handler = Proxy.newProxyInstance(clss.getClassLoader(), new Class<?>[]{clss}, new InvocationHandler() {

					@Override
					public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

						String method_name = method.getName();
		                if (method_name.equals(Hooklet.this.Handler)) {
//		                	ScriptArgs hookArgs = new ScriptArgs();
//		                	hookArgs.App = App.Current;
//		                	hookArgs.Context = Hooklet.this.Context;
//		                	hookArgs.Pane = Hooklet.this.Element.PaneObject;
//		                	hookArgs.Element = Hooklet.this.Element;
//		                	hookArgs.Evaluator = null;
//		                	hookArgs.Hooklet = Hooklet.this;
//		                	hookArgs.Args = args;
		                	//App.Current.callScript(Hooklet.this.Script, hookArgs);
		                }

						return null;
					}
				});
				
				if (handler != null) {
					try {
						
						Method method = this.Element.Object.getClass().getMethod(this.Event, clss);
						method.invoke(this.Element.Object, handler);
					} catch (NoSuchMethodException e) {
					} catch (IllegalArgumentException e) {
					} catch (IllegalAccessException e) {
					} catch (InvocationTargetException e) {
					}
				}
			} catch (ClassNotFoundException e) {
			}
		}
	}
}
