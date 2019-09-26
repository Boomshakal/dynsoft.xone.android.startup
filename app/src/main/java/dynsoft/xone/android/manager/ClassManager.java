package dynsoft.xone.android.manager;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.util.LinkedHashMap;
import java.util.Map;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Handler;

import dalvik.system.DexClassLoader;
import dynsoft.xone.android.converter.ColorConverter;
import dynsoft.xone.android.converter.IntConverter;
import dynsoft.xone.android.converter.LongConverter;
import dynsoft.xone.android.converter.TypeConverter;
import dynsoft.xone.android.core.App;
import dynsoft.xone.android.data.DataRow;
import dynsoft.xone.android.data.DataTable;
import dynsoft.xone.android.data.Parameters;
import dynsoft.xone.android.helper.StorageHelper;

public class ClassManager {
	
	private Map<Class<?>, TypeConverter> _typeConverters;
	private Map<String, String> _classes;
	private Map<String, Class<?>> _types;
	
	public ClassManager()
	{
		_typeConverters = new LinkedHashMap<Class<?>, TypeConverter>();
		_classes = new LinkedHashMap<String,String>();
		_types = new LinkedHashMap<String,Class<?>>();
	}
	
	public void syncData()
	{
	    File file = new File(App.Current.BookDirectory);
        if (file.exists() == false) {
            file.mkdirs();
        }
        
        SQLiteDatabase db = App.Current.openOrCreateDatabase(App.Current.BookCode, Context.MODE_PRIVATE, null);
        try {
            ClassManager.this.syncConverters(db);
            ClassManager.this.syncNameSpace(db);
            ClassManager.this.syncClasses(db);
            ClassManager.this.syncAssemblies(db);
        } finally {
            db.close();
        }
	}
	
	private void syncConverters(SQLiteDatabase db)
	{
	    _typeConverters.put(void.class, new IntConverter());
	    _typeConverters.put(boolean.class, new IntConverter());
        _typeConverters.put(byte.class, new LongConverter());
        _typeConverters.put(short.class, new LongConverter());
        _typeConverters.put(char.class, new IntConverter());
        _typeConverters.put(int.class, new IntConverter());
        _typeConverters.put(long.class, new LongConverter());
        _typeConverters.put(double.class, new LongConverter());
        _typeConverters.put(float.class, new IntConverter());
        
	    _typeConverters.put(Integer.class, new IntConverter());
	    _typeConverters.put(Long.class, new LongConverter());
	    _typeConverters.put(Color.class, new ColorConverter());
	}
	
	private void syncNameSpace(SQLiteDatabase db)
	{
	    db.execSQL("CREATE TABLE IF NOT EXISTS core_namespace(code nvarchar(500),name nvarchar(500))");
	    db.execSQL("delete from core_namespace");
        String sql = "select code,name from core_namespace where pltfrm=?";
        Parameters p = new Parameters().add(1, App.Current.Platform);
        DataTable table = App.Current.DbPortal.ExecuteDataTable(App.Current.BookConnector, sql, p).Value;
        if (table != null) {
            for (DataRow row : table.Rows) {
                String code = row.getValue("code", String.class);
                String name = row.getValue("name", String.class);
                db.execSQL("insert into core_namespace(code,name)values(?,?)", new Object[]{ code, name });
            }
        }
	}
	
	private void syncClasses(SQLiteDatabase db)
	{
	    db.execSQL("CREATE TABLE IF NOT EXISTS core_class(class nvarchar(500),assm nvarchar(500))");
	    db.execSQL("delete from core_class");
        String sql = "select class,assm from core_class where pltfrm=?";
        Parameters p = new Parameters().add(1, App.Current.Platform);
        DataTable table = App.Current.DbPortal.ExecuteDataTable(App.Current.BookConnector, sql, p).Value;
        if (table != null) {
            for (DataRow row : table.Rows) {
                String clss = row.getValue("class", String.class);
                String assm = row.getValue("assm", String.class);
                db.execSQL("insert into core_class(class,assm)values(?,?)", new Object[]{ clss, assm });
            }
        }
	}

	private void syncAssemblies(SQLiteDatabase db)
	{
	    db.execSQL("CREATE TABLE IF NOT EXISTS core_assembly(filename nvarchar(500),counter bigint)");
        String sql = "select filename,counter from core_assembly where pltfrm=?";
        Parameters p = new Parameters().add(1, App.Current.Platform);
        DataTable table = App.Current.DbPortal.ExecuteDataTable(App.Current.BookConnector, sql, p).Value;
        if (table != null) {
            for (DataRow row : table.Rows) {
                String filename = row.getValue("filename", String.class);
                sql = "select counter from core_assembly where filename=?";
                
                boolean equal = false;
                Cursor cursor = db.rawQuery(sql, new String[] { filename });
                if (cursor != null && cursor.moveToFirst()) {
                    long local = cursor.getLong(0);
                    long remote = row.getValue("counter", Long.class);
                    equal = local == remote;
                }
                cursor.close();
                
                if (equal == false) {
                    sql = "select data from core_assembly where filename=?";
                    p = new Parameters().add(1, filename);
                    DataRow rr = App.Current.DbPortal.ExecuteRecord(App.Current.BookConnector, sql, p).Value;
                    String path = App.Current.BookDirectory + File.separator + filename;
                    if (rr != null) {
                        StorageHelper.writeAllBytesToFile(path, rr.getValue("Data", byte[].class));
                    }
                }
            }
            
            //删除不需要的文件
            sql = "select filename from core_assembly";
            Cursor cursor = db.rawQuery(sql, new String[0]);
            if (cursor != null) {
                if (cursor.moveToFirst()) {
                    do {
                        String fn = cursor.getString(0);
                        boolean found = false;
                        for (DataRow row : table.Rows) {
                            String filename = row.getValue("filename", String.class);
                            if (fn != null && filename != null && fn.equals(filename)) {
                                found = true;
                                break;
                            }
                        }
                        if (found == false) {
                            File f = new File(App.Current.BookDirectory + File.separator + fn);
                            if (f != null && f.exists()) {
                                f.delete();
                            }
                        }
                    }
                    while(cursor.moveToNext());
                }
                cursor.close();
            }
            
            db.execSQL("delete from core_assembly");
            
            for (DataRow row : table.Rows) {
                String filename = row.getValue("filename", String.class);
                long counter = row.getValue("counter", Long.class);
                db.execSQL("insert into assembly(filename,counter)values(?,?)", new Object[]{ filename, counter });
            }
        }
	}
	
