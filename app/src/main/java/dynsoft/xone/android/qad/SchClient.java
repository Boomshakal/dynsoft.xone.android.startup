package dynsoft.xone.android.qad;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;

import dynsoft.xone.android.data.Result;

public class SchClient {

    private JSch _jsch;
    private Session _session;

    public Result<String> connect(final String host, final String user,
            final String passwd) {
        FutureTask<Result<String>> task = new FutureTask<Result<String>>(
                new Callable<Result<String>>() {
                    @Override
                    public Result<String> call() {
                        Result<String> result = new Result<String>();
                        try {
                            Properties prop = new Properties();
                            prop.put("StrictHostKeyChecking", "no");

                            _jsch = new JSch();
                            _session = _jsch.getSession(user, host, 22);
                            _session.setPassword(passwd);
                            _session.setConfig(prop);
                            _session.connect();

                        } catch (JSchException e) {
                            if (_session != null) {
                                try {
                                    _session.disconnect();
                                } catch (Exception ex) {
                                    result.HasError = true;
                                    result.Error = e.getMessage();
                                    return result;
                                }
                            }
                            result.HasError = true;
                            result.Error = e.getMessage();
                        }
                        return result;
                    }
                });

        new Thread(task).start();
        try {
            return task.get();
        } catch (InterruptedException e) {
            Result<String> result = new Result<String>();
            result.Error = e.getMessage();
            result.HasError = true;
            return result;
        } catch (ExecutionException e) {
            Result<String> result = new Result<String>();
            result.Error = e.getMessage();
            result.HasError = true;
            return result;
        }
    }

    public Result<String> upload(final String content, final String remote) {
        if (content == null) {
            Result<String> result = new Result<String>();
            result.HasError = true;
            result.Error = "content cannot be null.";
            return result;
        }

        FutureTask<Result<String>> task = new FutureTask<Result<String>>(
                new Callable<Result<String>>() {
                    @Override
                    public Result<String> call() {

                        Result<String> result = new Result<String>();
                        Channel channel = null;
                        try {
                            channel = _session.openChannel("sftp");
                            channel.connect();
                            ChannelSftp sftp = (ChannelSftp) channel;
                            InputStream input = new ByteArrayInputStream(
                                    content.getBytes());
                            sftp.put(input, remote);
                            input.close();

                        } catch (JSchException e) {
                            result.HasError = true;
                            result.Error = e.getMessage();
                            return result;
                        } catch (SftpException e) {
                            result.HasError = true;
                            result.Error = e.getMessage();
                            return result;
                        } catch (IOException e) {
                            result.HasError = true;
                            result.Error = e.getMessage();
                            return result;
                        } finally {
                            if (channel != null) {
                                try {
                                    channel.disconnect();
                                } catch (Exception e) {
                                    result.HasError = true;
                                    result.Error = e.getMessage();
                                    return result;
                                }
                            }
                        }
                        return result;
                    }
                });

        new Thread(task).start();
        try {
            return task.get();
        } catch (InterruptedException e) {
            Result<String> result = new Result<String>();
            result.HasError = true;
            result.Error = e.getMessage();
            return result;
        } catch (ExecutionException e) {
            Result<String> result = new Result<String>();
            result.HasError = true;
            result.Error = e.getMessage();
            return result;
        }
    }

    public Result<String> exec(final String cmd) {
        FutureTask<Result<String>> task = new FutureTask<Result<String>>(
                new Callable<Result<String>>() {
                    @Override
                    public Result<String> call() {
                        Result<String> res = new Result<String>();
                        Channel channel = null;
                        try {
                            channel = _session.openChannel("exec");
                            ((ChannelExec) channel).setCommand(cmd);

                            channel.setInputStream(null);
                            // ((ChannelExec)channel).setErrStream(System.err);

                            InputStream in = channel.getInputStream();
                            channel.connect();

                            String result = "";
                            byte[] tmp = new byte[1024];
                            while (true) {
                                while (in.available() > 0) {
                                    int i = in.read(tmp, 0, 1024);
                                    if (i < 0)
                                        break;
                                    result = result + new String(tmp, 0, i);
                                }
                                if (channel.isClosed()) {
                                    break;
                                }
                            }
                            channel.disconnect();
                            return res;

                        } catch (JSchException e) {
                            res.HasError = true;
                            res.Error = e.getMessage();
                            return res;
                        } catch (IOException e) {
                            res.HasError = true;
                            res.Error = e.getMessage();
                            return res;
                        } finally {
                            if (channel != null) {
                                try {
                                    channel.disconnect();
                                } catch (Exception e) {
                                    res.HasError = true;
                                    res.Error = e.getMessage();
                                    return res;
                                }
                            }
                        }
                    }
                });

        new Thread(task).start();
        try {
            return task.get();
        } catch (InterruptedException e) {
            Result<String> result = new Result<String>();
            result.HasError = true;
            result.Error = e.getMessage();
            return result;
        } catch (ExecutionException e) {
            Result<String> result = new Result<String>();
            result.HasError = true;
            result.Error = e.getMessage();
            return result;
        }
    }

    public void close() {
        if (_session != null) {
            _session.disconnect();
        }
    }
}
