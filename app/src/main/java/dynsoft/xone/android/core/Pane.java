package dynsoft.xone.android.core;

import dynsoft.xone.android.data.*;
import android.content.Context;
import android.content.Intent;
import android.view.MenuItem;
import android.widget.LinearLayout;

public class Pane extends LinearLayout {
	
	public final static String Pane_Param_Title = "x:title";
    public final static String Pane_Param_Pack = "x:pack";
    public final static String Pane_Param_Icon = "x:icon";
    public final static String Pane_Param_Conn = "x:conn";
    public final static String Pane_Param_Closeable = "x:closeable";
    
    public static final int NAVIGATE_FIRST = 0;
    public static final int NAVIGATE_PREV = 1;
    public static final int NAVIGATE_NEXT = 2;
    public static final int NAVIGATE_LAST = 3;
    
    public String Pack;
	public String Code;
	public Object Flag;
	public String Title;
	public String Icon;
	public String Connector;
	public Parameters Parameters;
	public ElementCollection Elements;
	public Element RootElement;
	
	public Pane(Context context)
	{
	    super(context);
		this.Parameters = new Parameters();
		this.Elements = new ElementCollection();
	}

	public void Initialize() {
        this.LoadPaneDataSet();
    }
	
	private void LoadPaneDataSet()
	{
		String sql = "select * from core_pane where code=?;"
				   + "select * from core_element where pane=? and dbo.core_user_has_perm(?,perm)=1 order by ordinal;"
				   + "select * from core_evaluation where pane=?;"
				   + "select * from core_hooklet where pane=?";
		
		Parameters p = new Parameters().add(1, this.Code).add(2, this.Code).add(3, App.Current.UserID).add(4, this.Code).add(5, this.Code);
		Result<DataSet> r = App.Current.DbPortal.ExecuteDataSet(App.Current.BookConnector, sql, p);
		  if (r.HasError == true) {
              App.Current.showError(Pane.this.getContext(), r.Error);
              return;
          }
          
          DataSet dataSet = r.Value;
          if (dataSet == null) {
              //App.Workbench.ShowError("@/win_core_pane_def_dataset_null");
              return;
          }
          
          DataTable paneTable = null;
          DataTable elementTable = null;
          DataTable evaluatorTable = null;
          DataTable hookletTable = null;

          if (dataSet.Tables.size() > 0) {
              paneTable = dataSet.Tables.get(0);
              if (paneTable.Rows.size() == 0) {
                  //App.Workbench.ShowError("@/win_core_pane_def_not_found");
              }
          }

          if (dataSet.Tables.size() > 1) {
              elementTable = dataSet.Tables.get(1);
          }

          if (dataSet.Tables.size() > 2) {
              evaluatorTable = dataSet.Tables.get(2);
          }

          if (dataSet.Tables.size() > 3) {
              hookletTable = dataSet.Tables.get(3);
          }

          if (paneTable != null && paneTable.Rows.size() > 0) {
              DataRow row = paneTable.Rows.get(0);
              
              if (this.Parameters.containsKey(Pane_Param_Icon) == false) {
                  this.Parameters.add(Pane_Param_Icon, row.getValue("icon", String.class));
              }
              
              if (this.Parameters.containsKey(Pane_Param_Title) == false) {
                  this.Parameters.add(Pane_Param_Title, row.getValue("title", String.class));
              }
              
              if (this.Parameters.containsKey(Pane_Param_Conn) == false) {
                  this.Parameters.add(Pane_Param_Conn, row.getValue("conn", String.class));
              }

              this.Icon = App.Current.ResourceManager.getString((String)this.Parameters.get(Pane_Param_Icon));
              this.Title = App.Current.ResourceManager.getString((String)this.Parameters.get(Pane_Param_Title));
              this.Pack = App.Current.ResourceManager.getString((String)this.Parameters.get(Pane_Param_Pack));
              this.Connector = App.Current.ResourceManager.getString((String)this.Parameters.get(Pane_Param_Conn));
          }

          if (elementTable != null) {
              for (DataRow rw : elementTable.Rows) {
                  Element element = new Element();
                  element.PaneObject = Pane.this;
                  element.Pack = Pane.this.Pack;
                  element.Pane = Pane.this.Code;
                  element.Code = rw.getValue("code", String.class);
                  element.Parent = rw.getValue("parent", String.class);
                  element.Perm = rw.getValue("perm", String.class);
                  element.ClassName = rw.getValue("class", String.class);
                  element.Ordinal = rw.getValue("ordinal", Integer.class);

                  if (evaluatorTable != null && evaluatorTable.Rows.size() > 0) {
                      for (DataRow er : evaluatorTable.Rows) {
                          String pane = er.getValue("pane", String.class);
                          String ecode = er.getValue("element", String.class);
                          
                          if (pane != null && pane.equals(element.Pane) 
                                  && ecode != null && ecode.equals(element.Code)){
                              
                              String property = er.getValue("property", String.class);
                              String evaluator = er.getValue("evaluator", String.class);
                              String val = er.getValue("value", String.class);
                              String conn = er.getValue("conn", String.class);

                              Evaluation eval = new Evaluation(element, property, evaluator, conn, val);
                              eval.Context = Pane.this.getContext();
                              element.Evaluators.put(property, eval);
                          }
                      }
                  }

                  if (hookletTable != null && hookletTable.Rows.size() > 0) {
                      for (DataRow er : hookletTable.Rows) {
                          String pane = er.getValue("pane", String.class);
                          String ecode = er.getValue("element", String.class);
                          
                          if (pane != null && pane.equals(element.Pane) 
                                  && ecode != null && ecode.equals(element.Code)){
                              String evt = er.getValue("event", String.class);
                              String handler = er.getValue("handler", String.class);
                              String owner = er.getValue("owner", String.class);
                              String script = er.getValue("script", String.class);
                              Hooklet hooklet = new Hooklet(Pane.this.getContext(), element, evt, handler, owner, script);
                              element.Hooklets.put(hooklet.Event, hooklet);
                          }
                      }
                  }

                  Pane.this.Elements.add(element);
              }
          }

          for (Element element : Pane.this.Elements) {
              if (element.Code.equals(Pane.this.Code) && element.Parent == null) {
                  if (Pane.this.RootElement == null) {
                      Pane.this.RootElement = element;
                      Pane.this.RootElement.Object = Pane.this;
                  }
              }

              for (Element child : Pane.this.Elements) {
                  if (child.Parent != null && child.Parent.equals(element.Code)) {
                      element.Children.add(child);
                  }
              }
          }

          if (Pane.this.RootElement != null) {
              Pane.this.onInitializing();
              Pane.this.RootElement.Initialize();
              Pane.this.onInitialized();
              
              Pane.this.onBuilding();
              Pane.this.RootElement.BuildItems();
              Pane.this.onBuilded();
              
              Pane.this.onPreparing();
              Pane.this.RootElement.Prepare();
              Pane.this.onPrepared();
          }
      
	}
	
