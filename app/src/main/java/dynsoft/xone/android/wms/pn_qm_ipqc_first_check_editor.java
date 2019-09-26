package dynsoft.xone.android.wms;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;

import java.math.BigDecimal;
import java.util.ArrayList;

import dynsoft.xone.android.activity.AlbumActivity;
import dynsoft.xone.android.activity.GalleryActivity;
import dynsoft.xone.android.control.ButtonTextCell;
import dynsoft.xone.android.control.TextCell;
import dynsoft.xone.android.core.App;
import dynsoft.xone.android.core.R;
import dynsoft.xone.android.data.DataRow;
import dynsoft.xone.android.data.Parameters;
import dynsoft.xone.android.data.Result;
import dynsoft.xone.android.data.ResultHandler;
import dynsoft.xone.android.sopactivity.ScanTestActivity;
import dynsoft.xone.android.util.Bimp;
import dynsoft.xone.android.util.Res;

/**
 * Created by Administrator on 2018/6/12.
 */

public class pn_qm_ipqc_first_check_editor extends pn_editor {
    private View view;
    private ButtonTextCell text_cell_1;
    private TextCell text_cell_2;
    private TextCell text_cell_3;
    private TextCell text_cell_4;
    private TextCell text_cell_5;
    private TextCell text_cell_6;
    private TextCell text_cell_7;
    private TextCell text_cell_8;
    private TextCell text_cell_9;
    private int id;

    public ImageButton btn_prev;
    public ImageButton btn_next;
    private int counts;
    private int all_data;
    private boolean isKeySHIFT;
    private StringBuilder _sb;
    private String edittext;
    private StringBuffer stringBuffer;
    private ArrayList<String> snString;

    public pn_qm_ipqc_first_check_editor(Context context) {
        super(context);
    }

    @Override
    public void setContentView() {
        LayoutParams lp = new LayoutParams(-1, -1);
        view = App.Current.Workbench.getLayoutInflater().inflate(R.layout.pn_qm_ipqc_first_check_editor, this, true);
        view.setLayoutParams(lp);
        _sb = new StringBuilder();
        snString = new ArrayList<String>();
        //noScrollgridview = (GridView) findViewById(R.id.noScrollgridview);
    }

