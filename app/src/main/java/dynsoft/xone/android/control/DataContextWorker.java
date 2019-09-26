package dynsoft.xone.android.control;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;

import dynsoft.xone.android.data.DataRow;

public class DataContextWorker implements IDataContext {

    private Object DataContext;
    private ArrayList<Binding> Bindings;
    
    public DataContextWorker()
    {
        this.Bindings = new ArrayList<Binding>();
    }
    
    @Override
    public void setDataContext(Object data) {
        this.DataContext = data;
        Binding[] bindings = this.getRegisterBindings();
        if (bindings != null && bindings.length > 0) {
            for (Binding binding : bindings) {
                if (binding.Method != null) {
                    Class<?>[] paramTypes = binding.Method.getParameterTypes();
                    if (paramTypes.length == 1) {
                        Class<?> paramType = paramTypes[0];
                        if (this.DataContext != null) {
                            if (this.DataContext instanceof DataRow) {
                                DataRow row = (DataRow)this.DataContext;
                                Object val = row.getValue(binding.Path);
                                if (val == null) {
                                    this.setNullValue(binding, paramType);
                                } else {
                                    try {
                                        binding.Method.invoke(binding.Target, val);
                                    } catch (IllegalArgumentException e) {
                                        e.printStackTrace();
                                    } catch (IllegalAccessException e) {
                                        e.printStackTrace();
                                    } catch (InvocationTargetException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        } else {
                            this.setNullValue(binding, paramType);
                        }
                    }
                }
            }
        }
    }

    @Override
    public Object getDataContext() {
        return this.DataContext;
    }

    @Override
    public void registerBinding(Binding binding) {
        this.Bindings.add(binding);
    }

    @Override
    public Binding[] getRegisterBindings() {
        return this.Bindings.toArray(new Binding[0]);
    }

    private void setNullValue(Binding binding, Class<?> paramType)
    {
        if (paramType == int.class) {
            try {
                if (paramType == boolean.class) {
                    binding.Method.invoke(binding.Target, false);
                } else if (paramType == byte.class) {
                    binding.Method.invoke(binding.Target, 0);
                } else if (paramType == char.class) {
                    binding.Method.invoke(binding.Target, '\u0000');
                } else if (paramType == short.class) {
                    binding.Method.invoke(binding.Target, 0);
                } else if (paramType == int.class) {
                    binding.Method.invoke(binding.Target, 0);
                } else if (paramType == long.class) {
                    binding.Method.invoke(binding.Target, 0L);
                } else if (paramType == float.class) {
                    binding.Method.invoke(binding.Target, 0.0f);
                } else if (paramType == double.class) {
                    binding.Method.invoke(binding.Target, 0.0d);
                } else {
                    binding.Method.invoke(binding.Target, new Object[]{ null });
                }
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        }
    }

}
