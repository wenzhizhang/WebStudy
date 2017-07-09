package com.duanxian.test;

import com.duanxian.shell.RemoteShellExecutor;
import com.duanxian.shell.RemoteShellExecutorThread;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

/**
 * Created by Lionsong on 2017/7/6.
 */
public class RemoteShellExecutorTest {
    private static final Logger LOGGER = LogManager.getLogger(RemoteShellExecutorTest.class);

    public static void main(String[] args) {
        String username = "duanxian";
        String password = "Gg3fgMc8zt";
        String host = "192.168.56.101";
        String cmd1 = "ls";
        String cmd2 = "bash /home/duanxian/Projects/Shell/TestShell.sh";
        String cmd3 = "grep 'test' /home/duanxian/Projects/Shell/TestShell.sh";
        String charset = "UTF-8";
        int port = 22;
        try {
            List<String> commands = new ArrayList<>();
            commands.add(cmd1);
            commands.add(cmd2);
            commands.add(cmd3);
            String cmd = cmd1+";if [ $? -eq 0 ];then "+cmd2+";else; exit 1;fi;if [ $? -eq 0 ];then "+cmd3+"else; exit 1;fi";
            Session session = createSession(username,password,host,port);
            long startTime = System.currentTimeMillis();
            ExecutorService executorService = Executors.newCachedThreadPool();
            CompletionService<Integer> cs = new ExecutorCompletionService<Integer>(executorService);

//            ArrayList<Future<Integer>> rs = new ArrayList<>();
            for (int j=0;j<10;j++){
                final int taskID = j;
                cs.submit(new RemoteShellExecutorThread(session,cmd,charset));
                LOGGER.info("Response for task "+taskID+" is: "+cs.take().get());
                long timeCost = System.currentTimeMillis() - startTime;
                LOGGER.info("Time cost for task "+taskID+": "+timeCost);

            }
//            for (Future<Integer> f:rs){
//                int res = f.get();
//                long timeCost = System.currentTimeMillis() - startTime;
//                LOGGER.info("Response is: "+res);
//                LOGGER.info("Time cost: "+timeCost);
//            }

        } catch (JSchException e) {
            LOGGER.error(e.getMessage());
        } catch (InterruptedException e) {
            LOGGER.error(e.getMessage());
        } catch (ExecutionException e) {
            LOGGER.error(e.getMessage());
        }


    }

    private static Session createSession(String username, String password, String host, int port) throws JSchException {
        int timeout = 30000;
        JSch jSch = new JSch();
        Session session = jSch.getSession(username,host,port);
        session.setPassword(password);
        session.setConfig("StrictHostKeyChecking","no");
        session.setTimeout(timeout);
        session.connect();
        return session;
    }
}
