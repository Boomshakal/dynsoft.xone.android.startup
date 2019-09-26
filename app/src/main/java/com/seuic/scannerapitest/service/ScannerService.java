package com.seuic.scannerapitest.service;

import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;

import com.seuic.scanner.DecodeInfo;
import com.seuic.scanner.DecodeInfoCallBack;
import com.seuic.scanner.Scanner;
import com.seuic.scanner.ScannerFactory;
import com.seuic.scanner.ScannerKey;

public class ScannerService extends Service implements DecodeInfoCallBack {
	static final String TAG = "ScannerApiTest";
	Scanner scanner;

	@Override
	public void onCreate() {
		super.onCreate();

		scanner = ScannerFactory.getScanner(this);
		scanner.open();
		scanner.setDecodeInfoCallBack(this);

		new Thread(runnable).start();
	};

	Runnable runnable = new Runnable() {

		@Override
		public void run() {
			int ret1 = ScannerKey.open();
			if (ret1 > -1) {
				while (true) {
					int ret = ScannerKey.getKeyEvent();
					if (ret > -1) {
						switch (ret) {
						case ScannerKey.KEY_DOWN:
							scanner.startScan();
							break;
						case ScannerKey.KEY_UP:
							scanner.stopScan();
							break;
						}
					}
				}
			}
		}
	};

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		super.onStartCommand(intent, flags, startId);
		return 0;
	};

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onDestroy() {
		scanner.setDecodeInfoCallBack(null);
		scanner.close();
		super.onDestroy();
	}

	public static final String BAR_CODE = "barcode";
	public static final String CODE_TYPE = "codetype";
	public static final String LENGTH = "length";
	// 此处为自定义广播接收器action
	public static final String ACTION = "seuic.android.scanner.scannertestreciever";

	@Override
	public void onDecodeComplete(DecodeInfo info) {
		Intent intent = new Intent(ACTION);
		Bundle bundle = new Bundle();
		bundle.putString(BAR_CODE, info.barcode);
		bundle.putString(CODE_TYPE, info.codetype);
		bundle.putInt(LENGTH, info.length);
		intent.putExtras(bundle);
		sendBroadcast(intent);
	}
}
