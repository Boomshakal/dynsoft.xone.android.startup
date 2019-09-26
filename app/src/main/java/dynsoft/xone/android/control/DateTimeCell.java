package dynsoft.xone.android.control;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.TimeZone;

import dynsoft.xone.android.core.App;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.app.TimePickerDialog.OnTimeSetListener;
import android.text.format.DateFormat;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TimePicker;

public class DateTimeCell extends LabelCell {

	public DateTimeCell(android.content.Context context) {
		super(context);
	}

	public DateTimeCell(android.content.Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public DateTimeCell(android.content.Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}
	
	public EditText DateBox;
	public EditText TimeBox;
	public ImageButton DateButton;
	public ImageButton TimeButton;

	@Override
	public void initLayout()
	{
		super.initLayout();
		
		this.DateBox = new EditText(this.getContext());
		this.TimeBox = new EditText(this.getContext());
		this.DateButton = new ImageButton(this.getContext());
		this.TimeButton = new ImageButton(this.getContext());
		
		this.DateBox.setKeyListener(null);
		this.DateBox.setSingleLine(true);
		this.TimeBox.setKeyListener(null);
		this.TimeBox.setSingleLine(true);
		
		this.DateButton.setFocusable(false);
		this.TimeButton.setFocusable(false);
		
		this.resetDateTime();
		
		if (App.Current.ResourceManager != null) {
		    this.DateButton.setImageBitmap(App.Current.ResourceManager.getImage("@/core_calendar_light"));
		}
		
		if (App.Current.ResourceManager != null) {
		    this.TimeButton.setImageBitmap(App.Current.ResourceManager.getImage("@/core_timer_light"));
		}
		
		this.DateButton.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				DateTimeCell.this.selectDate();
			}
		});
		
		this.TimeButton.setOnClickListener(new OnClickListener(){
            @Override
            public void onClick(View v) {
                DateTimeCell.this.selectTime();
            }
        });
		
		LinearLayout.LayoutParams lp_box = new LinearLayout.LayoutParams(-2,-2);
		lp_box.weight = 1;
		lp_box.gravity = Gravity.CENTER_VERTICAL;
		this.DateBox.setLayoutParams(lp_box);
		this.TimeBox.setLayoutParams(lp_box);
		
		LinearLayout.LayoutParams lp_button = new LinearLayout.LayoutParams(-2,-2);
		lp_button.gravity = Gravity.CENTER_VERTICAL;
		this.DateButton.setLayoutParams(lp_button);
		this.TimeButton.setLayoutParams(lp_button);
		
		this.addView(this.DateBox);
		this.addView(this.DateButton);
		this.addView(this.TimeBox);
		this.addView(this.TimeButton);
	}
	
	public void resetDateTime()
	{
	    Calendar cal = Calendar.getInstance();
        String date = DateFormat.format("yyyy-MM-dd", cal).toString();
        String time = DateFormat.format("HH:mm", cal).toString();
        this.DateBox.setText(date);
        this.TimeBox.setText(time);
	}
	
	private void selectDate()
	{
		Calendar calendar = Calendar.getInstance(TimeZone.getDefault());
		try {
			calendar.setTime(new SimpleDateFormat("yyyy-MM-dd").parse(this.DateBox.getText().toString()));
		} catch (ParseException e) {}
		
		DatePickerDialog.OnDateSetListener dateListener = new DatePickerDialog.OnDateSetListener() {  
			@Override  
			public void onDateSet(DatePicker datePicker, int year, int month, int day) {
					Calendar cal = Calendar.getInstance();
					cal.set(year, month, day);
					String date = DateFormat.format("yyyy-MM-dd", cal).toString();
					DateTimeCell.this.DateBox.setText(date);
				}
		    };

	    new DatePickerDialog(getContext(),  
			dateListener,  
			calendar.get(Calendar.YEAR),  
			calendar.get(Calendar.MONTH),  
			calendar.get(Calendar.DAY_OF_MONTH)).show(); 
	}
	
	public void selectTime()
	{
	    Calendar calendar = Calendar.getInstance(TimeZone.getDefault());
        try {
            calendar.setTime(new SimpleDateFormat("HH:mm").parse(this.TimeBox.getText().toString()));
        } catch (ParseException e) {}
        
        OnTimeSetListener dateListener = new OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                Calendar cal = Calendar.getInstance();
                cal.set(cal.YEAR, cal.MONTH, cal.DATE, hourOfDay, minute);
                String date = DateFormat.format("HH:mm", cal).toString();
                DateTimeCell.this.TimeBox.setText(date);
            }
        };

        new TimePickerDialog(getContext(),  
            dateListener,  
            calendar.get(Calendar.HOUR),  
            calendar.get(Calendar.MINUTE), true).show(); 
	}
}
