package dynsoft.xone.android.evaluator;

import dynsoft.xone.android.converter.TypeConverter;
import dynsoft.xone.android.core.App;
import dynsoft.xone.android.core.Evaluation;

public class FixedValueEvaluator extends Evaluator {

	@Override
	public void Evaluate(Evaluation evaluation) {
		
		if (evaluation.PropertyClass == java.lang.CharSequence.class) {
            evaluation.SetValue(evaluation.PropertyName, evaluation.Value);
        } else if (evaluation.PropertyClass == java.lang.String.class) {
        	evaluation.SetValue(evaluation.PropertyName, evaluation.Value);
        } else {
            TypeConverter converter = App.Current.ClassManager.getTypeConverter(evaluation.PropertyClass);
            if (converter != null) {
                if (converter.CanConvertFrom(String.class)) {
                    Object value = converter.ConvertFrom(evaluation.Value);
                    evaluation.SetValue(evaluation.PropertyName, value);
                }
            }
        }
	}
}
