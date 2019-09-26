package dynsoft.xone.android.control;

import dynsoft.xone.android.core.R;
import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RatingBar;

public class RatingBarCell extends LabelCell {

	public RatingBar TRatingBar;
	public boolean EnterToNext;
	
	public RatingBarCell(Context context) {
		super(context);
	}

	public RatingBarCell(android.content.Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public RatingBarCell(android.content.Context context, AttributeSet attrs,int defStyle) {
		super(context, attrs, defStyle);
		
	}
	
	public int getNumStars ()
	{
		return this.TRatingBar.getNumStars();
	}
   
	public float getRating ()
   {
	   return this.TRatingBar.getRating();
   }
   
	public float getStepSize ()
	{
		return this.TRatingBar.getStepSize();
	}
	
	public boolean isIndicator ()
	{
		return this.TRatingBar.isIndicator();	
	}
	public synchronized void setMax (int max)
	{
		this.TRatingBar.setMax(max);
	}
	public void setNumStars (int numStars)
	{
		this.TRatingBar.setNumStars(numStars);
	}
	public void setRating (float rating)
	{
		this.TRatingBar.setRating(rating);
	}
	
	
	@Override
	protected void initLayout()
	{
		super.initLayout();
		
		this.EnterToNext = true;
		TRatingBar = new RatingBar(Context);
		//TRatingBar=new RatingBar(this,null,android.R.attr.ratingBarStyle);
		LinearLayout.LayoutParams lp_text = new LinearLayout.LayoutParams(-2,-2);
		lp_text.weight=1;
		lp_text.gravity = Gravity.CENTER_VERTICAL;
		this.TRatingBar.setLayoutParams(lp_text);
		this.TRatingBar.setMinimumHeight(20);
		this.addView(TRatingBar);
	}

}