    @Override
    public void onPrepared() {
        super.onPrepared();

        text_cell_1 = (ButtonTextCell) findViewById(R.id.text_cell_1);
        text_cell_2 = (TextCell) findViewById(R.id.text_cell_2);
        text_cell_3 = (TextCell) findViewById(R.id.text_cell_3);
        text_cell_4 = (TextCell) findViewById(R.id.text_cell_4);
        text_cell_5 = (TextCell) findViewById(R.id.text_cell_5);
        text_cell_6 = (TextCell) findViewById(R.id.text_cell_6);
        text_cell_7 = (TextCell) findViewById(R.id.text_cell_7);
        text_cell_8 = (TextCell) findViewById(R.id.text_cell_8);
        text_cell_9 = (TextCell) findViewById(R.id.text_cell_9);

        id = this.Parameters.get("id", 0);

        if (text_cell_1 != null) {
            text_cell_1.setLabelText("实物SN");
            text_cell_1.setReadOnly();
            text_cell_1.Button.setImageBitmap(App.Current.ResourceManager.getImage("@/core_delete_light"));
            text_cell_1.Button.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    stringBuffer = new StringBuffer();
                    if (snString.size() > 0) {
                        snString.remove(snString.size() - 1);
                        for (int i = 0; i < snString.size(); i++) {
                            stringBuffer.append(snString.get(i) + ",");
                        }
                        text_cell_1.setContentText(stringBuffer.toString());
                    }
                }
            });
        }

        if (text_cell_2 != null) {
            text_cell_2.setLabelText("确认单编码");
            text_cell_2.setReadOnly();
        }

        if (text_cell_3 != null) {
            text_cell_3.setLabelText("审料单号");
            text_cell_3.setReadOnly();
        }

        if (text_cell_4 != null) {
            text_cell_4.setLabelText("生产任务");
            text_cell_4.setReadOnly();
        }

        if (text_cell_5 != null) {
            text_cell_5.setLabelText("物料编码");
            text_cell_5.setReadOnly();
        }

        if (text_cell_6 != null) {
            text_cell_6.setLabelText("机型名称");
            text_cell_6.setReadOnly();
        }

        if (text_cell_7 != null) {
            text_cell_7.setLabelText("组织编码");
            text_cell_7.setReadOnly();
        }

        if (text_cell_8 != null) {
            text_cell_8.setLabelText("批量");
            text_cell_8.setReadOnly();
        }

        if (text_cell_9 != null) {
            text_cell_9.setLabelText("段别");
            text_cell_9.setReadOnly();
        }

        loadItem(id);
    }

    private void loadItem(int id) {
        this.ProgressDialog.show();

        String sql = "exec fm_get_ipqc_first_check_by_id_and ?";
        Parameters p = new Parameters().add(1, id);
        App.Current.DbPortal.ExecuteRecordAsync("core_and", sql, p, new ResultHandler<DataRow>() {

            @Override
            public void handleMessage(Message msg) {
                pn_qm_ipqc_first_check_editor.this.ProgressDialog.dismiss();
                Result<DataRow> value = Value;
                if (value.HasError) {
                    App.Current.showError(getContext(), value.Error);
                    return;
                }
                if (value.Value != null) {
                    String code = value.Value.getValue("code", "");
                    String check_code = value.Value.getValue("check_code", "");
                    String task_code = value.Value.getValue("task_code", "");
                    String item_code = value.Value.getValue("item_code", "");
                    String item_name = value.Value.getValue("item_name", "");
                    String org_code = value.Value.getValue("org_code", "");
                    int plan_qty = value.Value.getValue("plan_qty", new BigDecimal(0)).intValue();
                    String segment = value.Value.getValue("segment", "");
                    text_cell_2.setContentText(code);
                    text_cell_3.setContentText(check_code);
                    text_cell_4.setContentText(task_code);
                    text_cell_5.setContentText(item_code);
                    text_cell_6.setContentText(item_name);
                    text_cell_7.setContentText(org_code);
                    text_cell_8.setContentText(String.valueOf(plan_qty));
                    text_cell_9.setContentText(segment);
                } else {

                }
            }
        });
    }

    private void checkShift(char ascallNoShift, char ascallOnShift) {
        if (this.isKeySHIFT) {
            _sb.append(ascallOnShift);
            this.isKeySHIFT = false;
        } else {
            _sb.append(ascallNoShift);
        }
    }

    private void handleNumPadKeys(int keyCode) {
        if (keyCode <= 153) {
            _sb.append((char) (keyCode - 96));
        } else if (keyCode == 154) {
            _sb.append('/');
        } else if (keyCode == 155) {
            _sb.append('*');
        } else if (keyCode == 156) {
            _sb.append('-');
        } else if (keyCode == 157) {
            _sb.append('+');
        } else if (keyCode == 158) {
            _sb.append('.');
        }
    }

    private void handleTopNumKeys(int keyCode) {
        if (keyCode >= 7 && keyCode <= 16) {
            switch (keyCode) {
                case 7:
                    this.checkShift('0', ')');
                    break;
                case 8:
                    this.checkShift('1', '!');
                    break;
                case 9:
                    this.checkShift('2', '@');
                    break;
                case 10:
                    this.checkShift('3', '#');
                    break;
                case 11:
                    this.checkShift('4', '$');
                    break;
                case 12:
                    this.checkShift('5', '%');
                    break;
                case 13:
                    this.checkShift('6', '^');
                    break;
                case 14:
                    this.checkShift('7', '&');
                    break;
                case 15:
                    this.checkShift('8', '*');
                    break;
                case 16:
                    this.checkShift('9', '(');
            }
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode <= 6 && keyCode != 3 && keyCode != 4) {
            return super.dispatchKeyEvent(event);
        } else if (keyCode == 3 || keyCode == 4) {
            return super.onKeyDown(keyCode, event);
        } else {
            if (keyCode == 59) {
                this.isKeySHIFT = true;
            }

            if (keyCode == 66) {
                this.isKeySHIFT = false;
                stringBuffer = new StringBuffer();
                edittext = _sb.toString().toUpperCase().replace("\n", "");
                if (!snString.contains(edittext)) {
                    snString.add(edittext);
                }
                for (int i = 0; i < snString.size(); i++) {
                    stringBuffer.append(snString.get(i) + ",");
                }
                text_cell_1.setContentText(stringBuffer.toString());
                _sb.delete(0, _sb.length());
                return true;
            } else {
                if (keyCode >= 7 && keyCode <= 16) {
                    this.handleTopNumKeys(keyCode);
                } else if (keyCode >= 29 && keyCode <= 54) {
                    this.checkShift((char) (keyCode + 68), (char) (keyCode + 36));
                } else {
                    if (keyCode < 144 || keyCode > 158) {
                        switch (keyCode) {
                            case 55:
                                this.checkShift(',', '<');
                                break;
                            case 56:
                                this.checkShift('.', '>');
                                break;
                            case 57:
                            case 58:
                            case 59:
                            case 60:
                            case 61:
                            case 63:
                            case 64:
                            case 65:
                            case 66:
                            case 67:
                            default:
                                return false;
                            case 62:
                                _sb.append(' ');
                                break;
                            case 68:
                                this.checkShift('`', '~');
                                break;
                            case 69:
                                this.checkShift('-', '_');
                                break;
                            case 70:
                                this.checkShift('=', '+');
                                break;
                            case 71:
                                this.checkShift('[', '{');
                                break;
                            case 72:
                                this.checkShift(']', '}');
                                break;
                            case 73:
                                this.checkShift('\\', '|');
                                break;
                            case 74:
                                this.checkShift(';', ':');
                                break;
                            case 75:
                                this.checkShift('\'', '"');
                                break;
                            case 76:
                                this.checkShift('/', '?');
                        }
                        return true;
                    }

                    this.handleNumPadKeys(keyCode);
                }
            }
            return super.onKeyDown(keyCode, event);
        }
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getKeyCode() > 6) {
            if (event.getAction() == KeyEvent.ACTION_DOWN) {
                this.onKeyDown(event.getKeyCode(), event);
                return true;

            } else if (event.getAction() == KeyEvent.ACTION_UP) {
                this.onKeyUp(event.getKeyCode(), event);
                return true;
            }
            return true;
        }
        return super.dispatchKeyEvent(event);
    }
    
    @Override
    public void onScan(String barcode) {
        final String bar_code = barcode.replace("\n", "").trim();
        stringBuffer = new StringBuffer();
        //判断条码是否符合当前工单
        String sql = "exec fm_check_first_scan_barcode_and ?,?";
        Parameters p = new Parameters().add(1, text_cell_4.getContentText()).add(2, bar_code);
        App.Current.DbPortal.ExecuteNonQueryAsync("core_and", sql, p, new ResultHandler<Integer>() {
            @Override
            public void handleMessage(Message msg) {
                Result<Integer> value = Value;
                if(value.HasError) {
                    App.Current.toastError(getContext(), value.Error);
                    App.Current.playSound(R.raw.hook);
                } else {
                    if (!snString.contains(bar_code)) {
                        snString.add(bar_code);
                    }
                    for (int i = 0; i < snString.size(); i++) {
                        stringBuffer.append(snString.get(i) + ",");
                    }
                    text_cell_1.setContentText(stringBuffer.toString());
                }
            }
        });
    }
    
