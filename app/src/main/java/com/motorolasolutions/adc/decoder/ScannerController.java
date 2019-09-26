package com.motorolasolutions.adc.decoder;

import android.content.Context;
import android.util.Log;

public class ScannerController implements BarCodeReader.DecodeCallback {

	public interface ScanListener {
		void onScan(String result, String error);
	}

	static final int STATE_IDLE = 0;
	static final int STATE_DECODE = 1;
	static final int STATE_HANDSFREE = 2;
	private BarCodeReader _bcr;
	private SqliteService _sqlite;
	private int trigMode = BarCodeReader.ParamVal.LEVEL;
	private int state = STATE_IDLE;
	private ScanListener _scanListener;
	static {
	    
		System.loadLibrary("IAL");
		System.loadLibrary("SDL");
		System.loadLibrary("barcodereader");
	}

	/**
	 * �ڳ����˳�ʱ����
	 */
	public void close() {
		setIdle();
		release();
	}

	public ScannerController(Context context, ScanListener scanListener) {
		this._scanListener = scanListener;
		this._sqlite = new SqliteService(context);
		if (_sqlite.getCount() == 0) {
			_sqlite.initCodeTpye();
		}
		_bcr = BarCodeReader.open(1);
		_bcr.setDecodeCallback(this);
	}

	private void beep() {
		playSound();
	}

	private synchronized void playSound() {

	}

	private void release() {
		if (_bcr != null) {
			_bcr.release();
			_bcr = null;
		}
	}

	private int doSetParam(BarCodeReader bcr, int num, int val) {
		String s = "";
		int ret = bcr.setParameter(num, val);
		if (ret != BarCodeReader.BCR_ERROR) {
			if (num == BarCodeReader.ParamNum.PRIM_TRIG_MODE) {
				trigMode = val;
				if (val == BarCodeReader.ParamVal.HANDSFREE) {
					s = "HandsFree";
				} else if (val == BarCodeReader.ParamVal.AUTO_AIM) {
					s = "AutoAim";
					ret = bcr
							.startHandsFreeDecode(BarCodeReader.ParamVal.AUTO_AIM);
					if (ret != BarCodeReader.BCR_SUCCESS) {
						// dspErr("AUtoAIm start FAILED");
					}
				} else if (val == BarCodeReader.ParamVal.LEVEL) {
					s = "Level";
				}
			} else if (num == BarCodeReader.ParamNum.IMG_VIDEOVF) {
				// if (snapPreview = (val == 1))
				// s = "SnapPreview";
			}
		} else {
			s = " FAILED (" + ret + ")";
		}
		// dspStat(":   Set #" + num + " to " + val + " " + s);
		return ret;
	}

	private int setIdle() {
		int prevState = state;
		int ret = prevState;
		state = STATE_IDLE;
		switch (prevState) {
		case STATE_HANDSFREE: // ��ǰ״̬Ϊ�Զ�ɨ��
			resetTrigger(); // ȡ���Զ�ɨ��
			break;
		case STATE_DECODE: // ��ǰ״̬������ɨ��
			// dspStat("decode stopped");
			_bcr.stopDecode(); // ȡ��ɨ��
			break;
		default:
			ret = STATE_IDLE;
		}
		return ret;
	}

	private void resetTrigger() {
		doSetParam(_bcr, BarCodeReader.ParamNum.PRIM_TRIG_MODE,
				BarCodeReader.ParamVal.LEVEL);
		trigMode = BarCodeReader.ParamVal.LEVEL;
	}

	/**
	 * ��������ɨ��
	 * 
	 * @return
	 */
	public boolean doHandsFree() {
		if (setIdle() != STATE_IDLE)
			return false;

		int ret = _bcr.startHandsFreeDecode(BarCodeReader.ParamVal.HANDSFREE);
		// �жϷ���ֵret ��������0 ��������ɨ��ʧ��
		if (ret != BarCodeReader.BCR_SUCCESS) {
			return false;
			// dspStat("startHandFree FAILED");
		} else {
			trigMode = BarCodeReader.ParamVal.HANDSFREE;
			state = STATE_HANDSFREE; // ״̬Ϊ����ɨ��
			return true;
			// dspStat("HandsFree decoding");
		}
	}

	private boolean doDecode() {
		if (setIdle() != STATE_IDLE)
			return false;

		state = STATE_DECODE;
		// dspData("");
		// dspStat(R.string.decoding);
		_bcr.startDecode(); // start decode (callback gets results)
		return true;
	}

	/**
	 * ����һ����ɨ��
	 * 
	 * @return
	 */
	public boolean start() {
		doSetParam(_bcr, 298, 1);
		doSetParam(_bcr, 306, 1);
		return doDecode();
	}

	/**
	 * ֹͣɨ�裬��Ӧһ�λ�����
	 */
	public void stop() {
		if (state == STATE_DECODE)
			_bcr.stopDecode();
		if (state == STATE_HANDSFREE)
			resetTrigger();
		state = STATE_IDLE;
	}

	public void onDecodeComplete(int symbology, int length, byte[] data,
			BarCodeReader reader) {
		// TODO Auto-generated method stub
		if (length > 0) {
			if (state == STATE_DECODE)
				state = STATE_IDLE;
			// if (isHandsFree() == false && isAutoAim() == false)
			// bcr.stopDecode();

			if (symbology == 0x99) // type 99?
			{
				symbology = data[0];
				int n = data[1];
				int s = 2;
				int d = 0;
				int len = 0;
				byte d99[] = new byte[data.length];
				for (int i = 0; i < n; ++i) {
					s += 2;
					len = data[s++];
					System.arraycopy(data, s, d99, d, len);
					s += len;
					d += len;
				}
				d99[d] = 0;
				data = d99;
			}
			String codeType = _sqlite.searchCode(symbology);
			if (codeType == null || codeType.equals("")) {// δ֪���� ����
				// dspStat("[" + decodes + "] type: " + "unknown type" +
				// " len: " + length);
				_scanListener.onScan(null, "δ֪��������");
				return;
			} else {
				// dspStat("[" + decodes + "] type: " + codeType + " len: " +
				// length);
			}
			// dspData(byte2hex(data));
			try {
				String Result = new String(data, "GB2312");
				_scanListener.onScan(Result, null);
				// dspData(isSettingCode(SDLguiActivity.this,Result));

			} catch (Exception e) {
				Log.i("info", "excption == " + e.getMessage());
				_scanListener.onScan(null, "ת�����");
			}
			beep();
		} else // no-decode
		{
			// dspData("");
			// ����length���жϵ�ǰ״̬
			switch (length) {

			case BarCodeReader.DECODE_STATUS_TIMEOUT: // ���볬ʱ�� ��ָ����ʱ����û��ɨ����
				// dspStat("decode timed out");
				_scanListener.onScan(null, "���볬ʱ");
				break;

			case BarCodeReader.DECODE_STATUS_CANCELED: // ȡ��ɨ��
				// dspStat("decode cancelled");
				_scanListener.onScan(null, "ȡ��ɨ��");
				break;

			case BarCodeReader.DECODE_STATUS_ERROR: // ɨ�����
			default:
				// dspStat("decode failed");
				_scanListener.onScan(null, "ɨ�����");
				break;
			}
		}

	}

	public void onEvent(int event, int info, byte[] data, BarCodeReader reader) {
		// TODO Auto-generated method stub

	}

}
