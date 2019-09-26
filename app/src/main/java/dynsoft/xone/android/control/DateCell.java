package dynsoft.xone.android.control;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.TimeZone;

import dynsoft.xone.android.core.App;
import dynsoft.xone.android.core.R;

import android.app.DatePickerDialog;
import android.text.format.DateFormat;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.View;
import android.widget.DatePicker;

public class DateCell extends ButtonTextCell {

	public DateCell(android.content.Context context) {
		super(context);
	}

	public DateCell(android.content.Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public DateCell(android.content.Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	@Override
	public void initLayout()
	{
		super.initLayout();
		
		this.TextBox.setKeyListener(null);
		
		this.resetDate();
		this.TextBox.setSingleLine(true);
		this.TextBox.setOnKeyListener(new OnKeyListener(){

            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_ENTER) {
                    DateCell.this.selectDate();
                    return true;
                }
                return false;
            }
        });
		
		if (App.Current.ResourceManager != null) {
		    this.Button.setImageBitmap(App.Current.ResourceManager.getImage("@/core_calendar_light"));
		}
		
		this.Button.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				DateCell.this.selectDate();
			}
		});
	}
	
	public void resetDate()
	{
	    Calendar cal = Calendar.getInstance();
        String date = DateFormat.format("yyyy-MM-dd", cal).toString();
        this.TextBox.setText(date);
	}
	
	private void selectDate()
	{
		Calendar calendar = Calendar.getInstance(TimeZone.getDefault());
		try {
			calendar.setTime(new SimpleDateFormat("yyyy-MM-dd").parse(this.TextBox.getText().toString()));
		} catch (ParseException e) {}
		
		DatePickerDialog.OnDateSetListener dateListener = new DatePickerDialog.OnDateSetListener() {  
			@Override  
			public void onDateSet(DatePicker datePicker, int year, int month, int day) {
					Calendar cal = Calendar.getInstance();
					cal.set(year, month, day);
					String date = DateFormat.format("yyyy-MM-dd", cal).toString();
					DateCell.this.TextBox.setText(date);
				}
		    };

	    new DatePickerDialog(getContext(),  
			dateListener,  
			calendar.get(Calendar.YEAR),  
			calendar.get(Calendar.MONTH),  
			calendar.get(Calendar.DAY_OF_MONTH)).show(); 
	}
}
