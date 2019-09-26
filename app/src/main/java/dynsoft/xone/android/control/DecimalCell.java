package dynsoft.xone.android.control;

import dynsoft.xone.android.core.App;
import android.app.Activity;
import android.content.Intent;
import android.util.AttributeSet;
import android.view.View;

public class DecimalCell extends ButtonTextCell {

    public DecimalCell(android.content.Context context) {
        super(context);
    }

    public DecimalCell(android.content.Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public DecimalCell(android.content.Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public void initLayout()
    {
        super.initLayout();
        
        this.TextBox.setSingleLine(true);
        this.Button.setImageBitmap(App.Current.ResourceManager.getImage("@/core_calculator_light"));
        this.Button.setOnClickListener(new OnClickListener(){
            @Override
            public void onClick(View v) {
                DecimalCell.this.openCalculator();
            }
        });
    }
    
    private int _requestCode;
    
    public void setRequestCode(int code)
    {
    	_requestCode = code;
    }
    
    public int getRequestCode()
    {
    	return _requestCode;
    }
    
    public void openCalculator()
    {
        Intent intent = new Intent();
        intent.setClass(this.getContext(), Calculator.class);
        intent.putExtra("number", this.TextBox.getText().toString());
        ((Activity)this.getContext()).startActivityForResult(intent, _requestCode);
    }
    
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent)
    {
        if (resultCode == Calculator.CalculatorResult) {
            String result = intent.getStringExtra("result");
            if (result != null) {
                this.TextBox.setText(result);
            }
        }
    }
}
