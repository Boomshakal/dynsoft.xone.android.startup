package dynsoft.xone.android.wms;

import java.math.BigDecimal;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import dynsoft.xone.android.adapter.TableAdapter;
import dynsoft.xone.android.control.ButtonTextCell;
import dynsoft.xone.android.control.RatingBarCell;
import dynsoft.xone.android.control.TextCell;
import dynsoft.xone.android.core.App;
import dynsoft.xone.android.core.R;
import dynsoft.xone.android.data.DataRow;
import dynsoft.xone.android.data.DataTable;
import dynsoft.xone.android.data.Parameters;
import dynsoft.xone.android.data.Result;
import dynsoft.xone.android.data.ResultHandler;
import dynsoft.xone.android.helper.XmlHelper;
import dynsoft.xone.android.link.Link;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.RatingBar.OnRatingBarChangeListener;
import android.widget.TextView;

public class pn_mm_handover_enter_list_editor extends pn_editor {

    public pn_mm_handover_enter_list_editor(Context context) {
        super(context);
    }

    public TextCell txt_wo_issue_order_cell;
    public TextCell txt_user_name;
    public TextCell txt_item_name_cell;
    public TextCell txt_scope_cell;
    public RatingBar txt_RatingBar;
    public TextCell txt_remart;
    public TextCell txt_enter_user_name;
    private int scope;
    public String task_code;

    @Override
    public void setContentView() {
        LayoutParams lp = new LayoutParams(-1, -1);
        View view = App.Current.Workbench.getLayoutInflater().inflate(R.layout.pn_mm_handover_enter_list_editor, this, true);
        view.setLayoutParams(lp);
    }

    @Override
    public void onPrepared() {

        super.onPrepared();

        _order_id = this.Parameters.get("order_id", 0L);
        _order_code = this.Parameters.get("code", "");
        // _id = this.Parameters.get("id", 0L);
        _user_name = this.Parameters.get("user_name", "");
        _items = this.Parameters.get("items", "");
        _user_id = this.Parameters.get("user_id", 0L);
        task_code = this.Parameters.get("task_code", "");
        scope = 0;
        this.txt_wo_issue_order_cell = (TextCell) this.findViewById(R.id.txt_wo_issue_order_cell);
        this.txt_user_name = (TextCell) this.findViewById(R.id.txt_user_name);
        this.txt_item_name_cell = (TextCell) this.findViewById(R.id.txt_item_name_cell);
        this.txt_remart = (TextCell) this.findViewById(R.id.txt_remart);
        this.txt_scope_cell = (TextCell) this.findViewById(R.id.txt_scope_cell);
        this.txt_RatingBar = (RatingBar) this.findViewById(R.id.ratingBar1);
        this.txt_enter_user_name = (TextCell) this.findViewById(R.id.txt_enter_user_name_cell);
        if (this.txt_scope_cell != null) {
            //this.txt_scope_cell.setLabelText("评分结果");
            this.txt_scope_cell.setReadOnly();
        }
        if (this.txt_wo_issue_order_cell != null) {
            this.txt_wo_issue_order_cell.setLabelText("单号");
            this.txt_wo_issue_order_cell.setContentText(_order_code);
            this.txt_wo_issue_order_cell.setReadOnly();
        }
        if (this.txt_enter_user_name != null) {
            this.txt_enter_user_name.setLabelText("用户名");
        }
        if (this.txt_user_name != null) {
            this.txt_user_name.setLabelText("仓管员");
            this.txt_user_name.setContentText(_user_name);
            this.txt_user_name.setReadOnly();
        }

        if (this.txt_item_name_cell != null) {
            this.txt_item_name_cell.setLabelText("事务说明");
            this.txt_item_name_cell.setReadOnly();
            this.txt_item_name_cell.setContentText(_items);
        }
        if (this.BackButton != null) {
            this.BackButton.setImageBitmap(App.Current.ResourceManager.getImage("@/core_exit_white"));
            this.BackButton.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    pn_mm_handover_enter_list_editor.this.backenvent();
                }
            });
        }
        if (this.txt_RatingBar != null) {
            //this.txt_RatingBar.setLabelText("评分");
            //this.txt_RatingBar.set

            this.txt_RatingBar.setNumStars(5);
            OnRatingBarChangeListener orbcl = new OnRatingBarChangeListener() {
                public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                    //小数点处理
                    int rat = (int) rating;
                    if (rat == rating) {
                        pn_mm_handover_enter_list_editor.this.txt_scope_cell.setContentText("当前得分：" + rat + "分");
                        scope = rat;
                    } else {
                        pn_mm_handover_enter_list_editor.this.txt_scope_cell.setContentText("当前得分：" + rating + "分");
                        scope = (int) rating;
                    }
                }
            };
            this.txt_RatingBar.setOnRatingBarChangeListener(orbcl);

        }

        if (this.txt_remart != null) {
            this.txt_remart.setLabelText("评价文字");
        }


        this.txt_wo_issue_order_cell.setContentText(_order_code);

    }

    private Long _order_id;
    //private Long _id;
    private String _order_code;
    private String _user_name;
    private String _items;
    private long _user_id;

    public void backenvent() {
        Link link = new Link("pane://x:code=mm_and_handover_enter_list_mgr");
        link.Parameters.add("order_id", _order_id);
        link.Parameters.add("user_operation", _user_id);
        link.Parameters.add("user_name", _user_name);
        link.Parameters.add("code", _order_code);
        // link.Parameters.add("id", _id);
        link.Parameters.add("items", _items);
        link.Open(this, this.getContext(), null);
        pn_mm_handover_enter_list_editor.this.close();
    }

    @Override
    public void onScan(final String barcode) {
        String str = "";
        if (barcode.startsWith("M")) {
            if (barcode.startsWith("M:")) {
                str = barcode.substring(2, barcode.length());
            } else {
                str = barcode;
            }
            this.txt_enter_user_name.setContentText(str);
        }
    }

    @Override
    public void commit() {
        if (scope == 0) {
            App.Current.showError(this.getContext(), "请评分。");
            return;
        }
        Log.e("LZH","LZH188");
        String user_name = this.txt_enter_user_name.getContentText().trim();
        String appraisal_comment = this.txt_remart.getContentText();
        String sql = "UPDATE  mm_handover_list SET enter_time =GETDATE(),appraisal_time=GETDATE(),appraisal_score=?,appraisal_comment=?,STATUS='已完成' ,enter_user_name =? WHERE code=?";
        Parameters p = new Parameters().add(1, scope).add(2, appraisal_comment).add(3, user_name).add(4, task_code);
        Log.e("LZH193",String.valueOf(scope));
        Log.e("LZH194",String.valueOf(appraisal_comment));
        Log.e("LZH195",String.valueOf(user_name));
        Log.e("LZH196",String.valueOf(task_code));
        Result<Integer> r = App.Current.DbPortal.ExecuteNonQuery(pn_mm_handover_enter_list_editor.this.Connector, sql, p);
        if (r.HasError) {
            App.Current.toastInfo(pn_mm_handover_enter_list_editor.this.getContext(), r.Error);
        }
        if (r.Value > 0) {
            App.Current.toastInfo(pn_mm_handover_enter_list_editor.this.getContext(), "提交成功!");
            clear();
        }

    }

    public void clear() {
        pn_mm_handover_enter_list_editor.this.close();
        //Link link = new Link("pane://x:code=mm_and_handover_list_mgr");
        //link.Open(this, this.getContext(), null);
    }


}