//    @Override
//    public void onScan(String barcode) {
//        String bar_code = barcode.replace("\n", "").trim();
//        stringBuffer = new StringBuffer();
//        if (!snString.contains(bar_code)) {
//            snString.add(bar_code);
//        }
//        for (int i = 0; i < snString.size(); i++) {
//            stringBuffer.append(snString.get(i) + ",");
//        }
//        text_cell_1.setContentText(stringBuffer.toString());
//    }

    @Override
    public void commit() {
        super.commit();
        if(TextUtils.isEmpty(text_cell_1.getContentText())) {
            App.Current.toastError(pn_qm_ipqc_first_check_editor.this.getContext(), "请扫描SN单号");
            App.Current.playSound(R.raw.hook);
            return;
        }
        if(snString.size() != 2) {
            App.Current.toastError(pn_qm_ipqc_first_check_editor.this.getContext(), "SN单号个数为" + snString.size() + "个，要求2个。");
            App.Current.playSound(R.raw.hook);
            return;
        }

        String sql = "exec fm_commit_ipqc_first_check_and ?,?";
        Parameters p = new Parameters().add(1, id).add(2, text_cell_1.getContentText());
        App.Current.DbPortal.ExecuteNonQueryAsync("core_and", sql, p, new ResultHandler<Integer>() {
            @Override
            public void handleMessage(Message msg) {
                Result<Integer> value = Value;
                if(value.HasError) {
                    App.Current.toastError(getContext(), value.Error);
                    App.Current.playSound(R.raw.hook);
                } else {
                    if(value.Value > 0) {
                        App.Current.toastInfo(getContext(), "提交成功");
                        App.Current.playSound(R.raw.pass);
                        close();
                    } else {
                        App.Current.toastInfo(getContext(), "提交失败");
                        App.Current.playSound(R.raw.hook);
                    }
                }
            }
        });
    }
}
