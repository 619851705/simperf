package simperf.remote;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import simperf.Simperf;
import simperf.remote.result.DefaultRemoteWriter;
import simperf.thread.DefaultCallback;
import simperf.thread.MonitorThread;

import com.google.gson.Gson;

/**
 * Զ��Simperf, ��ʱSimperf��Ϊһ��Client����Զ�̷�������ָ��
 * @author imbugs
 */
public class RemoteSimperf {
    protected static final Logger logger = LoggerFactory.getLogger(RemoteSimperf.class);
    protected Simperf             simperf;
    protected String              server;
    protected int                 port   = 20122;

    protected Gson                gson   = new Gson();
    protected static Socket       clientSocket;
    protected DataOutputStream    outToServer;
    protected BufferedReader      inFromServer;

    public RemoteSimperf(Simperf simperf, String server) {
        this.simperf = simperf;
        this.simperf.getMonitorThread().registerCallback(new DefaultRemoteWriter(this));
        this.simperf.getMonitorThread().registerCallback(new DefaultCallback() {
            public void onExit(MonitorThread monitorThread) {
                try {
                    RemoteSimperf.clientSocket.close();
                } catch (IOException e) {
                    logger.error("��RemoteSimperf�Ͽ�����ʱ��������.", e);
                }
            }
        });
        this.server = server;
    }

    public RemoteSimperf(Simperf simperf, String host, int port) {
        this(simperf, host);
        this.port = port;
    }

    public void start() {
        try {
            if (null == clientSocket) {
                clientSocket = new Socket(server, port);
                InputStreamReader isr = new InputStreamReader(clientSocket.getInputStream());
                outToServer = new DataOutputStream(clientSocket.getOutputStream());
                inFromServer = new BufferedReader(isr);
            }

            String line;
            RemoteCmd remoteCmd;
            do {
                line = inFromServer.readLine();
                logger.info("Remote cmd : " + line);
                remoteCmd = gson.fromJson(line, RemoteCmd.class);
                if (remoteCmd == null) {
                    continue;
                }
                RemoteInvoker invoker = new RemoteInvoker(simperf, remoteCmd);
                String result = invoker.invoke();
                write(result);
            } while (!remoteCmd.getCmd().equals(RemoteCmd.CMD_CLOSE));
            clientSocket.close();
        } catch (Exception e) {
            logger.warn("��RemoteSimperf�����ӹر�.");
        }
    }

    /**
     * �������������Ϣ
     * {type: '', success: '', msg:'', data: ''}
     * type:
     *      result, ���
     *      percent, �ٷֱ���Ϣ
     *      return, ����ֵ
     */
    public void write(String line) throws Exception {
        outToServer.writeBytes(line + "\n");
        logger.info("��������������� [" + line + "]");
    }

    public static void main(String[] args) {
        Gson gson = new Gson();
        RemoteCmd remoteCmd = gson.fromJson("{cmd: 'close', param: 'xxhh'}", RemoteCmd.class);
        System.out.println(remoteCmd);
    }

    public Simperf getSimperf() {
        return simperf;
    }

    public String getServer() {
        return server;
    }

    public int getPort() {
        return port;
    }
}
