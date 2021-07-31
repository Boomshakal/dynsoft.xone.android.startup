package dynsoft.xone.android.wms;

import android.content.Context;
import android.view.View;

import dynsoft.xone.android.control.ButtonTextCell;
import dynsoft.xone.android.core.App;
import dynsoft.xone.android.core.R;
import dynsoft.xone.android.data.DataRow;

import dynsoft.xone.android.data.Parameters;
import dynsoft.xone.android.data.Result;

/**
 * Created by Administrator on 2017/12/18.
 */

public class pn_fqc_query_status_mgr extends pn_mgr {

    private ButtonTextCell textcell_2;

    private View view;


    public pn_fqc_query_status_mgr(Context context) {
        super(context);
    }

    @Override
    public void setContentView() {
        LayoutParams lp = new LayoutParams(-1, -1);
        view = App.Current.Workbench.getLayoutInflater().inflate(R.layout.pn_bom_query_mgr, this, true);
        view.setLayoutParams(lp);
    }

    @Override
    public void onPrepared() {
        super.onPrepared();

        textcell_2 = view.findViewById(R.id.textcell_2);

        if (textcell_2 != null) {
            textcell_2.setLabelText("批次号");
            textcell_2.setReadOnly();
            textcell_2.Button.setOnClickListener(v -> {
                // TODO Auto-generated method stub
                App.Current.Workbench.scanByCamera();
            });
        }
    }

    @Override
    public void onScan(final String barcode) {
        final String bar_code = barcode.trim();
        if (bar_code.startsWith("CRQ:")) {
            String tempbar_code = bar_code.substring(4);
            String lot_number = tempbar_code.split("-")[0];
//            String prd_no = tempbar_code.split("-")[1];
//            String qty = tempbar_code.split("-")[2];

            this.textcell_2.setContentText(lot_number);
            get_fqc_status(lot_number);
        }
    }

    private void get_fqc_status(String lot_number) {
        final String sql = "exec p_get_fqc_status_and ?";
        Parameters p = new Parameters().add(1, lot_number);
        Result<DataRow> r = App.Current.DbPortal.ExecuteRecord(this.Connector, sql, p);
        if (r.HasError) {
            App.Current.showError(this.getContext(), r.Error);
            clear();
        } else {
            DataRow row = r.Value;
            if (row != null) {
                String fqc_status = row.getValue("fqc_status", "");
                String inspection_results = row.getValue("inspection_results", "");

                String showinfo = "检验结果：" + inspection_results + "\n检验状态：" + fqc_status;
                App.Current.showInfo(this.getContext(), showinfo);
            } else {
                App.Current.showInfo(this.getContext(), "未缴库,请先缴库！");
            }
        }

    }


    private void clear() {
        this.textcell_2.setContentText("");
    }

}
