package dynsoft.xone.android.sbo;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;

import dynsoft.xone.android.adapter.PageTableAdapter;
import dynsoft.xone.android.adapter.TableAdapter;
import dynsoft.xone.android.base.BasePane;
import dynsoft.xone.android.control.BorderLayout;
import dynsoft.xone.android.control.ButtonTextCell;
import dynsoft.xone.android.control.DateCell;
import dynsoft.xone.android.control.DecimalCell;
import dynsoft.xone.android.control.NestedListView;
import dynsoft.xone.android.control.PaneCell;
import dynsoft.xone.android.control.PaneHeader;
import dynsoft.xone.android.control.TextCell;
import dynsoft.xone.android.control.ToolBarCell;
import dynsoft.xone.android.core.App;
import dynsoft.xone.android.core.Pane;
import dynsoft.xone.android.core.R;
import dynsoft.xone.android.data.DataRow;
import dynsoft.xone.android.data.DataSet;
import dynsoft.xone.android.data.DataTable;
import dynsoft.xone.android.data.Parameters;
import dynsoft.xone.android.data.Result;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.text.format.DateFormat;
import android.text.format.Time;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageButton;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;

public class odoc_editor extends BasePane {

    public odoc_editor(Context context) {
		super(context);
	}

	public ScrollView Scroll;
    public LinearLayout Body;
    public TextCell DocNumCell;
    public ButtonTextCell BaseDocCell;
    public ButtonTextCell CardCodeCell;
    public ButtonTextCell CardNameCell;
    public DateCell DocDateCell;
    public DateCell DueDateCell;
    public ButtonTextCell SlpCodeCell;
    public TextCell CommentCell;
    public ToolBarCell LineSwitchCell;
    public ImageButton SwitchButton;
    public LinearLayout SingleLayout;
    public LinearLayout MultiLayout;
    public TextCell LineNumCell;
    public TextCell BaseLineCell;
    public ButtonTextCell ItemCodeCell;
    public ButtonTextCell ItemNameCell;
    public ButtonTextCell WhsCodeCell;
    public DecimalCell QuantityCell;
    public DecimalCell PriceCell;
    public TextCell BatchNumCell;
    public TextCell FreeTextCell;
    public NestedListView LinesListView;
    public PageTableAdapter LinesAdapter;
    public NestedListView BatchListView;
    public TableAdapter BatchAdapter;
    public int DocEntry;
    public DataRow DocRow;
    public int LineNum;
    public DataRow LineRow;
    
    @Override
    public void onPrepared() {
        super.onPrepared();
        
        this.Scroll = (ScrollView)this.Elements.get("scroll").Object;
        this.Body = (LinearLayout)this.Elements.get("body").Object;
        
        this.DocNumCell = (TextCell)this.Elements.get("docnum").Object;
        this.BaseDocCell = (ButtonTextCell)this.Elements.get("basedoc").Object;
        this.CardCodeCell = (ButtonTextCell)this.Elements.get("cardcode").Object;
        this.CardNameCell = (ButtonTextCell)this.Elements.get("cardname").Object;
        this.DocDateCell = (DateCell)this.Elements.get("docdate").Object;
        this.DueDateCell = (DateCell)this.Elements.get("duedate").Object;
        this.SlpCodeCell = (ButtonTextCell)this.Elements.get("slpcode").Object;
        this.CommentCell = (TextCell)this.Elements.get("comment").Object;
        
        this.LineSwitchCell = (ToolBarCell)this.Elements.get("lineswitch").Object;
        this.SwitchButton = (ImageButton)this.Elements.get("switch").Object;
        this.SingleLayout = (LinearLayout)this.Elements.get("single").Object;
        this.MultiLayout = (LinearLayout)this.Elements.get("multi").Object;
        this.LineNumCell = (TextCell)this.Elements.get("linenum").Object;
        this.BaseLineCell = (TextCell)this.Elements.get("baseline").Object;
        this.ItemCodeCell = (ButtonTextCell)this.Elements.get("itemcode").Object;
        this.ItemNameCell = (ButtonTextCell)this.Elements.get("itemname").Object;
        this.QuantityCell = (DecimalCell)this.Elements.get("quantity").Object;
        this.PriceCell = (DecimalCell)this.Elements.get("price").Object;
        this.WhsCodeCell = (ButtonTextCell)this.Elements.get("warehouse").Object;
        this.BatchNumCell = (TextCell)this.Elements.get("batchnum").Object;
        this.FreeTextCell = (TextCell)this.Elements.get("freetext").Object;
        this.LinesListView = (NestedListView)this.Elements.get("lines").Object;
        this.BatchListView = (NestedListView)this.Elements.get("batches").Object;

        if (Scroll != null) {
            LinearLayout.LayoutParams lp_scroll = new LinearLayout.LayoutParams(-1,-2);
            lp_scroll.weight = 1;
            Scroll.setLayoutParams(lp_scroll);
            Scroll.setBackgroundColor(Color.WHITE);
        }
        
        if (Body != null) {
            Body.setOrientation(LinearLayout.VERTICAL);
            LinearLayout.LayoutParams lp_module = new LinearLayout.LayoutParams(-1,-2);
            int count = Body.getChildCount();
            for (int i=0; i<count; i++) {
                View child = Body.getChildAt(i);
                child.setLayoutParams(lp_module);
            }
        }
        
        if (this.DocNumCell != null) {
            this.DocNumCell.setLabelText("单据编号");
            this.DocNumCell.TextBox.setKeyListener(null);
            this.DocNumCell.TextBox.setFocusable(false);
        }
        
        if (this.BaseDocCell != null) {
            this.BaseDocCell.setLabelText("基于单据");
            this.BaseDocCell.Button.setOnClickListener(new OnClickListener(){
                @Override
                public void onClick(View v) {
                    odoc_editor.this.chooseBaseDoc();
                }
            });
        }
        
        if (this.CardCodeCell != null) {
            this.CardCodeCell.setLabelText("业务伙伴");
            this.CardCodeCell.Button.setOnClickListener(new OnClickListener(){
                @Override
                public void onClick(View v) {
                    odoc_editor.this.chooseCardCode();
                }
            });
        }
        
        if (this.CardNameCell != null) {
            this.CardNameCell.setLabelText("名称");
            this.CardNameCell.TextBox.setKeyListener(null);
            this.CardNameCell.TextBox.setFocusable(false);
            this.CardNameCell.setButtonImage(App.Current.ResourceManager.getImage("@/core_forward_light"));
        }
        
        if (this.DocDateCell != null) {
            this.DocDateCell.setLabelText("单据日期");
            this.DocDateCell.TextBox.setFocusable(false);
        }
        
        if (this.DueDateCell != null) {
            this.DueDateCell.setLabelText("截止日期");
            this.DueDateCell.TextBox.setFocusable(false);
        }
        
        if (this.SlpCodeCell != null) {
            this.SlpCodeCell.setLabelText("业务员");
            this.SlpCodeCell.TextBox.setKeyListener(null);
            this.SlpCodeCell.TextBox.setFocusable(false);
        }
        
        if (this.CommentCell != null) {
            this.CommentCell.setLabelText("备注");
        }
        
        if (this.LineSwitchCell != null) {
            this.LineSwitchCell.initButtonLayout();
        }
        
        if (this.SwitchButton != null) {
            this.SwitchButton.setBackgroundColor(Color.TRANSPARENT);
            this.SwitchButton.setPadding(0, 0, 0, 0);
            this.SwitchButton.setScaleType(ScaleType.FIT_CENTER);
            this.SwitchButton.setImageBitmap(App.Current.ResourceManager.getImage("@/core_single_light"));
            this.SwitchButton.setOnClickListener(new OnClickListener(){
                @Override
                public void onClick(View v) {
                    odoc_editor.this.switchLines();
                }
            });
        }

        if (this.SingleLayout != null) {
            this.SingleLayout.setOrientation(LinearLayout.VERTICAL);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(-1,-2);
            int count = this.SingleLayout.getChildCount();
            for (int i=0; i<count; i++) {
                View child = this.SingleLayout.getChildAt(i);
                child.setLayoutParams(lp);
            }
        }
        
        if (this.MultiLayout != null) {
            this.MultiLayout.setOrientation(LinearLayout.VERTICAL);
            this.MultiLayout.setVisibility(View.GONE);
        }

        if (this.LineNumCell != null) {
            this.LineNumCell.setLabelText("行号");
            this.LineNumCell.TextBox.setKeyListener(null);
            this.LineNumCell.TextBox.setFocusable(false);
        }
        
        if (this.BaseLineCell != null) {
            this.BaseLineCell.setLabelText("基本行");
            this.BaseLineCell.TextBox.setKeyListener(null);
        }

        if (this.ItemCodeCell != null) {
            this.ItemCodeCell.setLabelText("物料编号");
            this.ItemCodeCell.Button.setOnClickListener(new OnClickListener(){
                @Override
                public void onClick(View v) {
                    odoc_editor.this.chooseItemCode();
                }
            });
        }
        
        if (this.ItemNameCell != null) {
            this.ItemNameCell.setLabelText("物料名称");
            this.ItemNameCell.TextBox.setKeyListener(null);
            this.ItemNameCell.TextBox.setFocusable(false);
        }
        
        if (this.QuantityCell != null) {
            this.QuantityCell.setLabelText("数量");
        }
        
        if (this.PriceCell != null) {
            this.PriceCell.setLabelText("价格");
        }
        
        if (this.WhsCodeCell != null) {
            this.WhsCodeCell.setLabelText("仓库");
            this.WhsCodeCell.TextBox.setKeyListener(null);
            this.WhsCodeCell.TextBox.setFocusable(false);
            this.WhsCodeCell.Button.setOnClickListener(new OnClickListener(){
                @Override
                public void onClick(View v) {
                    odoc_editor.this.chooseWhsCode();
                }
            });
        }
        
        if (this.BatchNumCell != null) {
            this.BatchNumCell.setLabelText("批序号");
        }
        
        if (this.FreeTextCell != null) {
            this.FreeTextCell.setLabelText("备注");
        }

        if (this.LinesListView != null) {
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(-1,-2);
            lp.height = App.dpToPx(300);
            this.LinesListView.setLayoutParams(lp);
            this.LinesListView.ScrollView = this.Scroll;
            this.LinesAdapter = new PageTableAdapter(getContext()) {
                @Override
                public View getView(int position, View convertView, ViewGroup parent) {
                    if (position < odoc_editor.this.LinesAdapter.DataTable.Rows.size()) {
                        if (convertView == odoc_editor.this.LinesAdapter.Footer) 
                            convertView = null;
                        return odoc_editor.this.getLineView(position, convertView, parent);
                    } else {
                        return odoc_editor.this.LinesAdapter.Footer;
                    }
                }
            };
            
            this.LinesAdapter.PageIndex = 0;
            this.LinesAdapter.PageSize = 10;
            this.LinesListView.setAdapter(this.LinesAdapter);
            this.LinesListView.setOnItemClickListener(new OnItemClickListener(){
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    if (id < odoc_editor.this.LinesAdapter.DataTable.Rows.size()) {
                        DataRow row = odoc_editor.this.LinesAdapter.DataTable.Rows.get((int)id);
                        odoc_editor.this.LineNum = row.getValue("LineNum", Integer.class);
                        odoc_editor.this.switchLines();
                        odoc_editor.this.refreshSingleLine();
                    } else {
                        odoc_editor.this.refreshMultiLines();
                    }
                }
            });
        }
        
