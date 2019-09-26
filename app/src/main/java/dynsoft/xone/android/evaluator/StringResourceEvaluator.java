package dynsoft.xone.android.evaluator;

import dynsoft.xone.android.core.App;
import dynsoft.xone.android.core.Evaluation;

public class StringResourceEvaluator extends Evaluator {

	@Override
	public void Evaluate(Evaluation evaluation) {
		String value = App.Current.ResourceManager.getString(evaluation.Value);
		evaluation.SetValue(evaluation.PropertyName, value);
	}

}
