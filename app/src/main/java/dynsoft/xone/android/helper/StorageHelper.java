package dynsoft.xone.android.helper;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import android.app.Activity;
import android.content.Context;
import android.os.Environment;

public class StorageHelper {

    public static Boolean hasSDCard()
    {
        String status = Environment.getExternalStorageState();  
        return status.equals(Environment.MEDIA_MOUNTED);
    }
    
    public static String readAllText(String path)
    {
        File file = new File(path);
        if (file.exists()) {
            int size = (int)file.length();
            byte[] bytes = new byte[size];
            try {
                BufferedInputStream buffer = new BufferedInputStream(new FileInputStream(file));
                buffer.read(bytes, 0, bytes.length);
                buffer.close();
                return new String(bytes);
            } catch (FileNotFoundException e) {
            } catch (IOException e) {
            }
        }
        return null;
    }
    
    public static byte[] readFileAllBytes(String path)
    {
        File file = new File(path);
        int size = (int) file.length();
        byte[] bytes = new byte[size];
        try {
            BufferedInputStream buf = new BufferedInputStream(new FileInputStream(file));
            buf.read(bytes, 0, bytes.length);
            buf.close();
            return bytes;
            
        } catch (FileNotFoundException e) {
        } catch (IOException e) {
        }
        return null;
    }
    
    public static void writeAllBytesToFile(String path, byte[] data)
    {
        try {
            BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(path));
            bos.write(data);
            bos.flush();
            bos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
        }
    }
    
    public static void copyAssetFile(Context c, String Name,String desPath) throws IOException
    {
        File outfile = null;
        if( desPath != null ) {
                 outfile = new File("/data/data/"+ c.getPackageName()+"/files/"+desPath+Name);
        } else {
            outfile = new File("/data/data/"+ c.getPackageName()+"/files/"+Name); 
        }

        if (!outfile.exists()) {
            outfile.createNewFile();
            FileOutputStream out = new FileOutputStream(outfile);        
            byte[] buffer = new byte[1024];  
            InputStream in;  
            int readLen = 0;  
            if( desPath != null ) {
                in = c.getAssets().open(desPath+Name);
            } else {
                in = c.getAssets().open(Name);
            }
            while((readLen = in.read(buffer)) != -1) {  
                out.write(buffer, 0, readLen);  
            }  
            out.flush();  
            in.close();  
            out.close();
        }  
    }
}

