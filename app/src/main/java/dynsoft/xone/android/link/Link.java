package dynsoft.xone.android.link;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import android.content.Context;
import android.view.View;
import dynsoft.xone.android.core.App;
import dynsoft.xone.android.data.Parameters;
import dynsoft.xone.android.helper.ExpressionHelper;

public class Link {
    
    public final static char LINK_EXPR_STARTER = '{';
    public final static char LINK_EXPR_ENDER = '}';
    public final static char LINK_EXPR_ESCAPE = '\\';
    
    public final static String PARAM_IN_ENV = "env:";
    public final static String PARAM_IN_APP = "app:";
    public final static String PARAM_IN_PARA = "para:";
    public final static String PARAM_IN_STR = "str:";
    public final static String PARAM_IN_IMG = "img:";
    
    public String Header;
    
    public String Url;
    
    public Object Object;
    
    public Object Result;
    
    public Linker Linker;
    
    public Map<String,String> Expressions;
    
    public Parameters Parameters;
    
    public boolean ExpressionReplaced;
    
    public Link(String url)
    {
        this.Expressions = new HashMap<String, String>();
        this.Parameters = new Parameters();
        this.Url = url;
        this.ParseUrl();
    }
    
    private void ParseUrl()
    {
        if (this.Url != null && this.Url.length() > 0) {
            int position = this.Url.indexOf("://");
            if (position > -1) {
                this.Header = this.Url.substring(0, position + 3);
                this.Linker = App.Current.LinkerManager.GetLinker(this.Header);
                if (this.Linker != null) {
                    String str = this.Url.substring(this.Header.length(), this.Url.length());
                    if (str != null && str.length() > 0) {
                        String[] arr = ExpressionHelper.SplitStringToArray(str, LINK_EXPR_ESCAPE, ';', false);
                        if (arr != null && arr.length > 0) {
                            for (String item : arr) {
                                String[] pair = ExpressionHelper.SplitStringToArray(item, LINK_EXPR_ESCAPE, '=', true);
                                if (pair != null && pair.length > 1) {
                                    this.Expressions.put(pair[0], pair[1]);
                                    this.Parameters.add(pair[0], pair[1]);
                                }
                            }
                        }
                    }
                }
            }
        }
    }
    
    public Link AddParameter(Object key, Object value)
    {
        this.Parameters.put(key, value);
        return this;
    }
    
    public Object CreateObject(View source, Context context, Parameters parameters)
    {
        if (this.Linker != null) {
            this.Object = this.Linker.CreateObject(source, context, this, parameters);
        }
        return this.Object;
    }

    public Object Open(View source, Context context, Parameters parameters)
    {
        if (this.Linker != null) {
            return this.Linker.Open(source, context, this, parameters);
        }
        return null;
    }
    
    public void ProcessExpressions(View source, Parameters parameters)
    {
        this.ExpressionReplaced = true;
        for (Entry<String, String> kv : this.Expressions.entrySet()) {
            this.ProcessSingleExpression(source, kv.getKey(), parameters);
        }
    }

    public void ProcessSingleExpression(View source, String key, Parameters parameters)
    {
        if (this.Expressions.containsKey(key) == false) return;
        
        String text = this.Expressions.get(key);
        if (text == null || text.length() == 0) return;

        String[] expressions = ExpressionHelper.FindExpressionArray(text, LINK_EXPR_ESCAPE, LINK_EXPR_STARTER, LINK_EXPR_ENDER, true);
        if (expressions != null && expressions.length > 0) {
            if (expressions.length == 1) {
                if (text.equals(expressions[0]) == true) {
                    this.Parameters.remove(key);
                    this.Parameters.put(key, EvaluateExpression(source, expressions[0], parameters));
                } else {
                    Object val = EvaluateExpression(source, expressions[0], parameters);
                    if (val != null) {
                        this.Parameters.remove(key);
                        this.Parameters.put(key, text.replace(expressions[0], val.toString()));
                    }
                }
            } else if (expressions.length > 1) {
                for (String expr : expressions) {
                    Object val = EvaluateExpression(source, expr, parameters);
                    if (val != null) {
                        text = text.replace(expr, val.toString());
                    }
                }
                this.Parameters.remove(key);
                this.Parameters.put(key, text);
            }
        }
    }
    
    public Object EvaluateExpression(View source, String expression, Parameters parameters)
    {
        if (expression == null || expression.length() == 0) return expression;
        
        Object value = null;
        String expr = ExpressionHelper.RemoveExpressionOuter(expression, LINK_EXPR_STARTER, LINK_EXPR_ENDER);
        if (expr.startsWith(PARAM_IN_STR)) {
            expr = expr.substring(PARAM_IN_STR.length(), expr.length());
            value = App.Current.ResourceManager.getString(expr);

        } else if (expr.startsWith(PARAM_IN_IMG)) {
            expr = expr.substring(PARAM_IN_IMG.length(), expr.length());
            value = App.Current.ResourceManager.getImage(expr);

        } else if (expr.startsWith(PARAM_IN_APP)) {
            expr = expr.substring(PARAM_IN_APP.length(), expr.length());
            value = App.Current.getExpressionValue(expr);

        } else if (expr.startsWith(PARAM_IN_PARA)) {
            if (parameters != null) {
                expr = expr.substring(PARAM_IN_PARA.length(), expr.length());
                value = parameters.get(expr);
            }
        }

        return value;
    }
}
