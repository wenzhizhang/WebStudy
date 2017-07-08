package com.duanxian.shell;


import com.jcraft.jsch.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

/**
 * This class provides the methods to execute shell commands / scripts on remote host, multiple threads can be running in parallel.
 * Created by Lionsong on 2017/7/6.
 */
public class RemoteShellExecutor implements Callable {
    private Session session;
    public static final String USERNAME = "Username";
    public static final String PASSWORD = "Password";
    public static final String HOST = "Host";
    public static final String PORT = "Port";
    private static final Logger LOGGER = LogManager.getLogger(RemoteShellExecutor.class);
    private static final JSch JSCH = new JSch();

    public RemoteShellExecutor(Map<String, String> hostInfo){
        String username = hostInfo.get(USERNAME);
        String password = hostInfo.get(PASSWORD);
        String host = hostInfo.get(HOST);
        int port = Integer.parseInt(hostInfo.get(PORT).trim());
        try {
            Session session = this.createSession(username,password,host,port);
            this.setSession(session);
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }

    /**
     * This method create a remote SSH session for the remote execution.
     * @param username
     * @param password
     * @param host
     * @param port
     * @return
     * @throws Exception
     */
    private Session createSession(String username, String password, String host, int port) throws Exception {
        Session session = null;
        try {
            session = JSCH.getSession(username, host, port);
            session.setPassword(password);
            session.setConfig("StrictHostKeyChecking", "no");
            session.setTimeout(30000);
        } catch (JSchException e) {
            LOGGER.error(e.getMessage());
        }
        if (session == null) {
            throw new Exception("Failed to create session.");
        } else {
            session.connect();
        }
        return session;
    }

    /**
     * This method executes shell commands in an active session.
     * @param session
     * @param shell
     * @param charset
     * @return
     */
    private List<String> execShell(Session session, String shell, String charset) {
        if (session == null) {
            try {
                throw new Exception("Session is null.");
            } catch (Exception e) {
                LOGGER.error(e.getMessage());
                System.exit(1);
            }
        }

        List<String> output = new ArrayList<String>();
        try {
            ChannelExec channel = (ChannelExec) session.openChannel("exec");
            String cmd = "bash " + shell;
            channel.setCommand(cmd);
            channel.setInputStream(null);
            channel.setErrStream(System.err);
            channel.connect();
            LOGGER.info("Channel is connected.");
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(channel.getInputStream(), Charset.forName(charset)));
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                output.add(line);
            }
            bufferedReader.close();
            channel.disconnect();
//            LOGGER.info("Channel is disconnected.");
//            session.disconnect();

        } catch (JSchException e) {
            LOGGER.error(e.getMessage());
        } catch (IOException e) {
            LOGGER.error(e.getMessage());
        }
        return output;

    }


    @Override
    public Object call() throws Exception {
        List<String> output = new ArrayList<>();
        execShell(this.getSession(),)
        return null;
    }

    public Session getSession() {
        return session;
    }

    public void setSession(Session session) {
        this.session = session;
    }
}
