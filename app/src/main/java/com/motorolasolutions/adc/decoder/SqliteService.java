package com.motorolasolutions.adc.decoder;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class SqliteService {

	private SqliteHelper sqlCon;

	public SqliteService(Context context) {
		sqlCon = new SqliteHelper(context);
	}

	public synchronized String searchCode(int codeID) {
		SQLiteDatabase db = null;
		String typeName = "";
		try {
			db = sqlCon.getReadableDatabase();
			String sql = "select * from CodeType where type_id = " + codeID;
			Cursor cursor = db.rawQuery(sql, new String[] {});
			while (cursor.moveToNext()) {
				typeName = cursor.getString(1);
			}
		} finally {
			if (db != null) {
				db.close();
			}
		}
		return typeName;
	}

	public synchronized void initCodeTpye() {
		SQLiteDatabase db = null;
		try {
			db = sqlCon.getWritableDatabase();
			db.beginTransaction();
			db.execSQL("insert into CodeType(type_name,type_id)"
					+ "values('Code 39',1)");
			db.execSQL("insert into CodeType(type_name,type_id)"
					+ "values('Codabar',2)");
			db.execSQL("insert into CodeType(type_name,type_id)"
					+ "values('Code 128',3)");
			db.execSQL("insert into CodeType(type_name,type_id)"
					+ "values('Discrete (Standard) 2 of 5',4)");
			db.execSQL("insert into CodeType(type_name,type_id)"
					+ "values('IATA',5)");
			db.execSQL("insert into CodeType(type_name,type_id)"
					+ "values('Interleaved 2 of 5',6)");
			db.execSQL("insert into CodeType(type_name,type_id)"
					+ "values('Code 93',7)");
			db.execSQL("insert into CodeType(type_name,type_id)"
					+ "values('UPC-A',8)");
			db.execSQL("insert into CodeType(type_name,type_id)"
					+ "values('UPC-E0',9)");
			db.execSQL("insert into CodeType(type_name,type_id)"
					+ "values('EAN-8',10)");
			db.execSQL("insert into CodeType(type_name,type_id)"
					+ "values('EAN-13',11)");
			db.execSQL("insert into CodeType(type_name,type_id)"
					+ "values('Code 11',12)");
			db.execSQL("insert into CodeType(type_name,type_id)"
					+ "values('Code 49',13)");
			db.execSQL("insert into CodeType(type_name,type_id)"
					+ "values('MSI',14)");
			db.execSQL("insert into CodeType(type_name,type_id)"
					+ "values('EAN-128',15)");
			db.execSQL("insert into CodeType(type_name,type_id)"
					+ "values('UPC-E1',16)");
			db.execSQL("insert into CodeType(type_name,type_id)"
					+ "values('PDF-417',17)");
			db.execSQL("insert into CodeType(type_name,type_id)"
					+ "values('Code 16K',18)");
			db.execSQL("insert into CodeType(type_name,type_id)"
					+ "values('Code 39 Full ASCII',19)");
			db.execSQL("insert into CodeType(type_name,type_id)"
					+ "values('UPC-D',20)");
			db.execSQL("insert into CodeType(type_name,type_id)"
					+ "values('Code 39 Trioptic',21)");
			db.execSQL("insert into CodeType(type_name,type_id)"
					+ "values('Bookland',22)");
			db.execSQL("insert into CodeType(type_name,type_id)"
					+ "values('Coupon Code',23)");
			db.execSQL("insert into CodeType(type_name,type_id)"
					+ "values('NW-7',24)");
			db.execSQL("insert into CodeType(type_name,type_id)"
					+ "values('ISBT-128',25)");
			db.execSQL("insert into CodeType(type_name,type_id)"
					+ "values('Micro PDF',26)");
			db.execSQL("insert into CodeType(type_name,type_id)"
					+ "values('DataMatrix',27)");
			db.execSQL("insert into CodeType(type_name,type_id)"
					+ "values('QR Code',28)");
			db.execSQL("insert into CodeType(type_name,type_id)"
					+ "values('Micro PDF CCA',29)");
			db.execSQL("insert into CodeType(type_name,type_id)"
					+ "values('PostNet US',30)");
			db.execSQL("insert into CodeType(type_name,type_id)"
					+ "values('Planet Code',31)");
			db.execSQL("insert into CodeType(type_name,type_id)"
					+ "values('Code 32',32)");
			db.execSQL("insert into CodeType(type_name,type_id)"
					+ "values('ISBT-128 Con',33)");
			db.execSQL("insert into CodeType(type_name,type_id)"
					+ "values('Japan Postal',34)");
			db.execSQL("insert into CodeType(type_name,type_id)"
					+ "values('Australian Postal',35)");
			db.execSQL("insert into CodeType(type_name,type_id)"
					+ "values('Dutch Postal',36)");
			db.execSQL("insert into CodeType(type_name,type_id)"
					+ "values('MaxiCode',37)");
			db.execSQL("insert into CodeType(type_name,type_id)"
					+ "values('Canadian Postal',38)");
			db.execSQL("insert into CodeType(type_name,type_id)"
					+ "values('UK Postal',39)");
			db.execSQL("insert into CodeType(type_name,type_id)"
					+ "values('Macro PDF',40)");
			db.execSQL("insert into CodeType(type_name,type_id)"
					+ "values('Macro QR',41)");
			db.execSQL("insert into CodeType(type_name,type_id)"
					+ "values('Micro QR',44)");
			db.execSQL("insert into CodeType(type_name,type_id)"
					+ "values('Aztec',45)");
			db.execSQL("insert into CodeType(type_name,type_id)"
					+ "values('Aztec Rune',46)");
			db.execSQL("insert into CodeType(type_name,type_id)"
					+ "values('GS1 DataBar-14',48)");
			db.execSQL("insert into CodeType(type_name,type_id)"
					+ "values('GS1 DataBar Limited',49)");
			db.execSQL("insert into CodeType(type_name,type_id)"
					+ "values('GS1 DataBar Expanded',50)");
			db.execSQL("insert into CodeType(type_name,type_id)"
					+ "values('USPS 4CB',52)");
			db.execSQL("insert into CodeType(type_name,type_id)"
					+ "values('UPU 4State',53)");
			db.execSQL("insert into CodeType(type_name,type_id)"
					+ "values('ISSN',54)");
			db.execSQL("insert into CodeType(type_name,type_id)"
					+ "values('Scanlet',55)");
			db.execSQL("insert into CodeType(type_name,type_id)"
					+ "values('CueCode',56)");
			db.execSQL("insert into CodeType(type_name,type_id)"
					+ "values('Matrix 2 of 5',57)");
			db.execSQL("insert into CodeType(type_name,type_id)"
					+ "values('UPC-A + 2 Supplemental',72)");
			db.execSQL("insert into CodeType(type_name,type_id)"
					+ "values('UPC-E0 + 2 Supplemental',73)");
			db.execSQL("insert into CodeType(type_name,type_id)"
					+ "values('EAN-8 + 2 Supplemental',74)");
			db.execSQL("insert into CodeType(type_name,type_id)"
					+ "values('EAN-13 + 2 Supplemental',75)");
			db.execSQL("insert into CodeType(type_name,type_id)"
					+ "values('UPC-E1 + 2 Supplemental',80)");
			db.execSQL("insert into CodeType(type_name,type_id)"
					+ "values('CCA EAN-13',82)");
			db.execSQL("insert into CodeType(type_name,type_id)"
					+ "values('CCA EAN-8',83)");
			db.execSQL("insert into CodeType(type_name,type_id)"
					+ "values('CCA GS1 DataBar Expanded',84)");
			db.execSQL("insert into CodeType(type_name,type_id)"
					+ "values('CCA GS1 DataBar Limited',85)");
			db.execSQL("insert into CodeType(type_name,type_id)"
					+ "values('CCA GS1 DataBar-14',86)");
			db.execSQL("insert into CodeType(type_name,type_id)"
					+ "values('CCA UPC-A',87)");
			db.execSQL("insert into CodeType(type_name,type_id)"
					+ "values('CCA UPC-E',88)");
			db.execSQL("insert into CodeType(type_name,type_id)"
					+ "values('CCC EAN-128',89)");
			db.execSQL("insert into CodeType(type_name,type_id)"
					+ "values('TLC-39',90)");
			db.execSQL("insert into CodeType(type_name,type_id)"
					+ "values('CCB EAN-128',97)");
			db.execSQL("insert into CodeType(type_name,type_id)"
					+ "values('CCB EAN-13',98)");
			db.execSQL("insert into CodeType(type_name,type_id)"
					+ "values('CCB EAN-8',99)");
			db.execSQL("insert into CodeType(type_name,type_id)"
					+ "values('CCB GS1 DataBar Expanded',100)");
			db.execSQL("insert into CodeType(type_name,type_id)"
					+ "values('CCB GS1 DataBar Limited',101)");
			db.execSQL("insert into CodeType(type_name,type_id)"
					+ "values('CCB GS1 DataBar-14',102)");
			db.execSQL("insert into CodeType(type_name,type_id)"
					+ "values('CCB UPC-A',103)");
			db.execSQL("insert into CodeType(type_name,type_id)"
					+ "values('CCB UPC-E',104)");
			db.execSQL("insert into CodeType(type_name,type_id)"
					+ "values('Signature Capture',105)");
			db.execSQL("insert into CodeType(type_name,type_id)"
					+ "values('Chinese 2 of 5',114)");
			db.execSQL("insert into CodeType(type_name,type_id)"
					+ "values('Korean 3 of 5',115)");
			db.execSQL("insert into CodeType(type_name,type_id)"
					+ "values('UPC-A + 5 supplemental',136)");
			db.execSQL("insert into CodeType(type_name,type_id)"
					+ "values('UPC-E0 + 5 supplemental',137)");
			db.execSQL("insert into CodeType(type_name,type_id)"
					+ "values('EAN-8 + 5 supplemental',138)");
			db.execSQL("insert into CodeType(type_name,type_id)"
					+ "values('EAN-13 + 5 supplemental',139)");
			db.execSQL("insert into CodeType(type_name,type_id)"
					+ "values('UPC-E1 + 5 Supplemental',144)");
			db.execSQL("insert into CodeType(type_name,type_id)"
					+ "values('Macro Micro PDF',154)");
			db.execSQL("insert into CodeType(type_name,type_id)"
					+ "values('GS1 Databar Coupon',180)");
			db.setTransactionSuccessful();
			db.endTransaction();
		} finally {
			if (db != null) {
				db.close();
			}
		}
	}

	public synchronized int getCount() {
		SQLiteDatabase db = null;
		int sum = 0;
		try {
			db = sqlCon.getReadableDatabase();
			String sql = "select count(id) from CodeType ";
			Cursor cur = db.rawQuery(sql, new String[] {});

			while (cur.moveToNext()) {
				sum = cur.getInt(0);
			}
			Log.i("info", "sum == " + sum);
		} finally {
			if (db != null) {
				db.close();
			}
		}
		return sum;
	}

	public static int computeTotalPagesBasedOnCount(int pagesize, int count) {
		if (pagesize <= 0 || count <= 0) {
			return -1;
		}
		if (pagesize + 1 == 0) {
			return -1;
		}
		int x = count / pagesize + 1;
		return x;
	}
}
