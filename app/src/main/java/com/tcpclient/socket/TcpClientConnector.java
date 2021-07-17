package com.tcpclient.socket;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;


public class TcpClientConnector {
    private static TcpClientConnector mTcpClientConnector;
    private Socket mClient;
    private ConnectLinstener mListener;
    private Thread mConnectThread;

    public interface ConnectLinstener {
        void onReceiveData(String data);
    }

    public void setOnConnectLinstener(ConnectLinstener linstener) {
        this.mListener = linstener;
    }

    public static TcpClientConnector getInstance() {
        if (mTcpClientConnector == null)
            mTcpClientConnector = new TcpClientConnector();
        return mTcpClientConnector;
    }

    @SuppressLint("HandlerLeak")
    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 100) {
                if (mListener != null) {
                    mListener.onReceiveData(msg.getData().getString("data"));
                }
            }
        }
    };

    public void creatConnect(final String mSerIP, final int mSerPort) {
        if (mConnectThread == null) {
            mConnectThread = new Thread(() -> {
                try {
                    connect(mSerIP, mSerPort);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            mConnectThread.start();
        }
    }

    /**
     * �����˽�������
     */
    private void connect(String mSerIP, int mSerPort) throws IOException {
        if (mClient == null) {
            mClient = new Socket(mSerIP, mSerPort);
        }
        InputStream inputStream = mClient.getInputStream();
        byte[] buffer = new byte[1024];
        int len;
        while ((len = inputStream.read(buffer)) != -1) {
            String data = new String(buffer, 0, len);
            Message message = new Message();
            message.what = 100;
            Bundle bundle = new Bundle();
            bundle.putString("data", data);
            message.setData(bundle);
            mHandler.sendMessage(message);
        }
    }

    /**
     * ��������
     *
     * @param data ��Ҫ���͵�����
     */
    public void send(String data) throws IOException {
        OutputStream outputStream = mClient.getOutputStream();
        outputStream.write(data.getBytes());
    }

    /**
     * �Ͽ�����
     */
    public void disconnect() throws IOException {
        if (mClient != null) {
            mClient.close();
            mClient = null;
        }
    }
}

