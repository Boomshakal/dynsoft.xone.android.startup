package dynsoft.xone.android.base;

import java.util.ArrayList;

import dynsoft.xone.android.control.PaneFooter;
import dynsoft.xone.android.control.PaneHeader;
import dynsoft.xone.android.control.ReportItem;
import dynsoft.xone.android.core.App;
import dynsoft.xone.android.core.Element;
import dynsoft.xone.android.core.Pane;
import dynsoft.xone.android.core.R;
import dynsoft.xone.android.core.Workbench;
import dynsoft.xone.android.link.Link;
import dynsoft.xone.android.link.PaneLinker;
import dynsoft.xone.android.start.*;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;

public class BasePane extends Pane {

    public BasePane(Context context) {
		super(context);
	}

	public LinearLayout Root;
    public PaneHeader Header;
    public PaneFooter Footer;
    public ImageButton CloseButton;
    public ImageButton OpenButton;
    public ImageButton PrevButton;
    public ImageButton NextButton;
    public ImageButton DeleteButton;
    public ImageButton NewButton;
    public ImageButton EditButton;
    public ImageButton SaveButton;
    public ImageButton CommitButton;
    public ImageButton RefreshButton;
    public ImageButton ReportButton;

    @Override
    public void onPrepared() {

        this.Root = (LinearLayout)this.Elements.get("root").Object;
        this.Header = (PaneHeader)this.Elements.get("header").Object;
        this.Footer = (PaneFooter)this.Elements.get("footer").Object;
        
        this.CloseButton = (ImageButton)this.Elements.get("close").Object;
        this.OpenButton = (ImageButton)this.Elements.get("open").Object;
        this.PrevButton = (ImageButton)this.Elements.get("prev").Object;
        this.NextButton = (ImageButton)this.Elements.get("next").Object;
        this.DeleteButton = (ImageButton)this.Elements.get("delete").Object;
        this.NewButton = (ImageButton)this.Elements.get("new").Object;
        this.EditButton = (ImageButton)this.Elements.get("edit").Object;
        this.SaveButton = (ImageButton)this.Elements.get("save").Object;
        this.CommitButton = (ImageButton)this.Elements.get("commit").Object;
        this.RefreshButton = (ImageButton)this.Elements.get("refresh").Object;
        this.ReportButton = (ImageButton)this.Elements.get("report").Object;
        
        if (this.Root != null) {
            LinearLayout.LayoutParams lp_root = new LinearLayout.LayoutParams(-1,-1);
            this.Root.setLayoutParams(lp_root);
            this.Root.setOrientation(LinearLayout.VERTICAL);
            this.Root.setBackgroundResource(R.drawable.bg_pane);
        }
        
        if (this.Header != null) {
            this.Header.Pane = this;
            
            LinearLayout.LayoutParams lp_header = new LinearLayout.LayoutParams(-1,-2);
            lp_header.height = PaneHeader.HeaderHeight;
            this.Header.setLayoutParams(lp_header);
            this.Header.setIconImage(App.Current.ResourceManager.getImage(this.Icon));
            this.Header.setMenuImage(App.Current.ResourceManager.getImage("@/core_menu_list_white"));
            this.Header.setTitleText(App.Current.ResourceManager.getString(this.Title));
        }

        if (Footer != null) {
            LinearLayout.LayoutParams lp_footer = new LinearLayout.LayoutParams(-1,-2);
            lp_footer.height = PaneFooter.FooterHeight;
            Footer.setLayoutParams(lp_footer);
            //Footer.setBackgroundResource(R.drawable.bg_footer);
            
            int childCount = Footer.getChildCount();
            if (childCount > 0) {
                LinearLayout.LayoutParams lp_cmd = new LinearLayout.LayoutParams(-2,-2);
                lp_cmd.gravity = Gravity.CENTER_VERTICAL;
                lp_cmd.weight = 1;
                lp_cmd.height = PaneFooter.ButtonHeight;
                lp_cmd.width = PaneFooter.ButtonWidth;
                
                for (int i=0; i<childCount;i++) {
                    ImageButton cmd = (ImageButton)Footer.getChildAt(i);
                    if (cmd != null) {
                        cmd.setLayoutParams(lp_cmd);
                        cmd.setBackgroundDrawable(null);
                        cmd.setScaleType(ScaleType.FIT_CENTER);
                        cmd.setPadding(0, 0, 0, 0);
                    }
                }
            }
        }
        
        if (this.CloseButton != null) {
            this.CloseButton.setImageBitmap(App.Current.ResourceManager.getImage("@/core_exit_white"));
            this.CloseButton.setOnClickListener(new OnClickListener(){
                @Override
                public void onClick(View v) {
                    BasePane.this.close();
                }
            });
        }
        
        if (this.OpenButton != null) {
            this.OpenButton.setImageBitmap(App.Current.ResourceManager.getImage("@/core_open_white"));
            this.OpenButton.setOnClickListener(new OnClickListener(){
                @Override
                public void onClick(View v) {
                    BasePane.this.open();
                }
            });
        }
        
        if (this.PrevButton != null) {
            this.PrevButton.setImageBitmap(App.Current.ResourceManager.getImage("@/core_prev_white"));
            this.PrevButton.setOnClickListener(new OnClickListener(){
                @Override
                public void onClick(View v) {
                    BasePane.this.prev();
                }
            });
        }
        
        if (this.NextButton != null) {
            this.NextButton.setImageBitmap(App.Current.ResourceManager.getImage("@/core_next_white"));
            this.NextButton.setOnClickListener(new OnClickListener(){
                @Override
                public void onClick(View v) {
                    BasePane.this.next();
                }
            });
        }
        
        if (this.DeleteButton != null) {
            this.DeleteButton.setImageBitmap(App.Current.ResourceManager.getImage("@/core_delete_white"));
            this.DeleteButton.setOnClickListener(new OnClickListener(){
                @Override
                public void onClick(View v) {
                    BasePane.this.delete();
                }
            });
        }
        
        if (this.NewButton != null) {
            this.NewButton.setImageBitmap(App.Current.ResourceManager.getImage("@/core_new_white"));
            this.NewButton.setOnClickListener(new OnClickListener(){
                @Override
                public void onClick(View v) {
                    BasePane.this.create();
                }
            });
        }
        
        if (this.EditButton != null) {
            this.EditButton.setImageBitmap(App.Current.ResourceManager.getImage("@/core_edit_white"));
            this.EditButton.setOnClickListener(new OnClickListener(){
                @Override
                public void onClick(View v) {
                    BasePane.this.edit();
                }
            });
        }
        
        if (this.SaveButton != null) {
            this.SaveButton.setImageBitmap(App.Current.ResourceManager.getImage("@/core_save_white"));
            this.SaveButton.setOnClickListener(new OnClickListener(){
                @Override
                public void onClick(View v) {
                    BasePane.this.save();
                }
            });
        }
        
        if (this.CommitButton != null) {
            this.CommitButton.setImageBitmap(App.Current.ResourceManager.getImage("@/core_commit_white"));
            this.CommitButton.setOnClickListener(new OnClickListener(){
                @Override
                public void onClick(View v) {
                    BasePane.this.commit();
                }
            });
        }
        
        if (this.RefreshButton != null) {
            this.RefreshButton.setImageBitmap(App.Current.ResourceManager.getImage("@/core_refresh_white"));
            this.RefreshButton.setOnClickListener(new OnClickListener(){
                @Override
                public void onClick(View v) {
                    BasePane.this.refresh();
                }
            });
        }
        
        if (this.ReportButton != null) {
            this.ReportButton.setImageBitmap(App.Current.ResourceManager.getImage("@/core_report_white"));
            this.ReportButton.setOnClickListener(new OnClickListener(){
                @Override
                public void onClick(View v) {
                    BasePane.this.report();
                }
            });
        }
    }