	public void onActivityResult(int requestCode, int resultCode, Intent intent)
	{
	}
	
	public boolean onContextItemSelected(MenuItem menuItem)
	{
		return false;
	}
	
	public void onScan(String barcode)
	{
	}
	
	public void onRefrsh()
	{
	}
	
	private void onElementHookletExecute(Hooklet hooklet)
	{
	    if (hooklet != null) {
            ScriptArgs hookArgs = new ScriptArgs();
            hookArgs.App = App.Current;
            hookArgs.Context = hooklet.Context;
            hookArgs.Pane = hooklet.Element.PaneObject;
            hookArgs.Element = hooklet.Element;
            hookArgs.Hooklet = hooklet;
            hookArgs.Args = null;
            //App.Current.callScript(hooklet.Script, hookArgs);
        }
	}
	
	public void onInitializing()
	{
	}
	
	public void onElementInitializing(Element element)
	{
	    Hooklet hooklet = element.Hooklets.get("Element.Initializing");
	    if (hooklet != null) {
	        this.onElementHookletExecute(hooklet);
	    }
	}
	
	public void onElementInitialized(Element element)
    {
        Hooklet hooklet = element.Hooklets.get("Element.Initialized");
        if (hooklet != null) {
            this.onElementHookletExecute(hooklet);
        }
    }
	
	public void onInitialized()
    {
    }
	
	public void onBuilding()
    {
    }
	
	public void onElementBuilding(Element element)
    {
        Hooklet hooklet = element.Hooklets.get("Element.Building");
        if (hooklet != null) {
            this.onElementHookletExecute(hooklet);
        }
    }
	
	public void onElementBuilded(Element element)
    {
	    Hooklet hooklet = element.Hooklets.get("Element.Builded");
        if (hooklet != null) {
            this.onElementHookletExecute(hooklet);
        }
    }
	
	public void onBuilded()
    {
    }
	
	public void onPreparing()
	{
	}
	
	public void onElementPreparing(Element element)
    {
	    Hooklet hooklet = element.Hooklets.get("Element.Preparing");
        if (hooklet != null) {
            this.onElementHookletExecute(hooklet);
        }
    }
	
	public void onElementPrepared(Element element)
    {
//        Hooklet hooklet = element.Hooklets.get("Element.Prepared");
//        if (hooklet != null) {
//            this.onElementHookletExecute(hooklet);
//        }
    }
	
	public void onPrepared()
    {
    }
	
}