	public TypeConverter getTypeConverter(Class<?> clss)
	{
		return _typeConverters.get(clss);
	}
	
	public Class<?> getType(String className)
	{
	    if (className == null) return null;
	    
	    if (className.equals("byte")) return byte.class;
	    if (className.equals("short")) return short.class;
	    if (className.equals("int")) return int.class;
	    if (className.equals("long")) return long.class;
	    if (className.equals("char")) return char.class;
	    if (className.equals("float")) return float.class;
	    if (className.equals("double")) return double.class;
	    if (className.equals("boolean")) return boolean.class;
	    if (className.equals("void")) return void.class;
	    
	    Class<?> clazz = _types.get(className);
        if (clazz != null) {
            return clazz;
        }
        
        try {
            clazz = Class.forName(className);
            if (clazz != null) {
                _types.put(className, clazz);
                return clazz;
            }
        } catch (ClassNotFoundException e) {
        }
        
        try {
            String path = _classes.get(className);
            if (path != null) {
                path = App.Current.BookDirectory + File.separator + path;
                File file = new File(path);
                if (file.exists()) {
                    DexClassLoader loader = new DexClassLoader(file.toString(), path, null, App.Current.getClassLoader());
                    clazz = loader.loadClass(className);
                    if (clazz != null) {
                        _types.put(className, clazz);
                    }
                }
            }
        } catch (ClassNotFoundException e) {
        }

		return null;
	}
	
	public Object createObject(String className)
    {
	    Class<?> clss = this.getType(className);
        if (clss != null) {
            try {
                return clss.newInstance();
            } catch (InstantiationException e) {
            } catch (IllegalAccessException e) {
            }
        }
        return null;
    }
	
	public Object createObject(String className, Class<?> argType, Object arg)
    {
	    Class<?> clss = this.getType(className);
        if (clss != null) {
            try {
                return clss.getDeclaredConstructor(argType).newInstance(arg);
            } catch (InstantiationException e) {
            } catch (IllegalAccessException e) {
            } catch (IllegalArgumentException e) {
            } catch (InvocationTargetException e) {
            } catch (NoSuchMethodException e) {
            }
        }
        return null;
    }
	
	public Object createObject(String className, Class<?> argType1, Object arg1, Class<?> argType2, Object arg2)
    {
        Class<?> clss = this.getType(className);
        if (clss != null) {
            try {
                return clss.getDeclaredConstructor(argType1, argType2).newInstance(arg1, arg2);
            } catch (InstantiationException e) {
            } catch (IllegalAccessException e) {
            } catch (IllegalArgumentException e) {
            } catch (InvocationTargetException e) {
            } catch (NoSuchMethodException e) {
            }
        }
        return null;
    }
	
	public Object createObject(String className, Class<?> argType1, Object arg1, Class<?> argType2, Object arg2, Class<?> argType3, Object arg3)
    {
        Class<?> clss = this.getType(className);
        if (clss != null) {
            try {
                return clss.getDeclaredConstructor(argType1, argType2, argType3).newInstance(arg1, arg2, arg3);
            } catch (InstantiationException e) {
            } catch (IllegalAccessException e) {
            } catch (IllegalArgumentException e) {
            } catch (InvocationTargetException e) {
            } catch (NoSuchMethodException e) {
            }
        }
        return null;
    }
	
	public Object createObject(String className, Class<?> argType1, Object arg1, Class<?> argType2, Object arg2, Class<?> argType3, Object arg3, Class<?> argType4, Object arg4)
    {
        Class<?> clss = this.getType(className);
        if (clss != null) {
            try {
                return clss.getDeclaredConstructor(argType1, argType2, argType3, argType4).newInstance(arg1, arg2, arg3, arg4);
            } catch (InstantiationException e) {
            } catch (IllegalAccessException e) {
            } catch (IllegalArgumentException e) {
            } catch (InvocationTargetException e) {
            } catch (NoSuchMethodException e) {
            }
        }
        return null;
    }
	
	public Object createObject(String className, Class<?> argType1, Object arg1, Class<?> argType2, Object arg2, Class<?> argType3, Object arg3, Class<?> argType4, Object arg4, Class<?> argType5, Object arg5)
    {
        Class<?> clss = this.getType(className);
        if (clss != null) {
            try {
                return clss.getDeclaredConstructor(argType1, argType2, argType3, argType4, argType5).newInstance(arg1, arg2, arg3, arg4, arg5);
            } catch (InstantiationException e) {
            } catch (IllegalAccessException e) {
            } catch (IllegalArgumentException e) {
            } catch (InvocationTargetException e) {
            } catch (NoSuchMethodException e) {
            }
        }
        return null;
    }
}
