package dynsoft.xone.android.evaluator;

import android.graphics.Bitmap;
import dynsoft.xone.android.core.App;
import dynsoft.xone.android.core.Evaluation;

public class ImageResourceEvaluator extends Evaluator {

	@Override
	public void Evaluate(Evaluation evaluation) {
		Bitmap value = App.Current.ResourceManager.getImage(evaluation.Value);
		evaluation.SetValue(evaluation.PropertyName, value);
	}
}
