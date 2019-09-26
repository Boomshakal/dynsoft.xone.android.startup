package dynsoft.xone.android.manager;

import java.util.LinkedHashMap;
import java.util.Map;

import dynsoft.xone.android.core.App;
import dynsoft.xone.android.data.Parameters;
import dynsoft.xone.android.data.Result;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;

public class ResourceManager {

    private Map<String, String> _strings;
    private Map<String, Bitmap> _bitmaps;
    
    public ResourceManager()
    {
        _strings = new LinkedHashMap<String,String>();
        _bitmaps = new LinkedHashMap<String,Bitmap>();
    }
    
    public String getString(String code) {
        if (code != null && code.length() > 0) {
            if (code.startsWith("@/")) {
                code = code.substring(2);
                String value = _strings.get(code);
                if (value != null && value.length() > 0) {
                    return value;
                } else {
                    String sql = "select value from core_string where code=? and lang=?";
                    String lang = App.Current.Locale.getLanguage().toLowerCase() + "-" + App.Current.Locale.getCountry().toLowerCase();
                    Parameters p = new Parameters().add(1, code).add(2, lang);
                    Result<String> r = App.Current.DbPortal.ExecuteScalar(App.Current.BookConnector, sql, p, String.class);
                    if (r.Value != null && r.Value.length() > 0) {
                        value = r.Value;
                        _strings.put(code, value);
                        return value;
                    }
                }
            }
        }
        return code;
    }
    
    public Bitmap getImage(String code) {
        if (code == null || code.length() == 0) return null;
        if (code.startsWith("@/")) {
            code = code.substring(2);
        }
        
        Bitmap bmp = _bitmaps.get(code);
        if (bmp == null) {
            SQLiteDatabase db = App.Current.openOrCreateDatabase(App.Current.BookCode, Context.MODE_PRIVATE, null);
            db.execSQL("CREATE TABLE IF NOT EXISTS core_image(code varchar(100),data blob)");
            Cursor cursor = db.rawQuery("select data from core_image where code=?", new String[]{ code });
            if (cursor != null && cursor.moveToFirst()) {
                byte[] data = cursor.getBlob(0);
                if (data != null) {
                    bmp = BitmapFactory.decodeByteArray(data, 0, data.length);
                    if (bmp != null) {
                        _bitmaps.put(code, bmp);
                    }
                }
            }
            cursor.close();
            
            
            if (bmp == null) {
                String sql = "select data from core_image where code=?";
                Parameters p = new Parameters().add(1, code);
                Result<byte[]> r = App.Current.DbPortal.ExecuteScalar(App.Current.BookConnector, sql, p, byte[].class);
                if (r.Value != null) {
                    bmp = BitmapFactory.decodeByteArray(r.Value, 0, r.Value.length);
                    if (bmp != null) {
                        _bitmaps.put(code, bmp);
                        db.execSQL("insert into core_image(code,data)values(?,?)", new Object[]{ code, r.Value });
                    }
                }
            }
            
            db.close();
        }
        
        return bmp;
    }
    
}
