package dynsoft.xone.android.wms;

import dynsoft.xone.android.control.TextCell;
import dynsoft.xone.android.core.App;
import dynsoft.xone.android.core.R;
import dynsoft.xone.android.data.DataRow;
import dynsoft.xone.android.data.DataTable;
import dynsoft.xone.android.data.Parameters;
import dynsoft.xone.android.data.Result;
import dynsoft.xone.android.data.ResultHandler;
import android.content.Context;
import android.graphics.Color;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.util.TimeUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;

import org.apache.commons.lang3.ObjectUtils;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class pn_so_after_sales_batch_show extends pn_editor implements OnClickListener {

    public pn_so_after_sales_batch_show(Context context) {
        super(context);
    }


    /*public TextCell txt_sn_number_cell; // sn����
	public TextCell txt_item_code_cell; //���ϱ���
	public TextCell txt_item_name_cell; //��������*/
    public ListView txt_item_main_cell;
    public TextCell txt_pc_number_cell;//���κ�
    private Button txt_button;
    public TextCell txt_customer_name_cell; //�ͻ�����
    public TextCell txt_is_return_cell; // �Ƿ񷵻�
    public TextCell txt_sh_date_cell; //�ջ�ʱ��
    public TextCell txt_comment_cell; //��ע
    public TextCell txt_item_code_cell;
    public CheckBox chk_is_scrap;
    public CheckBox change_storage_number;
    public TextCell txt_quantity_cell;
    boolean is_scrap = false;
    private String pc_number;
    private String datecode;
    private String storage_number;
    public TextCell txt_storage_number_cell;
    public TextCell txt_datecode_cell;
    private int scan_count;
    private boolean checkbool;
    private DataRow _order_row;
    private DataRow _lot_row;
    private Integer _total = 0;
    private Long _rownum;
    private String work_order_code;

    //���ö��ڵ�XML�ļ�
    @Override
    public void setContentView() {
        LayoutParams lp = new LayoutParams(-1, -1);
        View view = App.Current.Workbench.getLayoutInflater().inflate(
                R.layout.pn_so_after_sales_batch_show, this, true);
        view.setLayoutParams(lp);
    }

    //  ArrayList<Integer>MultiChoiceID = new ArrayList<Integer>();
    //  final String [] nItems = {"���","��ȡ����ƫ��","���ϲ���","�����","����","��Ӧ�Ʋ���","����"};

    //������ʾ�ؼ�
    @Override
    public void onPrepared() {

        super.onPrepared();

        scan_count = 0;
        checkbool = true;
		/*this.txt_sn_number_cell = (TextCell) this
				.findViewById(R.id.txt_sn_number_cell);
		
		this.txt_item_code_cell = (TextCell) this
				.findViewById(R.id.txt_item_code_cell);*/

        this.txt_pc_number_cell = (TextCell) this
                .findViewById(R.id.txt_pc_nunber_cell);

        this.txt_button = (Button) this
                .findViewById(R.id.txt_button);
        txt_button.setOnClickListener(this);
        txt_button.setVisibility(GONE);
		
		/*this.txt_item_name_cell = (TextCell) this
				.findViewById(R.id.txt_item_name_cell);*/

        this.txt_customer_name_cell = (TextCell) this
                .findViewById(R.id.txt_customer_name_cell);
        txt_customer_name_cell.setReadOnly();

        this.txt_item_code_cell = (TextCell) this
                .findViewById(R.id.txt_item_code_cell);
        txt_item_code_cell.setReadOnly();

        this.txt_quantity_cell = (TextCell) this
                .findViewById(R.id.txt_quantity_cell);
        txt_quantity_cell.setReadOnly();


        this.txt_storage_number_cell = (TextCell) this
                .findViewById(R.id.txt_storage_number_cell);
        txt_storage_number_cell.setReadOnly();

        this.txt_is_return_cell = (TextCell) this
                .findViewById(R.id.txt_is_return_cell);
        txt_is_return_cell.setReadOnly();

        this.txt_datecode_cell = (TextCell) this
                .findViewById(R.id.txt_datecode_cell);


        this.txt_sh_date_cell = (TextCell) this
                .findViewById(R.id.txt_sh_date_cell);
        txt_sh_date_cell.setReadOnly();
        this.txt_comment_cell = (TextCell) this
                .findViewById(R.id.txt_comment_cell);
        this.chk_is_scrap = (CheckBox) this.findViewById(R.id.chk_is_scrap);
        this.change_storage_number = (CheckBox) this.findViewById(R.id.change_storage_number);
        this.txt_storage_number_cell = (TextCell) this
                .findViewById(R.id.txt_storage_number_cell);

        this.txt_item_main_cell = (ListView) this.findViewById(R.id.txt_item_main_cell);

        if (this.txt_pc_number_cell != null) {
            this.txt_pc_number_cell.setLabelText("���κ�");
            this.txt_pc_number_cell.setReadOnly();
        }

        if (this.txt_quantity_cell != null) {
            this.txt_quantity_cell.setLabelText("����");
            this.txt_quantity_cell.setReadOnly();
        }

        if (this.txt_item_code_cell != null) {
            this.txt_item_code_cell.setLabelText("���ϱ��");
            this.txt_item_code_cell.setReadOnly();
        }

        if (this.txt_customer_name_cell != null) {
            this.txt_customer_name_cell.setLabelText("�ͻ�����");
            this.txt_customer_name_cell.setReadOnly();
        }

        if (this.txt_storage_number_cell != null) {
            this.txt_storage_number_cell.setLabelText("��λ");
            this.txt_customer_name_cell.setReadOnly();
        }

        if (this.txt_is_return_cell != null) {
            this.txt_is_return_cell.setLabelText("�Ƿ񷵻�");
            this.txt_is_return_cell.setReadOnly();
        }

        if (this.txt_datecode_cell != null) {
            this.txt_datecode_cell.setLabelText("����");
            //this.txt_datecode_cell.setReadOnly();
        }
		 
		/*if (this.txt_item_code_cell != null) {
			this.txt_item_code_cell.setLabelText("���ϱ���");
			this.txt_item_code_cell.setReadOnly();
		}
		
		if (this.txt_item_name_cell != null) {
			this.txt_item_name_cell.setLabelText("��������");
			this.txt_item_name_cell.setReadOnly();
		}*/

        if (this.txt_sh_date_cell != null) {
            this.txt_sh_date_cell.setLabelText("�ջ�����");
            this.txt_sh_date_cell.setReadOnly();
        }
        if (this.txt_comment_cell != null) {
            this.txt_comment_cell.setLabelText("��ע");
            //this.txt_comment_cell.setReadOnly();
        }

    }

    //ɨ����
    @Override
    public void onScan(final String barcode) {
        final String bar_code = barcode.trim();
        if (bar_code.startsWith("CRQ:")) {
            String lot = bar_code.substring(4,23);
            this.txt_pc_number_cell.setContentText(lot);
            this.loadItem(lot);

        }
        else if (bar_code.startsWith("L:")) {
            if(txt_storage_number_cell.getContentText().equals("")){this.txt_storage_number_cell.setContentText(bar_code.toString());
                this.loadItem1(bar_code);}
            else{App.Current.showError(pn_so_after_sales_batch_show.this.getContext(), "���д�λ");
                pn_so_after_sales_batch_show.this.Header.setTitleText("���д�λ");
                return;}
        }
        else {
            App.Current.showError(pn_so_after_sales_batch_show.this.getContext(), "��Ч�������ʽ");
            pn_so_after_sales_batch_show.this.Header.setTitleText("��Ч�������ʽ");
            clear();
            return;

        }
    }


    ///����SN��Ŵ������ϱ������������
    public void loadItem(String code) {
        this.ProgressDialog.show();
        String sql2 = "exec mm_after_sales_batch_3 ?";
        String sql = "exec mm_after_sales_batch_2 ?";
        String sql1 = "exec mm_after_sales_batch_1 ?";
        Parameters p = new Parameters().add(1, code);
        Parameters p1 = new Parameters().add(1, code);
        Parameters p2 = new Parameters().add(1, code);
        App.Current.DbPortal.ExecuteRecordAsync(this.Connector, sql, p, new ResultHandler<DataRow>() {
            @Override
            public void handleMessage(Message msg) {
                pn_so_after_sales_batch_show.this.ProgressDialog.dismiss();

                Result<DataRow> result = this.Value;
                if (result.HasError) {
                    App.Current.showError(pn_so_after_sales_batch_show.this.getContext(), result.Error);
                    return;
                }

                DataRow row = result.Value;
                if (row == null) {
                    App.Current.showError(pn_so_after_sales_batch_show.this.getContext(), "�Ҳ��������κŵĶ�Ӧ����Ϣ");

                    pn_so_after_sales_batch_show.this.Header.setTitleText("�Ҳ��������κŵĶ�Ӧ����Ϣ");
                    clear();
                    return;
                }
				/*pn_so_after_sales_batch_show.this.txt_item_code_cell.setContentText(row.getValue("item_code", ""));
				pn_so_after_sales_batch_show.this.txt_item_name_cell.setContentText(row.getValue("item_name", ""));*/
                pn_so_after_sales_batch_show.this.txt_customer_name_cell.setContentText(row.getValue("customer_name", ""));
                pn_so_after_sales_batch_show.this.txt_storage_number_cell.setContentText(row.getValue("storage_number", ""));
                pn_so_after_sales_batch_show.this.txt_is_return_cell.setContentText(row.getValue("is_return", ""));
                pn_so_after_sales_batch_show.this.txt_datecode_cell.setContentText(row.getValue("datecode", ""));
                pn_so_after_sales_batch_show.this.txt_sh_date_cell.setContentText(row.getValue("sh_date", new Date()).toString());
                //pn_so_after_storage_editor.this.txt_item_name_cell.setContentText(row.getValue("item_name", ""));
            }
        });
        App.Current.DbPortal.ExecuteDataTableAsync(this.Connector, sql1, p1, new ResultHandler<DataTable>() {
            @Override
            public void handleMessage(Message msg) {
                pn_so_after_sales_batch_show.this.ProgressDialog.dismiss();

                Result<DataTable> result = this.Value;
                if (result.HasError) {
                    App.Current.showError(pn_so_after_sales_batch_show.this.getContext(), result.Error);
                    return;
                }
                if (result.Value == null) {
                    App.Current.showError(pn_so_after_sales_batch_show.this.getContext(), "�Ҳ��������κŵĶ�Ӧ����Ϣ");

                    pn_so_after_sales_batch_show.this.Header.setTitleText("�Ҳ��������κŵĶ�Ӧ����Ϣ");
                    clear();
                    return;
                }
				/*pn_so_after_sales_batch_show.this.txt_item_code_cell.setContentText(row.getValue("item_code", ""));
				pn_so_after_sales_batch_show.this.txt_item_name_cell.setContentText(row.getValue("item_name", ""));*/
                MyAdapter myAdapter = new MyAdapter(result.Value);
                pn_so_after_sales_batch_show.this.txt_item_main_cell.setAdapter(myAdapter);
                //pn_so_after_storage_editor.this.txt_item_name_cell.setContentText(row.getValue("item_name", ""));


            }
        });

        App.Current.DbPortal.ExecuteRecordAsync(this.Connector, sql2, p2, new ResultHandler<DataRow>() {
            @Override
            public void handleMessage(Message msg) {
                pn_so_after_sales_batch_show.this.ProgressDialog.dismiss();

                Result<DataRow> result = this.Value;
                if (result.HasError) {
                    App.Current.showError(pn_so_after_sales_batch_show.this.getContext(), result.Error);
                    return;
                }

                DataRow row = result.Value;

				/*pn_so_after_sales_batch_show.this.txt_item_code_cell.setContentText(row.getValue("item_code", ""));
				pn_so_after_sales_batch_show.this.txt_item_name_cell.setContentText(row.getValue("item_name", ""));*/
                pn_so_after_sales_batch_show.this.txt_quantity_cell.setContentText(row.getValue("quantity", "0"));
                pn_so_after_sales_batch_show.this.txt_item_code_cell.setContentText(row.getValue("item_code", "0"));

            }
        });

    }

    public void loadItem1(String code) {pn_so_after_sales_batch_show.this.txt_storage_number_cell.setContentText(code.toString());}

    /////
    //�ύ����
    ////
    @Override
    public void commit() {
        if (change_storage_number.isChecked()) {
            this.txt_storage_number_cell.setContentText("");
            App.Current.showError(pn_so_after_sales_batch_show.this.getContext(), "��ɨ���´洢λ");
            pn_so_after_sales_batch_show.this.Header.setTitleText("��ɨ���´�λ");
            change_storage_number.setChecked(false);
            return;
        }
        pc_number = this.txt_pc_number_cell.getContentText();
        if (TextUtils.isEmpty(pc_number)) {
            App.Current.showError(pn_so_after_sales_batch_show.this.getContext(), "û����д���κ�");
            pn_so_after_sales_batch_show.this.Header.setTitleText("û����д���κ�");
            return;
        }
        datecode = this.txt_datecode_cell.getContentText();
        if (TextUtils.isEmpty(datecode)) {
            App.Current.showError(pn_so_after_sales_batch_show.this.getContext(), "û����д����");
            pn_so_after_sales_batch_show.this.Header.setTitleText("û����д����");
            return;
        }
        storage_number = this.txt_storage_number_cell.getContentText();
        if (TextUtils.isEmpty(storage_number)) {
            App.Current.showError(pn_so_after_sales_batch_show.this.getContext(), "û��ɨ�财λ");
            pn_so_after_sales_batch_show.this.Header.setTitleText("û��ɨ�财λ");
            return;
        }
        is_scrap = this.chk_is_scrap.isChecked();
        String comment = this.txt_comment_cell.getContentText().trim();
        final String pc_number = this.txt_pc_number_cell.getContentText().trim();
        final String datecode = this.txt_datecode_cell.getContentText().trim();
        final String storage_number = this.txt_storage_number_cell.getContentText().trim();
        String sql = " pn_so_after_sales_batch_show_update ?,?,?,?";
        Parameters p = new Parameters();
        p.add(1, comment).add(2, storage_number).add(3, datecode).add(4, pc_number);
        App.Current.DbPortal.ExecuteDataTableAsync(this.Connector, sql, p, new ResultHandler<DataTable>() {
            @Override
            public void handleMessage(Message msg) {
                pn_so_after_sales_batch_show.this.ProgressDialog.dismiss();
                App.Current.toastInfo(pn_so_after_sales_batch_show.this.getContext(), "������Ϣ���ύ");
                if (chk_is_scrap.isChecked()) {
                    pn_so_after_sales_batch_show.this.printLabel();
                }

                clear();

            }
        });
    }

    //�������
    public void clear() {

        this.txt_pc_number_cell.setContentText("");
        this.txt_customer_name_cell.setContentText("");
        this.txt_is_return_cell.setContentText("");
        this.txt_sh_date_cell.setContentText("");
        this.txt_comment_cell.setContentText("");
        this.txt_storage_number_cell.setContentText("");
        this.txt_datecode_cell.setContentText("");
    }

    @Override
    public void onClick(View view) {
        App.Current.Workbench.onShake();
    }

    class MyAdapter extends BaseAdapter {
        private DataTable mDataTable;

        public MyAdapter(DataTable mDataTable) {
            this.mDataTable = mDataTable;
        }

        @Override
        public int getCount() {
            return mDataTable.Rows.size();
        }

        @Override
        public Object getItem(int i) {
            return null;
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            ViewHolder viewHolder;
            if (view == null) {
                view = View.inflate(getContext(), R.layout.item_sales_batch, null);
                viewHolder = new ViewHolder();
                viewHolder.textCellItemCode = (TextView) view.findViewById(R.id.txt_item_code_cell);
                viewHolder.textCellItemName = (TextView) view.findViewById(R.id.txt_item_name_cell);
                viewHolder.textCellQuantity = (TextView) view.findViewById(R.id.txt_item_quantity_cell);
                view.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) view.getTag();
            }
            String item_code = mDataTable.Rows.get(i).getValue("item_code", "");
            String item_name = mDataTable.Rows.get(i).getValue("item_name", "");
            String item_quantity = mDataTable.Rows.get(i).getValue("quantity", "");
            viewHolder.textCellItemCode.setText(item_code);
            viewHolder.textCellItemName.setText(item_name);
            viewHolder.textCellQuantity.setText(item_quantity);
            return view;
        }
    }


    class ViewHolder {

        private TextView textCellItemCode;
        private TextView textCellItemName;
        private TextView textCellQuantity;
    }


    public void printLabel() {

        Map<String, String> parameters = new HashMap<String, String>();
        parameters.put("pc_number", this.txt_pc_number_cell.getContentText().trim());
        parameters.put("customer_name", this.txt_customer_name_cell.getContentText().trim());
        parameters.put("sh_date", this.txt_sh_date_cell.getContentText().trim());
        parameters.put("is_return", this.txt_is_return_cell.getContentText().trim());
        parameters.put("storage_number", this.txt_storage_number_cell.getContentText().trim());
        parameters.put("datecode", this.txt_datecode_cell.getContentText().trim());
        parameters.put("quantity", this.txt_quantity_cell.getContentText().trim());
        parameters.put("item_code", this.txt_item_code_cell.getContentText().trim());
        App.Current.Print("mm_after_PDA_batch", "��ӡ����", parameters);

    }

}


