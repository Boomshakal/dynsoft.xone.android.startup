package dynsoft.xone.android.manager;

import java.util.LinkedHashMap;
import java.util.Map;

import dynsoft.xone.android.core.App;
import dynsoft.xone.android.data.DataRow;
import dynsoft.xone.android.data.Parameters;
import dynsoft.xone.android.data.Result;
import dynsoft.xone.android.evaluator.Evaluator;

public class EvaluatorManager {

	private Map<String, Evaluator> _evaluators;
	
	public EvaluatorManager()
    {
		_evaluators = new LinkedHashMap<String, Evaluator>();
    }
	
    public Evaluator GetEvaluator(String code)
    {
        if (code.length() > 0) {
            Evaluator evaluator = _evaluators.get(code);
            if (evaluator == null) {
                String sql = "select code,class from core_evaluator where code=? and platform=?";
                Parameters p = new Parameters().add(1, code).add(2, App.Current.Platform);
                Result<DataRow> r = App.Current.DbPortal.ExecuteRecord(App.Current.BookConnector, sql, p);
                if (r.Value != null) {
                    String className = r.Value.getValue("class", String.class);
                    if (className != null && className.length() > 0) {
                        evaluator = (Evaluator)App.Current.ClassManager.createObject(className);
                        if (evaluator != null) {
                            evaluator.Code = code;
                            _evaluators.put(code, evaluator);
                        }
                    }
                }
            }
            return evaluator;
        }
        return null;
    }
}
