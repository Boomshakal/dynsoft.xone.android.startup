package dynsoft.xone.android.control;

import dynsoft.xone.android.core.App;
import dynsoft.xone.android.core.Element;
import dynsoft.xone.android.core.IParent;
import dynsoft.xone.android.core.Pane;
import dynsoft.xone.android.core.R;
import dynsoft.xone.android.helper.PaneHelper;
import dynsoft.xone.android.data.Parameters;
import dynsoft.xone.android.data.ResultHandler;
import dynsoft.xone.android.link.Link;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Message;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class TreeViewItem extends BorderLayout implements IParent {

    public static int BorderColor = Color.parseColor("#C0C0C0");

    protected boolean _expanded;
    protected boolean _hasItems;
    protected ImageView _imgIcon;
    protected TextView _txtTitle;
    protected TextView _txtComment;
    protected LinearLayout _loHeader;
    protected LinearLayout _loItems;
    protected CharSequence _title;
    protected String _url;
    protected String _sql;
    protected String _connector;
    
    public TreeViewItem(Context context) {
        super(context);
        this.Init(context);
    }

    public TreeViewItem(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.Init(context);
    }
    
    public TreeViewItem(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.Init(context);
    }
    
    public void setLinkUrl(String url)
    {
        _url = url;
    }
    
    public String getLinkUrl()
    {
        return _url;
    }
    
    public void setConnector(String connector)
    {
    	_connector = connector;
    }
    
    public String getConnector()
    {
    	return _connector;
    }
    
    public void setSQL(String sql)
    {
    	 _sql = sql;
    }
    
    public String getSQL()
    {
    	return _sql;
    }
    
    public void refresh()
    {
    	if (_sql != null && _sql.length() > 0 && _connector != null && _connector.length() > 0) {
    		_txtTitle.setText(_title + "...");
    		String sql = String.format(_sql, App.Current.UserID);
    		App.Current.DbPortal.ExecuteScalarAsync(_connector, sql, Integer.class, new ResultHandler<Integer>(){
    			@Override
    			public void handleMessage(Message msg){
    				if (this.Value.HasError == true) {
    					_txtTitle.setText(_title);
    					_txtTitle.setTextColor(Color.parseColor("#404040"));
    					App.Current.showError(TreeViewItem.this.getContext(), this.Value.Error);
    					return;
    				}

    				if (this.Value != null && this.Value.Value > 0) {
    					String count = String.valueOf(this.Value.Value);
    					_txtTitle.setText(_title + "(" + count + ")");
    					_txtTitle.setTextColor(Color.BLUE);
    				} else {
    					_txtTitle.setText(_title);
    					_txtTitle.setTextColor(Color.parseColor("#404040"));
    				}
    			}
    		});
    	}
    }
    
    protected void onHeaderClicked()
    {
        if (_url != null && _url.length() > 0) {
            Pane pane = PaneHelper.GetPane(this);
            Parameters parameters = (pane != null) ? pane.Parameters: null;
            Link link = new Link(_url);
            link.Open(this, this.getContext(), parameters);
        }
    }
    
    private void Init(Context context)
    {
        _hasItems = false;
        _expanded = false;
        
        this.setBorderColor(BorderColor);
        this.setBorderThickness(0, 0, 0, 1);
        
        View view = this.getView();
        if (view == null) {
            LayoutInflater.from(context).inflate(R.layout.treeviewitem, this, true);
        } else {
            this.addView(view);
        }

        _loHeader = (LinearLayout)this.findViewById(R.id.loHeader);
        _imgIcon = (ImageView)this.findViewById(R.id.imgIcon);
        _txtTitle = (TextView)this.findViewById(R.id.txtTitle);
        _txtComment = (TextView)this.findViewById(R.id.txtComment);
        _loItems = (LinearLayout)this.findViewById(R.id.loItems);
        
        if (_loHeader != null) {
            _loHeader.setClickable(true);
            _loHeader.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View arg0) {
                    boolean expanded = TreeViewItem.this.getExpanded();
                    TreeViewItem.this.setExpanded(!expanded);
                    TreeViewItem.this.onHeaderClicked();
                }
            });
        }
    }

    public View getView()
    {
        return null;
    }

    @Override
    public void addChild(Element child) {
        View item = (View)child.Object;
        this.addView(item);
    }

    public void setExpanded(boolean expanded)
    {
        if (_hasItems == false) return;
        
        if (_expanded != expanded) {
            _expanded = expanded;
            
            if (_loItems != null) {
                if (_expanded == true) {
                    _loItems.setVisibility(View.VISIBLE);
                } else {
                    _loItems.setVisibility(View.GONE);
                }
            }
        }
    }
    
    public boolean getExpanded()
    {
        return _expanded;
    }
    
    public void setIcon(Bitmap bitmap)
    {
        if (_imgIcon != null) {
            _imgIcon.setImageBitmap(bitmap);
        }
    }
    
    public void setTitle(CharSequence title)
    {
    	_title = title;
        if (_txtTitle != null) {
            _txtTitle.setText(title);
        }
    }
    
    public CharSequence getTitle()
    {
    	return _title;
    }
    
    public void setComment(CharSequence comment)
    {
        if (_txtComment != null) {
        	_txtComment.setText(comment);
        }
    }
    
    public CharSequence getComment()
    {
        if (_txtComment != null) {
            return _txtComment.getText();
        }
        return null;
    }
    
    public void addView(View item)
    {
        if (item != null) {
            if (_loItems != null) {
                _loItems.addView(item);
                this.onItemsChanged();
            }
        }
    }

    public void removeView(View item)
    {
        if (item != null) {
            if (_loItems != null) {
                _loItems.removeView(item);
                this.onItemsChanged();
            }
        }
    }
    
    public void removeAll()
    {
        if (_loItems != null) {
            _loItems.removeAllViews();
            this.onItemsChanged();
        }
    }
    
    private void onItemsChanged()
    {
        _hasItems = _loItems.getChildCount() > 0;
    }
}
