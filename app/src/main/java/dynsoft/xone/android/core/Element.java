package dynsoft.xone.android.core;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

public class Element {
	
    public final static String LOG_TAG = "Element";
	public final static int TagIndex = 268099;
	public final static Element Empty = new Element();
	
	public ElementCollection Children;
	
	public Map<String, Evaluation> Evaluators;
	
	public Map<String, Hooklet> Hooklets;

	public Pane PaneObject;
	
	public String Pane;
	
	public String Pack;
	
	public String Code;
	
	public String Parent;
	
	public String Perm;
	
	public String ClassName;
	
	public int Ordinal;
	
	public Object Object;
	
	public Element()
	{
		this.Children = new ElementCollection();
		this.Evaluators = new LinkedHashMap<String, Evaluation>();
		this.Hooklets = new LinkedHashMap<String, Hooklet>();
	}
	
	public void Initialize()
	{
	    this.PaneObject.onElementInitializing(this);
	    
		if (this.Object == null) {
		    Object element = App.Current.ClassManager.createObject(this.ClassName, Context.class, this.PaneObject.getContext());
		    if (element instanceof View) {
		        View view = (View)element;
		        view.setTag(R.id.pane, this.PaneObject);
                view.setTag(R.id.element, this);
		    }
            this.Object = element;
            if(this.Object == null) {
                Log.e(LOG_TAG, "Element object is null. Pane:" + this.Pane + ", Element:" + this.Code); 
            }
		}
		
		for (Element child : this.Children) {
			child.Initialize();
		}
		
		this.PaneObject.onElementInitialized(this);
	}
	
	public void BuildItems()
	{
	    this.PaneObject.onElementBuilding(this);
	    
	    for (Element child : this.Children) {
	        if (child.Object != null) {
	            if (this.Object instanceof IParent) {
	                IParent parent = (IParent)this.Object;
	                parent.addChild(child);
	            } else if (child.Object instanceof IChild) {
	                IChild c = (IChild)child.Object;
	                if (c != null) {
	                    c.attachToParent(this);
	                } else {
	                    Log.e(LOG_TAG, "Element object is not IChild. Pane:" + this.Pane + ", Element:" + child.Code); 
	                }
	            } else if (this.Object instanceof Activity) {
	                Activity activity = (Activity)this.Object;
	                View view = (View)child.Object;
	                if (view != null) {
	                    activity.setContentView(view);
	                } else {
                        Log.e(LOG_TAG, "Element object is not a View. Pane:" + this.Pane + ", Element:" + child.Code); 
                    }
	            } else if (this.Object instanceof ViewGroup) {
	                ViewGroup parent = (ViewGroup)this.Object;
	                View subView = (View)child.Object;
	                if (subView != null) {
	                    parent.addView(subView);
	                } else {
                        Log.e(LOG_TAG, "Element object is not a View. Pane:" + this.Pane + ", Element:" + child.Code); 
                    }
	            }
	        } else {
                Log.e(LOG_TAG, "Element object is null on Build. Pane:" + this.Pane + ", Element:" + child.Code); 
            }
        }
	    
		for (Element child : this.Children) {
			child.BuildItems();
		}
		
		this.PaneObject.onElementBuilded(this);
	}
	
	public void Prepare()
	{
	    this.PaneObject.onElementPreparing(this);
	    
		if (this.Object != null) {
			
			if (this.Hooklets.size() > 0) {
				Collection<Hooklet> hooklets = this.Hooklets.values();
                for (Hooklet hooklet : hooklets) {
                    if (hooklet.Event.startsWith("Element.") == false) {
                        hooklet.Attach();
                    }
                }
            }

            if (this.Evaluators.size() > 0) {
            	Collection<Evaluation> evaluators = this.Evaluators.values();
                for (Evaluation eval : evaluators) {
                    eval.Evaluate();
                }
            }
            
            for (Element child : this.Children) {
    			child.Prepare();
    		}
		} else {
			 Log.e(LOG_TAG, "Element object is null on Prepare. Pane:" + this.Pane + ", Element:" + this.Code); 
		}
		
		this.PaneObject.onElementPrepared(this);
	}
}