        if (this.BatchListView != null) {
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(-1,-2);
            lp.height = App.dpToPx(200);
            this.BatchListView.setLayoutParams(lp);
            this.BatchListView.ScrollView = this.Scroll;
            this.BatchAdapter = new TableAdapter(getContext()) {
                @Override
                public View getView(int position, View convertView, ViewGroup parent) {
                    if (convertView == null) {
                        convertView = LayoutInflater.from(getContext()).inflate(R.layout.odoc_obtn, null);
                        ImageView icon = (ImageView)convertView.findViewById(R.id.icon);
                        icon.setImageBitmap(App.Current.ResourceManager.getImage("@/sbo_obtn_gray"));
                    }
                    
                    DataRow row = (DataRow)odoc_editor.this.BatchAdapter.getItem(position);
                    TextView num = (TextView)convertView.findViewById(R.id.num);
                    TextView batchnum = (TextView)convertView.findViewById(R.id.batchnum);
                    TextView quantity = (TextView)convertView.findViewById(R.id.quantity);
                    
                    num.setText(String.valueOf(position + 1));
                    batchnum.setText(row.getValue("BatchNum", ""));
                    quantity.setText("数量:" + App.formatNumber(row.getValue("Quantity"), "0.##"));

                    return convertView;
                }
            };

            this.BatchListView.setAdapter(this.BatchAdapter);
            if (this.DocEntry < 0) {
                this.BatchListView.setVisibility(View.GONE);
            } else {
                this.BatchListView.setVisibility(View.VISIBLE);
            }
        }
        
