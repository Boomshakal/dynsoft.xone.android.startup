package dynsoft.xone.android.control;

import dynsoft.xone.android.core.App;
import dynsoft.xone.android.core.R;
import dynsoft.xone.android.expression.MathExpression;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

public class Calculator extends Activity {

    public final static int CalculatorResult = 0x8012308;
    public final static String ERROR_TEXT = "³ö´í";
    
    private boolean _afterEquals;
    private TextView _txtResult;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.setContentView(R.layout.calculator);

        String number = this.getIntent().getStringExtra("number");
        
        _txtResult = (TextView)this.findViewById(R.id.txtResult);
        if (number != null && number.length() > 0) {
            _txtResult.setText(number);  
        }
        
        Button btnLeft = (Button)this.findViewById(R.id.btnLeft);
        Button btnRight = (Button)this.findViewById(R.id.btnRight);
        Button btnMod = (Button)this.findViewById(R.id.btnMod);
        Button btnCE = (Button)this.findViewById(R.id.btnCE);
        Button btn7 = (Button)this.findViewById(R.id.btn7);
        Button btn8 = (Button)this.findViewById(R.id.btn8);
        Button btn9 = (Button)this.findViewById(R.id.btn9);
        Button btnDivide = (Button)this.findViewById(R.id.btnDivide);
        Button btn4 = (Button)this.findViewById(R.id.btn4);
        Button btn5 = (Button)this.findViewById(R.id.btn5);
        Button btn6 = (Button)this.findViewById(R.id.btn6);
        Button btnMultiple = (Button)this.findViewById(R.id.btnMultiple);
        Button btn1 = (Button)this.findViewById(R.id.btn1);
        Button btn2 = (Button)this.findViewById(R.id.btn2);
        Button btn3 = (Button)this.findViewById(R.id.btn3);
        Button btnMinus = (Button)this.findViewById(R.id.btnMinus);
        Button btnDot = (Button)this.findViewById(R.id.btnDot);
        Button btn0 = (Button)this.findViewById(R.id.btn0);
        Button btnEquals = (Button)this.findViewById(R.id.btnEquals);
        Button btnPlus = (Button)this.findViewById(R.id.btnPlus);
        Button btnReturn = (Button)this.findViewById(R.id.btnReturn);
        
        OnClickListener listener = new OnClickListener(){
            @Override
            public void onClick(View v) {
                Button btn = (Button)v;
                String c = btn.getText().toString();
                String r = _txtResult.getText().toString();
                if (r.equals(ERROR_TEXT)) {
                    _txtResult.setText(c);
                } else {
                    _txtResult.setText(r+c);
                }
                _afterEquals = false;
            }
        };
        
        btnLeft.setOnClickListener(listener);
        btnRight.setOnClickListener(listener);
        btnMod.setOnClickListener(listener);
        btn7.setOnClickListener(listener);
        btn8.setOnClickListener(listener);
        btn9.setOnClickListener(listener);
        btnDivide.setOnClickListener(listener);
        btn4.setOnClickListener(listener);
        btn5.setOnClickListener(listener);
        btn6.setOnClickListener(listener);
        btnMultiple.setOnClickListener(listener);
        btn1.setOnClickListener(listener);
        btn2.setOnClickListener(listener);
        btn3.setOnClickListener(listener);
        btnMinus.setOnClickListener(listener);
        btnDot.setOnClickListener(listener);
        btn0.setOnClickListener(listener);
        btnPlus.setOnClickListener(listener);
        
        btnCE.setOnClickListener(new OnClickListener(){
            @Override
            public void onClick(View v) {
                if (_afterEquals) {
                    _txtResult.setText("");
                } else {
                    String txt = _txtResult.getText().toString();
                    if (txt.equals(ERROR_TEXT)) {
                        _txtResult.setText("");
                    } else if (txt.length() > 0) {
                        _txtResult.setText(txt.substring(0, txt.length()-1));
                    }
                }
            }
        });
        
        btnEquals.setOnClickListener(new OnClickListener(){
            @Override
            public void onClick(View v) {
                Calculator.this.equals();
            }
        });
        
        btnReturn.setOnClickListener(new OnClickListener(){
            @Override
            public void onClick(View v) {
            	String txt = _txtResult.getText().toString();
                if (txt.length() > 0) {
	            	if (Calculator.this.equals() ==false) {
	            		return;
	            	}
                }
                
                txt = _txtResult.getText().toString();
                Intent intent = new Intent();
                intent.putExtra("result", txt);
                Calculator.this.setResult(CalculatorResult, intent);
                Calculator.this.finish();
            }
        });
    }
    
    private boolean equals()
    {
        String txt = _txtResult.getText().toString();
        if (txt.length() > 0) {
            txt = txt.replace('¡Á', '*');
            try {
                MathExpression expr = new MathExpression();
                double d = expr.evaluate(txt);
                _txtResult.setText(App.formatNumber(d, "0.#####"));
                return true;
            } catch (NumberFormatException e) {
                e.printStackTrace();
                _txtResult.setText(ERROR_TEXT);
                return false;
            } catch (ArithmeticException e) {
                e.printStackTrace();
                _txtResult.setText(ERROR_TEXT);
                return false;
            }
        }
        _afterEquals = true;
        return false;
    }
}