    public void close()
    {
    	((Workbench)App.Current.Workbench).closePane(BasePane.this);
    }
    
    public void open()
    {}
    
    public void prev()
    {}
    
    public void next()
    {}
    
    public void delete()
    {}
    
    public void create()
    {}
    
    public void edit()
    {}
    
    public void save()
    {}
    
    public void commit()
    {}
    
    public void refresh()
    {}
    
    public void report()
    {
        Element element = this.Elements.get("report");
        if (element != null && element.Children.size() > 0) {
            
            final ArrayList<ReportItem> reports = new ArrayList<ReportItem>();
            for (Element e : element.Children) {
                if (e.Object != null && e.Object instanceof ReportItem) {
                    reports.add((ReportItem)e.Object);
                }
            }
            
            if (reports.size() > 0) {
                BaseAdapter adapter = new BaseAdapter(){

                    @Override
                    public int getCount() {
                        return reports.size();
                    }

                    @Override
                    public Object getItem(int position) {
                        return reports.get(position);
                    }

                    @Override
                    public long getItemId(int position) {
                        return position;
                    }

                    @Override
                    public View getView(int position, View convertView, ViewGroup parent) {
                        ReportItem report = reports.get(position);
                        if (convertView == null) {
                            convertView = LayoutInflater.from(BasePane.this.getContext()).inflate(R.layout.report_item, null);
                            ImageView icon = (ImageView)convertView.findViewById(R.id.imgIcon);
                            icon.setImageBitmap(App.Current.ResourceManager.getImage("@/core_report_gray"));
                        }
                        
                        TextView title = (TextView)convertView.findViewById(R.id.txtTitle);
                        title.setText(report.Title);
                        return convertView;
                    }
                };
                
                DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (which >= 0) {
                            ReportItem item = reports.get(which);
                            BasePane.this.reportItem(item);
                        }
                        dialog.dismiss();
                    }
                };
                
                Dialog dialog = new AlertDialog.Builder(this.getContext()).setSingleChoiceItems(adapter, 0, listener).create();
                dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
                dialog.show();
            }
        }
    }
    
    public void reportItem(ReportItem item)
    {
        Link link = new Link(item.Url);
        link.AddParameter(PaneLinker.KEY_XFLAG, item);
        link.Open(this.ReportButton, this.getContext(), this.Parameters);
    }

}