        this.DocEntry = this.Parameters.get("DocEntry", -1);
        this.refreshData();
    }
    
    public View getLineView(int position, View convertView, ViewGroup parent)
    {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.odoc_line, null);
            ImageView icon = (ImageView)convertView.findViewById(R.id.icon);
            icon.setImageBitmap(App.Current.ResourceManager.getImage("@/sbo_oitm_gray"));
        }
        
        DataRow row = (DataRow)this.LinesAdapter.getItem(position);
        TextView num = (TextView)convertView.findViewById(R.id.num);
        TextView linenum = (TextView)convertView.findViewById(R.id.linenum);
        TextView itemname = (TextView)convertView.findViewById(R.id.itemname);
        TextView itemcode = (TextView)convertView.findViewById(R.id.itemcode);
        TextView quantity = (TextView)convertView.findViewById(R.id.quantity);
        TextView batchnum = (TextView)convertView.findViewById(R.id.batchnum);
        TextView comment = (TextView)convertView.findViewById(R.id.comment);
        
        num.setText(String.valueOf(position + 1));
        itemname.setText(row.getValue("Dscription",""));
        linenum.setText("#" + row.getValue("LineNum").toString());
        itemcode.setText("物料编号:" + row.getValue("ItemCode", ""));
        quantity.setText("数量:" + App.formatNumber(row.getValue("Quantity"), "0.##"));
        batchnum.setText(row.getValue("BatchNum", ""));
        
        if (batchnum.getText().length() > 0) {
            batchnum.setVisibility(View.VISIBLE);
        } else {
            batchnum.setVisibility(View.GONE);
        }
        
        comment.setText(row.getValue("FreeTxt", ""));
        if (comment.getText().length() > 0) {
            comment.setVisibility(View.VISIBLE);
        } else {
            comment.setVisibility(View.GONE);
        }
        
        return convertView;
    }
    
    public void refreshData()
    {
        if (this.DocEntry > -1) {
            String table = this.getDocType().MainTable;
            String sql = "select DocEntry,DocNum,CardCode,CardName,DocDate,DocDueDate,TaxDate,DocCur,DocTotal,OSLP.SlpCode,OSLP.SlpName,Comments from " 
                       + table + " left join OSLP on OSLP.SlpCode=" + table+ ".SlpCode where DocEntry=?";
            Parameters p = new Parameters().add(1, this.DocEntry);
            Result<DataRow> r = App.Current.DbPortal.ExecuteRecord(this.Connector, sql, p);
            if (r.Value != null) {
                
                this.DocRow = r.Value;
                
                String title = this.Title + " - " + r.Value.getValue("DocNum").toString();
                this.Header.setTitleText(title);
                
                if (this.DocNumCell != null) {
                    this.DocNumCell.setContentText(r.Value.getValue("DocNum").toString());
                }

                if (this.CardCodeCell != null) {
                    this.CardCodeCell.setContentText(r.Value.getValue("CardCode",""));
                }
                
                if (this.CardNameCell != null) {
                    this.CardNameCell.setContentText(r.Value.getValue("CardName", ""));
                }
                
                if (this.DocDateCell != null) {
                    this.DocDateCell.setContentText(App.formatDateTime(r.Value.getValue("DocDate"), "yyyy-MM-dd"));
                }
                
                if (this.DueDateCell != null) {
                    this.DueDateCell.setContentText(App.formatDateTime(r.Value.getValue("DocDueDate"), "yyyy-MM-dd"));
                }
                
                if (this.SlpCodeCell != null) {
                    this.SlpCodeCell.setContentText(r.Value.getValue("SlpName", ""));
                    this.SlpCodeCell.TextBox.setTag(r.Value.getValue("SlpCode"));
                }
                
                if (this.CommentCell != null) {
                    this.CommentCell.setContentText(r.Value.getValue("Comments", ""));
                }
                
                if (this.SingleLayout.getVisibility() == View.VISIBLE) {
                    this.LineNum = this.Parameters.get("LineNum", 0);
                    this.refreshSingleLine();
                } else {
                    this.LineNum = -1;
                    this.LineRow = null;
                    this.LinesAdapter.PageIndex = 0;
                    this.LinesAdapter.DataTable = null;
                    this.refreshMultiLines();
                }
            } else {
                this.DocRow = null;
                this.Root.setEnabled(false);
                if (r.HasError) {
                    App.Current.showError(getContext(), r.Error);
                }
            }
            
            if (this.BatchNumCell != null) this.BatchNumCell.setVisibility(View.GONE);
            if (this.DeleteButton != null) this.DeleteButton.setVisibility(View.GONE);
            if (this.SaveButton != null) this.SaveButton.setVisibility(View.GONE);
            if (this.PrevButton != null) this.PrevButton.setVisibility(View.VISIBLE);
            if (this.NextButton != null) this.NextButton.setVisibility(View.VISIBLE);
            
        } else {
            this.DocRow = null;
            this.LineNum = -1;
            this.LineRow = null;
            this.Header.setTitleText(this.Title + " - 新建");
            if (this.SingleLayout.getVisibility() == View.VISIBLE) {
                if (this.DeleteButton != null) this.DeleteButton.setVisibility(View.VISIBLE);
                if (this.SaveButton != null) this.SaveButton.setVisibility(View.VISIBLE);
            } else if (this.MultiLayout.getVisibility() == View.VISIBLE) {
                if (this.DeleteButton != null) this.DeleteButton.setVisibility(View.GONE);
                if (this.SaveButton != null) this.SaveButton.setVisibility(View.GONE);
            }
            if (this.PrevButton != null) this.PrevButton.setVisibility(View.GONE);
            if (this.NextButton != null) this.NextButton.setVisibility(View.GONE);
        }
    }
    
    @Override
    public void prev()
    {
        this.navigate(Pane.NAVIGATE_PREV);
    }
    
    @Override
    public void next()
    {
        this.navigate(Pane.NAVIGATE_NEXT);
    }
    
    private void navigate(int direction)
    {
        if (this.SingleLayout.getVisibility() == View.VISIBLE) {
            if (this.DocEntry < 0) {
                if (this.LinesAdapter.DataTable != null) {
                    for (DataRow r : this.LinesAdapter.DataTable.Rows) {
                        int lineNum = r.getValue("LineNum", Integer.class);
                        if (lineNum > this.LineNum) {
                            this.LineNum = lineNum;
                            this.refreshSingleLine();
                            break;
                        }
                    }
                }
            } else {
                String table = this.getDocType().LineTable;
                String sql = "";
                if (direction == Pane.NAVIGATE_NEXT) {
                    sql = "select top 1 LineNum from " + table + " where DocEntry=? and LineNum>?";
                } else if (direction == Pane.NAVIGATE_PREV) {
                    sql = "select top 1 LineNum from " + table + " where DocEntry=? and LineNum<? order by LineNum desc";
                }
                Parameters p = new Parameters().add(1, this.DocEntry).add(2, this.LineNum);
                Result<Integer> r = App.Current.DbPortal.ExecuteScalar(this.Connector, sql, p, Integer.class);
                if (r.Value != null && r.Value > 0) {
                    this.LineNum = r.Value;
                    this.refreshSingleLine();
                }
            }
        } else if (this.MultiLayout.getVisibility() == View.VISIBLE) {
            if (this.DocEntry < 0) return;
            
            String table = this.getDocType().MainTable;
            int docEntry = (Integer)this.Parameters.get("DocEntry");
            String sql = "";
            if (direction == Pane.NAVIGATE_NEXT) {
                sql = "select top 1 DocEntry from " + table + " where DocEntry>?";
            } else if (direction == Pane.NAVIGATE_PREV) {
                sql = "select top 1 DocEntry from " + table + " where DocEntry<? order by DocEntry desc";
            }
            Parameters p = new Parameters().add(1, docEntry);
            Result<Integer> r = App.Current.DbPortal.ExecuteScalar(this.Connector, sql, p, Integer.class);
            if (r.Value != null && r.Value > 0) {
                this.Parameters.remove("DocEntry");
                this.Parameters.add("DocEntry", r.Value);
                this.DocEntry = r.Value;
                this.LineNum = 0;
                this.LinesAdapter.PageIndex = 0;
                this.refreshData();
            }
        }
    }
    
    public void switchLines()
    {
        if (this.SingleLayout != null && this.MultiLayout != null) {
            if (this.SingleLayout.getVisibility() == View.VISIBLE) {
                this.SwitchButton.setImageBitmap(App.Current.ResourceManager.getImage("@/core_multis_light"));
                if (this.DeleteButton != null) this.DeleteButton.setVisibility(View.GONE);
                if (this.SaveButton != null) this.SaveButton.setVisibility(View.GONE);
                this.SingleLayout.setVisibility(View.GONE);
                this.MultiLayout.setVisibility(View.VISIBLE);
                if (this.LinesAdapter.DataTable == null) {
                    this.refreshMultiLines();
                } else {
                    this.LinesAdapter.notifyDataSetChanged();
                }
            } else {
                this.SwitchButton.setImageBitmap(App.Current.ResourceManager.getImage("@/core_single_light"));
                if (this.DocEntry < 0) {
                    if (this.DeleteButton != null) this.DeleteButton.setVisibility(View.VISIBLE);
                    if (this.SaveButton != null) this.SaveButton.setVisibility(View.VISIBLE);
                }
                this.SingleLayout.setVisibility(View.VISIBLE);
                this.MultiLayout.setVisibility(View.GONE);
                this.refreshSingleLine();
            }
        }
    }
    
    public void refreshSingleLine()
    {
        DataRow row = null;
        DataTable batchTable = null;
        if (this.DocEntry < 0) {
            if (this.LinesAdapter.DataTable != null) {
                for (DataRow r : this.LinesAdapter.DataTable.Rows) {
                    if (r.getValue("LineNum", Integer.class) == this.LineNum) {
                        this.LineRow = row = r;
                        break;
                    }
                }
            } else {
                this.LineNum = -1;
            }
        } else {
            DocType docType = this.getDocType();
            String table = docType.LineTable;
            String sql = "select LineNum,BaseType,BaseEntry,BaseLine,LineStatus,"+ table +".ItemCode,Dscription,Quantity,OWHS.WhsCode,OWHS.WhsName,Price,LineTotal,ManSerNum,ManBtchNum,ManOutOnly from " 
                       + table + " inner join OITM on OITM.ItemCode=" + table + ".ItemCode inner join OWHS on OWHS.WhsCode=" + table + ".WhsCode where " + table + ".DocEntry=? and LineNum=?;"
                       + "select IBT1.BatchNum,IBT1.Quantity from IBT1 where BaseType=" + String.valueOf(docType.TypeID) + " and BaseEntry=? and BaseLinNum=? union "
                       + "select ISNULL(ISNULL(nullif(OSRN.MnfSerial,''),nullif(OSRN.DistNumber,'')),OSRN.LotNumber) BatchNum,1.0 Quantity from SRI1 left join OSRN on SRI1.ItemCode=OSRN.ItemCode and SRI1.SysSerial=OSRN.SysNumber where SRI1.BaseType=20 and SRI1.BaseEntry=? and SRI1.BaseLinNum=?";
            
            Parameters p = new Parameters().add(1, this.DocEntry).add(2, this.LineNum).add(3, this.DocEntry).add(4, this.LineNum).add(5, this.DocEntry).add(6, this.LineNum);
            Result<DataSet> r = App.Current.DbPortal.ExecuteDataSet(this.Connector, sql, p);
            if (r.Value != null) {
                if (r.Value.Tables.size() > 0 && r.Value.Tables.get(0).Rows.size() > 0) {
                    this.LineRow = row = r.Value.Tables.get(0).Rows.get(0);
                }
                
                if (r.Value.Tables.size() > 1) {
                    batchTable = r.Value.Tables.get(1);
                }
            } else {
                App.Current.showError(getContext(), r.Error);
            }
        }
        
        if (row != null) {
            if (this.LineNumCell != null) {
                this.LineNumCell.setContentText(row.getValue("LineNum").toString());
                this.LineNumCell.setTag(row);
            }
            
            if (this.ItemCodeCell != null) {
                this.ItemCodeCell.setContentText(row.getValue("ItemCode", ""));
            }
            
            if (this.BaseDocCell != null) {
                Object baseType = row.getValue("BaseType");
                Object baseEntry = row.getValue("BaseEntry");
                
                Collection<DocType> types = this.getBaseTypes().values();
                if (baseType != null && baseEntry != null) {
                    for (DocType type : types) {
                        if (baseType.equals(type)) {
                            this.BaseDocCell.setContentText(type.TypeName + baseEntry.toString());
                            this.BaseDocCell.setTag(type);
                            this.BaseDocCell.TextBox.setTag(baseEntry);
                            break;
                        }
                    }
                }
            }
            
            if (this.BaseLineCell != null) {
                Object baseLine = row.getValue("BaseLine");
                if (baseLine != null) {
                    this.BaseLineCell.setContentText(baseLine.toString());
                    this.BaseLineCell.setTag(baseLine);
                }
            }
            
            if (this.ItemNameCell != null) {
                this.ItemNameCell.setContentText(row.getValue("Dscription", ""));
            }
            
            if (this.QuantityCell != null) {
                this.QuantityCell.setContentText(App.formatNumber(row.getValue("Quantity"), "0.##"));
            }
            
//            if (this.PriceCell != null) {
//                this.PriceCell.setContentText(App.formatNumber(row.getValue("UnitPrice"), "0.##"));
//            }
            
            if (this.WhsCodeCell != null) {
                this.WhsCodeCell.setContentText(row.getValue("WhsName", ""));
            }
            
            if (this.BatchNumCell != null) {
                this.BatchNumCell.setContentText(row.getValue("BatchNum", ""));
            }
            
            if (this.FreeTextCell != null) {
                this.FreeTextCell.setContentText(row.getValue("FreeTxt", ""));
            }
        } else {
            this.LineNum = -1;
            this.LineRow = null;
        }
        
        if (this.BatchAdapter != null) {
            this.BatchAdapter.DataTable = batchTable;
            this.BatchAdapter.notifyDataSetChanged();
        }
    }

    public void refreshMultiLines()
    {
        if (this.DocEntry < 0) return;
        
        String table = this.getDocType().LineTable;
        int docEntry = (Integer)this.Parameters.get("DocEntry");
        int top = this.LinesAdapter.PageSize*(this.LinesAdapter.PageIndex+1);
        int start = (this.LinesAdapter.PageSize*this.LinesAdapter.PageIndex)+1;
        int end = this.LinesAdapter.PageSize*(this.LinesAdapter.PageIndex+1);
        String sql = "with temp as (select top (?) ROW_NUMBER() over (order by LineNum) Number,LineNum,"+ table +".ItemCode,Dscription,Quantity,PriceBefDi UnitPrice,FreeTxt,OWHS.WhsCode,OWHS.WhsName,ManSerNum,ManBtchNum,ManOutOnly from " + table + " "
                   + "inner join OWHS on OWHS.WhsCode=" + table + ".WhsCode inner join OITM on OITM.ItemCode=" + table + ".ItemCode where " + table + ".DocEntry=?) select * from temp where Number>=? and Number<=?";
        Parameters p = new Parameters().add(1,top).add(2, docEntry).add(3,start).add(4,end);
        Result<DataTable> r = App.Current.DbPortal.ExecuteDataTable(this.Connector, sql, p);
        if (r.Value != null && r.Value.Rows.size() > 0) {
            if (this.LinesAdapter.DataTable != null) {
                for (DataRow row : r.Value.Rows) {
                    this.LinesAdapter.DataTable.Rows.add(row);
                }
            } else {
                this.LinesAdapter.DataTable = r.Value;
            }
            
            this.LinesAdapter.PageIndex += 1;
            this.LinesAdapter.notifyDataSetChanged();
            
        }  else if (r.HasError) {
            App.Current.showError(getContext(), r.Error);
        }
        
        this.LinesAdapter.notifyDataSetChanged();
    }
    
    
    
    public boolean checkMain()
    {
        if (this.CardCodeCell != null) {
            String cardCode = this.CardCodeCell.TextBox.getText().toString();
            if (cardCode == null || cardCode.length() == 0) {
                App.Current.showWarning(getContext(), "请先选择业务伙伴。");
                this.CardCodeCell.TextBox.requestFocus();
                return false;
            }
        }
        return true;
    }
    
    public boolean checkLine()
    {
        if (this.ItemCodeCell != null) {
            String itemCode = this.ItemCodeCell.TextBox.getText().toString();
            if (itemCode == null || itemCode.length() == 0) {
                App.Current.showWarning(getContext(), "请先选择物料编号。");
                this.ItemCodeCell.TextBox.requestFocus();
                return false;
            }
        }
        
        if (this.WhsCodeCell != null) {
            String whscode = this.WhsCodeCell.TextBox.getText().toString();
            if (whscode == null || whscode.length() == 0) {
                App.Current.showWarning(getContext(), "请先选择仓库。");
                this.WhsCodeCell.TextBox.requestFocus();
                return false;
            }
        }
        
        if (this.QuantityCell != null) {
            String qty = this.QuantityCell.TextBox.getText().toString();
            if (App.parseDecimal(qty, BigDecimal.ZERO) == BigDecimal.ZERO) {
                App.Current.showWarning(getContext(), "请输入有效数量。");
                this.QuantityCell.TextBox.requestFocus();
                return false;
            }
        }

        return true;
    }
    
    public DocType getDocType()
    {
        return null;
    }
    
    public Map<Integer, DocType> getBaseTypes()
    {
        return null;
    }
    
    public PageTableAdapter BaseDocAdapter;
    public Dialog BaseDocDialog;
    
    public void chooseBaseDoc()
    {
        if (this.BaseDocAdapter == null) {
            this.BaseDocAdapter = new PageTableAdapter(getContext()) {
                @Override
                public View getView(int position, View convertView, ViewGroup parent) {
                    if (position < odoc_editor.this.BaseDocAdapter.DataTable.Rows.size()) {
                        DataRow row = (DataRow)odoc_editor.this.BaseDocAdapter.getItem(position);
                        if (convertView == odoc_editor.this.BaseDocAdapter.Footer) 
                            convertView = null;
                        
                        if (convertView == null) {
                            convertView = App.Current.Workbench.getLayoutInflater().inflate(R.layout.odoc_base, null);
                            ImageView icon = (ImageView)convertView.findViewById(R.id.icon);
                            icon.setImageBitmap(App.Current.ResourceManager.getImage("@/sbo_odoc_gray"));
                        }

                        TextView num = (TextView)convertView.findViewById(R.id.num);
                        TextView docnum = (TextView)convertView.findViewById(R.id.txtDocNum);
                        TextView date = (TextView)convertView.findViewById(R.id.txtDocDate);
                        TextView card = (TextView)convertView.findViewById(R.id.txtCardCode);
                        TextView name = (TextView)convertView.findViewById(R.id.txtCardName);
                        TextView user = (TextView)convertView.findViewById(R.id.txtUserName);
                        TextView amount = (TextView)convertView.findViewById(R.id.txtDocTotal);
                        
                        num.setText(String.valueOf(position + 1));
                        DocType baseType = row.getValue("BaseType", DocType.class);
                        docnum.setText(baseType.TypeName + " " + row.getValue("DocNum").toString());
                        date.setText(App.formatDateTime(row.getValue("DocDate"), "yyyy-MM-dd"));
                        card.setText(row.getValue("CardCode", ""));
                        name.setText(row.getValue("CardName", ""));
                        user.setText(row.getValue("U_NAME", ""));
                        amount.setText(App.formatNumber(row.getValue("DocTotal"), "0.##"));

                        return convertView;
                    } else {
                        return odoc_editor.this.BaseDocAdapter.Footer;
                    }
                }
            };
        }
        
        this.BaseDocAdapter.DataTable = null;
        this.BaseDocAdapter.PageIndex = 0;
        this.BaseDocAdapter.PageSize = 10;
        this.refreshBaseDoc();
        
        if (this.BaseDocAdapter.DataTable != null && this.BaseDocAdapter.DataTable.Rows.size() > 0) {
            if (this.BaseDocAdapter.DataTable.Rows.size() > 1) {
                if (this.BaseDocDialog == null) {
                    this.BaseDocDialog = new AlertDialog.Builder(getContext())
                    .setTitle("选择基础单据")
                    .setSingleChoiceItems(this.BaseDocAdapter, 0, new DialogInterface.OnClickListener(){
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if (which < odoc_editor.this.BaseDocAdapter.DataTable.Rows.size()) {
                                DataRow row = (DataRow)((AlertDialog)dialog).getListView().getAdapter().getItem(which);
                                if (row != null) {
                                    odoc_editor.this.showBaseDoc(row);
                                }
                                dialog.dismiss();
                            } else {
                                odoc_editor.this.refreshBaseDoc();
                                odoc_editor.this.BaseDocAdapter.notifyDataSetChanged();
                            }
                        }
                    }).setNegativeButton("取消", null).create();
                }
                
                this.BaseDocAdapter.notifyDataSetChanged();
                this.BaseDocDialog.show();
            } else {
                this.showBaseDoc(this.BaseDocAdapter.DataTable.Rows.get(0));
            }
        }
    }
    
    public void showBaseDoc(final DataRow row)
    {
        final DocType baseType = row.getValue("BaseType", DocType.class);
        Integer baseEntry = (Integer)row.getValue("DocEntry");
        
        if (this.BaseDocCell.TextBox.getTag() != null) {
            DocType type = (DocType)this.BaseDocCell.getTag();
            int entry = (Integer)this.BaseDocCell.TextBox.getTag();
            
            if (type.TypeID != baseType.TypeID || entry != baseEntry) {
                App.Current.question(getContext(), "选择不同的来源单据，将会清空已输入的所有行，确定要继续吗？", new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (odoc_editor.this.LinesAdapter != null) {
                            odoc_editor.this.LinesAdapter.DataTable = null;
                            odoc_editor.this.LinesAdapter.notifyDataSetChanged();
                        }
                        odoc_editor.this.resetLine();
                        
                        odoc_editor.this.BaseDocCell.setContentText(baseType.TypeName + " " + row.getValue("DocNum").toString());
                        odoc_editor.this.BaseDocCell.setTag(baseType);
                        odoc_editor.this.BaseDocCell.TextBox.setTag(row.getValue("DocEntry"));
                        odoc_editor.this.CardCodeCell.setContentText(row.getValue("CardCode", ""));
                        odoc_editor.this.CardNameCell.setContentText(row.getValue("CardName", ""));
                        odoc_editor.this.ItemCodeCell.TextBox.requestFocus();
                    }
                });
            }
        } else {
            odoc_editor.this.BaseDocCell.setContentText(baseType.TypeName + " " + row.getValue("DocNum").toString());
            odoc_editor.this.BaseDocCell.setTag(baseType);
            odoc_editor.this.BaseDocCell.TextBox.setTag(row.getValue("DocEntry"));
            odoc_editor.this.CardCodeCell.setContentText(row.getValue("CardCode", ""));
            odoc_editor.this.CardNameCell.setContentText(row.getValue("CardName", ""));
            odoc_editor.this.ItemCodeCell.TextBox.requestFocus();
        }
    }
    
    public void refreshBaseDoc()
    {
        //基础单据一般有多种，例如采购收货的基础单据有采购订单，预留发票，采购退货
        //这里将所有基础单据类型混合在一起进行查询
        
        String txt = this.CardCodeCell.TextBox.getText().toString();
        Map<Integer, DocType> baseTypes = this.getBaseTypes();
        if (baseTypes == null) return;
        
        int top = this.BaseDocAdapter.PageSize*(this.BaseDocAdapter.PageIndex+1);
        int start = (this.BaseDocAdapter.PageSize*this.BaseDocAdapter.PageIndex)+1;
        int end = this.BaseDocAdapter.PageSize*(this.BaseDocAdapter.PageIndex+1);
        
        int paraIndex = 2;
        String sql = "";
        Parameters p = new Parameters();
        for (Integer index : baseTypes.keySet()) {
            DocType baseType = baseTypes.get(index);
            if (sql.length() > 0) {
                sql += " union all ";
            }
            sql += "select DocEntry,DocNum,CardCode,CardName,DocDate,DocTotal,UserSign,Comments," + String.valueOf(index) + " TypeIndex from " + baseType.MainTable + " where DocStatus='O' and (CardCode like '%'+?+'%' or CardName like '%'+?+'%')";
            
            //sql中两个like语句对应的参数
            p.add(paraIndex++, txt).add(paraIndex++, txt);
        }
        
        if (sql.length() > 0) {
            sql = "with temp as ( select top (?) ROW_NUMBER() over (order by TypeIndex,DocEntry desc) Number,* from (" + sql + ") T) select temp.*,OUSR.U_NAME from temp left join OUSR on OUSR.USERID=temp.UserSign where Number>=? and Number<=?";
        }
        p.add(1, top).add(paraIndex++,start).add(paraIndex++,end);
        Result<DataTable> r = App.Current.DbPortal.ExecuteDataTable(this.Connector, sql, p);
        if (r.Value != null && r.Value.Rows.size() > 0) {
            for (DataRow row : r.Value.Rows) {
                int baseIndex = row.getValue("TypeIndex", Integer.class);
                row.setValue("BaseType", baseTypes.get(baseIndex));
            }
            
            if (this.BaseDocAdapter.DataTable != null) {
                for (DataRow row : r.Value.Rows) {
                    this.BaseDocAdapter.DataTable.Rows.add(row);
                }
            } else {
                this.BaseDocAdapter.DataTable = r.Value;
            }
            
            this.BaseDocAdapter.PageIndex += 1;
        } else if (r.HasError) {
            App.Current.showError(getContext(), r.Error);
        }
    }
    
    public PageTableAdapter CardCodeAdapter;
    public Dialog CardCodeDialog;
    
    public void chooseCardCode()
    {
        if (this.CardCodeAdapter == null) {
            this.CardCodeAdapter = new PageTableAdapter(getContext()) {
                @Override
                public View getView(int position, View convertView, ViewGroup parent) {
                    if (position < odoc_editor.this.CardCodeAdapter.DataTable.Rows.size()) {
                        if (convertView == odoc_editor.this.CardCodeAdapter.Footer) 
                            convertView = null;
                        
                        DataRow row = (DataRow)odoc_editor.this.CardCodeAdapter.getItem(position);
                        if (convertView == null) {
                            convertView = App.Current.Workbench.getLayoutInflater().inflate(R.layout.odoc_ocrd, null);
                            ImageView icon = (ImageView)convertView.findViewById(R.id.icon);
                            icon.setImageBitmap(App.Current.ResourceManager.getImage("@/sbo_ocrd_gray"));
                        }

                        TextView num = (TextView)convertView.findViewById(R.id.num);
                        TextView code = (TextView)convertView.findViewById(R.id.txtCode);
                        TextView qty = (TextView)convertView.findViewById(R.id.txtCntct);
                        TextView name = (TextView)convertView.findViewById(R.id.txtName);
                        TextView addr = (TextView)convertView.findViewById(R.id.txtAddress);
                        
                        num.setText(String.valueOf(position + 1));
                        code.setText(row.getValue("CardCode", ""));
                        qty.setText(row.getValue("CntctPrsn", ""));
                        name.setText(row.getValue("CardName", ""));
                        addr.setText(row.getValue("Address", ""));
                        
                        return convertView;
                    } else {
                        return odoc_editor.this.CardCodeAdapter.Footer;
                    }
                }
            };
        }
        
        this.CardCodeAdapter.DataTable = null;
        this.CardCodeAdapter.PageIndex = 0;
        this.CardCodeAdapter.PageSize = 10;
        this.refreshCardCode();
        
        if (this.CardCodeAdapter.DataTable != null && this.CardCodeAdapter.DataTable.Rows.size() > 0) {
            if (this.CardCodeAdapter.DataTable.Rows.size() > 1) {
                if (this.CardCodeDialog == null) {
                    this.CardCodeDialog = new AlertDialog.Builder(getContext())
                    .setTitle("选择业务伙伴")
                    .setSingleChoiceItems(this.CardCodeAdapter, 0, new DialogInterface.OnClickListener(){
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if (which < odoc_editor.this.CardCodeAdapter.DataTable.Rows.size()) {
                                DataRow row = (DataRow)((AlertDialog)dialog).getListView().getAdapter().getItem(which);
                                if (row != null) {
                                    odoc_editor.this.showCardCode(row);
                                }
                                dialog.dismiss();
                            } else {
                                odoc_editor.this.refreshCardCode();
                                odoc_editor.this.CardCodeAdapter.notifyDataSetChanged();
                            }
                        }
                    })
                    .setNegativeButton("取消", null).create();
                }

                this.CardCodeAdapter.notifyDataSetChanged();
                this.CardCodeDialog.show();
            } else {
                this.showCardCode(this.CardCodeAdapter.DataTable.Rows.get(0));
            }
        }
    }
    
    public void showCardCode(DataRow row)
    {
        this.CardCodeCell.setContentText(row.getValue("CardCode", ""));
        this.CardNameCell.setContentText(row.getValue("CardName", ""));
        odoc_editor.this.ItemCodeCell.TextBox.requestFocus();
    }
    
    public void refreshCardCode()
    {
        String cardType = this.getDocType().CardType;
        String txt = this.CardCodeCell.TextBox.getText().toString();
        int top = this.CardCodeAdapter.PageSize*(this.CardCodeAdapter.PageIndex+1);
        int start = (this.CardCodeAdapter.PageSize*this.CardCodeAdapter.PageIndex)+1;
        int end = this.CardCodeAdapter.PageSize*(this.CardCodeAdapter.PageIndex+1);
        
        String sql = "with temp as (select top (?) ROW_NUMBER() over (order by CardCode) Number,CardCode,CardName,CntctPrsn,Address from OCRD where CardType='" + cardType + "' and (CardCode like '%'+?+'%' or CardName like '%'+?+'%')) select * from temp where Number>=? and Number<=?";
        Parameters p = new Parameters().add(1,top).add(2, txt).add(3, txt).add(4,start).add(5,end);
        Result<DataTable> r = App.Current.DbPortal.ExecuteDataTable(this.Connector, sql, p);
        if (r.Value != null && r.Value.Rows.size() > 0) {
            if (this.CardCodeAdapter.DataTable != null) {
                for (DataRow row : r.Value.Rows) {
                    this.CardCodeAdapter.DataTable.Rows.add(row);
                }
            } else {
                this.CardCodeAdapter.DataTable = r.Value;
            }
            
            this.CardCodeAdapter.PageIndex += 1;
        } else if (r.HasError) {
            App.Current.showError(getContext(), r.Error);
        }
    }
    
    public PageTableAdapter ItemCodeAdapter;
    public Dialog ItemCodeDialog;
    
    public void chooseItemCode()
    {
        if (this.ItemCodeAdapter == null) {
            this.ItemCodeAdapter = new PageTableAdapter(getContext()) {
                @Override
                public View getView(int position, View convertView, ViewGroup parent) {
                    if (position < odoc_editor.this.ItemCodeAdapter.DataTable.Rows.size()) {
                        if (convertView == odoc_editor.this.ItemCodeAdapter.Footer) 
                            convertView = null;
                        
                        DataRow row = (DataRow)odoc_editor.this.ItemCodeAdapter.getItem(position);
                        if (convertView == null) {
                            convertView = App.Current.Workbench.getLayoutInflater().inflate(R.layout.odoc_oitm, null);
                            ImageView icon = (ImageView)convertView.findViewById(R.id.icon);
                            icon.setImageBitmap(App.Current.ResourceManager.getImage("@/sbo_oitm_gray"));
                        }

                        TextView num = (TextView)convertView.findViewById(R.id.num);
                        TextView code = (TextView)convertView.findViewById(R.id.txtCode);
                        TextView qty = (TextView)convertView.findViewById(R.id.txtQty);
                        TextView name = (TextView)convertView.findViewById(R.id.txtName);
                        
                        num.setText(String.valueOf(position + 1));
                        code.setText(row.getValue("ItemCode", String.class));
                        qty.setText(row.getValue("Quantity").toString());
                        name.setText(row.getValue("ItemName", ""));

                        return convertView;
                    } else {
                        return odoc_editor.this.ItemCodeAdapter.Footer;
                    }
                }
            };
        }
        
        this.ItemCodeAdapter.DataTable = null;
        this.ItemCodeAdapter.PageIndex = 0;
        this.ItemCodeAdapter.PageSize = 10;
        this.refreshItemCode();
        
        if (this.ItemCodeAdapter.DataTable != null && this.ItemCodeAdapter.DataTable.Rows.size() > 0) {
            if (this.ItemCodeAdapter.DataTable.Rows.size() > 1) {
                if (this.ItemCodeDialog == null) {
                    this.ItemCodeDialog = new AlertDialog.Builder(getContext())
                    .setTitle("选择物料")
                    .setSingleChoiceItems(this.ItemCodeAdapter, 0, new DialogInterface.OnClickListener(){
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if (which < odoc_editor.this.ItemCodeAdapter.DataTable.Rows.size()) {
                                DataRow row = (DataRow)((AlertDialog)dialog).getListView().getAdapter().getItem(which);
                                if (row != null) {
                                    odoc_editor.this.showItemCode(row);
                                }
                                dialog.dismiss();
                            } else {
                                odoc_editor.this.refreshItemCode();
                                odoc_editor.this.ItemCodeAdapter.notifyDataSetChanged();
                            }
                        }
                    })
                    .setNegativeButton("取消", null).create();
                }

                this.ItemCodeAdapter.notifyDataSetChanged();
                this.ItemCodeDialog.show();
            } else {
                this.showItemCode(this.ItemCodeAdapter.DataTable.Rows.get(0));
            }
        }
    }
    
    public void showItemCode(DataRow row)
    {
        if (this.LineNum == -1) {
            this.LineRow = row;
        }
        
        if (this.BaseLineCell != null) {
            Object baseLine = row.getValue("LineNum");
            if (baseLine != null) {
                this.BaseLineCell.setContentText(baseLine.toString());
                this.BaseLineCell.setTag(baseLine);
            } else {
                this.BaseLineCell.setContentText("");
                this.BaseLineCell.setTag(null);
            }
        }
        
        this.ItemCodeCell.setContentText(row.getValue("ItemCode", ""));
        this.ItemNameCell.setContentText(row.getValue("ItemName", ""));
        this.WhsCodeCell.setContentText(row.getValue("WhsName", ""));
        this.WhsCodeCell.setTag(row.getValue("WhsCode"));
        
        String manSerial = row.getValue("ManSerNum", "N");
        if (manSerial.equals("Y")) {
            this.QuantityCell.setContentText("1");
            this.BatchNumCell.TextBox.requestFocus();
        } else {
            this.QuantityCell.setContentText(App.formatNumber(row.getValue("Quantity"), "0.##"));
            this.QuantityCell.TextBox.requestFocus();
        }
        
        if (this.PriceCell != null) {
            this.showItemPrice();
        }
    }
    
    public void showItemPrice()
    {
        String sql = "declare @currency nvarchar(3) \r\n"
                + "declare @price decimal(19,6) \r\n"
                + "declare @cardCode nvarchar(50) \r\n"
                + "declare @itemCode nvarchar(50) \r\n"
                + "declare @disc decimal(19,6) \r\n"
                + "select @currency=isnull(?,SysCurrncy) from OADM \r\n"
                + "select @cardCode=CardCode from OCRD where CardCode=? \r\n"
            //"--价格清单\r\n"
                + "select @price=Price,@itemCode=ITM1.ItemCode from ITM1 \r\n"
                + "left join OCRD on OCRD.ListNum=ITM1.PriceList \r\n"
                + "where ITM1.ItemCode=? and OCRD.CardCode=@cardCode and ITM1.Currency=@currency \r\n"
            //"--业务伙伴无关的特殊价格 \r\n"
                + "select @price=ISNULL(Price,@price) from OSPP where CardCode=N'*1' and ItemCode=@itemCode and Currency=@currency \r\n"
                + "select @price=ISNULL(Price,@price) from SPP1 where CardCode=N'*1' and ItemCode=@itemCode and Currency=@currency and GETDATE()>=FromDate and GETDATE()<=ToDate \r\n"
                + "select top 1 @price=ISNULL(SPP2.Price,@price) from SPP2 inner join SPP1 on SPP1.CardCode=SPP2.CardCode and SPP1.ItemCode=SPP2.ItemCode and SPP1.LINENUM=SPP2.SPP1LNum \r\n"
                + "where SPP2.CardCode=N'*1' and SPP2.ItemCode=@itemCode and SPP2.Currency=@currency and GETDATE()>=FromDate and GETDATE()<=ToDate \r\n"
                + "order by SPP2.Amount desc \r\n"
            //"--业务伙伴相关的特殊价格 \r\n"
                + "select @price=ISNULL(Price,@price) from OSPP where CardCode=@cardCode and ItemCode=@itemCode and Currency=@currency \r\n"
                + "select @price=ISNULL(Price,@price) from SPP1 where CardCode=@cardCode and ItemCode=@itemCode and Currency=@currency and GETDATE()>=FromDate and GETDATE()<=ToDate \r\n"
                + "select top 1 @price=ISNULL(SPP2.Price,@price) from SPP2 inner join SPP1 on SPP1.CardCode=SPP2.CardCode and SPP1.ItemCode=SPP2.ItemCode and SPP1.LINENUM=SPP2.SPP1LNum \r\n"
                + "where SPP2.CardCode=@cardCode and SPP2.ItemCode=@itemCode and SPP2.Currency=@currency and GETDATE()>=FromDate and GETDATE()<=ToDate \r\n"
                + "order by SPP2.Amount desc \r\n"
            //折扣组
                + "declare @objType nvarchar(20) \r\n"
                + "select @objType=ObjType from OSPG where CardCode=@cardCode \r\n"
                + "if (@objType=52) \r\n"
                + "    select @disc=ISNULL(MIN(Discount),0) from OSPG left join OITM on OSPG.ObjKey=OITM.ItmsGrpCod \r\n"
                + "    where OSPG.CardCode=@cardCode and OITM.ItemCode=@itemCode \r\n"
                + "else if (@objType=43) \r\n"
                + "    select @disc=ISNULL(MIN(Discount),0) from OSPG left join OITM on OSPG.ObjKey=OITM.FirmCode \r\n"
                + "    where OSPG.CardCode=@cardCode and OITM.ItemCode=@itemCode \r\n"
                + "else if (@objType=8) \r\n"
                + "    select @disc=ISNULL(MIN(Discount),0) from OSPG \r\n"
                + "    left join (select ItemCode,Property,Value from OITM unpivot ( \r\n"
                + "                Value for Property in (QryGroup1,QryGroup2,QryGroup3,QryGroup4,QryGroup5,QryGroup6,QryGroup7,QryGroup8, \r\n"
                + "                QryGroup9,QryGroup10,QryGroup11,QryGroup12,QryGroup13,QryGroup14,QryGroup15,QryGroup16, \r\n"
                + "                QryGroup17,QryGroup18,QryGroup19,QryGroup20,QryGroup21,QryGroup22,QryGroup23,QryGroup24, \r\n"
                + "                QryGroup25,QryGroup26,QryGroup27,QryGroup28,QryGroup29,QryGroup30,QryGroup31,QryGroup32, \r\n"
                + "                QryGroup33,QryGroup34,QryGroup35,QryGroup36,QryGroup37,QryGroup38,QryGroup39,QryGroup40, \r\n"
                + "                QryGroup41,QryGroup42,QryGroup43,QryGroup44,QryGroup45,QryGroup46,QryGroup47,QryGroup48, \r\n"
                + "                QryGroup49,QryGroup50,QryGroup51,QryGroup52,QryGroup53,QryGroup54,QryGroup55,QryGroup56, \r\n"
                + "                QryGroup57,QryGroup58,QryGroup59,QryGroup60,QryGroup61,QryGroup62,QryGroup63,QryGroup64) \r\n"
                + "                ) as un where ItemCode=@itemCode) T on OSPG.ObjType=8 and OSPG.ObjKey=RIGHT(T.Property,LEN(T.Property)-8) and T.Value=N'Y' \r\n"
                + "    where OSPG.CardCode=@cardCode and T.ItemCode=@itemCode \r\n"
                + "set @disc=ISNULL(@disc,0) \r\n"
            //返回
                + "select ItemName,isnull(@price,0) UnitPrice,isnull(@disc,0) DiscPrcnt,VatGourpSa VatGroup,OVTG.Name VatName,OVTG.Rate VatRate,OWHS.WhsCode,OWHS.WhsName,ManBtchNum,ManSerNum,ManOutOnly "
                + "from OITM left join OVTG on OVTG.Code=OITM.VatGourpSa left join OWHS on OWHS.WhsCode=OITM.DfltWH where ItemCode=@itemCode";

        String cardCode = this.CardCodeCell.getContentText();
        String currency = "RMB";
        String itemCode = this.ItemCodeCell.getContentText();
        
        Parameters p = new Parameters().add(1, currency).add(2, cardCode).add(3, itemCode);
        Result<DataRow> r = App.Current.DbPortal.ExecuteRecord(this.Connector, sql, p);
        if (r.Value != null) {
            Object o = r.Value.getValue("UnitPrice");
            if (o != null && o.toString().equals("0")==false) {
                this.PriceCell.setContentText(o.toString());
            }
        }
    }
    
    public void refreshItemCode()
    {
        String txt = this.ItemCodeCell.TextBox.getText().toString();
        int top = this.ItemCodeAdapter.PageSize*(this.ItemCodeAdapter.PageIndex+1);
        int start = (this.ItemCodeAdapter.PageSize*this.ItemCodeAdapter.PageIndex)+1;
        int end = this.ItemCodeAdapter.PageSize*(this.ItemCodeAdapter.PageIndex+1);
        
        String sql = "";
        Parameters p = new Parameters();
        if (this.BaseDocCell == null || this.BaseDocCell.getTag() == null) {
            sql = "with temp as (select top (?) ROW_NUMBER() over (order by ItemCode) Number,null LineNum,ItemCode,ItemName,1.0 Quantity,OWHS.WhsCode,OWHS.WhsName,ManSerNum,ManBtchNum,ManOutOnly from OITM left join OWHS on OWHS.WhsCode=OITM.DfltWH where (ItemCode like '%'+?+'%' or ItemName like '%'+?+'%')) select * from temp where Number>=? and Number<=?";
            p.add(1,top).add(2, txt).add(3, txt).add(4,start).add(5,end);
        } else {
            DocType baseType = (DocType)this.BaseDocCell.getTag();
            int baseEntry = (Integer)this.BaseDocCell.TextBox.getTag();
            String line = baseType.LineTable;
            sql = "with temp as (select top (?) ROW_NUMBER() over (order by " + line + ".DocEntry desc) Number,LineNum,"+ line +".ItemCode,Dscription ItemName,OpenQty Quantity,OWHS.WhsCode,OWHS.WhsName,ManSerNum,ManBtchNum,ManOutOnly from " + line + " "
                + "inner join OITM on OITM.ItemCode=" + line + ".ItemCode "
                + "inner join OWHS on OWHS.WhsCode=" + line + ".WhsCode "
                + "where " + line + ".DocEntry=? and LineStatus='O' and (" + line + ".ItemCode like '%'+?+'%' or Dscription like '%'+?+'%')) select * from temp where Number>=? and Number<=?";
            p.add(1, top).add(2, baseEntry).add(3, txt).add(4, txt).add(5,start).add(6,end);
        }
        
        Result<DataTable> r = App.Current.DbPortal.ExecuteDataTable(this.Connector, sql, p);
        if (r.Value != null && r.Value.Rows.size() > 0) {
            if (this.ItemCodeAdapter.DataTable != null) {
                for (DataRow row : r.Value.Rows) {
                    this.ItemCodeAdapter.DataTable.Rows.add(row);
                }
            } else {
                this.ItemCodeAdapter.DataTable = r.Value;
            }
            
            this.ItemCodeAdapter.PageIndex += 1;
        } else if (r.HasError) {
            App.Current.showError(getContext(), r.Error);
        }
    }
    
    public PageTableAdapter WhsCodeAdapter;
    public Dialog WhsCodeDialog;
    
    public void chooseWhsCode()
    {
        if (this.WhsCodeAdapter == null) {
            this.WhsCodeAdapter = new PageTableAdapter(getContext()) {
                @Override
                public View getView(int position, View convertView, ViewGroup parent) {
                    if (position < odoc_editor.this.WhsCodeAdapter.DataTable.Rows.size()) {
                        if (convertView == odoc_editor.this.WhsCodeAdapter.Footer) 
                            convertView = null;
                        
                        DataRow row = (DataRow)odoc_editor.this.WhsCodeAdapter.getItem(position);
                        if (convertView == null) {
                            convertView = App.Current.Workbench.getLayoutInflater().inflate(R.layout.odoc_owhs, null);
                            ImageView icon = (ImageView)convertView.findViewById(R.id.icon);
                            icon.setImageBitmap(App.Current.ResourceManager.getImage("@/sbo_owhs_gray"));
                        }

                        TextView num = (TextView)convertView.findViewById(R.id.num);
                        TextView code = (TextView)convertView.findViewById(R.id.txtWhsCode);
                        TextView name = (TextView)convertView.findViewById(R.id.txtWhsName);
                        
                        num.setText(String.valueOf(position + 1));
                        code.setText(row.getValue("WhsCode", ""));
                        name.setText(row.getValue("WhsName", ""));

                        return convertView;
                    } else {
                        return odoc_editor.this.WhsCodeAdapter.Footer;
                    }
                }
            };
        }
        
        this.WhsCodeAdapter.DataTable = null;
        this.WhsCodeAdapter.PageIndex = 0;
        this.WhsCodeAdapter.PageSize = 10;
        this.refreshWhsCode();
        
        if (this.WhsCodeAdapter.DataTable.Rows.size() > 0) {
            if (this.WhsCodeAdapter.DataTable.Rows.size() > 1) {
                if (this.WhsCodeDialog == null) {
                    this.WhsCodeDialog = new AlertDialog.Builder(getContext())
                    .setTitle("选择仓库")
                    .setSingleChoiceItems(this.WhsCodeAdapter, 0, new DialogInterface.OnClickListener(){
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if (which < odoc_editor.this.WhsCodeAdapter.DataTable.Rows.size()) {
                                DataRow row = (DataRow)((AlertDialog)dialog).getListView().getAdapter().getItem(which);
                                if (row != null) {
                                    odoc_editor.this.showWhsCode(row);
                                }
                                dialog.dismiss();
                            } else {
                                odoc_editor.this.refreshWhsCode();
                                odoc_editor.this.WhsCodeAdapter.notifyDataSetChanged();
                            }
                        }
                    })
                    .setNegativeButton("取消", null).create();
                }

                this.WhsCodeAdapter.notifyDataSetChanged();
                this.WhsCodeDialog.show();
            } else {
                this.showWhsCode(this.WhsCodeAdapter.DataTable.Rows.get(0));
            }
        }
    }
    
    public void showWhsCode(DataRow row)
    {
        odoc_editor.this.WhsCodeCell.setContentText(row.getValue("WhsName", ""));
        odoc_editor.this.WhsCodeCell.setTag(row.getValue("WhsCode"));
    }
    
    public void refreshWhsCode()
    {
        int top = this.WhsCodeAdapter.PageSize*(this.WhsCodeAdapter.PageIndex+1);
        int start = (this.WhsCodeAdapter.PageSize*this.WhsCodeAdapter.PageIndex)+1;
        int end = this.WhsCodeAdapter.PageSize*(this.WhsCodeAdapter.PageIndex+1);
        
        String sql = "with temp as (select top (?) ROW_NUMBER() over (order by WhsCode) Number,WhsCode,WhsName from OWHS) select * from temp where Number>=? and Number<=?";
        Parameters p = new Parameters().add(1,top).add(2,start).add(3,end);
        Result<DataTable> r = App.Current.DbPortal.ExecuteDataTable(this.Connector, sql, p);
        if (r.Value != null && r.Value.Rows.size() > 0) {
            if (this.WhsCodeAdapter.DataTable != null) {
                for (DataRow row : r.Value.Rows) {
                    this.WhsCodeAdapter.DataTable.Rows.add(row);
                }
            } else {
                this.WhsCodeAdapter.DataTable = r.Value;
            }
            
            this.WhsCodeAdapter.PageIndex += 1;
        } else if (r.HasError) {
            App.Current.showError(getContext(), r.Error);
        }
    }
    
    @Override
    public void delete()
    {
        if (this.DocEntry < 0) {
            DataRow row = (DataRow)this.LineNumCell.getTag();
            if (row != null) {
                this.LinesAdapter.DataTable.Rows.remove(row);
                this.LinesAdapter.notifyDataSetChanged();
                this.resetLine();
            }
        }
    }
    
    @Override
    public void save()
    {
        if (this.DocEntry < 0) {
            if (this.checkMain() && this.checkLine()) {
                
                if (this.LinesAdapter.DataTable == null) {
                    this.LinesAdapter.DataTable = new DataTable();
                }
                
                if (this.LineNum == -1) {
                    int index = this.LinesAdapter.DataTable.Rows.size();
                    if (index > 0) {
                        index = this.LinesAdapter.DataTable.Rows.get(index-1).getValue("LineNum", Integer.class) + 1;
                    }
                    
                    this.LineNum = index;
                    this.LineRow.setValue("LineNum", index);
                    this.LinesAdapter.DataTable.Rows.add(this.LineRow);
                    this.LinesAdapter.notifyDataSetChanged();
                }
               
                if (this.BaseDocCell != null
                    && this.BaseDocCell.getTag() != null 
                    && this.BaseDocCell.TextBox.getTag() != null) {
                    
                    DocType baseDoc = (DocType)this.BaseDocCell.getTag();
                    this.LineRow.setValue("BaseType", baseDoc.TypeID);
                    this.LineRow.setValue("BaseEntry", this.BaseDocCell.TextBox.getTag());
                    
                }
                
                if (this.BaseLineCell != null) {
                    this.LineRow.setValue("BaseLine", this.BaseLineCell.getTag());
                }

                if (this.ItemCodeCell != null) {
                    this.LineRow.setValue("ItemCode", this.ItemCodeCell.TextBox.getText().toString());
                }
                if (this.ItemNameCell != null) {
                    this.LineRow.setValue("Dscription", this.ItemNameCell.TextBox.getText().toString());
                }
                if (this.QuantityCell != null) {
                    this.LineRow.setValue("Quantity", App.parseDecimal(this.QuantityCell.TextBox.getText().toString(), BigDecimal.ONE));
                }
                if (this.PriceCell != null) {
                    this.LineRow.setValue("UnitPrice", this.PriceCell.TextBox.getText().toString());
                }
                if (this.WhsCodeCell != null) {
                    this.LineRow.setValue("WhsCode", this.WhsCodeCell.getTag());
                    this.LineRow.setValue("WhsName", this.WhsCodeCell.TextBox.getText().toString());
                }
                if (this.BatchNumCell != null) {
                    this.LineRow.setValue("BatchNum", this.BatchNumCell.TextBox.getText().toString());
                }
                if (this.FreeTextCell != null) {
                    this.LineRow.setValue("FreeTxt", this.FreeTextCell.TextBox.getText().toString());
                }
                
                this.resetLine();
            }
        }
    }
    
    public void resetMain()
    {
        this.DocEntry = -1;
        this.Parameters.remove("DocEntry");
        this.Parameters.add("DocEntry", this.DocEntry);
        
        if (this.DocNumCell != null) {
            this.DocNumCell.setContentText("");
        }
        
        if (this.BaseDocCell != null) {
            this.BaseDocCell.setContentText("");
            this.BaseDocCell.setTag(null);
            this.BaseDocCell.TextBox.setTag(null);
        }
        
        if (this.CardCodeCell != null) {
            this.CardCodeCell.setContentText("");
        }
        
        if (this.CardNameCell != null) {
            this.CardNameCell.setContentText("");
        }
        
        if (this.DocDateCell != null) {
            this.DocDateCell.resetDate();
        }
        
        if (this.DueDateCell != null) {
            this.DueDateCell.resetDate();
        }
        
        if (this.CommentCell != null) {
            this.CommentCell.setContentText("");
        }
    }
    
    public void resetLine()
    {
        this.LineNum = -1;
        this.LineRow = null;
        if (this.LineNumCell != null) {
            this.LineNumCell.setTag(null);
            this.LineNumCell.setContentText("");
        }
        
        if (this.BaseLineCell != null) {
            this.BaseLineCell.setContentText("");
            this.BaseLineCell.setTag(null);
        }
        
        if (this.ItemCodeCell != null) {
            this.ItemCodeCell.setContentText("");
        }
        
        if (this.ItemNameCell != null) {
            this.ItemNameCell.setContentText("");
        }
        
        if (this.WhsCodeCell != null) {
            this.WhsCodeCell.setContentText("");
        }
        
        if (this.QuantityCell != null) {
            this.QuantityCell.setContentText("");
        }
        
        if (this.PriceCell != null) {
            this.PriceCell.setContentText("");
        }
        
        if (this.BatchNumCell != null) {
            this.BatchNumCell.setContentText("");
        }
        
        if (this.FreeTextCell != null) {
            this.FreeTextCell.setContentText("");
        }
    }
    
    @Override
    public void commit()
    {
        if(this.DocEntry < 0) {
            if (this.checkMain() == false) return;
            
            if (this.LinesAdapter.DataTable == null) {
                App.Current.showInfo(getContext(), "没有明细行，不能提交。");
                return;
            }
            
            App.Current.question(getContext(), "单据提交后不能修改，确定要提交吗？", new DialogInterface.OnClickListener(){
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    DocType type = odoc_editor.this.getDocType();
                    Document doc = new Document(type);
                    String cardCode = null;
                    String docDate = App.formatCalendar(Calendar.getInstance(), "yyyy-MM-dd");
                    String dueDate = App.formatCalendar(Calendar.getInstance(), "yyyy-MM-dd");
                    String taxDate = App.formatCalendar(Calendar.getInstance(), "yyyy-MM-dd");
                    String comments = null;
                    
                    if (odoc_editor.this.CardCodeCell != null)
                        cardCode = odoc_editor.this.CardCodeCell.TextBox.getText().toString();
                    
                    if (odoc_editor.this.DocDateCell != null)
                        docDate = odoc_editor.this.DocDateCell.TextBox.getText().toString();
                    
                    if (odoc_editor.this.DueDateCell != null)
                        dueDate = odoc_editor.this.DueDateCell.TextBox.getText().toString();
                    
                    if (odoc_editor.this.CommentCell != null)
                        comments = odoc_editor.this.CommentCell.TextBox.getText().toString();
                    
                    odoc_editor.this.setDocBody(doc, cardCode, docDate, dueDate, taxDate, comments);
                    
                    if (odoc_editor.this.LinesAdapter.DataTable != null) {
                        int lienNum = 0;
                        for (DataRow row : odoc_editor.this.LinesAdapter.DataTable.Rows) {
                            String itemCode = row.getValue("ItemCode", "");
                            String baseType = "";
                            String baseEntry = "";
                            String baseLine = "";
                            
                            if (row.getValue("BaseType") != null) baseType = row.getValue("BaseType").toString();
                            if (row.getValue("BaseEntry") != null) baseEntry = row.getValue("BaseEntry").toString();
                            if (row.getValue("BaseLine") != null) baseLine = row.getValue("BaseLine").toString();
                            
                            String quantity = row.getValue("Quantity").toString();
                            String whsCode = row.getValue("WhsCode", "");
                            String freeTxt = row.getValue("FreeTxt", "");
                            String batchNum = row.getValue("BatchNum", "");
                            odoc_editor.this.addDocLine(doc, itemCode, baseType, baseEntry, baseLine, quantity, whsCode, freeTxt);
                            
                            String manSerial = row.getValue("ManSerNum", "N");
                            String manBatch = row.getValue("ManBtchNum", "N");
                            if (manSerial.equals("Y")) {
                                if (batchNum != null && batchNum.length() > 0) {
                                    odoc_editor.this.addDocSerial(doc, String.valueOf(lienNum), batchNum);
                                }
                            } else if (manBatch.equals("Y")) {
                                if (batchNum != null && batchNum.length() > 0) {
                                    odoc_editor.this.addDocBatch(doc, String.valueOf(lienNum), batchNum, quantity);
                                }
                            }
                            
                            lienNum++;
                        }
                    }
                    
                    Result<String> result = doc.save();
                    if (result.Value != null) {
                        App.Current.showInfo(odoc_editor.this.getContext(), "提交成功！单据编号为：" + result.Value);
                        
                        odoc_editor.this.resetMain();
                        odoc_editor.this.resetLine();
                        if (odoc_editor.this.LinesAdapter.DataTable != null) {
                            odoc_editor.this.LinesAdapter.DataTable = null;
                            odoc_editor.this.LinesAdapter.notifyDataSetChanged();
                        }
                        
                        if (odoc_editor.this.BatchAdapter != null && odoc_editor.this.BatchAdapter.DataTable != null) {
                            odoc_editor.this.BatchAdapter.DataTable = null;
                            odoc_editor.this.BatchAdapter.notifyDataSetChanged();
                        }
                    } else if (result.HasError) {
                        App.Current.showError(odoc_editor.this.getContext(), "提交失败！" + result.Error);
                    }
                }
            });
            
        } else {
            
        }
    }
    
    public void setDocBody(Document doc, String cardCode, String docDate, String dueDate, String taxDate, String comments)
    {
        doc.setBody(cardCode, docDate, dueDate, taxDate, comments);
    }
    
    public void addDocLine(Document doc, String itemCode, String baseType, String baseEntry, String baseLine, String quantity, String whsCode, String freetxt)
    {
        doc.addLine(itemCode, baseType, baseEntry, baseLine, quantity, whsCode, freetxt);
    }
    
    public void addDocBatch(Document doc, String lineNum, String number, String quantity)
    {
        doc.addBatch(lineNum, number, quantity);
    }
    
    public void addDocSerial(Document doc, String lineNum, String number)
    {
        doc.addSerial(lineNum, number, true);
    }
}
