package com.duanxian.shell;

import com.jcraft.jsch.Session;

import java.util.List;
import java.util.concurrent.Callable;

/**
 * Created by Lionsong on 2017/7/8.
 */
public class RemoteShellExecutorThread implements Callable {
    private RemoteShellExecutor remoteShellExecutor;

    public RemoteShellExecutorThread(Session session, String command, String charset){
        this.remoteShellExecutor = new RemoteShellExecutor(session, command, charset);
    }
    @Override
    public Integer call() throws Exception {
        int repsonse = remoteShellExecutor.execShellWithResponse(remoteShellExecutor.getSession(),remoteShellExecutor.getCommand(),remoteShellExecutor.getCharset());
        return repsonse;
    }
}
